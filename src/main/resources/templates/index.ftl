<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- Page title seen in browser tab -->
    <title>Reden Portal Bundestag</title>
    <!-- Load CSS File for styling the page -->
    <link rel="stylesheet" href="/style.css">
</head>
<body>
<header>
    <!-- Main title of the website -->
    <h1>Reden Portal Bundestag</h1>
    <!-- Einfach welcome text -->
    <p>Herzlichen Willkommen im Bundestagportal für detaillierte alle Abgeordnete, Rede, Reden, und Sitzung</p>

    <!-- navigation bar to switch the menu -->
    <nav class="main-nav">
        <a href="/fraktionen"> Fraktionen </a>
        <a href="/sitzungen"> Sitzungen </a>
        <a href="/statistiken"> Statistiken </a>

        <!-- API Documentation Links -->
        <div class="api-links">
            <a href="/swagger" target="_blank"> API Docs (Swagger)</a>
            <a href="/api/openapi.json" target="_blank">     OpenAPI JSON </a>
        </div>
    </nav>

</header>

<section>
    <!-- Overview statistics section -->
    <h2> Übersicht </h2>
    <div class="stats-grid">
        <div class="stat-card">
            <!-- FreeMarker injects the total number of speeches/Reden -->
            <h3>${totalReden}</h3>
            <p>Reden</p>
        </div>
        <div class="stat-card">
            <h3>${totalRedner}</h3>
            <p>Redner/Abgeordnete</p>
        </div>
        <div class="stat-card">
            <h3>${totalSitzungen}</h3>
            <p>Sitzungen</p>
        </div>
        <div class="stat-card">
            <h3>${totalFraktionen}</h3>
            <p>Fraktionen</p>
        </div>
    </div>
</section>

<section class="search-section">
    <h2> Abgeordneten Suchen </h2>
    <!-- Search input + button
         JavaScript function searchRedner() will be called -->
    <div class="search-box">
        <input type="text" id="searchInput" placeholder="Namen eingeben bitte">
        <button onclick="searchRedner()">Suche</button>
    </div>
    <!-- Search results will be inserted here dynamically using JavaScript -->
    <div id="searchResults"></div>
</section>

<!-- List of all speakers (sortable) -->
<section>
    <h2> Abgeordnete </h2>

    <!-- Dropdown to choose sorting method (name or fraktion) -->
    <div class="sort-options">
        <label for="sortSelect">Sortieren nach:</label>

        <!-- When user changes selection → run sortRedner() -->
        <select id="sortSelect" onchange="sortRedner()">
            <option value="name_asc">Name A-Z</option>
            <option value="name_desc">Name Z-A</option>
            <option value="fraktion_asc">Fraktion A-Z</option>
            <option value="fraktion_desc">Fraktion Z-A</option>
        </select>
    </div>

    <div class="redner-list" id="rednerList">
        <!-- The data will be filled in by JavaScript after it has been sorted -->
    </div>
</section>

<footer>
    <p>Reden-Portal Bundestag &copy; 2025</p>
</footer>

<!-- Load jQuery library so can use $.get() for search requests -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    /**
     * rednerData = Array of speaker/Redner objects
     * This data comes from FreeMarker (backend)
     * and becomes real JavaScript objects.
     */
    const rednerData = [
        <#list topRedner as redner>
        {
            id: '${redner.id}',
            vorname: '${redner.vorname}',
            nachname: '${redner.nachname}',
            fraktion: '${redner.fraktion.name!}',
            titel: '${redner.titel!}'
        }<#if redner_has_next>,</#if>
        </#list>
    ];

    /**
     * Renders the list of "Redner" (speakers) into HTML containers.
     * @param rednerArray - array of speaker objects
     */
    function renderRednerList(rednerArray) {
        let html = '';
        rednerArray.forEach(redner => {
            // If fraktion exists, show it. If not, show nothing.
            let fraktionHtml = '';
            if (redner.fraktion) {
                fraktionHtml = '<p>' + redner.fraktion + '</p>';
            }

            // If titel exists, show it. If not, show nothing.
            let titelHtml = '';
            if (redner.titel) {
                titelHtml = '<small>' + redner.titel + '</small>';
            }

            // Create the card HTML
            html += '<div class="redner-card">' +
                '<h3><a href="/redner/' + redner.id + '">' + redner.vorname + ' ' + redner.nachname + '</a></h3>' +
                fraktionHtml +
                titelHtml +
                '</div>';
        });
        // Insert the final HTML into page
        $('#rednerList').html(html);
    }

    /**
     * Sorts the list of speakers based on user selection.
     */
    function sortRedner() {
        const sortBy = $('#sortSelect').val();

        // Copy the array to avoid modifying original data
        const sortedRedner = [...rednerData].sort((a, b) => {
            const nameA = (a.vorname + ' ' + a.nachname).toLowerCase();
            const nameB = (b.vorname + ' ' + b.nachname).toLowerCase();
            const fraktionA = a.fraktion || '';
            const fraktionB = b.fraktion || '';

            // Sorting logic
            switch (sortBy) {
                case 'name_asc':
                    return nameA.localeCompare(nameB);
                case 'name_desc':
                    return nameB.localeCompare(nameA);
                case 'fraktion_asc':
                    return fraktionA.localeCompare(fraktionB) || nameA.localeCompare(nameB);
                case 'fraktion_desc':
                    return fraktionB.localeCompare(fraktionA) || nameA.localeCompare(nameB);
                default:
                    return 0;
            }
        });

        // Show sorted data
        renderRednerList(sortedRedner);
    }

    // When page loads, directly sort A-Z
    $(document).ready(function() {
        sortRedner();
    });

    /**
     * Performs AJAX search request to the backend.
     */
    function searchRedner() {
        const query = $('#searchInput').val();

        // Minimum 2 characters
        if (query.length < 2) {
            $('#searchResults').html('<p class="error">Bitte mindestens 2 Zeichen eingeben</p>');
            return;
        }

        // AJAX(Ansynchronous JavaScript And XML) GET request to API
        $('#searchResults').html('<p class="loading">Suche läuft...</p>');

        $.get('/api/redner/search/' + encodeURIComponent(query))
            .done(function(rednerList) {
                if (rednerList.length === 0) {
                    $('#searchResults').html('<p class="info">Keine Abgeordneten gefunden</p>');
                    return;
                }

                // Build search results HTML
                let html = '<h3>Suchergebnisse:</h3><div class="redner-list">';

                for (let i = 0; i < rednerList.length; i++) {
                    const redner = rednerList[i];
                    let fraktionHtml = '';
                    if (redner.fraktion) {
                        fraktionHtml = '<p>' + redner.fraktion.name + '</p>';
                    }

                    html += '<div class="redner-card">' +
                        '<h4><a href="/redner/' + redner.id + '">' + redner.vorname + ' ' + redner.nachname + '</a></h4>' +
                        fraktionHtml +
                        '</div>';
                }

                html += '</div>';
                $('#searchResults').html(html);
            })
            .fail(function() {
                $('#searchResults').html('<p class="error">Fehler bei der Suche</p>');
            });
    }

    // Trigger search when Enter key is pressed
    $('#searchInput').keypress(function(e) {
        if (e.which === 13) {
            searchRedner();
        }
    });
</script>
</body>
</html>