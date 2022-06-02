## 💾 License

[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)

- [GPL](https://www.gnu.org/licenses/gpl-3.0.en.html)
 
<p align="center">
 <img src="https://github.com/Endless077/Potholes/blob/main/Client/app/src/main/res/drawable/pothole_centoventotto.png" />
</p>

## 🚗 Pothoes

Client-Server che consenta la racconta e l’interrogazione di informazioni riguardanti la presenza di irregolarità (buche) su di una superficie.


## 🚀 Features

- Mostrare all’utente la lista di tutti gli eventi registrati dal server in un certo raggio dalla propria posizione.
- Permettere all’utente di avviare una sessione di registrazione eventi durante la quale il client si connette al server, riceve i parametri di soglia e comunica al server la posizione e il valore del cambiamento ogni volta che registra un nuovo evento.
- Permettere all’utente di visualizzare gli eventi vicini su mappa.
- Il server è di tipo concorrente, ed in grado di gestire un numero arbitrario di client contemporaneamente.
- Il server effettua il log delle principali operazioni (nuove connessioni, sconnessioni, richieste da parte dei client) su standard output.
- Documentazione dettagliata di client e server.

inoltre ecco come è stato realizzato:

Il server è realizzato in linguaggio C su piattaforma UNIX/Linuxe ed è ospitato online su Microsoft Azure (closed).

Il client è in linguaggio Java su piattaforma Android e fa utilizzo dei sensori di accelerazione.

Client e server comunicano tramite socket TCP o UDP.

Oltre alle system call UNIX, il server utilizza solo la libreria standard del C.


## 📲 Installation

Install Pothles with git

```bash
  git clone https://github.com/Endless077/Potholes.git
```

afther this go in "Server" folder, whe have two ways
to compilate and run server:

- Using gcc command.
- Using makefile (only server not starter).

With gcc:
```bash
  gcc -o <name_starter> Other/startserver.c Other/utils.c -lsqlite
  gcc -o <name_server> server.c Other/utils.c -pthread -lsqlite
```

With makefile:
```bash
  make makefile clean all [valgrind || start]
```

(you should have "Valgrind" because it run a script that check memoryleaks)

if you don't have "Valgrind" ignore the last word and use "start".

Run the server:
```bash
  ./<starter_name>
  ./<server_name>
```

The client installation require an upload server ip and port
in the "Network.java", a little substitution.
## 📃 Documentation

[Documentation](https://no-link) - here the documentation.


## 🖐 Authors

- [Garofalo Antonio](https://github.com/Endless077)
- [Bianco Luca](https://github.com/Luca-Bianco)


## 📧 Support

For support, email antonio.garofalo12@studenti.unina.it or luca.bianco4@studenti.unina.it.


## ✔️ Used By

This project is used by the following companies:

- No Company using thsi software...PepeLaugh.
