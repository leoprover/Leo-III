%--------------------------------------------------------------------------
% File     : SYN081+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 59
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 59 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.5.0, 0.08 v5.4.0, 0.04 v5.3.0, 0.13 v5.2.0, 0.07 v5.0.0, 0.05 v4.1.0, 0.06 v4.0.1, 0.11 v4.0.0, 0.10 v3.7.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    2 (   0 unit)
%            Number of atoms       :    4 (   0 equality)
%            Maximal formula depth :    4 (   4 average)
%            Number of connectives :    4 (   2 ~  ;   0  |;   1  &)
%                                         (   1 <=>;   0 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    1 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   0 constant; 1-1 arity)
%            Number of variables   :    2 (   0 singleton;   1 !;   1 ?)
%            Maximal term depth    :    2 (   2 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments :
%--------------------------------------------------------------------------
fof(pel59_1,axiom,
    ( ! [X] :
        ( big_f(X)
      <=> ~ big_f(f(X)) ) )).

fof(pel59,conjecture,
    ( ? [X] :
        ( big_f(X)
        & ~ big_f(f(X)) ) )).

%--------------------------------------------------------------------------
