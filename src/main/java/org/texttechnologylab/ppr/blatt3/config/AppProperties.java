package org.texttechnologylab.ppr.blatt3.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * AppProperties handles application configuration by loading properties,
 * from the "app.properties" file located in the resources folder.
 * If a property is not set in the file, default values are applied.
 */
public class AppProperties {
    private final Properties properties;

    /**
     * Constructor initializes the properties object,
     * loads properties from the file, and applies default values.
     */
    public AppProperties() {
        this.properties = new Properties();
        loadProperties();
        setDefaults();
    }

    /**
     * Loads properties from the "app.properties" file in the classpath.
     * If the file is not found, only default values will be used.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println(" Properties loaded");
            }
        } catch (IOException e) {
            System.err.println(" Error loading properties: " + e.getMessage());
        }
    }

    /**
     * Sets default values for all properties if they are missing.
     */
    private void setDefaults() {
        if (!properties.containsKey("server.port")) properties.setProperty("server.port", "7070");
        if (!properties.containsKey("server.host")) properties.setProperty("server.host", "localhost");
        if (!properties.containsKey("app.name")) properties.setProperty("app.name", "Reden_Portal");
        if (!properties.containsKey("app.version")) properties.setProperty("app.version", "1.0.0");
        if (!properties.containsKey("api.base.path")) properties.setProperty("api.base.path", "/api");
        if (!properties.containsKey("template.directory")) properties.setProperty("template.directory", "/templates");
        if (!properties.containsKey("static.directory")) properties.setProperty("static.directory", "/formatting");
    }

    // Getters
    /**
     * Returns the server port to use for the application.
     */
    public int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }

    /**
     * Returns the server host.
     */
    public String getServerHost() {
        return properties.getProperty("server.host");
    }

    /**
     * Returns the application name.
     */
    public String getAppName() {
        return properties.getProperty("app.name");
    }

    /**
     * Returns the application version.
     */
    public String getAppVersion() {
        return properties.getProperty("app.version");
    }

    /**
     * Returns the base path for the REST API ("/api").
     */
    public String getApiBasePath() {
        return properties.getProperty("api.base.path");
    }

    /**
     * Returns the directory for static resources such as CSS.
     * FreeMarker templates are not included here, they are in the template directory.
     */
    public String getStaticDirectory() {
        return properties.getProperty("static.directory");
    }

    /**
     * Returns the directory for FreeMarker templates (FTL files).
     */
    public String getTemplateDirectory() {
        return properties.getProperty("template.directory");
    }
}