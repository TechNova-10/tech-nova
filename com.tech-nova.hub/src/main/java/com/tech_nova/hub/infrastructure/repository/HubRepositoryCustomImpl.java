package com.tech_nova.hub.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.domain.model.Hub;
import com.tech_nova.hub.domain.model.QHub;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubRepositoryCustomImpl implements HubRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  QHub hub = QHub.hub;

  @Override
  public Page<Hub> searchHubs(String role, HubSearchDto hubSearchDto, Pageable pageable) {

    JPAQuery<Hub> query = queryFactory.selectFrom(hub)
        .where(conditions(role, hubSearchDto))
        .orderBy(getOrderSpecifiers(pageable))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());

    long total = Optional.ofNullable(queryFactory
            .select(hub.count())
            .from(hub)
            .where(conditions(role, hubSearchDto))
            .fetchOne())
        .orElseThrow();

    List<Hub> content = query.stream().toList();

    return new PageImpl<>(content, pageable, total);
  }

  private BooleanBuilder conditions(String role, HubSearchDto hubSearchDto) {
    BooleanBuilder builder = new BooleanBuilder()
        .and(provinceEq(hubSearchDto.getProvince()))
        .and(cityEq(hubSearchDto.getCity()))
        .and(districtContains(hubSearchDto.getDistrict()))
        .and(roadNameContains(hubSearchDto.getRoadName()));

    if ("MASTER".equals(role)) {
      builder.and(deletedEq(hubSearchDto.isDeleted()));
    } else {
      builder.and(hub.isDeleted.isFalse());
    }
    return builder;
  }

  private BooleanExpression deletedEq(Boolean isDeleted) {
    return isDeleted != null ? hub.isDeleted.eq(isDeleted) : null;
  }

  private BooleanExpression provinceEq(String province) {
    return province != null ? hub.province.eq(province) : null;
  }

  private BooleanExpression cityEq(String city) {
    return city != null ? hub.city.eq(city) : null;
  }

  private BooleanExpression districtContains(String district) {
    return district != null ? hub.district.contains(district) : null;
  }

  private BooleanExpression roadNameContains(String roadName) {
    return roadName != null ? hub.roadName.contains(roadName) : null;
  }

  private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {

    OrderSpecifier<?> orderSpecifier = switch (pageable.getSort().toString()) {
      case "updatedAt: ASC" -> hub.updatedAt.asc();
      case "name: ASC" -> hub.name.asc();
      case "updatedAt: DESC" -> hub.updatedAt.desc();
      case "name: DESC" -> hub.name.desc();
      default -> hub.createdAt.asc();
    };
    return new OrderSpecifier<?>[]{orderSpecifier};
  }
}

