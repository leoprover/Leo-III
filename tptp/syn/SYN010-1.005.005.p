%--------------------------------------------------------------------------
% File     : SYN010-1.005.005 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Example for Proposition 5.2 in [LMG94]
% Version  : Biased.
% English  : Example to show that connection tableaux with factorization
%            cannot polynomially simulate simulate connection tableaux with
%            folding up.

% Refs     : [LMG94] Letz et al. (1994), Controlled Integration of the Cut
% Source   : [LMG94]
% Names    : Example 5.1 [LMG94]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   27 (   0 non-Horn;   6 unit;  27 RR)
%            Number of atoms       :  132 (   0 equality)
%            Maximal clause size   :    6 (   5 average)
%            Number of predicates  :   26 (  26 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : Biased towards folding up.
%          : tptp2X: -f tptp -s5:5 SYN010-1.g
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p_0 )).

cnf(clause_2,negated_conjecture,
    ( p_0
    | ~ p_1_1
    | ~ p_1_2
    | ~ p_1_3
    | ~ p_1_4
    | ~ p_1_5 )).

cnf(clause_3,negated_conjecture,
    ( p_1_1
    | ~ p_2_1
    | ~ p_2_2
    | ~ p_2_3
    | ~ p_2_4
    | ~ p_2_5 )).

cnf(clause_4,negated_conjecture,
    ( p_1_2
    | ~ p_2_1
    | ~ p_2_2
    | ~ p_2_3
    | ~ p_2_4
    | ~ p_2_5 )).

cnf(clause_5,negated_conjecture,
    ( p_1_3
    | ~ p_2_1
    | ~ p_2_2
    | ~ p_2_3
    | ~ p_2_4
    | ~ p_2_5 )).

cnf(clause_6,negated_conjecture,
    ( p_1_4
    | ~ p_2_1
    | ~ p_2_2
    | ~ p_2_3
    | ~ p_2_4
    | ~ p_2_5 )).

cnf(clause_7,negated_conjecture,
    ( p_1_5
    | ~ p_2_1
    | ~ p_2_2
    | ~ p_2_3
    | ~ p_2_4
    | ~ p_2_5 )).

cnf(clause_8,negated_conjecture,
    ( p_2_1
    | ~ p_3_1
    | ~ p_3_2
    | ~ p_3_3
    | ~ p_3_4
    | ~ p_3_5 )).

cnf(clause_9,negated_conjecture,
    ( p_2_2
    | ~ p_3_1
    | ~ p_3_2
    | ~ p_3_3
    | ~ p_3_4
    | ~ p_3_5 )).

cnf(clause_10,negated_conjecture,
    ( p_2_3
    | ~ p_3_1
    | ~ p_3_2
    | ~ p_3_3
    | ~ p_3_4
    | ~ p_3_5 )).

cnf(clause_11,negated_conjecture,
    ( p_2_4
    | ~ p_3_1
    | ~ p_3_2
    | ~ p_3_3
    | ~ p_3_4
    | ~ p_3_5 )).

cnf(clause_12,negated_conjecture,
    ( p_2_5
    | ~ p_3_1
    | ~ p_3_2
    | ~ p_3_3
    | ~ p_3_4
    | ~ p_3_5 )).

cnf(clause_13,negated_conjecture,
    ( p_3_1
    | ~ p_4_1
    | ~ p_4_2
    | ~ p_4_3
    | ~ p_4_4
    | ~ p_4_5 )).

cnf(clause_14,negated_conjecture,
    ( p_3_2
    | ~ p_4_1
    | ~ p_4_2
    | ~ p_4_3
    | ~ p_4_4
    | ~ p_4_5 )).

cnf(clause_15,negated_conjecture,
    ( p_3_3
    | ~ p_4_1
    | ~ p_4_2
    | ~ p_4_3
    | ~ p_4_4
    | ~ p_4_5 )).

cnf(clause_16,negated_conjecture,
    ( p_3_4
    | ~ p_4_1
    | ~ p_4_2
    | ~ p_4_3
    | ~ p_4_4
    | ~ p_4_5 )).

cnf(clause_17,negated_conjecture,
    ( p_3_5
    | ~ p_4_1
    | ~ p_4_2
    | ~ p_4_3
    | ~ p_4_4
    | ~ p_4_5 )).

cnf(clause_18,negated_conjecture,
    ( p_4_1
    | ~ p_5_1
    | ~ p_5_2
    | ~ p_5_3
    | ~ p_5_4
    | ~ p_5_5 )).

cnf(clause_19,negated_conjecture,
    ( p_4_2
    | ~ p_5_1
    | ~ p_5_2
    | ~ p_5_3
    | ~ p_5_4
    | ~ p_5_5 )).

cnf(clause_20,negated_conjecture,
    ( p_4_3
    | ~ p_5_1
    | ~ p_5_2
    | ~ p_5_3
    | ~ p_5_4
    | ~ p_5_5 )).

cnf(clause_21,negated_conjecture,
    ( p_4_4
    | ~ p_5_1
    | ~ p_5_2
    | ~ p_5_3
    | ~ p_5_4
    | ~ p_5_5 )).

cnf(clause_22,negated_conjecture,
    ( p_4_5
    | ~ p_5_1
    | ~ p_5_2
    | ~ p_5_3
    | ~ p_5_4
    | ~ p_5_5 )).

cnf(clause_23,negated_conjecture,
    ( p_5_1 )).

cnf(clause_24,negated_conjecture,
    ( p_5_2 )).

cnf(clause_25,negated_conjecture,
    ( p_5_3 )).

cnf(clause_26,negated_conjecture,
    ( p_5_4 )).

cnf(clause_27,negated_conjecture,
    ( p_5_5 )).

%--------------------------------------------------------------------------
