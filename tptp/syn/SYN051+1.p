%--------------------------------------------------------------------------
% File     : SYN051+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 21
% Version  : Especial.
% English  : A moderately tricky problem, especially for 'natural' systems
%            with 'strong' restrictions on variables generated from
%            existential quantifiers.

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 21 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.4.0, 0.11 v5.3.0, 0.18 v5.2.0, 0.00 v4.1.0, 0.06 v4.0.1, 0.05 v3.7.0, 0.33 v3.5.0, 0.12 v3.4.0, 0.08 v3.3.0, 0.00 v3.2.0, 0.11 v3.1.0, 0.00 v2.5.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    3 (   0 unit)
%            Number of atoms       :    6 (   0 equality)
%            Maximal formula depth :    3 (   3 average)
%            Number of connectives :    3 (   0 ~  ;   0  |;   0  &)
%                                         (   1 <=>;   2 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    2 (   1 propositional; 0-1 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    3 (   0 singleton;   0 !;   3 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments :
%--------------------------------------------------------------------------
fof(pel21_1,axiom,
    ( ? [X] :
        ( p
       => big_f(X) ) )).

fof(pel21_2,axiom,
    ( ? [X] :
        ( big_f(X)
       => p ) )).

fof(pel21,conjecture,
    ( ? [X] :
        ( p
      <=> big_f(X) ) )).

%--------------------------------------------------------------------------
