DESTDIR ?= $(HOME)/bin
CC=gcc
CONTRIB=./contrib

default: all
all: leo3
static: TreeLimitedRunStatic leo3

		
leo3: 
		sbt assembly
		mkdir -p bin
		cp target/scala-2.13/leo3.jar bin/leo3.jar
		cat ./src/main/resources/scripts/exec_dummy bin/leo3.jar > bin/leo3
		chmod +x bin/leo3

install:
		install -m 0755 -d $(DESTDIR)
		install -m 0755 bin/leo3 $(DESTDIR)

clean:
		rm -rf ./target/
		rm -rf ./bin/

native:
		@echo Creating native Leo-III image with graalvm
		sbt nativeImage
		mv target/native-image/"Leo-III" bin/leo3-native
