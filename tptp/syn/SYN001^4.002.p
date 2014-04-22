%------------------------------------------------------------------------------
% File     : SYN001^4.002 : TPTP v6.0.0. Released v4.0.0.
% Domain   : Logic Calculi (Intuitionistic logic)
% Problem  : ILTP Problem SYJ212+1.002
% Version  : [Goe33] axioms.
% English  :

% Refs     : [Goe33] Goedel (1933), An Interpretation of the Intuitionistic
%          : [Gol06] Goldblatt (2006), Mathematical Modal Logic: A View of
%          : [ROK06] Raths et al. (2006), The ILTP Problem Library for Intu
%          : [Ben09] Benzmueller (2009), Email to Geoff Sutcliffe
%          : [BP10]  Benzmueller & Paulson (2009), Exploring Properties of
% Source   : [Ben09]
% Names    : SYJ212+1.002 [ROK06]

% Status   : CounterSatisfiable
% Rating   : 0.33 v5.4.0, 1.00 v5.0.0, 0.33 v4.1.0, 0.50 v4.0.1, 0.00 v4.0.0
% Syntax   : Number of formulae    :   44 (   0 unit;  22 type;  19 defn)
%            Number of atoms       :  218 (  19 equality;  48 variable)
%            Maximal formula depth :    9 (   5 average)
%            Number of connectives :   68 (   3   ~;   1   |;   2   &;  60   @)
%                                         (   0 <=>;   2  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   97 (  97   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   26 (  22   :)
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

thf(con,conjecture,
    ( ivalid @ ( iequiv @ ( iequiv @ ( inot @ ( inot @ ( iatom @ a1 ) ) ) @ ( iatom @ a2 ) ) @ ( iequiv @ ( iatom @ a2 ) @ ( iatom @ a1 ) ) ) )).

%------------------------------------------------------------------------------
