%------------------------------------------------------------------------------
% File     : SEU968^5 : TPTP v6.2.0. Released v4.0.0.
% Domain   : Set Theory (Functions)
% Problem  : TPS problem from FUNCTION-THMS
% Version  : Especial.
% English  :

% Refs     : [Bro09] Brown (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : tps_1066 [Bro09]

% Status   : Theorem
% Rating   : 0.57 v5.5.0, 0.67 v5.4.0, 0.80 v5.3.0, 1.00 v5.2.0, 0.80 v4.1.0, 0.67 v4.0.0
% Syntax   : Number of formulae    :    2 (   1 unit;   1 type;   0 defn)
%            Number of atoms       :   37 (   1 equality;  35 variable)
%            Maximal formula depth :   16 (   9 average)
%            Number of connectives :   33 (   0   ~;   0   |;   4   &;  21   @)
%                                         (   0 <=>;   8  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   13 (  13   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    3 (   1   :)
%            Number of variables   :   18 (   0 sgn;  10   !;   0   ?;   8   ^)
%                                         (  18   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : This problem is from the TPS library. Copyright (c) 2009 The TPS
%            project in the Department of Mathematical Sciences at Carnegie
%            Mellon University. Distributed under the Creative Commons copyleft
%            license: http://creativecommons.org/licenses/by-sa/3.0/
%          : 
%------------------------------------------------------------------------------
thf(a_type,type,(
    a: $tType )).

thf(cTHM135C_pme,conjecture,
    ( ! [Xg: a > a] :
        ( ( ^ [Xx: a] :
              ( Xg @ Xx ) )
        = Xg )
   => ! [Xf: a > a,Xg1: a > a,Xg2: a > a] :
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
