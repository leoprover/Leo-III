%--------------------------------------------------------------------------
% File     : SYN076-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 53
% Version  : Especial.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [Pel86]
% Names    : Pelletier 53 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.79 v6.0.0, 0.90 v5.5.0, 0.95 v5.3.0, 0.94 v5.0.0, 0.86 v4.1.0, 0.85 v4.0.1, 0.82 v3.7.0, 0.80 v3.5.0, 0.73 v3.4.0, 0.83 v3.3.0, 0.79 v3.2.0, 0.92 v3.1.0, 0.91 v2.7.0, 0.92 v2.6.0, 1.00 v2.0.0
% Syntax   : Number of clauses     :   34 (  32 non-Horn;   1 unit;  19 RR)
%            Number of atoms       :  195 ( 131 equality)
%            Maximal clause size   :    6 (   6 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :   14 (   4 constant; 0-2 arity)
%            Number of variables   :  129 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,axiom,
    (  c2 != c1 )).

cnf(clause_2,axiom,
    ( X1 = c2
    | X1 = c1 )).

cnf(clause_3,negated_conjecture,
    ( big_f(X10,f1(X10,X11))
    | f1(X10,X11) = X11
    | X10 = c3
    | big_f(f3(X2,X13),X2)
    | f3(X2,X13) = X13
    | X2 = c4 )).

cnf(clause_4,negated_conjecture,
    ( big_f(X10,f1(X10,X11))
    | f1(X10,X11) = X11
    | X10 = c3
    | ~ big_f(f3(X2,X13),X2)
    | f3(X2,X13) != X13
    | X2 = c4 )).

cnf(clause_5,negated_conjecture,
    ( big_f(X10,f1(X10,X11))
    | f1(X10,X11) = X11
    | X10 = c3
    | ~ big_f(X3,X2)
    | X3 = f4(X2)
    | X2 != c4 )).

cnf(clause_6,negated_conjecture,
    ( big_f(X10,f1(X10,X11))
    | f1(X10,X11) = X11
    | X10 = c3
    | big_f(X3,X2)
    | X3 != f4(X2)
    | X2 != c4 )).

cnf(clause_7,negated_conjecture,
    ( ~ big_f(X10,f1(X10,X11))
    | f1(X10,X11) != X11
    | X10 = c3
    | big_f(f3(X2,X13),X2)
    | f3(X2,X13) = X13
    | X2 = c4 )).

cnf(clause_8,negated_conjecture,
    ( ~ big_f(X10,f1(X10,X11))
    | f1(X10,X11) != X11
    | X10 = c3
    | ~ big_f(f3(X2,X13),X2)
    | f3(X2,X13) != X13
    | X2 = c4 )).

cnf(clause_9,negated_conjecture,
    ( ~ big_f(X10,f1(X10,X11))
    | f1(X10,X11) != X11
    | X10 = c3
    | ~ big_f(X3,X2)
    | X3 = f4(X2)
    | X2 != c4 )).

cnf(clause_10,negated_conjecture,
    ( ~ big_f(X10,f1(X10,X11))
    | f1(X10,X11) != X11
    | X10 = c3
    | big_f(X3,X2)
    | X3 != f4(X2)
    | X2 != c4 )).

cnf(clause_11,negated_conjecture,
    ( ~ big_f(X10,X12)
    | X12 = f2(X10)
    | X10 != c3
    | big_f(f3(X2,X13),X2)
    | f3(X2,X13) = X13
    | X2 = c4 )).

cnf(clause_12,negated_conjecture,
    ( ~ big_f(X10,X12)
    | X12 = f2(X10)
    | X10 != c3
    | ~ big_f(f3(X2,X13),X2)
    | f3(X2,X13) != X13
    | X2 = c4 )).

cnf(clause_13,negated_conjecture,
    ( ~ big_f(X10,X12)
    | X12 = f2(X10)
    | X10 != c3
    | ~ big_f(X3,X2)
    | X3 = f4(X2)
    | X2 != c4 )).

cnf(clause_14,negated_conjecture,
    ( ~ big_f(X10,X12)
    | X12 = f2(X10)
    | X10 != c3
    | big_f(X3,X2)
    | X3 != f4(X2)
    | X2 != c4 )).

cnf(clause_15,negated_conjecture,
    ( big_f(X10,X12)
    | X12 != f2(X10)
    | X10 != c3
    | big_f(f3(X2,X13),X2)
    | f3(X2,X13) = X13
    | X2 = c4 )).

