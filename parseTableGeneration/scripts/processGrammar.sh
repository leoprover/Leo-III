#!/bin/bash


PIPELINE="./scripts/internal/processGrammar/pipelineCmds.sh"

# -----------------------------------------------------------------------------------
INPUT="input/2_InputSyntax.bnf"
OUTPUT_DIR="./output/1_elimEpsilon"
LOGS="$OUTPUT_DIR/log"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$LOGS"

echo "processing $OUTPUT_DIR ..."
$PIPELINE "$INPUT" "$OUTPUT_DIR" "$LOGS" << EOF
./sgCFG $(cat tptpFormatParams) -g default
./sgCFG -i default -g default -t 'elimEpsilon()'
./sgCFG -i default -g default -t 'delete(,^conditional_term)' -t 'delete(,^let_term)'
grep -v '|conditional_term$\||let_term$'
EOF

OUTPUT_DIR="./output/1"
LOGS="$OUTPUT_DIR/log"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$LOGS"

echo "processing $OUTPUT_DIR ..."
$PIPELINE "$INPUT" "$OUTPUT_DIR" "$LOGS" << EOF
./sgCFG $(cat tptpFormatParams) -g default
./sgCFG -i default -g default -t 'delete(,^conditional_term)' -t 'delete(,^let_term)'
grep -v '|conditional_term$\||let_term$'
EOF

# -----------------------------------------------------------------------------------
# term
INPUT="output/1/2"
OUTPUT_DIR="./output/term"
LOGS="$OUTPUT_DIR/log"
START_SYMBOL="term"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$LOGS"

echo "processing $OUTPUT_DIR ..."
$PIPELINE "$INPUT" "$OUTPUT_DIR" "$LOGS" << EOF
./sgCFG -i default -g default -t 'subGrammar($START_SYMBOL)'
./sgCFG -i default -g default -t 'insert(start,Z0->$START_SYMBOL)'
./sgCFG -i default -g default -t 'addActionSymbols(0)'
./sgCFG -i default -g default -t 'breakRules(2,Z0)'
./sgCFG -i default -g default -t 'elimLeftRec_noEpsilon_full(,^Z.*,Z0)'
./sgCFG -i default -g default -t 'unfold(not,^Z.*)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'leftFactor_full(whileDecreasing,,^Z.*,Z0)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'removeDoubleProds()'
EOF

# -----------------------------------------------------------------------------------
# thf_formula (term as terminal)
INPUT="output/1/2"
OUTPUT_DIR="./output/thf_formula_termAsTerminal"
LOGS="$OUTPUT_DIR/log"
START_SYMBOL="thf_formula"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$LOGS"

echo "processing $OUTPUT_DIR ..."
$PIPELINE "$INPUT" "$OUTPUT_DIR" "$LOGS" << EOF
./sgCFG -i default -g default -t 'subGrammar($START_SYMBOL)'
./sgCFG -i default -g default -t 'delete(,^term$)' -t 'subGrammar($START_SYMBOL)'
sed 's/\<term\>/"term"/g'
./sgCFG -i default -g default -t 'insert(start,Z0->$START_SYMBOL)'
./sgCFG -i default -g default -t 'addActionSymbols(0)'
./sgCFG -i default -g default -t 'breakRules(2,Z0)'
./sgCFG -i default -g default -t 'elimLeftRec_noEpsilon_full(,^Z.*,Z0)'
./sgCFG -i default -g default -t 'unfold(not,^Z.*)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'leftFactor_full(whileDecreasing,,^Z.*,Z0)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'removeDoubleProds()'
EOF

# -----------------------------------------------------------------------------------
# TPTP_input (term and thf_formula as terminals)
INPUT="output/1/2"
OUTPUT_DIR="./output/TPTP_input_formulaAsTerminal"
LOGS="$OUTPUT_DIR/log"
START_SYMBOL="TPTP_input"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$LOGS"

echo "processing $OUTPUT_DIR ..."
$PIPELINE "$INPUT" "$OUTPUT_DIR" "$LOGS" << EOF
grep -v '|tff_annotated$\||fof_annotated$\||cnf_annotated$\||tpi_annotated$'
grep -v '|"\$tff" "("\||"\$fof" "("\||"\$cnf" "("'
./sgCFG -i default -g default -t 'delete(,^thf_formula$)' -t 'delete(,^term$)'
sed 's/\<thf_formula\>/"thf_formula"/g'
sed 's/\<term\>/"term"/g'
./sgCFG -i default -g default -t 'subGrammar($START_SYMBOL)'
./sgCFG -i default -g default -t 'insert(start,Z0->$START_SYMBOL)'
./sgCFG -i default -g default -t 'addActionSymbols(0)'
./sgCFG -i default -g default -t 'breakRules(2,Z0)'
./sgCFG -i default -g default -t 'elimLeftRec_noEpsilon_full(,^Z.*,Z0)'
./sgCFG -i default -g default -t 'unfold(not,^Z.*)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'leftFactor_full(whileDecreasing,,^Z.*,Z0)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'removeDoubleProds()'
EOF

# -----------------------------------------------------------------------------------
# thf_formula
INPUT="output/1/2"
OUTPUT_DIR="./output/thf_formula"
LOGS="$OUTPUT_DIR/log"
START_SYMBOL="thf_formula"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$LOGS"

echo "processing $OUTPUT_DIR ..."
$PIPELINE "$INPUT" "$OUTPUT_DIR" "$LOGS" << EOF
./sgCFG -i default -g default -t 'subGrammar($START_SYMBOL)'
./sgCFG -i default -g default -t 'insert(start,Z0->$START_SYMBOL)'
./sgCFG -i default -g default -t 'addActionSymbols(0)'
./sgCFG -i default -g default -t 'breakRules(2,Z0)'
./sgCFG -i default -g default -t 'elimLeftRec_noEpsilon_full(,^Z.*,Z0)'
./sgCFG -i default -g default -t 'unfold(not,^Z.*)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'leftFactor_full(whileDecreasing,,^Z.*,Z0)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'removeDoubleProds()'
EOF

# -----------------------------------------------------------------------------------
# TPTP_input
INPUT="output/1/2"
OUTPUT_DIR="./output/TPTP_input"
LOGS="$OUTPUT_DIR/log"
START_SYMBOL="TPTP_input"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$LOGS"

echo "processing $OUTPUT_DIR ..."
$PIPELINE "$INPUT" "$OUTPUT_DIR" "$LOGS" << EOF
grep -v '|tff_annotated$\||fof_annotated$\||cnf_annotated$\||tpi_annotated$'
grep -v '|"\$tff" "("\||"\$fof" "("\||"\$cnf" "("'
./sgCFG -i default -g default -t 'subGrammar($START_SYMBOL)'
./sgCFG -i default -g default -t 'insert(start,Z0->$START_SYMBOL)'
./sgCFG -i default -g default -t 'addActionSymbols(0)'
./sgCFG -i default -g default -t 'breakRules(2,Z0)'
./sgCFG -i default -g default -t 'elimLeftRec_noEpsilon_full(,^Z.*,Z0)'
./sgCFG -i default -g default -t 'unfold(not,^Z.*)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'leftFactor_full(whileDecreasing,,^Z.*,Z0)'
./sgCFG -i default -g default -t 'subGrammar(Z0)'
./sgCFG -i default -g default -t 'removeDoubleProds()'
EOF
