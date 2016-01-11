%------------------------------------------------------------------------------
% File     : SYO262^5 : TPTP v6.2.0. Released v4.0.0.
% Domain   : Syntactic
% Problem  : TPS problem THM19SK1
% Version  : Especial.
% English  :

% Refs     : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0152 [Bro09]
%          : THM19SK1 [TPS]

% Status   : Theorem
% Rating   : 0.33 v6.0.0, 0.17 v5.5.0, 0.00 v4.0.1, 0.33 v4.0.0
% Syntax   : Number of formulae    :    3 (   0 unit;   2 type;   0 defn)
%            Number of atoms       :   24 (   0 equality;  10 variable)
%            Maximal formula depth :   12 (   7 average)
%            Number of connectives :   21 (   4   ~;   0   |;   1   &;  16   @)
%                                         (   0 <=>;   0  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    5 (   5   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    5 (   2   :)
%            Number of variables   :    5 (   0 sgn;   2   !;   0   ?;   3   ^)
%                                         (   5   :;   0  !>;   0  ?*)
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

thf(cE,type,(
    cE: ( $i > $o ) > $i )).

thf(cTHM19SK1,conjecture,(
    ~ ( ! [Xx: $i] :
          ( cP @ Xx
          @ ( cE
            @ ^ [Xy: $i] :
                ( cP @ Xx @ Xy ) ) )
      & ! [Xf: $i > $i] :
          ~ ( cP
            @ ( cE
              @ ^ [Xx: $i] :
                  ~ ( cP @ Xx @ ( Xf @ Xx ) ) )
            @ ( Xf
              @ ( cE
                @ ^ [Xx: $i] :
                    ~ ( cP @ Xx @ ( Xf @ Xx ) ) ) ) ) ) )).

%------------------------------------------------------------------------------
