%--------------------------------------------------------------------------
% File     : SYN063-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 33
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 33 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   3 non-Horn;   2 unit;   4 RR)
%            Number of atoms       :   22 (   0 equality)
%            Maximal clause size   :    5 (   3 average)
%            Number of predicates  :    1 (   0 propositional; 1-1 arity)
%            Number of functors    :    5 (   5 constant; 0-0 arity)
%            Number of variables   :    4 (   4 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : This is a monadic predicate formulation of Pelletier 17.
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( big_p(a) )).

cnf(clause_2,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X)
    | big_p(c)
    | big_p(Y) )).

cnf(clause_3,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(b)
    | big_p(c) )).

cnf(clause_4,negated_conjecture,
    ( ~ big_p(c) )).

cnf(clause_5,negated_conjecture,
    ( ~ big_p(e)
    | big_p(b)
    | ~ big_p(d) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X)
    | big_p(c)
    | ~ big_p(d)
    | big_p(b) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_p(e)
    | big_p(b)
    | ~ big_p(a)
    | big_p(X)
    | big_p(c) )).

%--------------------------------------------------------------------------
