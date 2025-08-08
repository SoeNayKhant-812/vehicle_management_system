package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.MotorcycleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MotorcycleLogRepository extends JpaRepository<MotorcycleLog, String>, JpaSpecificationExecutor<MotorcycleLog> {
}
