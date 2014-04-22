%--------------------------------------------------------------------------
% File     : SYN080-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 58
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 58 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v3.4.0, 0.12 v3.3.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :    2 (   0 non-Horn;   2 unit;   1 RR)
%            Number of atoms       :    2 (   2 equality)
%            Maximal clause size   :    1 (   1 average)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    4 (   2 constant; 0-1 arity)
%            Number of variables   :    2 (   2 singleton)
%            Maximal term depth    :    3 (   2 average)
% SPC      : CNF_UNS_RFO_PEQ_UEQ

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    ( f(X) = g(Y) )).

cnf(prove_this,negated_conjecture,
    (  f(f(a)) != f(g(b)) )).

%--------------------------------------------------------------------------
