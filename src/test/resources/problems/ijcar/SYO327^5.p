%------------------------------------------------------------------------------
% File     : SYO327^5 : TPTP v6.2.0. Released v4.0.0.
% Domain   : Syntactic
% Problem  : TPS problem from BASIC-HO-THMS
% Version  : Especial.
% English  :

% Refs     : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_1011 [Bro09]

% Status   : Theorem
% Rating   : 0.33 v6.0.0, 0.17 v5.5.0, 0.20 v5.4.0, 0.75 v5.0.0, 0.50 v4.1.0, 0.67 v4.0.0
% Syntax   : Number of formulae    :    5 (   1 unit;   4 type;   0 defn)
%            Number of atoms       :   49 (   0 equality;  20 variable)
%            Maximal formula depth :   15 (   6 average)
%            Number of connectives :   39 (   0   ~;   0   |;   5   &;  29   @)
%                                         (   0 <=>;   5  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    7 (   7   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    6 (   4   :)
%            Number of variables   :    9 (   0 sgn;   9   !;   0   ?;   0   ^)
%                                         (   9   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : 
%------------------------------------------------------------------------------
thf(cHALF,type,(
    cHALF: $i > $i > $o )).

thf(cDOUBLE,type,(
    cDOUBLE: $i > $i > $o )).

thf(cS,type,(
    cS: $i > $i )).

thf(c0,type,(
    c0: $i )).

thf(cDOUBLE_TO_HALF_6,conjecture,
    ( ( ! [Q: $i > $i > $o,Xu: $i,Xv: $i] :
          ( ( ( cDOUBLE @ Xu @ Xv )
            & ( Q @ c0 @ c0 )
            & ! [Xx: $i,Xy: $i] :
                ( ( Q @ Xx @ Xy )
               => ( Q @ ( cS @ Xx ) @ ( cS @ ( cS @ Xy ) ) ) ) )
         => ( Q @ Xu @ Xv ) )
      & ( cHALF @ c0 @ c0 )
      & ( cHALF @ c0 @ ( cS @ c0 ) )
      & ! [Xx: $i,Xy: $i] :
          ( ( cHALF @ Xx @ Xy )
         => ( cHALF @ ( cS @ ( cS @ Xx ) ) @ ( cS @ Xy ) ) ) )
   => ! [Xu: $i,Xv: $i] :
        ( ( cDOUBLE @ Xu @ Xv )
       => ( cHALF @ Xv @ Xu ) ) )).

%------------------------------------------------------------------------------
