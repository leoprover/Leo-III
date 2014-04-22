%--------------------------------------------------------------------------
% File     : SYN039-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : A challenge to resolution programs
% Version  : Biased.
% English  :

% Refs     : [Lif89] Lifschitz (1989), What is the Inverse Method?
% Source   : [OTTER]
% Names    : lifsch.in [OTTER]

% Status   : Unsatisfiable
% Rating   : 0.00 v3.3.0, 0.33 v3.2.0, 0.00 v2.7.0, 0.12 v2.6.0, 0.00 v2.1.0, 0.37 v2.0.0
% Syntax   : Number of clauses     :   27 (  11 non-Horn;   0 unit;   7 RR)
%            Number of atoms       :   81 (   0 equality)
%            Maximal clause size   :    3 (   3 average)
%            Number of predicates  :    3 (   0 propositional; 2-2 arity)
%            Number of functors    :    1 (   0 constant; 2-2 arity)
%            Number of variables   :   84 (  21 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : It is easily solved by Maslov's inverse method. "In a personal
%            communication, Minc quotes one problem that was successfully
%            solved by the program not later than 1968" [Lif89] p.14.
%--------------------------------------------------------------------------
% formula_list(sos).
% -(exists x exists x1 all y exists z exists z1
%      (  ( -p(y,y) | p(x,x)   | -s(z,x)  ) &
%         ( s(x,y)  | -s(y,z)  | q(z1,z1) ) &
%         ( q(x1,y) | -q(y,z1) | s(x1,x1) )  )).

cnf(c_1,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | ~ s(X,f1(X,X1))
    | ~ q(X1,f1(X,X1)) )).

cnf(c_2,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | ~ s(X,f1(X,X1))
    | q(f1(X,X1),Z1) )).

cnf(c_3,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | ~ s(X,f1(X,X1))
    | ~ s(X1,X1) )).

cnf(c_4,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | s(f1(X,X1),Z)
    | ~ q(X1,f1(X,X1)) )).

cnf(c_5,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | s(f1(X,X1),Z)
    | q(f1(X,X1),Z1) )).

cnf(c_6,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | s(f1(X,X1),Z)
    | ~ s(X1,X1) )).

cnf(c_7,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | ~ q(Z1,Z1)
    | ~ q(X1,f1(X,X1)) )).

cnf(c_8,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | ~ q(Z1,Z1)
    | q(f1(X,X1),Z1) )).

cnf(c_9,negated_conjecture,
    ( p(f1(X,X1),f1(X,X1))
    | ~ q(Z1,Z1)
    | ~ s(X1,X1) )).

cnf(c_10,negated_conjecture,
    ( ~ p(X,X)
    | ~ s(X,f1(X,X1))
    | ~ q(X1,f1(X,X1)) )).

cnf(c_11,negated_conjecture,
    ( ~ p(X,X)
    | ~ s(X,f1(X,X1))
    | q(f1(X,X1),Z1) )).

cnf(c_12,negated_conjecture,
    ( ~ p(X,X)
    | ~ s(X,f1(X,X1))
    | ~ s(X1,X1) )).

cnf(c_13,negated_conjecture,
    ( ~ p(X,X)
    | s(f1(X,X1),Z)
    | ~ q(X1,f1(X,X1)) )).

cnf(c_14,negated_conjecture,
    ( ~ p(X,X)
    | s(f1(X,X1),Z)
    | q(f1(X,X1),Z1) )).

cnf(c_15,negated_conjecture,
    ( ~ p(X,X)
    | s(f1(X,X1),Z)
    | ~ s(X1,X1) )).

cnf(c_16,negated_conjecture,
    ( ~ p(X,X)
    | ~ q(Z1,Z1)
    | ~ q(X1,f1(X,X1)) )).

cnf(c_17,negated_conjecture,
    ( ~ p(X,X)
    | ~ q(Z1,Z1)
    | q(f1(X,X1),Z1) )).

cnf(c_18,negated_conjecture,
    ( ~ p(X,X)
    | ~ q(Z1,Z1)
    | ~ s(X1,X1) )).

cnf(c_19,negated_conjecture,
    ( s(Z,X)
    | ~ s(X,f1(X,X1))
    | ~ q(X1,f1(X,X1)) )).

cnf(c_20,negated_conjecture,
    ( s(Z,X)
    | ~ s(X,f1(X,X1))
    | q(f1(X,X1),Z1) )).

cnf(c_21,negated_conjecture,
    ( s(Z,X)
    | ~ s(X,f1(X,X1))
    | ~ s(X1,X1) )).

cnf(c_22,negated_conjecture,
    ( s(Z,X)
    | s(f1(X,X1),Z)
    | ~ q(X1,f1(X,X1)) )).

cnf(c_23,negated_conjecture,
    ( s(Z,X)
    | s(f1(X,X1),Z)
    | q(f1(X,X1),Z1) )).

cnf(c_24,negated_conjecture,
    ( s(Z,X)
    | s(f1(X,X1),Z)
    | ~ s(X1,X1) )).

cnf(c_25,negated_conjecture,
    ( s(Z,X)
    | ~ q(Z1,Z1)
    | ~ q(X1,f1(X,X1)) )).

cnf(c_26,negated_conjecture,
    ( s(Z,X)
    | ~ q(Z1,Z1)
    | q(f1(X,X1),Z1) )).

cnf(c_27,negated_conjecture,
    ( s(Z,X)
    | ~ q(Z1,Z1)
    | ~ s(X1,X1) )).

%--------------------------------------------------------------------------
