#!/bin/sh

classpath="target/scala-2.10/classes"
compile=true

# Check for not compile option
while getopts ":r" opt; do
   case $opt in
      r)
         compile=false
         ;;
      \?)
         echo "Invalid optioin: -$OPTARG" >&2
         exit -1
   esac
done

# First compile the project
if [ "$compile" = true ]
then
   sbt compile
fi

scala -classpath "$classpath" -i src/main/scala/LeoShell.scala 
