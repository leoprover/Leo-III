%--------------------------------------------------------------------------
% File     : SYN074+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 51
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 51 [Pel86]

% Status   : Theorem
% Rating   : 0.20 v6.0.0, 0.17 v5.5.0, 0.15 v5.4.0, 0.18 v5.3.0, 0.26 v5.2.0, 0.05 v5.0.0, 0.17 v4.0.1, 0.22 v4.0.0, 0.21 v3.7.0, 0.00 v3.3.0, 0.11 v3.2.0, 0.33 v3.1.0, 0.17 v2.7.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    2 (   0 unit)
%            Number of atoms       :    6 (   4 equality)
%            Maximal formula depth :    7 (   7 average)
%            Number of connectives :    4 (   0 ~  ;   0  |;   1  &)
%                                         (   3 <=>;   0 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    8 (   0 singleton;   4 !;   4 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_RFO_SEQ

% Comments :
%--------------------------------------------------------------------------
%----Problem axioms
fof(pel51_1,axiom,
    ( ? [Z,W] :
      ! [X,Y] :
        ( big_f(X,Y)
      <=> ( X = Z
          & Y = W ) ) )).

fof(pel51,conjecture,
    ( ? [Z] :
      ! [X] :
        ( ? [W] :
          ! [Y] :
            ( big_f(X,Y)
          <=> Y = W )
      <=> X = Z ) )).

%--------------------------------------------------------------------------
