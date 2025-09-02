package com.example.demo.task;

import com.example.demo.service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemReportTasks {

    private static final Logger logger = LoggerFactory.getLogger(SystemReportTasks.class);

    private final MetricsService metricsService;

    @Autowired
    public SystemReportTasks(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Scheduled(
            initialDelayString = "${tasks.report.initial-delay}",
            fixedDelayString = "${tasks.report.fixed-delay}"
    )
    public void generateSystemReport() {
        logger.info("--- [SYSTEM REPORT TASK STARTED] ---");
        try {
            long userCount = metricsService.getUserCount();
            long carCount = metricsService.getCarCount();

            logger.info("VMS System Report: Total Users = {}, Total Cars = {}", userCount, carCount);

        } catch (Exception e) {
            logger.error("Failed to generate system report due to an unexpected error.", e);
        }
        logger.info("--- [SYSTEM REPORT TASK FINISHED] ---");
    }
}
