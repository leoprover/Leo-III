%--------------------------------------------------------------------------
% File     : SYN047-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 17
% Version  : Especial.
% English  : A problem which appears to not be provable by [BBH72].
%            For details of why not, see [Pel82] p.135f.

% Refs     : Bledsoe W., Boyer R., Nenneman W. (1972), Computer Proofs of
%          : [Pel82] Pelletier (1982), Completely Non-clausal, Completely H
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 17 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    5 (   1 non-Horn;   2 unit;   5 RR)
%            Number of atoms       :   10 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    4 (   4 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( p )).

cnf(clause_2,negated_conjecture,
    ( ~ p
    | q
    | s )).

cnf(clause_3,negated_conjecture,
    ( ~ p
    | ~ r
    | s )).

cnf(clause_4,negated_conjecture,
    ( ~ s )).

cnf(clause_5,negated_conjecture,
    ( ~ q
    | r )).

%--------------------------------------------------------------------------
