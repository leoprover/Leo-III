%--------------------------------------------------------------------------
% File     : SYN036-4 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Andrews Challenge Problem
% Version  : Especial.
%            Theorem formulation : Different clausal form.
% English  :

% Refs     : [DeC79] DeChampeaux (1979), Sub-problem Finder and Instance Ch
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [AZ89]  Angshuman & Zhang (1989), Andrews' Challenge Problem:
%          : [Qua90] Quaife (1990), Andrews' Challenge Problem Revisited
% Source   : [Qua90]
% Names    : Theorem A [Qua90]

% Status   : Unsatisfiable
% Rating   : 0.00 v5.4.0, 0.10 v5.2.0, 0.00 v5.1.0, 0.09 v5.0.0, 0.14 v4.1.0, 0.12 v4.0.1, 0.00 v4.0.0, 0.14 v3.4.0, 0.25 v3.3.0, 0.33 v3.2.0, 0.00 v3.1.0, 0.33 v2.7.0, 0.12 v2.6.0, 0.00 v2.5.0, 0.20 v2.4.0, 0.00 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   32 (  25 non-Horn;   0 unit;   6 RR)
%            Number of atoms       :  168 (   0 equality)
%            Maximal clause size   :    6 (   5 average)
%            Number of predicates  :    2 (   0 propositional; 1-1 arity)
%            Number of functors    :    6 (   2 constant; 0-1 arity)
%            Number of variables   :   81 (  42 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause_1,negated_conjecture,
    ( ~ p(cx)
    | ~ q(cw)
    | p(Y3)
    | q(Z3) )).

cnf(clause_2,negated_conjecture,
    ( ~ p(cx)
    | ~ q(Z4)
    | p(Y3)
    | q(cw) )).

cnf(clause_3,negated_conjecture,
    ( ~ p(Y4)
    | ~ q(cw)
    | p(cx)
    | q(Z3) )).

cnf(clause_4,negated_conjecture,
    ( ~ p(Y4)
    | ~ q(Z4)
    | p(cx)
    | q(cw) )).

cnf(clause_5,negated_conjecture,
    ( ~ p(cx)
    | ~ p(X7)
    | ~ p(fy5(X7))
    | ~ q(cw)
    | q(Z3) )).

cnf(clause_6,negated_conjecture,
    ( ~ p(cx)
    | ~ p(X7)
    | ~ p(fy5(X7))
    | ~ q(Z4)
    | q(cw) )).

cnf(clause_7,negated_conjecture,
    ( ~ p(cx)
    | ~ q(cw)
    | ~ q(W9)
    | ~ q(fz5(W9))
    | p(Y3) )).

cnf(clause_8,negated_conjecture,
    ( ~ p(cx)
    | ~ q(W2)
    | p(Y1)
    | q(W4)
    | q(fz(W4)) )).

cnf(clause_9,negated_conjecture,
    ( ~ p(cx)
    | ~ q(W3)
    | ~ q(fz(W3))
    | p(Y1)
    | q(W4) )).

cnf(clause_10,negated_conjecture,
    ( ~ p(cx)
    | p(Y1)
    | q(cw)
    | q(W3)
    | q(fz(W3)) )).

cnf(clause_11,negated_conjecture,
    ( ~ p(X5)
    | ~ p(fy(X5))
    | ~ q(cw)
    | p(X2)
    | q(Z1) )).

cnf(clause_12,negated_conjecture,
    ( ~ p(X5)
    | ~ q(cw)
    | p(X2)
    | p(fy(X2))
    | q(Z1) )).

cnf(clause_13,negated_conjecture,
    ( ~ p(X2)
    | ~ p(fy5(X9))
    | ~ q(Z4)
    | p(X8)
    | q(cw) )).

cnf(clause_14,negated_conjecture,
    ( ~ p(X9)
    | ~ q(Z4)
    | p(X8)
    | p(fy5(X8))
    | q(cw) )).

cnf(clause_15,negated_conjecture,
    ( ~ p(Y2)
    | p(cx)
    | q(cw)
    | q(W3)
    | q(fz(W3)) )).

cnf(clause_16,negated_conjecture,
    ( ~ p(Y4)
    | ~ q(cw)
    | ~ q(W9)
    | ~ q(fz5(W9))
    | p(cx) )).

cnf(clause_17,negated_conjecture,
    ( ~ p(Y4)
    | ~ q(W6)
    | p(cx)
    | q(W9)
    | q(fz5(W9)) )).

cnf(clause_18,negated_conjecture,
    ( ~ p(Y4)
    | ~ q(W9)
    | ~ q(fz5(W9))
    | p(cx)
    | q(W10) )).

cnf(clause_19,negated_conjecture,
    ( ~ q(cw)
    | p(cx)
    | p(X1)
    | p(fy(X1))
    | q(Z1) )).

cnf(clause_20,negated_conjecture,
    ( ~ q(Z2)
    | p(cx)
    | p(X1)
    | p(fy(X1))
    | q(cw) )).

cnf(clause_21,negated_conjecture,
    ( ~ p(cx)
    | ~ p(X1)
    | ~ p(fy(X1))
    | ~ q(W3)
    | ~ q(fz(W3))
    | q(cw) )).

cnf(clause_22,negated_conjecture,
    ( ~ p(cx)
    | ~ p(X1)
    | ~ p(fy(X1))
    | q(cw)
    | q(W3)
    | q(fz(W3)) )).

cnf(clause_23,negated_conjecture,
    ( ~ p(cx)
    | ~ p(X7)
    | ~ p(fy5(X7))
    | ~ q(cw)
    | ~ q(W9)
    | ~ q(fz5(W9)) )).

cnf(clause_24,negated_conjecture,
    ( ~ p(X1)
    | ~ p(fy(X1))
    | ~ q(cw)
    | ~ q(W3)
    | ~ q(fz(W3))
    | p(cx) )).

cnf(clause_25,negated_conjecture,
    ( ~ p(X4)
    | ~ p(fy(X4))
    | ~ q(W1)
    | p(X2)
    | q(W4)
    | q(fz(W4)) )).

cnf(clause_26,negated_conjecture,
    ( ~ p(X4)
    | ~ p(fy(X4))
    | ~ q(W3)
    | ~ q(fz(W3))
    | p(X2)
    | q(W4) )).

cnf(clause_27,negated_conjecture,
    ( ~ p(X4)
    | ~ q(W1)
    | p(X2)
    | p(fy(X2))
    | q(W4)
    | q(fz(W4)) )).

cnf(clause_28,negated_conjecture,
    ( ~ p(X4)
    | ~ q(W3)
    | ~ q(fz(W3))
    | p(X2)
    | p(fy(X2))
    | q(W4) )).

cnf(clause_29,negated_conjecture,
    ( ~ p(X7)
    | ~ p(fy5(X7))
    | p(cx)
    | q(cw)
    | q(W9)
    | q(fz5(W9)) )).

cnf(clause_30,negated_conjecture,
    ( ~ q(cw)
    | ~ q(W3)
    | ~ q(fz(W3))
    | p(cx)
    | p(X1)
    | p(fy(X1)) )).

cnf(clause_31,negated_conjecture,
    ( ~ q(W9)
    | ~ q(fz5(W9))
    | p(cx)
    | p(X7)
    | p(fy5(X7))
    | q(cw) )).

cnf(clause_32,negated_conjecture,
    ( p(cx)
    | p(X7)
    | p(fy5(X7))
    | q(cw)
    | q(W9)
    | q(fz5(W9)) )).

%--------------------------------------------------------------------------
