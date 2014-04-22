%--------------------------------------------------------------------------
% File     : SYN006-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : A problem to demonstrate controlling splits
% Version  : Especial.
% English  :

% Refs     : [Pla82] Plaisted (1982), A Simplified Problem Reduction Format
% Source   : [Pla82]
% Names    : Problem 5.8 [Pla82]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   2 non-Horn;   4 unit;   6 RR)
%            Number of atoms       :   12 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    6 (   0 propositional; 2-2 arity)
%            Number of functors    :    3 (   1 constant; 0-1 arity)
%            Number of variables   :   13 (   7 singleton)
%            Maximal term depth    :    4 (   2 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p1(X,f(Y))
    | q1(f(X),Y)
    | q2(X,f(Y)) )).

cnf(clause_2,negated_conjecture,
    ( p1(a,Y) )).

cnf(clause_3,negated_conjecture,
    ( ~ q2(X,f(Y)) )).

cnf(clause_4,negated_conjecture,
    ( ~ p2(X,f(Y))
    | q3(f(X),Y)
    | q4(X,f(Y)) )).

cnf(clause_5,negated_conjecture,
    ( ~ q3(X,g(Y)) )).

cnf(clause_6,negated_conjecture,
    ( ~ q4(f(f(X)),f(g(f(Y)))) )).

cnf(clause_7,negated_conjecture,
    ( ~ q1(X,f(Y))
    | p2(f(X),Y) )).

%--------------------------------------------------------------------------
