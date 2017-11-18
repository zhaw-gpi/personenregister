Björn Scheppler, 18.11.2017

# Read Me Personenregister
In diesem Projekt wird eine mögliche Lösung für einen Ausschnitt aus dem
Personenregister-System entwickelt, welche im Rahmen der Modulabschlussarbeit
für die eUmzug-Plattform benötigt wird.

# Status
Abgeschlossen, wartet auf Feedback von Dozierenden und Studierenden

# Eigentliche Dokumentation
## Prototypische Vereinfachungen
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