%--------------------------------------------------------------------------
% File     : SYN031-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : MQW
% Version  : Especial.
% English  :

% Refs     : [MRS72] Michie et al. (1972), G-deduction
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [SPRFN]
% Names    : MQW [MRS72]
%          : MQW [WM76]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    5 (   2 non-Horn;   0 unit;   3 RR)
%            Number of atoms       :   10 (   0 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :    2 (   1 constant; 0-1 arity)
%            Number of variables   :    8 (   3 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(clause1,axiom,
    ( g(A,a)
    | g(f(A),A) )).

cnf(clause2,axiom,
    ( g(A,a)
    | g(A,f(A)) )).

cnf(clause3,axiom,
    ( ~ g(A,B)
    | g(f(B),B) )).

cnf(clause4,axiom,
    ( ~ g(A,B)
    | g(B,f(B)) )).

cnf(theorem,negated_conjecture,
    ( ~ g(A,B)
    | ~ g(B,a) )).

%--------------------------------------------------------------------------
