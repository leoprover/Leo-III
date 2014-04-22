%--------------------------------------------------------------------------
% File     : SYN079+1 : TPTP v6.0.0. Released v2.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 57
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 57 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v5.3.0, 0.09 v5.2.0, 0.00 v2.1.0
% Syntax   : Number of formulae    :    4 (   3 unit)
%            Number of atoms       :    6 (   0 equality)
%            Maximal formula depth :    6 (   2 average)
%            Number of connectives :    2 (   0 ~  ;   0  |;   1  &)
%                                         (   0 <=>;   1 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    4 (   3 constant; 0-2 arity)
%            Number of variables   :    3 (   0 singleton;   3 !;   0 ?)
%            Maximal term depth    :    2 (   2 average)
% SPC      : FOF_THM_RFO_NEQ

% Comments :
%--------------------------------------------------------------------------
fof(pel57_1,axiom,
    ( big_f(f(a,b),f(b,c)) )).

fof(pel57_2,axiom,
    ( big_f(f(b,c),f(a,c)) )).

fof(pel57_3,axiom,
    ( ! [X,Y,Z] :
        ( ( big_f(X,Y)
          & big_f(Y,Z) )
       => big_f(X,Z) ) )).

fof(pel57,conjecture,
    ( big_f(f(a,b),f(a,c)) )).

%--------------------------------------------------------------------------
