package com.flowable.training.db;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * @author Matthias Stöckli
 */
class FlowablePlatformTrainingDefaultEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    public static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 999999;

    private static final String[] DEFAULT_NAMES = new String[] { "flowable-platform-training-module", "flowable-platform-training-default" };

    private int order = DEFAULT_ORDER;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        new Loader(environment, application.getResourceLoader()).load();
    }

    @Override
    public int getOrder() {
        return order;
    }

    private static class Loader {

        private final ConfigurableEnvironment environment;

        private final ResourceLoader resourceLoader;

        private final List<PropertySourceLoader> propertySourceLoaders;

        Loader(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
            this.environment = environment;
            this.resourceLoader = resourceLoader == null ? new DefaultResourceLoader() : resourceLoader;
            this.propertySourceLoaders = SpringFactoriesLoader.loadFactories(
                PropertySourceLoader.class, getClass().getClassLoader());
        }

        void load() {
            for (PropertySourceLoader loader : propertySourceLoaders) {
                for (String extension : loader.getFileExtensions()) {
                    Arrays.stream(DEFAULT_NAMES).map(name -> "classpath:/" + name + "." + extension).forEach(location -> load(location, loader));
                }
            }

        }

        void load(String location, PropertySourceLoader loader) {
            try {
                Resource resource = resourceLoader.getResource(location);
                if (!resource.exists()) {
                    return;
                }
                String propertyResourceName = "flowablePlatformTrainingConfig: [" + location + "]";

                List<PropertySource<?>> propertySources = loader.load(propertyResourceName, resource);
                if (propertySources == null) {
                    return;
                }
                propertySources.forEach(source -> environment.getPropertySources().addLast(source));
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to load property "
                    + "source from location '" + location + "'", ex);
            }
        }
    }
}
