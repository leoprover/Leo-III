#!/bin/sh


./scripts/RemoteSOT.pl -t 30 -s LEO-II--- -S $1 | grep "SZS status" | head -1 | tail -c +3

wait
