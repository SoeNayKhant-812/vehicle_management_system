package com.example.demo.repository.log_repository;

import com.example.demo.dto.log_dto.MotorcycleLogFilterRequestDTO;
import com.example.demo.model.log_model.MotorcycleLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class MotorcycleLogSpecifications {

    public static Specification<MotorcycleLog> build(MotorcycleLogFilterRequestDTO filter) {
        return Specification
                .where(hasId(filter.getId()))
                .and(hasMotorcycleId(filter.getMotorcycleId()))
                .and(hasBrand(filter.getBrand()))
                .and(hasModel(filter.getModel()))
                .and(hasAction(filter.getAction()))
                .and(hasPerformedByUserId(filter.getPerformedByUserId()))
                .and(hasPerformedByUsername(filter.getPerformedByUsername()))
                .and(hasTimestampBetween(filter.getStartTime(), filter.getEndTime()));
    }

    private static Specification<MotorcycleLog> hasId(String id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    private static Specification<MotorcycleLog> hasMotorcycleId(Long motorcycleId) {
        return (root, query, cb) ->
                motorcycleId == null ? null : cb.equal(root.get("motorcycleId"), motorcycleId);
    }

    private static Specification<MotorcycleLog> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null : cb.equal(root.get("brand"), brand);
    }

    private static Specification<MotorcycleLog> hasModel(String model) {
        return (root, query, cb) ->
                model == null ? null : cb.equal(root.get("model"), model);
    }

    private static Specification<MotorcycleLog> hasAction(String action) {
        return (root, query, cb) ->
                action == null ? null : cb.equal(root.get("action"), action);
    }

    private static Specification<MotorcycleLog> hasPerformedByUserId(String uid) {
        return (root, query, cb) ->
                uid == null ? null : cb.equal(root.get("performedByUserId"), uid);
    }

    private static Specification<MotorcycleLog> hasPerformedByUsername(String uname) {
        return (root, query, cb) ->
                uname == null ? null : cb.equal(root.get("performedByUsername"), uname);
    }

    private static Specification<MotorcycleLog> hasTimestampBetween(Instant start, Instant end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("timestamp"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("timestamp"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("timestamp"), end);
            } else {
                return null;
            }
        };
    }
}
