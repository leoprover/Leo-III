%------------------------------------------------------------------------------
% File     : CSR146^1 : TPTP v6.2.0. Released v4.1.0.
% Domain   : Commonsense Reasoning
% Problem  : What is the relation between Chris and Corina during 2009?
% Version  : Especial.
% English  : During 2009 Corina is the wife of Chris. True holds at any time. 
%            What is the relation between Chris and Corina during 2009?

% Refs     : [Ben10] Benzmueller (2010), Email to Geoff Sutcliffe
% Source   : [Ben10]
% Names    : ex_5.tq_SUMO_handselected [Ben10]

% Status   : Theorem
% Rating   : 0.14 v6.1.0, 0.57 v6.0.0, 0.14 v5.5.0, 0.17 v5.4.0, 0.40 v5.3.0, 0.60 v5.2.0, 0.40 v5.1.0, 0.60 v5.0.0, 0.40 v4.1.0
% Syntax   : Number of formulae    :   15 (   4 unit;   9 type;   0 defn)
%            Number of atoms       :   57 (   1 equality;  12 variable)
%            Maximal formula depth :    9 (   4 average)
%            Number of connectives :   27 (   0   ~;   0   |;   1   &;  24   @)
%                                         (   1 <=>;   1  =>;   0  <=;   0 <~>)
%                                         (   0  ~|;   0  ~&;   0  !!;   0  ??)
%            Number of type conns  :   19 (  19   >;   0   *;   0   +;   0  <<)
%            Number of symbols     :   14 (   9   :)
%            Number of variables   :    9 (   2 sgn;   5   !;   2   ?;   2   ^)
%                                         (   9   :;   0  !>;   0  ?*)
%                                         (   0  @-;   0  @+)
% SPC      : TH0_THM_EQU

% Comments : This is a simple test problem for reasoning in/about SUMO.
%            Initally the problem has been hand generated in KIF syntax in
%            SigmaKEE and then automatically translated by Benzmueller's
%            KIF2TH0 translator into THF syntax.
%          : The translation has been applied in three modes: handselected,
%            SInE, and local. The local mode only translates the local
%            assumptions and the query. The SInE mode additionally translates
%            the SInE extract of the loaded knowledge base (usually SUMO). The
%            handselected mode contains a hand-selected relevant axioms.
%          : The examples are selected to illustrate the benefits of
%            higher-order reasoning in ontology reasoning.
%------------------------------------------------------------------------------
%----The extracted signature
thf(numbers,type,(
    num: $tType )).

thf(holdsDuring_THFTYPE_IiooI,type,(
    holdsDuring_THFTYPE_IiooI: $i > $o > $o )).

thf(lChris_THFTYPE_i,type,(
    lChris_THFTYPE_i: $i )).

thf(lCorina_THFTYPE_i,type,(
    lCorina_THFTYPE_i: $i )).

thf(lYearFn_THFTYPE_IiiI,type,(
    lYearFn_THFTYPE_IiiI: $i > $i )).

thf(n2009_THFTYPE_i,type,(
    n2009_THFTYPE_i: $i )).

thf(wife_THFTYPE_IiioI,type,(
    wife_THFTYPE_IiioI: $i > $i > $o )).

%----The handselected axioms from the knowledge base
thf(husband_THFTYPE_IiioI,type,(
    husband_THFTYPE_IiioI: $i > $i > $o )).

thf(inverse_THFTYPE_IIiioIIiioIoI,type,(
    inverse_THFTYPE_IIiioIIiioIoI: ( $i > $i > $o ) > ( $i > $i > $o ) > $o )).

thf(ax1,axiom,
    ( inverse_THFTYPE_IIiioIIiioIoI @ husband_THFTYPE_IiioI @ wife_THFTYPE_IiioI )).

thf(ax2,axiom,(
    ! [REL2: $i > $i > $o,REL1: $i > $i > $o] :
      ( ( inverse_THFTYPE_IIiioIIiioIoI @ REL1 @ REL2 )
     => ! [INST1: $i,INST2: $i] :
          ( ( REL1 @ INST1 @ INST2 )
        <=> ( REL2 @ INST2 @ INST1 ) ) ) )).

%----The translated axioms
thf(ax3,axiom,(
    ! [Z: $i] :
      ( holdsDuring_THFTYPE_IiooI @ Z @ $true ) )).

thf(ax4,axiom,(
    ? [X: $i] :
      ( ~ @ ( husband_THFTYPE_IiioI @ lChris_THFTYPE_i @ X ) ) )).

thf(ax5,axiom,
    ( holdsDuring_THFTYPE_IiooI @ ( lYearFn_THFTYPE_IiiI @ n2009_THFTYPE_i ) @ ( wife_THFTYPE_IiioI @ lCorina_THFTYPE_i @ lChris_THFTYPE_i ) )).

%----The translated conjectures
thf(con,conjecture,(
    ? [R: $i > $i > $o] :
      ( ( holdsDuring_THFTYPE_IiooI @ ( lYearFn_THFTYPE_IiiI @ n2009_THFTYPE_i ) @ ( R @ lChris_THFTYPE_i @ lCorina_THFTYPE_i ) )
      & ( ~
        @ ( R
          = ( ^ [X: $i,Y: $i] : $true ) ) ) ) )).

%------------------------------------------------------------------------------
