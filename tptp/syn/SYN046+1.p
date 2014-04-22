%--------------------------------------------------------------------------
% File     : SYN046+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 15
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 15 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :    4 (   0 equality)
%            Maximal formula depth :    4 (   4 average)
%            Number of connectives :    4 (   1 ~  ;   1  |;   0  &)
%                                         (   1 <=>;   1 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   2 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton;   0 !;   0 ?)
%            Maximal term depth    :    0 (   0 average)
% SPC      : FOF_THM_PRP

% Comments : This problem is incorrect in [Pel86] and is
%            corrected in [Pel88].
%--------------------------------------------------------------------------
fof(pel15,conjecture,
    ( ( p
     => q )
  <=> ( ~ p
      | q ) )).

%--------------------------------------------------------------------------
