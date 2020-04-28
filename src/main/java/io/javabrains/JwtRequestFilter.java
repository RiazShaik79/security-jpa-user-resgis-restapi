package io.javabrains;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private jwtUtil jwtUtil;
	
	@Autowired
	private PersonRepository personRepository;
	
	private Person person;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		
		
		final String authorizationHeader = request.getHeader("Authorization");
		String username =null;
		String jwt = null;
		
	
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
			jwt = authorizationHeader.substring(7);
			username = jwtUtil.extractUsername(jwt);
			//System.out.println(authorizationHeader.substring(7));
		}
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			if (jwtUtil.ValidateToken(jwt)) {
								
				person = personRepository.findOne(jwtUtil.extractUsername(jwt));
							
				try {
			//	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(jwtUtil.extractUsername(jwt),usernamePasswordAuthenticationToken);
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(person.getUid(), null, new ArrayList<>());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				
			}
			
			catch (Exception e) {
				System.out.println("error" + e);
			}
			
		}
		
		filterChain.doFilter(request, response);
	}
	}

}	
