# Praxismanagement-App

![App Screenshot](./screenshots/screenshot1.png)  
*(Hier kannst du Screenshots deiner App einfügen)*

---

## Über das Projekt

Dies ist meine **Praxismanagement-App**, die ich in meiner Freizeit entwickelt habe.  
Ich bin Junior Java-Entwickler und habe dieses Projekt genutzt, um meine Fähigkeiten in der Backend-Entwicklung und der Arbeit mit Datenbanken zu verbessern.

Die App befindet sich noch in der Entwicklung und wird ständig erweitert und verbessert. Es gibt also noch Bereiche, die optimiert werden können.

Ich freue mich über **Feedback, Verbesserungsvorschläge** oder Ideen von euch, damit die App weiter wachsen kann.

---

## Funktionen

### 1. Login
- Der Benutzer kann sich als **Arzt** oder **Personal** anmelden.
- Nach erfolgreichem Login wird das **Dashboard** geöffnet.

### 2. Dashboard
- **Headerbereich:**
   - Zeigt an, wer sich angemeldet hat.
   - Persönliche Infos: Name, Personalnummer, heutiges Datum, Uhrzeit, nächster Feiertag.
   - Eine kleine ToDo-Liste.
- **Mitte des Dashboards:**
   - Möglichkeit, Patienten zu suchen, die heute einen Termin haben. Suche erfolgt nach **Name** und **Geburtsdatum**.
   - Darunter befindet sich die **Liste aller heutigen Termine** mit Uhrzeit, behandelndem Arzt und Beschwerden.
   - Mitarbeiter können auf den Button **„Nächster Termin“** klicken und den **Status** des Patienten auswählen (z. B. verspätet, Termin storniert).
      - Nach Statusänderung wird die erste Zeile entfernt und der nächste Patient angezeigt, um eine bessere Übersicht zu haben.
   - Alle erledigten oder stornierten Termine werden in einer **gesonderten Liste unten** angezeigt, sodass man den Überblick behält.
   - Termine können **gelöscht oder neu angelegt** werden.
- **Unten im Dashboard:**
   - Diagrammübersicht über die **Anzahl der Patienten im laufenden Jahr** und in der **laufenden Woche**.
- **Termin anlegen:**
   - Patient muss über die **Versicherungsnummer** gesucht werden.
   - Nach Eingabe und Enter wird der Patient selektiert und die Infos zur Kontrolle angezeigt.
   - Benutzer gibt eine **Notiz** ein, wählt **Datum, Uhrzeit** und **Arzt**.
   - Alle gebuchten Termine werden in der Zeitleiste angezeigt.
- **Rechte Seite des Dashboards:**
   - Aufgaben, die der Arzt vom Personal benötigt (Funktion folgt in zukünftigen Versionen).
- **Linke Seite des Dashboards:**
   - Menübuttons für verschiedene Fenster: Dashboard, Praxis, Labor, Patient, Admin (momentan nur Dashboard und Labor funktional).

### 3. Labor-Fenster
- Zeigt ausreichende Informationen über die angemeldete Person.
- Benutzer kann persönliche Daten (außer Personalnummer) später aktualisieren (Funktion in Arbeit).
- Übersicht der Personen, die im Monat Urlaub haben. Auswahl anderer Monate über einen DatePicker möglich.
- Benutzer kann **Urlaub beantragen**, wird jedoch erst in der Tabelle angezeigt, wenn der Arzt genehmigt.
- Benutzer kann **neue ToDos** zu seiner ToDo-Liste hinzufügen.

---

## Screenshots
![Login Screen](./screenshots/Login.png)  
![Dashboard](./screenshots/Dashboard01.png)  
![Dashboard](./screenshots/Dashboard02.png)  
![Terminübersicht](./screenshots/appointments.png)
![Dashboard](./screenshots/Praxis.png)

---

## Installation

1. Stelle sicher, dass **Java 17 oder höher** installiert ist.
2. Klone das Repository:
   ```bash
   git clone https://github.com/Akbari786/Praxismanagement

3. Öffne das Projekt in deiner bevorzugten IDE (z. B. IntelliJ, Eclipse).

4. Baue das Projekt und installiere alle Abhängigkeiten.

5. Starte die App:
   java -jar praxismanagement-app.jar

## Aktueller Stand

Das Projekt ist nicht vollständig fertig, viele Funktionen sind noch in Arbeit.

Ich arbeite kontinuierlich daran, die App zu verbessern und neue Features hinzuzufügen.

## Feedback & Mitwirken

Ich freue mich über Feedback, Ideen oder Verbesserungsvorschläge.
Wenn du Lust hast, kannst du auch gerne direkt mithelfen, Pull Requests sind willkommen!
