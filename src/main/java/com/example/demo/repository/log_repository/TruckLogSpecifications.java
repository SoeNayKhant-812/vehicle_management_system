package com.example.demo.repository.log_repository;

import com.example.demo.dto.log_dto.TruckLogFilterRequestDTO;
import com.example.demo.model.log_model.TruckLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class TruckLogSpecifications {

    public static Specification<TruckLog> build(TruckLogFilterRequestDTO filter) {
        return Specification
                .where(hasId(filter.getId()))
                .and(hasTruckId(filter.getTruckId()))
                .and(hasBrand(filter.getBrand()))
                .and(hasModel(filter.getModel()))
                .and(hasAction(filter.getAction()))
                .and(hasPerformedByUserId(filter.getPerformedByUserId()))
                .and(hasPerformedByUsername(filter.getPerformedByUsername()))
                .and(hasTimestampBetween(filter.getStartTime(), filter.getEndTime()));
    }

    private static Specification<TruckLog> hasId(String id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    private static Specification<TruckLog> hasTruckId(Long truckId) {
        return (root, query, cb) ->
                truckId == null ? null : cb.equal(root.get("truckId"), truckId);
    }

    private static Specification<TruckLog> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null : cb.equal(root.get("brand"), brand);
    }

    private static Specification<TruckLog> hasModel(String model) {
        return (root, query, cb) ->
                model == null ? null : cb.equal(root.get("model"), model);
    }

    private static Specification<TruckLog> hasAction(String action) {
        return (root, query, cb) ->
                action == null ? null : cb.equal(root.get("action"), action);
    }

    private static Specification<TruckLog> hasPerformedByUserId(String uid) {
        return (root, query, cb) ->
                uid == null ? null : cb.equal(root.get("performedByUserId"), uid);
    }

    private static Specification<TruckLog> hasPerformedByUsername(String uname) {
        return (root, query, cb) ->
                uname == null ? null : cb.equal(root.get("performedByUsername"), uname);
    }

    private static Specification<TruckLog> hasTimestampBetween(Instant start, Instant end) {
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
