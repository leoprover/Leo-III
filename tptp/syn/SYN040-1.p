%--------------------------------------------------------------------------
% File     : SYN040-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 1
% Version  : Especial.
% English  : A biconditional version of the 'most difficult' theorem
%            proved by the original Logic Theorist.

% Refs     : [NSS63] Newell et al. (1963), Empirical Explorations with the
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 1 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    4 (   0 non-Horn;   2 unit;   4 RR)
%            Number of atoms       :    6 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    2 (   2 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : [NSS63] first appeared in 1957, as cited in [Pel86]. The 1963
%            version is a reprint.
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p
    | q )).

cnf(clause_2,negated_conjecture,
    ( ~ q
    | p )).

cnf(clause_3,negated_conjecture,
    ( ~ q )).

cnf(clause_4,negated_conjecture,
    ( p )).

%--------------------------------------------------------------------------
