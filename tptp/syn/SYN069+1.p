%--------------------------------------------------------------------------
% File     : SYN069+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 45
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 45 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v4.0.1, 0.05 v3.7.0, 0.00 v3.2.0, 0.11 v3.1.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    4 (   0 unit)
%            Number of atoms       :   18 (   0 equality)
%            Maximal formula depth :    7 (   6 average)
%            Number of connectives :   16 (   2 ~  ;   0  |;  10  &)
%                                         (   0 <=>;   4 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    6 (   0 propositional; 1-2 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    9 (   0 singleton;   5 !;   4 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments :
%--------------------------------------------------------------------------
fof(pel45_1,axiom,
    ( ! [X] :
        ( ( big_f(X)
          & ! [Y] :
              ( ( big_g(Y)
                & big_h(X,Y) )
             => big_j(X,Y) ) )
       => ! [Y1] :
            ( big_g(Y1)
            & big_h(X,Y1)
            & big_k(Y1) ) ) )).

fof(pel45_2,axiom,
    ( ~ ( ? [Y] :
            ( big_l(Y)
            & big_k(Y) ) ) )).

fof(pel45_3,axiom,
    ( ? [X] :
        ( big_f(X)
        & ! [Y] :
            ( big_h(X,Y)
           => big_l(Y) )
        & ! [Y1] :
            ( ( big_g(Y1)
              & big_h(X,Y1) )
           => big_j(X,Y1) ) ) )).

fof(pel45,conjecture,
    ( ? [X] :
        ( big_f(X)
        & ~ ( ? [Y] :
                ( big_g(Y)
                & big_h(X,Y) ) ) ) )).

%--------------------------------------------------------------------------
