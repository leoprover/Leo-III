%------------------------------------------------------------------------------
% File     : SYN045^7 : TPTP v6.0.0. Released v5.5.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 13
% Version  : [Ben12] axioms.
% English  :

% Refs     : [Goe69] Goedel (1969), An Interpretation of the Intuitionistic
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Ben12] Benzmueller (2012), Email to Geoff Sutcliffe
% Source   : [Ben12]
% Names    : s4-cumul-GSY045+1 [Ben12]

% Status   : Theorem
% Rating   : 0.29 v5.5.0
% Syntax   : Number of formulae    :   76 (   1 unit;  39 type;  32 defn)
%            Number of atoms       :  456 (  36 equality; 143 variable)
%            Maximal formula depth :   11 (   6 average)
%            Number of connectives :  189 (   5   ~;   5   |;   9   &; 160   @)
%                                         (   0 <=>;  10  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :  183 ( 183   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   44 (  39   :)
%            Number of variables   :   90 (   2 sgn;  34   !;   7   ?;  49   ^)
%                                         (  90   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : Goedel translation of SYN045+1
%------------------------------------------------------------------------------
%----Include axioms for Modal logic S4 under cumulative domains
include('Axioms/LCL015^0.ax').
include('Axioms/LCL013^5.ax').
include('Axioms/LCL015^1.ax').
%------------------------------------------------------------------------------
thf(r_type,type,(
    r: $i > $o )).

thf(q_type,type,(
    q: $i > $o )).

thf(p_type,type,(
    p: $i > $o )).

thf(pel13,conjecture,
    ( mvalid @ ( mand @ ( mbox_s4 @ ( mimplies @ ( mor @ ( mbox_s4 @ p ) @ ( mand @ ( mbox_s4 @ q ) @ ( mbox_s4 @ r ) ) ) @ ( mand @ ( mor @ ( mbox_s4 @ p ) @ ( mbox_s4 @ q ) ) @ ( mor @ ( mbox_s4 @ p ) @ ( mbox_s4 @ r ) ) ) ) ) @ ( mbox_s4 @ ( mimplies @ ( mand @ ( mor @ ( mbox_s4 @ p ) @ ( mbox_s4 @ q ) ) @ ( mor @ ( mbox_s4 @ p ) @ ( mbox_s4 @ r ) ) ) @ ( mor @ ( mbox_s4 @ p ) @ ( mand @ ( mbox_s4 @ q ) @ ( mbox_s4 @ r ) ) ) ) ) ) )).

%------------------------------------------------------------------------------
