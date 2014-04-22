%--------------------------------------------------------------------------
% File     : SYN072-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 49
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 49 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.12 v6.0.0, 0.00 v4.1.0, 0.20 v4.0.1, 0.00 v2.6.0, 0.11 v2.5.0, 0.00 v2.4.0, 0.00 v2.2.0, 0.33 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :    5 (   1 non-Horn;   4 unit;   4 RR)
%            Number of atoms       :    6 (   3 equality)
%            Maximal clause size   :    2 (   1 average)
%            Number of predicates  :    2 (   0 propositional; 1-2 arity)
%            Number of functors    :    5 (   5 constant; 0-0 arity)
%            Number of variables   :    1 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( X = c
    | X = d )).

cnf(clause_2,axiom,
    ( big_p(a) )).

cnf(clause_3,axiom,
    ( big_p(b) )).

cnf(clause_4,axiom,
    (  a != b )).

cnf(prove_this,negated_conjecture,
    ( ~ big_p(e) )).

%--------------------------------------------------------------------------
