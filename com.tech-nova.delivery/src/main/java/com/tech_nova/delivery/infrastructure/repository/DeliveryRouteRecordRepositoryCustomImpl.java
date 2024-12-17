package com.tech_nova.delivery.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech_nova.delivery.domain.model.delivery.DeliveryHubStatus;
import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.domain.model.delivery.QDeliveryRouteRecord;
import com.tech_nova.delivery.domain.repository.DeliveryRouteRecordRepositoryCustom;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
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
public class DeliveryRouteRecordRepositoryCustomImpl implements DeliveryRouteRecordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    QDeliveryRouteRecord routeRecord = QDeliveryRouteRecord.deliveryRouteRecord;


    @Override
    public Page<DeliveryRouteRecord> searchDeliveryRouteRecords(UUID userId, String role, DeliveryRouteSearchRequest searchRequest, Pageable pageable) {
        Long totalCnt = Optional.ofNullable(jpaQueryFactory
                        .select(routeRecord.count())
                        .from(routeRecord)
                        .where(conditions(userId, role, searchRequest))
                        .fetchOne())
                .orElseThrow();

        JPAQuery<DeliveryRouteRecord> query = jpaQueryFactory.selectFrom(routeRecord)
                .where(conditions(userId, role, searchRequest))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<DeliveryRouteRecord> content = query.stream().toList();
        return new PageImpl<>(content, pageable, totalCnt);
    }

    private BooleanBuilder conditions(UUID userId, String role, DeliveryRouteSearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(searchById(searchRequest.getId()))
                .and(searchByDeliveryManagerId(searchRequest.getDeliveryManagerId()))
                .and(searchByDeliveryId(searchRequest.getDeliveryId()))
                .and(searchCurrentStatus(searchRequest.getCurrentStatus()));

        if ("MASTER".equals(role)) {
            builder.and(deletedEq(searchRequest.isDeleted()));
        } else {
            builder.and(routeRecord.isDeleted.isFalse());
        }

        if ("HUB_DELIVERY_MANAGER".equals(role)) {
            builder.and(routeRecord.deliveryManager.id.eq(userId));
        }

        return builder;
    }

    private BooleanExpression deletedEq(Boolean isDeleted) {
        return isDeleted != null ? routeRecord.isDeleted.eq(isDeleted) : null;
    }

    private BooleanExpression searchById(UUID id) {
        return id != null ? routeRecord.id.eq(id) : null;
    }

    private BooleanExpression searchByDeliveryManagerId(UUID managerId) {
        return managerId != null ? routeRecord.deliveryManager.id.eq(managerId) : null;
    }

    private BooleanExpression searchByDeliveryId(UUID deliveryId) {
        return deliveryId != null ? routeRecord.delivery.id.eq(deliveryId) : null;
    }

    private BooleanExpression searchCurrentStatus(String currentStatus) {
        return currentStatus != null ? routeRecord.currentStatus.eq(DeliveryHubStatus.valueOf(currentStatus)) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {

        OrderSpecifier<?> orderSpecifier = switch (pageable.getSort().toString()) {
            case "createBy: ASC" -> routeRecord.createBy.asc();
            case "createBy: DESC" -> routeRecord.createBy.desc();
            case "updateBy: ASC" -> routeRecord.updateBy.asc();
            case "updateBy: DESC" -> routeRecord.updateBy.desc();
            default -> routeRecord.createdAt.asc();
        };
        return new OrderSpecifier<?>[]{orderSpecifier};
    }
}
