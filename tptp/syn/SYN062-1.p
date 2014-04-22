%--------------------------------------------------------------------------
% File     : SYN062-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 32
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 32 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   0 non-Horn;   3 unit;   7 RR)
%            Number of atoms       :   14 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    6 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :    4 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_f(X)
    | ~ big_g(X)
    | big_i(X) )).

cnf(clause_2,axiom,
    ( ~ big_f(X)
    | ~ big_h(X)
    | big_i(X) )).

cnf(clause_3,axiom,
    ( ~ big_i(X)
    | ~ big_h(X)
    | big_j(X) )).

cnf(clause_4,axiom,
    ( ~ big_k(X)
    | big_h(X) )).

cnf(clause_5,negated_conjecture,
    ( big_f(a) )).

cnf(clause_6,negated_conjecture,
    ( big_k(a) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_j(a) )).

%--------------------------------------------------------------------------
