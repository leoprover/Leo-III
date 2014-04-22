%--------------------------------------------------------------------------
% File     : SYN054-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 24
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 24 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    6 (   2 non-Horn;   0 unit;   6 RR)
%            Number of atoms       :   13 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    4 (   0 propositional; 1-1 arity)
%            Number of functors    :    2 (   2 constant; 0-0 arity)
%            Number of variables   :    5 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_s(X)
    | ~ big_q(X) )).

cnf(clause_2,axiom,
    ( ~ big_p(X)
    | big_q(X)
    | big_r(X) )).

cnf(clause_3,axiom,
    ( big_p(a)
    | big_q(b) )).

cnf(clause_4,axiom,
    ( ~ big_q(X)
    | big_s(X) )).

cnf(clause_5,negated_conjecture,
    ( ~ big_r(X)
    | big_s(X) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_r(X) )).

%--------------------------------------------------------------------------
