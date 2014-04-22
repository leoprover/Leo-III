%--------------------------------------------------------------------------
% File     : SYN036+2 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Andrews Challenge Problem
% Version  : Especial.
%            Theorem formulation : As in [And86].
% English  :

% Refs     : [And86] Andrews (1986), An Introduction to Mathematical Logic
%          : [DeC79] DeChampeaux (1979), Sub-problem Finder and Instance Ch
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
%          : [And94] Andrews (1994), Email to G. Sutcliffe
% Source   : [And94]
% Names    : X2129 [And86]

% Status   : Theorem
% Rating   : 0.08 v6.0.0, 0.00 v5.5.0, 0.08 v5.4.0, 0.09 v5.3.0, 0.13 v5.2.0, 0.14 v5.0.0, 0.10 v4.1.0, 0.11 v4.0.1, 0.16 v4.0.0, 0.20 v3.7.0, 0.33 v3.5.0, 0.25 v3.4.0, 0.08 v3.3.0, 0.11 v3.2.0, 0.33 v3.1.0, 0.50 v2.7.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.50 v2.2.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :    8 (   0 equality)
%            Maximal formula depth :    6 (   6 average)
%            Number of connectives :    7 (   0 ~  ;   0  |;   0  &)
%                                         (   7 <=>;   0 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   0 propositional; 1-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    8 (   0 singleton;   4 !;   4 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
fof(pel34,conjecture,
    ( ( ? [X] :
        ! [Y] :
          ( big_p(X)
        <=> big_p(Y) )
    <=> ( ? [U] : big_q(U)
      <=> ! [W] : big_p(W) ) )
  <=> ( ? [X1] :
        ! [Y1] :
          ( big_q(X1)
        <=> big_q(Y1) )
    <=> ( ? [U1] : big_p(U1)
      <=> ! [W1] : big_q(W1) ) ) )).

%--------------------------------------------------------------------------
