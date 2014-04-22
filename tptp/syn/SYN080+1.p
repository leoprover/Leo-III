%--------------------------------------------------------------------------
% File     : SYN080+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 58
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 58 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v3.2.0, 0.09 v3.1.0, 0.00 v2.5.0, 0.12 v2.4.0, 0.25 v2.3.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    2 (   2 unit)
%            Number of atoms       :    2 (   2 equality)
%            Maximal formula depth :    3 (   3 average)
%            Number of connectives :    0 (   0 ~  ;   0  |;   0  &)
%                                         (   0 <=>;   0 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    2 (   0 constant; 1-1 arity)
%            Number of variables   :    4 (   0 singleton;   4 !;   0 ?)
%            Maximal term depth    :    3 (   2 average)
% SPC      : FOF_THM_RFO_PEQ

% Comments :
%--------------------------------------------------------------------------
%----Problem axioms
fof(pel58_1,axiom,
    ( ! [X,Y] : f(X) = g(Y) )).

fof(pel58,conjecture,
    ( ! [X,Y] : f(f(X)) = f(g(Y)) )).

%--------------------------------------------------------------------------
