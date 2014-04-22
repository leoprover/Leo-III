%--------------------------------------------------------------------------
% File     : SYN070+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 46
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 46 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v4.1.0, 0.06 v4.0.1, 0.05 v3.7.0, 0.33 v3.5.0, 0.12 v3.4.0, 0.08 v3.3.0, 0.00 v3.2.0, 0.11 v3.1.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    4 (   0 unit)
%            Number of atoms       :   18 (   0 equality)
%            Maximal formula depth :    9 (   6 average)
%            Number of connectives :   18 (   4 ~  ;   0  |;   8  &)
%                                         (   0 <=>;   6 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    4 (   0 propositional; 1-2 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    8 (   0 singleton;   6 !;   2 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel46_1,axiom,
    ( ! [X,Y] :
        ( ( big_f(X)
          & ( ( big_f(Y)
              & big_h(Y,X) )
           => big_g(Y) ) )
       => big_g(X) ) )).

fof(pel46_2,axiom,
    ( ? [X] :
        ( big_f(X)
        & ~ big_g(X) )
   => ? [X1] :
        ( big_f(X1)
        & ~ big_g(X1)
        & ! [Y] :
            ( ( big_f(Y)
              & ~ big_g(Y) )
           => big_j(X1,Y) ) ) )).

fof(pel46_3,axiom,
    ( ! [X,Y] :
        ( ( big_f(X)
          & big_f(Y)
          & big_h(X,Y) )
       => ~ big_j(Y,X) ) )).

fof(pel46,conjecture,
    ( ! [X] :
        ( big_f(X)
       => big_g(X) ) )).

%--------------------------------------------------------------------------
