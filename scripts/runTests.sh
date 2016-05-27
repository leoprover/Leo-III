#!/bin/bash

if [[ "$1" == "--help" || "$1" == "-h" ]]; then
	echo "usage: $0 [TEST]"
	echo "execute tests or just one specific test"
	exit 0
fi

subdir="."
justOne="$1"

if [[ "$justOne" != "" ]]; then
	filterExp="-DwildcardSuites=$justOne"
else
	filterExp=
fi

shift

mvn $filterExp test $@
