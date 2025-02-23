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
package com.iemr.flw.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CryptoUtil {

	private static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);
	private static final String ALGORITHM = "AES";
	private static final String SECRET_KEY = "dev-envro-secret";

	public String encrypt(String value) throws Exception {
		SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(addPadding(value).getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public String decrypt(String encryptedValue) {
		try {
			SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
			return removePadding(new String(decryptedBytes, StandardCharsets.UTF_8));
		} catch (Exception e) {
			logger.error("Exception while decrypting string. Intentionally throwing null pointer", e);
			return null;
		}
	}

	private String addPadding(String value) {
		int paddingLength = 16 - (value.length() % 16);
		StringBuilder paddedValue = new StringBuilder(value);
		for (int i = 0; i < paddingLength; i++) {
			paddedValue.append((char) paddingLength);
		}
		return paddedValue.toString();
	}

	private String removePadding(String value) {
		int paddingLength = value.charAt(value.length() - 1);
		return value.substring(0, value.length() - paddingLength);
	}
}
