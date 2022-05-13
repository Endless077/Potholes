CC = gcc
CFLAGS = -O2 -Wall
PTHREAD_FLAG = -pthread
SQL_FLAG = -lsqlite3
LDLIBS = -lm
OBJECTFILES = 
TARGET = server_pothole
SRC = server.c Other/utils.c

all: $(TARGET)
	@printf "\e[32m%b\e[0m" "\n\tCreazione del file completata\n"

$(TARGET): $(OBJECTFILES)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJECTFILES) $(LDLIBS) $(SRC) $(PTHREAD_FLAG) $(SQL_FLAG)

.PHONY: depend clean

clean: 
	rm -f $(OBJECTFILES) $(TARGET)
	@printf "\e[32m%b\e[0m" "\n\tEliminazione file completata\n"
