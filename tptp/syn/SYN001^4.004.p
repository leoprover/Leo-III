%------------------------------------------------------------------------------
% File     : SYN001^4.004 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Logic Calculi (Intuitionistic logic)
% Problem  : ILTP Problem SYJ212+1.004
% Version  : [Goe33] axioms.
% English  :

% Refs     : [Goe33] Goedel (1933), An Interpretation of the Intuitionistic
%          : [Gol06] Goldblatt (2006), Mathematical Modal Logic: A View of
%          : [ROK06] Raths et al. (2006), The ILTP Problem Library for Intu
%          : [Ben09] Benzmueller (2009), Email to Geoff Sutcliffe
%          : [BP10]  Benzmueller & Paulson (2009), Exploring Properties of
% Source   : [Ben09]
% Names    : SYJ212+1.004 [ROK06]

% Status   : CounterSatisfiable
% Rating   : 0.67 v5.4.0, 1.00 v4.0.0
% Syntax   : Number of formulae    :   46 (   0 unit;  24 type;  19 defn)
%            Number of atoms       :  234 (  19 equality;  48 variable)
%            Maximal formula depth :   13 (   5 average)
%            Number of connectives :   80 (   3   ~;   1   |;   2   &;  72   @)
%                                         (   0 <=>;   2  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   99 (  99   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   28 (  24   :)
%            Number of variables   :   40 (   1 sgn;   7   !;   2   ?;  31   ^)
%                                         (  40   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_CSA_EQU

% Comments : This is an ILTP problem embedded in TH0
%          : In classical logic this is a Theorem.
%------------------------------------------------------------------------------
include('Axioms/LCL010^0.ax').
%------------------------------------------------------------------------------
thf(a1_type,type,(
    a1: $i > $o )).

thf(a2_type,type,(
    a2: $i > $o )).

thf(a3_type,type,(
    a3: $i > $o )).

thf(a4_type,type,(
    a4: $i > $o )).

thf(con,conjecture,
    ( ivalid @ ( iequiv @ ( iequiv @ ( iequiv @ ( iequiv @ ( inot @ ( inot @ ( iatom @ a1 ) ) ) @ ( iatom @ a2 ) ) @ ( iatom @ a3 ) ) @ ( iatom @ a4 ) ) @ ( iequiv @ ( iatom @ a4 ) @ ( iequiv @ ( iatom @ a3 ) @ ( iequiv @ ( iatom @ a2 ) @ ( iatom @ a1 ) ) ) ) ) )).

%------------------------------------------------------------------------------
