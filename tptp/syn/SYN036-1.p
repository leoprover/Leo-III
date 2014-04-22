%--------------------------------------------------------------------------
% File     : SYN036-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Andrews Challenge Problem
% Version  : Especial.
%            Theorem formulation : Erroneous, as in [Pel86]
% English  :

% Refs     : [DeC79] DeChampeaux (1979), Sub-problem Finder and Instance Ch
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
% Source   : [OTTER]
% Names    : Pelletier 34 [Pel86]
%          : andrews.in [OTTER]

% Status   : Unsatisfiable
% Rating   : 0.14 v6.0.0, 0.29 v5.5.0, 0.25 v5.4.0, 0.40 v5.2.0, 0.30 v5.1.0, 0.36 v5.0.0, 0.43 v4.1.0, 0.38 v4.0.1, 0.20 v4.0.0, 0.43 v3.5.0, 0.57 v3.4.0, 0.50 v3.3.0, 0.33 v3.2.0, 0.00 v3.1.0, 0.33 v2.7.0, 0.38 v2.6.0, 0.33 v2.5.0, 0.40 v2.4.0, 0.00 v2.2.1, 0.50 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :  128 ( 127 non-Horn;   0 unit;   8 RR)
%            Number of atoms       : 1024 (   0 equality)
%            Maximal clause size   :    8 (   8 average)
%            Number of predicates  :    2 (   0 propositional; 1-1 arity)
%            Number of functors    :   24 (  20 constant; 0-1 arity)
%            Number of variables   :  512 ( 384 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : This problem is incorrect in [Pel86] and is corrected in [Pel88].
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_2,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_3,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_4,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_5,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_6,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_7,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_8,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_9,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_10,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_11,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_12,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_13,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_14,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_15,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_16,negated_conjecture,
    ( big_p(X)
    | big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_17,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_18,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_19,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_20,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_21,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_22,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_23,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_24,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | ~ big_q(U)
    | big_p(V)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_25,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_26,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_27,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_28,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_29,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_30,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_31,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_32,negated_conjecture,
    ( ~ big_p(X)
    | ~ big_p(f1(X))
    | big_q(c1)
    | ~ big_p(c2)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_33,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_34,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_35,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_36,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_37,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_38,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_39,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_40,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_41,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_42,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_43,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_44,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_45,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_46,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_47,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_48,negated_conjecture,
    ( ~ big_p(c3)
    | big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_49,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_50,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_51,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_52,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_53,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_54,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_55,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_56,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | big_q(c4)
    | big_p(X3)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_57,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(f2(W))
    | big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_58,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(f2(W))
    | big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_59,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | ~ big_p(X1)
    | big_q(X2) )).

cnf(clause_60,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(f2(W))
    | ~ big_q(W)
    | big_p(c6)
    | ~ big_q(c7) )).

cnf(clause_61,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(Z)
    | big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_62,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | ~ big_q(Z)
    | big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_63,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(Z)
    | ~ big_q(c8)
    | big_p(c9)
    | big_q(X5) )).

cnf(clause_64,negated_conjecture,
    ( big_p(c3)
    | ~ big_p(Y)
    | ~ big_q(X4)
    | ~ big_p(c5)
    | big_q(Z)
    | ~ big_q(c8)
    | ~ big_p(X6)
    | ~ big_q(c10) )).

cnf(clause_65,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_66,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_67,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_68,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_69,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_70,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_71,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_72,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_73,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_74,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_75,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_76,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_77,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_78,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_79,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_80,negated_conjecture,
    ( ~ big_p(c11)
    | big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_81,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_82,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_83,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_84,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_85,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_86,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_87,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_88,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | ~ big_q(X8)
    | big_p(X9)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_89,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_90,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_91,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_92,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_93,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_94,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_95,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_96,negated_conjecture,
    ( big_p(c11)
    | ~ big_p(X7)
    | big_q(c12)
    | ~ big_p(c13)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_97,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_98,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_99,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_100,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_101,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_102,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_103,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_104,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_105,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_106,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_107,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_108,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_109,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_110,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_111,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_112,negated_conjecture,
    ( big_p(X10)
    | big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_113,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_114,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_115,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_116,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_117,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_118,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_119,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_120,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | big_q(c14)
    | big_p(X11)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_121,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(X13)
    | big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_122,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(X13)
    | big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_123,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(X13)
    | ~ big_q(c16)
    | ~ big_p(X14)
    | big_q(X15) )).

cnf(clause_124,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(X13)
    | ~ big_q(c16)
    | big_p(c17)
    | ~ big_q(c18) )).

cnf(clause_125,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(f4(X16))
    | big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_126,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | big_q(f4(X16))
    | big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

cnf(clause_127,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | big_p(c19)
    | big_q(X17) )).

cnf(clause_128,negated_conjecture,
    ( ~ big_p(X10)
    | ~ big_p(f3(X10))
    | ~ big_q(X12)
    | ~ big_p(c15)
    | ~ big_q(f4(X16))
    | ~ big_q(X16)
    | ~ big_p(X18)
    | ~ big_q(c20) )).

%--------------------------------------------------------------------------
