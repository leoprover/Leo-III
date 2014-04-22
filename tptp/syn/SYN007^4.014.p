%------------------------------------------------------------------------------
% File     : SYN007^4.014 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Logic Calculi (Intuitionistic logic)
% Problem  : Pelletier Problem 71
% Version  : [Goe33] axioms.
% English  :

% Refs     : [Goe33] Goedel (1933), An Interpretation of the Intuitionistic
%          : [Gol06] Goldblatt (2006), Mathematical Modal Logic: A View of
%          : [ROK06] Raths et al. (2006), The ILTP Problem Library for Intu
%          : [Ben09] Benzmueller (2009), Email to Geoff Sutcliffe
%          : [BP10]  Benzmueller & Paulson (2009), Exploring Properties of
% Source   : [Ben09]
% Names    :

% Status   : CounterSatisfiable
% Rating   : 1.00 v4.0.0
% Syntax   : Number of formulae    :   56 (   0 unit;  34 type;  19 defn)
%            Number of atoms       :  312 (  19 equality;  48 variable)
%            Maximal formula depth :   31 (   5 average)
%            Number of connectives :  138 (   3   ~;   1   |;   2   &; 130   @)
%                                         (   0 <=>;   2  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :  109 ( 109   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   38 (  34   :)
%            Number of variables   :   40 (   1 sgn;   7   !;   2   ?;  31   ^)
%                                         (  40   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_CSA_EQU

% Comments : This is an ILTP problem embedded in TH0
%          : In classical logic this is a Theorem.
%------------------------------------------------------------------------------
include('Axioms/LCL010^0.ax').
%------------------------------------------------------------------------------
thf(p_1_type,type,(
    p_1: $i > $o )).

thf(p_10_type,type,(
    p_10: $i > $o )).

thf(p_11_type,type,(
    p_11: $i > $o )).

thf(p_12_type,type,(
    p_12: $i > $o )).

thf(p_13_type,type,(
    p_13: $i > $o )).

thf(p_14_type,type,(
    p_14: $i > $o )).

thf(p_2_type,type,(
    p_2: $i > $o )).

thf(p_3_type,type,(
    p_3: $i > $o )).

thf(p_4_type,type,(
    p_4: $i > $o )).

thf(p_5_type,type,(
    p_5: $i > $o )).

thf(p_6_type,type,(
    p_6: $i > $o )).

thf(p_7_type,type,(
    p_7: $i > $o )).

thf(p_8_type,type,(
    p_8: $i > $o )).

thf(p_9_type,type,(
    p_9: $i > $o )).

thf(prove_this,conjecture,
    ( ivalid @ ( iequiv @ ( iatom @ p_1 ) @ ( iequiv @ ( iatom @ p_2 ) @ ( iequiv @ ( iatom @ p_3 ) @ ( iequiv @ ( iatom @ p_4 ) @ ( iequiv @ ( iatom @ p_5 ) @ ( iequiv @ ( iatom @ p_6 ) @ ( iequiv @ ( iatom @ p_7 ) @ ( iequiv @ ( iatom @ p_8 ) @ ( iequiv @ ( iatom @ p_9 ) @ ( iequiv @ ( iatom @ p_10 ) @ ( iequiv @ ( iatom @ p_11 ) @ ( iequiv @ ( iatom @ p_12 ) @ ( iequiv @ ( iatom @ p_13 ) @ ( iequiv @ ( iatom @ p_14 ) @ ( iequiv @ ( iatom @ p_1 ) @ ( iequiv @ ( iatom @ p_2 ) @ ( iequiv @ ( iatom @ p_3 ) @ ( iequiv @ ( iatom @ p_4 ) @ ( iequiv @ ( iatom @ p_5 ) @ ( iequiv @ ( iatom @ p_6 ) @ ( iequiv @ ( iatom @ p_7 ) @ ( iequiv @ ( iatom @ p_8 ) @ ( iequiv @ ( iatom @ p_9 ) @ ( iequiv @ ( iatom @ p_10 ) @ ( iequiv @ ( iatom @ p_11 ) @ ( iequiv @ ( iatom @ p_12 ) @ ( iequiv @ ( iatom @ p_13 ) @ ( iatom @ p_14 ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )).

%------------------------------------------------------------------------------
