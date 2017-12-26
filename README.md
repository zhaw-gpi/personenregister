Björn Scheppler, 26.12.2017

In diesem Projekt wird eine mögliche Lösung für einen Ausschnitt aus dem
Personenregister-System entwickelt, welche im Rahmen der Modulabschlussarbeit
für die eUmzug-Plattform benötigt wird.

# Informationen für das Testen
## Tests mit soapUI
1. Clean & Build: Dabei werden im Target nebst den kompilierten Java-Klassen auch
die eCH-Java-Klassen generiert vom JAXB2-Maven-Plugin.
2. Run, damit der SOAP WebService läuft und über die URL http://localhost:8083/soap
verfügbar ist
3. Nun entweder in soapUI händisch neue SOAP-Requests generieren mit der WSDL
http://localhost:8083/soap/PersonenRegisterService?wsdl. Welche Personen dabei
gefunden werden können, kann über die H2-Konsole geprüft werden in den Tabellen
RESIDENT und RESIDENT_RELATION. Hierzu auf http://localhost:8083/console anmelden 
mit Driver Class = org.h2.Driver, JDBC URL = jdbc:h2:./personenregister, User Name 
= sa, Password = Leer lassen
4. Oder die vorgefertigten Requests/TestSuite nutzen im soapUI-Projekt \src\test\resources\
Personenregister-Testdaten-soapui-project.xml

## Tests aus der Umzugsplattform heraus
Hierzu den Anweisungen folgen in https://github.com/zhaw-gpi/eumzug_musterloesung

# TODO
1. PersonenRegisterController:135:localPersonId sollte frisch pro Person generiert
werden und nicht von Hauptperson übernommen werden

# Prototypische Vereinfachungen
1. In personMoveRequest werden die municipalityId und municipalityName 
vermutlich deshalb mitgeliefert, weil eine Person in mehreren Gemeinden registriert
sein kann (Hauptwohnsitz und Wochenaufenthalter). Das heisst, moveAllowed müsste
eigentlich bei der Beziehungstabelle sein zwischen Municipality und Resident. Dies
mit JPA umzusetzen, ist gar nicht so einfach, u.a. braucht es dann eine eigene
Entität für diese Beziehungstabelle mit einem Pseudo-Primärschlüssel:
https://www.thoughts-on-java.org/many-relationships-additional-properties/. Daher
lassen wir dies weg.
2. Schema Validation: Mit @SchemaValidation bei der Webservice-Endpoint-Klasse
werden lediglich Standard-Fehlermeldungen ausgegeben, nicht aber benutzerdefinierte.
Das ist ok bei einem Prototyp. Störender ist, dass XJC keine Annotations für die
XSD-Restrictions hinzufügt (also, dass z.B. ein String nicht leer oder länger als
40 Zeichen sein darf). Es gibt teilweise Plugins, aber die sind seit mehreren
Jahren nicht mehr aktualisiert => man müsste entweder ein eigenes Plugin schreiben
oder aber die Annotations in den generierten Java-Klassen von Hand hinzufügen, aber
dann wäre die Generierung im Build-Prozess natürlich nicht mehr opportun.

# Mitwirkende
1. Björn Scheppler: Hauptarbeit
2. Peter Heinrich: Der stille Support im Hintergrund mit vielen Tipps sowie zuständig
für den Haupt-Stack mit SpringBoot & Co.
3. Gruppe TZb02 (Bekim Kadrija, Jovica Rajic, Luca Belmonte, Simon Bärtschi, Sven 
Baumann): Mitzuziehende Personen auswählen