%--------------------------------------------------------------------------
% File     : SYN055+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 25
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 25 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    5 (   1 unit)
%            Number of atoms       :   13 (   0 equality)
%            Maximal formula depth :    5 (   4 average)
%            Number of connectives :    9 (   1 ~  ;   1  |;   4  &)
%                                         (   0 <=>;   3 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    5 (   0 propositional; 1-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    6 (   0 singleton;   3 !;   3 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel25_1,axiom,
    ( ? [X] : big_p(X) )).

fof(pel25_2,axiom,
    ( ! [X] :
        ( big_f(X)
       => ( ~ big_g(X)
          & big_r(X) ) ) )).

fof(pel25_3,axiom,
    ( ! [X] :
        ( big_p(X)
       => ( big_g(X)
          & big_f(X) ) ) )).

fof(pel25_4,axiom,
    ( ! [X] :
        ( big_p(X)
       => big_q(X) )
    | ? [Z] :
        ( big_p(Z)
        & big_r(Z) ) )).

fof(pel25,conjecture,
    ( ? [X] :
        ( big_q(X)
        & big_p(X) ) )).

%--------------------------------------------------------------------------
