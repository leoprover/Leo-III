%--------------------------------------------------------------------------
% File     : SYN100-1.005 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem sym(m(t(3,5)))
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : Sym(M(T3n)) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   82 (  33 non-Horn;   8 unit;  81 RR)
%            Number of atoms       :  220 (   0 equality)
%            Maximal clause size   :    3 (   3 average)
%            Number of predicates  :   44 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :   74 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s5 SYN100-1.g
% Bugfixes : v1.2.0 - Bugfix in SYN086-1.
%          : v1.2.1 - Bugfix in SYN086-1.
%--------------------------------------------------------------------------
cnf(m_s3_goal_1,negated_conjecture,
    ( ~ p_0(X)
    | ~ q_0(X) )).

cnf(m_s3_type11_1,axiom,
    ( p_0(X)
    | ~ p_1(X)
    | ~ p_2(X) )).

cnf(m_s3_type11_2,axiom,
    ( p_0(X)
    | ~ q_1(X)
    | ~ q_2(X) )).

cnf(m_s3_type11_3,axiom,
    ( p_1(X)
    | ~ p_2(X)
    | ~ p_3(X) )).

cnf(m_s3_type11_4,axiom,
    ( p_1(X)
    | ~ q_2(X)
    | ~ q_3(X) )).

cnf(m_s3_type11_5,axiom,
    ( p_2(X)
    | ~ p_3(X)
    | ~ p_4(X) )).

cnf(m_s3_type11_6,axiom,
    ( p_2(X)
    | ~ q_3(X)
    | ~ q_4(X) )).

cnf(m_s3_type11_7,axiom,
    ( p_3(X)
    | ~ p_4(X)
    | ~ p_5(X) )).

cnf(m_s3_type11_8,axiom,
    ( p_3(X)
    | ~ q_4(X)
    | ~ q_5(X) )).

cnf(m_s3_type11_9,axiom,
    ( p_4(X)
    | ~ p_5(X)
    | ~ p_6(X) )).

cnf(m_s3_type11_10,axiom,
    ( p_4(X)
    | ~ q_5(X)
    | ~ q_6(X) )).

cnf(m_s3_type11_11,axiom,
    ( p_5(X)
    | ~ p_6(X)
    | ~ p_7(X) )).

cnf(m_s3_type11_12,axiom,
    ( p_5(X)
    | ~ q_6(X)
    | ~ q_7(X) )).

cnf(m_s3_type11_13,axiom,
    ( p_6(X)
    | ~ p_7(X)
    | ~ p_8(X) )).

cnf(m_s3_type11_14,axiom,
    ( p_6(X)
    | ~ q_7(X)
    | ~ q_8(X) )).

cnf(m_s3_type11_15,axiom,
    ( p_7(X)
    | ~ p_8(X)
    | ~ p_9(X) )).

cnf(m_s3_type11_16,axiom,
    ( p_7(X)
    | ~ q_8(X)
    | ~ q_9(X) )).

cnf(m_s3_type12_1,axiom,
    ( q_0(X)
    | ~ p_1(X)
    | ~ q_2(X) )).

cnf(m_s3_type12_2,axiom,
    ( q_0(X)
    | ~ q_1(X)
    | ~ p_2(X) )).

cnf(m_s3_type12_3,axiom,
    ( q_1(X)
    | ~ p_2(X)
    | ~ q_3(X) )).

cnf(m_s3_type12_4,axiom,
    ( q_1(X)
    | ~ q_2(X)
    | ~ p_3(X) )).

cnf(m_s3_type12_5,axiom,
    ( q_2(X)
    | ~ p_3(X)
    | ~ q_4(X) )).

cnf(m_s3_type12_6,axiom,
    ( q_2(X)
    | ~ q_3(X)
    | ~ p_4(X) )).

cnf(m_s3_type12_7,axiom,
    ( q_3(X)
    | ~ p_4(X)
    | ~ q_5(X) )).

cnf(m_s3_type12_8,axiom,
    ( q_3(X)
    | ~ q_4(X)
    | ~ p_5(X) )).

