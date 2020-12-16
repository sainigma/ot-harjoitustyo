# Vaatimusmäärittely

## Sovelluksen tarkoitus

Perinteinen tykkipeli jossa pelaaja yrittää osua maaliin tykin kulmaa ja ammuksen nopeutta muuttamalla.

Pelin pääpelisilmukka on, että pelaaja yrittää osua maaliin erilaisia parametrejä muuttamalla. Pelin haastavuutta voidaan varioida sekä simulaation parametrejä, esim. tuulivektoria, muuttamalla, että tekemällä maaleista liikkuvia. Pelin voittoehtona on maaliin osuminen, häviöehtoina taas ammusten loppuminen tai maalin pääseminen liian lähelle.

## Projektin eteneminen

### Näkymät/grafiikka

MVP merkinnät omaan käyttöön, merkitsee minimum viable product -ominaisuuksia

- [x] Erillisiä näkymiä voi luoda
- [x] Näkymät voidaan piilottaa, minimoida ja deaktivoida (+käänteiset toiminnot), status propagoituu lapsiobjekteihin
- [x] Peliobjektien ryhmityksen/periytyvyyden toteutus, piirtologiikka ja piirtojärjestys
- [x] Tykin/mörssärin kontrollit, tavoitekulman ja -suunnan voi asettaa ja PID-säädin animoi tykin kulmiin
- [x] Karttanäkymä, tykin kulma ja elävien projektiilien sijainnit piirretään reaaliajassa
- [x] Peliobjektien animointi animaatiotiedostoilla
- [x] Projektiilin lentoradan piirto
- [x] Edellisten projektiilien lentoradan piirto
- [x] Näkymä tykin ammusten valintaan (MVP)
- [x] Laivojen tulitusetäisyyden piirto
- [x] Fullscreen/borderless fullscreen/windowed, tuki resoluution vaihdolle
- [x] Päämenu (MVP)
- [x] Kirjainten piirto
- [x] Näkymä kentän lopettamiseen (MVP)
- [x] Tuulen suunnan ilmaisu
- [x] Hiscorenäkymä (MVP)
- [ ] Lopetusnäkymän tahditus/silmäkarkki
- [ ] Latausanimaatio tykille (MVP)
- [ ] Tykin jalustan lähimaasto taustaa nopeammin liikkuvalle layerille, parallax
- [ ] Aktiivisen projektiilin alkuparametrien piirto karttanäkymään

### Fysiikkasolveri

- [x] Solveri toimii staattisella kiihtyvyydellä
- [x] Simulaatio toimii 3d-avaruudessa
- [x] Solveri toimii dynaamisella kiihtyvyydellä
- [x] Ilmanvastus, gravitaation muutos, ilman tiheyden muutos, coriolis vaikuttaa lentorataan
- [x] Tuuli otetaan huomioon simulaatiossa

## Pelilogiikka

- [x] Käyttäjän näppäinsyötteiden kuuntelu
- [x] Käyttäjä voi ladata tykin erilaisilla ammuksen ja panostusten yhdistelmillä
- [x] Ammusvarasto ammuksille ja panoksille
- [x] Lataamisella ja ampumisella oikea sekvenssi
- [x] Käyttäjä voi laukaista tykin
- [x] Käyttäjä voi vaihtaa näkymää
- [x] Projektiilin elinkaari (spawnaus ja poisto)
- [x] Solverin elinkaari (spawnaus, deaktivointi, arkistointi)
- [x] Kenttien spawnaus parametrisesti
- [x] Maaliobjektit
- [x] Projektiilin osumien jälkikäsittely (osuiko maaliin)
- [x] Pisteidenlasku (MVP)
- [x] Pisteiden tallentaminen verkkoon
- [x] Pelin päättyminen (MVP)
- [x] Pisteiden tallentaminen lokaalisti (MVP)
- [ ] Pisteiden järjestely
- [ ] Seuraavaan waypointtiin siirtyminen maalikappaleelle
- [ ] Näppäinasetusten lataaminen tiedostosta
- [ ] Häviöstate (maali pääsee karkuun/liian lähelle)
- [ ] Hiiritapahtumien seuraaminen, klikattavat objektit

## Pelisilmukka
![](./assets/gameplayloop.png)

## Pelin näkymät
![](./assets/paanakyma.png)
Isot objektien ryhmittymät ovat omissa renderbuffereissaan. Mahdollisesti 3 bufferia (ui, peliobjektit, tausta)

![](./assets/karttanakyma.png)
Lentorata päivittyy karttanäkymään reaaliajassa. Pitkästä lentoajasta johtuen näkymän pitää osata piirtää useita projektiileja samanaikaisesti. Edellisiä lentoratoja pitää pystyä myös piirtämään uudelleen näkymään (esim. jos käyttäjä haluaa tarkastella projektiilihistoriaa) Jokainen projektiili voidaan esim. tallentaa omiin buffereihin joita pidetään koko pelin ajan muistissa.

Karttanäkymän täytyy myös pystyä piirtämään maali (esim. laiva) järkevästi (vähintään pisteenä/ikonina, mieluiten spritenä jota pystyy pyörittämään)

## Rajoitukset
- Peli pyörii Cubbli-linuxjakelulla ja käyttää kiinteää resoluutiota (joko 800x600 retrosyistä tai suoraan 720p reso&kuvasuhde)

## Perustoiminnallisuudet

- Käyttäjän kontrollit:
    - Projektiilin määritys
        - Massa, lähtönopeus, ajastus, vaikutusalueen koko
    - Lentoradan määritys tykin kulmaa ja suuntaa muuttamalla

- Käyttöliittymän näkymät
    - Näkymä alkuparametrien asettamiseen
        - Alinäkymä projektiilin määrittämiseen
    - Näkymä projektiilin lentoradan seuraamiseen
        - Piirtää lentoradan kartalle reaaliajassa ylhäältäpäin
        - Jatkaa piirtämistä myös kun näkymä ei ole aktiivinen

- Fysiikkasolveri
    - Abstrakti luokka, voidaan eriyttää erikoistapauksien laskemiseen
    - Ratkaisee ongelmaa aikaikkunassa, projektiilin sijainti voidaan laskea suoraan seuraavaan ruudunpiirtoon
    - Pysäyttää simulaation kun lopetusehdot täyttyy (aika ja/tai sijainti)

- Projektiilien lentorata
    - Kolmiulotteinen karteesinen koordinaatisto
    - Projektiilin massa ja nopeus vaikuttaa lentorataan ilmanvastuksen kautta
    - Ei ota huomioon maapallon pyörimistä tai kaarevuutta
    - Tuuli "työntää" projektiilia

- Paikallinen high-score lista

## Erikoistoiminnallisuudet (nice-to-have/jatkokehitys)


- Projektiilien lentorata
    - Maapallon kaarevuus ja pyöriminen vaikuttaa lentorataan
    - Gravitaation ja ilmantiheyden vaihtelu vaikuttaa lentorataan
    - Eötvösilmiö vaikuttaa lentorataan
    - Projektiileilla on lähtötilanteessa pyörimismäärää ja magnusilmiö vaikuttaa lentorataan

- Globaali high-score lista (sinällään triviaali toteuttaa, paitsi jos halutaan jonkinlainen huijauksenesto)