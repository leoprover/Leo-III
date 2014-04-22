%--------------------------------------------------------------------------
% File     : SYN038-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Syntactic formula
% Version  : Especial.
% English  : Exists X, Exists Y, All Z (((p(X,Y) -> (p(X,Z) <-> q(Y,Z)))
%            & (p(X,Y) <-> (p(Z,Z) -> q(Z,Z)))) -> (q(Z,Y) <-> q(Z,Z)))

% Refs     : [Fri63] Friedman (1963), A Semi-Decision Procedure for the Fun
%          : [FL+74] Fleisig et al. (1974), An Implementation of the Model
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [SPRFN]
% Names    : Example 4 [FL+74]
%          : EX4-T? [WM76]
%          : ex4.lop [SETHEO]
%          : FEX4T1 [SPRFN]
%          : FEX4T2 [SPRFN]

% Status   : Unsatisfiable
% Rating   : 0.00 v5.0.0, 0.07 v4.1.0, 0.00 v2.1.0, 0.12 v2.0.0
% Syntax   : Number of clauses     :    7 (   2 non-Horn;   0 unit;   5 RR)
%            Number of atoms       :   17 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    1 (   0 constant; 2-2 arity)
%            Number of variables   :   14 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p(X,f(X,Y))
    | ~ p(X,Y)
    | q(Y,f(X,Y)) )).

cnf(clause_2,negated_conjecture,
    ( ~ p(X,Y)
    | ~ q(Y,f(X,Y))
    | p(X,f(X,Y)) )).

cnf(clause_3,negated_conjecture,
    ( ~ p(X,Y)
    | ~ p(f(X,Y),f(X,Y))
    | q(f(X,Y),f(X,Y)) )).

cnf(clause_4,negated_conjecture,
    ( p(X,Y)
    | p(f(X,Y),f(X,Y)) )).

cnf(clause_5,negated_conjecture,
    ( p(X,Y)
    | ~ q(f(X,Y),f(X,Y)) )).

cnf(clause_6,negated_conjecture,
    ( q(X,Y)
    | q(f(X,Y),f(X,Y)) )).

cnf(clause_7,negated_conjecture,
    ( ~ q(X,Y)
    | ~ q(f(X,Y),f(X,Y)) )).

%--------------------------------------------------------------------------
