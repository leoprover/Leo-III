%--------------------------------------------------------------------------
% File     : SYN082-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 60
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
% Source   : [Pel86]
% Names    : Pelletier 60 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    8 (   4 non-Horn;   0 unit;   8 RR)
%            Number of atoms       :   25 (   0 equality)
%            Maximal clause size   :    4 (   3 average)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    4 (   2 constant; 0-1 arity)
%            Number of variables   :    9 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( big_f(a,f(a))
    | ~ big_f(Y,b)
    | big_f(Y,f(a)) )).

cnf(clause_2,negated_conjecture,
    ( big_f(a,f(a))
    | big_f(a,b) )).

cnf(clause_3,negated_conjecture,
    ( big_f(g(X),X)
    | ~ big_f(a,X)
    | ~ big_f(Y,b)
    | big_f(Y,f(a)) )).

cnf(clause_4,negated_conjecture,
    ( big_f(g(X),X)
    | ~ big_f(a,X)
    | big_f(a,b) )).

cnf(clause_5,negated_conjecture,
    ( big_f(g(X),X)
    | ~ big_f(a,X)
    | ~ big_f(a,f(a)) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_f(g(X),X)
    | ~ big_f(a,X)
    | ~ big_f(Y,b)
    | big_f(Y,f(a)) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_f(g(X),X)
    | ~ big_f(a,X)
    | big_f(a,b) )).

cnf(clause_8,negated_conjecture,
    ( ~ big_f(g(X),X)
    | ~ big_f(a,X)
    | ~ big_f(a,f(a)) )).

%--------------------------------------------------------------------------
