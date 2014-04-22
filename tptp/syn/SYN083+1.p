%--------------------------------------------------------------------------
% File     : SYN083+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 61
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 61 [Pel86]

% Status   : Theorem
% Rating   : 0.08 v5.5.0, 0.12 v5.4.0, 0.11 v5.3.0, 0.17 v5.2.0, 0.14 v5.0.0, 0.12 v4.1.0, 0.09 v4.0.1, 0.10 v4.0.0, 0.11 v3.7.0, 0.00 v3.5.0, 0.11 v3.4.0, 0.05 v3.3.0, 0.07 v3.2.0, 0.18 v3.1.0, 0.11 v2.7.0, 0.17 v2.6.0, 0.14 v2.5.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    2 (   2 unit)
%            Number of atoms       :    2 (   2 equality)
%            Maximal formula depth :    5 (   4 average)
%            Number of connectives :    0 (   0 ~  ;   0  |;   0  &)
%                                         (   0 <=>;   0 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    1 (   0 constant; 2-2 arity)
%            Number of variables   :    7 (   0 singleton;   7 !;   0 ?)
%            Maximal term depth    :    4 (   4 average)
% SPC      : FOF_THM_RFO_PEQ

% Comments :
%--------------------------------------------------------------------------
%----Problem axioms
fof(p61_1,axiom,
    ( ! [X,Y,Z] : f(X,f(Y,Z)) = f(f(X,Y),Z) )).

fof(pel61,conjecture,
    ( ! [X,Y,Z,W] : f(X,f(Y,f(Z,W))) = f(f(f(X,Y),Z),W) )).

%--------------------------------------------------------------------------
