%--------------------------------------------------------------------------
% File     : SYN071+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 48
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Rud93] Rudnicki (1993), Email to G. Sutcliffe
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 48 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v3.3.0, 0.11 v3.2.0, 0.00 v3.1.0, 0.17 v2.7.0, 0.00 v2.4.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    3 (   0 unit)
%            Number of atoms       :    6 (   6 equality)
%            Maximal formula depth :    2 (   2 average)
%            Number of connectives :    3 (   0 ~  ;   3  |;   0  &)
%                                         (   0 <=>;   0 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    4 (   4 constant; 0-0 arity)
%            Number of variables   :    0 (   0 singleton;   0 !;   0 ?)
%            Maximal term depth    :    1 (   1 average)
% SPC      : FOF_THM_EPR

% Comments : [Pel86] says that Rudnicki has not published this problem
%            anywhere.
%          : [Rud93] says "I have seen it for the first time around 1977,
%            it was shown to me by A. Trybulec but I am sure it is folklore."
%--------------------------------------------------------------------------
fof(pel48_1,axiom,
    ( a = b
    | c = d )).

fof(pel48_2,axiom,
    ( a = c
    | b = d )).

fof(pel48,conjecture,
    ( a = d
    | b = c )).

%--------------------------------------------------------------------------
