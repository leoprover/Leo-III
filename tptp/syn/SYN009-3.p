%--------------------------------------------------------------------------
% File     : SYN009-3 : TPTP v6.0.0. Released v2.5.0.
% Domain   : Syntactic
% Problem  : A problem to demonstrate the usefulness of relevancy testing
% Version  : Especial.
% English  :

% Refs     : [He01a] I-SATCHMO: An Improvement of SATCHMO
%          : [He01b] UNSEARCHMO: Eliminating Redundant Search Space on Back
% Source   : [He01a]
% Names    : Example 5.1 [He01a]
%          : Example 5.2 [He01]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.5.0
% Syntax   : Number of clauses     :   10 (   2 non-Horn;   5 unit;  10 RR)
%            Number of atoms       :   20 (   0 equality)
%            Maximal clause size   :    6 (   2 average)
%            Number of predicates  :    7 (   0 propositional; 1-3 arity)
%            Number of functors    :    3 (   3 constant; 0-0 arity)
%            Number of variables   :   18 (   3 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p(X,Y,Z)
    | ~ t(X,Y,Z) )).

cnf(clause_2,negated_conjecture,
    ( ~ q(X,Y,Z)
    | ~ t(X,Y,Z) )).

cnf(clause_3,negated_conjecture,
    ( ~ r(X,Y,Z)
    | ~ t(X,Y,Z) )).

cnf(clause_4,negated_conjecture,
    ( s(a) )).

cnf(clause_5,negated_conjecture,
    ( s(b) )).

cnf(clause_6,negated_conjecture,
    ( s(c) )).

cnf(clause_7,negated_conjecture,
    ( ~ v(X,Y,Z) )).

cnf(clause_8,negated_conjecture,
    ( u(c,c,c) )).

cnf(clause_9,negated_conjecture,
    ( ~ s(X)
    | ~ s(Y)
    | ~ s(Z)
    | p(X,Y,Z)
    | q(X,Y,Z)
    | r(X,Y,Z) )).

cnf(clause_10,negated_conjecture,
    ( ~ u(X,Y,Z)
    | t(X,Y,Z)
    | v(X,Y,Z) )).

%--------------------------------------------------------------------------
