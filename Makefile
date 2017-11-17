DESTDIR ?= $(HOME)/bin/
JAVADIR ?= java/
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
		sbt assembly
		mkdir bin -p
		cp target/scala-2.12/leo3.jar bin/leo3.jar
		cp ./src/main/resources/scripts/leo3 bin/leo3
		
install:
		install -m 0755 -d $(DESTDIR)
		install -m 0755 bin/leo3.jar $(DESTDIR)
		echo -e "#!/bin/bash\njava -Xss32m -Xmx1g -jar $(DESTDIR)leo3.jar \$$@" > $(DESTDIR)leo3
		chmod +x $(DESTDIR)leo3

clean:
		rm -rf target/

