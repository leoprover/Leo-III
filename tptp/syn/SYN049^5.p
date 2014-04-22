%------------------------------------------------------------------------------
% File     : SYN049^5 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Syntactic
% Problem  : TPS problem PELL19
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0188 [Bro09]
%          : PELL19 [TPS]
%          : Pelletier 19 [Pel86]

% Status   : Theorem
% Rating   : 0.17 v6.0.0, 0.00 v5.3.0, 0.25 v5.2.0, 0.00 v4.0.0
% Syntax   : Number of formulae    :    3 (   0 unit;   2 type;   0 defn)
%            Number of atoms       :   12 (   0 equality;   4 variable)
%            Maximal formula depth :    7 (   4 average)
%            Number of connectives :    7 (   0   ~;   0   |;   0   &;   4   @)
%                                         (   0 <=>;   3  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    2 (   2   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    4 (   2   :)
%            Number of variables   :    3 (   0 sgn;   2   !;   1   ?;   0   ^)
%                                         (   3   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : 
%------------------------------------------------------------------------------
thf(cQ,type,(
    cQ: $i > $o )).

thf(cP,type,(
    cP: $i > $o )).

thf(cPELL19,conjecture,(
    ? [Xx: $i] :
    ! [Xy: $i,Xz: $i] :
      ( ( ( cP @ Xy )
       => ( cQ @ Xz ) )
     => ( ( cP @ Xx )
       => ( cQ @ Xx ) ) ) )).

%------------------------------------------------------------------------------
