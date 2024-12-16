package com.tech_nova.delivery.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.model.manager.QDeliveryManager;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepositoryCustom;
import com.tech_nova.delivery.presentation.request.DeliveryManagerSearchRequest;
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
public class DeliveryManagerRepositoryCustomImpl implements DeliveryManagerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    QDeliveryManager manager = QDeliveryManager.deliveryManager;


    @Override
    public Page<DeliveryManager> searchDeliveryManager(String role, DeliveryManagerSearchRequest searchRequest, Pageable pageable) {
        Long totalCnt = Optional.ofNullable(jpaQueryFactory
                        .select(manager.count())
                        .from(manager)
                        .where(conditions(role, searchRequest))
                        .fetchOne())
                .orElseThrow();

        JPAQuery<DeliveryManager> query = jpaQueryFactory.selectFrom(manager)
                .where(conditions(role, searchRequest))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<DeliveryManager> content = query.stream().toList();
        return new PageImpl<>(content, pageable, totalCnt);
    }

    private BooleanBuilder conditions(String role, DeliveryManagerSearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(searchById(searchRequest.getId()))
                .and(searchByAssignedHubId(searchRequest.getAssignedHubId()))
                .and(searchByManagerUserId(searchRequest.getManagerUserId()))
                .and(searchByManagerRole(searchRequest.getManagerRole()));

        if ("MASTER".equals(role)) {
            builder.and(deletedEq(searchRequest.isDeleted()));
            builder.and(searchByAssignedHubId(searchRequest.getAssignedHubId()));
        } else {
            builder.and(manager.isDeleted.isFalse());
        }

        if ("HUB_MANAGER".equals(role) && searchRequest.getManageHubIds() != null && !searchRequest.getManageHubIds().isEmpty()) {
            builder.and(manager.assignedHubId.in(searchRequest.getManageHubIds()));
        }
        return builder;
    }

    private BooleanExpression deletedEq(Boolean isDeleted) {
        return isDeleted != null ? manager.isDeleted.eq(isDeleted) : null;
    }

    private BooleanExpression searchById(UUID id) {
        return id != null ? manager.id.eq(id) : null;
    }

    private BooleanExpression searchByAssignedHubId(UUID assignedHubId) {
        return assignedHubId != null ? manager.assignedHubId.eq(assignedHubId) : null;
    }

    private BooleanExpression searchByManagerUserId(UUID managerUserId) {
        return managerUserId != null ? manager.managerUserId.eq(managerUserId) : null;
    }

    private BooleanExpression searchByManagerRole(String managerRole) {
        return managerRole != null ? manager.managerRole.eq(DeliveryManagerRole.valueOf(managerRole)) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {

        OrderSpecifier<?> orderSpecifier = switch (pageable.getSort().toString()) {
            case "createBy: ASC" -> manager.createBy.asc();
            case "createBy: DESC" -> manager.createBy.desc();
            case "updateBy: ASC" -> manager.updateBy.asc();
            case "updateBy: DESC" -> manager.updateBy.desc();
            default -> manager.createdAt.asc();
        };
        return new OrderSpecifier<?>[]{orderSpecifier};
    }
}
