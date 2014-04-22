%--------------------------------------------------------------------------
% File     : SYN067-2 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Pelletier Problem 38
% Version  : Especial.
%            Theorem formulation : Different clausification.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [TPTP]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.29 v5.5.0, 0.50 v5.2.0, 0.40 v5.1.0, 0.55 v5.0.0, 0.64 v4.1.0, 0.62 v4.0.1, 0.60 v4.0.0, 0.71 v3.5.0, 0.86 v3.4.0, 0.75 v3.3.0, 0.67 v3.2.0, 0.33 v3.1.0, 0.67 v2.7.0, 0.62 v2.6.0, 0.67 v2.5.0, 0.60 v2.4.0, 0.40 v2.3.0, 0.67 v2.2.1, 1.00 v2.0.0
% Syntax   : Number of clauses     :   55 (  44 non-Horn;   1 unit;  28 RR)
%            Number of atoms       :  289 (   0 equality)
%            Maximal clause size   :    9 (   5 average)
%            Number of predicates  :    2 (   0 propositional; 1-2 arity)
%            Number of functors    :   12 (   6 constant; 0-1 arity)
%            Number of variables   :  150 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : A full predicate logic version of Pelletier 17 and 33.
%          : Created using the TPTP clausifier.
% Bugfixes : v1.2.1 - Clauses replaced due to bug found in TPTP clausifier
%--------------------------------------------------------------------------
cnf(pel38_1,negated_conjecture,
    ( big_r(sk2(B),sk1(B))
    | big_r(sk6(D),sk5(D))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_2,negated_conjecture,
    ( big_r(B,sk2(B))
    | big_r(sk6(D),sk5(D))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_3,negated_conjecture,
    ( big_r(D,sk6(D))
    | big_r(sk2(B),sk1(B))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_4,negated_conjecture,
    ( big_r(D,sk6(D))
    | big_r(B,sk2(B))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_5,negated_conjecture,
    ( big_p(sk1(B))
    | big_r(sk6(D),sk5(D))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_6,negated_conjecture,
    ( big_p(sk1(B))
    | big_r(D,sk6(D))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_7,negated_conjecture,
    ( big_p(sk5(D))
    | big_r(sk2(B),sk1(B))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_8,negated_conjecture,
    ( big_p(sk5(D))
    | big_r(B,sk2(B))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_9,negated_conjecture,
    ( big_p(sk1(B))
    | big_p(sk5(D))
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A) )).

cnf(pel38_10,negated_conjecture,
    ( big_p(A)
    | big_r(sk2(A),sk1(A))
    | big_r(sk6(C),sk5(C))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_11,negated_conjecture,
    ( big_p(A)
    | big_r(C,sk6(C))
    | big_r(sk2(A),sk1(A))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_12,negated_conjecture,
    ( big_p(A)
    | big_r(A,sk2(A))
    | big_r(sk6(C),sk5(C))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_13,negated_conjecture,
    ( big_p(A)
    | big_r(C,sk6(C))
    | big_r(A,sk2(A))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_14,negated_conjecture,
    ( big_p(A)
    | big_p(sk5(C))
    | big_r(sk2(A),sk1(A))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_15,negated_conjecture,
    ( big_p(A)
    | big_p(sk5(C))
    | big_r(A,sk2(A))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_16,negated_conjecture,
    ( big_p(C)
    | big_r(sk2(B),sk1(B))
    | big_r(sk4(C),sk3(C))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_17,negated_conjecture,
    ( big_p(C)
    | big_r(B,sk2(B))
    | big_r(sk4(C),sk3(C))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_18,negated_conjecture,
    ( big_p(C)
    | big_r(C,sk4(C))
    | big_r(sk2(B),sk1(B))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_19,negated_conjecture,
    ( big_p(C)
    | big_r(C,sk4(C))
    | big_r(B,sk2(B))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_20,negated_conjecture,
    ( big_p(C)
    | big_p(sk1(B))
    | big_r(sk4(C),sk3(C))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_21,negated_conjecture,
    ( big_p(C)
    | big_p(sk1(B))
    | big_r(C,sk4(C))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_22,negated_conjecture,
    ( big_p(C)
    | big_p(sk3(C))
    | big_r(sk2(B),sk1(B))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_23,negated_conjecture,
    ( big_p(C)
    | big_p(sk3(C))
    | big_r(B,sk2(B))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_24,negated_conjecture,
    ( big_p(C)
    | big_p(sk1(B))
    | big_p(sk3(C))
    | ~ big_p(A)
    | ~ big_r(B,A) )).

cnf(pel38_25,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_r(sk2(A),sk1(A))
    | big_r(sk4(B),sk3(B)) )).

cnf(pel38_26,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_r(B,sk4(B))
    | big_r(sk2(A),sk1(A)) )).

cnf(pel38_27,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_r(A,sk2(A))
    | big_r(sk4(B),sk3(B)) )).

cnf(pel38_28,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_r(B,sk4(B))
    | big_r(A,sk2(A)) )).

cnf(pel38_29,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_p(sk3(B))
    | big_r(sk2(A),sk1(A)) )).

cnf(pel38_30,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_p(sk3(B))
    | big_r(A,sk2(A)) )).

cnf(pel38_31,negated_conjecture,
    ( big_p(A)
    | big_p(sk1(A))
    | big_r(sk6(C),sk5(C))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_32,negated_conjecture,
    ( big_p(A)
    | big_p(sk1(A))
    | big_r(C,sk6(C))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_33,negated_conjecture,
    ( big_p(A)
    | big_p(sk1(A))
    | big_p(sk5(C))
    | ~ big_p(B)
    | ~ big_r(C,B) )).

cnf(pel38_34,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_p(sk1(A))
    | big_r(sk4(B),sk3(B)) )).

cnf(pel38_35,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_p(sk1(A))
    | big_r(B,sk4(B)) )).

cnf(pel38_36,negated_conjecture,
    ( big_p(B)
    | big_p(A)
    | big_p(sk1(A))
    | big_p(sk3(B)) )).

cnf(pel38_37,negated_conjecture,
    ( big_p(a) )).

cnf(pel38_38,negated_conjecture,
    ( big_p(sk11)
    | big_p(sk8)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_39,negated_conjecture,
    ( big_p(sk8)
    | big_r(sk10,sk11)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_40,negated_conjecture,
    ( big_p(sk8)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_p(sk9)
    | ~ big_r(B,A)
    | ~ big_r(sk10,B) )).

cnf(pel38_41,negated_conjecture,
    ( big_p(sk11)
    | big_p(sk8)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_r(B,A)
    | ~ big_r(sk9,B) )).

cnf(pel38_42,negated_conjecture,
    ( big_p(sk8)
    | big_r(sk10,sk11)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_r(B,A)
    | ~ big_r(sk9,B) )).

cnf(pel38_43,negated_conjecture,
    ( big_p(sk8)
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_r(D,C)
    | ~ big_r(B,A)
    | ~ big_r(sk10,D)
    | ~ big_r(sk9,B) )).

cnf(pel38_44,negated_conjecture,
    ( big_p(sk11)
    | big_r(sk7,sk8)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_45,negated_conjecture,
    ( big_r(sk10,sk11)
    | big_r(sk7,sk8)
    | ~ big_p(sk7)
    | ~ big_p(sk9) )).

cnf(pel38_46,negated_conjecture,
    ( big_r(sk7,sk8)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_p(sk9)
    | ~ big_r(B,A)
    | ~ big_r(sk10,B) )).

cnf(pel38_47,negated_conjecture,
    ( big_p(sk11)
    | big_r(sk7,sk8)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_r(B,A)
    | ~ big_r(sk9,B) )).

cnf(pel38_48,negated_conjecture,
    ( big_r(sk10,sk11)
    | big_r(sk7,sk8)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_r(B,A)
    | ~ big_r(sk9,B) )).

cnf(pel38_49,negated_conjecture,
    ( big_r(sk7,sk8)
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_p(sk7)
    | ~ big_r(D,C)
    | ~ big_r(B,A)
    | ~ big_r(sk10,D)
    | ~ big_r(sk9,B) )).

cnf(pel38_50,negated_conjecture,
    ( big_p(sk11)
    | ~ big_p(A)
    | ~ big_p(sk9)
    | ~ big_r(B,A)
    | ~ big_r(sk7,B) )).

cnf(pel38_51,negated_conjecture,
    ( big_r(sk10,sk11)
    | ~ big_p(A)
    | ~ big_p(sk9)
    | ~ big_r(B,A)
    | ~ big_r(sk7,B) )).

cnf(pel38_52,negated_conjecture,
    ( ~ big_p(C)
    | ~ big_p(A)
    | ~ big_p(sk9)
    | ~ big_r(D,C)
    | ~ big_r(B,A)
    | ~ big_r(sk10,D)
    | ~ big_r(sk7,B) )).

cnf(pel38_53,negated_conjecture,
    ( big_p(sk11)
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A)
    | ~ big_r(sk7,B)
    | ~ big_r(sk9,D) )).

cnf(pel38_54,negated_conjecture,
    ( big_r(sk10,sk11)
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(D,C)
    | ~ big_r(B,A)
    | ~ big_r(sk7,B)
    | ~ big_r(sk9,D) )).

cnf(pel38_55,negated_conjecture,
    ( ~ big_p(E)
    | ~ big_p(C)
    | ~ big_p(A)
    | ~ big_r(F,E)
    | ~ big_r(D,C)
    | ~ big_r(B,A)
    | ~ big_r(sk10,F)
    | ~ big_r(sk7,B)
    | ~ big_r(sk9,D) )).

%--------------------------------------------------------------------------
