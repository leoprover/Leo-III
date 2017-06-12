%------------------------------------------------------------------------------
% File     : SYO530^1 : TPTP v6.3.0. Released v5.2.0.
% Domain   : Syntactic
% Problem  : Binary choice on individuals
% Version  : Especial.
% English  : epsa and epsb work together to give an a and b such that R a b 
%            holds, if such an a and b exist for a binary relation R on $i.
%            A choice operator on i can be used to define a choice operator on
%            i*i (Curried). In this version, the solution is given and the 
%            goal is to check that it works.

% Refs     : [Bac10] Backes (2010), Tableaux for Higher-Order Logic with If
%          : [Bro11] Brown E. (2011), Email to Geoff Sutcliffe
% Source   : [Bro11]
% Names    : CHOICE7 [Bro11]

% Status   : Theorem
% Rating   : 0.17 v6.3.0, 0.20 v6.2.0, 0.43 v6.1.0, 0.29 v5.5.0, 0.50 v5.4.0, 0.60 v5.2.0
% Syntax   : Number of formulae    :    7 (   0 unit;   3 type;   2 defn)
%            Number of atoms       :   37 (   2 equality;  16 variable)
%            Maximal formula depth :    8 (   6 average)
%            Number of connectives :   18 (   0   ~;   0   |;   0   &;  16   @)
%                                         (   0 <=>;   2  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   15 (  15   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    5 (   3   :)
%            Number of variables   :   10 (   0 sgn;   2   !;   4   ?;   4   ^)
%                                         (  10   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : 
%------------------------------------------------------------------------------
thf(eps,type,(
    eps: ( $i > $o ) > $i )).

thf(choiceax,axiom,(
    ! [P: $i > $o] :
      ( ? [X: $i] :
          ( P @ X )
     => ( P @ ( eps @ P ) ) ) )).

thf(epsa,type,(
    epsa: ( $i > $i > $o ) > $i )).

thf(epsad,definition,
    ( epsa
    = ( ^ [R: $i > $i > $o] :
          ( eps
          @ ^ [X: $i] :
            ? [Y: $i] :
              ( R @ X @ Y ) ) ) )).

thf(epsb,type,(
    epsb: ( $i > $i > $o ) > $i )).

thf(epsbd,definition,
    ( epsb
    = ( ^ [R: $i > $i > $o] :
          ( eps
          @ ^ [Y: $i] :
              ( R @ ( epsa @ R ) @ Y ) ) ) )).

thf(conj,conjecture,(
    ! [R: $i > $i > $o] :
      ( ? [X: $i,Y: $i] :
          ( R @ X @ Y )
     => ( R @ ( epsa @ R ) @ ( epsb @ R ) ) ) )).

%------------------------------------------------------------------------------
