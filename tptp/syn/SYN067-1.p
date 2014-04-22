%--------------------------------------------------------------------------
% File     : SYN067-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 38
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 38 [Pel86]
%          : p38a.in [ANL]
%          : p38b.in [ANL]

% Status   : Unsatisfiable
% Rating   : 0.00 v6.0.0, 0.14 v5.5.0, 0.25 v5.4.0, 0.40 v5.2.0, 0.20 v5.1.0, 0.36 v5.0.0, 0.50 v4.0.1, 0.40 v4.0.0, 0.57 v3.5.0, 0.71 v3.4.0, 0.50 v3.3.0, 0.33 v3.2.0, 0.00 v3.1.0, 0.50 v2.6.0, 0.67 v2.5.0, 0.80 v2.4.0, 0.20 v2.3.0, 0.33 v2.2.1, 0.75 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   84 (  69 non-Horn;   0 unit;  57 RR)
%            Number of atoms       :  500 (   0 equality)
%            Maximal clause size   :    9 (   6 average)
%            Number of predicates  :    2 (   0 propositional; 1-2 arity)
%            Number of functors    :   11 (   5 constant; 0-1 arity)
%            Number of variables   :  188 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : A full predicate logic version of Pelletier 17 and 33.
%          : Evidently this CNF is erroneous, because the FOF is provable.
%            I cannot recall where I got this CNF from.
%          : Previous comment is weird, because SPASS found a proof.
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_p(f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_p(f4(X1)) )).

cnf(clause_2,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_p(f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(X1,f3(X1)) )).

cnf(clause_3,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_p(f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(f3(X1),f4(X1)) )).

cnf(clause_4,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_p(f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_p(f6(X1)) )).

cnf(clause_5,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_p(f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(X1,f5(X1)) )).

cnf(clause_6,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_p(f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(f5(X1),f6(X1)) )).

cnf(clause_7,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_p(f4(X1)) )).

cnf(clause_8,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(X1,f3(X1)) )).

cnf(clause_9,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(f3(X1),f4(X1)) )).

cnf(clause_10,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_p(f6(X1)) )).

cnf(clause_11,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(X1,f5(X1)) )).

cnf(clause_12,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(f5(X1),f6(X1)) )).

cnf(clause_13,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_p(f4(X1)) )).

cnf(clause_14,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(X1,f3(X1)) )).

cnf(clause_15,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(f3(X1),f4(X1)) )).

cnf(clause_16,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_p(f6(X1)) )).

cnf(clause_17,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(X1,f5(X1)) )).

cnf(clause_18,negated_conjecture,
    ( ~ big_p(a)
    | big_p(X7)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(f5(X1),f6(X1)) )).

cnf(clause_19,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_p(f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_p(f4(X1)) )).

cnf(clause_20,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_p(f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(X1,f3(X1)) )).

cnf(clause_21,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_p(f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(f3(X1),f4(X1)) )).

cnf(clause_22,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_p(f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_p(f6(X1)) )).

cnf(clause_23,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_p(f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(X1,f5(X1)) )).

cnf(clause_24,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_p(f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(f5(X1),f6(X1)) )).

cnf(clause_25,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_p(f4(X1)) )).

cnf(clause_26,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(X1,f3(X1)) )).

cnf(clause_27,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(f3(X1),f4(X1)) )).

cnf(clause_28,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_p(f6(X1)) )).

cnf(clause_29,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(X1,f5(X1)) )).

cnf(clause_30,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(X7,f1(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(f5(X1),f6(X1)) )).

cnf(clause_31,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_p(f4(X1)) )).

cnf(clause_32,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(X1,f3(X1)) )).

cnf(clause_33,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | big_p(X1)
    | big_r(f3(X1),f4(X1)) )).

cnf(clause_34,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_p(f6(X1)) )).

cnf(clause_35,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(X1,f5(X1)) )).

