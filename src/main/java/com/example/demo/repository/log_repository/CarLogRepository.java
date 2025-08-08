package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.CarLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarLogRepository extends JpaRepository<CarLog, String>, JpaSpecificationExecutor<CarLog> {
}
