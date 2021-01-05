%------------------------------------------------------------------------------
% File     : SYN000_3 : TPTP v7.3.0. Released v7.1.0.
% Domain   : Syntactic
% Problem  : TPTP TF1 syntax
% Version  : Biased.
% English  : 

% Refs     :
% Source   : [TPTP]
% Names    :

% Status   : Satisfiable
% Rating   : 1.00 v7.1.0
% Syntax   : Number of formulae    :   14 (   2 unit;  10 type)
%            Number of atoms       :    6 (   5 equality)
%            Maximal formula depth :    9 (   4 average)
%            Number of connectives :    3 (   1   ~;   0   |;   0   &)
%                                         (   0 <=>;   2  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&)
%            Number of type conns  :   12 (   7   >;   5   *;   0   +;   0  <<)
%            Number of predicates  :   13 (   9 propositional; 0-2 arity)
%            Number of functors    :    6 (   2 constant; 0-5 arity)
%            Number of variables   :   22 (   1 sgn;  17   !;   0   ?)
%                                         (  22   :;   5  !>;   0  ?*)
%            Maximal term depth    :    3 (   1 average)
% SPC      : TF0_SAT_EQU_NAR

% Comments : 
%------------------------------------------------------------------------------
tff(beverage_type,type,(
    beverage: $tType )).

tff(syrup_type,type,(
    syrup: $tType )).

%----Type constructor
tff(cup_of_type,type,(
    cup_of: $tType > $tType )).

tff(full_cup_type,type,(
    full_cup: beverage > cup_of(beverage) )).

tff(coffee_type,type,(
    coffee: beverage )).

tff(help_stay_awake_type,type,(
    help_stay_awake: cup_of(beverage) > $o )).

%----Polymorphic symbol
tff(mixture_type,type,(
    mixture:
      !>[BeverageOrSyrup: $tType] :
        ( ( BeverageOrSyrup * syrup ) > BeverageOrSyrup ) )).

%----Use of polymorphic symbol
tff(mixture_of_coffee_help_stay_awake,axiom,(
    ! [S: syrup] : help_stay_awake(full_cup(mixture(beverage,coffee,S))) )).

%----Type constructor
tff(map,type,(
    map: ( $tType * $tType ) > $tType )).

%----Polymorphic symbols
tff(lookup,type,(
    lookup:
      !>[A: $tType,B: $tType] :
        ( ( map(A,B) * A ) > B ) )).

tff(update,type,(
    update:
      !>[A: $tType,B: $tType] :
        ( ( map(A,B) * A * B ) > map(A,B) ) )).

%----Use of polymorphic symbols
tff(lookup_update_same,axiom,(
    ! [A: $tType,B: $tType,M: map(A,B),K: A,V: B] : lookup(A,B,update(A,B,M,K,V),K) = V )).

tff(lookup_update_diff,axiom,(
    ! [A: $tType,B: $tType,M: map(A,B),V: B,K: A,L: A] :
      ( K != L
     => lookup(A,B,update(A,B,M,K,V),L) = lookup(A,B,M,L) ) )).

tff(map_ext,axiom,(
    ! [A: $tType,B: $tType,M: map(A,B),N: map(A,B)] :
      ( ! [K: A] : lookup(A,B,M,K) = lookup(A,B,N,K)
     => M = N ) )).

%------------------------------------------------------------------------------
