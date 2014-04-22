%--------------------------------------------------------------------------
% File     : SYN101-1.002.002 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem n(t(2,2),2)
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : N(T2n)) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   17 (   0 non-Horn;   5 unit;  17 RR)
%            Number of atoms       :   37 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    8 (   0 propositional; 1-2 arity)
%            Number of functors    :    2 (   2 constant; 0-0 arity)
%            Number of variables   :   24 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s2:2 SYN101-1.g
% Bugfixes : v1.2.0 - Bugfix in SYN086-1.
%          : v1.2.1 - Bugfix in SYN086-1.
%--------------------------------------------------------------------------
cnf(n_s2_goal_1,negated_conjecture,
    ( ~ p_1_2(a,a) )).

cnf(n_s2_type11_1,axiom,
    ( p_1_2(X_1,X_2)
    | ~ p_2_2(X_1,X_2)
    | ~ p_1_1(X_1,X_2) )).

cnf(n_s2_type11_2,axiom,
    ( p_1_2(X_1,X_2)
    | ~ q_2_2(X_1,X_2)
    | ~ q_1_1(X_1,X_2) )).

cnf(n_s2_type12_1,axiom,
    ( q_1_2(X_1,X_2)
    | ~ p_2_2(X_1,X_2)
    | ~ q_1_1(X_1,X_2) )).

cnf(n_s2_type12_2,axiom,
    ( q_1_2(X_1,X_2)
    | ~ q_2_2(X_1,X_2)
    | ~ p_1_1(X_1,X_2) )).

cnf(n_s2_type21_1,axiom,
    ( p_1_1(X_1,X_2)
    | ~ p_1_2(X_1,X_2) )).

cnf(n_s2_type21_2,axiom,
    ( q_1_1(X_1,X_2)
    | ~ q_1_2(X_1,X_2) )).

cnf(n_s2_type22_1,axiom,
    ( p_2_2(X_1,X_2)
    | ~ p_1_2(X_1,X_2) )).

cnf(n_s2_type22_2,axiom,
    ( q_2_2(X_1,X_2)
    | ~ q_1_2(X_1,X_2) )).

cnf(n_t2_1,axiom,
    ( p_1_1(X_1,X_2)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2) )).

cnf(n_t2_2,axiom,
    ( q_1_1(X_1,X_2)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2) )).

cnf(n_t2_3,axiom,
    ( p_2_2(X_1,X_2)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2) )).

cnf(n_t2_4,axiom,
    ( q_2_2(X_1,X_2)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2) )).

cnf(n_1,axiom,
    ( nq_1(a) )).

cnf(n_2,axiom,
    ( nq_1(b) )).

cnf(n_3,axiom,
    ( nq_2(a) )).

cnf(n_4,axiom,
    ( nq_2(b) )).

%--------------------------------------------------------------------------
