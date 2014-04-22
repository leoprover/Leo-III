%--------------------------------------------------------------------------
% File     : SYN081-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 59
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 59 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    3 (   1 non-Horn;   0 unit;   2 RR)
%            Number of atoms       :    6 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    1 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   0 constant; 1-1 arity)
%            Number of variables   :    3 (   0 singleton)
%            Maximal term depth    :    2 (   2 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ big_f(X)
    | ~ big_f(f(X)) )).

cnf(clause_2,axiom,
    ( big_f(f(X))
    | big_f(X) )).

cnf(prove_this,negated_conjecture,
    ( ~ big_f(X)
    | big_f(f(X)) )).

%--------------------------------------------------------------------------
