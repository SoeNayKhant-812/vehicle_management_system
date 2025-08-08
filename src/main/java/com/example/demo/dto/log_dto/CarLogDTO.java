package com.example.demo.dto.log_dto;

import com.example.demo.model.log_model.CarLog;
import java.util.List;

public class CarLogDTO {
	private List<CarLog> logs;
	private long totalElements;
	private int totalPages;

	public CarLogDTO(List<CarLog> logs, long totalElements, int totalPages) {
		this.logs = logs;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public List<CarLog> getLogs() {
		return logs;
	}

	public void setLogs(List<CarLog> logs) {
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
