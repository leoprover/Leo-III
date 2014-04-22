%------------------------------------------------------------------------------
% File     : SYN047^4 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Logic Calculi (Intuitionistic logic)
% Problem  : Pelletier Problem 17
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
% Rating   : 0.67 v5.4.0, 1.00 v4.0.0
% Syntax   : Number of formulae    :   46 (   0 unit;  24 type;  19 defn)
%            Number of atoms       :  241 (  19 equality;  48 variable)
%            Maximal formula depth :   10 (   5 average)
%            Number of connectives :   87 (   3   ~;   1   |;   2   &;  79   @)
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
thf(p_type,type,(
    p: $i > $o )).

thf(q_type,type,(
    q: $i > $o )).

thf(r_type,type,(
    r: $i > $o )).

thf(s_type,type,(
    s: $i > $o )).

thf(pel17,conjecture,
    ( ivalid @ ( iequiv @ ( iimplies @ ( iand @ ( iatom @ p ) @ ( iimplies @ ( iatom @ q ) @ ( iatom @ r ) ) ) @ ( iatom @ s ) ) @ ( iand @ ( ior @ ( inot @ ( iatom @ p ) ) @ ( ior @ ( iatom @ q ) @ ( iatom @ s ) ) ) @ ( ior @ ( inot @ ( iatom @ p ) ) @ ( ior @ ( inot @ ( iatom @ r ) ) @ ( iatom @ s ) ) ) ) ) )).

%------------------------------------------------------------------------------
