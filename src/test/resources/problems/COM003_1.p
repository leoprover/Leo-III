%--------------------------------------------------------------------------
% File     : COM003_1 : TPTP v6.0.0. Released v5.0.0.
% Domain   : Computing Theory
% Problem  : The halting problem is undecidable
% Version  : [Bur87b] axioms.
% English  :

% Refs     : [Bur87a] Burkholder (1987), The Halting Problem
%          : [Bur87b] Burkholder (1987), A 76th Automated Theorem Proving Pr
% Source   : [TPTP]
% Names    : 

% Status   : Theorem
% Rating   : 0.00 v6.0.0, 0.33 v5.5.0, 0.00 v5.3.0, 0.33 v5.2.0, 0.67 v5.1.0, 1.00 v5.0.0
% Syntax   : Number of formulae    :   17 (   7 unit;  12 type)
%            Number of atoms       :   57 (   0 equality)
%            Maximal formula depth :   10 (   4 average)
%            Number of connectives :   35 (   7   ~;   0   |;  14   &)
%                                         (   0 <=>;  14  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&)
%            Number of type conns  :   12 (   6   >;   6   *;   0   +;   0  <<)
%            Number of predicates  :   18 (  14 propositional; 0-3 arity)
%            Number of functors    :    4 (   2 constant; 0-1 arity)
%            Number of variables   :   22 (   0 sgn;  15   !;   7   ?)
%            Maximal term depth    :    2 (   1 average)
% SPC      : TF0_THM_NEQ_NAR

% Comments :
%--------------------------------------------------------------------------
tff(program_type,type,(
    program: $tType )).

tff(algorithm_type,type,(
    algorithm: $tType )).

tff(input_type,type,(
    input: $tType )).

tff(output_type,type,(
    output: $tType )).

tff(bad_type,type,(
    bad: output )).

tff(good_type,type,(
    good: output )).

tff(decides_type,type,(
    decides: ( algorithm * program * input ) > $o )).

tff(halts2_type,type,(
    halts2: ( program * input ) > $o )).

tff(halts3_type,type,(
    halts3: ( program * program * input ) > $o )).

tff(outputs_type,type,(
    outputs: ( program * output ) > $o )).

tff(algorithm_of_type,type,(
    algorithm_of: program > algorithm )).

tff(as_input_type,type,(
    as_input: program > input )).

tff(p1,axiom,
    ( ? [X: algorithm] :
      ! [Y: program,Z: input] : decides(X,Y,Z)
   => ? [W: program] :
      ! [Y: program,Z: input] : decides(algorithm_of(W),Y,Z) )).

tff(p2,axiom,(
    ! [W: program,Y: program,Z: input] :
      ( decides(algorithm_of(W),Y,Z)
     => ! [Y: program,Z: input] :
          ( ( halts2(Y,Z)
           => ( halts3(W,Y,Z)
              & outputs(W,good) ) )
          & ( ~ halts2(Y,Z)
           => ( halts3(W,Y,Z)
              & outputs(W,bad) ) ) ) ) )).

tff(p3,axiom,
    ( ? [W: program] :
      ! [Y: program] :
        ( ( halts2(Y,as_input(Y))
         => ( halts3(W,Y,as_input(Y))
            & outputs(W,good) ) )
        & ( ~ halts2(Y,as_input(Y))
         => ( halts3(W,Y,as_input(Y))
            & outputs(W,bad) ) ) )
   => ? [V: program] :
      ! [Y: program] :
        ( ( halts2(Y,as_input(Y))
         => ( halts2(V,as_input(Y))
            & outputs(V,good) ) )
        & ( ~ halts2(Y,as_input(Y))
         => ( halts2(V,as_input(Y))
            & outputs(V,bad) ) ) ) )).

tff(p4,axiom,
    ( ? [V: program] :
      ! [Y: program] :
        ( ( halts2(Y,as_input(Y))
         => ( halts2(V,as_input(Y))
            & outputs(V,good) ) )
        & ( ~ halts2(Y,as_input(Y))
         => ( halts2(V,as_input(Y))
            & outputs(V,bad) ) ) )
   => ? [U: program] :
      ! [Y: program] :
        ( ( halts2(Y,as_input(Y))
         => ~ halts2(U,as_input(Y)) )
        & ( ~ halts2(Y,as_input(Y))
         => ( halts2(U,as_input(Y))
            & outputs(U,bad) ) ) ) )).

tff(prove_this,conjecture,(
    ~ ? [X1: algorithm] :
      ! [Y1: program,Z1: input] : decides(X1,Y1,Z1) )).

%--------------------------------------------------------------------------
