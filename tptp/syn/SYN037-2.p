%--------------------------------------------------------------------------
% File     : SYN037-2 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Andrews Challenge Problem Variant
% Version  : Especial.
%            Theorem formulation : Different clausal form.
% English  :

% Refs     : [DeC79] DeChampeaux (1979), Sub-problem Finder and Instance Ch
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [AZ89]  Angshuman & Zhang (1989), Andrews' Challenge Problem:
%          : [Qua90] Quaife (1990), Andrews' Challenge Problem Revisited
% Source   : [Qua90]
% Names    : Theorem P [Qua90]

% Status   : Unsatisfiable
% Rating   : 0.00 v5.1.0, 0.09 v5.0.0, 0.07 v4.1.0, 0.00 v4.0.0, 0.14 v3.4.0, 0.25 v3.3.0, 0.33 v3.2.0, 0.00 v3.1.0, 0.17 v2.7.0, 0.25 v2.6.0, 0.00 v2.5.0, 0.20 v2.4.0, 0.00 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   22 (  13 non-Horn;   0 unit;  12 RR)
%            Number of atoms       :   78 (   0 equality)
%            Maximal clause size   :    4 (   4 average)
%            Number of predicates  :    5 (   3 propositional; 0-1 arity)
%            Number of functors    :   10 (   6 constant; 0-1 arity)
%            Number of variables   :   20 (  12 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ m1
    | ~ m3
    | ~ p(X1)
    | ~ p(fy(X1)) )).

cnf(clause_2,negated_conjecture,
    ( ~ m1
    | ~ m3
    | p(X1)
    | p(fy(X1)) )).

cnf(clause_3,negated_conjecture,
    ( ~ m1
    | ~ p(cx)
    | m3
    | p(Y4) )).

cnf(clause_4,negated_conjecture,
    ( ~ m1
    | ~ p(Y5)
    | m3
    | p(cx) )).

cnf(clause_5,negated_conjecture,
    ( ~ m2
    | ~ m3
    | ~ q(cw)
    | q(Z1) )).

cnf(clause_6,negated_conjecture,
    ( ~ m2
    | ~ m3
    | ~ q(Z)
    | q(cw) )).

cnf(clause_7,negated_conjecture,
    ( ~ m2
    | ~ q(W2)
    | ~ q(fz5(W2))
    | m3 )).

cnf(clause_8,negated_conjecture,
    ( ~ m2
    | m3
    | q(W2)
    | q(fz5(W2)) )).

cnf(clause_9,negated_conjecture,
    ( ~ m3
    | ~ p(cx)
    | m1
    | p(Y1) )).

cnf(clause_10,negated_conjecture,
    ( ~ m3
    | ~ p(Y2)
    | m1
    | p(cx) )).

cnf(clause_11,negated_conjecture,
    ( ~ m3
    | ~ q(W1)
    | ~ q(fz2(W1))
    | m2 )).

cnf(clause_12,negated_conjecture,
    ( ~ m3
    | m2
    | q(W1)
    | q(fz2(W1)) )).

cnf(clause_13,negated_conjecture,
    ( ~ p(X2)
    | ~ p(fy3(X2))
    | m1
    | m3 )).

cnf(clause_14,negated_conjecture,
    ( ~ q(cw)
    | m2
    | m3
    | q(Z4) )).

cnf(clause_15,negated_conjecture,
    ( ~ q(Z3)
    | m2
    | m3
    | q(cw) )).

cnf(clause_16,negated_conjecture,
    ( m1
    | m3
    | p(X2)
    | p(fy3(X2)) )).

cnf(clause_17,negated_conjecture,
    ( ~ m1
    | ~ q(U1)
    | q(Uu1) )).

cnf(clause_18,negated_conjecture,
    ( ~ m2
    | ~ p(V1)
    | p(Vv1) )).

cnf(clause_19,negated_conjecture,
    ( ~ p(cvv)
    | m2 )).

cnf(clause_20,negated_conjecture,
    ( ~ q(cuu)
    | m1 )).

cnf(clause_21,negated_conjecture,
    ( m2
    | p(cv) )).

cnf(clause_22,negated_conjecture,
    ( m1
    | q(cu) )).

%--------------------------------------------------------------------------
