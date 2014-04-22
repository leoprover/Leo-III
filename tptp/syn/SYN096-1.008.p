%--------------------------------------------------------------------------
% File     : SYN096-1.008 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem m(t(3,8))
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : M(T3n) [Pla94]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   65 (   0 non-Horn;   4 unit;  65 RR)
%            Number of atoms       :  182 (   0 equality)
%            Maximal clause size   :    3 (   3 average)
%            Number of predicates  :   34 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   1 constant; 0-0 arity)
%            Number of variables   :   61 (   0 singleton)
%            Maximal term depth    :    1 (   1 average)
% SPC      : CNF_UNS_EPR

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s8 SYN096-1.g
% Bugfixes : v1.2.1 - Bugfix in SYN087-1.
%--------------------------------------------------------------------------
cnf(m_s3_goal_1,negated_conjecture,
    ( ~ p_0(X)
    | ~ q_0(X) )).

cnf(m_s3_type11_1,axiom,
    ( p_0(X)
    | ~ p_1(X)
    | ~ p_2(X) )).

cnf(m_s3_type11_2,axiom,
    ( p_0(X)
    | ~ q_1(X)
    | ~ q_2(X) )).

cnf(m_s3_type11_3,axiom,
    ( p_1(X)
    | ~ p_2(X)
    | ~ p_3(X) )).

cnf(m_s3_type11_4,axiom,
    ( p_1(X)
    | ~ q_2(X)
    | ~ q_3(X) )).

cnf(m_s3_type11_5,axiom,
    ( p_2(X)
    | ~ p_3(X)
    | ~ p_4(X) )).

cnf(m_s3_type11_6,axiom,
    ( p_2(X)
    | ~ q_3(X)
    | ~ q_4(X) )).

cnf(m_s3_type11_7,axiom,
    ( p_3(X)
    | ~ p_4(X)
    | ~ p_5(X) )).

cnf(m_s3_type11_8,axiom,
    ( p_3(X)
    | ~ q_4(X)
    | ~ q_5(X) )).

cnf(m_s3_type11_9,axiom,
    ( p_4(X)
    | ~ p_5(X)
    | ~ p_6(X) )).

cnf(m_s3_type11_10,axiom,
    ( p_4(X)
    | ~ q_5(X)
    | ~ q_6(X) )).

cnf(m_s3_type11_11,axiom,
    ( p_5(X)
    | ~ p_6(X)
    | ~ p_7(X) )).

cnf(m_s3_type11_12,axiom,
    ( p_5(X)
    | ~ q_6(X)
    | ~ q_7(X) )).

cnf(m_s3_type11_13,axiom,
    ( p_6(X)
    | ~ p_7(X)
    | ~ p_8(X) )).

cnf(m_s3_type11_14,axiom,
    ( p_6(X)
    | ~ q_7(X)
    | ~ q_8(X) )).

cnf(m_s3_type11_15,axiom,
    ( p_7(X)
    | ~ p_8(X)
    | ~ p_9(X) )).

cnf(m_s3_type11_16,axiom,
    ( p_7(X)
    | ~ q_8(X)
    | ~ q_9(X) )).

cnf(m_s3_type11_17,axiom,
    ( p_8(X)
    | ~ p_9(X)
    | ~ p_10(X) )).

cnf(m_s3_type11_18,axiom,
    ( p_8(X)
    | ~ q_9(X)
    | ~ q_10(X) )).

cnf(m_s3_type11_19,axiom,
    ( p_9(X)
    | ~ p_10(X)
    | ~ p_11(X) )).

cnf(m_s3_type11_20,axiom,
    ( p_9(X)
    | ~ q_10(X)
    | ~ q_11(X) )).

cnf(m_s3_type11_21,axiom,
    ( p_10(X)
    | ~ p_11(X)
    | ~ p_12(X) )).

cnf(m_s3_type11_22,axiom,
    ( p_10(X)
    | ~ q_11(X)
    | ~ q_12(X) )).

cnf(m_s3_type11_23,axiom,
    ( p_11(X)
    | ~ p_12(X)
    | ~ p_13(X) )).

cnf(m_s3_type11_24,axiom,
    ( p_11(X)
    | ~ q_12(X)
    | ~ q_13(X) )).

cnf(m_s3_type11_25,axiom,
    ( p_12(X)
    | ~ p_13(X)
    | ~ p_14(X) )).

cnf(m_s3_type11_26,axiom,
    ( p_12(X)
    | ~ q_13(X)
    | ~ q_14(X) )).

cnf(m_s3_type11_27,axiom,
    ( p_13(X)
    | ~ p_14(X)
    | ~ p_15(X) )).

