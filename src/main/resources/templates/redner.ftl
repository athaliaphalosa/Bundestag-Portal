<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${redner.vorname} ${redner.nachname} - Reden-Portal</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
<header>
    <h1><a href="/" style="color: white; text-decoration: none;">Reden-Portal</a> / ${redner.vorname} ${redner.nachname}</h1>
    <p><a href="/">Zurück zur Startseite</a></p>
</header>

<div class="redner-header">
    <h2>${redner.vorname} ${redner.nachname}</h2>
    <#if redner.titel??>
        <p><strong>${redner.titel}</strong></p>
    </#if>
    <#if redner.fraktion??>
        <p>Fraktion: <strong>${redner.fraktion.name}</strong></p>
    </#if>
    <div class="redner-stats">
        <span class="stat-badge">${reden?size} Reden</span>
        <span class="stat-badge">${gesamtKommentare} Kommentare</span>
    </div>
</div>

<div class="reden-list">
    <#list reden as rede>
        <div class="rede-item">
            <h3><a href="/rede/${rede.rid}">Rede
                    <#if rede.sitzung?? && rede.sitzung.sitzungDatum??>
                        vom ${rede.sitzung.sitzungDatum?string("dd.MM.yyyy")}
                    </#if>
                </a></h3>
            <p>
                <#if rede.sitzung??>
                    WP ${rede.sitzung.wahlperiode}, Sitzung ${rede.sitzung.sitzungNr}
                </#if>
                <span class="stat-badge">${rede.text?length} Zeichen</span>
                <span class="stat-badge">${rede.kommentar?size} Kommentare</span>
            </p>
            <p class="rede-preview">
                <#if rede.text?length gt 200>
                    ${rede.text[0..200]}...
                <#else>
                    ${rede.text}
                </#if>
            </p>
        </div>
    </#list>
</div>

<footer>
    <p><a href="/">Zurück zur Startseite</a></p>
</footer>

</body>
</html>