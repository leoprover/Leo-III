%--------------------------------------------------------------------------
% File     : SYN087-1.003 : TPTP v6.0.0. Bugfixed v1.2.1.
% Domain   : Syntactic
% Problem  : Plaisted problem s(3,3)
% Version  : Biased.
% English  :

% Refs     : [Pla94] Plaisted (1994), The Search Efficiency of Theorem Prov
% Source   : [Pla94]
% Names    : S3n [Pla94]

% Status   : Satisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   21 (   0 non-Horn;   0 unit;  21 RR)
%            Number of atoms       :   58 (   0 equality)
%            Maximal clause size   :    3 (   3 average)
%            Number of predicates  :   14 (  14 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_SAT_PRP

% Comments : Biased away from various calculi.
%          : tptp2X: -f tptp -s3 SYN087-1.g
% Bugfixes : v1.2.1 - Errors in type 1 clauses fixed.
%--------------------------------------------------------------------------
cnf(s3_goal_1,negated_conjecture,
    ( ~ p_0
    | ~ q_0 )).

cnf(s3_type11_1,axiom,
    ( p_0
    | ~ p_1
    | ~ p_2 )).

cnf(s3_type11_2,axiom,
    ( p_0
    | ~ q_1
    | ~ q_2 )).

cnf(s3_type11_3,axiom,
    ( p_1
    | ~ p_2
    | ~ p_3 )).

cnf(s3_type11_4,axiom,
    ( p_1
    | ~ q_2
    | ~ q_3 )).

cnf(s3_type11_5,axiom,
    ( p_2
    | ~ p_3
    | ~ p_4 )).

cnf(s3_type11_6,axiom,
    ( p_2
    | ~ q_3
    | ~ q_4 )).

cnf(s3_type11_7,axiom,
    ( p_3
    | ~ p_4
    | ~ p_5 )).

cnf(s3_type11_8,axiom,
    ( p_3
    | ~ q_4
    | ~ q_5 )).

cnf(s3_type12_1,axiom,
    ( q_0
    | ~ p_1
    | ~ q_2 )).

cnf(s3_type12_2,axiom,
    ( q_0
    | ~ q_1
    | ~ p_2 )).

cnf(s3_type12_3,axiom,
    ( q_1
    | ~ p_2
    | ~ q_3 )).

cnf(s3_type12_4,axiom,
    ( q_1
    | ~ q_2
    | ~ p_3 )).

cnf(s3_type12_5,axiom,
    ( q_2
    | ~ p_3
    | ~ q_4 )).

cnf(s3_type12_6,axiom,
    ( q_2
    | ~ q_3
    | ~ p_4 )).

cnf(s3_type12_7,axiom,
    ( q_3
    | ~ p_4
    | ~ q_5 )).

cnf(s3_type12_8,axiom,
    ( q_3
    | ~ q_4
    | ~ p_5 )).

cnf(s3_type2_1,axiom,
    ( p_5
    | ~ p_2 )).

cnf(s3_type2_2,axiom,
    ( p_6
    | ~ p_3 )).

cnf(s3_type2_3,axiom,
    ( q_5
    | ~ q_2 )).

cnf(s3_type2_4,axiom,
    ( q_6
    | ~ q_3 )).

%--------------------------------------------------------------------------
