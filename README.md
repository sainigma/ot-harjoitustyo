# Tykkipeli - Ohjelmistotekniikan harjoitustyö

Perinteinen tykkipeli jossa pelaaja yrittää osua maaliin tykin kulmaa ja ammuksen nopeutta muuttamalla.

Sovelluksesta on tällä hetkellä valmiina peliobjekti- näkymä- ja renderöintilogiikka, sekä yksinkertainen fysiikkasolveri. Testit kattavat tällä hetkellä fysikkasolverin ja tilanteen jossa lentorataan ei vaikuta ilmanvastus.

## Dokumentaatio

[Vaatimuusmäärittely](/dokumentaatio/vaatimusmaarittely.md)

[Tuntikirjanpito](/dokumentaatio/tuntikirjanpito.md)

[Arkkitehtuurikuvaus](/dokumentaatio/arkkitehtuuri.md)

[Käyttöohje](/dokumentaatio/kayttoohje.md)

# Komentorivitoiminnot

## Asennus & ajo

    git clone https://github.com/sainigma/ot-harjoitustyo.git
    cd ot-harjoitustyo/Tykkipeli
    mvn compile exec:java -Dexec.mainClass=game.Main

## Ajo

Tykkipeli kansion juuressa:

    mvn -q exec:java -Dexec.mainClass=game.Main

## Testit

Tykkipeli kansion juuressa:

    mvn test
    mvn test jacoco:report
    mvn jxr:jxr checkstyle:checkstyle

Generoitua kattavuusraporttia voi tarkastella polussa Tykkipeli/target/site/jacoco/index.html