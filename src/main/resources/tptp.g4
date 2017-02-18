/*  This is a grammar file for TPTP language Version 6.4.0.7
 *  Use ANTLR on this file to obtain the parser.
 *  The BNF specification of the TPTP language was provided by Geoff Sutcliffe.
 *  The grammar file is created by Alexander Steen and Tobias Gleißner.
 *  Date: Dec 2016
 *  Last updated: Feb 2017
 */
grammar tptp;

@header {
package leo.modules.parsers.antlr;
} 

// %----v6.4.0.0 (TPTP version.internal development number)
// %----v6.4.0.1 Noted that <number>s may not be used in CNF or FOF.
// %----v6.4.0.2 Added tcf
// %----v6.4.0.3 Moved ^ into <th0_quantifier>
// %----v6.4.0.4 Changed <thf_top_level_type> and <thf_unitary_type> to be more
// %----         precise. <thf_binary_type> removed from <thf_binary_formula>.
// %----v6.4.0.5 Specified that the quantification in a $let must be universal.
// %----         Added ()s around the <thf_let_plain_defn> in a
// %----         <thf_let_quantified_defn> (to avoid binding ambiguity).
// %----v6.4.0.6 Changed $ite to be an instance of a <thf_unitary_formula>, so
// %----         it uses application rather than FOF style $ite(...).
// %----v6.4.0.7 Added := to the list of binary connectives, to accomodate logic
// %----         specifications. This changed $let to use <thf_unitary_formula>
// %----         for it's first arguments; the original rule hierarchy for $let
// %----         has been changed to :== semantic rules. Semantic rules for logic
// %----         specifications have been added.
// %----v6.4.0.8 Added := to the TFF binary connectives, to accomodate logic
// %----         specifications.
// %----v6.4.0.9 Split off TFX language, for FOOL and modal logic.
// %----v6.4.0.10 Renamed things to separate THF from TFF from FOF from general
// %----v6.4.0.11 Added <tff_subtype> back
// %----v6.4.0.12 Added <tff_tuple_term>. Allow <thf_tuple>s to use either {}s or
// %----          []s.


// Tokens, Lexer rules

// %----Character classes
// <percentage_sign>      ::: [%]
// <double_quote>         ::: ["]
// <do_char>              ::: ([\40-\41\43-\133\135-\176]|[\\]["\\])
// <single_quote>         ::: [']
// %---Space and visible characters upto ~, except ' and \
// <sq_char>              ::: ([\40-\46\50-\133\135-\176]|[\\]['\\])
// <sign>                 ::: [+-]
// <dot>                  ::: [.]
// <exponent>             ::: [Ee]
// <slash>                ::: [/]
// <zero_numeric>         ::: [0]
// <non_zero_numeric>     ::: [1-9]
// <numeric>              ::: [0-9]
// <lower_alpha>          ::: [a-z]
// <upper_alpha>          ::: [A-Z]
// <alpha_numeric>        ::: (<lower_alpha>|<upper_alpha>|<numeric>|[_])
// <dollar>               ::: [$]

// Most of the character classes are modelled as directly literals in this grammar.
// Here we define some handy ones which are collections of individual literals

