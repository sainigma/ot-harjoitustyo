# Testaus

Sovellusta testataan JUnit frameworkilla ja testit koostuu yksikkö-, integraatio- ja backendtesteistä. Lisäksi sovelluselle tehtiin rajallisia järjestelmätestejä.

Testit keskittyvät pääpelisilmukan logiikkaan sekä pistelistan backendtapahtumiin. Käyttöliittymää ja grafiikkaluokkia ei testata.

Testeillä saavutetaan 73% rivikattavuus sekä 41% haarautumakattavuus. Alhaisen haarautumakattavuuden syynä on, että suuri osa pelin logiikasta on riippuvainen käyttäjän syötteistä. Tätä paikataan integraatiotesteillä, jotka toteuttaa samat toiminnot kuten syötteistä riippuva logiikkakin.

Testikattavuutta voisi nostaa tekemällä end-to-end testejä, esim. luomalla mockup näppäinkuuntelijan, mutta sellaisen suunnittelu olisi vaatinut tarkkaa suunnitelmaa siitä miten pelin käyttöliittymä toimisi.

## Yksikkötestit
Sovelluksen yksikkötestit keskittyvät fysiikanratkojan ja GameObject implementaatio Mortarin testaamiseen. Testeissä tulee päällekkäisyyksiä integraatiotestien kanssa, mutta koska merkittävä osa logiikasta on riippuvainen luokista, on yksikkötestaus välttämätöntä.

Mortar -luokan testeissä testataan että tykki kontrolloituu oikein. Tykin liike on riippuvainen PID-säätimistä, jotka voivat ohjauskertoimista riippuen käyttäytyä kaoottisesti. Testit varmistaa että tykki saavuttaa normaalissa päivityssyklissä tavoitearvon, ja että PID-säädin sammuu normaalisti eikä jää fluktuoimaan/tärisemään.

Fysiikkaratkojan testeissä varmistetaan että ratkoja on äärellinen, ja että ratkojan implementaatio Ballistics käyttäytyy oikein. Oikeellisuuden testaamiseksi käytetään antogonisti-implementaatiota Parabola. Kumpikin implementaatio aikaansaa lentoradan, mutta parabolinen ratkoja luo lentoradan vakiokiihtyvyydellä, kun taas ballistisessa ratkojassa kiihtyvyys on dynaaminen.

## Integraatiotestit

Integraatiotestit kohdistuvat kokonaan pääpelisilmukan logiikan luokan BaseGame:n testaamiseen. Testit koskettavat epäsuorasti kaikkia game.logic.controllers paketin luokkia, GameObject- ja Mortarluokkia sekä animaattoreita.

Testeissä testataan, että kenttä ladataan oikein tiedostosta, se populoidaan oikeilla objekteilla ja että itse pääpelisilmukka toimii.

Pelisilmukkatesteissä näppäinsyötteitä simuloidaan yhdistymällä suoraan käyttöliittymän ohjaamiin metodeihin. Testeissä testataan tykin lataamista, tykillä ampumista, sekä projektiilin loppuhetken tapahtumia, eli mereen tai maaliin osumista, maalin uppoamista.

Integraatiotesteillä saavutetaan hyvä rivi- ja haarautumakattavuus peliobjekti- ja animaatioluokissa, sekä alilogiikoista koostuvassa paketissa game.components.controllers, poislukien EndLogic ja ReloadLogic luokat, jotka koostuvat enimmäkseen käyttöliittymäkoodista.

## Backendtestit

Koska peli tallentaa pisteet myös backend-serverille, osa testeistä keskittyy tarkistamaan että tiedon siirtäminen toimii ja että sekä client että backend selviytyy virheellisistä pyynnöistä ja timeouteista.

ScoreManager -luokkaa koskevat testit ovat integraatiotyyppisiä, eli ne testaavat pistetietojen lähetystä ja tallennusta kokonaisuudessaan.

Services -luokkaa koskevat testit taas ovat yksikkötyyppisiä. Koska ScoreManager itsessään testaa Services -luokkaa oikeellisen datan puolesta, ServicesTest keskittyy virheellisen datan käsittelyyn. Testeissä lähetetään väärällä avaimella enkryptattua dataa, sekä vääränmuotoista dataa.

Huomioitavaa backendtesteissä on, että ne vaativat yhteyden backendiin. Tämä otetaan testeissä huomioon, mutta backendin puuttuessa testit testaavat että timeoutin tunnistus toimii oikein.

## Järjestelmätestaus

Sovelluksen järjestelmätestaus suoritettiin manuaalisesti 64-bittisessä Linux koneessa jossa on integroitu näytönohjain, sekä 64-bittisessä Windows koneessa jossa on erillinen näytönohjain. Merkittäviä eroja ei tullut vastaan, ainoa huomattava ero oli että erillisen näytönohjaimen koneessa läpinäkyvät tekstuurit renderöivät kauniimmin.

 Kummassakin koneessa näytön päivitystaajuus oli 60Hz. Sovellus ottaa poikkeavat ruudunpäivitysnopeudet huomioon käyttämällä ruudunpäivitykseen kulunutta aikaa ajastimena, mutta sen toimivuus on käytännössä testaamaton. Suuremmilla päivitysnopeuksilla saattaa ilmetä performanssiongelmia esim. fysiikkaratkojan tai renderöijän performanssin kanssa (sovellus käyttää piirtämiseen välitöntä piirtomodea, mikä tekee prosessorista pullonkaulan näytönohjaimelle).