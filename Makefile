CC=gcc
CONTRIB=./contrib

default: all

TreeLimitedRun: $(CONTRIB)/TreeLimitedRun.c
		$(CC) $(CONTRIB)/TreeLimitedRun.c -o TreeLimitedRun -static

all: TreeLimitedRun
		@echo Compiling auxiliary scripts ...
		mv TreeLimitedRun ./src/main/resources/scripts/.
		@echo Unpacking picosat ...
		tar -C ./src/native -xzf contrib/picosat-965.tar.gz
		@echo Building Leo-III ...
		echo "assembly" | sbt shell
		mkdir bin -p
		cp "target/Leo III-assembly-1.1.jar" bin/leo3.jar
		cp ./src/main/resources/scripts/leo3 bin/leo3
