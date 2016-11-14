#!/bin/bash


sedList=

counter=0
for i in "$@"; do
	if [[ $counter == 0 ]]; then
		sedList="$i"
	else
		sedList="$sedList|$i"
	fi
	counter=$((counter+1))
done

#echo "$sedList"


awkArgs='
BEGIN {
}

{
	line=""
	after=$0
	while(after != "") {
		if( match(after, "action_[^ ]+") ) {
			#RSTART is where the pattern starts
			#RLENGTH is the length of the pattern
			#before = substr($0,1,RSTART-1);
			#pattern = substr($0,RSTART,RLENGTH);
			#after = substr($0,RSTART+RLENGTH);
			#printf("%s<%s>%s\n", before, pattern, after);
			#print substr($0,1,RSTART-1) substr($0,RSTART+RLENGTH)
			if( substr(after, RSTART, RLENGTH) !~ /action_('"$sedList"')\>/ ) {
				line=(line substr(after,1,RSTART-1))
			}
			else {
				line=(line substr(after,1,RSTART-1+RLENGTH))
			}
			after=substr(after,RSTART+RLENGTH)
		}
		else {
			line=(line after)
			after=""
		}
		#print "line: " line, "after: " after
	}
	print line
}

END {
}
'

#echo "$awkArgs"

awk "$awkArgs"
