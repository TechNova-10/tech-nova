package com.tech_nova.movement.domain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMovement is a Querydsl query type for Movement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovement extends EntityPathBase<Movement> {

    private static final long serialVersionUID = 927024901L;

    public static final QMovement movement = new QMovement("movement");

    public final ComparablePath<java.util.UUID> arrivalHubId = createComparable("arrivalHubId", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> createdBy = createComparable("createdBy", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> deleted_at = createDateTime("deleted_at", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> deleted_By = createComparable("deleted_By", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> departureHubId = createComparable("departureHubId", java.util.UUID.class);

    public final NumberPath<Double> distance = createNumber("distance", Double.class);

    public final ComparablePath<java.util.UUID> intermediateHubId = createComparable("intermediateHubId", java.util.UUID.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final ComparablePath<java.util.UUID> movementInfoId = createComparable("movementInfoId", java.util.UUID.class);

    public final NumberPath<Double> timeTravel = createNumber("timeTravel", Double.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> updatedBy = createComparable("updatedBy", java.util.UUID.class);

    public QMovement(String variable) {
        super(Movement.class, forVariable(variable));
    }

    public QMovement(Path<? extends Movement> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMovement(PathMetadata metadata) {
        super(Movement.class, metadata);
    }

}

