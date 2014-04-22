%------------------------------------------------------------------------------
% File     : SYN052^7 : TPTP v6.0.0. Released v5.5.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 22
% Version  : [Ben12] axioms.
% English  :

% Refs     : [Goe69] Goedel (1969), An Interpretation of the Intuitionistic
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Ben12] Benzmueller (2012), Email to Geoff Sutcliffe
% Source   : [Ben12]
% Names    : s4-cumul-GSY052+1 [Ben12]

% Status   : Theorem
% Rating   : 0.43 v5.5.0
% Syntax   : Number of formulae    :   75 (   1 unit;  38 type;  32 defn)
%            Number of atoms       :  450 (  36 equality; 147 variable)
%            Maximal formula depth :   14 (   6 average)
%            Number of connectives :  184 (   5   ~;   5   |;   9   &; 155   @)
%                                         (   0 <=>;  10  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :  183 ( 183   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   43 (  38   :)
%            Number of variables   :   93 (   2 sgn;  34   !;   7   ?;  52   ^)
%                                         (  93   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : Goedel translation of SYN052+1
%------------------------------------------------------------------------------
%----Include axioms for Modal logic S4 under cumulative domains
include('Axioms/LCL015^0.ax').
include('Axioms/LCL013^5.ax').
include('Axioms/LCL015^1.ax').
%------------------------------------------------------------------------------
thf(p_type,type,(
    p: $i > $o )).

thf(big_f_type,type,(
    big_f: mu > $i > $o )).

thf(pel22,conjecture,
    ( mvalid
    @ ( mbox_s4
      @ ( mimplies
        @ ( mbox_s4
          @ ( mforall_ind
            @ ^ [X: mu] :
                ( mand @ ( mbox_s4 @ ( mimplies @ ( mbox_s4 @ p ) @ ( mbox_s4 @ ( big_f @ X ) ) ) ) @ ( mbox_s4 @ ( mimplies @ ( mbox_s4 @ ( big_f @ X ) ) @ ( mbox_s4 @ p ) ) ) ) ) )
        @ ( mand
          @ ( mbox_s4
            @ ( mimplies @ ( mbox_s4 @ p )
              @ ( mbox_s4
                @ ( mforall_ind
                  @ ^ [X1: mu] :
                      ( mbox_s4 @ ( big_f @ X1 ) ) ) ) ) )
          @ ( mbox_s4
            @ ( mimplies
              @ ( mbox_s4
                @ ( mforall_ind
                  @ ^ [X1: mu] :
                      ( mbox_s4 @ ( big_f @ X1 ) ) ) )
              @ ( mbox_s4 @ p ) ) ) ) ) ) )).

%------------------------------------------------------------------------------
