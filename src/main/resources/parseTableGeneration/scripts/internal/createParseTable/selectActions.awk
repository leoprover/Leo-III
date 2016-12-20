#!/bin/awk -f

# this script will print all rules for which it finds an action symbol
# followed by { ... }

BEGIN {
	lhs=""
	rhs=""
}

{
	if( match($0,".*->") ) {
		lhs=$0
		#print lhs
	}
	if( match($0, "action_[^ ]+ {[^}]*}") ) {
		#RSTART is where the pattern starts
		#RLENGTH is the length of the pattern
		#before = substr($0,1,RSTART-1);
		#pattern = substr($0,RSTART,RLENGTH);
		#after = substr($0,RSTART+RLENGTH);
		#printf("%s<%s>%s\n", before, pattern, after);

		rhs=substr($0,1,RSTART-2)
		action=substr($0,RSTART,RLENGTH)
		#printf("rhs: %s\naction: %s\n", rhs, action);

		if( match(rhs, " +\\|| +") ) {
			#RSTART is where the pattern starts
			#RLENGTH is the length of the pattern
			before = substr(rhs,1,RSTART-1);
			pattern = substr(rhs,RSTART,RLENGTH);
			after = substr(rhs,RSTART+RLENGTH);
			#printf("%s<%s>%s\n", before, pattern, after);

			rhs=after
		}
		print action, "\n\t", lhs, rhs
	}
}

END {
}


# prints every occurence of action_* { * }:
#grep -o 'action_[^ ]\+ {[^}]*}'
