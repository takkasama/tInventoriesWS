package io.github.takkasama.web.tinventoriesws;

import io.github.takkasama.web.tinventoriesws.util.ConstraintViolationExceptionMapper;
import io.github.takkasama.web.tinventoriesws.controller.SecurityFilter;
import jakarta.ws.rs.ApplicationPath;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
@ApplicationPath("resources")
public class JakartaRestConfiguration extends ResourceConfig {
    
    
    public JakartaRestConfiguration() {
        super();

        packages("io.github.takkasama.web.tinventoriesws.controller",
                "io.swagger.v3.jaxrs2.integration.resources");
        registerCustomProviders();

    }

    private void registerCustomProviders() {

        Set<String> providerClassNamesSet = new HashSet<>() {
            {
                add(ConstraintViolationExceptionMapper.class.getName());
                add(SecurityFilter.class.getName());
            }

        };

        Map<String, Object> properties = new HashMap<>() {
            {
                put("jersey.config.server.provider.classnames", providerClassNamesSet.stream().collect(Collectors.joining(",")));
            }

        };
        addProperties(Collections.unmodifiableMap(properties));

    }

}
