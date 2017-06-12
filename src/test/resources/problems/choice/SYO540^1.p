%------------------------------------------------------------------------------
% File     : SYO540^1 : TPTP v6.3.0. Released v5.2.0.
% Domain   : Syntactic
% Problem  : Property of if-then-else on $i defined from choice on $i
% Version  : Especial.
% English  : A choice operator on $i is used to define an if-then-else operator
%            at $i. Check that if the then-part and the else-part are both X, 
%            it returns X.

% Refs     : [Bro11] Brown E. (2011), Email to Geoff Sutcliffe
% Source   : [Bro11]
% Names    : CHOICE15c [Bro11]

% Status   : Theorem
% Rating   : 0.17 v6.3.0, 0.20 v6.2.0, 0.43 v6.1.0, 0.29 v5.5.0, 0.33 v5.4.0, 0.40 v5.2.0
% Syntax   : Number of formulae    :    5 (   0 unit;   2 type;   1 defn)
%            Number of atoms       :   29 (   4 equality;  14 variable)
%            Maximal formula depth :   10 (   6 average)
%            Number of connectives :   12 (   1   ~;   1   |;   2   &;   7   @)
%                                         (   0 <=>;   1  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    6 (   6   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    5 (   2   :)
%            Number of variables   :    8 (   0 sgn;   3   !;   1   ?;   4   ^)
%                                         (   8   :;   0  !>;   0  ?*)
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
    ! [B: $o,X: $i] :
      ( ( if @ B @ X @ X )
      = X ) )).

%------------------------------------------------------------------------------
