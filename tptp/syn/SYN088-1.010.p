%--------------------------------------------------------------------------
% File     : SYN088-1.010 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Plaisted problem s(4,10)
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : S4n [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   22 (   0 non-Horn;  21 unit;  22 RR)
%            Number of atoms       :   32 (   0 equality)
%            Maximal clause size   :   11 (   1 average)
%            Number of predicates  :   11 (   0 propositional; 1-10 arity)
%            Number of functors    :    2 (   2 constant; 0-0 arity)
%            Number of variables   :   10 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s10 SYN088-1.g
%--------------------------------------------------------------------------
cnf(s4_goal_1,negated_conjecture,
    ( ~ p(a,a,a,a,a,a,a,a,a,a) )).

cnf(s4_1,axiom,
    ( p(X_1,X_2,X_3,X_4,X_5,X_6,X_7,X_8,X_9,X_10)
    | ~ q_1(X_1)
    | ~ q_2(X_2)
    | ~ q_3(X_3)
    | ~ q_4(X_4)
    | ~ q_5(X_5)
    | ~ q_6(X_6)
    | ~ q_7(X_7)
    | ~ q_8(X_8)
    | ~ q_9(X_9)
    | ~ q_10(X_10) )).

cnf(s4_2,axiom,
    ( q_1(a) )).

cnf(s4_3,axiom,
    ( q_1(b) )).

cnf(s4_4,axiom,
    ( q_2(a) )).

cnf(s4_5,axiom,
    ( q_2(b) )).

cnf(s4_6,axiom,
    ( q_3(a) )).

cnf(s4_7,axiom,
    ( q_3(b) )).

cnf(s4_8,axiom,
    ( q_4(a) )).

cnf(s4_9,axiom,
    ( q_4(b) )).

cnf(s4_10,axiom,
    ( q_5(a) )).

cnf(s4_11,axiom,
    ( q_5(b) )).

cnf(s4_12,axiom,
    ( q_6(a) )).

cnf(s4_13,axiom,
    ( q_6(b) )).

cnf(s4_14,axiom,
    ( q_7(a) )).

cnf(s4_15,axiom,
    ( q_7(b) )).

cnf(s4_16,axiom,
    ( q_8(a) )).

cnf(s4_17,axiom,
    ( q_8(b) )).

cnf(s4_18,axiom,
    ( q_9(a) )).

cnf(s4_19,axiom,
    ( q_9(b) )).

cnf(s4_20,axiom,
    ( q_10(a) )).

cnf(s4_21,axiom,
    ( q_10(b) )).

%--------------------------------------------------------------------------