cnf(clause_36,negated_conjecture,
    ( ~ big_p(a)
    | ~ big_p(X8)
    | ~ big_r(X7,X8)
    | big_r(f1(X7),f2(X7))
    | ~ big_p(a)
    | ~ big_p(X2)
    | ~ big_r(X1,X2)
    | big_r(f5(X1),f6(X1)) )).

cnf(clause_37,negated_conjecture,
    ( big_p(a)
    | big_p(a)
    | big_p(a) )).

cnf(clause_38,negated_conjecture,
    ( big_p(a)
    | big_p(a)
    | big_p(c3) )).

cnf(clause_39,negated_conjecture,
    ( big_p(a)
    | big_p(a)
    | big_r(c4,c3) )).

cnf(clause_40,negated_conjecture,
    ( big_p(a)
    | big_p(a)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_41,negated_conjecture,
    ( big_p(a)
    | ~ big_p(c4)
    | big_p(a) )).

cnf(clause_42,negated_conjecture,
    ( big_p(a)
    | ~ big_p(c4)
    | big_p(c3) )).

cnf(clause_43,negated_conjecture,
    ( big_p(a)
    | ~ big_p(c4)
    | big_r(c4,c3) )).

cnf(clause_44,negated_conjecture,
    ( big_p(a)
    | ~ big_p(c4)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_45,negated_conjecture,
    ( big_p(a)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(a) )).

cnf(clause_46,negated_conjecture,
    ( big_p(a)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(c3) )).

cnf(clause_47,negated_conjecture,
    ( big_p(a)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_r(c4,c3) )).

cnf(clause_48,negated_conjecture,
    ( big_p(a)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_49,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | big_p(a)
    | big_p(a) )).

cnf(clause_50,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | big_p(a)
    | big_p(c3) )).

cnf(clause_51,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | big_p(a)
    | big_r(c4,c3) )).

cnf(clause_52,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | big_p(a)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_53,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(c4)
    | big_p(a) )).

cnf(clause_54,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(c4)
    | big_p(c3) )).

cnf(clause_55,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(c4)
    | big_r(c4,c3) )).

cnf(clause_56,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(c4)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_57,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(a) )).

cnf(clause_58,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(c3) )).

cnf(clause_59,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_r(c4,c3) )).

cnf(clause_60,negated_conjecture,
    ( ~ big_p(c2)
    | big_p(c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_61,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | big_p(a)
    | big_p(a) )).

cnf(clause_62,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | big_p(a)
    | big_p(c3) )).

cnf(clause_63,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | big_p(a)
    | big_r(c4,c3) )).

cnf(clause_64,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | big_p(a)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_65,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(c4)
    | big_p(a) )).

cnf(clause_66,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(c4)
    | big_p(c3) )).

cnf(clause_67,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(c4)
    | big_r(c4,c3) )).

cnf(clause_68,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(c4)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_69,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(a) )).

cnf(clause_70,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(c3) )).

cnf(clause_71,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_r(c4,c3) )).

cnf(clause_72,negated_conjecture,
    ( ~ big_p(c2)
    | big_r(c2,c1)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_73,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | big_p(a)
    | big_p(a) )).

cnf(clause_74,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | big_p(a)
    | big_p(c3) )).

cnf(clause_75,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | big_p(a)
    | big_r(c4,c3) )).

cnf(clause_76,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | big_p(a)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_77,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(c4)
    | big_p(a) )).

cnf(clause_78,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(c4)
    | big_p(c3) )).

cnf(clause_79,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(c4)
    | big_r(c4,c3) )).

cnf(clause_80,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(c4)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

cnf(clause_81,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(a) )).

cnf(clause_82,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_p(c3) )).

cnf(clause_83,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | big_r(c4,c3) )).

cnf(clause_84,negated_conjecture,
    ( ~ big_p(X9)
    | ~ big_r(c2,X10)
    | ~ big_r(X10,X9)
    | ~ big_p(X3)
    | ~ big_r(c4,X4)
    | ~ big_r(X4,X3)
    | ~ big_p(X5)
    | ~ big_r(c4,X6)
    | ~ big_r(X6,X5) )).

%--------------------------------------------------------------------------
