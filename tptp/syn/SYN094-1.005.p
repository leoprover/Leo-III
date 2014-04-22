%--------------------------------------------------------------------------
% File     : SYN094-1.005 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem u(t(3,5))
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : U(T3n) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.33 v5.5.0, 0.00 v2.1.0
% Syntax   : Number of clauses     :   82 (  40 non-Horn;   1 unit;  82 RR)
%            Number of atoms       :  232 (   0 equality)
%            Maximal clause size   :    4 (   3 average)
%            Number of predicates  :   63 (  63 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s5 SYN094-1.g
% Bugfixes : v1.2.1 - Bugfix in SYN087-1.
%--------------------------------------------------------------------------
cnf(u_s3_goal_1,negated_conjecture,
    ( u_1
    | ~ p_0
    | ~ q_0 )).

cnf(u_1_s3_goal_1,axiom,
    ( ~ u_1 )).

cnf(u_s3_type11_1,axiom,
    ( u_2
    | p_0
    | ~ p_1
    | ~ p_2 )).

cnf(u_2_s3_type11_1,axiom,
    ( p_0
    | ~ u_2 )).

cnf(u_s3_type11_2,axiom,
    ( u_3
    | p_0
    | ~ q_1
    | ~ q_2 )).

cnf(u_3_s3_type11_2,axiom,
    ( p_0
    | ~ u_3 )).

cnf(u_s3_type11_3,axiom,
    ( u_4
    | p_1
    | ~ p_2
    | ~ p_3 )).

cnf(u_4_s3_type11_3,axiom,
    ( p_1
    | ~ u_4 )).

cnf(u_s3_type11_4,axiom,
    ( u_5
    | p_1
    | ~ q_2
    | ~ q_3 )).

cnf(u_5_s3_type11_4,axiom,
    ( p_1
    | ~ u_5 )).

cnf(u_s3_type11_5,axiom,
    ( u_6
    | p_2
    | ~ p_3
    | ~ p_4 )).

cnf(u_6_s3_type11_5,axiom,
    ( p_2
    | ~ u_6 )).

cnf(u_s3_type11_6,axiom,
    ( u_7
    | p_2
    | ~ q_3
    | ~ q_4 )).

cnf(u_7_s3_type11_6,axiom,
    ( p_2
    | ~ u_7 )).

cnf(u_s3_type11_7,axiom,
    ( u_8
    | p_3
    | ~ p_4
    | ~ p_5 )).

cnf(u_8_s3_type11_7,axiom,
    ( p_3
    | ~ u_8 )).

cnf(u_s3_type11_8,axiom,
    ( u_9
    | p_3
    | ~ q_4
    | ~ q_5 )).

cnf(u_9_s3_type11_8,axiom,
    ( p_3
    | ~ u_9 )).

cnf(u_s3_type11_9,axiom,
    ( u_10
    | p_4
    | ~ p_5
    | ~ p_6 )).

cnf(u_10_s3_type11_9,axiom,
    ( p_4
    | ~ u_10 )).

cnf(u_s3_type11_10,axiom,
    ( u_11
    | p_4
    | ~ q_5
    | ~ q_6 )).

cnf(u_11_s3_type11_10,axiom,
    ( p_4
    | ~ u_11 )).

cnf(u_s3_type11_11,axiom,
    ( u_12
    | p_5
    | ~ p_6
    | ~ p_7 )).

cnf(u_12_s3_type11_11,axiom,
    ( p_5
    | ~ u_12 )).

cnf(u_s3_type11_12,axiom,
    ( u_13
    | p_5
    | ~ q_6
    | ~ q_7 )).

cnf(u_13_s3_type11_12,axiom,
    ( p_5
    | ~ u_13 )).

cnf(u_s3_type11_13,axiom,
    ( u_14
    | p_6
    | ~ p_7
    | ~ p_8 )).

cnf(u_14_s3_type11_13,axiom,
    ( p_6
    | ~ u_14 )).

cnf(u_s3_type11_14,axiom,
    ( u_15
    | p_6
    | ~ q_7
    | ~ q_8 )).

cnf(u_15_s3_type11_14,axiom,
    ( p_6
    | ~ u_15 )).

cnf(u_s3_type11_15,axiom,
    ( u_16
    | p_7
    | ~ p_8
    | ~ p_9 )).

cnf(u_16_s3_type11_15,axiom,
    ( p_7
    | ~ u_16 )).

cnf(u_s3_type11_16,axiom,
    ( u_17
    | p_7
    | ~ q_8
    | ~ q_9 )).

cnf(u_17_s3_type11_16,axiom,
    ( p_7
    | ~ u_17 )).

cnf(u_s3_type12_1,axiom,
    ( u_18
    | q_0
    | ~ p_1
    | ~ q_2 )).

cnf(u_18_s3_type12_1,axiom,
    ( q_0
    | ~ u_18 )).

cnf(u_s3_type12_2,axiom,
    ( u_19
    | q_0
    | ~ q_1
    | ~ p_2 )).

cnf(u_19_s3_type12_2,axiom,
    ( q_0
    | ~ u_19 )).

cnf(u_s3_type12_3,axiom,
    ( u_20
    | q_1
    | ~ p_2
    | ~ q_3 )).

cnf(u_20_s3_type12_3,axiom,
    ( q_1
    | ~ u_20 )).

cnf(u_s3_type12_4,axiom,
    ( u_21
    | q_1
    | ~ q_2
    | ~ p_3 )).

cnf(u_21_s3_type12_4,axiom,
    ( q_1
    | ~ u_21 )).

cnf(u_s3_type12_5,axiom,
    ( u_22
    | q_2
    | ~ p_3
    | ~ q_4 )).

cnf(u_22_s3_type12_5,axiom,
    ( q_2
    | ~ u_22 )).

cnf(u_s3_type12_6,axiom,
    ( u_23
    | q_2
    | ~ q_3
    | ~ p_4 )).

cnf(u_23_s3_type12_6,axiom,
    ( q_2
    | ~ u_23 )).

cnf(u_s3_type12_7,axiom,
    ( u_24
    | q_3
    | ~ p_4
    | ~ q_5 )).

cnf(u_24_s3_type12_7,axiom,
    ( q_3
    | ~ u_24 )).

cnf(u_s3_type12_8,axiom,
    ( u_25
    | q_3
    | ~ q_4
    | ~ p_5 )).

cnf(u_25_s3_type12_8,axiom,
    ( q_3
    | ~ u_25 )).

cnf(u_s3_type12_9,axiom,
    ( u_26
    | q_4
    | ~ p_5
    | ~ q_6 )).

cnf(u_26_s3_type12_9,axiom,
    ( q_4
    | ~ u_26 )).

cnf(u_s3_type12_10,axiom,
    ( u_27
    | q_4
    | ~ q_5
    | ~ p_6 )).

cnf(u_27_s3_type12_10,axiom,
    ( q_4
    | ~ u_27 )).

cnf(u_s3_type12_11,axiom,
    ( u_28
    | q_5
    | ~ p_6
    | ~ q_7 )).

cnf(u_28_s3_type12_11,axiom,
    ( q_5
    | ~ u_28 )).

cnf(u_s3_type12_12,axiom,
    ( u_29
    | q_5
    | ~ q_6
    | ~ p_7 )).

cnf(u_29_s3_type12_12,axiom,
    ( q_5
    | ~ u_29 )).

cnf(u_s3_type12_13,axiom,
    ( u_30
    | q_6
    | ~ p_7
    | ~ q_8 )).

cnf(u_30_s3_type12_13,axiom,
    ( q_6
    | ~ u_30 )).

cnf(u_s3_type12_14,axiom,
    ( u_31
    | q_6
    | ~ q_7
    | ~ p_8 )).

cnf(u_31_s3_type12_14,axiom,
    ( q_6
    | ~ u_31 )).

cnf(u_s3_type12_15,axiom,
    ( u_32
    | q_7
    | ~ p_8
    | ~ q_9 )).

cnf(u_32_s3_type12_15,axiom,
    ( q_7
    | ~ u_32 )).

cnf(u_s3_type12_16,axiom,
    ( u_33
    | q_7
    | ~ q_8
    | ~ p_9 )).

cnf(u_33_s3_type12_16,axiom,
    ( q_7
    | ~ u_33 )).

cnf(u_s3_type2_1,axiom,
    ( u_34
    | p_9
    | ~ p_4 )).

cnf(u_34_s3_type2_1,axiom,
    ( p_9
    | ~ u_34 )).

cnf(u_s3_type2_2,axiom,
    ( u_35
    | p_10
    | ~ p_5 )).

cnf(u_35_s3_type2_2,axiom,
    ( p_10
    | ~ u_35 )).

cnf(u_s3_type2_3,axiom,
    ( u_36
    | q_9
    | ~ q_4 )).

cnf(u_36_s3_type2_3,axiom,
    ( q_9
    | ~ u_36 )).

cnf(u_s3_type2_4,axiom,
    ( u_37
    | q_10
    | ~ q_5 )).

cnf(u_37_s3_type2_4,axiom,
    ( q_10
    | ~ u_37 )).

cnf(u_t3_1,axiom,
    ( u_38
    | p_8 )).

cnf(u_38_t3_1,axiom,
    ( p_8
    | ~ u_38 )).

cnf(u_t3_2,axiom,
    ( u_39
    | p_9 )).

cnf(u_39_t3_2,axiom,
    ( p_9
    | ~ u_39 )).

cnf(u_t3_3,axiom,
    ( u_40
    | q_8 )).

cnf(u_40_t3_3,axiom,
    ( q_8
    | ~ u_40 )).

cnf(u_t3_4,axiom,
    ( u_41
    | q_9 )).

cnf(u_41_t3_4,axiom,
    ( q_9
    | ~ u_41 )).

%--------------------------------------------------------------------------
