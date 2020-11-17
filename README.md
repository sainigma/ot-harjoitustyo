# Tykkipeli - Ohjelmistotekniikan harjoitustyö

Perinteinen tykkipeli jossa pelaaja yrittää osua maaliin tykin kulmaa ja ammuksen nopeutta muuttamalla.

Sovelluksesta on tällä hetkellä valmiina peliobjekti- näkymä- ja renderöintilogiikka, sekä yksinkertainen fysiikkasolveri. Testit kattavat tällä hetkellä fysikkasolverin ja tilanteen jossa lentorataan ei vaikuta ilmanvastus.

## Dokumentaatio

[Vaatimuusmäärittely](/dokumentaatio/vaatimusmaarittely.md)

[Tuntikirjanpito](/dokumentaatio/tuntikirjanpito.md)

# Komentorivitoiminnot

## Asennus

    git clone https://github.com/sainigma/ot-harjoitustyo.git
    cd ot-harjoitustyo/Tykkipeli
    mvn compile exec:java -Dexec.mainClass=Main

## Ajo

Tykkipeli kansion juuressa:

    mvn -q exec:java -Dexec.mainClass=Main

## Testit

Tykkipeli kansion juuressa:

    mvn test
    mvn test jacoco:report

Generoitua kattavuusraporttia voi tarkastella polussa Tykkipeli/target/site/jacoco/index.html