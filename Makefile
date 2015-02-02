all:
	mvn compile
	mvn assembly::single

clean:
	mvn clean
