package com.plantapps.MultiTenantSubscription.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig
{
	
	@Bean
	public SecurityFilterChain getSecurityFilterChain(HttpSecurity security) throws Exception
	{
		return security.authorizeHttpRequests(auth-> 
		auth.requestMatchers("/graphiql", "/subscriptions").permitAll()
		.requestMatchers("/gql.html").permitAll()
		//.requestMatchers(HttpMethod.POST, "/graphql").authenticated()
		.requestMatchers("/graphql").permitAll()
		.anyRequest().authenticated())
		.csrf(csrf->csrf.disable())
		.oauth2ResourceServer(oauth2->oauth2.jwt())
		.build();
	}
	
	@Bean
	public JwtDecoder jwtDecoder() {
	    byte[] keyBytes = "a-string-secret-at-least-256-bits-long".getBytes();
	    SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
	    return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}
	
	

}
