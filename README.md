# Tykkipeli - Ohjelmistotekniikan harjoitustyö

Perinteinen tykkipeli jossa pelaaja yrittää osua maaliin tykin kulmaa ja ammuksen nopeutta muuttamalla.

Peli on jo lähes valmis, pelimoottori on valmis ja pelilogiikka kattaa pääpelisilmukan. Jäljelläoleva työ liittyy pelin aloitukseen, lopetukseen sekä tykin lataamisen näkymään.

## Dokumentaatio

[Vaatimuusmäärittely](/dokumentaatio/vaatimusmaarittely.md)

[Tuntikirjanpito](/dokumentaatio/tuntikirjanpito.md)

[Arkkitehtuurikuvaus](/dokumentaatio/arkkitehtuuri.md)

[Käyttöohje](/dokumentaatio/kayttoohje.md)

# Releaset

[Viikko 6](https://github.com/sainigma/ot-harjoitustyo/releases/tag/0.8)
[Viikko 5](https://github.com/sainigma/ot-harjoitustyo/releases/tag/0.6)

# Komentorivitoiminnot

## Kääntö & ajo

    git clone https://github.com/sainigma/ot-harjoitustyo.git
    cd ot-harjoitustyo/Tykkipeli
    mvn compile exec:java -Dexec.mainClass=game.Main

## Pakkaus .jariksi

Tykkipeli kansion juuressa:

    mvn package

Paketti luodaan target -kansioon, ja sen voi ajaa komennolla

    java -jar Tykkipeli-1.0-SNAPSHOT.jar

## Testit & dokumentaatio

Tykkipeli kansion juuressa:

    mvn test
    mvn test jacoco:report
    mvn jxr:jxr checkstyle:checkstyle
    mvn javadoc:javadoc

Generoitua kattavuusraporttia voi tarkastella polussa Tykkipeli/target/site/jacoco/index.html

Generoitua javadocdokumentaatiota voi tarkastella polussa Tykkipeli/target/site/apidocs/index.html