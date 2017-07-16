%------------------------------------------------------------------------------
% File     : NUN024^1 : TPTP v6.4.0. Released v6.4.0.
% Domain   : Number Theory
% Problem  : Function h s.t. h(0) = 1, h(1) = 0, h(2) = 0, no witness
% Version  : Especial.
% English  : Using an axiomatiztion of if-then-else, find the if-then-else
%            term that expresses the function H.

% Refs     : [Rie16] Riener (2016), Email to Geoff Sutcliffe
% Source   : [TPTP]
% Names    : ntape6-1-without-witness.tptp [Rie16]

% Status   : Theorem
% Rating   : 0.86 v6.4.0
% Syntax   : Number of formulae    :    4 (   0 unit;   3 type;   0 defn)
%            Number of atoms       :   38 (   7 equality;  16 variable)
%            Maximal formula depth :   12 (   6 average)
%            Number of connectives :   26 (   3   ~;   0   |;   5   &;  15   @)
%                                         (   0 <=>;   3  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&)
%            Number of type conns  :    5 (   5   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    5 (   3   :;   0   =)
%            Number of variables   :    9 (   0 sgn;   8   !;   1   ?;   0   ^)
%                                         (   9   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU_NAR

% Comments :
%------------------------------------------------------------------------------
thf(n6,type,(
    zero: $i )).

thf(n7,type,(
    s: $i > $i )).

thf(n8,type,(
    ite: $o > $i > $i > $i )).

thf(n9,conjecture,
    ( ( ! [X100: $o,U: $i,V: $i] :
          ( X100
         => ( ( ite @ X100 @ U @ V )
            = U ) )
      & ! [X100: $o,U: $i,V: $i] :
          ( ~ ( X100 )
         => ( ( ite @ X100 @ U @ V )
            = V ) )
      & ! [X: $i] :
          ( ( s @ X )
         != zero )
      & ! [X: $i] :
          ( ( s @ X )
         != X ) )
   => ? [H: $i > $i] :
        ( ( ( H @ zero )
          = ( s @ zero ) )
        & ( ( H @ ( s @ zero ) )
          = zero )
        & ( ( H @ ( s @ ( s @ zero ) ) )
          = zero ) ) )).

%------------------------------------------------------------------------------
