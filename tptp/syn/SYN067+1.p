%--------------------------------------------------------------------------
% File     : SYN067+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 38
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 38 [Pel86]

% Status   : Theorem
% Rating   : 0.04 v6.0.0, 0.00 v5.5.0, 0.12 v5.4.0, 0.17 v5.3.0, 0.30 v5.2.0, 0.29 v5.0.0, 0.25 v4.1.0, 0.22 v4.0.1, 0.21 v4.0.0, 0.20 v3.7.0, 0.33 v3.5.0, 0.25 v3.4.0, 0.08 v3.3.0, 0.11 v3.2.0, 0.33 v3.1.0, 0.50 v2.7.0, 0.33 v2.6.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.67 v2.2.1, 0.50 v2.2.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :   18 (   0 equality)
%            Maximal formula depth :   10 (  10 average)
%            Number of connectives :   20 (   3 ~  ;   4  |;  10  &)
%                                         (   1 <=>;   2 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   0 propositional; 1-2 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :   10 (   0 singleton;   2 !;   8 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments :
%--------------------------------------------------------------------------
fof(pel38,conjecture,
    ( ! [X] :
        ( ( big_p(a)
          & ( big_p(X)
           => ? [Y] :
                ( big_p(Y)
                & big_r(X,Y) ) ) )
       => ? [Z,W] :
            ( big_p(Z)
            & big_r(X,W)
            & big_r(W,Z) ) )
  <=> ! [X1] :
        ( ( ~ big_p(a)
          | big_p(X1)
          | ? [Z1,W1] :
              ( big_p(Z1)
              & big_r(X1,W1)
              & big_r(W1,Z1) ) )
        & ( ~ big_p(a)
          | ~ ( ? [Y1] :
                  ( big_p(Y1)
                  & big_r(X1,Y1) ) )
          | ? [Z2,W2] :
              ( big_p(Z2)
              & big_r(X1,W2)
              & big_r(W2,Z2) ) ) ) )).

%--------------------------------------------------------------------------
