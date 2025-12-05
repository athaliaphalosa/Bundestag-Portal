package org.texttechnologylab.ppr.blatt3.utils;

import org.texttechnologylab.ppr.blatt3.config.AppProperties;
import org.texttechnologylab.ppr.blatt3.data.redenportal.ObjectFactory;
import org.texttechnologylab.ppr.blatt3.data.redenportal.Rede;
import org.texttechnologylab.ppr.blatt3.data.redenportal.Redner;
import org.texttechnologylab.ppr.blatt3.data.redenportal.Sitzung;
import org.texttechnologylab.ppr.blatt3.rest.RESTHandler;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * Loading XML data from the '20' folder in resources.
 * Starting the web interface using Javalin and FreeMarker.
 * The '20' folder must exist in the resources directory, otherwise the program will throw an error.
 */
public class MainUbung3 {

    /** Factory instance used to load and access XML data */
    private static ObjectFactory factory;

    /**
     * Loads XML data from the resources folder "20" and starts the web interface.
     * @param args
     */
    public static void main(String[] args) {
        // Load the '20' folder from classpath
        ClassLoader classLoader = MainUbung3.class.getClassLoader();
        URL resource = classLoader.getResource("20");
        if (resource == null) {
            throw new IllegalArgumentException("Folder 20 nicht gefunden!");
        }

        String folderPath = null;
        try {
            // Convert URL to absolute path
            folderPath = new File(resource.toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        System.out.println("-".repeat(180));
        System.out.println("XML-Daten werden geladen von: " + folderPath);
        System.out.println("Bitte kurz warten ;)");
        System.out.println("-".repeat(180));

        // Initialize factory and load XML files
        factory = new ObjectFactory();
        factory.loadXML(folderPath);

        // Start the web interface
        startWebInterface();

    }

    /**
     * Start the Web Interface using Javalin.
     * Any errors during startup are printed to the console.
     */
    private static void startWebInterface() {
        System.out.println("WEB INTERFACE WIRD GESTARTET...");

        try {
            AppProperties properties = new AppProperties();
            RESTHandler restHandler = new RESTHandler(factory, properties);
            restHandler.start();

        } catch (Exception e) {
            System.err.println(" !!Fehler beim Starten des Web Interface!! : " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n  Stellen Sie sicher, dass folgende Dependencies im POM.xml sind:");
            System.out.println("   - Javalin");
            System.out.println("   - FreeMarker");
        }
    }
}