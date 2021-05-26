# Vertaisarvionti

## Kenelle ja kuka

Arvioija: Jennina Färm
Arvioitava: Tommi Häkkinen

## Projektin testaus oikeassa laitteessa

Sovellus tuntuu toimivan hyvin. Se on selkeä ja kontrastit ovat selkeät toiminnallisuuksien puolesta.

Teema on yhtäläinen ja toimiva. Napit erottuvat selkeästi otsikosta ja fontit ovat selkeät ja tarvittavan kokoiset.

Näkymästä siirtyminen toiseen on moitteetonta. Kartan tietojen haku toki kestää hiukan. Ahvenanmaan tiedot tulevat näkyviin ainakin minun laitteellani.

## Tekniset ominaisuudet

Selkeä RestFull Apin käyttö erillisissä threadeissä. Fetchiin oli käytetty Java.netin HttpURLConnectioniä ja parsimiseen org.json kirjastoa, jotka olivat varsin riittäviä ja toimivia tähän projektiin. Lokaatio api toimii hyvin, on koodillisesti selkeää ja käyttäjältä pyydetään oikeudet lokaation käyttöön.

Koodi on helposti luettavaa ja funktioiden sekä muuttujien nimeäminen on selkeää ja helposti ymmärrettävää. Lisäksi suurimpia funktioita ja toimintoja on tuettu kommenteilla, joka nopeuttaa koodin läpikäyntiä ja ymmärrettävyyttä.

Asiat on jaettu neljään eri luokkaan: kahteen aktiviteettiin ja kahteen apu luokkaan. Jos olisi halunnut hifistellä Main aktiviteetin fetchit olisivat voineet olla omalla luokassaan, mutta tämän laajuisessa koodissa se ei tuota turhan paljon lisäarvoa. Main activityssä on myös hienosti käytetty sisäluokkaa (ymmärtääkseni oikein) LocationListeneriä.

Readme on selkeä ja antaa kokonaiskäsityksen sovelluksesta, sen toiminnasta ja mahdollisista bugeista.

## Numero arviointi

Projekti sisältää RESTful APIn ja laitteisto APIn käytön (lookaatio), se on julkaisussa Google Consolessa, UX on selkeä, yksinkertainen ja soljuva, toki hieman hitaahko (mutta en oleta meidän tekevän siihen suhteen paljon asioita) sekä koodi on selkeää ja helposti luettavaa, josta olisi helppo jatkaa jatkokehityksessä.

Arvosana on mielestäni selkeä 4 ja menisi jopa 5 puolelle.
