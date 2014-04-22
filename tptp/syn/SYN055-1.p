%--------------------------------------------------------------------------
% File     : SYN055-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 25
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 25 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   2 non-Horn;   1 unit;   7 RR)
%            Number of atoms       :   16 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    5 (   0 propositional; 1-1 arity)
%            Number of functors    :    2 (   2 constant; 0-0 arity)
%            Number of variables   :    6 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( big_p(a) )).

cnf(clause_2,axiom,
    ( ~ big_f(X)
    | ~ big_g(X)
    | ~ big_r(X) )).

cnf(clause_3,axiom,
    ( ~ big_p(X)
    | big_f(X) )).

cnf(clause_4,axiom,
    ( ~ big_p(X)
    | big_g(X) )).

cnf(clause_5,axiom,
    ( ~ big_p(X)
    | big_q(X)
    | big_p(b) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_p(X)
    | big_q(X)
    | big_r(b) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_q(X)
    | ~ big_p(X) )).

%--------------------------------------------------------------------------
