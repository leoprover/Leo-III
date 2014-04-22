%--------------------------------------------------------------------------
% File     : SYN058-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 28
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
% Source   : [Pel86]
% Names    : Pelletier 28 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    9 (   0 non-Horn;   3 unit;   8 RR)
%            Number of atoms       :   16 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    6 (   0 propositional; 1-1 arity)
%            Number of functors    :    3 (   3 constant; 0-0 arity)
%            Number of variables   :    4 (   3 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_p(X)
    | big_q(Y) )).

cnf(clause_2,axiom,
    ( ~ big_q(b)
    | big_q(c) )).

cnf(clause_3,axiom,
    ( ~ big_q(b)
    | big_s(c) )).

cnf(clause_4,axiom,
    ( ~ big_r(b)
    | big_q(c) )).

cnf(clause_5,axiom,
    ( ~ big_r(b)
    | big_s(c) )).

cnf(clause_6,axiom,
    ( ~ big_s(Y)
    | ~ big_f(X)
    | big_g(X) )).

cnf(clause_7,negated_conjecture,
    ( big_p(d) )).

cnf(clause_8,negated_conjecture,
    ( big_f(d) )).

cnf(clause_9,negated_conjecture,
    ( ~ big_g(d) )).

%--------------------------------------------------------------------------
