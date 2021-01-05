%------------------------------------------------------------------------------
% File     : SYN000_2 : TPTP v7.3.0. Bugfixed v5.5.1.
% Domain   : Syntactic
% Problem  : Advanced TPTP TF0 syntax without arithmetic
% Version  : Biased.
% English  : 

% Refs     :
% Source   : [TPTP]
% Names    :

% Status   : Satisfiable
% Rating   : 0.67 v7.2.0, 1.00 v6.0.0
% Syntax   : Number of formulae    :   26 (  16 unit;   7 type)
%            Number of atoms       :   26 (   2 equality)
%            Maximal formula depth :    5 (   2 average)
%            Number of connectives :    6 (   2   ~;   0   |;   1   &)
%                                         (   1 <=>;   0  =>;   0  <=;   0 <~>)
%                                         (   1  ~|;   1  ~&)
%            Number of type conns  :    9 (   5   >;   4   *;   0   +;   0  <<)
%            Number of predicates  :   14 (  11 propositional; 0-2 arity)
%            Number of functors    :    6 (   4 constant; 0-2 arity)
%            Number of variables   :   18 (   0 sgn;  13   !;   1   ?)
%                                         (  14   :;   0  !>;   0  ?*)
%            Maximal term depth    :    2 (   1 average)
% SPC      : TF0_SAT_EQU_NAR

% Comments : 
% Bugfixes : v5.5.1 - Fixed let_binders.
%------------------------------------------------------------------------------
%----Quoted symbols
tff(distinct_object,axiom,(
    "An Apple" != "A \"Microsoft \\ escape\"" )).

%----Types for stuff below
tff(a_type,type,(
    a: $i )).

tff(b_type,type,(
    b: $i )).

tff(f_type,type,(
    f: $i > $i )).

tff(g_type,type,(
    g: ( $i * $i ) > $i )).

tff(h_type,type,(
    h: ( $i * $i * $i ) > $i )).

tff(p_type,type,(
    p: $i > $o )).

tff(q_type,type,(
    q: ( $i * $i ) > $o )).

%----Tuples
tff(tt_type,type,(
    tt: $tType )).

tff(at_type,type,(
    at: tt )).

tff(dt_type,type,(
    dt:
      [ $i,
        tt,
        $i ] )).

tff(pt_type,type,(
    pt:
      ( [ tt,
          $i ]
      > $o ) )).

tff(ft_type,type,(
    ft:
      ( ( $i
        * [ $i,
            tt,
            $i ] )
      > [ tt,
          $i ] ) )).

tff(tuple_1,axiom,(
    pt(ft(a,dt)) )).

%----Waiting for TPTP4X upgrades
%% tff(tuple_2,axiom,pt(ft(a,[a,at,a])) ).

%----Rare connectives
tff(never_used_connectives,axiom,(
    ! [X: $i] :
      ( ( p(X)
       ~| ~ q(X,a) )
     ~& p(X) ) )).

%----Roles
tff(role_definition,definition,(
    ! [X: $i] : f(a) = f(X) )).

tff(role_assumption,assumption,(
    p(a) )).

tff(role_lemma,lemma,(
    p(a) )).

tff(role_theorem,theorem,(
    p(a) )).

tff(role_unknown,unknown,(
    p(a) )).

%----Selective include directive
include('Axioms/SYN000_0.ax',[ia1,ia3]).

%----Source
tff(source_unknown,axiom,(
    ! [X: $i] : p(X) ),
    unknown).

tff(source,axiom,(
    ! [X: $i] : p(X) ),
    file('SYN000-1.p')).

tff(source_name,axiom,(
    ! [X: $i] : p(X) ),
    file('SYN000-1.p',source_unknown)).

tff(source_copy,axiom,(
    ! [X: $i] : p(X) ),
    source_unknown).

tff(source_introduced_assumption,axiom,(
    ! [X: $i] : p(X) ),
    introduced(assumption,[from,the,world])).

tff(source_inference,axiom,(
    p(a) ),
    inference(magic,[status(thm),assumptions([source_introduced_assumption])],[theory(equality),source_unknown])).

tff(source_inference_with_bind,axiom,(
    p(a) ),
    inference(magic,[status(thm)],[theory(equality),source_unknown:[bind(X,$fot(a))]])).

%----Useful info
tff(useful_info,axiom,(
    ! [X: $i] : p(X) ),
    unknown,
    [simple,prolog(like,Data,[nested,12.2]),AVariable,12.2,"A distinct object",$tff(p(X) | ~ q(X,a)),data(name):[colon,list,2],[simple,prolog(like,Data,[nested,12.2]),AVariable,12.2]]).

%------------------------------------------------------------------------------
