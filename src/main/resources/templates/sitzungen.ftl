<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sitzungen - Reden-Portal</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
<header>
    <h1><a href="/" style="color: white; text-decoration: none;"> Sitzungen</a> </h1>
    <p><a href="/">← Zurück zur Startseite</a></p>
</header>

<h2>Sitzungen des Bundestages</h2>

<div class="sitzungen-list">
    <#list sitzungen as sitzung>
        <div class="sitzung-card">
            <h3>WP ${sitzung.wahlperiode}, Sitzung ${sitzung.sitzungNr}</h3>
            <p class="metadata">
                <#if sitzung.sitzungDatum??>
                    Datum: ${sitzung.sitzungDatum?string("dd.MM.yyyy")}
                </#if>
                <#if sitzung.ort??>
                    <br>Ort: ${sitzung.ort}
                </#if>
            </p>
        </div>
    </#list>
</div>

<footer>
    <p><a href="/">← Zurück zur Startseite</a></p>
</footer>
</body>
</html>