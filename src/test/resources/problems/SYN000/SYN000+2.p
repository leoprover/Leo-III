%------------------------------------------------------------------------------
% File     : SYN000+2 : TPTP v7.3.0. Bugfixed v7.1.0.
% Domain   : Syntactic
% Problem  : Advanced TPTP FOF syntax
% Version  : Biased.
% English  : 

% Refs     :
% Source   : [TPTP]
% Names    :

% Status   : Satisfiable
% Rating   : 0.33 v7.3.0, 0.67 v7.1.0
% Syntax   : Number of formulae    :   17 (  16 unit)
%            Number of atoms       :   19 (   2 equality)
%            Maximal formula depth :    5 (   2 average)
%            Number of connectives :    4 (   2   ~;   0   |;   0   &)
%                                         (   0 <=>;   0  =>;   0  <=;   0 <~>)
%                                         (   1  ~|;   1  ~&)
%            Number of predicates  :    7 (   2 propositional; 0-3 arity)
%            Number of functors    :   10 (   8 constant; 0-3 arity)
%            Number of variables   :    8 (   0 sgn;   8   !;   0   ?)
%            Maximal term depth    :    2 (   1 average)
% SPC      : FOF_SAT_RFO_SEQ

% Comments :
% Bugfixes : v4.0.1 - Added more numbers, particularly rationals.
%          : v4.1.1 - Removed rationals with negative denominators.
%          : v7.1.0 - Removed numbers
%------------------------------------------------------------------------------
%----Quoted symbols
fof(distinct_object,axiom,(
    "An Apple" != "A \"Microsoft \\ escape\"" )).

%----Connectives - seen |, &, =>, ~ already
fof(never_used_connectives,axiom,(
    ! [X] :
      ( ( p(X)
       ~| ~ q(X,a) )
     ~& p(X) ) )).

%----Roles
fof(role_definition,definition,(
    ! [X] : f(d) = f(X) )).

fof(role_assumption,assumption,(
    p(a) )).

fof(role_lemma,lemma,(
    p(l) )).

fof(role_theorem,theorem,(
    p(t) )).

fof(role_unknown,unknown,(
    p(u) )).

%----Selective include directive
include('Axioms/SYN000+0.ax',[ia1,ia3]).

%----Source
fof(source_unknown,axiom,(
    ! [X] : p(X) ),
    unknown).

fof(source,axiom,(
    ! [X] : p(X) ),
    file('SYN000-1.p')).

fof(source_name,axiom,(
    ! [X] : p(X) ),
    file('SYN000-1.p',source_unknown)).

fof(source_copy,axiom,(
    ! [X] : p(X) ),
    source_unknown).

fof(source_introduced_assumption,axiom,(
    ! [X] : p(X) ),
    introduced(assumption,[from,the,world])).

fof(source_inference,axiom,(
    p(a) ),
    inference(magic,
        [status(thm),assumptions([source_introduced_assumption])],
        [theory(equality),source_unknown])).

fof(source_inference_with_bind,axiom,(
    p(a) ),
    inference(magic,
        [status(thm)],
        [theory(equality),source_unknown:[bind(X,$fot(a))]])).

%----Useful info
fof(useful_info,axiom,(
    ! [X] : p(X) ),
    unknown,
    [simple,
     prolog(like,Data,[nested,12.2]),
     AVariable,
     12.2,
     "A distinct object",
     $fof(p(X) | ~ q(X,a) | r(X,f(Y),g(X,f(Y),Z)) | ~ s(f(f(f(b))))),
     data(name):[colon,list,2],
     [simple,prolog(like,Data,[nested,12.2]),AVariable,12.2]
    ]).

%------------------------------------------------------------------------------
