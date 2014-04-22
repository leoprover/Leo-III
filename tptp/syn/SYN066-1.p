%--------------------------------------------------------------------------
% File     : SYN066-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 37
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 37 [Pel86]
%          : p37.in [ANL]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    6 (   1 non-Horn;   2 unit;   3 RR)
%            Number of atoms       :   10 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    3 (   0 propositional; 2-2 arity)
%            Number of functors    :    5 (   1 constant; 0-2 arity)
%            Number of variables   :   12 (   4 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_p(Y,X)
    | big_p(f(X,Y),g(X)) )).

cnf(clause_2,axiom,
    ( big_p(f(X,Y),X) )).

cnf(clause_3,axiom,
    ( ~ big_p(f(X,Y),g(X))
    | big_q(h(X,Y),g(X)) )).

cnf(clause_4,axiom,
    ( big_p(X,Y)
    | big_q(i(X,Y),X) )).

cnf(clause_5,axiom,
    ( ~ big_q(X,Y)
    | big_r(Z,Z) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_r(a,Z) )).

%--------------------------------------------------------------------------
