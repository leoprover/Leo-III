%--------------------------------------------------------------------------
% File     : SYN041-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 3, 16
% Version  : Especial.
% English  : 3: The hardest theorm proved by a breadth-first logic
%               theorist.
%            16:A surprising theorem of propositional logic.

% Refs     : [SRM73] Siklossy et al. (1973), Breadth First Search: Some Sur
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 3 [Pel86]
%          : Pelletier 16 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    4 (   0 non-Horn;   4 unit;   4 RR)
%            Number of atoms       :    4 (   0 equality)
%            Maximal clause size   :    1 (   1 average)
%            Number of predicates  :    2 (   2 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( p )).

cnf(clause_2,negated_conjecture,
    ( ~ q )).

cnf(clause_3,negated_conjecture,
    ( q )).

cnf(clause_4,negated_conjecture,
    ( ~ p )).

%--------------------------------------------------------------------------
