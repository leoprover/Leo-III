%--------------------------------------------------------------------------
% File     : SYN060-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 30
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 30 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   4 non-Horn;   1 unit;   3 RR)
%            Number of atoms       :   13 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    4 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :    6 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_f(X)
    | ~ big_h(X) )).

cnf(clause_2,axiom,
    ( big_g(X)
    | big_f(X) )).

cnf(clause_3,axiom,
    ( ~ big_g(X)
    | ~ big_h(X) )).

cnf(clause_4,axiom,
    ( big_g(X)
    | big_h(X) )).

cnf(clause_5,axiom,
    ( big_i(X)
    | big_f(X) )).

cnf(clause_6,axiom,
    ( big_i(X)
    | big_h(X) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_i(a) )).

%--------------------------------------------------------------------------
