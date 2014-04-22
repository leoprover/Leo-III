%--------------------------------------------------------------------------
% File     : SYN102-1.007.007 : TPTP v6.0.0. Bugfixed v1.2.0.
% Domain   : Syntactic
% Problem  : Plaisted problem n(t(3,7),7)
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : N(T3n)) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.3.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of clauses     :   71 (   0 non-Horn;  14 unit;  71 RR)
%            Number of atoms       :  200 (   0 equality)
%            Maximal clause size   :    8 (   3 average)
%            Number of predicates  :   37 (   0 propositional; 1-7 arity)
%            Number of functors    :    2 (   2 constant; 0-0 arity)
%            Number of variables   :  392 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s7:7 SYN102-1.g
% Bugfixes : v1.2.0 - Bugfix in SYN087-1.
%--------------------------------------------------------------------------
cnf(n_s3_goal_1,negated_conjecture,
    ( ~ p_0(a,a,a,a,a,a,a)
    | ~ q_0(a,a,a,a,a,a,a) )).

cnf(n_s3_type11_1,axiom,
    ( p_0(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_2,axiom,
    ( p_0(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_3,axiom,
    ( p_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_4,axiom,
    ( p_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_5,axiom,
    ( p_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_6,axiom,
    ( p_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_7,axiom,
    ( p_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_8,axiom,
    ( p_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_9,axiom,
    ( p_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_10,axiom,
    ( p_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_11,axiom,
    ( p_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_12,axiom,
    ( p_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_13,axiom,
    ( p_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_14,axiom,
    ( p_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_15,axiom,
    ( p_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_16,axiom,
    ( p_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_17,axiom,
    ( p_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_18,axiom,
    ( p_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_19,axiom,
    ( p_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_20,axiom,
    ( p_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_21,axiom,
    ( p_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_22,axiom,
    ( p_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_23,axiom,
    ( p_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type11_24,axiom,
    ( p_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_1,axiom,
    ( q_0(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_2,axiom,
    ( q_0(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_3,axiom,
    ( q_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_4,axiom,
    ( q_1(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_5,axiom,
    ( q_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_6,axiom,
    ( q_2(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_7,axiom,
    ( q_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_8,axiom,
    ( q_3(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_9,axiom,
    ( q_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_10,axiom,
    ( q_4(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_11,axiom,
    ( q_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_12,axiom,
    ( q_5(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_13,axiom,
    ( q_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_14,axiom,
    ( q_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_15,axiom,
    ( q_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_16,axiom,
    ( q_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_17,axiom,
    ( q_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_18,axiom,
    ( q_8(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_19,axiom,
    ( q_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_20,axiom,
    ( q_9(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_21,axiom,
    ( q_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_22,axiom,
    ( q_10(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_23,axiom,
    ( q_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type12_24,axiom,
    ( q_11(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type2_1,axiom,
    ( p_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type2_2,axiom,
    ( p_14(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ p_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type2_3,axiom,
    ( q_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_6(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_s3_type2_4,axiom,
    ( q_14(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ q_7(X_1,X_2,X_3,X_4,X_5,X_6,X_7) )).

cnf(n_t3_1,axiom,
    ( p_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2)
    | ~ nq_3(X_3)
    | ~ nq_4(X_4)
    | ~ nq_5(X_5)
    | ~ nq_6(X_6)
    | ~ nq_7(X_7) )).

cnf(n_t3_2,axiom,
    ( p_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2)
    | ~ nq_3(X_3)
    | ~ nq_4(X_4)
    | ~ nq_5(X_5)
    | ~ nq_6(X_6)
    | ~ nq_7(X_7) )).

cnf(n_t3_3,axiom,
    ( q_12(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2)
    | ~ nq_3(X_3)
    | ~ nq_4(X_4)
    | ~ nq_5(X_5)
    | ~ nq_6(X_6)
    | ~ nq_7(X_7) )).

cnf(n_t3_4,axiom,
    ( q_13(X_1,X_2,X_3,X_4,X_5,X_6,X_7)
    | ~ nq_1(X_1)
    | ~ nq_2(X_2)
    | ~ nq_3(X_3)
    | ~ nq_4(X_4)
    | ~ nq_5(X_5)
    | ~ nq_6(X_6)
    | ~ nq_7(X_7) )).

cnf(n_1,axiom,
    ( nq_1(a) )).

cnf(n_2,axiom,
    ( nq_1(b) )).

cnf(n_3,axiom,
    ( nq_2(a) )).

cnf(n_4,axiom,
    ( nq_2(b) )).

cnf(n_5,axiom,
    ( nq_3(a) )).

cnf(n_6,axiom,
    ( nq_3(b) )).

cnf(n_7,axiom,
    ( nq_4(a) )).

cnf(n_8,axiom,
    ( nq_4(b) )).

cnf(n_9,axiom,
    ( nq_5(a) )).

cnf(n_10,axiom,
    ( nq_5(b) )).

cnf(n_11,axiom,
    ( nq_6(a) )).

cnf(n_12,axiom,
    ( nq_6(b) )).

cnf(n_13,axiom,
    ( nq_7(a) )).

cnf(n_14,axiom,
    ( nq_7(b) )).

%--------------------------------------------------------------------------
