%--------------------------------------------------------------------------
% File     : SYN061+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 31
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 31 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v4.1.0, 0.06 v4.0.1, 0.05 v3.7.0, 0.33 v3.5.0, 0.12 v3.4.0, 0.08 v3.3.0, 0.00 v3.2.0, 0.11 v3.1.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    4 (   0 unit)
%            Number of atoms       :    9 (   0 equality)
%            Maximal formula depth :    5 (   4 average)
%            Number of connectives :    7 (   2 ~  ;   1  |;   3  &)
%                                         (   0 <=>;   1 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    5 (   0 propositional; 1-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    4 (   0 singleton;   1 !;   3 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel31_1,axiom,
    ( ~ ( ? [X] :
            ( big_f(X)
            & ( big_g(X)
              | big_h(X) ) ) ) )).

fof(pel31_2,axiom,
    ( ? [X] :
        ( big_i(X)
        & big_f(X) ) )).

fof(pel31_3,axiom,
    ( ! [X] :
        ( ~ big_h(X)
       => big_j(X) ) )).

fof(pel31,conjecture,
    ( ? [X] :
        ( big_i(X)
        & big_j(X) ) )).

%--------------------------------------------------------------------------
