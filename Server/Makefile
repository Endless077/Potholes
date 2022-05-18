CC = gcc
CFLAGS = -O2 -Wall
PTHREAD_FLAG = -pthread
SQL_FLAG = -lsqlite3
LDLIBS = -lm
OBJECTFILES = 
TARGET = server_pothole
SRC = server.c Other/utils.c

all: $(TARGET)
	@printf "\e[32m%b\e[0m" "\n\tFile Creation...\n"

$(TARGET): $(OBJECTFILES)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJECTFILES) $(LDLIBS) $(SRC) $(PTHREAD_FLAG) $(SQL_FLAG)

.PHONY: depend clean

clean: 
	@printf "\e[32m%b\e[0m" "\n\tClean...\n"
	rm -f $(OBJECTFILES) $(TARGET)

valgrind:
	@printf "\e[32m%b\e[0m" "\n\Valgrind MemCheck...\n"
	valgrind --leak-check=full \
		--show-leak-kinds=all \
        --track-origins=yes \
        --verbose \
        --log-file=valgrind-out.txt \
        ./server_pothole