%--------------------------------------------------------------------------
% File     : SYN061-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 31
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 31 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    6 (   1 non-Horn;   2 unit;   5 RR)
%            Number of atoms       :   10 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    5 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :    4 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_f(X)
    | ~ big_g(X) )).

cnf(clause_2,axiom,
    ( ~ big_f(X)
    | ~ big_h(X) )).

cnf(clause_3,axiom,
    ( big_i(a) )).

cnf(clause_4,axiom,
    ( big_f(a) )).

cnf(clause_5,axiom,
    ( big_h(X)
    | big_j(X) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_i(X)
    | ~ big_j(X) )).

%--------------------------------------------------------------------------
