package com.example.demo.dto.log_dto;

import com.example.demo.model.log_model.MotorcycleLog;
import java.util.List;

public class MotorcycleLogDTO {
	private List<MotorcycleLog> logs;
	private long totalElements;
	private int totalPages;

	public MotorcycleLogDTO(List<MotorcycleLog> logs, long totalElements, int totalPages) {
		this.logs = logs;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public List<MotorcycleLog> getLogs() {
		return logs;
	}

	public void setLogs(List<MotorcycleLog> logs) {
		this.logs = logs;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
}
