%--------------------------------------------------------------------------
% File     : SYN014-2 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : A problem in quantification theory
% Version  : [Wan65] axioms : Reduced & Augmented > Especial.
%            Theorem formulation : Modified.
% English  :

% Refs     : [Wos65] Wos (1965), Unpublished Note
%          : [Wan65] Wang (1965), Formalization and Automatic Theorem-Provi
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [SPRFN]
% Names    : Problem 32 [Wos65]
%          : wos32 [WM76]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.2.1, 0.25 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   26 (  13 non-Horn;   7 unit;  23 RR)
%            Number of atoms       :   70 (   0 equality)
%            Maximal clause size   :    6 (   3 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :    6 (   4 constant; 0-1 arity)
%            Number of variables   :   29 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments :
%--------------------------------------------------------------------------
cnf(reflexivityish,axiom,
    ( equalish(X,X) )).

cnf(symmetryish,axiom,
    ( ~ equalish(X,Y)
    | equalish(Y,X) )).

cnf(transitivityish,axiom,
    ( ~ equalish(X,Y)
    | ~ equalish(Y,Z)
    | equalish(X,Z) )).

cnf(element_substitutionish1,axiom,
    ( ~ equalish(A,B)
    | ~ element(C,A)
    | element(C,B) )).

cnf(element_substitutionish2,axiom,
    ( ~ element(A,B)
    | ~ equalish(A,C)
    | element(C,B) )).

cnf(c_3,negated_conjecture,
    ( ~ element(A,a)
    | equalish(A,k)
    | equalish(A,a) )).

cnf(c_4,negated_conjecture,
    ( ~ equalish(A,k)
    | element(A,a)
    | equalish(A,a) )).

cnf(c_5,negated_conjecture,
    ( ~ equalish(f(A),m)
    | ~ element(A,m)
    | equalish(A,m) )).

cnf(c_6,negated_conjecture,
    ( ~ equalish(f(A),A)
    | ~ element(A,m)
    | equalish(A,m) )).

cnf(c_7,negated_conjecture,
    ( element(A,f(A))
    | ~ element(A,m)
    | equalish(A,m) )).

cnf(c_8,negated_conjecture,
    ( element(f(A),A)
    | ~ element(A,m)
    | equalish(A,m) )).

cnf(c_9,negated_conjecture,
    ( ~ element(A,B)
    | ~ element(B,A)
    | equalish(A,B)
    | equalish(A,m)
    | element(B,m)
    | equalish(B,m) )).

cnf(c_10,negated_conjecture,
    ( ~ equalish(g(A),n)
    | element(A,n)
    | equalish(A,n) )).

cnf(c_11,negated_conjecture,
    ( ~ equalish(g(A),A)
    | element(A,n)
    | equalish(A,n) )).

cnf(c_12,negated_conjecture,
    ( element(A,g(A))
    | element(A,n)
    | equalish(A,n) )).

cnf(c_13,negated_conjecture,
    ( element(g(A),A)
    | element(A,n)
    | equalish(A,n) )).

cnf(c_14,negated_conjecture,
    ( ~ element(A,B)
    | ~ element(B,A)
    | equalish(A,B)
    | equalish(A,n)
    | ~ element(B,n)
    | equalish(B,n) )).

cnf(c_15,negated_conjecture,
    ( ~ equalish(A,m)
    | element(A,k)
    | equalish(A,k) )).

cnf(c_16,negated_conjecture,
    ( ~ equalish(A,n)
    | element(A,k)
    | equalish(A,k) )).

cnf(c_17,negated_conjecture,
    ( ~ element(A,k)
    | equalish(A,n)
    | equalish(A,m)
    | equalish(A,k) )).

cnf(c_18,negated_conjecture,
    ( ~ equalish(n,a) )).

cnf(c_19,negated_conjecture,
    ( ~ equalish(m,n) )).

%----This is the only difference from wos33 - SYN015-1.p
cnf(c_20,negated_conjecture,
    ( equalish(n,k) )).

cnf(c_21,negated_conjecture,
    ( ~ equalish(m,a) )).

cnf(c_22,negated_conjecture,
    ( ~ equalish(k,a) )).

cnf(c_23,negated_conjecture,
    ( equalish(m,k) )).

%--------------------------------------------------------------------------
