%------------------------------------------------------------------------------
% File     : SYO519^1 : TPTP v6.3.0. Released v4.1.0.
% Domain   : Syntactic
% Problem  : For any X,Y:i, there is a function swapping X and Y
% Version  : Especial.
% English  : 

% Refs     : [Bro09] Brown E. (2009), Email to Geoff Sutcliffe
% Source   : [Bro09]
% Names    : swapi [Bro09]

% Status   : Theorem
% Rating   : 1.00 v4.1.0
% Syntax   : Number of formulae    :    1 (   0 unit;   0 type;   0 defn)
%            Number of atoms       :    8 (   2 equality;   6 variable)
%            Maximal formula depth :    7 (   7 average)
%            Number of connectives :    3 (   0   ~;   0   |;   1   &;   2   @)
%                                         (   0 <=>;   0  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :    1 (   1   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :    1 (   0   :)
%            Number of variables   :    3 (   0 sgn;   2   !;   1   ?;   0   ^)
%                                         (   3   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : This is a theorem because the default semantics is Henkin with
%            choice.
%------------------------------------------------------------------------------
thf(ifi,conjecture,(
    ! [X: $i,Y: $i] :
    ? [F: $i > $i] :
      ( ( ( F @ X )
        = Y )
      & ( ( F @ Y )
        = X ) ) )).

%------------------------------------------------------------------------------
