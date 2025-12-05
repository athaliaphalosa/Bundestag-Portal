<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rede - Reden-Portal</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
<header>
    <h1><a href="/" style="color: white; text-decoration: none;"> Rede </a> </h1>
    <a href="/">← Startseite</a>
    <#if rede.redner??>
        | <a href="/redner/${rede.redner.id}">← Zurück zu ${rede.redner.vorname} ${rede.redner.nachname}</a>
    </#if>
</header>

<div class="rede-header">
    <h2>Rede im Deutschen Bundestag</h2>
    <#if rede.redner??>
        <h3>von ${rede.redner.vorname} ${rede.redner.nachname}</h3>
        <#if rede.redner.fraktion??>
            <p class="metadata">Fraktion: ${rede.redner.fraktion.name}</p>
        </#if>
    </#if>

    <#if rede.sitzung??>
        <p class="metadata">
            Wahlperiode ${rede.sitzung.wahlperiode}, Sitzung ${rede.sitzung.sitzungNr}
            <#if rede.sitzung.sitzungDatum??>
                | ${rede.sitzung.sitzungDatum?string("dd.MM.yyyy")}
            </#if>
        </p>
    </#if>

    <p class="metadata">
        ${rede.text?length} Zeichen | ${kommentare?size} Kommentare
    </p>
</div>

<div class="rede-text">
    <h3>Wortlaut der Rede:</h3>
    <div class="text-content">${rede.text}</div>
</div>

<#if kommentare?size gt 0>
    <div class="kommentare-section">
        <h3>Kommentare (${kommentare?size})</h3>
        <#list kommentare as kommentar>
            <div class="kommentar">
                <p>${kommentar.textk}</p>
            </div>
        </#list>
    </div>
</#if>

<footer>
    <p>
        <a href="/">← Startseite</a>
        <#if rede.redner??>
            | <a href="/redner/${rede.redner.id}">← Zurück zu ${rede.redner.vorname} ${rede.redner.nachname}</a>
        </#if>
    </p>
</footer>
</body>
</html>