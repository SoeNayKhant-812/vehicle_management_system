package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.CarLogDTO;
import com.example.demo.dto.log_dto.CarLogFilterRequestDTO;
import com.example.demo.model.log_model.CarLog;
import com.example.demo.service.log_service.CarLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs/car")
//@PreAuthorize("hasRole('ADMIN')")
public class CarLogController {

    private final CarLogService carLogService;

    @Autowired
    public CarLogController(CarLogService carLogService) {
        this.carLogService = carLogService;
    }

    @GetMapping("/all")
    public ResponseEntity<CarLogDTO> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CarLog> logPage = carLogService.getAllLogs(page, size);
        return ResponseEntity.ok(new CarLogDTO(
                logPage.getContent(),
                logPage.getTotalElements(),
                logPage.getTotalPages()
        ));
    }

    @GetMapping("/filter")
    public ResponseEntity<CarLogDTO> getFilteredLogs(
            @RequestBody CarLogFilterRequestDTO filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<CarLog> logPage = carLogService.getFilteredLogs(filterRequest, page, size, sortBy, sortDir);
        return ResponseEntity.ok(new CarLogDTO(
                logPage.getContent(),
                logPage.getTotalElements(),
                logPage.getTotalPages()
        ));
    }
}