fragment Do_char : [\u0020-\u0021\u0023-\u005B\u005D-\u007E] | '\\'["\\];
fragment Sq_char : [\u0020-\u0026\u0028-\u005B\u005D-\u007E] | '\\'['\\];
fragment Sign : [+-];
fragment Exponent : [Ee];
fragment Non_zero_numeric : [1-9];
fragment Numeric : [0-9];
fragment Lower_alpha : [a-z];
fragment Upper_alpha : [A-Z];
fragment Alpha_numeric : Lower_alpha | Upper_alpha | Numeric | '_';

Or: '|';
And: '&';
Iff : '<=>';
Impl : '=>';
If: '<=';
Niff: '<~>';
Nor: '~|';
Nand: '~&';
Not: '~';
ForallComb: '!!';
TyForall: '!>';
Infix_inequality : '!=';
Infix_equality : '=';
Forall: '!';
ExistsComb: '??';
TyExists: '?*';
Exists: '?';
Lambda: '^';
ChoiceComb: '@@+';
Choice: '@+';
DescriptionComb: '@@-';
Description: '@-';
EqComb: '@=';
App: '@';
Assignment: ':=';

// %----Numbers. Signs are made part of the same token here.
// <real>                 ::- (<signed_real>|<unsigned_real>)
// <signed_real>          ::- <sign><unsigned_real>
// <unsigned_real>        ::- (<decimal_fraction>|<decimal_exponent>)
// <rational>             ::- (<signed_rational>|<unsigned_rational>)
// <signed_rational>      ::- <sign><unsigned_rational>
// <unsigned_rational>    ::- <decimal><slash><positive_decimal>
// <integer>              ::- (<signed_integer>|<unsigned_integer>)
// <signed_integer>       ::- <sign><unsigned_integer>
// <unsigned_integer>     ::- <decimal>
// <decimal>              ::- (<zero_numeric>|<positive_decimal>)
// <positive_decimal>     ::- <non_zero_numeric><numeric>*
// <decimal_exponent>     ::- (<decimal>|<decimal_fraction>)<exponent><exp_integer>
// <decimal_fraction>     ::- <decimal><dot_decimal>
// <dot_decimal>          ::- <dot><numeric><numeric>*
// <exp_integer>          ::- (<signed_exp_integer>|<unsigned_exp_integer>)
// <signed_exp_integer>   ::- <sign><unsigned_exp_integer>
// <unsigned_exp_integer> ::- <numeric><numeric>*

Real : Signed_real | Unsigned_real;
Signed_real : Sign Unsigned_real;
Unsigned_real : Decimal_fraction|Decimal_exponent;
Rational: Signed_rational | Unsigned_rational;
Signed_rational: Sign Unsigned_rational;
Unsigned_rational: Decimal '/' Positive_decimal;
Integer : Signed_integer | Unsigned_integer;
Signed_integer: Sign Unsigned_integer;
Unsigned_integer: Decimal;
Decimal : '0' | Positive_decimal;
Positive_decimal : Non_zero_numeric Numeric*;
Decimal_exponent : (Decimal|Decimal_fraction) Exponent Exp_integer;
Decimal_fraction : Decimal Dot_decimal;
Dot_decimal : '.' Numeric Numeric*;
Exp_integer : Signed_exp_integer|Unsigned_exp_integer;
Signed_exp_integer : Sign Unsigned_exp_integer;
Unsigned_exp_integer : Numeric Numeric*;


// <dollar_word>          ::- <dollar><lower_word>
// <dollar_dollar_word>   ::- <dollar><dollar><lower_word>
// <upper_word>           ::- <upper_alpha><alpha_numeric>*
// <lower_word>           ::- <lower_alpha><alpha_numeric>*
Dollar_word : '$' Lower_word;
Dollar_dollar_word : '$$' Lower_word;
Upper_word : Upper_alpha Alpha_numeric*;
Lower_word : Lower_alpha Alpha_numeric*;

// %----<single_quoted>s contain visible characters. \ is the escape character for
// %----' and \, i.e., \' is not the end of the <single_quoted>.
// %----The token does not include the outer quotes, e.g., 'cat' and cat are the
// %----same. See <atomic_word> for information about stripping the quotes.
// <single_quoted>        ::- <single_quote><sq_char><sq_char>*<single_quote>
// %----<distinct_object>s contain visible characters. \ is the escape character
// %----for " and \, i.e., \" is not the end of the <distinct_object>.
// %----<distinct_object>s are different from (but may be equal to) other tokens,
// %----e.g., "cat" is different from 'cat' and cat. Distinct objects are always
// %----interpreted as themselves, so if they are different they are unequal,
// %----e.g., "Apple" != "Microsoft" is implicit.
// <distinct_object>      ::- <double_quote><do_char>*<double_quote>

Single_quoted : '\'' Sq_char+ '\'';
Distinct_object : '"' Do_char+ '"';

// Comments

WS : [ \r\t\n]+ -> skip ;
Line_comment : '%' ~[\r\n]* -> skip;
Block_comment : '/*' .*? '*/' -> skip;


// %----Files. Empty file is OK.
// <TPTP_file>            ::= <TPTP_input>*
// <TPTP_input>           ::= <annotated_formula> | <include>
tptp_file           : tptp_input* EOF;
tptp_input          : annotated_formula | include;

// %----Include directives
// <include>              ::= include(<file_name><formula_selection>).
// <formula_selection>    ::= ,[<name_list>] | <null>
// <name_list>            ::= <name> | <name>,<name_list>
include : 'include(' file_name formula_selection? ').';
formula_selection: ',' '['  name (',' name)* ']';

// %----General purpose
// <name>                 ::= <atomic_word> | <integer>
// %----Integer names are expected to be unsigned
// <atomic_word>          ::= <lower_word> | <single_quoted>
// %----<single_quoted> tokens do not include their outer quotes, therefore the
// %----<lower_word> <atomic_word> cat and the <single_quoted> <atomic_word> 'cat'
// %----are the same. Quotes must be removed from a <single_quoted> <atomic_word>
// %----if doing so produces a <lower_word> <atomic_word>. Note that <numbers>s
// %----and <variable>s are not <lower_word>s, so '123' and 123, and 'X' and X,
// %----are different.
// <atomic_defined_word>  ::= <dollar_word>
// <atomic_system_word>   ::= <dollar_dollar_word>
// <number>               ::= <integer> | <rational> | <real>
// %----Numbers are always interpreted as themselves, and are thus implicitly
// %----distinct if they have different values, e.g., 1 != 2 is an implicit axiom.
// %----All numbers are base 10 at the moment.
// <file_name>            ::= <single_quoted>
name : atomic_word | Integer;
atomic_word : Lower_word | Single_quoted;
atomic_defined_word : Dollar_word;
atomic_system_word : Dollar_dollar_word;
number : Integer | Rational | Real;
file_name : Single_quoted;

// %----Formula records
// <annotated_formula>    ::= <thf_annotated> | <tff_annotated> | <tcf_annotated> |
//                            <fof_annotated> | <cnf_annotated> | <tpi_annotated>
// %----Future languages may include ...  english | efof | tfof | mathml | ...
// <tpi_annotated>        ::= tpi(<name>,<formula_role>,<tpi_formula><annotations>).
// <tpi_formula>          ::= <fof_formula>
// <thf_annotated>        ::= thf(<name>,<formula_role>,<thf_formula>
//                            <annotations>).
// <tff_annotated>        ::= tff(<name>,<formula_role>,<tff_formula>
//                            <annotations>).
// <tcf_annotated>        ::= tcf(<name>,<formula_role>,<tcf_formula>
//                            <annotations>).
// <fof_annotated>        ::= fof(<name>,<formula_role>,<fof_formula>
//                            <annotations>).
// <cnf_annotated>        ::= cnf(<name>,<formula_role>,<cnf_formula>
//                            <annotations>).
// <annotations>          ::= ,<source><optional_info> | <null>
// <formula_role>         ::= <lower_word>
annotated_formula : thf_annotated | tff_annotated | fof_annotated | cnf_annotated;
                // tcf_annotated | tpi_annotated;
thf_annotated  : 'thf(' name ',' formula_role ',' thf_formula annotations? ').';
tff_annotated  : 'tff(' name ',' formula_role ',' tff_formula annotations? ').';
fof_annotated  : 'fof(' name ',' formula_role ',' fof_formula annotations? ').';
cnf_annotated  : 'cnf(' name ',' formula_role ',' cnf_formula annotations? ').';

annotations : ',' source optional_info?;
formula_role : Lower_word;

// %----Formula sources
//<source>               ::= <general_term>
// %----Useful info fields
// <optional_info>        ::= ,<useful_info> | <null>
// <useful_info>          ::= <general_list>
source : general_term;
optional_info : ',' general_list;

// %----Non-logical data
// <general_term>         ::= <general_data> | <general_data>:<general_term> |
//                            <general_list>
// <general_data>         ::= <atomic_word> | <general_function> |
//                            <variable> | <number> | <distinct_object> |
//                            <formula_data>
// <general_function>     ::= <atomic_word>(<general_terms>)
// <formula_data>         ::= $thf(<thf_formula>) | $tff(<tff_formula>) |
//                            $fof(<fof_formula>) | $cnf(<cnf_formula>) |
//                            $fot(<term>)
// <general_list>         ::= [] | [<general_terms>]
// <general_terms>        ::= <general_term> | <general_term>,<general_terms>
general_term: general_data | general_data ':' general_term | general_list;
general_data : atomic_word | general_function | variable | number | Distinct_object | formula_data;
general_function: atomic_word '(' general_term (',' general_term)* ')';
formula_data : '$thf(' thf_formula ')' | '$tff(' tff_formula ')' | '$fof(' fof_formula ')'
             | '$fot(' fof_term ')' | '$cnf(' cnf_formula ')';
general_list : '[]' | '[' general_term (',' general_term)* ']'; 


////////////////////////////////////////////////////////////////////////////////
//// THF Formulae
////////////////////////////////////////////////////////////////////////////////
// <thf_formula>          ::= <thf_logic_formula> | <thf_sequent>
// <thf_logic_formula>    ::= <thf_binary_formula> | <thf_unitary_formula> |
//                            <thf_type_formula> | <thf_subtype>  
// <thf_binary_formula>   ::= <thf_binary_pair> | <thf_binary_tuple> | <thf_binary_type> // Fixed
thf_formula : thf_logic_formula | thf_sequent;
thf_logic_formula : thf_binary_formula | thf_unitary_formula | thf_type_formula | thf_subtype;
thf_binary_formula : thf_binary_pair | thf_binary_tuple | thf_binary_type;

// %----Only some binary connectives can be written without ()s.
// %----There's no precedence among binary connectives
// <thf_binary_pair>      ::= <thf_unitary_formula> <thf_pair_connective>
//                            <thf_unitary_formula>
// <thf_binary_tuple>     ::= <thf_or_formula> | <thf_and_formula> |
//                            <thf_apply_formula>
// <thf_or_formula>       ::= <thf_unitary_formula> <vline> <thf_unitary_formula> |
//                            <thf_or_formula> <vline> <thf_unitary_formula>
// <thf_and_formula>      ::= <thf_unitary_formula> & <thf_unitary_formula> |
//                            <thf_and_formula> & <thf_unitary_formula>
// <thf_apply_formula>    ::= <thf_unitary_formula> @ <thf_unitary_formula> |
//                            <thf_apply_formula> @ <thf_unitary_formula>
thf_binary_pair : thf_unitary_formula thf_pair_connective thf_unitary_formula;
thf_binary_tuple : thf_or_formula | thf_and_formula | thf_apply_formula;
thf_or_formula : thf_unitary_formula Or thf_unitary_formula
               | thf_or_formula Or thf_unitary_formula;
thf_and_formula : thf_unitary_formula And thf_unitary_formula
               | thf_and_formula And thf_unitary_formula;
thf_apply_formula : thf_unitary_formula App thf_unitary_formula
               | thf_apply_formula App thf_unitary_formula;

// <thf_unitary_formula>  ::= <thf_quantified_formula> | <thf_unary_formula> |
//                            <thf_atom> | <thf_conditional> | <thf_let> | 
//                            <thf_tuple> | (<thf_logic_formula>)
thf_unitary_formula : thf_quantified_formula | thf_unary_formula | thf_atom | thf_conditional
                    | thf_let | thf_tuple | '(' thf_logic_formula ')';
// <thf_quantified_formula> ::= <thf_quantification> <thf_unitary_formula>
// <thf_quantification>   ::= <thf_quantifier> [<thf_variable_list>] :
// <thf_variable_list>    ::= <thf_variable> | <thf_variable>,<thf_variable_list>
// <thf_variable>         ::= <thf_typed_variable> | <variable>
// <thf_typed_variable>   ::= <variable> : <thf_top_level_type>
thf_quantified_formula : thf_quantification thf_unitary_formula;
thf_quantification : thf_quantifier '[' thf_variable (',' thf_variable)* ']' ':';
thf_variable : variable (':' thf_top_level_type)?;

// %----Unary connectives bind more tightly than binary. The negated formula
// %----must be ()ed because a ~ is also a term.
// <thf_unary_formula>    ::= <thf_unary_connective> (<thf_logic_formula>)
// <thf_atom>             ::= <thf_function> | <variable> | <defined_term> | <thf_conn_term>
// <thf_function>         ::= <atom> | <functor>(<thf_arguments>) |
//                              <defined_functor>(<thf_arguments>) |
//                              <system_functor>(<thf_arguments>)
thf_unary_formula : thf_unary_connective '(' thf_logic_formula ')';
thf_atom : thf_function | variable | defined_term | thf_conn_term;

thf_function: thf_plain_term | thf_defined_term | thf_system_term;
// Splitted rules of <thf_function> to avoid using <atom> here: We use conditional arguments, i.e.
// the atoms are included (= thf_arguments is empty).
thf_plain_term : functor  ('(' thf_arguments ')')?;
thf_defined_term : defined_functor ('(' thf_arguments ')')?;
thf_system_term : system_functor ('(' thf_arguments ')')?;

// <thf_conn_term>        ::= <thf_pair_connective> | <assoc_connective> |
//                            <thf_unary_connective>
thf_conn_term : thf_pair_connective | assoc_connective | thf_unary_connective;

// <thf_conditional>      ::= $ite(<thf_logic_formula>,<thf_logic_formula>,
//                             <thf_logic_formula>)
thf_conditional: '$ite(' thf_logic_formula ',' thf_logic_formula ',' thf_logic_formula ')';                      
                            
// %----The LHS of a term or formula binding must be a non-variable term that
// %----is flat with pairwise distinct variable arguments, and the variables in 
// %----the LHS must be exactly those bound in the universally quantified variable
// %----list, in the same order. Let definitions are not recursive: a non-variable
// %----symbol introduced in the LHS of a let definition cannot occur in the RHS.
// %----If a symbol with the same signature as the one in the LHS of the binding
// %----is declared above the let expression (at the top level or in an 
// %----encompassing let) then it can be used in the RHS of the binding, but it is
// %----not accessible in the term or formula of the let expression. Let 
// %----expressions can be eliminated by a simple definition expansion.
// <thf_let>              ::= $let(<thf_unitary_formula>,<thf_formula>)
thf_let : '$let(' thf_unitary_formula ',' thf_formula ')';

// %----Arguments recurse back up to formulae (this is the THF world here)
// <thf_arguments>        ::= <thf_formula_list>
thf_arguments : thf_formula_list; 

// %----A <thf_type_formula> is an assertion that the formula is in this type.
// <thf_type_formula>     ::= <thf_typeable_formula> : <thf_top_level_type>
// <thf_typeable_formula> ::= <thf_atom> | (<thf_logic_formula>)
// <thf_subtype>          ::= <thf_atom> <subtype_sign> <thf_atom>
thf_type_formula : thf_typeable_formula ':' thf_top_level_type;
thf_typeable_formula: thf_atom | '(' thf_logic_formula ')';
thf_subtype : thf_atom '<<' thf_atom;

// %----<thf_top_level_type> appears after ":", where a type is being specified
// %----for a term or variable. <thf_unitary_type> includes <thf_unitary_formula>,
// %----so the syntax allows just about any lambda expression with "enough"
// %----parentheses to serve as a type. The expected use of this flexibility is
// %----parametric polymorphism in types, expressed with lambda abstraction.
// %----Mapping is right-associative: o > o > o means o > (o > o).
// %----Xproduct is left-associative: o * o * o means (o * o) * o.
// %----Union is left-associative: o + o + o means (o + o) + o.
// <thf_top_level_type>   ::= <thf_unitary_type> | <thf_mapping_type>
// <thf_unitary_type>     ::= <thf_unitary_formula> | (<thf_binary_type>)
// <thf_binary_type>      ::= <thf_mapping_type> | <thf_xprod_type> |
//                            <thf_union_type>
// <thf_mapping_type>     ::= <thf_unitary_type> <arrow> <thf_unitary_type> |
//                            <thf_unitary_type> <arrow> <thf_mapping_type>
// <thf_xprod_type>       ::= <thf_unitary_type> <star> <thf_unitary_type> |
//                            <thf_xprod_type> <star> <thf_unitary_type>
// <thf_union_type>       ::= <thf_unitary_type> <plus> <thf_unitary_type> |
//                            <thf_union_type> <plus> <thf_unitary_type>
thf_top_level_type : thf_unitary_type | thf_mapping_type;
thf_unitary_type : thf_unitary_formula;
thf_binary_type : thf_mapping_type | thf_xprod_type | thf_union_type;
thf_mapping_type : thf_unitary_type '>' thf_unitary_type
                 | thf_unitary_type '>' thf_mapping_type;
thf_xprod_type : thf_unitary_type '*' thf_unitary_type
                 | thf_xprod_type '*' thf_unitary_type;
thf_union_type : thf_unitary_type '+' thf_unitary_type
                 | thf_union_type '+' thf_unitary_type;
                 
// %----Sequents using the Gentzen arrow
// <thf_sequent>          ::= <thf_tuple> <gentzen_arrow> <thf_tuple> |
//                            (<thf_sequent>)
// 
// <thf_tuple>            ::= [] | [<thf_formula_list>]
// <thf_formula_list>     ::= <thf_logic_formula> |
//                            <thf_logic_formula>,<thf_formula_list>
thf_sequent : thf_tuple '-->' thf_tuple | '(' thf_sequent ')';
thf_tuple : '[]' | '[' thf_formula_list ']' | '{}' | '{' thf_formula_list '}';
thf_formula_list : thf_logic_formula (',' thf_logic_formula)*;



////////////////////////////////////////////////////////////////////////////////
//// TFF Formulae
////////////////////////////////////////////////////////////////////////////////

// %----TFF formulae.
// <tff_formula>          ::= <tff_logic_formula> | <tff_typed_atom> | 
//                            <tff_sequent>
// <tff_logic_formula>    ::= <tff_binary_formula> | <tff_unitary_formula>
// <tff_binary_formula>   ::= <tff_binary_nonassoc> | <tff_binary_assoc>
// <tff_binary_nonassoc>  ::= <tff_unitary_formula> <binary_connective>
//                            <tff_unitary_formula>
// <tff_binary_assoc>     ::= <tff_or_formula> | <tff_and_formula>
// <tff_or_formula>       ::= <tff_unitary_formula> <vline> <tff_unitary_formula> |
//                            <tff_or_formula> <vline> <tff_unitary_formula>
// <tff_and_formula>      ::= <tff_unitary_formula> & <tff_unitary_formula> |
//                            <tff_and_formula> & <tff_unitary_formula>
// <tff_unitary_formula>  ::= <tff_quantified_formula> | <tff_unary_formula> |
//                            <tff_atomic_formula> | <tff_conditional> | <tff_let> |
//                            (<tff_logic_formula>) 
tff_formula : tff_logic_formula | tff_typed_atom | tff_sequent;
tff_logic_formula: tff_binary_formula | tff_unitary_formula;
tff_binary_formula: tff_binary_nonassoc | tff_binary_assoc;
tff_binary_nonassoc: tff_unitary_formula binary_connective tff_unitary_formula;
tff_binary_assoc: tff_or_formula | tff_and_formula;
tff_or_formula : tff_unitary_formula Or tff_unitary_formula
               | tff_or_formula Or tff_unitary_formula;
tff_and_formula : tff_unitary_formula And tff_unitary_formula
               | tff_and_formula And tff_unitary_formula;
tff_unitary_formula : tff_quantified_formula | tff_unary_formula | tff_atomic_formula
                    | tff_conditional | tff_let | '(' tff_logic_formula ')';

// <tff_quantified_formula> ::= <fof_quantifier> [<tff_variable_list>] :
//                            <tff_unitary_formula>
// <tff_variable_list>    ::= <tff_variable> | <tff_variable>,<tff_variable_list>
// <tff_variable>         ::= <tff_typed_variable> | <variable>
// <tff_typed_variable>   ::= <variable> : <tff_atomic_type>
// <tff_unary_formula>    ::= <unary_connective> <tff_unitary_formula> |
//                            <fof_infix_unary>
// <tff_conditional>      ::= $ite_f(<tff_logic_formula>,<tff_logic_formula>,
//                            <tff_logic_formula>)
tff_quantified_formula : fof_quantifier '[' tff_variable_list ']' ':' tff_unitary_formula;
tff_variable_list : tff_variable (',' tff_variable)*;
tff_variable : variable (':' tff_atomic_type)?;
tff_unary_formula: unary_connective tff_unitary_formula | fof_infix_unary;
tff_atomic_formula : fof_atomic_formula;
tff_conditional: '$ite_f(' tff_logic_formula ',' tff_logic_formula ',' tff_logic_formula ')';

// <tff_let>              ::= $let_tf(<tff_let_term_defns>,<tff_formula>) |
//                            $let_ff(<tff_let_formula_defns>,<tff_formula>)
// %----See the commentary for <thf_let>.
// <tff_let_term_defns>   ::= <tff_let_term_defn> | [<tff_let_term_list>]
// <tff_let_term_list>    ::= <tff_let_term_defn> | 
//                            <tff_let_term_defn>,<tff_let_term_list>
// <tff_let_term_defn>    ::= ! [<tff_variable_list>] : <tff_let_term_defn> |
//                            <tff_let_term_binding>
// <tff_let_term_binding> ::= <plain_term> = <term> | (<tff_let_term_binding>)
// <tff_let_formula_defns> ::= <tff_let_formula_defn> | [<tff_let_formula_list>]
// <tff_let_formula_list> ::= <tff_let_formula_defn> |
//                            <tff_let_formula_defn>,<tff_let_formula_list>
// <tff_let_formula_defn> ::= ! [<tff_variable_list>] : <tff_let_formula_defn> |
//                            <tff_let_formula_binding>
// <tff_let_formula_binding> ::= <plain_atomic_formula> <=> <tff_unitary_formula> |
//                            (<tff_let_formula_binding>)
tff_let : '$let_tf(' tff_let_term_defns ',' tff_formula ')'
        | '$let_ff(' tff_let_formula_defns ',' tff_formula ')';
tff_let_term_defns : tff_let_term_defn | '[' tff_let_term_list ']';
tff_let_term_list : tff_let_term_defn (',' tff_let_term_defn)*;
tff_let_term_defn : Forall '[' tff_variable_list ']' ':' tff_let_term_binding
                  | tff_let_term_binding;
tff_let_term_binding : fof_plain_term Infix_equality fof_term | '(' tff_let_term_binding ')';
tff_let_formula_defns : tff_let_formula_defn | '[' tff_let_formula_list ']';
tff_let_formula_list: tff_let_formula_defn (',' tff_let_formula_defn)*;
tff_let_formula_defn: Forall '[' tff_variable_list ']' ':' tff_let_formula_binding
                    | tff_let_formula_binding;
tff_let_formula_binding: fof_plain_atomic_formula Iff tff_unitary_formula
                       | '(' tff_let_formula_binding ')';
                       
// <tff_sequent>          ::= <tff_formula_tuple> <gentzen_arrow> 
//                            <tff_formula_tuple> | (<tff_sequent>)
// <tff_formula_tuple>    ::= [] | [<tff_formula_tuple_list>]
// <tff_formula_tuple_list> ::= <tff_logic_formula> |
//                            <tff_logic_formula>,<tff_formula_tuple_list>
tff_sequent: tff_formula_tuple '>>' tff_formula_tuple | '(' tff_sequent ')';                      
tff_formula_tuple : '[]' | '[' tff_formula_tuple_list ']';
tff_formula_tuple_list: tff_logic_formula (',' tff_logic_formula)*;
                           
// %----<tff_typed_atom> can appear only at top level
// <tff_typed_atom>       ::= <tff_untyped_atom> : <tff_top_level_type> |
//                            (<tff_typed_atom>)
// <tff_untyped_atom>     ::= <functor> | <system_functor>
tff_typed_atom: tff_untyped_atom ':' tff_top_level_type | '(' tff_typed_atom ')';
tff_untyped_atom: functor | system_functor;


// %----See <thf_top_level_type> for commentary. <tff_quantified_type> is TFF1.
// <tff_top_level_type>   ::= <tff_atomic_type> | <tff_mapping_type> |
//                            <tf1_quantified_type> | (<tff_top_level_type>)
// <tf1_quantified_type>  ::= !> [<tff_variable_list>] : <tff_monotype>
// <tff_monotype>         ::= <tff_atomic_type> | (<tff_mapping_type>)
// <tff_unitary_type>     ::= <tff_atomic_type> | (<tff_xprod_type>)
// <tff_atomic_type>      ::= <atomic_word> | <defined_type> |
//                            <atomic_word>(<tff_type_arguments>) | <variable>
// <tff_type_arguments>   ::= <tff_atomic_type> |
//                            <tff_atomic_type>,<tff_type_arguments>
// <tff_mapping_type>     ::= <tff_unitary_type> <arrow> <tff_atomic_type>
// <tff_xprod_type>       ::= <tff_unitary_type> <star> <tff_atomic_type> |
//                            <tff_xprod_type> <star> <tff_atomic_type>
tff_top_level_type : tff_atomic_type | tff_mapping_type | tf1_quantified_type | '(' tff_top_level_type ')';
tf1_quantified_type : '!>' '[' tff_variable_list ']' ':' tff_monotype;
tff_monotype: tff_atomic_type | '(' tff_mapping_type ')';
tff_unitary_type : tff_atomic_type | '(' tff_xprod_type ')';
tff_atomic_type : defined_type | type_functor ('(' tff_type_arguments ')')? | variable;
tff_type_arguments : tff_atomic_type (',' tff_atomic_type)*;
tff_mapping_type : tff_unitary_type '>' tff_atomic_type;
tff_xprod_type: tff_unitary_type '*' tff_atomic_type
              | tff_xprod_type '*' tff_atomic_type;                        


////////////////////////////////////////////////////////////////////////////////
//// FOF Formulae
////////////////////////////////////////////////////////////////////////////////


//<fof_formula>          ::= <fof_logic_formula> | <fof_sequent>
//<fof_logic_formula>    ::= <fof_binary_formula> | <fof_unitary_formula>
//%----Future answer variable ideas | <answer_formula>
//<fof_binary_formula>   ::= <fof_binary_nonassoc> | <fof_binary_assoc>
//%----Only some binary connectives are associative
//%----There's no precedence among binary connectives
//<fof_binary_nonassoc>  ::= <fof_unitary_formula> <binary_connective>
//                           <fof_unitary_formula>
//%----Associative connectives & and | are in <binary_assoc>
//<fof_binary_assoc>     ::= <fof_or_formula> | <fof_and_formula>
//<fof_or_formula>       ::= <fof_unitary_formula> <vline> <fof_unitary_formula> |
//                           <fof_or_formula> <vline> <fof_unitary_formula>
//<fof_and_formula>      ::= <fof_unitary_formula> & <fof_unitary_formula> |
//                           <fof_and_formula> & <fof_unitary_formula>
//%----<fof_unitary_formula> are in ()s or do not have a <binary_connective> at
//%----the top level.
//<fof_unitary_formula>  ::= <fof_quantified_formula> | <fof_unary_formula> |
//                           <fof_atomic_formula> | (<fof_logic_formula>)
//<fof_quantified_formula> ::= <fof_quantifier> [<fof_variable_list>] :
//                           <fof_unitary_formula>
//<fof_variable_list>    ::= <variable> | <variable>,<fof_variable_list>
//<fof_unary_formula>    ::= <unary_connective> <fof_unitary_formula> |
//                           <fof_infix_unary>
fof_formula : fof_logic_formula | fof_sequent;
fof_logic_formula : fof_binary_formula | fof_unitary_formula;
fof_binary_formula : fof_binary_nonassoc | fof_binary_assoc;
fof_binary_nonassoc: fof_unitary_formula binary_connective fof_unitary_formula;
fof_binary_assoc: fof_or_formula | fof_and_formula;
fof_or_formula : fof_unitary_formula Or fof_unitary_formula
               | fof_or_formula Or fof_unitary_formula;
fof_and_formula : fof_unitary_formula And fof_unitary_formula
               | fof_and_formula And fof_unitary_formula;
fof_unitary_formula : fof_quantified_formula | fof_unary_formula | fof_atomic_formula
                    | '(' fof_logic_formula ')';
fof_quantified_formula : fof_quantifier '[' fof_variable_list ']' ':' fof_unitary_formula;
fof_variable_list : variable (',' variable)*;
fof_unary_formula: unary_connective fof_unitary_formula | fof_infix_unary;


//<fof_infix_unary>      ::= <fof_term> <infix_inequality> <fof_term>
//<fof_atomic_formula>   ::= <fof_plain_atomic_formula> |
//                           <fof_defined_atomic_formula> |
//                           <fof_system_atomic_formula>
//<fof_plain_atomic_formula> ::= <fof_plain_term>
//<fof_defined_atomic_formula> ::= <fof_defined_plain_formula> |
//                           <fof_defined_infix_formula>
//<fof_defined_plain_formula> ::= <fof_defined_plain_term>
//<fof_defined_infix_formula> ::= <fof_term> <defined_infix_pred> <fof_term>
//%----System terms have system specific interpretations
//<fof_system_atomic_formula> ::= <fof_system_term>

fof_infix_unary : fof_term Infix_inequality fof_term;
fof_atomic_formula : fof_plain_atomic_formula | fof_defined_atomic_formula | fof_system_atomic_formula;
fof_plain_atomic_formula : fof_plain_term;

fof_defined_atomic_formula : fof_defined_plain_formula | fof_defined_infix_formula;
fof_defined_plain_formula : fof_defined_term;
fof_defined_infix_formula : fof_term defined_infix_pred fof_term;
fof_system_atomic_formula: fof_system_term;



//%----FOF terms.
//<fof_plain_term>       ::= <constant> | <functor>(<fof_arguments>)
//%----Defined terms have TPTP specific interpretations
//<fof_defined_term>     ::= <defined_term> | <fof_defined_atomic_term>
//<fof_defined_atomic_term>  ::= <fof_defined_plain_term>
//%----None yet             | <defined_infix_term>
//%----None yet <defined_infix_term> ::= <fof_term> <defined_infix_func> <fof_term>
//%----None yet <defined_infix_func> ::=
//<fof_defined_plain_term>   ::= <defined_constant> |
//                           <defined_functor>(<fof_arguments>)
//%----System terms have system specific interpretations
//<fof_system_term>      ::= <system_constant> | <system_functor>(<fof_arguments>)
//%----Arguments recurse back to terms (this is the FOF world here)
//<fof_arguments>        ::= <fof_term> | <fof_term>,<fof_arguments>
//%----These are terms used as arguments. Not the entry point for terms because
//%----<fof_plain_term> is also used as <fof_plain_atomic_formula>. The <tff_
//%----options are for only TFF, but are here because fof_plain_atomic_formula>
//%----is used in fof_atomic_formula>, which is also used as <tff_atomic_formula>.
//<fof_term>             ::= <fof_function_term> | <variable> |
//                           <tff_conditional_term> | <tff_let_term> |
//                           <tff_tuple_term>
// ADAPTED:                 | <defined_term>
//<fof_function_term>    ::= <fof_plain_term> | <fof_defined_plain_term> |
//                           <fof_system_term>

fof_plain_term: functor ('(' fof_arguments ')')?; // contracted for easier handling
fof_defined_term: defined_functor ('(' fof_arguments ')')?; // contracted for easier handling
fof_system_term: system_functor ('(' fof_arguments ')')?; // contracted for easier handling

fof_arguments : fof_term (',' fof_term)*;

fof_term: defined_term | fof_function_term | variable | tff_conditional_term | tff_let_term | tff_tuple_term;
fof_function_term : fof_plain_term | fof_defined_term | fof_system_term;

//%----Conditional terms should be used by only TFF.
//<tff_conditional_term> ::= $ite_t(<tff_logic_formula>,<fof_term>,<fof_term>)
//%----Let terms should be used by only TFF. $let_ft is for use when there is
//%----a $ite_t in the <fof_term>. See the commentary for $let_tf and $let_ff.
//<tff_let_term>         ::= $let_ft(<tff_let_formula_defns>,<fof_term>) |
//                           $let_tt(<tff_let_term_defns>,<fof_term>)
//%----<tff_tuple_term> uses {}s to disambiguate from tuples of formulae in []s.
//<tff_tuple_term>       ::= {} | {<fof_arguments>}

tff_conditional_term : '$ite_t(' tff_logic_formula ',' fof_term ',' fof_term ')';
tff_let_term : '$let_ft(' tff_let_formula_defns ',' fof_term ')'
         | '$let_tt(' tff_let_term_defns ',' fof_term ')';
tff_tuple_term : '{}' | '{' fof_arguments '}';


//  <fof_sequent>          ::= <fof_formula_tuple> <gentzen_arrow>
//                             <fof_formula_tuple> | (<fof_sequent>)
//
//  <fof_formula_tuple>    ::= [] | [<fof_formula_tuple_list>]
//  <fof_formula_tuple_list> ::= <fof_logic_formula> |
//                             <fof_logic_formula>,<fof_formula_tuple_list>
fof_sequent : fof_formula_tuple '>>' fof_formula_tuple | '(' fof_sequent ')';
fof_formula_tuple : '[]' | '[' fof_formula_tuple_list ']';
fof_formula_tuple_list: fof_logic_formula (',' fof_logic_formula)*;

////////////////////////////////////////////////////////////////////////////////
//// CNF Formulae
////////////////////////////////////////////////////////////////////////////////


// <cnf_formula>          ::= <disjunction> | (<disjunction>)
// <disjunction>          ::= <literal> | <disjunction> <vline> <literal>
// <literal>              ::= <fof_atomic_formula> | ~ <fof_atomic_formula> |
//                           <fof_infix_unary>
cnf_formula : cnf_disjunction | '(' cnf_disjunction ')';
cnf_disjunction : cnf_literal | cnf_disjunction '|' cnf_literal;
cnf_literal : fof_atomic_formula | Not fof_atomic_formula | fof_infix_unary;


////////////////////////////////////////////////////////////////////////////////
//// Connectives
////////////////////////////////////////////////////////////////////////////////


// %----Connectives - THF
// <thf_quantifier>       ::= <fol_quantifier> | <th0_quantifier> |
//                            <th1_quantifier>
// %----TH0 quantifiers are also available in TH1
// <th0_quantifier>       ::= ^ | @+ | @-
// <th1_quantifier>       ::= !> | ?*
// <thf_pair_connective>  ::= <infix_equality> | <infix_inequality> |
//                            <binary_connective> | <assignment>
// <thf_unary_connective> ::= <unary_connective> | <th1_unary_connective>
// <th1_unary_connective> ::= !! | ?? | @@+ | @@- | @=
thf_quantifier : fof_quantifier | th0_quantifier | th1_quantifier;
th0_quantifier: Lambda | Choice | Description;
th1_quantifier: TyForall | TyExists;
thf_pair_connective : Infix_equality | Infix_inequality | binary_connective | Assignment ;
thf_unary_connective : unary_connective | th1_unary_connective;
th1_unary_connective : ForallComb | ExistsComb | ChoiceComb | DescriptionComb | EqComb;

// %----Types for THF and TFF
//<type_functor>         ::= <atomic_word>
// <defined_type>         ::= <atomic_defined_word>
type_functor: atomic_word;
defined_type : atomic_defined_word;

// %----Connectives - FOF
// <fol_quantifier>       ::= ! | ?
// <binary_connective>    ::= <=> | => | <= | <~> | ~<vline> | ~&
// <assoc_connective>     ::= <vline> | &
// <unary_connective>     ::= ~
fof_quantifier: Forall | Exists;
binary_connective: Iff | Impl | If | Niff | Nor | Nand;
assoc_connective : Or | And;
unary_connective : Not;


//////////////////////////////////////////////////////////
// Shared formula stuff
//////////////////////////////////////////////////////////


// <defined_infix_pred>   ::= <infix_equality>
// <infix_equality>       ::= = // Modelled as Token
// <infix_inequality>     ::= != // Modelled as Token
defined_infix_pred : Infix_equality;

// <constant>             ::= <functor>
// <functor>              ::= <atomic_word>
constant : functor;
functor : atomic_word;

// <system_constant>      ::= <system_functor>
// <system_functor>       ::= <atomic_system_word>
system_constant : system_functor;
system_functor : atomic_system_word;

// <defined_constant>     ::= <defined_functor>
// <defined_functor>      ::= <atomic_defined_word>
defined_constant : defined_functor;
defined_functor : atomic_defined_word;

// <defined_term>         ::= <number> | <distinct_object>
// <variable>             ::= <upper_word>
defined_term : number | Distinct_object;
variable : Upper_word;
