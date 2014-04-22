%--------------------------------------------------------------------------
% File     : SYN037-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Andrews Challenge Problem Variant
% Version  : Especial.
% English  :

% Refs     : [DeC79] DeChampeaux (1979), Sub-problem Finder and Instance Ch
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [AZ89]  Angshuman & Zhang (1989), Andrews' Challenge Problem:
% Source   : [AZ89]
% Names    : Problem 1 [AZ89]

% Status   : Unsatisfiable
% Rating   : 0.00 v5.1.0, 0.09 v5.0.0, 0.07 v4.1.0, 0.00 v4.0.0, 0.14 v3.4.0, 0.25 v3.3.0, 0.33 v3.2.0, 0.00 v3.1.0, 0.17 v2.7.0, 0.25 v2.6.0, 0.00 v2.5.0, 0.20 v2.4.0, 0.00 v2.2.1, 0.25 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   36 (  11 non-Horn;   0 unit;  30 RR)
%            Number of atoms       :  104 (   0 equality)
%            Maximal clause size   :    4 (   3 average)
%            Number of predicates  :   12 (   8 propositional; 0-1 arity)
%            Number of functors    :    8 (   6 constant; 0-1 arity)
%            Number of variables   :   18 (  10 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ n2
    | ~ n9
    | ~ n6
    | ~ n10 )).

cnf(clause_2,negated_conjecture,
    ( ~ n2
    | ~ n9
    | n6
    | n10 )).

cnf(clause_3,negated_conjecture,
    ( n2
    | n9
    | ~ n6
    | ~ n10 )).

cnf(clause_4,negated_conjecture,
    ( n2
    | n9
    | n6
    | n10 )).

cnf(clause_5,negated_conjecture,
    ( ~ n2
    | n9
    | ~ n6
    | n10 )).

cnf(clause_6,negated_conjecture,
    ( ~ n2
    | n9
    | n6
    | ~ n10 )).

cnf(clause_7,negated_conjecture,
    ( n2
    | ~ n9
    | ~ n6
    | n10 )).

cnf(clause_8,negated_conjecture,
    ( n2
    | ~ n9
    | n6
    | ~ n10 )).

cnf(clause_9,negated_conjecture,
    ( ~ q(s8)
    | n8 )).

cnf(clause_10,negated_conjecture,
    ( ~ n8
    | q(X10) )).

cnf(clause_11,negated_conjecture,
    ( ~ p(X9)
    | n7 )).

cnf(clause_12,negated_conjecture,
    ( ~ n7
    | p(s7) )).

cnf(clause_13,negated_conjecture,
    ( ~ n5(X8)
    | n6 )).

cnf(clause_14,negated_conjecture,
    ( ~ n6
    | n5(s6) )).

cnf(clause_15,negated_conjecture,
    ( ~ q(s5(W))
    | ~ q(W)
    | n5(W) )).

cnf(clause_16,negated_conjecture,
    ( q(s5(W))
    | q(W)
    | n5(W) )).

cnf(clause_17,negated_conjecture,
    ( ~ n5(W)
    | ~ q(X7)
    | q(W) )).

cnf(clause_18,negated_conjecture,
    ( ~ n5(W)
    | q(X7)
    | ~ q(W) )).

cnf(clause_19,negated_conjecture,
    ( ~ p(s4)
    | n4 )).

cnf(clause_20,negated_conjecture,
    ( ~ n4
    | p(X6) )).

cnf(clause_21,negated_conjecture,
    ( ~ q(X5)
    | n3 )).

cnf(clause_22,negated_conjecture,
    ( ~ n3
    | q(s3) )).

cnf(clause_23,negated_conjecture,
    ( ~ n1(X4)
    | n2 )).

cnf(clause_24,negated_conjecture,
    ( ~ n2
    | n1(s2) )).

cnf(clause_25,negated_conjecture,
    ( ~ p(X)
    | ~ p(s1(X))
    | n1(X) )).

cnf(clause_26,negated_conjecture,
    ( p(X)
    | p(s1(X))
    | n1(X) )).

cnf(clause_27,negated_conjecture,
    ( ~ n1(X)
    | ~ p(X)
    | p(X3) )).

cnf(clause_28,negated_conjecture,
    ( ~ n1(X)
    | p(X)
    | ~ p(X3) )).

cnf(clause_29,negated_conjecture,
    ( ~ n3
    | ~ n4
    | n9 )).

cnf(clause_30,negated_conjecture,
    ( n3
    | n4
    | n9 )).

cnf(clause_31,negated_conjecture,
    ( ~ n3
    | n4
    | ~ n9 )).

cnf(clause_32,negated_conjecture,
    ( n3
    | ~ n4
    | ~ n9 )).

cnf(clause_33,negated_conjecture,
    ( ~ n7
    | ~ n8
    | n10 )).

cnf(clause_34,negated_conjecture,
    ( n7
    | n8
    | n10 )).

cnf(clause_35,negated_conjecture,
    ( ~ n7
    | n8
    | ~ n10 )).

cnf(clause_36,negated_conjecture,
    ( n7
    | ~ n8
    | ~ n10 )).

%--------------------------------------------------------------------------
