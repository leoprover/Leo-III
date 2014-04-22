%--------------------------------------------------------------------------
% File     : SYN077-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 54
% Version  : Especial.
% English  : Montegue's paradox of grounded classes

% Refs     : [Mon55] Montegue (1955), On the Paradox of Grounded Classes
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
% Source   : [Pel86]
% Names    : Pelletier 54 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.21 v6.0.0, 0.30 v5.3.0, 0.33 v5.2.0, 0.25 v5.1.0, 0.29 v5.0.0, 0.21 v4.1.0, 0.23 v4.0.1, 0.18 v4.0.0, 0.09 v3.7.0, 0.20 v3.5.0, 0.18 v3.4.0, 0.17 v3.3.0, 0.21 v3.2.0, 0.31 v3.1.0, 0.36 v2.7.0, 0.33 v2.6.0, 0.20 v2.5.0, 0.25 v2.4.0, 0.22 v2.3.0, 0.33 v2.2.0, 0.56 v2.1.0, 0.78 v2.0.0
% Syntax   : Number of clauses     :    7 (   3 non-Horn;   0 unit;   6 RR)
%            Number of atoms       :   19 (   2 equality)
%            Maximal clause size   :    4 (   3 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    5 (   1 constant; 0-2 arity)
%            Number of variables   :   14 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_f(X,f(Y))
    | X = Y )).

cnf(clause_2,axiom,
    ( X != Y
    | big_f(X,f(Y)) )).

cnf(clause_3,negated_conjecture,
    ( ~ big_f(X,a)
    | ~ big_f(X,Y)
    | big_f(g(X,Y),Y) )).

cnf(clause_4,negated_conjecture,
    ( ~ big_f(X,a)
    | ~ big_f(X,Y)
    | ~ big_f(Z,g(X,Y))
    | ~ big_f(Z,Y) )).

cnf(clause_5,negated_conjecture,
    ( big_f(X,h(X))
    | big_f(X,a) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_f(X,h(Y))
    | big_f(i(Y,X),X)
    | big_f(Y,a) )).

cnf(clause_7,negated_conjecture,
    ( big_f(X,a)
    | big_f(i(X,Y),h(X))
    | ~ big_f(Y,h(X)) )).

%--------------------------------------------------------------------------
