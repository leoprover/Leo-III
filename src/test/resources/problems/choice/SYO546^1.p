%------------------------------------------------------------------------------
% File     : SYO546^1 : TPTP v6.3.0. Released v5.2.0.
% Domain   : Syntactic
% Problem  : Property of case from ($o>$o) to $i defined from choice on $i
% Version  : Especial.
% English  : A choice operator on $i is used to define an if-then-else operator
%            at $i. Check that case always returns one of the four given 
%            results.

% Refs     : [Bro11] Brown E. (2011), Email to Geoff Sutcliffe
% Source   : [Bro11]
% Names    : CHOICE22c [Bro11]

% Status   : Theorem
% Rating   : 0.67 v6.3.0, 0.80 v6.2.0, 0.86 v5.5.0, 0.83 v5.4.0, 0.80 v5.2.0
% Syntax   : Number of formulae    :    6 (   0 unit;   3 type;   1 defn)
%            Number of atoms       :   52 (  10 equality;  22 variable)
%            Maximal formula depth :   15 (   7 average)
%            Number of connectives :   17 (   0   ~;   3   |;   4   &;   9   @)
%                                         (   0 <=>;   1  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   11 (  11   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    8 (   3   :)
%            Number of variables   :   12 (   2 sgn;   2   !;   1   ?;   9   ^)
%                                         (  12   :;   0  !>;   0  ?*)
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

thf(caseoo,type,(
    case: ( $o > $o ) > $i > $i > $i > $i > $i )).

thf(caseood,definition,
    ( case
    = ( ^ [B: $o > $o,X: $i,Y: $i,U: $i,V: $i] :
          ( eps
          @ ^ [Z: $i] :
              ( ( ( B
                  = ( ^ [A: $o] : $false ) )
                & ( Z = X ) )
              | ( ( B = ~ )
                & ( Z = Y ) )
              | ( ( B
                  = ( ^ [A: $o] : A ) )
                & ( Z = U ) )
              | ( ( B
                  = ( ^ [A: $o] : $true ) )
                & ( Z = V ) ) ) ) ) )).

thf(f,type,(
    f: $o > $o )).

thf(conj,conjecture,(
    ! [X: $i] :
      ( ( case @ f @ X @ X @ X @ X )
      = X ) )).

%------------------------------------------------------------------------------
