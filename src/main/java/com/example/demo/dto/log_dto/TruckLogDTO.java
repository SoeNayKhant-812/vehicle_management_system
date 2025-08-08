package com.example.demo.dto.log_dto;

import com.example.demo.model.log_model.TruckLog;
import java.util.List;

public class TruckLogDTO {
	private List<TruckLog> logs;
	private long totalElements;
	private int totalPages;

	public TruckLogDTO(List<TruckLog> logs, long totalElements, int totalPages) {
		this.logs = logs;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public List<TruckLog> getLogs() {
		return logs;
	}

	public void setLogs(List<TruckLog> logs) {
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
