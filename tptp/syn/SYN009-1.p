%--------------------------------------------------------------------------
% File     : SYN009-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : A problem to demonstrate the usefulness of relevancy testing
% Version  : Biased.
% English  :

% Refs     : [WL89]  Wilson & Loveland (1989), Incorporating Relevancy Test
% Source   : [WL89]
% Names    : Figure 8 [WL89]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   1 non-Horn;   6 unit;   7 RR)
%            Number of atoms       :   12 (   0 equality)
%            Maximal clause size   :    6 (   2 average)
%            Number of predicates  :    4 (   0 propositional; 1-3 arity)
%            Number of functors    :    3 (   3 constant; 0-0 arity)
%            Number of variables   :    9 (   6 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p(c,X,Y) )).

cnf(clause_2,negated_conjecture,
    ( ~ q(X,c,Y) )).

cnf(clause_3,negated_conjecture,
    ( ~ r(X,Y,c) )).

cnf(clause_4,negated_conjecture,
    ( s(a) )).

cnf(clause_5,negated_conjecture,
    ( s(b) )).

cnf(clause_6,negated_conjecture,
    ( s(c) )).

cnf(clause_7,negated_conjecture,
    ( ~ s(X)
    | ~ s(Y)
    | ~ s(Z)
    | p(X,Y,Z)
    | q(X,Y,Z)
    | r(X,Y,Z) )).

%--------------------------------------------------------------------------
