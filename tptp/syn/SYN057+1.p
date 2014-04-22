%--------------------------------------------------------------------------
% File     : SYN057+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 27
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 27 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v3.4.0, 0.08 v3.3.0, 0.00 v3.2.0, 0.11 v3.1.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    5 (   0 unit)
%            Number of atoms       :   13 (   0 equality)
%            Maximal formula depth :    5 (   4 average)
%            Number of connectives :   12 (   4 ~  ;   0  |;   3  &)
%                                         (   0 <=>;   5 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    5 (   0 propositional; 1-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    6 (   0 singleton;   4 !;   2 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel27_1,axiom,
    ( ? [X] :
        ( big_f(X)
        & ~ big_g(X) ) )).

fof(pel27_2,axiom,
    ( ! [X] :
        ( big_f(X)
       => big_h(X) ) )).

fof(pel27_3,axiom,
    ( ! [X] :
        ( ( big_j(X)
          & big_i(X) )
       => big_f(X) ) )).

fof(pel27_4,axiom,
    ( ? [X] :
        ( big_h(X)
        & ~ big_g(X) )
   => ! [X1] :
        ( big_i(X1)
       => ~ big_h(X1) ) )).

fof(pel27,conjecture,
    ( ! [X] :
        ( big_j(X)
       => ~ big_i(X) ) )).

%--------------------------------------------------------------------------
