%------------------------------------------------------------------------------
% File     : SYN041^4 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Logic Calculi (Intuitionistic logic)
% Problem  : Pelletier Problem 3
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
% Rating   : 0.14 v5.5.0, 0.17 v5.4.0, 0.20 v5.3.0, 0.40 v5.2.0, 0.20 v5.1.0, 0.40 v5.0.0, 0.20 v4.1.0, 0.00 v4.0.1, 0.33 v4.0.0
% Syntax   : Number of formulae    :   44 (   0 unit;  22 type;  19 defn)
%            Number of atoms       :  217 (  19 equality;  48 variable)
%            Maximal formula depth :    8 (   5 average)
%            Number of connectives :   67 (   3   ~;   1   |;   2   &;  59   @)
%                                         (   0 <=>;   2  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   97 (  97   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   26 (  22   :)
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

thf(pel3,conjecture,
    ( ivalid @ ( iimplies @ ( inot @ ( iimplies @ ( iatom @ p ) @ ( iatom @ q ) ) ) @ ( iimplies @ ( iatom @ q ) @ ( iatom @ p ) ) ) )).

%------------------------------------------------------------------------------
