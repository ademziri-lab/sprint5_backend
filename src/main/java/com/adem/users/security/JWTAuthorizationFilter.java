package com.adem.users.security;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwt = request.getHeader("Authorization");
		if (jwt == null || !jwt.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecParams.SECRET)).build();
		// enlever le préfixe Bearer du jwt
		jwt = jwt.substring(7); // 7 caractères dans "Bearer "
		DecodedJWT decodedJWT = verifier.verify(jwt);

		String username = decodedJWT.getSubject();
		List<String> roles = decodedJWT.getClaims().get("roles").asList(String.class);
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String r : roles)
			authorities.add(new SimpleGrantedAuthority(r));
		UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(username, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(user);
		filterChain.doFilter(request, response);
	}
	
	}


