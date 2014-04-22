%--------------------------------------------------------------------------
% File     : SYN059-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 29
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 29 [Pel86]

% Status   : Satisfiable
% Rating   : 0.00 v2.2.0, 0.33 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   32 (  22 non-Horn;   2 unit;  32 RR)
%            Number of atoms       :  120 (   0 equality)
%            Maximal clause size   :    5 (   4 average)
%            Number of predicates  :    4 (   0 propositional; 1-1 arity)
%            Number of functors    :    7 (   7 constant; 0-0 arity)
%            Number of variables   :   32 (  10 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_SAT_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( big_f(a) )).

cnf(clause_2,axiom,
    ( big_g(b) )).

cnf(clause_3,negated_conjecture,
    ( ~ big_f(X)
    | big_h(X)
    | ~ big_f(Y)
    | ~ big_g(Z)
    | big_h(Y) )).

cnf(clause_4,negated_conjecture,
    ( ~ big_f(X)
    | big_h(X)
    | ~ big_f(Y)
    | ~ big_g(Z)
    | big_j(Z) )).

cnf(clause_5,negated_conjecture,
    ( ~ big_f(X)
    | big_h(X)
    | big_f(e)
    | big_g(f) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_f(X)
    | big_h(X)
    | big_f(e)
    | ~ big_j(f) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_f(X)
    | big_h(X)
    | ~ big_h(e)
    | big_g(f) )).

cnf(clause_8,negated_conjecture,
    ( ~ big_f(X)
    | big_h(X)
    | ~ big_h(e)
    | ~ big_j(f) )).

cnf(clause_9,negated_conjecture,
    ( ~ big_g(X)
    | big_j(X)
    | ~ big_f(Y)
    | ~ big_g(Z)
    | big_h(Y) )).

cnf(clause_10,negated_conjecture,
    ( ~ big_g(X)
    | big_j(X)
    | ~ big_f(Y)
    | ~ big_g(Z)
    | big_j(Z) )).

cnf(clause_11,negated_conjecture,
    ( ~ big_g(X)
    | big_j(X)
    | big_f(e)
    | big_g(j) )).

cnf(clause_12,negated_conjecture,
    ( ~ big_g(X)
    | big_j(X)
    | big_f(e)
    | ~ big_j(f) )).

cnf(clause_13,negated_conjecture,
    ( ~ big_g(X)
    | big_j(X)
    | ~ big_h(e)
    | big_g(f) )).

cnf(clause_14,negated_conjecture,
    ( ~ big_g(X)
    | big_j(X)
    | ~ big_h(e)
    | ~ big_j(f) )).

cnf(clause_15,negated_conjecture,
    ( big_f(c)
    | ~ big_f(X)
    | ~ big_g(Y)
    | big_h(X) )).

cnf(clause_16,negated_conjecture,
    ( big_f(c)
    | ~ big_f(X)
    | ~ big_g(Y)
    | big_j(Y) )).

cnf(clause_17,negated_conjecture,
    ( big_f(c)
    | big_f(e)
    | big_g(f) )).

cnf(clause_18,negated_conjecture,
    ( big_f(c)
    | big_f(e)
    | ~ big_j(f) )).

cnf(clause_19,negated_conjecture,
    ( big_f(c)
    | ~ big_h(e)
    | big_g(f) )).

cnf(clause_20,negated_conjecture,
    ( big_f(c)
    | ~ big_h(e)
    | ~ big_j(f) )).

cnf(clause_21,negated_conjecture,
    ( big_g(d)
    | ~ big_f(X)
    | ~ big_g(Y)
    | big_h(X) )).

cnf(clause_22,negated_conjecture,
    ( big_g(d)
    | ~ big_f(X)
    | ~ big_g(Y)
    | big_j(Y) )).

cnf(clause_23,negated_conjecture,
    ( big_g(d)
    | big_f(e)
    | big_g(f) )).

cnf(clause_24,negated_conjecture,
    ( big_g(d)
    | big_f(e)
    | ~ big_j(f) )).

cnf(clause_25,negated_conjecture,
    ( big_g(d)
    | ~ big_h(e)
    | big_g(f) )).

cnf(clause_26,negated_conjecture,
    ( big_g(d)
    | ~ big_h(e)
    | ~ big_j(f) )).

cnf(clause_27,negated_conjecture,
    ( ~ big_h(c)
    | ~ big_j(d)
    | ~ big_f(X)
    | ~ big_g(Y)
    | big_h(X) )).

cnf(clause_28,negated_conjecture,
    ( ~ big_h(c)
    | ~ big_j(d)
    | ~ big_f(X)
    | ~ big_g(Y)
    | big_j(Y) )).

cnf(clause_29,negated_conjecture,
    ( ~ big_h(c)
    | ~ big_j(d)
    | big_f(e)
    | big_g(f) )).

cnf(clause_30,negated_conjecture,
    ( ~ big_h(c)
    | ~ big_j(d)
    | big_f(e)
    | ~ big_j(f) )).

cnf(clause_31,negated_conjecture,
    ( ~ big_h(c)
    | big_j(d)
    | ~ big_h(e)
    | big_g(f) )).

cnf(clause_32,negated_conjecture,
    ( ~ big_h(c)
    | ~ big_j(d)
    | ~ big_h(e)
    | ~ big_j(f) )).

%--------------------------------------------------------------------------
