#!/bin/bash

if [[ $# != 3 ]]; then
	echo "syntax: $0 ANNOTATED_GRAMMAR PROCESSED_GRAMMAR OUTPUT_DIR"
	exit
fi

ANNOTATED_GRAMMAR="$1"
PROCESSED_GRAMMAR="$2"
OUTPUT_DIR="$3"

mkdir -p "$OUTPUT_DIR"

INTERNALS="scripts/internal/createParseTable"

cat "$ANNOTATED_GRAMMAR" | ./$INTERNALS/selectActions.awk > "$OUTPUT_DIR/actions"

selectedActions=$(cat "$OUTPUT_DIR"/actions | sed -n -e 's/\(^action_[^ ]*\).*/\1/p' | sed -n 's/action_\(.*\)/\1/p')

# selectedActions=$(echo $selectedActions)
# echo "only keep actions with these numbers:" $selectedActions
# echo $selectedActions

cat "$PROCESSED_GRAMMAR" | ./$INTERNALS/filter_actionSymbols.sh $selectedActions > "$OUTPUT_DIR/1_actionsFiltered"

cat "$OUTPUT_DIR/1_actionsFiltered" | ./$INTERNALS/createParseTable.awk > "$OUTPUT_DIR/2_table"

cat "$OUTPUT_DIR/1_actionsFiltered" | ./sgCFG -i default -g default -t 'annotate(first)' 2>/dev/null | grep 'FIRST' | sed 's/FIRST=\({[^}]*}\) \([^ ]*\).*/\2 \1/g' | sort --key='1' > "$OUTPUT_DIR/firstSets"

# this script inserts the first sets into the parse table
./$INTERNALS/insertFirstSets.sh "$OUTPUT_DIR/firstSets" "$OUTPUT_DIR/2_table" > "$OUTPUT_DIR/3_table"

# ./$INTERNALS/insertFirstSets.sh --unfold "$OUTPUT_DIR/firstSets" "$OUTPUT_DIR/10_table" > "$OUTPUT_DIR/4_table"
