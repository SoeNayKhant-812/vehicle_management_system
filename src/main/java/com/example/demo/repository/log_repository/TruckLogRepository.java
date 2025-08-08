package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.TruckLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TruckLogRepository extends JpaRepository<TruckLog, String>, JpaSpecificationExecutor<TruckLog> {
}
