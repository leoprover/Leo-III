%--------------------------------------------------------------------------
% File     : SYN046-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 15
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
% Source   : [Pel86]
% Names    : Pelletier 15 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    3 (   0 non-Horn;   2 unit;   3 RR)
%            Number of atoms       :    4 (   0 equality)
%            Maximal clause size   :    2 (   1 average)
%            Number of predicates  :    2 (   2 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : This problem is incorrect in [Pel86] and is
%            corrected in [Pel88].
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p
    | q )).

cnf(clause_2,negated_conjecture,
    ( p )).

cnf(clause_3,negated_conjecture,
    ( ~ q )).

%--------------------------------------------------------------------------
