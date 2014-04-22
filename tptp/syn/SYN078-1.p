%--------------------------------------------------------------------------
% File     : SYN078-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 56
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
% Source   : [Pel86]
% Names    : Pelletier 56 [Pel86]

% Status   : Satisfiable
% Rating   : 0.22 v6.0.0, 0.14 v5.5.0, 0.12 v5.4.0, 0.20 v5.3.0, 0.22 v5.2.0, 0.30 v5.0.0, 0.22 v4.1.0, 0.14 v4.0.1, 0.20 v4.0.0, 0.25 v3.7.0, 0.00 v3.2.0, 0.20 v3.1.0, 0.00 v2.6.0, 0.14 v2.5.0, 0.17 v2.4.0, 0.00 v2.2.0, 0.33 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   12 (   6 non-Horn;   0 unit;  12 RR)
%            Number of atoms       :   35 (   7 equality)
%            Maximal clause size   :    5 (   3 average)
%            Number of predicates  :    2 (   0 propositional; 1-2 arity)
%            Number of functors    :    4 (   3 constant; 0-1 arity)
%            Number of variables   :   11 (   4 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_SAT_RFO_EQU_NUE

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ big_f(X)
    | Y != f(Y)
    | big_f(Y)
    | ~ big_f(Z)
    | big_f(f(Z)) )).

cnf(clause_2,negated_conjecture,
    ( ~ big_f(X)
    | Y != f(Y)
    | big_f(Y)
    | big_f(a) )).

cnf(clause_3,negated_conjecture,
    ( ~ big_f(X)
    | Y != f(Y)
    | big_f(Y)
    | b = f(b) )).

cnf(clause_4,negated_conjecture,
    ( ~ big_f(X)
    | Y != f(Y)
    | big_f(Y)
    | ~ big_f(b) )).

cnf(clause_5,negated_conjecture,
    ( big_f(c)
    | ~ big_f(X)
    | big_f(f(X)) )).

cnf(clause_6,negated_conjecture,
    ( big_f(c)
    | big_f(a) )).

cnf(clause_7,negated_conjecture,
    ( big_f(c)
    | b = f(b) )).

cnf(clause_8,negated_conjecture,
    ( big_f(c)
    | ~ big_f(b) )).

cnf(clause_9,negated_conjecture,
    ( ~ big_f(f(c))
    | ~ big_f(X)
    | big_f(f(X)) )).

cnf(clause_10,negated_conjecture,
    ( ~ big_f(f(c))
    | big_f(a) )).

cnf(clause_11,negated_conjecture,
    ( ~ big_f(f(c))
    | b = f(b) )).

cnf(clause_12,negated_conjecture,
    ( ~ big_f(f(c))
    | ~ big_f(b) )).

%--------------------------------------------------------------------------
