#!/bin/bash

OVERWRITE=0

while [[ "$1" == "-o" || "$1" == "--overwrite" ]]; do
	case "$1" in
	-o | --overwrite)
			OVERWRITE=1
			shift
		;;
	esac
done


if [[ "$1" == "" || "$2" == "" || "$3" == "" ]]; then
	echo "syntax: $0 [OPTIONS] INPUT OUTPUT_DIR LOGS"
	echo "OPTIONS:"
	echo "-o | --overwrite"
	exit 1
fi


INPUT="$1"
OUTPUT_DIR="$2"

LOGS="$3"

# mkdir -p "$OUTPUT_DIR"
# mkdir -p "$LOGS"
> "$LOGS.log" # <- delete log file

# these are the transformations to be applied to the grammar:
STEPS=()
while read -r -d $'\n' ; do
	STEPS+=("$REPLY")
done < <(cat -)

# echo "array length: ${#STEPS[*]}"

counter=0
for cmd in "${STEPS[@]}"; do
	OUTPUT="$counter"

	echo "-------------------------------------------" >> "$LOGS.log"
	echo -e "step $counter:\n\t$cmd" | tee -a "$LOGS.log" >&2

	cat << EOF >> "$LOGS.log"
cat "$INPUT" | $cmd 2>"$LOGS/$OUTPUT" > "$OUTPUT_DIR/$OUTPUT"
EOF

	if [[ ! -e "$OUTPUT_DIR/$OUTPUT" || "$OVERWRITE" == 1 ]]; then

		cat "$INPUT" |
			eval "$cmd" 2>"$LOGS/$OUTPUT" > "$OUTPUT_DIR/$OUTPUT" || {
				rm "$OUTPUT_DIR/$OUTPUT"
				exit 1
			}
	else
		echo "file \"$OUTPUT_DIR/$OUTPUT\" already exists, skipped"
	fi

	INPUT="$OUTPUT_DIR/$OUTPUT"
	counter=$((counter+1))
done
