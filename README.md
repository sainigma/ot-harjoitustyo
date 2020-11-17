# Ohjelmistotekniikan harjoitustyö



[Vaatimuusmäärittely](/dokumentaatio/vaatimusmaarittely.md)

[Tuntikirjanpito](/dokumentaatio/tuntikirjanpito.md)

## Asennus

    git clone https://github.com/sainigma/ot-harjoitustyo.git
    cd ot-harjoitustyo/Tykkipeli
    mvn compile exec:java -Dexec.mainClass=Main

## Ajo

Tykkipelit kansion juuressa:

    mvn -q exec:java -Dexec.mainClass=Main

## Testit

Tykkipelit kansion juuressa:

    mvn test
    mvn test jacoco:report
