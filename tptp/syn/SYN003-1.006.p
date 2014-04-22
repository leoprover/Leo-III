%--------------------------------------------------------------------------
% File     : SYN003-1.006 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Implications that form a contradiction
% Version  : Biased.
% English  : P1 & Q1 -> P2     P1 & R1 -> P2     Q -> Q1     R -> R1   :
%            P2 & Q2 -> P3     P2 & R2 -> P3     Q -> Q2     R -> R2   :
%              ......           ......        ....      ....           :
%            Pk-1 & Qk-1 ->Pk  Pk-1 & Rk-1 -> Pk Q -> Qk-1   R -> Rk-1 :
%            P1                ~Pk               Q           R         :
%          : The size is k, in the above.

% Refs     : [Pla82] Plaisted (1982), A Simplified Problem Reduction Format
% Source   : [Pla82]
% Names    : Problem 5.2 [Pla82]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   24 (   0 non-Horn;   4 unit;  24 RR)
%            Number of atoms       :   54 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :   18 (  18 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments : "This set of clauses can cause the following strategies to
%            generate a search space which is exponential in k: All-negative
%            resolution, set-of-support with ~Pk as the support set, input
%            resolution, SL-resolution, locking resolution with a bad choice
%            of indices, and ancestor-filter form (linear resolution)."
%            [Pla82] p.243.
%          : tptp2X: -f tptp -s6 SYN003-1.g
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

cnf(prp_1,negated_conjecture,
    ( ~ p_1
    | ~ r_1
    | p_2 )).

cnf(prp_2,negated_conjecture,
    ( ~ p_2
    | ~ r_2
    | p_3 )).

cnf(prp_3,negated_conjecture,
    ( ~ p_3
    | ~ r_3
    | p_4 )).

cnf(prp_4,negated_conjecture,
    ( ~ p_4
    | ~ r_4
    | p_5 )).

cnf(prp_5,negated_conjecture,
    ( ~ p_5
    | ~ r_5
    | p_6 )).

cnf(qq_1,negated_conjecture,
    ( ~ q
    | q_1 )).

cnf(qq_2,negated_conjecture,
    ( ~ q
    | q_2 )).

cnf(qq_3,negated_conjecture,
    ( ~ q
    | q_3 )).

cnf(qq_4,negated_conjecture,
    ( ~ q
    | q_4 )).

cnf(qq_5,negated_conjecture,
    ( ~ q
    | q_5 )).

cnf(rr_1,negated_conjecture,
    ( ~ r
    | r_1 )).

cnf(rr_2,negated_conjecture,
    ( ~ r
    | r_2 )).

cnf(rr_3,negated_conjecture,
    ( ~ r
    | r_3 )).

cnf(rr_4,negated_conjecture,
    ( ~ r
    | r_4 )).

cnf(rr_5,negated_conjecture,
    ( ~ r
    | r_5 )).

cnf(base_1,negated_conjecture,
    ( p_1 )).

cnf(base_2,negated_conjecture,
    ( ~ p_6 )).

cnf(base_3,negated_conjecture,
    ( q )).

cnf(base_4,negated_conjecture,
    ( r )).

%--------------------------------------------------------------------------