cnf(m_s3_type12_9,axiom,
    ( q_4(X)
    | ~ p_5(X)
    | ~ q_6(X) )).

cnf(m_s3_type12_10,axiom,
    ( q_4(X)
    | ~ q_5(X)
    | ~ p_6(X) )).

cnf(m_s3_type12_11,axiom,
    ( q_5(X)
    | ~ p_6(X)
    | ~ q_7(X) )).

cnf(m_s3_type12_12,axiom,
    ( q_5(X)
    | ~ q_6(X)
    | ~ p_7(X) )).

cnf(m_s3_type12_13,axiom,
    ( q_6(X)
    | ~ p_7(X)
    | ~ q_8(X) )).

cnf(m_s3_type12_14,axiom,
    ( q_6(X)
    | ~ q_7(X)
    | ~ p_8(X) )).

cnf(m_s3_type12_15,axiom,
    ( q_7(X)
    | ~ p_8(X)
    | ~ q_9(X) )).

cnf(m_s3_type12_16,axiom,
    ( q_7(X)
    | ~ q_8(X)
    | ~ p_9(X) )).

cnf(m_s3_type2_1,axiom,
    ( p_9(X)
    | ~ p_4(X) )).

cnf(m_s3_type2_2,axiom,
    ( p_10(X)
    | ~ p_5(X) )).

cnf(m_s3_type2_3,axiom,
    ( q_9(X)
    | ~ q_4(X) )).

cnf(m_s3_type2_4,axiom,
    ( q_10(X)
    | ~ q_5(X) )).

cnf(m_t3_1,axiom,
    ( p_8(a) )).

cnf(m_t3_2,axiom,
    ( p_9(a) )).

cnf(m_t3_3,axiom,
    ( q_8(a) )).

cnf(m_t3_4,axiom,
    ( q_9(a) )).

cnf(sym_m_s3_goal_1,axiom,
    ( sym_p_0(X)
    | sym_q_0(X) )).

cnf(sym_m_s3_type11_1,axiom,
    ( ~ sym_p_0(X)
    | sym_p_1(X)
    | sym_p_2(X) )).

cnf(sym_m_s3_type11_2,axiom,
    ( ~ sym_p_0(X)
    | sym_q_1(X)
    | sym_q_2(X) )).

cnf(sym_m_s3_type11_3,axiom,
    ( ~ sym_p_1(X)
    | sym_p_2(X)
    | sym_p_3(X) )).

cnf(sym_m_s3_type11_4,axiom,
    ( ~ sym_p_1(X)
    | sym_q_2(X)
    | sym_q_3(X) )).

cnf(sym_m_s3_type11_5,axiom,
    ( ~ sym_p_2(X)
    | sym_p_3(X)
    | sym_p_4(X) )).

cnf(sym_m_s3_type11_6,axiom,
    ( ~ sym_p_2(X)
    | sym_q_3(X)
    | sym_q_4(X) )).

cnf(sym_m_s3_type11_7,axiom,
    ( ~ sym_p_3(X)
    | sym_p_4(X)
    | sym_p_5(X) )).

cnf(sym_m_s3_type11_8,axiom,
    ( ~ sym_p_3(X)
    | sym_q_4(X)
    | sym_q_5(X) )).

cnf(sym_m_s3_type11_9,axiom,
    ( ~ sym_p_4(X)
    | sym_p_5(X)
    | sym_p_6(X) )).

cnf(sym_m_s3_type11_10,axiom,
    ( ~ sym_p_4(X)
    | sym_q_5(X)
    | sym_q_6(X) )).

cnf(sym_m_s3_type11_11,axiom,
    ( ~ sym_p_5(X)
    | sym_p_6(X)
    | sym_p_7(X) )).

cnf(sym_m_s3_type11_12,axiom,
    ( ~ sym_p_5(X)
    | sym_q_6(X)
    | sym_q_7(X) )).

cnf(sym_m_s3_type11_13,axiom,
    ( ~ sym_p_6(X)
    | sym_p_7(X)
    | sym_p_8(X) )).

