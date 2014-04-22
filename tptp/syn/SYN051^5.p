%------------------------------------------------------------------------------
% File     : SYN051^5 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Syntactic
% Problem  : TPS problem PELL21
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0129 [Bro09]
%          : PELL21 [TPS]
%          : Pelletier 21 [Pel86]

% Status   : Theorem
% Rating   : 0.17 v6.0.0, 0.00 v5.3.0, 0.25 v5.2.0, 0.00 v4.0.0
% Syntax   : Number of formulae    :    3 (   1 unit;   2 type;   0 defn)
%            Number of atoms       :   15 (   0 equality;   4 variable)
%            Maximal formula depth :    6 (   4 average)
%            Number of connectives :   11 (   0   ~;   0   |;   2   &;   4   @)
%                                         (   0 <=>;   5  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    1 (   1   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    4 (   2   :)
%            Number of variables   :    3 (   0 sgn;   0   !;   3   ?;   0   ^)
%                                         (   3   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : 
%------------------------------------------------------------------------------
thf(p,type,(
    p: $o )).

thf(cF,type,(
    cF: $i > $o )).

thf(cPELL21,conjecture,
    ( ( ? [Xx: $i] :
          ( p
         => ( cF @ Xx ) )
      & ? [Xx: $i] :
          ( ( cF @ Xx )
         => p ) )
   => ? [Xx: $i] :
        ( ( p
         => ( cF @ Xx ) )
        & ( ( cF @ Xx )
         => p ) ) )).

%------------------------------------------------------------------------------
