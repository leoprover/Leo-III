%--------------------------------------------------------------------------
% File     : SYN029-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : EW2
% Version  : Especial.
% English  :

% Refs     : [MRS72] Michie et al. (1972), G-deduction
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [SPRFN]
% Names    : EW2 [MRS72]
%          : EW2 [WM76]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    5 (   1 non-Horn;   1 unit;   5 RR)
%            Number of atoms       :   10 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    3 (   3 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%--------------------------------------------------------------------------
cnf(clause1,axiom,
    ( q
    | p )).

cnf(clause2,axiom,
    ( ~ p
    | q )).

cnf(clause3,axiom,
    ( ~ r
    | ~ q
    | p )).

cnf(clause4,axiom,
    ( r )).

cnf(prove_something,negated_conjecture,
    ( ~ p
    | ~ q )).

%--------------------------------------------------------------------------
