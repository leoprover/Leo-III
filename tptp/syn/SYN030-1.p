%--------------------------------------------------------------------------
% File     : SYN030-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : EW3
% Version  : Especial.
% English  :

% Refs     : [MRS72] Michie et al. (1972), G-deduction
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [SPRFN]
% Names    : EW3 [MRS72]
%          : EW3 [WM76]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    9 (   1 non-Horn;   0 unit;   9 RR)
%            Number of atoms       :   22 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    5 (   5 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%--------------------------------------------------------------------------
cnf(clause1,axiom,
    ( ~ t
    | ~ s
    | p )).

cnf(clause2,axiom,
    ( ~ r
    | s )).

cnf(clause3,axiom,
    ( ~ t
    | r )).

cnf(clause4,axiom,
    ( ~ r
    | ~ q
    | t )).

cnf(clause5,axiom,
    ( ~ q
    | r )).

cnf(clause6,axiom,
    ( q
    | r )).

cnf(clause7,axiom,
    ( ~ r
    | q )).

cnf(clause8,axiom,
    ( ~ s
    | ~ p
    | q )).

cnf(theorem,negated_conjecture,
    ( ~ p
    | ~ q
    | ~ r )).

%--------------------------------------------------------------------------
