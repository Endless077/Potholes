![Logo](https://github.com/Endless077/Potholes/blob/main/client/app/src/main/res/drawable/pothole_centoventotto.png)


## 🚗 Pothoes

Client-Server application for reporting and querying information about surface irregularities (potholes).


## 🚀 Features

- Display a list of all events recorded by the server within a certain radius of the user's location.
- Allow the user to start an event recording session where the client connects to the server, receives threshold parameters, and reports the position and value of each new event to the server.
- Enable the user to view nearby events on a map.
- The server is concurrent and can handle an arbitrary number of clients simultaneously.
- The server logs key operations (new connections, disconnections, client requests) to standard output.
- Detailed documentation for both the client and server.

### Implementation Details

- The server is implemented in C on a UNIX/Linux platform and is hosted online on Microsoft Azure (access restricted).
- The client is developed in Java for the Android platform and utilizes acceleration sensors.
- Communication between the client and server is handled via TCP or UDP sockets.
- Apart from UNIX system calls, the server uses only the standard C library.


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

## 💾 License

[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)

©️ GNU General Public License (GPL), more details [here](https://www.gnu.org/licenses/gpl-3.0.en.html).

## 📃 Documentation

[Documentation](http://www.example.com/) - here the documentation.


## 🖐 Authors

- [Garofalo Antonio](https://github.com/Endless077)
- [Bianco Luca](https://github.com/Luca-Bianco)


## 📧 Support

Support e feedback contattando uno dei collaboratori.

