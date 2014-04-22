%--------------------------------------------------------------------------
% File     : SYN084-2 : TPTP v6.0.0. Released v1.2.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 62
% Version  : Especial.
%            Theorem formulation : Different clausification.
% English  :

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Pel88] Pelletier (1988), Errata
%          : [Hah94] Haehnle (1994), Email to G. Sutcliffe
%          : [Pel95] Pelletier (1995), Email to G. Sutcliffe
% Source   : [Hah94]
% Names    : Pelletier 62 [Pel86]

% Status   : Unsatisfiable
% Rating   : 0.00 v2.0.0
% Syntax   : Number of clauses     :    7 (   2 non-Horn;   1 unit;   6 RR)
%            Number of atoms       :   19 (   0 equality)
%            Maximal clause size   :    4 (   3 average)
%            Number of predicates  :    1 (   0 propositional; 1-1 arity)
%            Number of functors    :    4 (   3 constant; 0-1 arity)
%            Number of variables   :    2 (   0 singleton)
%            Maximal term depth    :    3 (   2 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : This problem is incorrect in [Pel86] and is 'corrected' in
%            [Pel88]. The 1988 version is broken too [Pel95]. This is the
%            correct version.
%          : This CNF has been created from a corrected (<=> as the main
%            connective) version of the [Pel88] FOF version, as prescribed
%            in [Pel95].
%--------------------------------------------------------------------------
cnf(pel62_1,negated_conjecture,
    ( big_p(a) )).

cnf(pel62_2,negated_conjecture,
    ( big_p(f(f(A)))
    | big_p(A)
    | ~ big_p(a) )).

cnf(pel62_3,negated_conjecture,
    ( big_p(f(f(A)))
    | ~ big_p(f(A))
    | ~ big_p(a) )).

cnf(pel62_4,negated_conjecture,
    ( ~ big_p(f(f(sk1)))
    | ~ big_p(f(f(sk2))) )).

cnf(pel62_5,negated_conjecture,
    ( big_p(f(sk1))
    | big_p(f(sk2))
    | ~ big_p(sk1)
    | ~ big_p(sk2) )).

cnf(pel62_6,negated_conjecture,
    ( big_p(f(sk1))
    | ~ big_p(sk1)
    | ~ big_p(f(f(sk2))) )).

cnf(pel62_7,negated_conjecture,
    ( big_p(f(sk2))
    | ~ big_p(sk2)
    | ~ big_p(f(f(sk1))) )).

%--------------------------------------------------------------------------
