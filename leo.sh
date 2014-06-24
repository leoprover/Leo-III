#!/bin/sh

# First compile the project
sbt compile

# Insert classpath here
# May get through 'sbt "show fullClasspath"'
scala -classpath "target/scala-2.10/classes:/home/ryu/.ivy2/cache/org.scala-stm/scala-stm_2.10/jars/scala-stm_2.10-0.7.jar" -i src/main/scala/LeoShell.scala 
