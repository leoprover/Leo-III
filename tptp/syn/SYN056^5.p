%------------------------------------------------------------------------------
% File     : SYN056^5 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Syntactic
% Problem  : TPS problem PELL26
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0368 [Bro09]
%          : PELL26 [TPS]
%          : Pelletier 26 [Pel86]

% Status   : Theorem
% Rating   : 0.17 v6.0.0, 0.00 v5.3.0, 0.25 v5.2.0, 0.00 v4.0.0
% Syntax   : Number of formulae    :    5 (   0 unit;   4 type;   0 defn)
%            Number of atoms       :   44 (   0 equality;  18 variable)
%            Maximal formula depth :   10 (   4 average)
%            Number of connectives :   35 (   0   ~;   0   |;   5   &;  18   @)
%                                         (   0 <=>;  12  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    4 (   4   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    6 (   4   :)
%            Number of variables   :   10 (   0 sgn;   6   !;   4   ?;   0   ^)
%                                         (  10   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : 
%------------------------------------------------------------------------------
thf(cR,type,(
    cR: $i > $o )).

thf(cP,type,(
    cP: $i > $o )).

thf(cS,type,(
    cS: $i > $o )).

thf(cQ,type,(
    cQ: $i > $o )).

thf(cPELL26,conjecture,
    ( ( ( ? [Xx: $i] :
            ( cP @ Xx )
       => ? [Xx: $i] :
            ( cQ @ Xx ) )
      & ( ? [Xx: $i] :
            ( cQ @ Xx )
       => ? [Xx: $i] :
            ( cP @ Xx ) )
      & ! [Xx: $i,Xy: $i] :
          ( ( ( cP @ Xx )
            & ( cQ @ Xy ) )
         => ( ( ( cR @ Xx )
             => ( cS @ Xy ) )
            & ( ( cS @ Xy )
             => ( cR @ Xx ) ) ) ) )
   => ( ( ! [Xx: $i] :
            ( ( cP @ Xx )
           => ( cR @ Xx ) )
       => ! [Xx: $i] :
            ( ( cQ @ Xx )
           => ( cS @ Xx ) ) )
      & ( ! [Xx: $i] :
            ( ( cQ @ Xx )
           => ( cS @ Xx ) )
       => ! [Xx: $i] :
            ( ( cP @ Xx )
           => ( cR @ Xx ) ) ) ) )).

%------------------------------------------------------------------------------
