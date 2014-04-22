%--------------------------------------------------------------------------
% File     : SYN034-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : QW
% Version  : Especial.
% English  :

% Refs     : [MRS72] Michie et al. (1972), G-deduction
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [SPRFN]
% Names    : QW [MRS72]
%          : QW [WM76]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    3 (   2 non-Horn;   0 unit;   1 RR)
%            Number of atoms       :    7 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    2 (   1 constant; 0-1 arity)
%            Number of variables   :    4 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause1,axiom,
    ( p(A,a)
    | p(A,f(A)) )).

cnf(clause2,axiom,
    ( p(A,a)
    | p(f(A),A) )).

cnf(theorem,negated_conjecture,
    ( ~ p(A,B)
    | ~ p(B,A)
    | ~ p(B,a) )).

%--------------------------------------------------------------------------
