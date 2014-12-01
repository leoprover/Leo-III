#!/bin/sh

classpath="target/classes"
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
   mvn compile
fi

scala -classpath "$classpath" -i src/main/scala/LeoShell.scala 
