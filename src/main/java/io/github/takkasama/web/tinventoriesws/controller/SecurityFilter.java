package io.github.takkasama.web.tinventoriesws.controller;

import io.github.takkasama.web.tinventoriesws.util.JwTokenHelper;
import io.github.takkasama.web.tinventoriesws.util.Secure;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;

/**
 *
 * @author cbcar 
 */
@Provider
@Secure
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter{
    
    
   
    private static final String AUTHORIZATION_SERVICE_PATH = "validateUser";
    private static final String ACTIVATION_SERVICE_PATH = "activateUser";

    private static final String RENEWAL_SERVICE_PATH = "renewToken";
    private final JwTokenHelper jwTokenHelper = JwTokenHelper.getInstance();
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        if (method.getName().equals(AUTHORIZATION_SERVICE_PATH) || method.getName().equals(ACTIVATION_SERVICE_PATH)) {
            return;
        }

      
        String authorizationHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);

       
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            abortWithUnauthorized(request, "Authorization is missing in header");
            return;
        }  else if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(request, "Invalid authorization");
            return;
        }
        
      
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        try {

           
            try {
                Claims claims = jwTokenHelper.claimKey(token);
                
                if (claims.containsKey("rnw") && (Boolean)claims.get("rnw")== true && !method.getName().equals(RENEWAL_SERVICE_PATH)){
                    abortWithUnauthorized(request, "Invalid authorization");
                }
                
                final SecurityContext currentSecurityContext = request.getSecurityContext();
                request.setSecurityContext(new SecurityContext() {

                    @Override
                    public Principal getUserPrincipal() {
                        return () -> claims.getSubject();
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        return currentSecurityContext.isSecure();
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return AUTHENTICATION_SCHEME;
                    }
                });
            } catch (ExpiredJwtException | MalformedJwtException e) {
                if (e instanceof ExpiredJwtException) {
                    abortWithUnauthorized(request, "Authorization is expired");
                } else if (e instanceof MalformedJwtException) {
                    abortWithUnauthorized(request, "Authorization is not correct");
                }
            }

        } catch (Exception e) {
            abortWithUnauthorized(request, "Invalid authorization");
        }
    }
    
    private boolean isTokenBasedAuthentication(String authorizationHeader) {

    
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase());
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {

  
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED.getStatusCode(), message)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                message)
                        .build());
    }
    
}
