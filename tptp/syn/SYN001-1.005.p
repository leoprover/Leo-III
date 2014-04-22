%------------------------------------------------------------------------------
% File     : SYN001-1.005 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : All signed combinations of some propositions.
% Version  : Especial.
% English  : Pelletier 2: A biconditional version of the 'most difficult'
%            theorem proved by the new logic theorist.
%          : Pelletier 6: The Law of Excluded Middle: can be quite
%            difficult for 'natural' systems.
%          : Pelletier 7: Expanded Law of Excluded Middle. The strategies
%            of the original Logic Theorist cannot prove this.
%          : Pelletier 8: Pierce's Law. Unprovable by Logic Theorist, and
%            tricky for 'natural' systems.
%          : Pelletier 11: A simple problem designed to see whether
%            'natural' systems can do it efficiently (or whether they
%            incorrectly try to prove the -> each way).
%          : The size is the number of propositions.

% Refs     : [NS72]  Newell & Simon (1972), Human Problem Solving
%          : [LS74]  Lawrence & Starkey (1974), Experimental Tests of Resol
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
%          : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
% Source   : [SPRFN], [Pel86]
% Names    : ls5 (Size 2) [LS74]
%          : ls5 (Size 2) [WM76]
%          : Pelletier 2 (Size 1) [Pel86]
%          : Pelletier 6 (Size 1) [Pel86]
%          : Pelletier 7 (Size 1) [Pel86]
%          : Pelletier 8 (Size 1) [Pel86]
%          : Pelletier 9 (Size 2) [Pel86]
%          : Pelletier 11 (Size 1) [Pel86]
%          : Pelletier 12 (Size 3) [Pel86]
%          : Pelletier 14 (Size 2) [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.1.0
% Syntax   : Number of clauses     :   32 (  26 non-Horn;   0 unit;  32 RR)
%            Number of atoms       :  160 (   0 equality)
%            Maximal clause size   :    5 (   5 average)
%            Number of predicates  :    5 (   5 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%          : tptp2X: -f tptp -s5 SYN001-1.g
%------------------------------------------------------------------------------
cnf(ppppp,negated_conjecture,
    ( p_1
    | p_2
    | p_3
    | p_4
    | p_5 )).

cnf(ppppn,negated_conjecture,
    ( p_1
    | p_2
    | p_3
    | p_4
    | ~ p_5 )).

cnf(pppnp,negated_conjecture,
    ( p_1
    | p_2
    | p_3
    | ~ p_4
    | p_5 )).

cnf(pppnn,negated_conjecture,
    ( p_1
    | p_2
    | p_3
    | ~ p_4
    | ~ p_5 )).

cnf(ppnpp,negated_conjecture,
    ( p_1
    | p_2
    | ~ p_3
    | p_4
    | p_5 )).

cnf(ppnpn,negated_conjecture,
    ( p_1
    | p_2
    | ~ p_3
    | p_4
    | ~ p_5 )).

cnf(ppnnp,negated_conjecture,
    ( p_1
    | p_2
    | ~ p_3
    | ~ p_4
    | p_5 )).

cnf(ppnnn,negated_conjecture,
    ( p_1
    | p_2
    | ~ p_3
    | ~ p_4
    | ~ p_5 )).

cnf(pnppp,negated_conjecture,
    ( p_1
    | ~ p_2
    | p_3
    | p_4
    | p_5 )).

cnf(pnppn,negated_conjecture,
    ( p_1
    | ~ p_2
    | p_3
    | p_4
    | ~ p_5 )).

cnf(pnpnp,negated_conjecture,
    ( p_1
    | ~ p_2
    | p_3
    | ~ p_4
    | p_5 )).

cnf(pnpnn,negated_conjecture,
    ( p_1
    | ~ p_2
    | p_3
    | ~ p_4
    | ~ p_5 )).

cnf(pnnpp,negated_conjecture,
    ( p_1
    | ~ p_2
    | ~ p_3
    | p_4
    | p_5 )).

cnf(pnnpn,negated_conjecture,
    ( p_1
    | ~ p_2
    | ~ p_3
    | p_4
    | ~ p_5 )).

cnf(pnnnp,negated_conjecture,
    ( p_1
    | ~ p_2
    | ~ p_3
    | ~ p_4
    | p_5 )).

cnf(pnnnn,negated_conjecture,
    ( p_1
    | ~ p_2
    | ~ p_3
    | ~ p_4
    | ~ p_5 )).

cnf(npppp,negated_conjecture,
    ( ~ p_1
    | p_2
    | p_3
    | p_4
    | p_5 )).

cnf(npppn,negated_conjecture,
    ( ~ p_1
    | p_2
    | p_3
    | p_4
    | ~ p_5 )).

cnf(nppnp,negated_conjecture,
    ( ~ p_1
    | p_2
    | p_3
    | ~ p_4
    | p_5 )).

cnf(nppnn,negated_conjecture,
    ( ~ p_1
    | p_2
    | p_3
    | ~ p_4
    | ~ p_5 )).

cnf(npnpp,negated_conjecture,
    ( ~ p_1
    | p_2
    | ~ p_3
    | p_4
    | p_5 )).

cnf(npnpn,negated_conjecture,
    ( ~ p_1
    | p_2
    | ~ p_3
    | p_4
    | ~ p_5 )).

cnf(npnnp,negated_conjecture,
    ( ~ p_1
    | p_2
    | ~ p_3
    | ~ p_4
    | p_5 )).

cnf(npnnn,negated_conjecture,
    ( ~ p_1
    | p_2
    | ~ p_3
    | ~ p_4
    | ~ p_5 )).

cnf(nnppp,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | p_3
    | p_4
    | p_5 )).

cnf(nnppn,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | p_3
    | p_4
    | ~ p_5 )).

cnf(nnpnp,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | p_3
    | ~ p_4
    | p_5 )).

cnf(nnpnn,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | p_3
    | ~ p_4
    | ~ p_5 )).

cnf(nnnpp,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | ~ p_3
    | p_4
    | p_5 )).

cnf(nnnpn,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | ~ p_3
    | p_4
    | ~ p_5 )).

cnf(nnnnp,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | ~ p_3
    | ~ p_4
    | p_5 )).

cnf(nnnnn,negated_conjecture,
    ( ~ p_1
    | ~ p_2
    | ~ p_3
    | ~ p_4
    | ~ p_5 )).

%------------------------------------------------------------------------------
