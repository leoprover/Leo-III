%--------------------------------------------------------------------------
% File     : SYN063-2 : TPTP v6.0.0. Released v1.2.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 33
% Version  : Especial.
%            Theorem formulation : Different clausification.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [TPTP]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    3 (   0 non-Horn;   2 unit;   3 RR)
%            Number of atoms       :    4 (   0 equality)
%            Maximal clause size   :    2 (   1 average)
%            Number of predicates  :    1 (   0 propositional; 1-1 arity)
%            Number of functors    :    2 (   2 constant; 0-0 arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : This is a monadic predicate formulation of Pelletier 17.
%          : Created using the TPTP clausifier.
%--------------------------------------------------------------------------
cnf(pel33_1,negated_conjecture,
    ( big_p(c)
    | ~ big_p(a) )).

cnf(pel33_2,negated_conjecture,
    ( big_p(a) )).

cnf(pel33_3,negated_conjecture,
    ( ~ big_p(c) )).

%--------------------------------------------------------------------------
