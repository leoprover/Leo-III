%--------------------------------------------------------------------------
% File     : SYN077+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 54
% Version  : Especial.
% English  : Montegue's paradox of grounded classes

% Refs     : [Mon55] Montegue (1955), On the Paradox of Grounded Classes
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 54 [Pel86]

% Status   : Theorem
% Rating   : 0.37 v6.0.0, 0.30 v5.5.0, 0.37 v5.4.0, 0.32 v5.3.0, 0.37 v5.2.0, 0.20 v5.1.0, 0.24 v5.0.0, 0.21 v4.1.0, 0.26 v4.0.0, 0.25 v3.7.0, 0.29 v3.5.0, 0.11 v3.4.0, 0.17 v3.3.0, 0.00 v3.2.0, 0.22 v3.1.0, 0.33 v2.7.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    2 (   0 unit)
%            Number of atoms       :    7 (   1 equality)
%            Maximal formula depth :   12 (   8 average)
%            Number of connectives :    7 (   2 ~  ;   0  |;   2  &)
%                                         (   2 <=>;   1 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    8 (   0 singleton;   4 !;   4 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_RFO_SEQ

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
%----Problem axioms
fof(pel54_1,axiom,
    ( ! [Y] :
      ? [Z] :
      ! [X] :
        ( big_f(X,Z)
      <=> X = Y ) )).

fof(pel54,conjecture,
    ( ~ ( ? [W] :
          ! [X] :
            ( big_f(X,W)
          <=> ! [U] :
                ( big_f(X,U)
               => ? [Y] :
                    ( big_f(Y,U)
                    & ~ ( ? [Z] :
                            ( big_f(Z,U)
                            & big_f(Z,Y) ) ) ) ) ) ) )).

%--------------------------------------------------------------------------
