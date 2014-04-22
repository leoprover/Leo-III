%--------------------------------------------------------------------------
% File     : SYN052-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 22
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 22 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    5 (   1 non-Horn;   0 unit;   2 RR)
%            Number of atoms       :   10 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    2 (   1 propositional; 0-1 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :    4 (   4 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( p
    | ~ big_f(X) )).

cnf(clause_2,negated_conjecture,
    ( big_f(X)
    | ~ p )).

cnf(clause_3,negated_conjecture,
    ( big_f(Y)
    | p )).

cnf(clause_4,negated_conjecture,
    ( ~ p
    | ~ big_f(a) )).

cnf(clause_5,negated_conjecture,
    ( big_f(Y)
    | ~ big_f(a) )).

%--------------------------------------------------------------------------
