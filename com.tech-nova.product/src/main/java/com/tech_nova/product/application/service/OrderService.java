package com.tech_nova.product.application.service;

import com.tech_nova.product.application.dto.OrderProductResponse;
import com.tech_nova.product.application.dto.OrderRequest;
import com.tech_nova.product.application.dto.OrderResponse;
import com.tech_nova.product.domain.model.AuditField;
import com.tech_nova.product.domain.model.Order;
import com.tech_nova.product.domain.model.OrderProduct;
import com.tech_nova.product.domain.model.Product;
import com.tech_nova.product.domain.repository.OrderRepository;
import com.tech_nova.product.domain.repository.ProductRepository;
import com.tech_nova.product.infrastructure.client.DeliveryServiceClient;
import com.tech_nova.product.infrastructure.dto.DeliveryRequest;
import com.tech_nova.product.presentation.dto.ApiResponseDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DeliveryServiceClient deliveryServiceClient;

    @Transactional public UUID createOrder(OrderRequest request, UUID userId, String role) {
        Order order = buildOrderFromRequest(request);
        processOrderProducts(request, order);
        orderRepository.save(order);

        DeliveryRequest deliveryRequest = DeliveryRequest.builder()
                .orderId(order.getOrderId())
                .recipientCompanyId(request.getReceivingCompanyId())
                .province(request.getProvince())
                .city(request.getCity())
                .district(request.getDistrict())
                .roadName(request.getRoadName())
                .detailAddress(request.getDetailAddress())
                .build();

        try {
            ApiResponseDto<UUID> deliveryResponse = deliveryServiceClient.createDelivery(
                    deliveryRequest,
                    "orderApp-001", // X-Order-Origin 헤더
                     userId, // 사용자 ID
                     role // 사용자 역할
             );

            UUID deliveryId = deliveryResponse.getData();
            if (deliveryId == null) {
                throw new IllegalStateException("배송 ID가 반환되지 않았습니다.");
            }
            order.assignDelivery(deliveryId); // 주문에 배송 ID 연결
            return order.getOrderId(); // 주문 ID 반환
        } catch (FeignException e) {
            throw new RuntimeException("배송 생성 중 오류가 발생했습니다: " + e.contentUTF8(), e);
        } catch (Exception e) {
            throw new RuntimeException("배송 생성 중 오류가 발생했습니다.", e);
        }
    }


    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = findOrderById(orderId);

        // 재고 복구
        order.getOrderProducts().forEach(orderProduct -> {
            increaseStock(orderProduct.getProduct().getProductId(), orderProduct.getQuantity());
            orderProduct.softDelete();  // 중간 테이블 데이터 소프트 삭제
        });
        order.softDelete();  // 주문 소프트 삭제
        orderRepository.save(order);
    }

    @Transactional
    public void updateOrder(UUID orderId, OrderRequest request) {
        Order existingOrder = findOrderById(orderId);

        // 기존 주문 제품의 재고 복구 및 중간 테이블 소프트 삭제
        existingOrder.getOrderProducts().forEach(orderProduct -> {
            increaseStock(orderProduct.getProduct().getProductId(), orderProduct.getQuantity());
            orderProduct.softDelete();
        });
        existingOrder.getOrderProducts().clear();

        // 새로운 주문 데이터 설정
        Order updatedOrder = existingOrder.toBuilder()
                .requestingCompanyId(request.getRequestingCompanyId())
                .receivingCompanyId(request.getReceivingCompanyId())
                .requestDetails(request.getRequestDetails())
                .deliveryDeadLine(LocalDateTime.now()) // 현재 시각으로 설정
                .build();

        processOrderProducts(request, updatedOrder);
        orderRepository.save(updatedOrder);
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = findOrderById(orderId);

        // 중간 테이블 데이터 소프트 삭제
        order.getOrderProducts().forEach(AuditField::softDelete);
        order.softDelete();  // 주문 소프트 삭제
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrdersByRequestingCompany(UUID companyId, Pageable pageable) {
        return orderRepository.findByRequestingCompanyIdAndIsDeletedFalse(companyId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrdersByReceivingCompany(UUID companyId, Pageable pageable) {
        return orderRepository.findByReceivingCompanyIdAndIsDeletedFalse(companyId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> searchAllOrders(Pageable pageable) {
        return orderRepository.findByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        Order order = findOrderById(orderId);
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .requestingCompanyId(order.getRequestingCompanyId())
                .receivingCompanyId(order.getReceivingCompanyId())
                .orderProducts(order.getOrderProducts().stream()
                        .filter(orderProduct -> !orderProduct.isDeleted()) // 삭제되지 않은 항목만 포함
                        .map(op -> OrderProductResponse.builder()
                                .productId(op.getProduct().getProductId())
                                .quantity(op.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .requestDetails(order.getRequestDetails())
                .deliveryDeadLine(order.getDeliveryDeadLine())
                .deliveryDate(order.getDeliveryDate())
                .build();
    }

    private void decreaseStock(UUID productId, int quantity) {
        Product product = findProductById(productId);

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        product = product.toBuilder()
                .stock(product.getStock() - quantity)
                .build();

        productRepository.save(product);
    }

    private void increaseStock(UUID productId, int quantity) {
        Product product = findProductById(productId);
        product = product.toBuilder()
                .stock(product.getStock() + quantity)
                .build();
        productRepository.save(product);
    }

    private Product findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    private Order buildOrderFromRequest(OrderRequest request) {
        return Order.builder()
                .requestingCompanyId(request.getRequestingCompanyId())
                .receivingCompanyId(request.getReceivingCompanyId())
                .requestDetails(request.getRequestDetails())
                .deliveryDeadLine(LocalDateTime.now()) // 현재 시각으로 설정
                .orderProducts(new ArrayList<>()) // 리스트 초기화
                .build();
    }

    private void processOrderProducts(OrderRequest request, Order order) {
        request.getOrderProducts().forEach(orderProductRequest -> {
            Product product = findProductById(orderProductRequest.getProductId());
            decreaseStock(product.getProductId(), orderProductRequest.getQuantity());
            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .product(product)
                    .quantity(orderProductRequest.getQuantity())
                    .build();
            order.getOrderProducts().add(orderProduct);
        });
    }

    private void validateUserRole(String token, UUID companyId) {
        // 권한 확인 로직 구현 (주석 처리)
        // 예: authServiceClient.checkUserRole(token, companyId);
    }
}
