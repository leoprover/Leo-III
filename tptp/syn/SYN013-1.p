%--------------------------------------------------------------------------
% File     : SYN013-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : A problem in quantification theory
% Version  : Especial.
% English  :

% Refs     : [Wan65] Wang (1965), Formalization and Automatic Theorem-Provi
%          : [Wos65] Wos (1965), Unpublished Note
%          : [MOW76] McCharen et al. (1976), Problems and Experiments for a
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [MOW76]
% Names    : ExQ1 [Wan65]
%          : EXQ1 [MOW76]
%          : wos31 [WM76]
%          : exq1.ver1.in [ANL]
%          : exq1.ver2.in [ANL]
%          : wang1.in [OTTER]

% Status   : Unsatisfiable
% Rating   : 0.07 v6.0.0, 0.10 v5.5.0, 0.20 v5.3.0, 0.17 v5.2.0, 0.06 v5.1.0, 0.12 v5.0.0, 0.07 v4.1.0, 0.08 v4.0.1, 0.00 v3.4.0, 0.08 v3.3.0, 0.21 v3.2.0, 0.31 v3.1.0, 0.18 v2.7.0, 0.17 v2.6.0, 0.20 v2.5.0, 0.42 v2.4.0, 0.22 v2.3.0, 0.33 v2.2.0, 0.44 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   16 (  11 non-Horn;   3 unit;  14 RR)
%            Number of atoms       :   49 (  28 equality)
%            Maximal clause size   :    6 (   3 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    5 (   3 constant; 0-1 arity)
%            Number of variables   :   15 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(c_1,negated_conjecture,
    (  m != n )).

cnf(c_2,negated_conjecture,
    (  n != k )).

cnf(c_3,negated_conjecture,
    (  k != m )).

cnf(c_4,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | f(Y) != m )).

cnf(c_5,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | f(Y) != Y )).

cnf(c_6,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | element(Y,f(Y)) )).

cnf(c_7,negated_conjecture,
    ( Y = m
    | ~ element(Y,m)
    | element(f(Y),Y) )).

cnf(c_8,negated_conjecture,
    ( Y = m
    | element(Y,m)
    | V = m
    | V = Y
    | ~ element(Y,V)
    | ~ element(V,Y) )).

cnf(c_9,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | g(Y) != n )).

cnf(c_10,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | g(Y) != Y )).

cnf(c_11,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | element(Y,g(Y)) )).

cnf(c_12,negated_conjecture,
    ( Y = n
    | element(Y,n)
    | element(g(Y),Y) )).

cnf(c_13,negated_conjecture,
    ( Y = n
    | ~ element(Y,n)
    | V = n
    | V = Y
    | ~ element(Y,V)
    | ~ element(V,Y) )).

cnf(c_14,negated_conjecture,
    ( Y = k
    | Y != m
    | element(Y,k) )).

cnf(c_15,negated_conjecture,
    ( Y = k
    | Y != n
    | element(Y,k) )).

cnf(c_16,negated_conjecture,
    ( Y = k
    | Y = m
    | Y = n
    | ~ element(Y,k) )).

%--------------------------------------------------------------------------
