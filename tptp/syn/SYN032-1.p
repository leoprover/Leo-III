%--------------------------------------------------------------------------
% File     : SYN032-1 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Ances
% Version  : Especial.
% English  :

% Refs     : [RR+72] Reboh et al. (1972), Study of automatic theorem provin
% Source   : [SPRFN]
% Names    : ANCES2 [RR+72]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   3 non-Horn;   0 unit;   7 RR)
%            Number of atoms       :   17 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    6 (   6 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton)
%            Maximal term depth    :    0 (   0 average)
% SPC      : CNF_UNS_PRP

% Comments :
%--------------------------------------------------------------------------
cnf(three,hypothesis,
    ( h
    | a
    | ~ j )).

cnf(four,hypothesis,
    ( h
    | k
    | j )).

cnf(five,hypothesis,
    ( h
    | j
    | ~ k )).

cnf(six,hypothesis,
    ( b
    | ~ a )).

cnf(seven,hypothesis,
    ( c
    | ~ h )).

cnf(one,hypothesis,
    ( ~ h
    | ~ c )).

cnf(prove_something,negated_conjecture,
    ( ~ a
    | ~ b )).

%--------------------------------------------------------------------------
