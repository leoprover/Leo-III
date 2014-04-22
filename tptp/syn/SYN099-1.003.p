%--------------------------------------------------------------------------
% File     : SYN099-1.003 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem sym(m(t(2,3)))
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : Sym(M(T2n)) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   50 (  12 non-Horn;  14 unit;  49 RR)
%            Number of atoms       :  110 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :   24 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :   38 (   2 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s3 SYN099-1.g
% Bugfixes : v1.2.0 - Bugfix in SYN086-1.
%          : v1.2.1 - Bugfix in SYN086-1.
%--------------------------------------------------------------------------
cnf(m_s2_goal_1,negated_conjecture,
    ( ~ p_1_3(X) )).

cnf(m_s2_type11_1,axiom,
    ( p_1_2(X)
    | ~ p_2_2(X)
    | ~ p_1_1(X) )).

cnf(m_s2_type11_2,axiom,
    ( p_1_2(X)
    | ~ q_2_2(X)
    | ~ q_1_1(X) )).

cnf(m_s2_type11_3,axiom,
    ( p_1_3(X)
    | ~ p_2_3(X)
    | ~ p_1_2(X) )).

cnf(m_s2_type11_4,axiom,
    ( p_1_3(X)
    | ~ q_2_3(X)
    | ~ q_1_2(X) )).

cnf(m_s2_type11_5,axiom,
    ( p_2_3(X)
    | ~ p_3_3(X)
    | ~ p_2_2(X) )).

cnf(m_s2_type11_6,axiom,
    ( p_2_3(X)
    | ~ q_3_3(X)
    | ~ q_2_2(X) )).

cnf(m_s2_type12_1,axiom,
    ( q_1_2(X)
    | ~ p_2_2(X)
    | ~ q_1_1(X) )).

cnf(m_s2_type12_2,axiom,
    ( q_1_2(X)
    | ~ q_2_2(X)
    | ~ p_1_1(X) )).

cnf(m_s2_type12_3,axiom,
    ( q_1_3(X)
    | ~ p_2_3(X)
    | ~ q_1_2(X) )).

cnf(m_s2_type12_4,axiom,
    ( q_1_3(X)
    | ~ q_2_3(X)
    | ~ p_1_2(X) )).

cnf(m_s2_type12_5,axiom,
    ( q_2_3(X)
    | ~ p_3_3(X)
    | ~ q_2_2(X) )).

cnf(m_s2_type12_6,axiom,
    ( q_2_3(X)
    | ~ q_3_3(X)
    | ~ p_2_2(X) )).

cnf(m_s2_type21_1,axiom,
    ( p_1_1(X)
    | ~ p_1_2(X) )).

cnf(m_s2_type21_2,axiom,
    ( q_1_1(X)
    | ~ q_1_2(X) )).

cnf(m_s2_type22_1,axiom,
    ( p_2_2(X)
    | ~ p_1_2(X) )).

cnf(m_s2_type22_2,axiom,
    ( p_3_3(X)
    | ~ p_2_3(X) )).

cnf(m_s2_type22_3,axiom,
    ( q_2_2(X)
    | ~ q_1_2(X) )).

cnf(m_s2_type22_4,axiom,
    ( q_3_3(X)
    | ~ q_2_3(X) )).

cnf(m_t2_1,axiom,
    ( p_1_1(a) )).

cnf(m_t2_2,axiom,
    ( q_1_1(a) )).

cnf(m_t2_3,axiom,
    ( p_2_2(a) )).

cnf(m_t2_4,axiom,
    ( q_2_2(a) )).

cnf(m_t2_5,axiom,
    ( p_3_3(a) )).

cnf(m_t2_6,axiom,
    ( q_3_3(a) )).

cnf(sym_m_s2_goal_1,axiom,
    ( sym_p_1_3(X) )).

cnf(sym_m_s2_type11_1,axiom,
    ( ~ sym_p_1_2(X)
    | sym_p_2_2(X)
    | sym_p_1_1(X) )).

cnf(sym_m_s2_type11_2,axiom,
    ( ~ sym_p_1_2(X)
    | sym_q_2_2(X)
    | sym_q_1_1(X) )).

cnf(sym_m_s2_type11_3,axiom,
    ( ~ sym_p_1_3(X)
    | sym_p_2_3(X)
    | sym_p_1_2(X) )).

cnf(sym_m_s2_type11_4,axiom,
    ( ~ sym_p_1_3(X)
    | sym_q_2_3(X)
    | sym_q_1_2(X) )).

cnf(sym_m_s2_type11_5,axiom,
    ( ~ sym_p_2_3(X)
    | sym_p_3_3(X)
    | sym_p_2_2(X) )).

cnf(sym_m_s2_type11_6,axiom,
    ( ~ sym_p_2_3(X)
    | sym_q_3_3(X)
    | sym_q_2_2(X) )).

cnf(sym_m_s2_type12_1,axiom,
    ( ~ sym_q_1_2(X)
    | sym_p_2_2(X)
    | sym_q_1_1(X) )).

cnf(sym_m_s2_type12_2,axiom,
    ( ~ sym_q_1_2(X)
    | sym_q_2_2(X)
    | sym_p_1_1(X) )).

cnf(sym_m_s2_type12_3,axiom,
    ( ~ sym_q_1_3(X)
    | sym_p_2_3(X)
    | sym_q_1_2(X) )).

cnf(sym_m_s2_type12_4,axiom,
    ( ~ sym_q_1_3(X)
    | sym_q_2_3(X)
    | sym_p_1_2(X) )).

cnf(sym_m_s2_type12_5,axiom,
    ( ~ sym_q_2_3(X)
    | sym_p_3_3(X)
    | sym_q_2_2(X) )).

cnf(sym_m_s2_type12_6,axiom,
    ( ~ sym_q_2_3(X)
    | sym_q_3_3(X)
    | sym_p_2_2(X) )).

cnf(sym_m_s2_type21_1,axiom,
    ( ~ sym_p_1_1(X)
    | sym_p_1_2(X) )).

cnf(sym_m_s2_type21_2,axiom,
    ( ~ sym_q_1_1(X)
    | sym_q_1_2(X) )).

cnf(sym_m_s2_type22_1,axiom,
    ( ~ sym_p_2_2(X)
    | sym_p_1_2(X) )).

cnf(sym_m_s2_type22_2,axiom,
    ( ~ sym_p_3_3(X)
    | sym_p_2_3(X) )).

cnf(sym_m_s2_type22_3,axiom,
    ( ~ sym_q_2_2(X)
    | sym_q_1_2(X) )).

cnf(sym_m_s2_type22_4,axiom,
    ( ~ sym_q_3_3(X)
    | sym_q_2_3(X) )).

cnf(sym_m_t2_1,axiom,
    ( ~ sym_p_1_1(a) )).

cnf(sym_m_t2_2,axiom,
    ( ~ sym_q_1_1(a) )).

cnf(sym_m_t2_3,axiom,
    ( ~ sym_p_2_2(a) )).

cnf(sym_m_t2_4,axiom,
    ( ~ sym_q_2_2(a) )).

cnf(sym_m_t2_5,axiom,
    ( ~ sym_p_3_3(a) )).

cnf(sym_m_t2_6,axiom,
    ( ~ sym_q_3_3(a) )).

%--------------------------------------------------------------------------
