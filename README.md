# Henkilotietomuutospalvelu

Henkilotietomuutospalvelu (HTMP) on ajastettu palvelu, joka:

 1. Hakee oppijanumerorekisteristä viimeisimmän VTJ-päivityksen ajankohdan mukaan järjestetyn listan käyttäjäidentiteettejä (https://virkailija.opintopolku.fi/oppijanumerorekisteri-service/swagger-ui.html#!/Service_To_Service/hetusAndOidsOrderedByLastVtjSyncTimestampUsingGET)
 1. Käynnistää VTJ-päivityksen jokaiselle identiteetille oppijanumerorekisterissä (https://virkailija.opintopolku.fi/oppijanumerorekisteri-service/swagger-ui.html#!/Henkilot/paivitaYksilointitiedotUsingPUT)
 
Jatkossa HTMP on tarkoitus muuttaa käyttämään VRK:n muutostietopalvelua. Ks. suunnitelmat: https://jira.oph.ware.fi/jira/browse/KJHH-845. Placeholder-endpoint: _/startLocalSyncWithVtjMuutostietopalvelu_.

## Teknologia

HTMP on Spring Boot -sovellus. 

## Käynnistäminen kehitysmoodissa

 * Hae _henkilotietomuutospalvelu.yml_ luokalta
 * Aja `mvn clean install && java -jar -Dspring.config.location=henkilotietomuutospalvelu.yml -Dspring.profiles.active=dev -Dschedule=false target/henkilotietomuutospalvelu-0.0.1-SNAPSHOT.jar`
   * Muuta `Dschedule` -parametri arvoon _true_ (tai poista se ) mikäli haluat ajastuksen käyntiin. _false_ -arvolla voi käynnistää yhden päivitysajon kerrallaan esim. Swaggerin kautta (_/swagger-ui.html#!/vtj-sync-controller/startRemoteSyncWithVtjRajapintaUsingGET_)
