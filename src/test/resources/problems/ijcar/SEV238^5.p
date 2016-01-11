%------------------------------------------------------------------------------
% File     : SEV238^5 : TPTP v6.2.0. Released v4.0.0.
% Domain   : Set Theory (Sets of sets)
% Problem  : TPS problem THM2D
% Version  : Especial.
% English  :

% Refs     : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0480 [Bro09]
%          : THM2D [TPS]

% Status   : Theorem
% Rating   : 0.33 v6.2.0, 0.17 v6.1.0, 0.33 v6.0.0, 0.17 v5.5.0, 0.20 v5.4.0, 0.25 v4.1.0, 0.67 v4.0.1, 0.33 v4.0.0
% Syntax   : Number of formulae    :    1 (   0 unit;   0 type;   0 defn)
%            Number of atoms       :   93 (   0 equality;  93 variable)
%            Maximal formula depth :   18 (  18 average)
%            Number of connectives :   92 (   0   ~;   0   |;  12   &;  61   @)
%                                         (   1 <=>;  18  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   14 (  14   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    2 (   0   :)
%            Number of variables   :   36 (   0 sgn;  18   !;  10   ?;   8   ^)
%                                         (  36   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : Polymorphic definitions expanded.
%          : 
%------------------------------------------------------------------------------
thf(cTHM2D_pme,conjecture,(
    ! [K: ( $i > $o ) > $i > $o] :
      ( ( ! [Xx: $i > $o] :
            ( ! [Xx0: $i] :
                ( ( Xx @ Xx0 )
               => ? [S: $i > $o] :
                    ( ! [Xx1: $i] :
                        ( ( S @ Xx1 )
                       => ( K @ S @ Xx1 ) )
                    & ( S @ Xx0 ) ) )
           => ! [Xx0: $i] :
                ( ( K @ Xx @ Xx0 )
               => ( K
                  @ ^ [Xx1: $i] :
                    ? [S: $i > $o] :
                      ( ! [Xx2: $i] :
                          ( ( S @ Xx2 )
                         => ( K @ S @ Xx2 ) )
                      & ( S @ Xx1 ) )
                  @ Xx0 ) ) )
        & ( ! [Xx: $i] :
              ( ? [S: $i > $o] :
                  ( ! [Xx0: $i] :
                      ( ( S @ Xx0 )
                     => ( K @ S @ Xx0 ) )
                  & ( S @ Xx ) )
             => ( K
                @ ^ [Xx0: $i] :
                  ? [S: $i > $o] :
                    ( ! [Xx1: $i] :
                        ( ( S @ Xx1 )
                       => ( K @ S @ Xx1 ) )
                    & ( S @ Xx0 ) )
                @ Xx ) )
         => ! [Xx: $i] :
              ( ( K
                @ ^ [Xx0: $i] :
                  ? [S: $i > $o] :
                    ( ! [Xx1: $i] :
                        ( ( S @ Xx1 )
                       => ( K @ S @ Xx1 ) )
                    & ( S @ Xx0 ) )
                @ Xx )
             => ( K
                @ ( K
                  @ ^ [Xx0: $i] :
                    ? [S: $i > $o] :
                      ( ! [Xx1: $i] :
                          ( ( S @ Xx1 )
                         => ( K @ S @ Xx1 ) )
                      & ( S @ Xx0 ) ) )
                @ Xx ) ) ) )
     => ! [Xx: $i] :
          ( ( K
            @ ^ [Xx0: $i] :
              ? [S: $i > $o] :
                ( ! [Xx1: $i] :
                    ( ( S @ Xx1 )
                   => ( K @ S @ Xx1 ) )
                & ( S @ Xx0 ) )
            @ Xx )
        <=> ( ! [Xx0: $i] :
                ( ( K
                  @ ^ [Xx1: $i] :
                    ? [S: $i > $o] :
                      ( ! [Xx2: $i] :
                          ( ( S @ Xx2 )
                         => ( K @ S @ Xx2 ) )
                      & ( S @ Xx1 ) )
                  @ Xx0 )
               => ( K
                  @ ( K
                    @ ^ [Xx1: $i] :
                      ? [S: $i > $o] :
                        ( ! [Xx2: $i] :
                            ( ( S @ Xx2 )
                           => ( K @ S @ Xx2 ) )
                        & ( S @ Xx1 ) ) )
                  @ Xx0 ) )
            & ( K
              @ ^ [Xx0: $i] :
                ? [S: $i > $o] :
                  ( ! [Xx1: $i] :
                      ( ( S @ Xx1 )
                     => ( K @ S @ Xx1 ) )
                  & ( S @ Xx0 ) )
              @ Xx ) ) ) ) )).

%------------------------------------------------------------------------------
