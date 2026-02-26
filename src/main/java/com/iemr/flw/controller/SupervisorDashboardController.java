package com.iemr.flw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.flw.service.SupervisorDashboardService;
import com.iemr.flw.utils.response.OutputResponse;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/supervisor", produces = MediaType.APPLICATION_JSON_VALUE)
public class SupervisorDashboardController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private SupervisorDashboardService supervisorDashboardService;

	@Operation(summary = "Get comprehensive ASHA Supervisor dashboard with facilities, ASHAs, villages and incentive data")
	@GetMapping("/{userId}/dashboard")
	public String getSupervisorDashboard(@PathVariable Integer userId) {
		OutputResponse response = new OutputResponse();
		try {
			String result = supervisorDashboardService.getSupervisorDashboard(userId);
			response.setResponse(result);
		} catch (Exception e) {
			logger.error("getSupervisorDashboard failed: " + e.getMessage(), e);
			response.setError(e);
		}
		return response.toString();
	}
}
