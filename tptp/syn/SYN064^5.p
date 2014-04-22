%------------------------------------------------------------------------------
% File     : SYN064^5 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Syntactic
% Problem  : TPS problem PELL35
% Version  : Especial.
% English  : 

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0090 [Bro09]
%          : PELL35 [TPS]
%          : Pelletier 35 [Pel86]

% Status   : Theorem
% Rating   : 0.17 v6.0.0, 0.00 v4.0.0
% Syntax   : Number of formulae    :    2 (   0 unit;   1 type;   0 defn)
%            Number of atoms       :    9 (   0 equality;   4 variable)
%            Maximal formula depth :    8 (   6 average)
%            Number of connectives :    5 (   0   ~;   0   |;   0   &;   4   @)
%                                         (   0 <=>;   1  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    2 (   2   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    3 (   1   :)
%            Number of variables   :    4 (   0 sgn;   2   !;   2   ?;   0   ^)
%                                         (   4   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : 
%------------------------------------------------------------------------------
thf(cP,type,(
    cP: $i > $i > $o )).

thf(cPELL35,conjecture,(
    ? [Xx: $i,Xy: $i] :
      ( ( cP @ Xx @ Xy )
     => ! [Xx0: $i,Xy0: $i] :
          ( cP @ Xx0 @ Xy0 ) ) )).

%------------------------------------------------------------------------------
