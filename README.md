![Wallpaper](https://github.com/Endless077/Potholes/blob/main/client/app/src/main/res/drawable/pothole_centoventotto.png)

## 🚗 Potholes

Applicazione Client-Server per la segnalazione e la consultazione di informazioni riguardanti irregolarità della superficie (buche).


## 🚀 Funzionalità

- Visualizzare un elenco di tutti gli eventi registrati dal server entro un determinato raggio dalla posizione dell'utente.
- Permettere all'utente di avviare una sessione di registrazione evento in cui il client si connette al server, riceve i parametri di soglia e segnala la posizione e il valore di ogni nuovo evento al server.
- Consentire all'utente di visualizzare gli eventi nelle vicinanze su una mappa.
- Il server è concorrente e può gestire un numero arbitrario di client contemporaneamente.
- Il server registra le operazioni principali (nuove connessioni, disconnessioni, richieste dei client) sull'output standard.
- Documentazione dettagliata sia per il client che per il server.

### Dettagli di Implementazione

- Il server è implementato in C su una piattaforma UNIX/Linux ed è ospitato online su Microsoft Azure (accesso ristretto).
- Il client è sviluppato in Java per la piattaforma Android e utilizza sensori di accelerazione.
- La comunicazione tra il client e il server avviene tramite socket TCP o UDP.
- Oltre alle chiamate di sistema UNIX, il server utilizza solo la libreria standard di C.


## 📲 Installazione

Installa Potholes con git

```bash
  git clone https://github.com/Endless077/Potholes.git
```

dopo aver aperto la cartella “Server”, abbiamo due modi
per compilare ed eseguire il server:

- Utilizzando il comando gcc.
- Utilizzando il makefile (solo server).

gcc:
```bash
  gcc -o <name_starter> Other/startserver.c Other/utils.c -lsqlite
  gcc -o <name_server> server.c Other/utils.c -pthread -lsqlite
```

makefile:
```bash
  make makefile clean all [valgrind || start]
```

(dovresti avere “Valgrind” installato per eseguire uno script di controllo per eventuali leaks di memoria)

se non possiedi "Valgrind" ignora l'ultima opzione ed usa "start".

Avvia il server:
```bash
  ./<starter_name>
  ./<server_name>
```

L'installazione del client richiede l'aggiornamento del server ip e della porta
nel file "Network.java", una semplice sostituzione di parametri.


## 📃 Documentation

[Documentazione](http://www.example.com/) - qui la documentazione.


## 🖐 Authors

- [Garofalo Antonio](https://github.com/Endless077)
- [Bianco Luca](https://github.com/Luca-Bianco)

📧 Support e feedback contattando uno dei collaboratori.


## 💾 About

[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)

©️ GNU General Public License (GPL), more details [here](https://www.gnu.org/licenses/gpl-3.0.en.html).
