%------------------------------------------------------------------------------
% File     : SYN055^5 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Syntactic
% Problem  : TPS problem PELL25
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0272 [Bro09]
%          : PELL25 [TPS]
%          : Pelletier 25 [Pel86]

% Status   : Theorem
% Rating   : 0.17 v6.0.0, 0.00 v5.3.0, 0.25 v5.2.0, 0.00 v4.0.0
% Syntax   : Number of formulae    :    6 (   0 unit;   5 type;   0 defn)
%            Number of atoms       :   36 (   0 equality;  13 variable)
%            Maximal formula depth :    9 (   4 average)
%            Number of connectives :   26 (   1   ~;   1   |;   7   &;  13   @)
%                                         (   0 <=>;   4  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    5 (   5   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    8 (   5   :)
%            Number of variables   :    6 (   0 sgn;   3   !;   3   ?;   0   ^)
%                                         (   6   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : 
%------------------------------------------------------------------------------
thf(cP,type,(
    cP: $i > $o )).

thf(cQ,type,(
    cQ: $i > $o )).

thf(cR,type,(
    cR: $i > $o )).

thf(cF,type,(
    cF: $i > $o )).

thf(cG,type,(
    cG: $i > $o )).

thf(cPELL25,conjecture,
    ( ( ? [Xx: $i] :
          ( cP @ Xx )
      & ! [Xx: $i] :
          ( ( cF @ Xx )
         => ( ~ ( cG @ Xx )
            & ( cR @ Xx ) ) )
      & ! [Xx: $i] :
          ( ( cP @ Xx )
         => ( ( cG @ Xx )
            & ( cF @ Xx ) ) )
      & ( ! [Xx: $i] :
            ( ( cP @ Xx )
           => ( cQ @ Xx ) )
        | ? [Xx: $i] :
            ( ( cP @ Xx )
            & ( cR @ Xx ) ) ) )
   => ? [Xx: $i] :
        ( ( cQ @ Xx )
        & ( cP @ Xx ) ) )).

%------------------------------------------------------------------------------
