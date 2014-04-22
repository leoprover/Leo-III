%--------------------------------------------------------------------------
% File     : SYN078+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 56
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 56 [Pel86]

% Status   : Theorem
% Rating   : 0.03 v6.0.0, 0.00 v5.3.0, 0.07 v5.2.0, 0.00 v5.0.0, 0.04 v4.0.1, 0.09 v4.0.0, 0.08 v3.7.0, 0.00 v3.4.0, 0.05 v3.3.0, 0.00 v3.2.0, 0.09 v3.1.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :    5 (   1 equality)
%            Maximal formula depth :    6 (   6 average)
%            Number of connectives :    4 (   0 ~  ;   0  |;   1  &)
%                                         (   1 <=>;   2 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   0 propositional; 1-2 arity)
%            Number of functors    :    1 (   0 constant; 1-1 arity)
%            Number of variables   :    3 (   0 singleton;   2 !;   1 ?)
%            Maximal term depth    :    2 (   1 average)
% SPC      : FOF_THM_RFO_SEQ

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
%----Problem axioms
fof(pel56,conjecture,
    ( ! [X] :
        ( ? [Y] :
            ( big_p(Y)
            & X = f(Y) )
       => big_p(X) )
  <=> ! [U] :
        ( big_p(U)
       => big_p(f(U)) ) )).

%--------------------------------------------------------------------------
