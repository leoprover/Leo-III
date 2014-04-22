%--------------------------------------------------------------------------
% File     : SYN090-1.008 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem t(3,8)
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : T3n [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.33 v5.5.0, 0.00 v2.1.0
% Syntax   : Number of clauses     :   65 (   0 non-Horn;   4 unit;  65 RR)
%            Number of atoms       :  182 (   0 equality)
%            Maximal clause size   :    3 (   3 average)
%            Number of predicates  :   34 (  34 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s8 SYN090-1.g
% Bugfixes : v1.2.1 - Bugfix in SYN087-1.
%--------------------------------------------------------------------------
cnf(s3_goal_1,negated_conjecture,
    ( ~ p_0
    | ~ q_0 )).

cnf(s3_type11_1,axiom,
    ( p_0
    | ~ p_1
    | ~ p_2 )).

cnf(s3_type11_2,axiom,
    ( p_0
    | ~ q_1
    | ~ q_2 )).

cnf(s3_type11_3,axiom,
    ( p_1
    | ~ p_2
    | ~ p_3 )).

cnf(s3_type11_4,axiom,
    ( p_1
    | ~ q_2
    | ~ q_3 )).

cnf(s3_type11_5,axiom,
    ( p_2
    | ~ p_3
    | ~ p_4 )).

cnf(s3_type11_6,axiom,
    ( p_2
    | ~ q_3
    | ~ q_4 )).

cnf(s3_type11_7,axiom,
    ( p_3
    | ~ p_4
    | ~ p_5 )).

cnf(s3_type11_8,axiom,
    ( p_3
    | ~ q_4
    | ~ q_5 )).

cnf(s3_type11_9,axiom,
    ( p_4
    | ~ p_5
    | ~ p_6 )).

cnf(s3_type11_10,axiom,
    ( p_4
    | ~ q_5
    | ~ q_6 )).

cnf(s3_type11_11,axiom,
    ( p_5
    | ~ p_6
    | ~ p_7 )).

cnf(s3_type11_12,axiom,
    ( p_5
    | ~ q_6
    | ~ q_7 )).

cnf(s3_type11_13,axiom,
    ( p_6
    | ~ p_7
    | ~ p_8 )).

cnf(s3_type11_14,axiom,
    ( p_6
    | ~ q_7
    | ~ q_8 )).

cnf(s3_type11_15,axiom,
    ( p_7
    | ~ p_8
    | ~ p_9 )).

cnf(s3_type11_16,axiom,
    ( p_7
    | ~ q_8
    | ~ q_9 )).

cnf(s3_type11_17,axiom,
    ( p_8
    | ~ p_9
    | ~ p_10 )).

cnf(s3_type11_18,axiom,
    ( p_8
    | ~ q_9
    | ~ q_10 )).

cnf(s3_type11_19,axiom,
    ( p_9
    | ~ p_10
    | ~ p_11 )).

cnf(s3_type11_20,axiom,
    ( p_9
    | ~ q_10
    | ~ q_11 )).

cnf(s3_type11_21,axiom,
    ( p_10
    | ~ p_11
    | ~ p_12 )).

cnf(s3_type11_22,axiom,
    ( p_10
    | ~ q_11
    | ~ q_12 )).

cnf(s3_type11_23,axiom,
    ( p_11
    | ~ p_12
    | ~ p_13 )).

cnf(s3_type11_24,axiom,
    ( p_11
    | ~ q_12
    | ~ q_13 )).

cnf(s3_type11_25,axiom,
    ( p_12
    | ~ p_13
    | ~ p_14 )).

cnf(s3_type11_26,axiom,
    ( p_12
    | ~ q_13
    | ~ q_14 )).

cnf(s3_type11_27,axiom,
    ( p_13
    | ~ p_14
    | ~ p_15 )).

cnf(s3_type11_28,axiom,
    ( p_13
    | ~ q_14
    | ~ q_15 )).

cnf(s3_type12_1,axiom,
    ( q_0
    | ~ p_1
    | ~ q_2 )).

cnf(s3_type12_2,axiom,
    ( q_0
    | ~ q_1
    | ~ p_2 )).

cnf(s3_type12_3,axiom,
    ( q_1
    | ~ p_2
    | ~ q_3 )).

cnf(s3_type12_4,axiom,
    ( q_1
    | ~ q_2
    | ~ p_3 )).

cnf(s3_type12_5,axiom,
    ( q_2
    | ~ p_3
    | ~ q_4 )).

cnf(s3_type12_6,axiom,
    ( q_2
    | ~ q_3
    | ~ p_4 )).

cnf(s3_type12_7,axiom,
    ( q_3
    | ~ p_4
    | ~ q_5 )).

cnf(s3_type12_8,axiom,
    ( q_3
    | ~ q_4
    | ~ p_5 )).

cnf(s3_type12_9,axiom,
    ( q_4
    | ~ p_5
    | ~ q_6 )).

cnf(s3_type12_10,axiom,
    ( q_4
    | ~ q_5
    | ~ p_6 )).

cnf(s3_type12_11,axiom,
    ( q_5
    | ~ p_6
    | ~ q_7 )).

cnf(s3_type12_12,axiom,
    ( q_5
    | ~ q_6
    | ~ p_7 )).

cnf(s3_type12_13,axiom,
    ( q_6
    | ~ p_7
    | ~ q_8 )).

cnf(s3_type12_14,axiom,
    ( q_6
    | ~ q_7
    | ~ p_8 )).

cnf(s3_type12_15,axiom,
    ( q_7
    | ~ p_8
    | ~ q_9 )).

cnf(s3_type12_16,axiom,
    ( q_7
    | ~ q_8
    | ~ p_9 )).

cnf(s3_type12_17,axiom,
    ( q_8
    | ~ p_9
    | ~ q_10 )).

cnf(s3_type12_18,axiom,
    ( q_8
    | ~ q_9
    | ~ p_10 )).

cnf(s3_type12_19,axiom,
    ( q_9
    | ~ p_10
    | ~ q_11 )).

cnf(s3_type12_20,axiom,
    ( q_9
    | ~ q_10
    | ~ p_11 )).

cnf(s3_type12_21,axiom,
    ( q_10
    | ~ p_11
    | ~ q_12 )).

cnf(s3_type12_22,axiom,
    ( q_10
    | ~ q_11
    | ~ p_12 )).

cnf(s3_type12_23,axiom,
    ( q_11
    | ~ p_12
    | ~ q_13 )).

cnf(s3_type12_24,axiom,
    ( q_11
    | ~ q_12
    | ~ p_13 )).

cnf(s3_type12_25,axiom,
    ( q_12
    | ~ p_13
    | ~ q_14 )).

cnf(s3_type12_26,axiom,
    ( q_12
    | ~ q_13
    | ~ p_14 )).

cnf(s3_type12_27,axiom,
    ( q_13
    | ~ p_14
    | ~ q_15 )).

cnf(s3_type12_28,axiom,
    ( q_13
    | ~ q_14
    | ~ p_15 )).

cnf(s3_type2_1,axiom,
    ( p_15
    | ~ p_7 )).

cnf(s3_type2_2,axiom,
    ( p_16
    | ~ p_8 )).

cnf(s3_type2_3,axiom,
    ( q_15
    | ~ q_7 )).

cnf(s3_type2_4,axiom,
    ( q_16
    | ~ q_8 )).

cnf(t3_1,axiom,
    ( p_14 )).

cnf(t3_2,axiom,
    ( p_15 )).

cnf(t3_3,axiom,
    ( q_14 )).

cnf(t3_4,axiom,
    ( q_15 )).

%--------------------------------------------------------------------------