cnf(sym_m_s3_type11_14,axiom,
    ( ~ sym_p_6(X)
    | sym_q_7(X)
    | sym_q_8(X) )).

cnf(sym_m_s3_type11_15,axiom,
    ( ~ sym_p_7(X)
    | sym_p_8(X)
    | sym_p_9(X) )).

cnf(sym_m_s3_type11_16,axiom,
    ( ~ sym_p_7(X)
    | sym_q_8(X)
    | sym_q_9(X) )).

cnf(sym_m_s3_type12_1,axiom,
    ( ~ sym_q_0(X)
    | sym_p_1(X)
    | sym_q_2(X) )).

cnf(sym_m_s3_type12_2,axiom,
    ( ~ sym_q_0(X)
    | sym_q_1(X)
    | sym_p_2(X) )).

cnf(sym_m_s3_type12_3,axiom,
    ( ~ sym_q_1(X)
    | sym_p_2(X)
    | sym_q_3(X) )).

cnf(sym_m_s3_type12_4,axiom,
    ( ~ sym_q_1(X)
    | sym_q_2(X)
    | sym_p_3(X) )).

cnf(sym_m_s3_type12_5,axiom,
    ( ~ sym_q_2(X)
    | sym_p_3(X)
    | sym_q_4(X) )).

cnf(sym_m_s3_type12_6,axiom,
    ( ~ sym_q_2(X)
    | sym_q_3(X)
    | sym_p_4(X) )).

cnf(sym_m_s3_type12_7,axiom,
    ( ~ sym_q_3(X)
    | sym_p_4(X)
    | sym_q_5(X) )).

cnf(sym_m_s3_type12_8,axiom,
    ( ~ sym_q_3(X)
    | sym_q_4(X)
    | sym_p_5(X) )).

cnf(sym_m_s3_type12_9,axiom,
    ( ~ sym_q_4(X)
    | sym_p_5(X)
    | sym_q_6(X) )).

cnf(sym_m_s3_type12_10,axiom,
    ( ~ sym_q_4(X)
    | sym_q_5(X)
    | sym_p_6(X) )).

cnf(sym_m_s3_type12_11,axiom,
    ( ~ sym_q_5(X)
    | sym_p_6(X)
    | sym_q_7(X) )).

cnf(sym_m_s3_type12_12,axiom,
    ( ~ sym_q_5(X)
    | sym_q_6(X)
    | sym_p_7(X) )).

cnf(sym_m_s3_type12_13,axiom,
    ( ~ sym_q_6(X)
    | sym_p_7(X)
    | sym_q_8(X) )).

cnf(sym_m_s3_type12_14,axiom,
    ( ~ sym_q_6(X)
    | sym_q_7(X)
    | sym_p_8(X) )).

cnf(sym_m_s3_type12_15,axiom,
    ( ~ sym_q_7(X)
    | sym_p_8(X)
    | sym_q_9(X) )).

cnf(sym_m_s3_type12_16,axiom,
    ( ~ sym_q_7(X)
    | sym_q_8(X)
    | sym_p_9(X) )).

cnf(sym_m_s3_type2_1,axiom,
    ( ~ sym_p_9(X)
    | sym_p_4(X) )).

cnf(sym_m_s3_type2_2,axiom,
    ( ~ sym_p_10(X)
    | sym_p_5(X) )).

cnf(sym_m_s3_type2_3,axiom,
    ( ~ sym_q_9(X)
    | sym_q_4(X) )).

cnf(sym_m_s3_type2_4,axiom,
    ( ~ sym_q_10(X)
    | sym_q_5(X) )).

cnf(sym_m_t3_1,axiom,
    ( ~ sym_p_8(a) )).

cnf(sym_m_t3_2,axiom,
    ( ~ sym_p_9(a) )).

cnf(sym_m_t3_3,axiom,
    ( ~ sym_q_8(a) )).

cnf(sym_m_t3_4,axiom,
    ( ~ sym_q_9(a) )).

%--------------------------------------------------------------------------
