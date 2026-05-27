package com.iemr.flw.utils;


import java.util.function.Function;
import javax.crypto.SecretKey;

import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.iemr.flw.utils.exception.IEMRException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String SECRET_KEY;
	private String userName;

	@Autowired
	private TokenDenylist tokenDenylist;
	// Generate a key using the secret
	private SecretKey getSigningKey() {
		if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
			throw new IllegalStateException("JWT secret key is not set in application.properties");
		}
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

	// Validate and parse JWT Token
	public Claims validateToken(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();

			String jti = claims.getId();

			// Check if token is denylisted (only if jti exists)
			if (jti != null && tokenDenylist.isTokenDenylisted(jti)) {
				return null;
			}

			return claims;
		} catch (Exception e) {
			return null; // Handle token parsing/validation errors
		}
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Integer extractUserId(String jwtToken) throws IEMRException {
		try {
			// Validate JWT token and extract claims
			Claims claims = validateToken(jwtToken);

			if (claims == null) {
				throw new IEMRException("Invalid JWT token.");
			}

			String userId = claims.get("userId", String.class);

			return Integer.parseInt(userId);

		} catch (Exception e) {
			throw new IEMRException("Validation error: " + e.getMessage(), e);
		}

	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claims != null ? claimsResolver.apply(claims) : null;
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public String getUserNameFromStorage() {
		return  userName;

	}

	public void setUserNameFromStorage(String loginUserName) {
		  this.userName = loginUserName;

	}
}
