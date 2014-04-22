%--------------------------------------------------------------------------
% File     : SYN083-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 61
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 61 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    2 (   0 non-Horn;   2 unit;   1 RR)
%            Number of atoms       :    2 (   2 equality)
%            Maximal clause size   :    1 (   1 average)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    5 (   4 constant; 0-2 arity)
%            Number of variables   :    3 (   0 singleton)
%            Maximal term depth    :    4 (   4 average)
% SPC      : CNF_UNS_RFO_PEQ_UEQ

% Comments :
%--------------------------------------------------------------------------
cnf(f_is_associative,axiom,
    ( f(X,f(Y,Z)) = f(f(X,Y),Z) )).

cnf(prove_this,negated_conjecture,
    (  f(a,f(b,f(c,d))) != f(f(f(a,b),c),d) )).

%--------------------------------------------------------------------------
