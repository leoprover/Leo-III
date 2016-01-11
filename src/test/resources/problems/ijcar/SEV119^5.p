%------------------------------------------------------------------------------
% File     : SEV119^5 : TPTP v6.2.0. Released v4.0.0.
% Domain   : Set Theory (Relations)
% Problem  : TPS problem THM252
% Version  : Especial.
% English  :

% Refs     : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0371 [Bro09]
%          : THM252 [TPS]
%          : THM252C [TPS]

% Status   : Theorem
% Rating   : 0.29 v5.5.0, 0.33 v5.4.0, 0.60 v4.1.0, 0.67 v4.0.0
% Syntax   : Number of formulae    :    2 (   1 unit;   1 type;   0 defn)
%            Number of atoms       :   46 (   1 equality;  44 variable)
%            Maximal formula depth :   22 (  12 average)
%            Number of connectives :   42 (   0   ~;   2   |;   4   &;  28   @)
%                                         (   0 <=>;   8  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   15 (  15   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    3 (   1   :)
%            Number of variables   :   19 (   0 sgn;  15   !;   0   ?;   4   ^)
%                                         (  19   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : Polymorphic definitions expanded.
%          : 
%------------------------------------------------------------------------------
thf(a_type,type,(
    a: $tType )).

thf(cTHM252_pme,conjecture,(
    ! [PROP: ( a > a > $o ) > $o,R: a > a > $o,S: a > a > $o] :
      ( ( ^ [Xx: a,Xy: a] :
          ! [Xp: a > a > $o] :
            ( ( ! [Xx0: a,Xy0: a] :
                  ( ( ( R @ Xx0 @ Xy0 )
                    | ( S @ Xx0 @ Xy0 ) )
                 => ( Xp @ Xx0 @ Xy0 ) )
              & ( PROP @ Xp ) )
           => ( Xp @ Xx @ Xy ) ) )
      = ( ^ [Xx: a,Xy: a] :
          ! [Xp: a > a > $o] :
            ( ( ! [Xx0: a,Xy0: a] :
                  ( ( ! [Xp0: a > a > $o] :
                        ( ( ! [Xx1: a,Xy1: a] :
                              ( ( R @ Xx1 @ Xy1 )
                             => ( Xp0 @ Xx1 @ Xy1 ) )
                          & ( PROP @ Xp0 ) )
                       => ( Xp0 @ Xx0 @ Xy0 ) )
                    | ! [Xp0: a > a > $o] :
                        ( ( ! [Xx1: a,Xy1: a] :
                              ( ( S @ Xx1 @ Xy1 )
                             => ( Xp0 @ Xx1 @ Xy1 ) )
                          & ( PROP @ Xp0 ) )
                       => ( Xp0 @ Xx0 @ Xy0 ) ) )
                 => ( Xp @ Xx0 @ Xy0 ) )
              & ( PROP @ Xp ) )
           => ( Xp @ Xx @ Xy ) ) ) ) )).

%------------------------------------------------------------------------------
