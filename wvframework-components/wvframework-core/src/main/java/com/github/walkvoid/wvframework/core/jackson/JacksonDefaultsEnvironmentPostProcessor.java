package com.github.walkvoid.wvframework.core.jackson;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * Loads {@code classpath:wvframework-jackson.properties} as the lowest-precedence
 * property source so {@code spring.jackson.*} defaults apply before
 * {@code JacksonAutoConfiguration}, while still allowing application config to override.
 */
public class JacksonDefaultsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    public static final String PROPERTY_SOURCE_NAME = "wvframeworkJacksonDefaults";
    static final String LOCATION = "classpath:wvframework-jackson.properties";

    private final PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getPropertySources().contains(PROPERTY_SOURCE_NAME)) {
            return;
        }
        Resource resource = new DefaultResourceLoader().getResource(LOCATION);
        if (!resource.exists()) {
            return;
        }
        try {
            var loaded = loader.load(PROPERTY_SOURCE_NAME, resource);
            if (loaded.isEmpty()) {
                return;
            }
            environment.getPropertySources().addLast(loaded.get(0));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + LOCATION, e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
