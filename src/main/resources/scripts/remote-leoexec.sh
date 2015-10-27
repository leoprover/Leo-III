#!/bin/sh

# Get the current path in ressource
cd $(dirname $(readlink -f $0)) # Problem on OSX, look at it again later
./RemoteSOT.pl -t 30 -s LEO-II--- -S $1 | grep "SZS status" | head -1 | tail -c +3

wait
