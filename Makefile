CC=gcc
CONTRIB=./contrib

default: all

TreeLimitedRun: $(CONTRIB)/TreeLimitedRun.c
		$(CC) $(CONTRIB)/TreeLimitedRun.c -o TreeLimitedRun -static

all: TreeLimitedRun
		@echo Compiling auxiliary scripts ...
		mv TreeLimitedRun ./src/main/resources/scripts/.
		@echo Downloading picosat ...
		curl http://fmv.jku.at/picosat/picosat-965.tar.gz | tar -C ./src/native -xz
		@echo Building Leo-III ...
		sbt buildParser
		sbt nativeCompile
		sbt assembly
  
