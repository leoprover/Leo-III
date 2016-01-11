%------------------------------------------------------------------------------
% File     : SEU948^5 : TPTP v6.2.0. Released v4.0.0.
% Domain   : Set Theory (Functions)
% Problem  : TPS problem THM135
% Version  : Especial.
% English  : The composition of iterates of a function is also an iterate of
%            that function. 

% Refs     : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_0547 [Bro09]
%          : THM135 [TPS]
%          : tps_1048 [Bro09]

% Status   : Theorem
%          : Without eta : CounterSatisfiable
% Rating   : 0.33 v6.2.0, 0.50 v5.5.0, 0.60 v5.4.0, 0.75 v5.3.0, 1.00 v5.2.0, 0.75 v4.1.0, 0.67 v4.0.0
% Syntax   : Number of formulae    :    2 (   1 unit;   1 type;   0 defn)
%            Number of atoms       :   33 (   0 equality;  32 variable)
%            Maximal formula depth :   15 (   8 average)
%            Number of connectives :   31 (   0   ~;   0   |;   4   &;  20   @)
%                                         (   0 <=>;   7  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   12 (  12   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    3 (   1   :)
%            Number of variables   :   16 (   0 sgn;   9   !;   0   ?;   7   ^)
%                                         (  16   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_NEQ

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : Polymorphic definitions expanded.
%          : 
%------------------------------------------------------------------------------
thf(a_type,type,(
    a: $tType )).

thf(cTHM135_pme,conjecture,(
    ! [Xf: a > a,Xg1: a > a,Xg2: a > a] :
      ( ( ! [Xp: ( a > a ) > $o] :
            ( ( ( Xp
                @ ^ [Xu: a] : Xu )
              & ! [Xj: a > a] :
                  ( ( Xp @ Xj )
                 => ( Xp
                    @ ^ [Xx: a] :
                        ( Xf @ ( Xj @ Xx ) ) ) ) )
           => ( Xp @ Xg1 ) )
        & ! [Xp: ( a > a ) > $o] :
            ( ( ( Xp
                @ ^ [Xu: a] : Xu )
              & ! [Xj: a > a] :
                  ( ( Xp @ Xj )
                 => ( Xp
                    @ ^ [Xx: a] :
                        ( Xf @ ( Xj @ Xx ) ) ) ) )
           => ( Xp @ Xg2 ) ) )
     => ! [Xp: ( a > a ) > $o] :
          ( ( ( Xp
              @ ^ [Xu: a] : Xu )
            & ! [Xj: a > a] :
                ( ( Xp @ Xj )
               => ( Xp
                  @ ^ [Xx: a] :
                      ( Xf @ ( Xj @ Xx ) ) ) ) )
         => ( Xp
            @ ^ [Xx: a] :
                ( Xg1 @ ( Xg2 @ Xx ) ) ) ) ) )).

%------------------------------------------------------------------------------
