%--------------------------------------------------------------------------
% File     : SYN098-1.002 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem sym(u(t(3,2)))
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : Sym(U(T3n)) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.33 v5.5.0, 0.00 v4.1.0, 0.20 v4.0.1, 0.00 v2.1.0
% Syntax   : Number of clauses     :   68 (  25 non-Horn;   2 unit;  68 RR)
%            Number of atoms       :  176 (   0 equality)
%            Maximal clause size   :    4 (   3 average)
%            Number of predicates  :   54 (  54 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s2 SYN098-1.g
% Bugfixes : v1.2.0 - Bugfix in SYN086-1.
%          : v1.2.1 - Bugfix in SYN086-1.
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

cnf(u_s3_type12_1,axiom,
    ( u_6
    | q_0
    | ~ p_1
    | ~ q_2 )).

cnf(u_6_s3_type12_1,axiom,
    ( q_0
    | ~ u_6 )).

cnf(u_s3_type12_2,axiom,
    ( u_7
    | q_0
    | ~ q_1
    | ~ p_2 )).

cnf(u_7_s3_type12_2,axiom,
    ( q_0
    | ~ u_7 )).

cnf(u_s3_type12_3,axiom,
    ( u_8
    | q_1
    | ~ p_2
    | ~ q_3 )).

cnf(u_8_s3_type12_3,axiom,
    ( q_1
    | ~ u_8 )).

cnf(u_s3_type12_4,axiom,
    ( u_9
    | q_1
    | ~ q_2
    | ~ p_3 )).

cnf(u_9_s3_type12_4,axiom,
    ( q_1
    | ~ u_9 )).

cnf(u_s3_type2_1,axiom,
    ( u_10
    | p_3
    | ~ p_1 )).

cnf(u_10_s3_type2_1,axiom,
    ( p_3
    | ~ u_10 )).

cnf(u_s3_type2_2,axiom,
    ( u_11
    | p_4
    | ~ p_2 )).

cnf(u_11_s3_type2_2,axiom,
    ( p_4
    | ~ u_11 )).

cnf(u_s3_type2_3,axiom,
    ( u_12
    | q_3
    | ~ q_1 )).

cnf(u_12_s3_type2_3,axiom,
    ( q_3
    | ~ u_12 )).

cnf(u_s3_type2_4,axiom,
    ( u_13
    | q_4
    | ~ q_2 )).

cnf(u_13_s3_type2_4,axiom,
    ( q_4
    | ~ u_13 )).

cnf(u_t3_1,axiom,
    ( u_14
    | p_2 )).

cnf(u_14_t3_1,axiom,
    ( p_2
    | ~ u_14 )).

cnf(u_t3_2,axiom,
    ( u_15
    | p_3 )).

cnf(u_15_t3_2,axiom,
    ( p_3
    | ~ u_15 )).

cnf(u_t3_3,axiom,
    ( u_16
    | q_2 )).

cnf(u_16_t3_3,axiom,
    ( q_2
    | ~ u_16 )).

cnf(u_t3_4,axiom,
    ( u_17
    | q_3 )).

cnf(u_17_t3_4,axiom,
    ( q_3
    | ~ u_17 )).

cnf(sym_u_s3_goal_1,axiom,
    ( ~ sym_u_1
    | sym_p_0
    | sym_q_0 )).

cnf(sym_u_1_s3_goal_1,axiom,
    ( sym_u_1 )).

cnf(sym_u_s3_type11_1,axiom,
    ( ~ sym_u_2
    | ~ sym_p_0
    | sym_p_1
    | sym_p_2 )).

cnf(sym_u_2_s3_type11_1,axiom,
    ( ~ sym_p_0
    | sym_u_2 )).

cnf(sym_u_s3_type11_2,axiom,
    ( ~ sym_u_3
    | ~ sym_p_0
    | sym_q_1
    | sym_q_2 )).

cnf(sym_u_3_s3_type11_2,axiom,
    ( ~ sym_p_0
    | sym_u_3 )).

cnf(sym_u_s3_type11_3,axiom,
    ( ~ sym_u_4
    | ~ sym_p_1
    | sym_p_2
    | sym_p_3 )).

cnf(sym_u_4_s3_type11_3,axiom,
    ( ~ sym_p_1
    | sym_u_4 )).

cnf(sym_u_s3_type11_4,axiom,
    ( ~ sym_u_5
    | ~ sym_p_1
    | sym_q_2
    | sym_q_3 )).

cnf(sym_u_5_s3_type11_4,axiom,
    ( ~ sym_p_1
    | sym_u_5 )).

cnf(sym_u_s3_type12_1,axiom,
    ( ~ sym_u_6
    | ~ sym_q_0
    | sym_p_1
    | sym_q_2 )).

cnf(sym_u_6_s3_type12_1,axiom,
    ( ~ sym_q_0
    | sym_u_6 )).

cnf(sym_u_s3_type12_2,axiom,
    ( ~ sym_u_7
    | ~ sym_q_0
    | sym_q_1
    | sym_p_2 )).

cnf(sym_u_7_s3_type12_2,axiom,
    ( ~ sym_q_0
    | sym_u_7 )).

cnf(sym_u_s3_type12_3,axiom,
    ( ~ sym_u_8
    | ~ sym_q_1
    | sym_p_2
    | sym_q_3 )).

cnf(sym_u_8_s3_type12_3,axiom,
    ( ~ sym_q_1
    | sym_u_8 )).

cnf(sym_u_s3_type12_4,axiom,
    ( ~ sym_u_9
    | ~ sym_q_1
    | sym_q_2
    | sym_p_3 )).

cnf(sym_u_9_s3_type12_4,axiom,
    ( ~ sym_q_1
    | sym_u_9 )).

cnf(sym_u_s3_type2_1,axiom,
    ( ~ sym_u_10
    | ~ sym_p_3
    | sym_p_1 )).

cnf(sym_u_10_s3_type2_1,axiom,
    ( ~ sym_p_3
    | sym_u_10 )).

cnf(sym_u_s3_type2_2,axiom,
    ( ~ sym_u_11
    | ~ sym_p_4
    | sym_p_2 )).

cnf(sym_u_11_s3_type2_2,axiom,
    ( ~ sym_p_4
    | sym_u_11 )).

cnf(sym_u_s3_type2_3,axiom,
    ( ~ sym_u_12
    | ~ sym_q_3
    | sym_q_1 )).

cnf(sym_u_12_s3_type2_3,axiom,
    ( ~ sym_q_3
    | sym_u_12 )).

cnf(sym_u_s3_type2_4,axiom,
    ( ~ sym_u_13
    | ~ sym_q_4
    | sym_q_2 )).

cnf(sym_u_13_s3_type2_4,axiom,
    ( ~ sym_q_4
    | sym_u_13 )).

cnf(sym_u_t3_1,axiom,
    ( ~ sym_u_14
    | ~ sym_p_2 )).

cnf(sym_u_14_t3_1,axiom,
    ( ~ sym_p_2
    | sym_u_14 )).

cnf(sym_u_t3_2,axiom,
    ( ~ sym_u_15
    | ~ sym_p_3 )).

cnf(sym_u_15_t3_2,axiom,
    ( ~ sym_p_3
    | sym_u_15 )).

cnf(sym_u_t3_3,axiom,
    ( ~ sym_u_16
    | ~ sym_q_2 )).

cnf(sym_u_16_t3_3,axiom,
    ( ~ sym_q_2
    | sym_u_16 )).

cnf(sym_u_t3_4,axiom,
    ( ~ sym_u_17
    | ~ sym_q_3 )).

cnf(sym_u_17_t3_4,axiom,
    ( ~ sym_q_3
    | sym_u_17 )).

%--------------------------------------------------------------------------
