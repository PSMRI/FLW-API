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
package com.iemr.flw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfig {
	private static final String DEFAULT_SERVER_URL = "http://localhost:9090";
	private static final String BEARER_AUTH_SCHEME = "bearerAuth";

	@Bean
	public OpenAPI customOpenAPI(Environment env) {
		String devUrl = env.getProperty("api.dev.url", DEFAULT_SERVER_URL);
		String uatUrl = env.getProperty("api.uat.url", DEFAULT_SERVER_URL);
		String demoUrl = env.getProperty("api.demo.url", DEFAULT_SERVER_URL);
		return new OpenAPI()
			.info(new Info().title("FLW API").version("3.1.0").description("A microservice for the creation and management of Field Level Workers (FLW) in the AMRIT ecosystem."))
			.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME))
			.components(new Components().addSecuritySchemes(BEARER_AUTH_SCHEME,
				new SecurityScheme().name(BEARER_AUTH_SCHEME).type(SecurityScheme.Type.HTTP).scheme("bearer")))
			.servers(List.of(
				new Server().url(devUrl).description("Dev"),
				new Server().url(uatUrl).description("UAT"),
				new Server().url(demoUrl).description("Demo")
			));
	}
}
