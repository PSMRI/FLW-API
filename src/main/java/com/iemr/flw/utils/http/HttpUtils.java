/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.flw.utils.http;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.iemr.flw.utils.RestTemplateUtil;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Component
public class HttpUtils {
	public static final String AUTHORIZATION = "Authorization";
	private RestTemplate rest;
	private HttpHeaders headers;
	private HttpStatus status;

	public HttpUtils() {
		if (rest == null) {
			rest = new RestTemplate();
			headers = new HttpHeaders();
		}
	}

	public String get(String uri) {
		String body;
		HttpHeaders localheaders = new HttpHeaders();
		localheaders.add("Content-Type", "application/json");
		RestTemplateUtil.getJwttokenFromHeaders(localheaders);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, requestEntity, String.class);
		setStatus((HttpStatus) responseEntity.getStatusCode());
		body = responseEntity.getBody();
		return body;
	}

	public String get(String uri, HashMap<String, Object> header) {
		String body;
		HttpHeaders headers = new HttpHeaders();
		if (header.containsKey(HttpHeaders.AUTHORIZATION)) {
			headers.add(HttpHeaders.AUTHORIZATION, header.get(HttpHeaders.AUTHORIZATION).toString());
		}
		if (header.containsKey(HttpHeaders.CONTENT_TYPE)) {
			headers.add(HttpHeaders.CONTENT_TYPE, header.get(HttpHeaders.CONTENT_TYPE).toString());
		} else {
			headers.add("Content-Type", MediaType.APPLICATION_JSON);
		}
		RestTemplateUtil.getJwttokenFromHeaders(headers);
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, requestEntity, String.class);
		setStatus((HttpStatus) responseEntity.getStatusCode());
		body = responseEntity.getBody();
		return body;
	}

	public String post(String uri, String json) {
		String body;
		RestTemplateUtil.getJwttokenFromHeaders(headers);
		HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.POST, requestEntity, String.class);
		setStatus((HttpStatus) responseEntity.getStatusCode());
		body = responseEntity.getBody();
		return body;
	}

	public String post(String uri, String data, HashMap<String, Object> header) {
		HttpHeaders headers = new HttpHeaders();
		if (header.containsKey(HttpHeaders.AUTHORIZATION)) {
			headers.add(HttpHeaders.AUTHORIZATION, header.get(HttpHeaders.AUTHORIZATION).toString());
		}
		RestTemplateUtil.getJwttokenFromHeaders(headers);
		HttpEntity<String> requestEntity = new HttpEntity<String>(data, headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.POST, requestEntity, String.class);
		setStatus((HttpStatus) responseEntity.getStatusCode());
		return responseEntity.getBody();
	}

	public ResponseEntity<String> postWithResponseEntity(String uri, String data, HashMap<String, Object> header) {
		HttpHeaders headers = new HttpHeaders();
		if (header.containsKey(HttpHeaders.AUTHORIZATION)) {
			headers.add(HttpHeaders.AUTHORIZATION, header.get(HttpHeaders.AUTHORIZATION).toString());
		}
		// for fetosense api
		if (header.containsKey("apiKey")) {
			headers.add("apiKey", header.get("apiKey").toString());
		}
		headers.add("Content-Type", MediaType.APPLICATION_JSON);
		RestTemplateUtil.getJwttokenFromHeaders(headers);
		HttpEntity<String> requestEntity = new HttpEntity<String>(data, headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.POST, requestEntity, String.class);
		return responseEntity;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public ResponseEntity<String> getV1(String uri) {
		System.out.println(uri.toString());
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, requestEntity, String.class);
		return responseEntity;
	}
}
