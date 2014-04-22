%--------------------------------------------------------------------------
% File     : SYN007+1.014 : TPTP v6.0.0. Released v1.0.0.
% Domain   : Syntactic
% Problem  : Pelletier Problem 71
% Version  : Especial.
%            Theorem formulation : For N = SIZE.
% English  : Clausal forms of statements of the form :
%            (p1 <-> (p2 <->...(pN <-> (p1 <-> (p2 <->...<-> pN)...)

% Refs     : [Pel86] Pelletier (1986), Seventy-five Problems for Testing Au
%          : [Urq87] Urquart (1987), Hard Problems for Resolution
% Source   : [Pel86]
% Names    : Pelletier 71 [Pel86]

% Status   : Theorem
% Rating   : 0.00 v4.1.0, 0.39 v4.0.1, 0.37 v4.0.0, 0.40 v3.7.0, 0.67 v3.5.0, 0.38 v3.4.0, 0.33 v3.2.0, 0.78 v3.1.0, 0.67 v2.7.0, 0.33 v2.4.0, 0.33 v2.2.1, 0.00 v2.1.0
% Syntax   : Number of formulae    :    1 (   0 unit)
%            Number of atoms       :   28 (   0 equality)
%            Maximal formula depth :   28 (  28 average)
%            Number of connectives :   27 (   0 ~  ;   0  |;   0  &)
%                                         (  27 <=>;   0 =>;   0 <=)
%                                         (   0 <~>;   0 ~|;   0 ~&)
%            Number of predicates  :   14 (  14 propositional; 0-0 arity)
%            Number of functors    :    0 (   0 constant; --- arity)
%            Number of variables   :    0 (   0 singleton;   0 !;   0 ?)
%            Maximal term depth    :    0 (   0 average)
% SPC      : FOF_THM_PRP

% Comments : The number of distinct letters in U-N is N. The number of
%            occurrences of sentence letters in 2N. The number of clauses
%            goes up dramatically as N increases, but I don't think it
%            shows that the problems are dramatically more difficult as N
%            increases. Rather, it's that the awkward clause form
%            representation comes to the fore most dramatically with
%            embedded biconditionals. On all other measures of complexity,
%            one should say that the problems increase linearly in
%            difficulty. Urquhart says that the proof size of any resolution
%            system increases exponentially with increase in N.
%          : This problem can also be done in terms of graphs, as described
%            in [Pel86] Problem 74.
%          : tptp2X: -f tptp -s14 SYN007+1.g
%--------------------------------------------------------------------------
fof(prove_this,conjecture,
    ( p_1
  <=> ( p_2
    <=> ( p_3
      <=> ( p_4
        <=> ( p_5
          <=> ( p_6
            <=> ( p_7
              <=> ( p_8
                <=> ( p_9
                  <=> ( p_10
                    <=> ( p_11
                      <=> ( p_12
                        <=> ( p_13
                          <=> ( p_14
                            <=> ( p_1
                              <=> ( p_2
                                <=> ( p_3
                                  <=> ( p_4
                                    <=> ( p_5
                                      <=> ( p_6
                                        <=> ( p_7
                                          <=> ( p_8
                                            <=> ( p_9
                                              <=> ( p_10
                                                <=> ( p_11
                                                  <=> ( p_12
                                                    <=> ( p_13
                                                      <=> p_14 ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )).

%--------------------------------------------------------------------------