cnf(m_s3_type11_28,axiom,
    ( p_13(X)
    | ~ q_14(X)
    | ~ q_15(X) )).

cnf(m_s3_type12_1,axiom,
    ( q_0(X)
    | ~ p_1(X)
    | ~ q_2(X) )).

cnf(m_s3_type12_2,axiom,
    ( q_0(X)
    | ~ q_1(X)
    | ~ p_2(X) )).

cnf(m_s3_type12_3,axiom,
    ( q_1(X)
    | ~ p_2(X)
    | ~ q_3(X) )).

cnf(m_s3_type12_4,axiom,
    ( q_1(X)
    | ~ q_2(X)
    | ~ p_3(X) )).

cnf(m_s3_type12_5,axiom,
    ( q_2(X)
    | ~ p_3(X)
    | ~ q_4(X) )).

cnf(m_s3_type12_6,axiom,
    ( q_2(X)
    | ~ q_3(X)
    | ~ p_4(X) )).

cnf(m_s3_type12_7,axiom,
    ( q_3(X)
    | ~ p_4(X)
    | ~ q_5(X) )).

cnf(m_s3_type12_8,axiom,
    ( q_3(X)
    | ~ q_4(X)
    | ~ p_5(X) )).

cnf(m_s3_type12_9,axiom,
    ( q_4(X)
    | ~ p_5(X)
    | ~ q_6(X) )).

cnf(m_s3_type12_10,axiom,
    ( q_4(X)
    | ~ q_5(X)
    | ~ p_6(X) )).

cnf(m_s3_type12_11,axiom,
    ( q_5(X)
    | ~ p_6(X)
    | ~ q_7(X) )).

cnf(m_s3_type12_12,axiom,
    ( q_5(X)
    | ~ q_6(X)
    | ~ p_7(X) )).

cnf(m_s3_type12_13,axiom,
    ( q_6(X)
    | ~ p_7(X)
    | ~ q_8(X) )).

cnf(m_s3_type12_14,axiom,
    ( q_6(X)
    | ~ q_7(X)
    | ~ p_8(X) )).

cnf(m_s3_type12_15,axiom,
    ( q_7(X)
    | ~ p_8(X)
    | ~ q_9(X) )).

cnf(m_s3_type12_16,axiom,
    ( q_7(X)
    | ~ q_8(X)
    | ~ p_9(X) )).

cnf(m_s3_type12_17,axiom,
    ( q_8(X)
    | ~ p_9(X)
    | ~ q_10(X) )).

cnf(m_s3_type12_18,axiom,
    ( q_8(X)
    | ~ q_9(X)
    | ~ p_10(X) )).

cnf(m_s3_type12_19,axiom,
    ( q_9(X)
    | ~ p_10(X)
    | ~ q_11(X) )).

cnf(m_s3_type12_20,axiom,
    ( q_9(X)
    | ~ q_10(X)
    | ~ p_11(X) )).

cnf(m_s3_type12_21,axiom,
    ( q_10(X)
    | ~ p_11(X)
    | ~ q_12(X) )).

cnf(m_s3_type12_22,axiom,
    ( q_10(X)
    | ~ q_11(X)
    | ~ p_12(X) )).

cnf(m_s3_type12_23,axiom,
    ( q_11(X)
    | ~ p_12(X)
    | ~ q_13(X) )).

cnf(m_s3_type12_24,axiom,
    ( q_11(X)
    | ~ q_12(X)
    | ~ p_13(X) )).

cnf(m_s3_type12_25,axiom,
    ( q_12(X)
    | ~ p_13(X)
    | ~ q_14(X) )).

cnf(m_s3_type12_26,axiom,
    ( q_12(X)
    | ~ q_13(X)
    | ~ p_14(X) )).

cnf(m_s3_type12_27,axiom,
    ( q_13(X)
    | ~ p_14(X)
    | ~ q_15(X) )).

cnf(m_s3_type12_28,axiom,
    ( q_13(X)
    | ~ q_14(X)
    | ~ p_15(X) )).

cnf(m_s3_type2_1,axiom,
    ( p_15(X)
    | ~ p_7(X) )).

cnf(m_s3_type2_2,axiom,
    ( p_16(X)
    | ~ p_8(X) )).

cnf(m_s3_type2_3,axiom,
    ( q_15(X)
    | ~ q_7(X) )).

cnf(m_s3_type2_4,axiom,
    ( q_16(X)
    | ~ q_8(X) )).

cnf(m_t3_1,axiom,
    ( p_14(a) )).

cnf(m_t3_2,axiom,
    ( p_15(a) )).

cnf(m_t3_3,axiom,
    ( q_14(a) )).

cnf(m_t3_4,axiom,
    ( q_15(a) )).

%--------------------------------------------------------------------------