cnf(clause_16,negated_conjecture,
    ( big_f(X10,X12)
    | X12 != f2(X10)
    | X10 != c3
    | ~ big_f(f3(X2,X13),X2)
    | f3(X2,X13) != X13
    | X2 = c4 )).

cnf(clause_17,negated_conjecture,
    ( big_f(X10,X12)
    | X12 != f2(X10)
    | X10 != c3
    | ~ big_f(X3,X2)
    | X3 = f4(X2)
    | X2 != c4 )).

cnf(clause_18,negated_conjecture,
    ( big_f(X10,X12)
    | X12 != f2(X10)
    | X10 != c3
    | big_f(X3,X2)
    | X3 != f4(X2)
    | X2 != c4 )).

cnf(clause_19,negated_conjecture,
    ( ~ big_f(f7(X4),X5)
    | X5 = f5(X4)
    | f7(X4) = X4
    | ~ big_f(X8,f10(X7))
    | X8 = f8(X7)
    | f10(X7) = X7 )).

cnf(clause_20,negated_conjecture,
    ( ~ big_f(f7(X4),X5)
    | X5 = f5(X4)
    | f7(X4) = X4
    | big_f(X8,f10(X7))
    | X8 != f8(X7)
    | f10(X7) = X7 )).

cnf(clause_21,negated_conjecture,
    ( ~ big_f(f7(X4),X5)
    | X5 = f5(X4)
    | f7(X4) = X4
    | big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) = X9
    | f10(X7) != X7 )).

cnf(clause_22,negated_conjecture,
    ( ~ big_f(f7(X4),X5)
    | X5 = f5(X4)
    | f7(X4) = X4
    | ~ big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) != X9
    | f10(X7) != X7 )).

cnf(clause_23,negated_conjecture,
    ( big_f(f7(X4),X5)
    | X5 != f5(X4)
    | f7(X4) = X4
    | ~ big_f(X8,f10(X7))
    | X8 = f8(X7)
    | f10(X7) = X7 )).

cnf(clause_24,negated_conjecture,
    ( big_f(f7(X4),X5)
    | X5 != f5(X4)
    | f7(X4) = X4
    | big_f(X8,f10(X7))
    | X8 != f8(X7)
    | f10(X7) = X7 )).

cnf(clause_25,negated_conjecture,
    ( big_f(f7(X4),X5)
    | X5 != f5(X4)
    | f7(X4) = X4
    | big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) = X9
    | f10(X7) != X7 )).

cnf(clause_26,negated_conjecture,
    ( big_f(f7(X4),X5)
    | X5 != f5(X4)
    | f7(X4) = X4
    | ~ big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) != X9
    | f10(X7) != X7 )).

cnf(clause_27,negated_conjecture,
    ( big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) = X6
    | f7(X4) != X4
    | ~ big_f(X8,f10(X7))
    | X8 = f8(X7)
    | f10(X7) = X7 )).

cnf(clause_28,negated_conjecture,
    ( big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) = X6
    | f7(X4) != X4
    | big_f(X8,f10(X7))
    | X8 != f8(X7)
    | f10(X7) = X7 )).

cnf(clause_29,negated_conjecture,
    ( big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) = X6
    | f7(X4) != X4
    | big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) = X9
    | f10(X7) != X7 )).

cnf(clause_30,negated_conjecture,
    ( big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) = X6
    | f7(X4) != X4
    | ~ big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) != X9
    | f10(X7) != X7 )).

cnf(clause_31,negated_conjecture,
    ( ~ big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) != X6
    | f7(X4) != X4
    | ~ big_f(X8,f10(X7))
    | X8 = f8(X7)
    | f10(X7) = X7 )).

cnf(clause_32,negated_conjecture,
    ( ~ big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) != X6
    | f7(X4) != X4
    | big_f(X8,f10(X7))
    | X8 != f8(X7)
    | f10(X7) = X7 )).

cnf(clause_33,negated_conjecture,
    ( ~ big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) != X6
    | f7(X4) != X4
    | big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) = X9
    | f10(X7) != X7 )).

cnf(clause_34,negated_conjecture,
    ( ~ big_f(f7(X4),f6(X4,X6))
    | f6(X4,X6) != X6
    | f7(X4) != X4
    | ~ big_f(f9(X7,X9),f10(X7))
    | f9(X7,X9) != X9
    | f10(X7) != X7 )).

%--------------------------------------------------------------------------
