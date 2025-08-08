package com.example.demo.repository.log_repository;

import com.example.demo.dto.log_dto.CarLogFilterRequestDTO;
import com.example.demo.model.log_model.CarLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class CarLogSpecifications {

    public static Specification<CarLog> build(CarLogFilterRequestDTO filter) {
        return Specification
                .where(hasId(filter.getId()))
                .and(hasCarId(filter.getCarId()))
                .and(hasBrand(filter.getBrand()))
                .and(hasModel(filter.getModel()))
                .and(hasAction(filter.getAction()))
                .and(hasPerformedByUserId(filter.getPerformedByUserId()))
                .and(hasPerformedByUsername(filter.getPerformedByUsername()))
                .and(hasTimestampBetween(filter.getStartTime(), filter.getEndTime()));
    }

    private static Specification<CarLog> hasId(String id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    private static Specification<CarLog> hasCarId(Long carId) {
        return (root, query, cb) ->
                carId == null ? null : cb.equal(root.get("carId"), carId);
    }

    private static Specification<CarLog> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null : cb.equal(root.get("brand"), brand);
    }

    private static Specification<CarLog> hasModel(String model) {
        return (root, query, cb) ->
                model == null ? null : cb.equal(root.get("model"), model);
    }

    private static Specification<CarLog> hasAction(String action) {
        return (root, query, cb) ->
                action == null ? null : cb.equal(root.get("action"), action);
    }

    private static Specification<CarLog> hasPerformedByUserId(String uid) {
        return (root, query, cb) ->
                uid == null ? null : cb.equal(root.get("performedByUserId"), uid);
    }

    private static Specification<CarLog> hasPerformedByUsername(String uname) {
        return (root, query, cb) ->
                uname == null ? null : cb.equal(root.get("performedByUsername"), uname);
    }

    private static Specification<CarLog> hasTimestampBetween(Instant start, Instant end) {
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
