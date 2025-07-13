package com.plantapps.MultiTenantSubscription.interceptor;

import java.util.Map;

import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class SiteWebSocketInterceptor implements WebSocketGraphQlInterceptor
{
//	private final AuthenticationManager authenticationManager;
//	public SiteWebSocketInterceptor(AuthenticationManager authenticationManager)
//	{
//		this.authenticationManager=authenticationManager;
//	}
	private final JwtDecoder jwtDecoder;
	public SiteWebSocketInterceptor(JwtDecoder jwtDecoder)
	{
		this.jwtDecoder=jwtDecoder;
	}
    
	@Override
	public Mono<Object> handleConnectionInitialization(WebSocketSessionInfo sessionInfo,
			Map<String, Object> connectionInitPayload)
	{
		 String rawToken = (String) connectionInitPayload.get("Authorization");
		 if (rawToken == null || !rawToken.startsWith("Bearer ")) {
	            return Mono.error(new AccessDeniedException("Missing or invalid Authorization token"));
	        }
		 
			String token = rawToken.substring(7);
			try
			{
			if (token != null) {
	            var jwt = jwtDecoder.decode(token);
	            var auth = new JwtAuthenticationToken(jwt);
	            SecurityContextHolder.getContext().setAuthentication(auth);
			}
			}
			catch (Exception e) {
				return Mono.error(new AccessDeniedException("Custom Error: Missing or invalid Authorization token"));
			}
	    
			try
			{
				// Optionally add tenantId or user info to context
				//sessionInfo.getAttributes().put("jwt", jwt);
				// sessionInfo.getAttributes().put("tenantId",
				// connectionInitPayload.get("site-id"));
//				BearerTokenAuthenticationToken authenticationToken = new BearerTokenAuthenticationToken(token);
//				authenticationManager.authenticate(authenticationToken);
				
				String siteId = (String) connectionInitPayload.get("site-id");

				// Save site-id in session attributes
				sessionInfo.getAttributes().put("site-id", siteId);
				// return Mono.just(connectionInitPayload);
				// Return a map of values to be added to the GraphQL context
				// return Mono.just(Map.of("site-id", siteId));

				return Mono.empty(); // allow connection
			}
			catch (JwtException e)
			{
				return Mono.error(new AccessDeniedException("Invalid token: " + e.getMessage()));
			}
	}
	
	@Override
	public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain)
	{
		String siteId = (String)request.getAttributes().get("site-id");

        if (siteId != null) {
            // Inject site-id into GraphQL context
            request.configureExecutionInput((executionInput, builder) ->
                builder.graphQLContext(contextBuilder -> contextBuilder.of("site-id", siteId)).build()
            );
        }

        return chain.next(request);
	}
}
