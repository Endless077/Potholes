CC = gcc
CFLAGS = -O2 -Wall
SQL_FLAG = -lsqlite3
LDLIBS = -lm #=flag da usare per il linker
OBJECTFILES = 
TARGET = serverapp
SRC = provaserver.c

all: $(TARGET)
	@printf "\e[32m%b\e[0m" "\n\tCreazione del file completata\n"

$(TARGET): $(OBJECTFILES)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJECTFILES) $(LDLIBS) $(SRC) $(SQL_FLAG)

.PHONY: depend clean # Controlliamo che non sia presente realmente un file chiamato clean


clean: 
	rm -f $(OBJECTFILES) $(TARGET)
	@printf "\e[32m%b\e[0m" "\n\tEliminazione file completata\n"
