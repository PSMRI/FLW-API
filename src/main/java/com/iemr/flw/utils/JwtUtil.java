package com.iemr.flw.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.function.Function;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	private static final long EXPIRATION_TIME = 24L * 60 * 60 * 1000; // 1 day in milliseconds

	// Generate a key using the secret
	private Key getSigningKey() {
		if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
			throw new IllegalStateException("JWT secret key is not set in application.properties");
		}
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

	// Validate and parse JWT Token
	public Claims validateToken(String token) {
		try {
			return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		} catch (Exception e) {
			return null; // Handle token parsing/validation errors
		}
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}
}
