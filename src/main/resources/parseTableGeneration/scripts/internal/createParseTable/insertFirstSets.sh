#!/bin/bash

unfold=0

if [[ "$1" == "--unfold" ]]; then
	unfold=1
	shift
fi

if [[ "$1" == "" || "$2" == "" ]]; then
	echo "syntax: $0 [--unfold] FIRST_SETS_FILE PARSE_TABLE"
	exit
fi

firstSetsFile="$1"

parseTable="$2"


# this script inserts the first sets into the parse table
awk '
NR==FNR { # this is true only for the first input file
	key=$1
	value=$0
	match( value, " " )
		value=substr(value, RSTART+RLENGTH)
	a[key]=value
	next
}
{
	if( $0 ~ /FIRST/ ) {
		match( $0, "FIRST\\([^\\)]*\\)" )
		before=substr( $0, 1, RSTART-1 )
		pattern=substr( $0, RSTART, RLENGTH )
		after=substr( $0, RSTART+RLENGTH)

		match( pattern, "FIRST\\(" )
		pattern = substr( pattern, RSTART+RLENGTH)
		match( pattern, "\\)" )
		pattern = substr( pattern, 1, RSTART-1 )

		firstSet=a[pattern]
		match( firstSet, "{" )
		firstSet = substr( firstSet, RSTART+RLENGTH)
		match( firstSet, "}" )
		firstSet = substr( firstSet, 1, RSTART-1 )

		temp=firstSet
		i=1
		do {
			match(temp, "\"")
			temp = substr( temp, RSTART+RLENGTH )
			# print "hallo1" temp
			match(temp, "\"")
			firstSetArray[i] = "\"" substr( temp, 1, RSTART-1 ) "\""
			temp = substr( temp, RSTART+RLENGTH )
			# print "hallo2" temp


			i++
		} while( temp != "" )
		# split(firstSet, firstSetArray, ",")

		if( '"$unfold"' ) {
			for(i in firstSetArray)
				print before, firstSetArray[i], after #, "|", pattern, "->", firstSet
		}
		else {
			print before, firstSet , after
		}

		delete firstSetArray

		# print before, firstSet, after
		# print before, "*", after, pattern
	}
	else {
		print $0
	}
}
' "$firstSetsFile" "$parseTable"
