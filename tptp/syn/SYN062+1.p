%--------------------------------------------------------------------------
% File     : SYN062+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 32
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 32 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    4 (   0 unit)
%            Number of atoms       :   12 (   0 equality)
%            Maximal formula depth :    5 (   4 average)
%            Number of connectives :    8 (   0 ~  ;   1  |;   3  &)
%                                         (   0 <=>;   4 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    6 (   0 propositional; 1-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    4 (   0 singleton;   4 !;   0 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel32_1,axiom,
    ( ! [X] :
        ( ( big_f(X)
          & ( big_g(X)
            | big_h(X) ) )
       => big_i(X) ) )).

fof(pel32_2,axiom,
    ( ! [X] :
        ( ( big_i(X)
          & big_h(X) )
       => big_j(X) ) )).

fof(pel32_3,axiom,
    ( ! [X] :
        ( big_k(X)
       => big_h(X) ) )).

fof(pel32,conjecture,
    ( ! [X] :
        ( ( big_f(X)
          & big_k(X) )
       => big_j(X) ) )).

%--------------------------------------------------------------------------
