package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.TruckLogDTO;
import com.example.demo.dto.log_dto.TruckLogFilterRequestDTO;
import com.example.demo.model.log_model.TruckLog;
import com.example.demo.service.log_service.TruckLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs/truck")
@PreAuthorize("hasRole('ADMIN')")
public class TruckLogController {

    private final TruckLogService truckLogService;

    @Autowired
    public TruckLogController(TruckLogService truckLogService) {
        this.truckLogService = truckLogService;
    }

    @GetMapping("/all")
    public ResponseEntity<TruckLogDTO> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TruckLog> logPage = truckLogService.getAllLogs(page, size);
        return ResponseEntity.ok(new TruckLogDTO(
                logPage.getContent(),
                logPage.getTotalElements(),
                logPage.getTotalPages()
        ));
    }

    @GetMapping("/filter")
    public ResponseEntity<TruckLogDTO> getFilteredLogs(
            @RequestBody TruckLogFilterRequestDTO filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<TruckLog> logPage = truckLogService.getFilteredLogs(filterRequest, page, size, sortBy, sortDir);
        return ResponseEntity.ok(new TruckLogDTO(
                logPage.getContent(),
                logPage.getTotalElements(),
                logPage.getTotalPages()
        ));
    }
}
