%--------------------------------------------------------------------------
% File     : SYN012-1 : TPTP v6.0.0. Released v1.1.0.
% Domain   : Syntactic
% Problem  : A problem to demonstrate Model Elimination
% Version  : Biased.
% English  :

% Refs     : [Lov68] Loveland (1968), Mechanical Theorem Proving by Model E
% Source   : [Lov68]
% Names    : Example [Lov68]

% Status   : Unsatisfiable
% Rating   : 0.00 v5.0.0, 0.07 v4.1.0, 0.00 v4.0.0, 0.14 v3.4.0, 0.25 v3.3.0, 0.33 v3.2.0, 0.00 v2.1.0, 0.12 v2.0.0
% Syntax   : Number of clauses     :    7 (   2 non-Horn;   0 unit;   5 RR)
%            Number of atoms       :   17 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    1 (   0 constant; 2-2 arity)
%            Number of variables   :   14 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : Biased towards ME.
%--------------------------------------------------------------------------
%----exists(X,exists(Y,forall(Z,((bf(X,Y) -> (bf(X,Z) <-> bg(Y,Z)))
%----& (bf(X,Y) <-> (bf(Z,Z) -> bg(Z,Z)))) -> (bg(Z,Y) <-> bg(Z,Z)))))
cnf(clause_1,negated_conjecture,
    ( ~ big_f(X,Y)
    | ~ big_f(X,g(X,Y))
    | big_g(Y,g(X,Y)) )).

cnf(clause_2,negated_conjecture,
    ( ~ big_f(X,Y)
    | big_f(X,g(X,Y))
    | ~ big_g(Y,g(X,Y)) )).

cnf(clause_3,negated_conjecture,
    ( ~ big_f(X,Y)
    | ~ big_f(g(X,Y),g(X,Y))
    | big_g(g(X,Y),g(X,Y)) )).

cnf(clause_4,negated_conjecture,
    ( big_f(X,Y)
    | big_f(g(X,Y),g(X,Y)) )).

cnf(clause_5,negated_conjecture,
    ( big_f(X,Y)
    | ~ big_g(g(X,Y),g(X,Y)) )).

cnf(clause_6,negated_conjecture,
    ( big_g(X,Y)
    | big_g(g(X,Y),g(X,Y)) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_g(X,Y)
    | ~ big_g(g(X,Y),g(X,Y)) )).

%--------------------------------------------------------------------------
