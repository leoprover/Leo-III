%------------------------------------------------------------------------------
% File     : SYO556^1 : TPTP v6.3.0. Released v5.2.0.
% Domain   : Syntactic
% Problem  : Relationship between if-then-else and choice on $
% Version  : Especial.
% English  :

% Refs     : [Bro11] Brown (2011), Email to Geoff Sutcliffe
% Source   : [Bro11]
% Names    : CHOICE28 [Bro11]

% Status   : Theorem
% Rating   : 0.50 v6.3.0, 0.60 v6.2.0, 0.57 v6.0.0, 0.71 v5.5.0, 0.67 v5.4.0, 0.80 v5.2.0
% Syntax   : Number of formulae    :    5 (   0 unit;   2 type;   1 defn)
%            Number of atoms       :   33 (   4 equality;  14 variable)
%            Maximal formula depth :   10 (   6 average)
%            Number of connectives :   16 (   1   ~;   1   |;   2   &;  11   @)
%                                         (   0 <=>;   1  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    7 (   7   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    6 (   2   :)
%            Number of variables   :    9 (   1 sgn;   2   !;   2   ?;   5   ^)
%                                         (   9   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : A choice operator eps on $i is assumed. if-then-else on $i is 
%            defined using eps. eps satisfies the following equation: 
%            eps P = if (P nonempty) (eps P) (eps emptypred)
%------------------------------------------------------------------------------
thf(eps,type,(
    eps: ( $i > $o ) > $i )).

thf(choiceax,axiom,(
    ! [P: $i > $o] :
      ( ? [X: $i] :
          ( P @ X )
     => ( P @ ( eps @ P ) ) ) )).

thf(if,type,(
    if: $o > $i > $i > $i )).

thf(ifd,definition,
    ( if
    = ( ^ [B: $o,X: $i,Y: $i] :
          ( eps
          @ ^ [Z: $i] :
              ( ( B
                & ( Z = X ) )
              | ( ~ ( B )
                & ( Z = Y ) ) ) ) ) )).

thf(conj,conjecture,(
    ! [P: $i > $o] :
      ( ( eps @ P )
      = ( if
        @ ? [X: $i] :
            ( P @ X )
        @ ( eps @ P )
        @ ( eps
          @ ^ [X: $i] : $false ) ) ) )).

%------------------------------------------------------------------------------
