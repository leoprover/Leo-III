%--------------------------------------------------------------------------
% File     : SYN054+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 24
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 24 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    5 (   0 unit)
%            Number of atoms       :   12 (   0 equality)
%            Maximal formula depth :    4 (   4 average)
%            Number of connectives :    9 (   2 ~  ;   2  |;   2  &)
%                                         (   0 <=>;   3 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    4 (   0 propositional; 1-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    6 (   0 singleton;   2 !;   4 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel24_1,axiom,
    ( ~ ( ? [X] :
            ( big_s(X)
            & big_q(X) ) ) )).

fof(pel24_2,axiom,
    ( ! [X] :
        ( big_p(X)
       => ( big_q(X)
          | big_r(X) ) ) )).

fof(pel24_3,axiom,
    ( ~ ( ? [X] : big_p(X) )
   => ? [Y] : big_q(Y) )).

fof(pel24_4,axiom,
    ( ! [X] :
        ( ( big_q(X)
          | big_r(X) )
       => big_s(X) ) )).

fof(pel24,conjecture,
    ( ? [X] :
        ( big_p(X)
        & big_r(X) ) )).

%--------------------------------------------------------------------------
