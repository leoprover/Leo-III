%--------------------------------------------------------------------------
% File     : SYN045-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 13
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 13 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    4 (   2 non-Horn;   1 unit;   4 RR)
%            Number of atoms       :    7 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    3 (   3 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( p
    | q )).

cnf(clause_2,negated_conjecture,
    ( p
    | r )).

cnf(clause_3,negated_conjecture,
    ( ~ p )).

cnf(clause_4,negated_conjecture,
    ( ~ q
    | ~ r )).

%--------------------------------------------------------------------------
