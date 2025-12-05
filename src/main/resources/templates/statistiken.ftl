<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Statistiken - Reden-Portal</title>
    <link rel="stylesheet" href="/style.css">
    <style>
        .simple-stats {
            margin: 20px 0;
        }

        .stat-row {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 20px;
        }

        .stat-item {
            background: white;
            padding: 15px;
            border: 1px solid whitesmoke;
            border-radius: 5px;
            flex: 1;
            min-width: 150px;
            text-align: center;
        }

        .stat-big {
            font-size: 24px;
            font-weight: bold;
            color: cornflowerblue;
            margin-bottom: 5px;
        }

        .stat-small {
            color: gray;
            font-size: 14px;
        }

        .simple-list {
            background: white;
            padding: 15px;
            border: 1px solid mediumblue;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .list-item {
            padding: 8px 0;
            border-bottom: 1px solid mediumblue;
            display: flex;
            justify-content: space-between;
        }

        .list-item:last-child {
            border-bottom: none;
        }

        .fraktion-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .fraktion-table td {
            padding: 8px;
            border-bottom: 1px solid mediumblue;
        }

        .fraktion-table tr:last-child td {
            border-bottom: none;
        }

        .fraktion-name {
            text-align: left;
        }

        .fraktion-count {
            text-align: right;
            font-weight: bold;
            color: cornflowerblue;
        }
    </style>
</head>
<body>
<header>
    <h1><a href="/" style="color: white; text-decoration: none;">Reden Portal Bundestag</a></h1>
    <p>Herzlichen Willkommen im Bundestagportal für detaillierte alle Abgeordnete, Rede, Reden, und Sitzung</p>

    <!-- NAVIGATION BAR -->
    <nav class="main-nav">
        <a href="/fraktionen"> Fraktionen </a>
        <a href="/sitzungen"> Sitzungen </a>
        <a href="/statistiken"> Statistiken </a>
    </nav>
</header>

<div class="container">
    <h2>Statistik Übersicht</h2>

    <div class="simple-stats">
        <div class="stat-row">
            <div class="stat-item">
                <div class="stat-big" id="gesamtReden">-</div>
                <div class="stat-small">Reden insgesamt</div>
            </div>
            <div class="stat-item">
                <div class="stat-big" id="gesamtRedner">-</div>
                <div class="stat-small">Abgeordnete</div>
            </div>
            <div class="stat-item">
                <div class="stat-big" id="gesamtSitzungen">-</div>
                <div class="stat-small">Sitzungen</div>
            </div>
            <div class="stat-item">
                <div class="stat-big" id="gesamtKommentare">-</div>
                <div class="stat-small">Kommentare</div>
            </div>
        </div>

        <div class="stat-row">
            <div class="stat-item">
                <div class="stat-big" id="avgKommentare">-</div>
                <div class="stat-small">Kommentare pro Rede</div>
            </div>
            <div class="stat-item">
                <div class="stat-big" id="avgRedenLaenge">-</div>
                <div class="stat-small">Zeichen pro Rede</div>
            </div>
        </div>
    </div>

    <div class="simple-list">
        <h3>Top 5 Redner (meiste Reden)</h3>
        <div id="topRednerList">
            <div class="loading">Lade Daten...</div>
        </div>
    </div>

    <div style="display: flex; gap: 20px; margin-bottom: 20px;">
        <div class="simple-list" style="flex: 1;">
            <h3>Reden nach Fraktionen</h3>
            <table class="fraktion-table" id="redenProFraktion">
                <tr><td colspan="2" class="loading">Lade Daten...</td></tr>
            </table>
        </div>

        <div class="simple-list" style="flex: 1;">
            <h3>Abgeordnete nach Fraktionen</h3>
            <table class="fraktion-table" id="rednerProFraktion">
                <tr><td colspan="2" class="loading">Lade Daten...</td></tr>
            </table>
        </div>
    </div>
</div>

<footer>
    <p><a href="/">← Zurück zur Startseite</a></p>
</footer>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        loadStatistiken();
    });

    function loadStatistiken() {
        fetch('/api/statistiken')
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Fehler: ' + response.status);
                }
                return response.json();
            })
            .then(function(stats) {
                // Basis Statistiken
                document.getElementById('gesamtReden').textContent = stats.gesamtReden.toLocaleString('de-DE');
                document.getElementById('gesamtRedner').textContent = stats.gesamtRedner.toLocaleString('de-DE');
                document.getElementById('gesamtSitzungen').textContent = stats.gesamtSitzungen.toLocaleString('de-DE');
                document.getElementById('gesamtKommentare').textContent = stats.gesamtKommentare.toLocaleString('de-DE');
                document.getElementById('avgKommentare').textContent = stats.durchschnittKommentareProRede;
                document.getElementById('avgRedenLaenge').textContent = stats.durchschnittRedenLaenge.toLocaleString('de-DE');

                // Top Redner
                var topRednerHtml = '';
                if (stats.topRedner && stats.topRedner.length > 0) {
                    stats.topRedner.forEach(function(redner, index) {
                        topRednerHtml += '<div class="list-item">' +
                            '<div>' + (index + 1) + '. ' + redner.name + '<br><small>' + redner.fraktion + '</small></div>' +
                            '<div>' + redner.anzahlReden + ' Reden</div>' +
                            '</div>';
                    });
                } else {
                    topRednerHtml = '<div class="error">Keine Daten verfügbar</div>';
                }
                document.getElementById('topRednerList').innerHTML = topRednerHtml;

                // Reden pro Fraktion
                var redenProFraktionHtml = '';
                if (stats.redenProFraktion && Object.keys(stats.redenProFraktion).length > 0) {
                    Object.entries(stats.redenProFraktion).forEach(function(entry) {
                        redenProFraktionHtml += '<tr>' +
                            '<td class="fraktion-name">' + entry[0] + '</td>' +
                            '<td class="fraktion-count">' + entry[1] + '</td>' +
                            '</tr>';
                    });
                } else {
                    redenProFraktionHtml = '<tr><td colspan="2" class="error">Keine Daten</td></tr>';
                }
                document.getElementById('redenProFraktion').innerHTML = redenProFraktionHtml;

                // Redner pro Fraktion
                var rednerProFraktionHtml = '';
                if (stats.rednerProFraktion && Object.keys(stats.rednerProFraktion).length > 0) {
                    Object.entries(stats.rednerProFraktion).forEach(function(entry) {
                        rednerProFraktionHtml += '<tr>' +
                            '<td class="fraktion-name">' + entry[0] + '</td>' +
                            '<td class="fraktion-count">' + entry[1] + '</td>' +
                            '</tr>';
                    });
                } else {
                    rednerProFraktionHtml = '<tr><td colspan="2" class="error">Keine Daten</td></tr>';
                }
                document.getElementById('rednerProFraktion').innerHTML = rednerProFraktionHtml;
            })
            .catch(function(error) {
                var loadingElements = document.querySelectorAll('.loading, .error');
                loadingElements.forEach(function(el) {
                    el.innerHTML = 'Fehler beim Laden: ' + error.message;
                });
            });
    }
</script>
</body>
</html>