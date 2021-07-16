DESTDIR ?= $(HOME)/bin
CC=gcc
CONTRIB=./contrib

default: all
all: TreeLimitedRun leo3
static: TreeLimitedRunStatic leo3
native: static

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
		mkdir bin
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
		$(GRAALVM_HOME)/bin/native-image -jar bin/leo3.jar \
		-H:+ReportExceptionStackTraces \
		-H:Name="leo3-bin" \
		--initialize-at-build-time=scala.runtime.Statics$$VM \
		-H:ConfigurationFileDirectories=${CONTRIB}/graalvm/ \
		-O2 \
		--no-server \
		--no-fallback \
		--static \
		--libc=musl
		mv leo3-bin bin/leo3-bin


native-profile:
		@echo Creating native Leo-III image with graalvm
		$(GRAALVM_HOME)/bin/native-image -jar bin/leo3.jar \
		-H:+ReportExceptionStackTraces \
		-H:Name="leo3-profile" \
		--initialize-at-run-time=leo.modules.modes.Normalization\$$ \
		--initialize-at-build-time=scala.runtime.Statics$$VM \
		-H:ConfigurationFileDirectories=${CONTRIB}/graalvm/ \
		--no-server \
		--no-fallback \
		--pgo-instrument



native-profile-run:
		@echo run LEO profile
		./leo3-profile ./src/test/resources/problems/choice/SYO556\^1.p --seq -t 300 --atp cvc4 --atp eprover


native-pgo:
			@echo Creating native Leo-III image with graalvm
			$(GRAALVM_HOME)/bin/native-image -jar bin/leo3.jar \
			-H:+ReportExceptionStackTraces \
			-H:Name="leo3-pgo" \
			--initialize-at-run-time=leo.modules.modes.Normalization\$$ \
			-H:ReflectionConfigurationFiles=${CONTRIB}/graalvm/reflect-config.json \
			-H:ResourceConfigurationFiles=${CONTRIB}/graalvm/resource-config.json \
			-O2 \
			--no-server \
			--static \
			--pgo
			mv leo3-pgo bin/leo3-pgo

