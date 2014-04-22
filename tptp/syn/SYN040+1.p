%--------------------------------------------------------------------------
% File     : SYN040+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 1
% Version  : Especial.
% English  : A biconditional version of the 'most difficult' theorem
%            proved by the original Logic Theorist.

% Refs     : [NSS63] Newell et al. (1963), Empirical Explorations with the
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 1 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :    4 (   0 equality)
%            Maximal formula depth :    4 (   4 average)
%            Number of connectives :    5 (   2 ~  ;   0  |;   0  &)
%                                         (   1 <=>;   2 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   2 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton;   0 !;   0 ?)
%            Maximal term depth    :    0 (   0 average)
% SPC      : FOF_THM_PRP

% Comments : [NSS63] first appeared in 1957, as cited in [Pel86]. The 1963
%            version is a reprint.
%--------------------------------------------------------------------------
fof(pel1,conjecture,
    ( ( p
     => q )
  <=> ( ~ q
     => ~ p ) )).

%--------------------------------------------------------------------------
