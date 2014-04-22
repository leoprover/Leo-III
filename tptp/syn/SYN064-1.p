%--------------------------------------------------------------------------
% File     : SYN064-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 35
% Version  : Especial.
% English  :

% Refs     : [KM64]  Kalish & Montegue (1964), Logic: Techniques of Formal
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 35 [Pel86]
%          : p35.in [ANL]

% Status   : Unsatisfiable
% Rating   : 0.00 v5.3.0, 0.05 v5.2.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :    2 (   0 non-Horn;   2 unit;   1 RR)
%            Number of atoms       :    2 (   0 equality)
%            Maximal clause size   :    1 (   1 average)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    2 (   0 constant; 2-2 arity)
%            Number of variables   :    4 (   2 singleton)
%            Maximal term depth    :    2 (   2 average)
% SPC      : CNF_UNS_RFO_NEQ_HRN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( big_p(X,Y) )).

cnf(clause_2,negated_conjecture,
    ( ~ big_p(f(X,Y),g(X,Y)) )).

%--------------------------------------------------------------------------
