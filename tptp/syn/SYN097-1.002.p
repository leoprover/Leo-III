%--------------------------------------------------------------------------
% File     : SYN097-1.002 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem sym(u(t(2,2)))
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : Sym(U(T2n)) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.33 v5.5.0, 0.00 v2.1.0
% Syntax   : Number of clauses     :   52 (  16 non-Horn;   2 unit;  52 RR)
%            Number of atoms       :  126 (   0 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :   38 (  38 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s2 SYN097-1.g
% Bugfixes : v1.2.0 - Bugfix in SYN086-1.
%          : v1.2.1 - Bugfix in SYN086-1.
%--------------------------------------------------------------------------
cnf(u_s2_goal_1,negated_conjecture,
    ( u_1
    | ~ p_1_2 )).

cnf(u_1_s2_goal_1,axiom,
    ( ~ u_1 )).

cnf(u_s2_type11_1,axiom,
    ( u_2
    | p_1_2
    | ~ p_2_2
    | ~ p_1_1 )).

cnf(u_2_s2_type11_1,axiom,
    ( p_1_2
    | ~ u_2 )).

cnf(u_s2_type11_2,axiom,
    ( u_3
    | p_1_2
    | ~ q_2_2
    | ~ q_1_1 )).

cnf(u_3_s2_type11_2,axiom,
    ( p_1_2
    | ~ u_3 )).

cnf(u_s2_type12_1,axiom,
    ( u_4
    | q_1_2
    | ~ p_2_2
    | ~ q_1_1 )).

cnf(u_4_s2_type12_1,axiom,
    ( q_1_2
    | ~ u_4 )).

cnf(u_s2_type12_2,axiom,
    ( u_5
    | q_1_2
    | ~ q_2_2
    | ~ p_1_1 )).

cnf(u_5_s2_type12_2,axiom,
    ( q_1_2
    | ~ u_5 )).

cnf(u_s2_type21_1,axiom,
    ( u_6
    | p_1_1
    | ~ p_1_2 )).

cnf(u_6_s2_type21_1,axiom,
    ( p_1_1
    | ~ u_6 )).

cnf(u_s2_type21_2,axiom,
    ( u_7
    | q_1_1
    | ~ q_1_2 )).

cnf(u_7_s2_type21_2,axiom,
    ( q_1_1
    | ~ u_7 )).

cnf(u_s2_type22_1,axiom,
    ( u_8
    | p_2_2
    | ~ p_1_2 )).

cnf(u_8_s2_type22_1,axiom,
    ( p_2_2
    | ~ u_8 )).

cnf(u_s2_type22_2,axiom,
    ( u_9
    | q_2_2
    | ~ q_1_2 )).

cnf(u_9_s2_type22_2,axiom,
    ( q_2_2
    | ~ u_9 )).

cnf(u_t2_1,axiom,
    ( u_10
    | p_1_1 )).

cnf(u_10_t2_1,axiom,
    ( p_1_1
    | ~ u_10 )).

cnf(u_t2_2,axiom,
    ( u_11
    | q_1_1 )).

cnf(u_11_t2_2,axiom,
    ( q_1_1
    | ~ u_11 )).

cnf(u_t2_3,axiom,
    ( u_12
    | p_2_2 )).

cnf(u_12_t2_3,axiom,
    ( p_2_2
    | ~ u_12 )).

cnf(u_t2_4,axiom,
    ( u_13
    | q_2_2 )).

cnf(u_13_t2_4,axiom,
    ( q_2_2
    | ~ u_13 )).

cnf(sym_u_s2_goal_1,axiom,
    ( ~ sym_u_1
    | sym_p_1_2 )).

cnf(sym_u_1_s2_goal_1,axiom,
    ( sym_u_1 )).

cnf(sym_u_s2_type11_1,axiom,
    ( ~ sym_u_2
    | ~ sym_p_1_2
    | sym_p_2_2
    | sym_p_1_1 )).

cnf(sym_u_2_s2_type11_1,axiom,
    ( ~ sym_p_1_2
    | sym_u_2 )).

cnf(sym_u_s2_type11_2,axiom,
    ( ~ sym_u_3
    | ~ sym_p_1_2
    | sym_q_2_2
    | sym_q_1_1 )).

cnf(sym_u_3_s2_type11_2,axiom,
    ( ~ sym_p_1_2
    | sym_u_3 )).

cnf(sym_u_s2_type12_1,axiom,
    ( ~ sym_u_4
    | ~ sym_q_1_2
    | sym_p_2_2
    | sym_q_1_1 )).

cnf(sym_u_4_s2_type12_1,axiom,
    ( ~ sym_q_1_2
    | sym_u_4 )).

cnf(sym_u_s2_type12_2,axiom,
    ( ~ sym_u_5
    | ~ sym_q_1_2
    | sym_q_2_2
    | sym_p_1_1 )).

cnf(sym_u_5_s2_type12_2,axiom,
    ( ~ sym_q_1_2
    | sym_u_5 )).

cnf(sym_u_s2_type21_1,axiom,
    ( ~ sym_u_6
    | ~ sym_p_1_1
    | sym_p_1_2 )).

cnf(sym_u_6_s2_type21_1,axiom,
    ( ~ sym_p_1_1
    | sym_u_6 )).

cnf(sym_u_s2_type21_2,axiom,
    ( ~ sym_u_7
    | ~ sym_q_1_1
    | sym_q_1_2 )).

cnf(sym_u_7_s2_type21_2,axiom,
    ( ~ sym_q_1_1
    | sym_u_7 )).

cnf(sym_u_s2_type22_1,axiom,
    ( ~ sym_u_8
    | ~ sym_p_2_2
    | sym_p_1_2 )).

cnf(sym_u_8_s2_type22_1,axiom,
    ( ~ sym_p_2_2
    | sym_u_8 )).

cnf(sym_u_s2_type22_2,axiom,
    ( ~ sym_u_9
    | ~ sym_q_2_2
    | sym_q_1_2 )).

cnf(sym_u_9_s2_type22_2,axiom,
    ( ~ sym_q_2_2
    | sym_u_9 )).

cnf(sym_u_t2_1,axiom,
    ( ~ sym_u_10
    | ~ sym_p_1_1 )).

cnf(sym_u_10_t2_1,axiom,
    ( ~ sym_p_1_1
    | sym_u_10 )).

cnf(sym_u_t2_2,axiom,
    ( ~ sym_u_11
    | ~ sym_q_1_1 )).

cnf(sym_u_11_t2_2,axiom,
    ( ~ sym_q_1_1
    | sym_u_11 )).

cnf(sym_u_t2_3,axiom,
    ( ~ sym_u_12
    | ~ sym_p_2_2 )).

cnf(sym_u_12_t2_3,axiom,
    ( ~ sym_p_2_2
    | sym_u_12 )).

cnf(sym_u_t2_4,axiom,
    ( ~ sym_u_13
    | ~ sym_q_2_2 )).

cnf(sym_u_13_t2_4,axiom,
    ( ~ sym_q_2_2
    | sym_u_13 )).

%--------------------------------------------------------------------------
