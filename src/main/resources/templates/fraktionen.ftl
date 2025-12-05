<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fraktionen - Reden-Portal</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
<header>
    <h1><a href="/" style="color: white; text-decoration: none;"> Fraktionen </a> </h1>
</header>

<h2>Fraktionen im Bundestag</h2>

<div class="fraktionen-list">
    <#list fraktionen as fraktion>
        <div class="fraktion-card">
            <h3>${fraktion.name}</h3>
            <p>Mitglieder: ${rednerProFraktion[fraktion.name]!0}</p>
        </div>
    </#list>
</div>

<footer>
    <p><a href="/">← Zurück zur Startseite</a></p>
</footer>
</body>
</html>