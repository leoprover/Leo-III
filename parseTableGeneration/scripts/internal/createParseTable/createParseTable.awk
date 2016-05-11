#!/bin/awk -f

BEGIN {
	lhs=""
	# allRhs
	rhsIndex=1
}

!/^ *$/ { # (execute this block just for nonempty lines)
	if( $2 ~ /->/) {
		lhs=$1

		rhsIndex=1
		nonTerminalRule_count=0
		print ""
		printf "// %s", lhs
		print ""
	}
	else {
		match($0,/\|/)
		line = substr($0,RSTART+RLENGTH)
		rhs_count = split(line,rhs," ")

		# copy rhs into allRhs array
		for( i=1; i<=rhs_count; i++) {
			allRhs[rhsIndex,i] = rhs[i]
		}

		firstSymbol = rhs[1]
		if( firstSymbol ~ /action_/ ) {
			nonTerminalRule_count++
		}
		else if( firstSymbol ~ /Z/ ) {
			nonTerminalRule_count++
		}

		# write comment:
		# printf "// ->"
		# printf "// %s ->", lhs
		#for( i=1; i<=rhs_count; i++) {
			# printf " %s", rhs[i]
		# }

		# if( nonTerminalRule_count > 1 ) {
			# printf "// CONFLICT"
			# print ""
		# }

		printf "\t"
		# firstSymbol = rhs[1]
		if( firstSymbol ~ /action_/ ) {
			printf "(%s, anyToken) ->", lhs
			# printf "(%s, anyToken) ->", "'" tolower(lhs)
		}
		else if( firstSymbol ~ /Z/ ) {
			printf "(%s, FIRST(%s)) ->", lhs, firstSymbol
			# printf "(%s, FIRST(%s)) ->", "'" tolower(lhs), firstSymbol
		}
		else if( firstSymbol ~ /^[ ]*$/ || firstSymbol ~ /^""$/ ) {
			printf "(%s, anyToken) ->", lhs
		}
		else {
			printf "(%s, %s) ->", lhs, firstSymbol
			# printf "(%s, %s) ->", "'" tolower(lhs), firstSymbol
		}

		printf " "
		# printf " Seq("

		for( i=1; i<=rhs_count; i++) {
			rhsSymbol = rhs[i]
			if( 0 ) {
				if( rhsSymbol ~ /Z/ )
					rhsSymbol = rhsSymbol
					# rhsSymbol = "'" tolower(rhsSymbol)
				else if( rhsSymbol ~ /action_/ )
					rhsSymbol = rhsSymbol " _"
				if( i != 1)
					printf ", "
				printf "%s", rhsSymbol
			}
			if( i != 1)
				printf " "
			printf "%s", rhsSymbol
		}
		# printf "),"
		print ""

		rhsIndex++
	}
}

END {
}
