# Tykkipeli - Ohjelmistotekniikan harjoitustyö

Perinteinen tykkipeli jossa pelaaja yrittää osua maaliin tykin kulmaa ja ammuksen nopeutta muuttamalla.

Peli on jo jossain määrin toimivassa muodossa, pelimoottori on enimmäkseen valmis ja pelilogiikka kattaa tykin peruskontrolloinnin. Varsinainen pääpelisilmukka on kuitenkin vielä toteuttamatta, ja joitain kriittisiä näkymiä vielä puuttuu.

## Dokumentaatio

[Vaatimuusmäärittely](/dokumentaatio/vaatimusmaarittely.md)

[Tuntikirjanpito](/dokumentaatio/tuntikirjanpito.md)

[Arkkitehtuurikuvaus](/dokumentaatio/arkkitehtuuri.md)

[Käyttöohje](/dokumentaatio/kayttoohje.md)

# Releaset

[Viikko 5](https://github.com/sainigma/ot-harjoitustyo/releases/tag/0.6)

# Komentorivitoiminnot

## Kääntö & ajo

    git clone https://github.com/sainigma/ot-harjoitustyo.git
    cd ot-harjoitustyo/Tykkipeli
    mvn compile exec:java -Dexec.mainClass=game.Main

## Kääntö

Tykkipeli kansion juuressa:

    mvn -q exec:java -Dexec.mainClass=game.Main

## Testit

Tykkipeli kansion juuressa:

    mvn test
    mvn test jacoco:report
    mvn jxr:jxr checkstyle:checkstyle

Generoitua kattavuusraporttia voi tarkastella polussa Tykkipeli/target/site/jacoco/index.html