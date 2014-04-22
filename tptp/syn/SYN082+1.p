%--------------------------------------------------------------------------
% File     : SYN082+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 60
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 60 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :    4 (   0 equality)
%            Maximal formula depth :    7 (   7 average)
%            Number of connectives :    3 (   0 ~  ;   0  |;   1  &)
%                                         (   1 <=>;   1 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    1 (   0 constant; 1-1 arity)
%            Number of variables   :    3 (   0 singleton;   2 !;   1 ?)
%            Maximal term depth    :    2 (   1 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
fof(pel60,conjecture,
    ( ! [X] :
        ( big_f(X,f(X))
      <=> ? [Y] :
            ( ! [Z] :
                ( big_f(Z,Y)
               => big_f(Z,f(X)) )
            & big_f(X,Y) ) ) )).

%--------------------------------------------------------------------------
