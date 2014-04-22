%--------------------------------------------------------------------------
% File     : SYN059+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 29
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 29 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v2.1.0
% Syntax   : Number of formulae    :    3 (   2 unit)
%            Number of atoms       :   10 (   0 equality)
%            Maximal formula depth :    6 (   3 average)
%            Number of connectives :    7 (   0 ~  ;   0  |;   3  &)
%                                         (   1 <=>;   3 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    4 (   0 propositional; 1-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    6 (   0 singleton;   4 !;   2 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel29_1,axiom,
    ( ? [X] : big_f(X) )).

fof(pel29_2,axiom,
    ( ? [Y] : big_g(Y) )).

fof(pel29,conjecture,
    ( ( ! [X] :
          ( big_f(X)
         => big_h(X) )
      & ! [U] :
          ( big_g(U)
         => big_j(U) ) )
  <=> ! [W,Y] :
        ( ( big_f(W)
          & big_g(Y) )
       => ( big_h(W)
          & big_j(Y) ) ) )).

%--------------------------------------------------------------------------
