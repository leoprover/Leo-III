%--------------------------------------------------------------------------
% File     : SYN091-1.003 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem sym(s(2,3))
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : Sym(S2n) [Pla94]

% Status   : Satisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   38 (  12 non-Horn;   2 unit;  38 RR)
%            Number of atoms       :   98 (   0 equality)
%            Maximal clause size   :    3 (   3 average)
%            Number of predicates  :   24 (  24 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_SAT_PRP

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s3 SYN091-1.g
% Bugfixes : v1.2.0 - Bugfix in SYN086-1.
%          : v1.2.1 - Bugfix in SYN086-1.
%--------------------------------------------------------------------------
cnf(s2_goal_1,negated_conjecture,
    ( ~ p_1_3 )).

cnf(s2_type11_1,axiom,
    ( p_1_2
    | ~ p_2_2
    | ~ p_1_1 )).

cnf(s2_type11_2,axiom,
    ( p_1_2
    | ~ q_2_2
    | ~ q_1_1 )).

cnf(s2_type11_3,axiom,
    ( p_1_3
    | ~ p_2_3
    | ~ p_1_2 )).

cnf(s2_type11_4,axiom,
    ( p_1_3
    | ~ q_2_3
    | ~ q_1_2 )).

cnf(s2_type11_5,axiom,
    ( p_2_3
    | ~ p_3_3
    | ~ p_2_2 )).

cnf(s2_type11_6,axiom,
    ( p_2_3
    | ~ q_3_3
    | ~ q_2_2 )).

cnf(s2_type12_1,axiom,
    ( q_1_2
    | ~ p_2_2
    | ~ q_1_1 )).

cnf(s2_type12_2,axiom,
    ( q_1_2
    | ~ q_2_2
    | ~ p_1_1 )).

cnf(s2_type12_3,axiom,
    ( q_1_3
    | ~ p_2_3
    | ~ q_1_2 )).

cnf(s2_type12_4,axiom,
    ( q_1_3
    | ~ q_2_3
    | ~ p_1_2 )).

cnf(s2_type12_5,axiom,
    ( q_2_3
    | ~ p_3_3
    | ~ q_2_2 )).

cnf(s2_type12_6,axiom,
    ( q_2_3
    | ~ q_3_3
    | ~ p_2_2 )).

cnf(s2_type21_1,axiom,
    ( p_1_1
    | ~ p_1_2 )).

cnf(s2_type21_2,axiom,
    ( q_1_1
    | ~ q_1_2 )).

cnf(s2_type22_1,axiom,
    ( p_2_2
    | ~ p_1_2 )).

cnf(s2_type22_2,axiom,
    ( p_3_3
    | ~ p_2_3 )).

cnf(s2_type22_3,axiom,
    ( q_2_2
    | ~ q_1_2 )).

cnf(s2_type22_4,axiom,
    ( q_3_3
    | ~ q_2_3 )).

cnf(sym_s2_goal_1,axiom,
    ( sym_p_1_3 )).

cnf(sym_s2_type11_1,axiom,
    ( ~ sym_p_1_2
    | sym_p_2_2
    | sym_p_1_1 )).

cnf(sym_s2_type11_2,axiom,
    ( ~ sym_p_1_2
    | sym_q_2_2
    | sym_q_1_1 )).

cnf(sym_s2_type11_3,axiom,
    ( ~ sym_p_1_3
    | sym_p_2_3
    | sym_p_1_2 )).

cnf(sym_s2_type11_4,axiom,
    ( ~ sym_p_1_3
    | sym_q_2_3
    | sym_q_1_2 )).

cnf(sym_s2_type11_5,axiom,
    ( ~ sym_p_2_3
    | sym_p_3_3
    | sym_p_2_2 )).

cnf(sym_s2_type11_6,axiom,
    ( ~ sym_p_2_3
    | sym_q_3_3
    | sym_q_2_2 )).

cnf(sym_s2_type12_1,axiom,
    ( ~ sym_q_1_2
    | sym_p_2_2
    | sym_q_1_1 )).

cnf(sym_s2_type12_2,axiom,
    ( ~ sym_q_1_2
    | sym_q_2_2
    | sym_p_1_1 )).

cnf(sym_s2_type12_3,axiom,
    ( ~ sym_q_1_3
    | sym_p_2_3
    | sym_q_1_2 )).

cnf(sym_s2_type12_4,axiom,
    ( ~ sym_q_1_3
    | sym_q_2_3
    | sym_p_1_2 )).

cnf(sym_s2_type12_5,axiom,
    ( ~ sym_q_2_3
    | sym_p_3_3
    | sym_q_2_2 )).

cnf(sym_s2_type12_6,axiom,
    ( ~ sym_q_2_3
    | sym_q_3_3
    | sym_p_2_2 )).

cnf(sym_s2_type21_1,axiom,
    ( ~ sym_p_1_1
    | sym_p_1_2 )).

cnf(sym_s2_type21_2,axiom,
    ( ~ sym_q_1_1
    | sym_q_1_2 )).

cnf(sym_s2_type22_1,axiom,
    ( ~ sym_p_2_2
    | sym_p_1_2 )).

cnf(sym_s2_type22_2,axiom,
    ( ~ sym_p_3_3
    | sym_p_2_3 )).

cnf(sym_s2_type22_3,axiom,
    ( ~ sym_q_2_2
    | sym_q_1_2 )).

cnf(sym_s2_type22_4,axiom,
    ( ~ sym_q_3_3
    | sym_q_2_3 )).

%--------------------------------------------------------------------------
