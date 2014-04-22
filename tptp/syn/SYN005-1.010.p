%--------------------------------------------------------------------------
% File     : SYN005-1.010 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Disjunctions that form a contradiction
% Version  : Biased.
% English  : ~p1(X1,X2) v ~p2(X2,X3) v ... v ~p10(X10,X1).
%             p1(a,a)      p2(a,a) ...        p10(a,a)

% Refs     : [Pla82] Plaisted (1982), A Simplified Problem Reduction Format
% Source   : [Pla82]
% Names    : Problem 5.4 [Pla82]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   11 (   0 non-Horn;  10 unit;  11 RR)
%            Number of atoms       :   20 (   0 equality)
%            Maximal clause size   :   10 (   2 average)
%            Number of predicates  :   10 (   0 propositional; 2-2 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :   10 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : "On this example locking resolution (even with a bad choice
%            of indices) and SL-resolution generate search spaces of size
%            polynomial in n, but positive unit resolution, all-negative
%            resolution, set-of-support strategy, ancestry-filter form,
%            and input resolution all generate search spaces of size
%            exponential in n." [Pla82] p.244.
%          : tptp2X: -f tptp -s10 SYN005-1.g
%--------------------------------------------------------------------------
cnf(disjunction,negated_conjecture,
    ( ~ p_1(X1,X2)
    | ~ p_2(X2,X3)
    | ~ p_3(X3,X4)
    | ~ p_4(X4,X5)
    | ~ p_5(X5,X6)
    | ~ p_6(X6,X7)
    | ~ p_7(X7,X8)
    | ~ p_8(X8,X9)
    | ~ p_9(X9,X10)
    | ~ p_10(X10,X1) )).

cnf(p_1,negated_conjecture,
    ( p_1(a,a) )).

cnf(p_2,negated_conjecture,
    ( p_2(a,a) )).

cnf(p_3,negated_conjecture,
    ( p_3(a,a) )).

cnf(p_4,negated_conjecture,
    ( p_4(a,a) )).

cnf(p_5,negated_conjecture,
    ( p_5(a,a) )).

cnf(p_6,negated_conjecture,
    ( p_6(a,a) )).

cnf(p_7,negated_conjecture,
    ( p_7(a,a) )).

cnf(p_8,negated_conjecture,
    ( p_8(a,a) )).

cnf(p_9,negated_conjecture,
    ( p_9(a,a) )).

cnf(p_10,negated_conjecture,
    ( p_10(a,a) )).

%--------------------------------------------------------------------------
