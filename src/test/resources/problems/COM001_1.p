%------------------------------------------------------------------------------
% File     : COM001_1 : TPTP v6.0.0. Released v5.0.0.
% Domain   : Computing Theory
% Problem  : A program correctness theorem
% Version  : Especial.
% English  : A simple computing state space, with four states - P3, P4,
%            P5, and P8 (the full version of this state space is in the
%            problem COM002-1). There is a branch at P3 such that the
%            following state is either P4 or P8. P8 has a loop back to P3,
%            while P4 leads to termination. The problem is to show that
%            there is a loop in the computation, passing through P3.

% Refs     : [RR+72] Reboh et al. (1972), Study of automatic theorem provin
%          : [WM76]  Wilson & Minker (1976), Resolution, Refinements, and S
% Source   : [SPRFN]
% Names    : 

% Status   : Theorem
% Rating   : 0.00 v2.0.0
% Syntax   : Number of formulae    :   32 (  21 unit;  21 type)
%            Number of atoms       :   51 (   0 equality)
%            Maximal formula depth :    6 (   3 average)
%            Number of connectives :    6 (   0   ~;   0   |;   2   &)
%                                         (   0 <=>;   4  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&)
%            Number of type conns  :   13 (   7   >;   6   *;   0   +;   0  <<)
%            Number of predicates  :   27 (  23 propositional; 0-2 arity)
%            Number of functors    :   11 (   8 constant; 0-2 arity)
%            Number of variables   :   11 (   0 sgn;  11   !;   0   ?)
%            Maximal term depth    :    3 (   1 average)
% SPC      : TF0_THM_NEQ_NAR

% Comments : I suspect this problem was originally by R.M. Burstall.
%------------------------------------------------------------------------------
tff(state_type,type,(
    state: $tType )).

tff(label_type,type,(
    label: $tType )).

tff(statement_type,type,(
    statement: $tType )).

tff(register_type,type,(
    register: $tType )).

tff(number_type,type,(
    number: $tType )).

tff(boolean_type,type,(
    boolean: $tType )).

tff(p3_type,type,(
    p3: state )).

tff(p4_type,type,(
    p4: state )).

tff(p5_type,type,(
    p5: state )).

tff(p8_type,type,(
    p8: state )).

tff(n_type,type,(
    n: number )).

tff(register_j_type,type,(
    register_j: register )).

tff(out_type,type,(
    out: label )).

tff(loop_type,type,(
    loop: label )).

tff(equal_function_type,type,(
    equal_function: ( register * number ) > boolean )).

tff(goto_type,type,(
    goto: label > statement )).

tff(ifthen_type,type,(
    ifthen: ( boolean * state ) > statement )).

tff(follows_type,type,(
    follows: ( state * state ) > $o )).

tff(succeeds_type,type,(
    succeeds: ( state * state ) > $o )).

tff(labels_type,type,(
    labels: ( label * state ) > $o )).

tff(has_type,type,(
    has: ( state * statement ) > $o )).

tff(direct_success,axiom,(
    ! [Start_state: state,Goal_state: state] :
      ( follows(Goal_state,Start_state)
     => succeeds(Goal_state,Start_state) ) )).

tff(transitivity_of_success,axiom,(
    ! [Start_state: state,Intermediate_state: state,Goal_state: state] :
      ( ( succeeds(Goal_state,Intermediate_state)
        & succeeds(Intermediate_state,Start_state) )
     => succeeds(Goal_state,Start_state) ) )).

tff(goto_success,axiom,(
    ! [Goal_state: state,Label: label,Start_state: state] :
      ( ( has(Start_state,goto(Label))
        & labels(Label,Goal_state) )
     => succeeds(Goal_state,Start_state) ) )).

tff(conditional_success,axiom,(
    ! [Goal_state: state,Condition: boolean,Start_state: state] :
      ( has(Start_state,ifthen(Condition,Goal_state))
     => succeeds(Goal_state,Start_state) ) )).

tff(label_state_3,hypothesis,(
    labels(loop,p3) )).

tff(state_3,hypothesis,(
    has(p3,ifthen(equal_function(register_j,n),p4)) )).

tff(state_4,hypothesis,(
    has(p4,goto(out)) )).

tff(transition_4_to_5,hypothesis,(
    follows(p5,p4) )).

tff(transition_3_to_8,hypothesis,(
    follows(p8,p3) )).

tff(state_8,hypothesis,(
    has(p8,goto(loop)) )).

tff(prove_there_is_a_loop_through_p3,conjecture,(
    succeeds(p3,p3) )).
%------------------------------------------------------------------------------

