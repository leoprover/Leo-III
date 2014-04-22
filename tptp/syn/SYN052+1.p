%--------------------------------------------------------------------------
% File     : SYN052+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 22
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 22 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v3.2.0, 0.11 v3.1.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :    4 (   0 equality)
%            Maximal formula depth :    4 (   4 average)
%            Number of connectives :    3 (   0 ~  ;   0  |;   0  &)
%                                         (   2 <=>;   1 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   1 propositional; 0-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    2 (   0 singleton;   2 !;   0 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel22,conjecture,
    ( ! [X] :
        ( p
      <=> big_f(X) )
   => ( p
    <=> ! [X1] : big_f(X1) ) )).

%--------------------------------------------------------------------------
