%--------------------------------------------------------------------------
% File     : SYN004-1.007 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Implications that form a contradiction
% Version  : Biased.
% English  : P1 & Q1 -> P2     P1 & Q1 -> Q2     :
%            P2 & Q2 -> P3     P2 & Q2 -> Q3     :
%              ......           ......           :
%            Pk-1 & Qk-1 ->Pk  Pk-1 & Qk-1 -> Qk :
%            P1     Q1         ~Pk v ~Qk         :
%          : The size is k, in the above.

% Refs     : [Pla82] Plaisted (1982), A Simplified Problem Reduction Format
% Source   : [Pla82]
% Names    : Problem 5.3 [Pla82]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   15 (   0 non-Horn;   2 unit;  15 RR)
%            Number of atoms       :   40 (   0 equality)
%            Maximal clause size   :    3 (   3 average)
%            Number of predicates  :   14 (  14 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : "This set of clauses can cause the following strategies to
%            generate minimal length proofs having length exponential in k:
%            SL-resolution, locking resolution with a bad choice of indices.
%            However, all of the following strategies can always generate
%            proofs polynomial in the size of S if S is an inconsistent set
%            of propositional Horn clauses: all-negative resolution,
%            set-of-support strategy, ancestry-filter form, and input
%            resolution." [Pla82] p.244.
%          : tptp2X: -f tptp -s7 SYN004-1.g
%--------------------------------------------------------------------------
cnf(pqp_1,negated_conjecture,
    ( ~ p_1
    | ~ q_1
    | p_2 )).

cnf(pqp_2,negated_conjecture,
    ( ~ p_2
    | ~ q_2
    | p_3 )).

cnf(pqp_3,negated_conjecture,
    ( ~ p_3
    | ~ q_3
    | p_4 )).

cnf(pqp_4,negated_conjecture,
    ( ~ p_4
    | ~ q_4
    | p_5 )).

cnf(pqp_5,negated_conjecture,
    ( ~ p_5
    | ~ q_5
    | p_6 )).

cnf(pqp_6,negated_conjecture,
    ( ~ p_6
    | ~ q_6
    | p_7 )).

cnf(pqq_1,negated_conjecture,
    ( ~ p_1
    | ~ q_1
    | q_2 )).

cnf(pqq_2,negated_conjecture,
    ( ~ p_2
    | ~ q_2
    | q_3 )).

cnf(pqq_3,negated_conjecture,
    ( ~ p_3
    | ~ q_3
    | q_4 )).

cnf(pqq_4,negated_conjecture,
    ( ~ p_4
    | ~ q_4
    | q_5 )).

cnf(pqq_5,negated_conjecture,
    ( ~ p_5
    | ~ q_5
    | q_6 )).

cnf(pqq_6,negated_conjecture,
    ( ~ p_6
    | ~ q_6
    | q_7 )).

cnf(base_1,negated_conjecture,
    ( p_1 )).

cnf(base_2,negated_conjecture,
    ( q_1 )).

cnf(base_3,negated_conjecture,
    ( ~ p_7
    | ~ q_7 )).

%--------------------------------------------------------------------------
