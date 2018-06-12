DESTDIR ?= $(HOME)/bin
CC=gcc
CONTRIB=./contrib

default: all

TreeLimitedRun: $(CONTRIB)/TreeLimitedRun.c
		$(CC) $(CONTRIB)/TreeLimitedRun.c -o TreeLimitedRun -static

all: TreeLimitedRun
		@echo Compiling auxiliary scripts ...
		mv TreeLimitedRun ./src/main/resources/scripts/.
		@echo Building Leo-III ...
		sbt assembly
		mkdir bin -p
		cp target/scala-2.12/leo3.jar bin/leo3.jar
		cat ./src/main/resources/scripts/exec_dummy bin/leo3.jar > bin/leo3
		chmod +x bin/leo3

install:
		install -m 0755 -d $(DESTDIR)
		install -m 0755 bin/leo3 $(DESTDIR)

clean:
		rm -rf ./target/
		rm -rf ./bin/

