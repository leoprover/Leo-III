DESTDIR ?= $(HOME)/bin
CC=gcc
CONTRIB=./contrib

default: all
all: TreeLimitedRun leo3
static: TreeLimitedRunStatic leo3
native: leo3

TreeLimitedRun: $(CONTRIB)/TreeLimitedRun.c
		@echo Compiling auxiliary scripts ...
		$(CC) $(CONTRIB)/TreeLimitedRun.c -o TreeLimitedRun
		mv TreeLimitedRun ./src/main/resources/scripts/.
		
TreeLimitedRunStatic: $(CONTRIB)/TreeLimitedRun.c
		@echo Compiling auxiliary scripts ...
		$(CC) $(CONTRIB)/TreeLimitedRun.c -o TreeLimitedRun -static
		mv TreeLimitedRun ./src/main/resources/scripts/.
		
leo3: 
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

native:
		@echo Creating native Leo-III image with graalvm
		native-image -jar bin/leo3.jar \
			-H:+ReportExceptionStackTraces \
			-H:Name="leo3-bin" \
			--delay-class-initialization-to-runtime=leo.modules.modes.Normalization\$$ \
			-H:ReflectionConfigurationFiles=${CONTRIB}/graalvm/reflectconfig \
			-O2 \
			--static
		mv leo3-bin bin/leo3-bin
