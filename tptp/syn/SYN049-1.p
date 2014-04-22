%--------------------------------------------------------------------------
% File     : SYN049-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 19
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 19 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v5.3.0, 0.05 v5.2.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :    3 (   0 non-Horn;   2 unit;   2 RR)
%            Number of atoms       :    4 (   0 equality)
%            Maximal clause size   :    2 (   1 average)
%            Number of predicates  :    2 (   0 propositional; 1-1 arity)
%            Number of functors    :    2 (   0 constant; 1-1 arity)
%            Number of variables   :    3 (   2 singleton)
%            Maximal term depth    :    2 (   2 average)
% SPC      : CNF_UNS_RFO_NEQ_HRN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ big_p(f(X))
    | big_q(g(X)) )).

cnf(clause_2,negated_conjecture,
    ( big_p(X) )).

cnf(clause_3,negated_conjecture,
    ( ~ big_q(X) )).

%--------------------------------------------------------------------------
