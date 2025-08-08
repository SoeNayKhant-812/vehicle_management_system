package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.MotorcycleLogDTO;
import com.example.demo.dto.log_dto.MotorcycleLogFilterRequestDTO;
import com.example.demo.model.log_model.MotorcycleLog;
import com.example.demo.service.log_service.MotorcycleLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs/motorcycle")
@PreAuthorize("hasRole('ADMIN')")
public class MotorcycleLogController {

    private final MotorcycleLogService motorcycleLogService;

    @Autowired
    public MotorcycleLogController(MotorcycleLogService motorcycleLogService) {
        this.motorcycleLogService = motorcycleLogService;
    }

    @GetMapping("/all")
    public ResponseEntity<MotorcycleLogDTO> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MotorcycleLog> logPage = motorcycleLogService.getAllLogs(page, size);
        return ResponseEntity.ok(new MotorcycleLogDTO(
                logPage.getContent(),
                logPage.getTotalElements(),
                logPage.getTotalPages()
        ));
    }

    @GetMapping("/filter")
    public ResponseEntity<MotorcycleLogDTO> getFilteredLogs(
            @RequestBody MotorcycleLogFilterRequestDTO filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<MotorcycleLog> logPage = motorcycleLogService.getFilteredLogs(filterRequest, page, size, sortBy, sortDir);
        return ResponseEntity.ok(new MotorcycleLogDTO(
                logPage.getContent(),
                logPage.getTotalElements(),
                logPage.getTotalPages()
        ));
    }
}
