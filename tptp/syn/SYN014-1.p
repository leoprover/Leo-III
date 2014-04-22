%--------------------------------------------------------------------------
% File     : SYN014-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : A problem in quantification theory
% Version  : [Wan65] axioms : Especial.
% English  :

% Refs     : [Wan65] Wang (1965), Formalization and Automatic Theorem-Provi
%          : [MOW76] McCharen et al. (1976), Problems and Experiments for a
% Source   : [MOW76]
% Names    : ExQ2 [Wan65]
%          : EXQ2 [MOW76]
%          : exq2.ver1.in [ANL]
%          : exq2.ver2.in [ANL]

% Status   : Unsatisfiable
% Rating   : 0.21 v6.0.0, 0.30 v5.3.0, 0.33 v5.2.0, 0.25 v5.1.0, 0.29 v4.1.0, 0.15 v4.0.1, 0.09 v4.0.0, 0.18 v3.7.0, 0.20 v3.5.0, 0.18 v3.4.0, 0.25 v3.3.0, 0.29 v3.2.0, 0.31 v3.1.0, 0.45 v2.7.0, 0.42 v2.6.0, 0.40 v2.5.0, 0.50 v2.4.0, 0.22 v2.3.0, 0.44 v2.2.1, 0.67 v2.2.0, 0.89 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   17 (  14 non-Horn;   1 unit;  15 RR)
%            Number of atoms       :   55 (  32 equality)
%            Maximal clause size   :    6 (   3 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    6 (   4 constant; 0-1 arity)
%            Number of variables   :   17 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(c_1,negated_conjecture,
    (  m != n )).

cnf(c_2,negated_conjecture,
    ( n = k
    | m = k )).

cnf(c_3,negated_conjecture,
    ( Y = j
    | Y != k
    | element(Y,j) )).

cnf(c_4,negated_conjecture,
    ( Y = j
    | Y = k
    | ~ element(Y,j) )).

cnf(c_5,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | f(Y) != m )).

cnf(c_6,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | f(Y) != Y )).

cnf(c_7,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | element(Y,f(Y)) )).

cnf(c_8,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | element(f(Y),Y) )).

cnf(c_9,negated_conjecture,
    ( Y = m
    | element(Y,m)
    | V1 = m
    | V1 = Y
    | ~ element(Y,V1)
    | ~ element(V1,Y) )).

cnf(c_10,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | g(Y) != n )).

cnf(c_11,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | g(Y) != Y )).

cnf(c_12,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | element(Y,g(Y)) )).

cnf(c_13,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | element(g(Y),Y) )).

cnf(c_14,negated_conjecture,
    ( Y = n
    | ~ element(Y,n)
    | V = n
    | V = Y
    | ~ element(Y,V)
    | ~ element(V,Y) )).

cnf(c_15,negated_conjecture,
    ( Y = k
    | Y != m
    | element(Y,k) )).

cnf(c_16,negated_conjecture,
    ( Y = k
    | Y != n
    | element(Y,k) )).

cnf(c_17,negated_conjecture,
    ( Y = k
    | Y = m
    | Y = n
    | ~ element(Y,k) )).

