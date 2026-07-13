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
package com.iemr.flw.utils.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisStorage {
	@Autowired
	private LettuceConnectionFactory connection;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	public String setObject(String key, String value, int expirationTime) throws RedisSessionException {
		RedisConnection redCon = connection.getConnection();
		byte[] sessionData = redCon.get(key.getBytes());
		String userRespFromRedis = null;
		if (sessionData != null) {
			userRespFromRedis = new String(redCon.get(key.getBytes()));
		}
		if ((userRespFromRedis == null) || (userRespFromRedis.isEmpty())) {
			logger.info("updating session time of redis for " + key);
			redCon.set(key.getBytes(), value.getBytes(), Expiration.seconds(expirationTime), SetOption.UPSERT);
		}

		return key;
	}

	public String getObject(String key, Boolean extendExpirationTime, int expirationTime) throws RedisSessionException {
		RedisConnection redCon = connection.getConnection();
		byte[] sessionData = redCon.get(key.getBytes());
		String userRespFromRedis = null;
		if (sessionData != null) {
			userRespFromRedis = new String(redCon.get(key.getBytes()));
		}
		if ((userRespFromRedis != null) && (userRespFromRedis.trim().length() != 0)) {
			logger.info("updating session time of redis for " + key);
			redCon.expire(key.getBytes(), expirationTime);
		} else {
			throw new RedisSessionException("Unable to fetch session object from Redis server");
		}
		return userRespFromRedis;
	}

	public Long deleteObject(String key) throws RedisSessionException {
		RedisConnection redCon = connection.getConnection();
		Long userRespFromRedis = Long.valueOf(0L);
		userRespFromRedis = redCon.del(key.getBytes());
		return userRespFromRedis;
	}

	public String updateObject(String key, String value, Boolean extendExpirationTime, int expirationTime)
			throws RedisSessionException {
		RedisConnection redCon = connection.getConnection();
		byte[] sessionData = redCon.get(key.getBytes());
		String userRespFromRedis = null;
		if (sessionData != null) {
			userRespFromRedis = new String(redCon.get(key.getBytes()));
		}
		if ((userRespFromRedis != null) && (userRespFromRedis.trim().length() != 0)) {
			logger.info("updating session time of redis for " + key);
			redCon.set(key.getBytes(), value.getBytes(), Expiration.seconds(expirationTime), SetOption.UPSERT);
		} else {
			throw new RedisSessionException("Unable to fetch session object from Redis server");
		}

		return key;
	}


	public void save(String key, String value, long ttl, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
	}

	/**
	 * Save/update value in Redis without TTL (never expires until manually deleted)
	 */
	public void save(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	/**
	 * Get value from Redis. Returns null if key doesn't exist.
	 */
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * Delete/evict a single key
	 */
	public void evict(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * Check if a key exists
	 */
	public boolean exists(String key) {
		Boolean result = redisTemplate.hasKey(key);
		return Boolean.TRUE.equals(result);
	}

	/**
	 * Evict all keys matching a pattern (use carefully — KEYS is O(n), avoid on huge datasets)
	 * Example pattern: "incentives:user:*"
	 */
	public void evictByPattern(String pattern) {
		redisTemplate.keys(pattern).forEach(redisTemplate::delete);
	}


}
