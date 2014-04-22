%--------------------------------------------------------------------------
% File     : SYN069-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 45
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 45 [Pel86]
%          : p45.in [ANL]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    9 (   2 non-Horn;   1 unit;   9 RR)
%            Number of atoms       :   27 (   0 equality)
%            Maximal clause size   :    5 (   3 average)
%            Number of predicates  :    6 (   0 propositional; 1-2 arity)
%            Number of functors    :    3 (   1 constant; 0-1 arity)
%            Number of variables   :   11 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_f(X)
    | big_g(f(X))
    | ~ big_g(Y)
    | ~ big_h(X,Y)
    | big_k(Y) )).

cnf(clause_2,axiom,
    ( ~ big_f(X)
    | big_h(X,f(X))
    | ~ big_g(Y)
    | ~ big_h(X,Y)
    | big_k(Y) )).

cnf(clause_3,axiom,
    ( ~ big_f(X)
    | ~ big_j(X,f(X))
    | ~ big_g(Y)
    | ~ big_h(X,Y)
    | big_k(Y) )).

cnf(clause_4,axiom,
    ( ~ big_l(X)
    | ~ big_k(X) )).

cnf(clause_5,axiom,
    ( big_f(a) )).

cnf(clause_6,axiom,
    ( ~ big_h(a,X)
    | big_l(X) )).

cnf(clause_7,axiom,
    ( ~ big_g(X)
    | ~ big_h(a,X)
    | big_j(a,X) )).

cnf(clause_8,negated_conjecture,
    ( ~ big_f(X)
    | big_g(g(X)) )).

cnf(clause_9,negated_conjecture,
    ( ~ big_f(X)
    | big_h(X,g(X)) )).

%--------------------------------------------------------------------------
