%--------------------------------------------------------------------------
% File     : SYN065+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 36
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 36 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.5.0, 0.04 v5.3.0, 0.13 v5.2.0, 0.00 v5.0.0, 0.05 v4.1.0, 0.06 v4.0.1, 0.00 v3.4.0, 0.08 v3.3.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    4 (   3 unit)
%            Number of atoms       :    8 (   0 equality)
%            Maximal formula depth :    7 (   4 average)
%            Number of connectives :    4 (   0 ~  ;   2  |;   0  &)
%                                         (   0 <=>;   2 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    3 (   0 propositional; 2-2 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    9 (   0 singleton;   6 !;   3 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments :
%--------------------------------------------------------------------------
fof(pel36_1,axiom,
    ( ! [X] :
      ? [Y] : big_f(X,Y) )).

fof(pel36_2,axiom,
    ( ! [X] :
      ? [Y] : big_g(X,Y) )).

fof(pel36_3,axiom,
    ( ! [X,Y] :
        ( ( big_f(X,Y)
          | big_g(X,Y) )
       => ! [Z] :
            ( ( big_f(Y,Z)
              | big_g(Y,Z) )
           => big_h(X,Z) ) ) )).

fof(pel36,conjecture,
    ( ! [X] :
      ? [Y] : big_h(X,Y) )).

%--------------------------------------------------------------------------
