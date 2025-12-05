
package org.texttechnologylab.ppr.blatt3.rest;

import freemarker.template.Configuration;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinFreemarker;
import org.texttechnologylab.ppr.blatt3.config.AppProperties;
import org.texttechnologylab.ppr.blatt3.data.redenportal.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * RESTHandler handles both the web interface (FreeMarker templates),
 * and REST API endpoints for Bundestag Reden Portal
 */
public class RESTHandler {

    private final ObjectFactory factory; // Provides access to all Reden/Redner/Sitzungen/Fraktionen
    private final AppProperties properties;  // Server, template, and API configuration
    private Javalin app;  // Javalin web server instance
    private final List<Map<String, Object>> apiRoutes = new ArrayList<>();  // API route metadata for OpenAPI


    /**
     * Constructor for RESTHandler.
     * @param factory  ObjectFactory to access XML data
     * @param properties  AppProperties to configure server and templates
     */
    public RESTHandler(ObjectFactory factory, AppProperties properties) {
        this.factory = factory;
        this.properties = properties;
    }


    /**
     * Starts the Javalin server and sets up all routes for templates and REST API.
     */
    public void start() {
        try {
            app = Javalin.create(config -> {
                // Serve static files (CSS) from the classpath
                config.staticFiles.add(properties.getStaticDirectory(), Location.CLASSPATH);

                // Simple Freemarker config
                Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
                freemarkerConfig.setClassForTemplateLoading(this.getClass(), properties.getTemplateDirectory());

                config.fileRenderer(new JavalinFreemarker(freemarkerConfig));

            }).start(properties.getServerPort());
            setupRoutes();  // Register all pages and API endpoints
            System.out.println(" Server gestartet auf: http://" + properties.getServerHost() + ":" + properties.getServerPort());

        } catch (Exception e) {
            System.err.println(" Fehler beim Starten des Servers: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers API route metadata for OpenAPI generation.
     * @param method  HTTP method (CRUD)
     * @param path    API path
     * @param summary  Short summary of the endpoint
     * @param description   Detailed description of the endpoint
     */
    private void registerApiRoute(String method, String path, String summary, String description) {
        Map<String, Object> route = new HashMap<>();
        route.put("method", method.toLowerCase());
        route.put("path", path);
        route.put("summary", summary);
        route.put("description", description);
        apiRoutes.add(route);
    }

    /**
     * Setup all FreeMarker template routes and REST API endpoints.
     */
    private void setupRoutes() {
        String apiBase = properties.getApiBasePath();

        // FreeMarker Template routes
        app.get("/", this::renderHomePage);
        app.get("/redner/{id}", this::renderRednerPage);
        app.get("/rede/{id}", this::renderRedePage);
        app.get("/fraktionen", this::renderFraktionenPage);
        app.get("/sitzungen", this::renderSitzungenPage);
        app.get("/statistiken", this::renderStatistikenPage);

        // REST API routes
        // Get Redner
        app.get(apiBase + "/redner", this::getAllRedner);
        registerApiRoute("GET", apiBase + "/redner", "Liste aller Redner", "Gibt eine Liste aller Abgeordneten zurück, optional filterbar");
        app.get(apiBase + "/redner/{id}", this::getRednerById);
        registerApiRoute("GET", apiBase + "/redner/{id}", "Redner nach ID", "Gibt einen Redner anhand der ID zurück");
        app.get(apiBase + "/redner/search/{name}", this::searchRedner);
        registerApiRoute("GET", apiBase + "/redner/search/{name}", "Redner suchen", "Sucht Redner anhand des Namens");

        // Put, Post, Delete for Redner
        app.post(apiBase + "/redner", this::createRedner);
        registerApiRoute("POST", apiBase + "/redner", "Redner erstellen", "Erstellt einen neuen Redner (simuliert)");
        app.put(apiBase + "/redner/{id}", this::updateRedner);
        registerApiRoute("PUT", apiBase + "/redner/{id}", "Redner aktualisieren", "Aktualisiert einen bestehenden Redner (simuliert)");
        app.delete(apiBase + "/redner/{id}", this::deleteRedner);
        registerApiRoute("DELETE", apiBase + "/redner/{id}", "Redner löschen", "Löscht einen Redner (simuliert)");

        // Get Reden
        app.get(apiBase + "/reden", this::getAllReden);
        registerApiRoute("GET", apiBase + "/reden", "Liste aller Reden", "Gibt alle Reden zurück, filterbar nach Redner, Fraktion, Sitzung");
        app.get(apiBase + "/reden/{id}", this::getRedeById);
        registerApiRoute("GET", apiBase + "/reden/{id}", "Rede nach ID", "Gibt eine Rede anhand der ID zurück");
        app.get(apiBase + "/reden/redner/{rednerId}", this::getRedenByRedner);
        registerApiRoute("GET", apiBase + "/reden/redner/{rednerId}", "Reden nach Redner", "Gibt alle Reden eines bestimmten Redners zurück");


        // Get Fraktionen
        app.get(apiBase + "/fraktionen", this::getAllFraktionen);
        registerApiRoute("GET", apiBase + "/fraktionen", "Liste aller Fraktionen", "Gibt alle Fraktionen zurück");
        app.get(apiBase + "/fraktionen/{name}", this::getFraktionByName);
        registerApiRoute("GET", apiBase + "/fraktionen/{name}", "Fraktion nach Name", "Gibt eine Fraktion anhand des Namens zurück");

        // Get Sitzungen
        app.get(apiBase + "/sitzungen", this::getAllSitzungen);
        registerApiRoute("GET", apiBase + "/sitzungen", "Liste aller Sitzungen", "Gibt alle Sitzungen zurück");

        app.get(apiBase + "/sitzungen/{id}", this::getSitzungById);
        registerApiRoute("GET", apiBase + "/sitzungen/{id}", "Sitzung nach ID", "Gibt eine Sitzung anhand der ID zurück");

        // Get Statistiken
        app.get(apiBase + "/statistiken", this::getStatistiken);
        registerApiRoute("GET", apiBase + "/statistiken", "Statistiken", "Gibt allgemeine Statistiken zurück");

        // OpenAPI JSON endpoint
        app.get(apiBase + "/openapi.json", this::generateOpenApi);

        // Swagger UI endpoint
        app.get("/swagger", this::showSwaggerUI);

        // Alternative URLs for Swagger
        app.get("/api/docs", ctx -> ctx.redirect("/swagger"));
        app.get("/docs", ctx -> ctx.redirect("/swagger"));
        app.get("/api-docs", ctx -> ctx.redirect("/swagger"));

        // Exception handling
        app.exception(Exception.class, (e, ctx) -> {
            System.err.println("Fehler in Route " + ctx.path() + ": " + e.getMessage());
            ctx.status(500).json(Map.of("error", "Interner Serverfehler"));
        });
    }

    /**
     * Generates an OpenAPI 3.0.3 specification dynamically based on all routes,
     * stored in the `apiRoutes` list.
     * @param ctx Javalin context for the request
     */
    private void generateOpenApi(Context ctx) {
        // The main JSON object that will be returned
        Map<String, Object> openApi = new HashMap<>();

        // API information
        Map<String, Object> info = new HashMap<>();
        info.put("title", properties.getAppName() + " API");
        info.put("version", properties.getAppVersion());
        info.put("description", "Dynamisch generierte OpenAPI Spezifikation für das Bundestag Reden Portal");

        // Server information
        List<Map<String, String>> servers = new ArrayList<>();
        Map<String, String> server = new HashMap<>();

        // Base URL from API
        server.put("url", "http://" + properties.getServerHost() + ":" + properties.getServerPort());
        server.put("description", "Development Server");
        servers.add(server);

        // Add basic openAPI fields
        openApi.put("openapi", "3.0.3");
        openApi.put("info", info);
        openApi.put("servers", servers);

        // Build paths
        Map<String, Object> paths = new HashMap<>();

        // Loop through all API routes that were registered earlier
        for (Map<String, Object> route : apiRoutes) {
            String path = (String) route.get("path");   //ex. /api/rede/{id}
            String method = (String) route.get("method");  //GET

            // Either reuse an existing path entry or create a new one
            Map<String, Object> pathItem = (Map<String, Object>) paths.getOrDefault(path, new HashMap<>());
            Map<String, Object> operation = new HashMap<>();

            // Create the object that describes this specific HTTP operation
            operation.put("summary", route.get("summary"));
            operation.put("description", route.get("description"));

            // Automatically assign tags based on URL
            // "tags" help organize endpoints in Swagger UI
            if (path.contains("/redner")) {
                operation.put("tags", List.of("Redner"));
            } else if (path.contains("/reden")) {
                operation.put("tags", List.of("Reden"));
            } else if (path.contains("/fraktionen")) {
                operation.put("tags", List.of("Fraktionen"));
            } else if (path.contains("/sitzungen")) {
                operation.put("tags", List.of("Sitzungen"));
            } else if (path.contains("/statistiken")) {
                operation.put("tags", List.of("Statistiken"));
            }

            // Add parameters for path variables (ex. {id}, {name})
            List<Map<String, Object>> parameters = new ArrayList<>();
            if (path.contains("{id}")) {
                Map<String, Object> param = new HashMap<>();
                param.put("name", "id");
                param.put("in", "path");
                param.put("required", true);
                param.put("schema", Map.of("type", "string"));
                param.put("description", "Eindeutige ID");
                parameters.add(param);
            }
            if (path.contains("{name}")) {
                Map<String, Object> param = new HashMap<>();
                param.put("name", "name");
                param.put("in", "path");
                param.put("required", true);
                param.put("schema", Map.of("type", "string"));
                param.put("description", "Name für die Suche");
                parameters.add(param);
            }
            if (path.contains("{rednerId}")) {
                Map<String, Object> param = new HashMap<>();
                param.put("name", "rednerId");
                param.put("in", "path");
                param.put("required", true);
                param.put("schema", Map.of("type", "string"));
                param.put("description", "ID des Redners");
                parameters.add(param);
            }

            // Only add parameters if they exist
            if (!parameters.isEmpty()) {
                operation.put("parameters", parameters);
            }

            // Add default response definitions
            Map<String, Object> responses = new HashMap<>();
            responses.put("200", Map.of("description", "Erfolgreiche Antwort"));
            responses.put("404", Map.of("description", "Ressource nicht gefunden"));
            responses.put("500", Map.of("description", "Interner Serverfehler"));

            operation.put("responses", responses);

            // Save the HTTP method (GET) under this path
            pathItem.put(method, operation);
            paths.put(path, pathItem);
        }

        // Add all paths to the main OpenAPI object
        openApi.put("paths", paths);

        // Return the final OpenAPI JSON
        ctx.contentType("application/json; charset=utf-8");
        ctx.json(openApi);
    }

    /**
     * Serves a simple HTML page that loads Swagger UI
     * @param ctx  Javalin context used to send the HTML response
     */
    private void showSwaggerUI(Context ctx) {
        // Get the app name and base API path from the configuration
        String appName = properties.getAppName();
        String apiBase = properties.getApiBasePath();

        // Create an HTML page that:
        // 1. loads Swagger CSS and JS from a CDN (Content Delivery Network)
        // 2. displays Swagger UI inside <div id="swagger-ui">
        // 3. tells Swagger to load our OpenAPI JSON file
        String html = """
        <!DOCTYPE html>
        <html lang="de">
        <head>
            <meta charset="UTF-8">
            <title>%s - API Docs</title>
            
            <!-- Swagger UI default CSS -->
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5.10.5/swagger-ui.css">
        </head>
        <body>
        
            <!-- Where Swagger UI will be rendered -->
            <div id="swagger-ui"></div>
            
            <!-- Swagger UI JavaScript bundle -->
            <script 
                src="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5.10.5/swagger-ui-bundle.js">
            </script>
            <script>
                // Initialize Swagger UI when the page loads
                window.onload = function() {
                    window.ui = SwaggerUIBundle({
                    
                        // URL of the OpenAPI JSON file
                        url: '%s/openapi.json',
                        dom_id: '#swagger-ui',
                        presets: [
                            SwaggerUIBundle.presets.apis
                        ]
                    });
                };
            </script>
        </body>
        </html>
        """.formatted(appName, apiBase);

        // Send the generated HTML to the browser
        ctx.html(html);
    }

    /**
     * Renders the home page (index.ftl) and fills it with basic statistics (Übersicht).
     * The basic statistics collects the total number of speeches, speakers, sessions, and factions from the data factory.
     * @param ctx  Javalin context used to render the HTML page
     */
    private void renderHomePage(Context ctx) {

        // Model map that will be passed to the FreeMarker template
        Map<String, Object> model = new HashMap<>();

        // Page title
        model.put("title", "Reden-Portal - Startseite");

        // Basic stastitics (Übersicht Statistiken)
        model.put("totalReden", factory.getAlleRede().size());
        model.put("totalRedner", factory.getAlleRedner().size());
        model.put("totalSitzungen", factory.getAlleSitzung().size());
        model.put("totalFraktionen", factory.getAlleFraktion().size());

        // Load a list of top speakers(Redner)
        List<Redner> topRedner = factory.getAlleRedner().stream()
                .limit(26000)
                .collect(Collectors.toList());
        model.put("topRedner", topRedner);

        // Render the "index.ftl" FreeMarker template with the model data
        ctx.render("index.ftl", model);
    }

    /**
     * Renders the page of a single speaker (Redner).
     * This method takes the speaker ID from the URL, finds the matching speaker,
     * and loads all speeches (Reden) belonging to that speaker.
     * @param ctx  Javalin context used to render the template
     */
    private void renderRednerPage(Context ctx) {

        // Get the speaker ID from the URL
        String rednerId = ctx.pathParam("id");

        // Data model to pass to the FreeMarker template
        Map<String, Object> model = new HashMap<>();

        // Finde the speaker
        Redner redner = findRednerById(rednerId);
        if (redner == null) {
            ctx.status(404).render("error.ftl", Map.of("message", "Redner nicht gefunden"));
            return;
        }

        // Page title: speakers full name
        model.put("title", redner.getVorname() + " " + redner.getNachname());
        model.put("redner", redner);

        // Load all speeches (Rede) from this speaker
        List<Rede> reden = factory.getAlleRede().stream()
                .filter(rede -> rede.getRedner() != null && rednerId.equals(rede.getRedner().getId()))
                .collect(Collectors.toList());
        model.put("reden", reden);

        // Total of speeches
        model.put("anzahlReden", reden.size());

        // Sum of all comments from all speeches
        model.put("gesamtKommentare", reden.stream()
                .mapToInt(rede -> rede.getKommentar().size())
                .sum());

        // Render FreeMarker template "redner.ftl"
        ctx.render("redner.ftl", model);
    }

    /**
     * Renders the page for a single speech (Rede).
     * This method reads the speech ID from the URL, finds the speech,
     * and loads its details and comments.
     * @param ctx Javalin context used to render the template
     */
    private void renderRedePage(Context ctx) {

        // Get speech ID from URL
        String redeId = ctx.pathParam("id");

        // Model map that will be sent to the template
        Map<String, Object> model = new HashMap<>();

        // Try to find the speech
        Rede rede = findRedeById(redeId);
        if (rede == null) {
            ctx.status(404).render("error.ftl", Map.of("message", "Rede nicht gefunden"));
            return;
        }

        // Create the page title (include speakers name if available)
        model.put("title", "Rede von " +
                (rede.getRedner() != null ?
                        rede.getRedner().getVorname() + " " + rede.getRedner().getNachname() : "Unbekannt"));
        model.put("rede", rede);

        // Load comments for this speech
        model.put("kommentare", rede.getKommentar());

        // Render the speech page
        ctx.render("rede.ftl", model);
    }

    /**
     * Renders the page that shows all factions (Fraktionen).
     * It loads all factions from the factory and also counts,
     * how many speakers belong to each faction.
     * @param ctx  Javalin context used to render the template
     */
    private void renderFraktionenPage(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Fraktionen Übersicht");

        // All factions from factory
        model.put("fraktionen", factory.getAlleFraktion());

        // Count how many speakers each faction has.
        // Speakers without a faction are grouped as "Fraktionslos".
        Map<String, Long> rednerProFraktion = factory.getAlleRedner().stream()
                .collect(Collectors.groupingBy(
                        redner -> redner.getFraktion() != null ? redner.getFraktion().getName() : "Fraktionslos",
                        Collectors.counting()
                ));
        model.put("rednerProFraktion", rednerProFraktion);

        // Render the FreeMarker template
        ctx.render("fraktionen.ftl", model);
    }

    /**
     * Renders the page that lists all parliamentary sessions (Sitzungen).
     * It just loads all sessions and sends them to the template.
     * @param ctx  Javalin context used to render the template
     */
    private void renderSitzungenPage(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Sitzungen Übersicht");
        // All sessions
        model.put("sitzungen", factory.getAlleSitzung());

        // Show the page
        ctx.render("sitzungen.ftl", model);
    }

    /**
     * Renders the statistics page.
     * Currently only sets a title, the rest is handled inside the template.
     *
     */
    private void renderStatistikenPage(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        // Page title
        model.put("title", "Statistiken Reden Portal");

        // Render the statistics template
        ctx.render("statistiken.ftl", model);
    }

    // REST API Methods
    /**
     * Returns all speakers (Redner) as JSON.
     * Allows optional filtering by faction, searching by name, and sorting by name or number of speeches.
     * @param ctx Javalin context object.
     * ctx is used to read request data (query params) and to send back the JSON response.
     */
    private void getAllRedner(Context ctx) {
        try {
            String fraktion = ctx.queryParam("fraktion");
            String sort = ctx.queryParam("sort");
            String search = ctx.queryParam("search");

            List<Redner> redner = factory.getAlleRedner().stream()

                    // Filter by faction (optional)
                    .filter(r -> fraktion == null ||
                            (r.getFraktion() != null && r.getFraktion().getName().equalsIgnoreCase(fraktion)))

                    // Search by name (optional)
                    .filter(r -> search == null ||
                            (r.getVorname() + " " + r.getNachname()).toLowerCase().contains(search.toLowerCase()))

                    // Sorting
                    .sorted((r1, r2) -> {

                        // Sort alphabetically by name
                        if ("name".equals(sort)) {
                            return (r1.getNachname() + r1.getVorname()).compareTo(r2.getNachname() + r2.getVorname());
                        }

                        // No Sorting
                        return 0;
                    })
                    .collect(Collectors.toList());

            // Send JSON result
            ctx.json(redner);

        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Redner: " + e.getMessage()));
        }
    }

    /**
     * Returns one specific speaker based on the given ID.
     * @param ctx Javalin context object.
     * ctx is used to read the path parameter "id" and to return the result as JSON.
     */
    private void getRednerById(Context ctx) {
        try {
            String id = ctx.pathParam("id");

            // Find speaker by ID
            Redner redner = findRednerById(id);
            if (redner != null) {
                ctx.json(redner);
            } else {
                ctx.status(404).json(Map.of("error", "Redner nicht gefunden"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden des Redners: " + e.getMessage()));
        }
    }


    /**
     * Searches speakers based on a name or part of a name.
     * The search is case-insensitive and matches first name, last name, or the full name.
     * @param ctx Javalin context object.
     * ctx is used to read the path parameter "name" and to return search results as JSON.
     */
    private void searchRedner(Context ctx) {
        try {
            String name = ctx.pathParam("name").toLowerCase();
            List<Redner> results = factory.getAlleRedner().stream()
                    // Check if first name or last name contains the search term
                    .filter(redner ->
                            redner.getVorname().toLowerCase().contains(name) ||
                                    redner.getNachname().toLowerCase().contains(name) ||
                                    (redner.getVorname() + " " + redner.getNachname()).toLowerCase().contains(name))
                    .collect(Collectors.toList());

            // Return matching results as JSON
            ctx.json(results);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler bei der Suche: " + e.getMessage()));
        }
    }


    /**
     * Returns all speeches (Reden) as JSON.
     * Supports optional filtering.
     * @param ctx Javalin context object.
     * ctx is used to read query parameters and send JSON response.
     */
    private void getAllReden(Context ctx) {
        try {
            String rednerId = ctx.queryParam("rednerId");
            String fraktion = ctx.queryParam("fraktion");
            String sitzung = ctx.queryParam("sitzung");
            String sort = ctx.queryParam("sort");

            List<Rede> reden = factory.getAlleRede().stream()
                    .filter(rede -> rednerId == null ||
                            (rede.getRedner() != null && rednerId.equals(rede.getRedner().getId())))
                    .filter(rede -> fraktion == null ||
                            (rede.getRedner() != null && rede.getRedner().getFraktion() != null &&
                                    rede.getRedner().getFraktion().getName().equalsIgnoreCase(fraktion)))
                    .filter(rede -> sitzung == null ||
                            (rede.getSitzung() != null &&
                                    (rede.getSitzung().getWahlperiode() + "_" + rede.getSitzung().getSitzungNr()).equals(sitzung)))
                    .collect(Collectors.toList());

            ctx.json(reden);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Reden: " + e.getMessage()));
        }
    }

    /**
     * Returns one speech (Rede) by ID as JSON.
     * @param ctx Javalin context object.
     * ctx used to read path parameter "id" and send JSON response.
     */
    private void getRedeById(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            Rede rede = findRedeById(id);
            if (rede != null) {
                ctx.json(rede);
            } else {
                ctx.status(404).json(Map.of("error", "Rede nicht gefunden"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Rede: " + e.getMessage()));
        }
    }

    /**
     * Returns all speeches (Reden) by a specific speaker (Redner) as JSON.
     * @param ctx Javalin context object.
     * ctx used to read path parameter "rednerId" and send JSON response.
     */
    private void getRedenByRedner(Context ctx) {
        try {
            String rednerId = ctx.pathParam("rednerId");
            List<Rede> reden = factory.getAlleRede().stream()
                    .filter(rede -> rede.getRedner() != null && rednerId.equals(rede.getRedner().getId()))
                    .collect(Collectors.toList());
            ctx.json(reden);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Reden: " + e.getMessage()));
        }
    }

    /**
     * Returns all factions (Fraktionen) as JSON.
     * @param ctx Javalin context object.
     * ctx used to send JSON response.
     */
    private void getAllFraktionen(Context ctx) {
        try {
            ctx.json(factory.getAlleFraktion());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Fraktionen: " + e.getMessage()));
        }
    }

    /**
     * Returns one faction by its name as JSON.
     * Also includes the number of speakers and speeches in that faction.
     * @param ctx Javalin context object.
     * ctx used to read path parameter "name" and send JSON response.
     */
    private void getFraktionByName(Context ctx) {
        try {
            String name = ctx.pathParam("name");
            Fraktion fraktion = factory.getAlleFraktion().stream()
                    .filter(f -> f.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);

            if (fraktion != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("fraktion", fraktion);
                result.put("anzahlRedner", factory.getAlleRedner().stream()
                        .filter(r -> r.getFraktion() != null && r.getFraktion().getName().equals(fraktion.getName()))
                        .count());
                result.put("anzahlReden", factory.getAlleRede().stream()
                        .filter(r -> r.getRedner() != null && r.getRedner().getFraktion() != null &&
                                r.getRedner().getFraktion().getName().equals(fraktion.getName()))
                        .count());
                ctx.json(result);
            } else {
                ctx.status(404).json(Map.of("error", "Fraktion nicht gefunden"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Fraktion: " + e.getMessage()));
        }
    }

    /**
     * Returns all sessions (Sitzungen) as JSON.
     * @param ctx Javalin context object.
     * ctx used to send JSON response.
     */
    private void getAllSitzungen(Context ctx) {
        try {
            ctx.json(factory.getAlleSitzung());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Sitzungen: " + e.getMessage()));
        }
    }

    /**
     * Returns one session (Sitzung) by ID as JSON.
     * Also includes all speeches in that session.
     * @param ctx Javalin context object.
     * ctx used to read path parameter "id" and send JSON response.
     */
    private void getSitzungById(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            Sitzung sitzung = factory.getAlleSitzung().stream()
                    .filter(s -> (s.getWahlperiode() + "_" + s.getSitzungNr()).equals(id))
                    .findFirst()
                    .orElse(null);

            if (sitzung != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("sitzung", sitzung);
                result.put("reden", factory.getAlleRede().stream()
                        .filter(rede -> rede.getSitzung() != null &&
                                rede.getSitzung().getWahlperiode().equals(sitzung.getWahlperiode()) &&
                                rede.getSitzung().getSitzungNr().equals(sitzung.getSitzungNr()))
                        .collect(Collectors.toList()));
                ctx.json(result);
            } else {
                ctx.status(404).json(Map.of("error", "Sitzung nicht gefunden"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Fehler beim Laden der Sitzung: " + e.getMessage()));
        }
    }

    /**
     * Returns general statistics for the portal as JSON.
     * Includes counts of speeches, speakers, sessions, factions, comments,
     * top 5 speakers, speeches per faction, and average comments/length.
     * @param ctx Javalin context object.
     * ctx used to send JSON response with UTF-8 content type.
     */
    private void getStatistiken(Context ctx) {
        try {
            // Create a map to store all statistics
            Map<String, Object> stats = new HashMap<>();

            // Total counts
            stats.put("gesamtReden", factory.getAlleRede().size());
            stats.put("gesamtRedner", factory.getAlleRedner().size());
            stats.put("gesamtSitzungen", factory.getAlleSitzung().size());
            stats.put("gesamtFraktionen", factory.getAlleFraktion().size());
            stats.put("gesamtKommentare", factory.getAlleKommentar().size());

            // Top 5 speakers based on number of speeches
            List<Map<String, Object>> topRednerList = factory.getAlleRedner().stream()
                    .map(redner -> {
                        Map<String, Object> rednerStat = new HashMap<>();
                        rednerStat.put("name", redner.getVorname() + " " + redner.getNachname());
                        rednerStat.put("id", redner.getId());

                        // Faction name or "Fraktionslos" if none
                        String fraktionName = "Fraktionslos";
                        if (redner.getFraktion() != null && redner.getFraktion().getName() != null) {
                            fraktionName = redner.getFraktion().getName();
                        }

                        // Number of speeches for this speaker
                        rednerStat.put("fraktion", fraktionName);
                        rednerStat.put("anzahlReden", getAnzahlRedenFuerRedner(redner.getId()));
                        return rednerStat;
                    })

                    // Sort descending by number of speeches
                    .sorted((r1, r2) -> {
                        Long count1 = (Long) r1.get("anzahlReden");
                        Long count2 = (Long) r2.get("anzahlReden");
                        return count2.compareTo(count1);
                    })
                    .limit(5)
                    .collect(Collectors.toList());
            stats.put("topRedner", topRednerList);

            // Number of speeches per faction
            Map<String, Long> redenProFraktion = factory.getAlleRede().stream()
                    .filter(rede -> rede.getRedner() != null && rede.getRedner().getFraktion() != null)
                    .collect(Collectors.groupingBy(
                            rede -> {
                                String fraktionName = rede.getRedner().getFraktion().getName();
                                return (fraktionName != null) ? fraktionName : "Fraktionslos";
                            },
                            Collectors.counting()
                    ));
            stats.put("redenProFraktion", redenProFraktion);

            // Number of speakers per faction
            Map<String, Long> rednerProFraktion = factory.getAlleRedner().stream()
                    .collect(Collectors.groupingBy(
                            redner -> {
                                if (redner.getFraktion() == null) return "Fraktionslos";
                                String fraktionName = redner.getFraktion().getName();
                                return (fraktionName != null) ? fraktionName : "Fraktionslos";
                            },
                            Collectors.counting()
                    ));
            stats.put("rednerProFraktion", rednerProFraktion);

            // Average number of comments per speech
            double avgKommentare = factory.getAlleRede().stream()
                    .mapToInt(rede -> rede.getKommentar().size())
                    .average()
                    .orElse(0.0);
            stats.put("durchschnittKommentareProRede", Math.round(avgKommentare * 10.0) / 10.0);

            // Average speech length (number of characters)
            double avgRedenLaenge = factory.getAlleRede().stream()
                    .mapToInt(rede -> rede.getText().length())
                    .average()
                    .orElse(0.0);
            stats.put("durchschnittRedenLaenge", Math.round(avgRedenLaenge));

            // Convert stats map to JSON and send as response
            String jsonResponse = generateUtf8Json(stats);
            ctx.contentType("application/json; charset=utf-8");
            ctx.result(jsonResponse);

        } catch (Exception e) {
            // Error handling
            System.err.println("FEHLER in getStatistiken: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("{\"error\":\"Fehler beim Laden der Statistiken\"}");
        }
    }


    // Post, Put, Delete methods for Redner (Speeches) and Rede (Speaker)
    /**
     * Creates a new Redner (speaker).
     * @param ctx  Javalin request context containing the JSON
     */
    private void createRedner(Context ctx) {
        try {
            System.out.println("POST /api/redner called");

            String body = ctx.body();
            if (body == null || body.trim().isEmpty()) {
                ctx.status(400).json(Map.of(
                        "error", "Request body is required",
                        "example", Map.of(
                                "vorname", "Max",
                                "nachname", "Mustermann",
                                "titel", "Dr.",
                                "geburtsdatum", "1970-01-01",
                                "geschlecht", "männlich"
                        )
                ));
                return;
            }

            var requestData = ctx.bodyAsClass(Map.class);

            ctx.status(201).json(Map.of(
                    "status", "success",
                    "message", "Redner created (factory is read only)",
                    "data_received", requestData
            ));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Bad request: " + e.getMessage()));
        }
    }

    /**
     * Updates an existing Redner by ID.
     * @param ctx  Javalin request context with path parameter and update data.
     */
    private void updateRedner(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            System.out.println("PUT /api/redner/" + id + " called");

            String body = ctx.body();
            if (body == null || body.trim().isEmpty()) {
                ctx.status(400).json(Map.of(
                        "error", "Request body is required for update",
                        "example", Map.of("vorname", "New Name")
                ));
                return;
            }

            // Read update data from JSON request body
            var updateData = ctx.bodyAsClass(Map.class);

            // Check if redner exist
            Redner redner = findRednerById(id);
            if (redner == null) {
                ctx.status(404).json(Map.of("error", "Redner nicht gefunden"));
                return;
            }

            // Simulated update response
            ctx.json(Map.of(
                    "status", "success",
                    "message", "Redner updated (factory is read only)",
                    "id", id,
                    "existing_redner", redner.getVorname() + " " + redner.getNachname(),
                    "updates_requested", updateData
            ));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Bad request: " + e.getMessage()));
        }
    }

    /**
     * Deletes an existing Redner by ID.
     * @param ctx  Javalin request context containing the ID
     */    private void deleteRedner(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            System.out.println("DELETE /api/redner/" + id + " called");

            // Check if Redner available
            Redner redner = findRednerById(id);
            if (redner == null) {
                ctx.status(404).json(Map.of("error", "Redner nicht gefunden"));
                return;
            }

            // Simulated delete response
            ctx.json(Map.of(
                    "status", "success",
                    "message", "Redner deleted (factory is read only)",
                    "id", id,
                    "deleted_redner", redner.getVorname() + " " + redner.getNachname()
            ));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Bad request: " + e.getMessage()));
        }
    }


    // Helper methods
    /**
     * Converts a Map to a JSON string using UTF-8 encoding.
     * @param data the map to convert to JSON
     * @return JSON string representation of the map
     */
    private String generateUtf8Json(Map<String, Object> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(escapeJson(entry.getKey())).append("\":");

            Object value = entry.getValue();
            json.append(valueToJson(value));
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Converts an object to JSON string representation.
     * @param value the object to convert
     * @return JSON string representation of the object
     */
    private String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Map) {
            return generateUtf8Json((Map<String, Object>) value);
        } else if (value instanceof List) {
            return listToJson((List<?>) value);
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }

    /**
     * Converts a List to a JSON array string.
     * Calls valueToJson on each item in the list.
     * @param list the list to convert
     * @return JSON array string
     */
    private String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        boolean first = true;
        for (Object item : list) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append(valueToJson(item));
        }

        json.append("]");
        return json.toString();
    }

    /**
     * Escapes special characters in a string for JSON.
     * @param str the input string
     * @return escaped string
     */
    private String escapeJson(String str) {
        if (str == null) return "";

        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Finds a Redner object by its ID.
     * @param id the ID of the Redner
     * @return the Redner if found, otherwise null
     */
    private Redner findRednerById(String id) {
        return factory.getAlleRedner().stream()
                .filter(r -> id.equals(r.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a Rede object by its ID.
     * @param id the ID of the Rede
     * @return the Rede if found, otherwise null
     */
    private Rede findRedeById(String id) {
        return factory.getAlleRede().stream()
                .filter(r -> id.equals(r.getRid()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Counts the number of speeches (Reden) for a given Redner ID.
     * @param rednerId the ID of the Redner
     * @return number of speeches, or 0 if ID is null or error
     */
    private long getAnzahlRedenFuerRedner(String rednerId) {
        if (rednerId == null) return 0;
        try {
            return factory.getAlleRede().stream()
                    .filter(rede -> rede.getRedner() != null && rednerId.equals(rede.getRedner().getId()))
                    .count();
        } catch (Exception e) {
            System.err.println("Fehler in getAnzahlRedenFuerRedner für Redner " + rednerId + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Stops the Javalin server if it is running.
     */
    public void stop() {
        if (app != null) {
            app.stop();
            System.out.println(" Server gestoppt!!");
        }
    }
}
