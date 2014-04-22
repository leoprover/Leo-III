%--------------------------------------------------------------------------
% File     : SYN002-1.007.008 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Odd and Even Problem
% Version  : Especial.
% English  : Given by the clauses C1: p(X) v p(f^M(X)) and C2: ~p(X)
%            v ~p(f^N(X)), where if M is odd N is even and vice versa,
%            N > M. The sizes are used for N and M.

% Refs     : [Soc92] Socher-Ambrosius (1992), How to Avoid the Derivation o
% Source   : [Soc92]
% Names    : ederX-Y.lop (Size X:Y) [TUM]

% Status   : Unsatisfiable
% Rating   : 0.00 v3.1.0, 0.17 v2.7.0, 0.12 v2.6.0, 0.00 v2.1.0
% Syntax   : Number of clauses     :    2 (   1 non-Horn;   0 unit;   1 RR)
%            Number of atoms       :    4 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    1 (   0 propositional; 1-1 arity)
%            Number of functors    :    1 (   0 constant; 1-1 arity)
%            Number of variables   :    2 (   0 singleton)
%            Maximal term depth    :    9 (   5 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%          : tptp2X: -f tptp -s7:8 SYN002-1.g
%--------------------------------------------------------------------------
cnf(positive,negated_conjecture,
    ( p(X)
    | p(f(f(f(f(f(f(f(X)))))))) )).

cnf(negative,negated_conjecture,
    ( ~ p(X)
    | ~ p(f(f(f(f(f(f(f(f(X))))))))) )).

%--------------------------------------------------------------------------
