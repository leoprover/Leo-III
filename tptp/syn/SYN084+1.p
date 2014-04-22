%--------------------------------------------------------------------------
% File     : SYN084+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 62
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
%          : [Pel95] Pelletier (1995), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 62 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.5.0, 0.08 v5.4.0, 0.09 v5.3.0, 0.13 v5.2.0, 0.07 v5.0.0, 0.05 v4.1.0, 0.06 v4.0.1, 0.05 v3.7.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :   10 (   0 equality)
%            Maximal formula depth :    7 (   7 average)
%            Number of connectives :   12 (   3 ~  ;   4  |;   2  &)
%                                         (   1 <=>;   2 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    1 (   0 propositional; 1-1 arity)
%            Number of functors    :    2 (   1 constant; 0-1 arity)
%            Number of variables   :    2 (   0 singleton;   2 !;   0 ?)
%            Maximal term depth    :    3 (   2 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments : This problem is incorrect in [Pel86] and is 'corrected' in
%            [Pel88]. The 1988 version is broken too [Pel95]. This is the
%            correct version.
%--------------------------------------------------------------------------
fof(pel62,conjecture,
    ( ! [X] :
        ( ( big_p(a)
          & ( big_p(X)
           => big_p(f(X)) ) )
       => big_p(f(f(X))) )
  <=> ! [X1] :
        ( ( ~ big_p(a)
          | big_p(X1)
          | big_p(f(f(X1))) )
        & ( ~ big_p(a)
          | ~ big_p(f(X1))
          | big_p(f(f(X1))) ) ) )).

%--------------------------------------------------------------------------
