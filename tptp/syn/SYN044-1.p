%--------------------------------------------------------------------------
% File     : SYN044-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 10
% Version  : Especial.
% English  : A reasonably simple probloem designed to see whether 'natural'
%            systems correctly manipulate premises.

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 10 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    6 (   2 non-Horn;   0 unit;   6 RR)
%            Number of atoms       :   13 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    3 (   3 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( ~ q
    | r )).

cnf(clause_2,axiom,
    ( ~ r
    | p )).

cnf(clause_3,axiom,
    ( ~ r
    | q )).

cnf(clause_4,axiom,
    ( ~ p
    | q
    | r )).

cnf(clause_5,negated_conjecture,
    ( ~ p
    | ~ q )).

cnf(clause_6,negated_conjecture,
    ( p
    | q )).

%--------------------------------------------------------------------------
