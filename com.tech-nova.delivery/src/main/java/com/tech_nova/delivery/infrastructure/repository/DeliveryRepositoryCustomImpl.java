package com.tech_nova.delivery.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.model.delivery.DeliveryStatus;
import com.tech_nova.delivery.domain.model.delivery.QDelivery;
import com.tech_nova.delivery.domain.repository.DeliveryRepositoryCustom;
import com.tech_nova.delivery.presentation.request.DeliverySearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryCustomImpl implements DeliveryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    QDelivery delivery = QDelivery.delivery;


    @Override
    public Page<Delivery> searchDelivery(UUID userId, String role, DeliverySearchRequest searchRequest, Pageable pageable) {
        Long totalCnt = Optional.ofNullable(jpaQueryFactory
                        .select(delivery.count())
                        .from(delivery)
                        .where(conditions(userId, role, searchRequest))
                        .fetchOne())
                .orElseThrow();

        JPAQuery<Delivery> query = jpaQueryFactory.selectFrom(delivery)
                .where(conditions(userId, role, searchRequest))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Delivery> content = query.stream().toList();
        return new PageImpl<>(content, pageable, totalCnt);
    }

    private BooleanBuilder conditions(UUID userId, String role, DeliverySearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(searchById(searchRequest.getId()))
                .and(searchByOrderId(searchRequest.getOrderId()))
                .and(searchByDepartureHubId(searchRequest.getDepartureHubId()))
                .and(searchByArrivalHubId(searchRequest.getArrivalHubId()))
                .and(searchCurrentStatus(searchRequest.getCurrentStatus()))
                .and(searchByRecipient(searchRequest.getRecipient()));

        if ("MASTER".equals(role)) {
            builder.and(deletedEq(searchRequest.isDeleted()));
        } else {
            builder.and(delivery.isDeleted.isFalse());
        }

        if ("HUB_MANAGER".equals(role) && searchRequest.getManageHubIds() != null && !searchRequest.getManageHubIds().isEmpty()) {
            builder.and(delivery.departureHubId.in(searchRequest.getManageHubIds()));
            builder.and(delivery.arrivalHubId.in(searchRequest.getManageHubIds()));
        }

        if ("HUB_DELIVERY_MANAGER".equals(role)) {
            builder.and(delivery.routeRecords.any().deliveryManager.id.eq(userId));
        }

        if ("COMPANY_DELIVERY_MANAGER".equals(role)) {
            builder.and(delivery.companyRouteRecords.any().deliveryManager.id.eq(userId));
        }

        return builder;
    }

    private BooleanExpression deletedEq(Boolean isDeleted) {
        return isDeleted != null ? delivery.isDeleted.eq(isDeleted) : null;
    }

    private BooleanExpression searchById(UUID id) {
        return id != null ? delivery.id.eq(id) : null;
    }

    private BooleanExpression searchByOrderId(UUID orderId) {
        return orderId != null ? delivery.orderId.eq(orderId) : null;
    }

    private BooleanExpression searchByDepartureHubId(UUID departureHubId) {
        return departureHubId != null ? delivery.departureHubId.eq(departureHubId) : null;
    }

    private BooleanExpression searchByArrivalHubId(UUID arrivalHubId) {
        return arrivalHubId != null ? delivery.departureHubId.eq(arrivalHubId) : null;
    }

    private BooleanExpression searchCurrentStatus(String currentStatus) {
        return currentStatus != null ? delivery.currentStatus.eq(DeliveryStatus.valueOf(currentStatus)) : null;
    }

    private BooleanExpression searchByRecipient(String recipient) {
        return recipient != null ? delivery.recipient.eq(recipient) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {

        OrderSpecifier<?> orderSpecifier = switch (pageable.getSort().toString()) {
            case "createBy: ASC" -> delivery.createBy.asc();
            case "createBy: DESC" -> delivery.createBy.desc();
            case "updateBy: ASC" -> delivery.updateBy.asc();
            case "updateBy: DESC" -> delivery.updateBy.desc();
            default -> delivery.createdAt.asc();
        };
        return new OrderSpecifier<?>[]{orderSpecifier};
    }
}
