%--------------------------------------------------------------------------
% File     : SYN051-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 21
% Version  : Especial.
% English  : A moderately tricky problem, especially for 'natural' systems
%            with 'strong' restrictions on variables generated from
%            existential quantifiers.

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 21 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    4 (   1 non-Horn;   0 unit;   3 RR)
%            Number of atoms       :    8 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    2 (   1 propositional; 0-1 arity)
%            Number of functors    :    2 (   2 constant; 0-0 arity)
%            Number of variables   :    2 (   2 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ p
    | big_f(a) )).

cnf(clause_2,axiom,
    ( ~ big_f(b)
    | p )).

cnf(clause_3,negated_conjecture,
    ( p
    | big_f(X) )).

cnf(clause_4,negated_conjecture,
    ( ~ big_f(X)
    | ~ p )).

%--------------------------------------------------------------------------
