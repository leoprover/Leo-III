%------------------------------------------------------------------------------
% File     : SYN044^4 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Logic Calculi (Intuitionistic logic)
% Problem  : Pelletier Problem 10
% Version  : [Goe33] axioms.
% English  :

% Refs     : [Goe33] Goedel (1933), An Interpretation of the Intuitionistic
%          : [Gol06] Goldblatt (2006), Mathematical Modal Logic: A View of
%          : [ROK06] Raths et al. (2006), The ILTP Problem Library for Intu
%          : [Ben09] Benzmueller (2009), Email to Geoff Sutcliffe
%          : [BP10]  Benzmueller & Paulson (2009), Exploring Properties of
% Source   : [Ben09]
% Names    :

% Status   : Theorem
% Rating   : 0.14 v5.5.0, 0.17 v5.4.0, 0.20 v5.3.0, 0.40 v5.1.0, 0.60 v5.0.0, 0.40 v4.1.0, 0.33 v4.0.1, 0.67 v4.0.0
% Syntax   : Number of formulae    :   48 (   0 unit;  23 type;  19 defn)
%            Number of atoms       :  236 (  19 equality;  48 variable)
%            Maximal formula depth :    8 (   5 average)
%            Number of connectives :   81 (   3   ~;   1   |;   2   &;  73   @)
%                                         (   0 <=>;   2  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   98 (  98   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   27 (  23   :)
%            Number of variables   :   40 (   1 sgn;   7   !;   2   ?;  31   ^)
%                                         (  40   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : This is an ILTP problem embedded in TH0
%------------------------------------------------------------------------------
include('Axioms/LCL010^0.ax').
%------------------------------------------------------------------------------
thf(p_type,type,(
    p: $i > $o )).

thf(q_type,type,(
    q: $i > $o )).

thf(r_type,type,(
    r: $i > $o )).

thf(pel10_1,axiom,
    ( ivalid @ ( iimplies @ ( iatom @ q ) @ ( iatom @ r ) ) )).

thf(pel10_2,axiom,
    ( ivalid @ ( iimplies @ ( iatom @ r ) @ ( iand @ ( iatom @ p ) @ ( iatom @ q ) ) ) )).

thf(pel10_3,axiom,
    ( ivalid @ ( iimplies @ ( iatom @ p ) @ ( ior @ ( iatom @ q ) @ ( iatom @ r ) ) ) )).

thf(pel10,conjecture,
    ( ivalid @ ( iequiv @ ( iatom @ p ) @ ( iatom @ q ) ) )).

%------------------------------------------------------------------------------
