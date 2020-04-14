// Generated from /home/lex/dev/Leo-III/contrib/tptp.g4 by ANTLR 4.7.2

package leo.modules.parsers.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class tptpParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, Or=36, And=37, Iff=38, Impl=39, 
		If=40, Niff=41, Nor=42, Nand=43, Not=44, ForallComb=45, TyForall=46, Infix_inequality=47, 
		Infix_equality=48, Forall=49, ExistsComb=50, TyExists=51, Exists=52, Lambda=53, 
		ChoiceComb=54, Choice=55, DescriptionComb=56, Description=57, EqComb=58, 
		App=59, Assignment=60, Real=61, Signed_real=62, Unsigned_real=63, Rational=64, 
		Signed_rational=65, Unsigned_rational=66, Integer=67, Signed_integer=68, 
		Unsigned_integer=69, Decimal=70, Positive_decimal=71, Decimal_exponent=72, 
		Decimal_fraction=73, Dot_decimal=74, Exp_integer=75, Signed_exp_integer=76, 
		Unsigned_exp_integer=77, Dollar_word=78, Dollar_dollar_word=79, Upper_word=80, 
		Lower_word=81, Single_quoted=82, Distinct_object=83, WS=84, Line_comment=85, 
		Block_comment=86;
	public static final int
		RULE_tptp_file = 0, RULE_tptp_input = 1, RULE_include = 2, RULE_formula_selection = 3, 
		RULE_name = 4, RULE_atomic_word = 5, RULE_atomic_defined_word = 6, RULE_atomic_system_word = 7, 
		RULE_number = 8, RULE_file_name = 9, RULE_annotated_formula = 10, RULE_thf_annotated = 11, 
		RULE_tff_annotated = 12, RULE_fof_annotated = 13, RULE_cnf_annotated = 14, 
		RULE_annotations = 15, RULE_formula_role = 16, RULE_source = 17, RULE_optional_info = 18, 
		RULE_general_term = 19, RULE_general_data = 20, RULE_general_function = 21, 
		RULE_formula_data = 22, RULE_general_list = 23, RULE_thf_formula = 24, 
		RULE_thf_logic_formula = 25, RULE_thf_binary_formula = 26, RULE_thf_binary_pair = 27, 
		RULE_thf_binary_tuple = 28, RULE_thf_or_formula = 29, RULE_thf_and_formula = 30, 
		RULE_thf_apply_formula = 31, RULE_thf_unitary_formula = 32, RULE_thf_quantified_formula = 33, 
		RULE_thf_quantification = 34, RULE_thf_variable = 35, RULE_thf_unary_formula = 36, 
		RULE_thf_atom = 37, RULE_thf_function = 38, RULE_thf_plain_term = 39, 
		RULE_thf_defined_term = 40, RULE_thf_system_term = 41, RULE_thf_conn_term = 42, 
		RULE_thf_conditional = 43, RULE_thf_let = 44, RULE_thf_arguments = 45, 
		RULE_thf_type_formula = 46, RULE_thf_typeable_formula = 47, RULE_thf_subtype = 48, 
		RULE_thf_top_level_type = 49, RULE_thf_unitary_type = 50, RULE_thf_apply_type = 51, 
		RULE_thf_binary_type = 52, RULE_thf_mapping_type = 53, RULE_thf_xprod_type = 54, 
		RULE_thf_union_type = 55, RULE_thf_sequent = 56, RULE_thf_tuple = 57, 
		RULE_thf_formula_list = 58, RULE_tff_formula = 59, RULE_tff_logic_formula = 60, 
		RULE_tff_binary_formula = 61, RULE_tff_binary_nonassoc = 62, RULE_tff_binary_assoc = 63, 
		RULE_tff_or_formula = 64, RULE_tff_and_formula = 65, RULE_tff_unitary_formula = 66, 
		RULE_tff_quantified_formula = 67, RULE_tff_variable_list = 68, RULE_tff_variable = 69, 
		RULE_tff_unary_formula = 70, RULE_tff_atomic_formula = 71, RULE_tff_conditional = 72, 
		RULE_tff_let = 73, RULE_tff_let_term_defns = 74, RULE_tff_let_term_list = 75, 
		RULE_tff_let_term_defn = 76, RULE_tff_let_term_binding = 77, RULE_tff_let_formula_defns = 78, 
		RULE_tff_let_formula_list = 79, RULE_tff_let_formula_defn = 80, RULE_tff_let_formula_binding = 81, 
		RULE_tff_sequent = 82, RULE_tff_formula_tuple = 83, RULE_tff_formula_tuple_list = 84, 
		RULE_tff_typed_atom = 85, RULE_tff_untyped_atom = 86, RULE_tff_top_level_type = 87, 
		RULE_tf1_quantified_type = 88, RULE_tff_monotype = 89, RULE_tff_unitary_type = 90, 
		RULE_tff_atomic_type = 91, RULE_tff_type_arguments = 92, RULE_tff_mapping_type = 93, 
		RULE_tff_xprod_type = 94, RULE_fof_formula = 95, RULE_fof_logic_formula = 96, 
		RULE_fof_binary_formula = 97, RULE_fof_binary_nonassoc = 98, RULE_fof_binary_assoc = 99, 
		RULE_fof_or_formula = 100, RULE_fof_and_formula = 101, RULE_fof_unitary_formula = 102, 
		RULE_fof_quantified_formula = 103, RULE_fof_variable_list = 104, RULE_fof_unary_formula = 105, 
		RULE_fof_infix_unary = 106, RULE_fof_atomic_formula = 107, RULE_fof_plain_atomic_formula = 108, 
		RULE_fof_defined_atomic_formula = 109, RULE_fof_defined_plain_formula = 110, 
		RULE_fof_defined_infix_formula = 111, RULE_fof_system_atomic_formula = 112, 
		RULE_fof_plain_term = 113, RULE_fof_defined_term = 114, RULE_fof_system_term = 115, 
		RULE_fof_arguments = 116, RULE_fof_term = 117, RULE_fof_function_term = 118, 
		RULE_tff_conditional_term = 119, RULE_tff_let_term = 120, RULE_tff_tuple_term = 121, 
		RULE_fof_sequent = 122, RULE_fof_formula_tuple = 123, RULE_fof_formula_tuple_list = 124, 
		RULE_cnf_formula = 125, RULE_cnf_disjunction = 126, RULE_cnf_literal = 127, 
		RULE_thf_quantifier = 128, RULE_th0_quantifier = 129, RULE_th1_quantifier = 130, 
		RULE_thf_pair_connective = 131, RULE_thf_unary_connective = 132, RULE_th1_unary_connective = 133, 
		RULE_type_functor = 134, RULE_defined_type = 135, RULE_fof_quantifier = 136, 
		RULE_binary_connective = 137, RULE_assoc_connective = 138, RULE_unary_connective = 139, 
		RULE_defined_infix_pred = 140, RULE_constant = 141, RULE_functor = 142, 
		RULE_system_constant = 143, RULE_system_functor = 144, RULE_defined_constant = 145, 
		RULE_defined_functor = 146, RULE_defined_term = 147, RULE_variable = 148;
	private static String[] makeRuleNames() {
		return new String[] {
			"tptp_file", "tptp_input", "include", "formula_selection", "name", "atomic_word", 
			"atomic_defined_word", "atomic_system_word", "number", "file_name", "annotated_formula", 
			"thf_annotated", "tff_annotated", "fof_annotated", "cnf_annotated", "annotations", 
			"formula_role", "source", "optional_info", "general_term", "general_data", 
			"general_function", "formula_data", "general_list", "thf_formula", "thf_logic_formula", 
			"thf_binary_formula", "thf_binary_pair", "thf_binary_tuple", "thf_or_formula", 
			"thf_and_formula", "thf_apply_formula", "thf_unitary_formula", "thf_quantified_formula", 
			"thf_quantification", "thf_variable", "thf_unary_formula", "thf_atom", 
			"thf_function", "thf_plain_term", "thf_defined_term", "thf_system_term", 
			"thf_conn_term", "thf_conditional", "thf_let", "thf_arguments", "thf_type_formula", 
			"thf_typeable_formula", "thf_subtype", "thf_top_level_type", "thf_unitary_type", 
			"thf_apply_type", "thf_binary_type", "thf_mapping_type", "thf_xprod_type", 
			"thf_union_type", "thf_sequent", "thf_tuple", "thf_formula_list", "tff_formula", 
			"tff_logic_formula", "tff_binary_formula", "tff_binary_nonassoc", "tff_binary_assoc", 
			"tff_or_formula", "tff_and_formula", "tff_unitary_formula", "tff_quantified_formula", 
			"tff_variable_list", "tff_variable", "tff_unary_formula", "tff_atomic_formula", 
			"tff_conditional", "tff_let", "tff_let_term_defns", "tff_let_term_list", 
			"tff_let_term_defn", "tff_let_term_binding", "tff_let_formula_defns", 
			"tff_let_formula_list", "tff_let_formula_defn", "tff_let_formula_binding", 
			"tff_sequent", "tff_formula_tuple", "tff_formula_tuple_list", "tff_typed_atom", 
			"tff_untyped_atom", "tff_top_level_type", "tf1_quantified_type", "tff_monotype", 
			"tff_unitary_type", "tff_atomic_type", "tff_type_arguments", "tff_mapping_type", 
			"tff_xprod_type", "fof_formula", "fof_logic_formula", "fof_binary_formula", 
			"fof_binary_nonassoc", "fof_binary_assoc", "fof_or_formula", "fof_and_formula", 
			"fof_unitary_formula", "fof_quantified_formula", "fof_variable_list", 
			"fof_unary_formula", "fof_infix_unary", "fof_atomic_formula", "fof_plain_atomic_formula", 
			"fof_defined_atomic_formula", "fof_defined_plain_formula", "fof_defined_infix_formula", 
			"fof_system_atomic_formula", "fof_plain_term", "fof_defined_term", "fof_system_term", 
			"fof_arguments", "fof_term", "fof_function_term", "tff_conditional_term", 
			"tff_let_term", "tff_tuple_term", "fof_sequent", "fof_formula_tuple", 
			"fof_formula_tuple_list", "cnf_formula", "cnf_disjunction", "cnf_literal", 
			"thf_quantifier", "th0_quantifier", "th1_quantifier", "thf_pair_connective", 
			"thf_unary_connective", "th1_unary_connective", "type_functor", "defined_type", 
			"fof_quantifier", "binary_connective", "assoc_connective", "unary_connective", 
			"defined_infix_pred", "constant", "functor", "system_constant", "system_functor", 
			"defined_constant", "defined_functor", "defined_term", "variable"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'include'", "'('", "')'", "'.'", "','", "'['", "']'", "'thf'", 
			"'tff'", "'fof'", "'cnf'", "':'", "'$thf('", "'$tff('", "'$fof('", "'$fot('", 
			"'$cnf('", "'[]'", "'$ite('", "'$let('", "'<<'", "'>'", "'*'", "'+'", 
			"'-->'", "'{}'", "'{'", "'}'", "'$ite_f('", "'$let_tf('", "'$let_ff('", 
			"'>>'", "'$ite_t('", "'$let_ft('", "'$let_tt('", "'|'", "'&'", "'<=>'", 
			"'=>'", "'<='", "'<~>'", "'~|'", "'~&'", "'~'", "'!!'", "'!>'", "'!='", 
			"'='", "'!'", "'??'", "'?*'", "'?'", "'^'", "'@@+'", "'@+'", "'@@-'", 
			"'@-'", "'@='", "'@'", "':='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"Or", "And", "Iff", "Impl", "If", "Niff", "Nor", "Nand", "Not", "ForallComb", 
			"TyForall", "Infix_inequality", "Infix_equality", "Forall", "ExistsComb", 
			"TyExists", "Exists", "Lambda", "ChoiceComb", "Choice", "DescriptionComb", 
			"Description", "EqComb", "App", "Assignment", "Real", "Signed_real", 
			"Unsigned_real", "Rational", "Signed_rational", "Unsigned_rational", 
			"Integer", "Signed_integer", "Unsigned_integer", "Decimal", "Positive_decimal", 
			"Decimal_exponent", "Decimal_fraction", "Dot_decimal", "Exp_integer", 
			"Signed_exp_integer", "Unsigned_exp_integer", "Dollar_word", "Dollar_dollar_word", 
			"Upper_word", "Lower_word", "Single_quoted", "Distinct_object", "WS", 
			"Line_comment", "Block_comment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "tptp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public tptpParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class Tptp_fileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(tptpParser.EOF, 0); }
		public List<Tptp_inputContext> tptp_input() {
			return getRuleContexts(Tptp_inputContext.class);
		}
		public Tptp_inputContext tptp_input(int i) {
			return getRuleContext(Tptp_inputContext.class,i);
		}
		public Tptp_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tptp_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTptp_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTptp_file(this);
		}
	}

	public final Tptp_fileContext tptp_file() throws RecognitionException {
		Tptp_fileContext _localctx = new Tptp_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_tptp_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10))) != 0)) {
				{
				{
				setState(298);
				tptp_input();
				}
				}
				setState(303);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(304);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tptp_inputContext extends ParserRuleContext {
		public Annotated_formulaContext annotated_formula() {
			return getRuleContext(Annotated_formulaContext.class,0);
		}
		public IncludeContext include() {
			return getRuleContext(IncludeContext.class,0);
		}
		public Tptp_inputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tptp_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTptp_input(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTptp_input(this);
		}
	}

	public final Tptp_inputContext tptp_input() throws RecognitionException {
		Tptp_inputContext _localctx = new Tptp_inputContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_tptp_input);
		try {
			setState(308);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
			case T__8:
			case T__9:
			case T__10:
				enterOuterAlt(_localctx, 1);
				{
				setState(306);
				annotated_formula();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(307);
				include();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IncludeContext extends ParserRuleContext {
		public File_nameContext file_name() {
			return getRuleContext(File_nameContext.class,0);
		}
		public Formula_selectionContext formula_selection() {
			return getRuleContext(Formula_selectionContext.class,0);
		}
		public IncludeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_include; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterInclude(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitInclude(this);
		}
	}

	public final IncludeContext include() throws RecognitionException {
		IncludeContext _localctx = new IncludeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_include);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			match(T__0);
			setState(311);
			match(T__1);
			setState(312);
			file_name();
			setState(314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(313);
				formula_selection();
				}
			}

			setState(316);
			match(T__2);
			setState(317);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Formula_selectionContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public Formula_selectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula_selection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFormula_selection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFormula_selection(this);
		}
	}

	public final Formula_selectionContext formula_selection() throws RecognitionException {
		Formula_selectionContext _localctx = new Formula_selectionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_formula_selection);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			match(T__4);
			setState(320);
			match(T__5);
			setState(321);
			name();
			setState(326);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(322);
				match(T__4);
				setState(323);
				name();
				}
				}
				setState(328);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(329);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public Atomic_wordContext atomic_word() {
			return getRuleContext(Atomic_wordContext.class,0);
		}
		public TerminalNode Integer() { return getToken(tptpParser.Integer, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitName(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_name);
		try {
			setState(333);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(331);
				atomic_word();
				}
				break;
			case Integer:
				enterOuterAlt(_localctx, 2);
				{
				setState(332);
				match(Integer);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Atomic_wordContext extends ParserRuleContext {
		public TerminalNode Lower_word() { return getToken(tptpParser.Lower_word, 0); }
		public TerminalNode Single_quoted() { return getToken(tptpParser.Single_quoted, 0); }
		public Atomic_wordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_word; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterAtomic_word(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitAtomic_word(this);
		}
	}

	public final Atomic_wordContext atomic_word() throws RecognitionException {
		Atomic_wordContext _localctx = new Atomic_wordContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_atomic_word);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			_la = _input.LA(1);
			if ( !(_la==Lower_word || _la==Single_quoted) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Atomic_defined_wordContext extends ParserRuleContext {
		public TerminalNode Dollar_word() { return getToken(tptpParser.Dollar_word, 0); }
		public Atomic_defined_wordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_defined_word; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterAtomic_defined_word(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitAtomic_defined_word(this);
		}
	}

	public final Atomic_defined_wordContext atomic_defined_word() throws RecognitionException {
		Atomic_defined_wordContext _localctx = new Atomic_defined_wordContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_atomic_defined_word);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(Dollar_word);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Atomic_system_wordContext extends ParserRuleContext {
		public TerminalNode Dollar_dollar_word() { return getToken(tptpParser.Dollar_dollar_word, 0); }
		public Atomic_system_wordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_system_word; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterAtomic_system_word(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitAtomic_system_word(this);
		}
	}

	public final Atomic_system_wordContext atomic_system_word() throws RecognitionException {
		Atomic_system_wordContext _localctx = new Atomic_system_wordContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_atomic_system_word);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(339);
			match(Dollar_dollar_word);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode Integer() { return getToken(tptpParser.Integer, 0); }
		public TerminalNode Rational() { return getToken(tptpParser.Rational, 0); }
		public TerminalNode Real() { return getToken(tptpParser.Real, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(341);
			_la = _input.LA(1);
			if ( !(((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & ((1L << (Real - 61)) | (1L << (Rational - 61)) | (1L << (Integer - 61)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class File_nameContext extends ParserRuleContext {
		public TerminalNode Single_quoted() { return getToken(tptpParser.Single_quoted, 0); }
		public File_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFile_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFile_name(this);
		}
	}

	public final File_nameContext file_name() throws RecognitionException {
		File_nameContext _localctx = new File_nameContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_file_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			match(Single_quoted);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Annotated_formulaContext extends ParserRuleContext {
		public Thf_annotatedContext thf_annotated() {
			return getRuleContext(Thf_annotatedContext.class,0);
		}
		public Tff_annotatedContext tff_annotated() {
			return getRuleContext(Tff_annotatedContext.class,0);
		}
		public Fof_annotatedContext fof_annotated() {
			return getRuleContext(Fof_annotatedContext.class,0);
		}
		public Cnf_annotatedContext cnf_annotated() {
			return getRuleContext(Cnf_annotatedContext.class,0);
		}
		public Annotated_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotated_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterAnnotated_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitAnnotated_formula(this);
		}
	}

	public final Annotated_formulaContext annotated_formula() throws RecognitionException {
		Annotated_formulaContext _localctx = new Annotated_formulaContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_annotated_formula);
		try {
			setState(349);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(345);
				thf_annotated();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(346);
				tff_annotated();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
				setState(347);
				fof_annotated();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(348);
				cnf_annotated();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_annotatedContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Formula_roleContext formula_role() {
			return getRuleContext(Formula_roleContext.class,0);
		}
		public Thf_formulaContext thf_formula() {
			return getRuleContext(Thf_formulaContext.class,0);
		}
		public AnnotationsContext annotations() {
			return getRuleContext(AnnotationsContext.class,0);
		}
		public Thf_annotatedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_annotated; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_annotated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_annotated(this);
		}
	}

	public final Thf_annotatedContext thf_annotated() throws RecognitionException {
		Thf_annotatedContext _localctx = new Thf_annotatedContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_thf_annotated);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
			match(T__7);
			setState(352);
			match(T__1);
			setState(353);
			name();
			setState(354);
			match(T__4);
			setState(355);
			formula_role();
			setState(356);
			match(T__4);
			setState(357);
			thf_formula();
			setState(359);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(358);
				annotations();
				}
			}

			setState(361);
			match(T__2);
			setState(362);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_annotatedContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Formula_roleContext formula_role() {
			return getRuleContext(Formula_roleContext.class,0);
		}
		public Tff_formulaContext tff_formula() {
			return getRuleContext(Tff_formulaContext.class,0);
		}
		public AnnotationsContext annotations() {
			return getRuleContext(AnnotationsContext.class,0);
		}
		public Tff_annotatedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_annotated; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_annotated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_annotated(this);
		}
	}

	public final Tff_annotatedContext tff_annotated() throws RecognitionException {
		Tff_annotatedContext _localctx = new Tff_annotatedContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_tff_annotated);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			match(T__8);
			setState(365);
			match(T__1);
			setState(366);
			name();
			setState(367);
			match(T__4);
			setState(368);
			formula_role();
			setState(369);
			match(T__4);
			setState(370);
			tff_formula();
			setState(372);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(371);
				annotations();
				}
			}

			setState(374);
			match(T__2);
			setState(375);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_annotatedContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Formula_roleContext formula_role() {
			return getRuleContext(Formula_roleContext.class,0);
		}
		public Fof_formulaContext fof_formula() {
			return getRuleContext(Fof_formulaContext.class,0);
		}
		public AnnotationsContext annotations() {
			return getRuleContext(AnnotationsContext.class,0);
		}
		public Fof_annotatedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_annotated; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_annotated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_annotated(this);
		}
	}

	public final Fof_annotatedContext fof_annotated() throws RecognitionException {
		Fof_annotatedContext _localctx = new Fof_annotatedContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_fof_annotated);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(377);
			match(T__9);
			setState(378);
			match(T__1);
			setState(379);
			name();
			setState(380);
			match(T__4);
			setState(381);
			formula_role();
			setState(382);
			match(T__4);
			setState(383);
			fof_formula();
			setState(385);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(384);
				annotations();
				}
			}

			setState(387);
			match(T__2);
			setState(388);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cnf_annotatedContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Formula_roleContext formula_role() {
			return getRuleContext(Formula_roleContext.class,0);
		}
		public Cnf_formulaContext cnf_formula() {
			return getRuleContext(Cnf_formulaContext.class,0);
		}
		public AnnotationsContext annotations() {
			return getRuleContext(AnnotationsContext.class,0);
		}
		public Cnf_annotatedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cnf_annotated; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterCnf_annotated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitCnf_annotated(this);
		}
	}

	public final Cnf_annotatedContext cnf_annotated() throws RecognitionException {
		Cnf_annotatedContext _localctx = new Cnf_annotatedContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_cnf_annotated);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(390);
			match(T__10);
			setState(391);
			match(T__1);
			setState(392);
			name();
			setState(393);
			match(T__4);
			setState(394);
			formula_role();
			setState(395);
			match(T__4);
			setState(396);
			cnf_formula();
			setState(398);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(397);
				annotations();
				}
			}

			setState(400);
			match(T__2);
			setState(401);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AnnotationsContext extends ParserRuleContext {
		public SourceContext source() {
			return getRuleContext(SourceContext.class,0);
		}
		public Optional_infoContext optional_info() {
			return getRuleContext(Optional_infoContext.class,0);
		}
		public AnnotationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterAnnotations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitAnnotations(this);
		}
	}

	public final AnnotationsContext annotations() throws RecognitionException {
		AnnotationsContext _localctx = new AnnotationsContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_annotations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
			match(T__4);
			setState(404);
			source();
			setState(406);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(405);
				optional_info();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Formula_roleContext extends ParserRuleContext {
		public TerminalNode Lower_word() { return getToken(tptpParser.Lower_word, 0); }
		public Formula_roleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula_role; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFormula_role(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFormula_role(this);
		}
	}

	public final Formula_roleContext formula_role() throws RecognitionException {
		Formula_roleContext _localctx = new Formula_roleContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_formula_role);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(408);
			match(Lower_word);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SourceContext extends ParserRuleContext {
		public General_termContext general_term() {
			return getRuleContext(General_termContext.class,0);
		}
		public SourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_source; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterSource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitSource(this);
		}
	}

	public final SourceContext source() throws RecognitionException {
		SourceContext _localctx = new SourceContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_source);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(410);
			general_term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Optional_infoContext extends ParserRuleContext {
		public General_listContext general_list() {
			return getRuleContext(General_listContext.class,0);
		}
		public Optional_infoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optional_info; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterOptional_info(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitOptional_info(this);
		}
	}

	public final Optional_infoContext optional_info() throws RecognitionException {
		Optional_infoContext _localctx = new Optional_infoContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_optional_info);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			match(T__4);
			setState(413);
			general_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class General_termContext extends ParserRuleContext {
		public General_dataContext general_data() {
			return getRuleContext(General_dataContext.class,0);
		}
		public General_termContext general_term() {
			return getRuleContext(General_termContext.class,0);
		}
		public General_listContext general_list() {
			return getRuleContext(General_listContext.class,0);
		}
		public General_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_general_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterGeneral_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitGeneral_term(this);
		}
	}

	public final General_termContext general_term() throws RecognitionException {
		General_termContext _localctx = new General_termContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_general_term);
		try {
			setState(421);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(415);
				general_data();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(416);
				general_data();
				setState(417);
				match(T__11);
				setState(418);
				general_term();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(420);
				general_list();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class General_dataContext extends ParserRuleContext {
		public Atomic_wordContext atomic_word() {
			return getRuleContext(Atomic_wordContext.class,0);
		}
		public General_functionContext general_function() {
			return getRuleContext(General_functionContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode Distinct_object() { return getToken(tptpParser.Distinct_object, 0); }
		public Formula_dataContext formula_data() {
			return getRuleContext(Formula_dataContext.class,0);
		}
		public General_dataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_general_data; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterGeneral_data(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitGeneral_data(this);
		}
	}

	public final General_dataContext general_data() throws RecognitionException {
		General_dataContext _localctx = new General_dataContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_general_data);
		try {
			setState(429);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(423);
				atomic_word();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(424);
				general_function();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(425);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(426);
				number();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(427);
				match(Distinct_object);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(428);
				formula_data();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class General_functionContext extends ParserRuleContext {
		public Atomic_wordContext atomic_word() {
			return getRuleContext(Atomic_wordContext.class,0);
		}
		public List<General_termContext> general_term() {
			return getRuleContexts(General_termContext.class);
		}
		public General_termContext general_term(int i) {
			return getRuleContext(General_termContext.class,i);
		}
		public General_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_general_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterGeneral_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitGeneral_function(this);
		}
	}

	public final General_functionContext general_function() throws RecognitionException {
		General_functionContext _localctx = new General_functionContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_general_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			atomic_word();
			setState(432);
			match(T__1);
			setState(433);
			general_term();
			setState(438);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(434);
				match(T__4);
				setState(435);
				general_term();
				}
				}
				setState(440);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(441);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Formula_dataContext extends ParserRuleContext {
		public Thf_formulaContext thf_formula() {
			return getRuleContext(Thf_formulaContext.class,0);
		}
		public Tff_formulaContext tff_formula() {
			return getRuleContext(Tff_formulaContext.class,0);
		}
		public Fof_formulaContext fof_formula() {
			return getRuleContext(Fof_formulaContext.class,0);
		}
		public Fof_termContext fof_term() {
			return getRuleContext(Fof_termContext.class,0);
		}
		public Cnf_formulaContext cnf_formula() {
			return getRuleContext(Cnf_formulaContext.class,0);
		}
		public Formula_dataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula_data; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFormula_data(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFormula_data(this);
		}
	}

	public final Formula_dataContext formula_data() throws RecognitionException {
		Formula_dataContext _localctx = new Formula_dataContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_formula_data);
		try {
			setState(463);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__12:
				enterOuterAlt(_localctx, 1);
				{
				setState(443);
				match(T__12);
				setState(444);
				thf_formula();
				setState(445);
				match(T__2);
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 2);
				{
				setState(447);
				match(T__13);
				setState(448);
				tff_formula();
				setState(449);
				match(T__2);
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 3);
				{
				setState(451);
				match(T__14);
				setState(452);
				fof_formula();
				setState(453);
				match(T__2);
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 4);
				{
				setState(455);
				match(T__15);
				setState(456);
				fof_term();
				setState(457);
				match(T__2);
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 5);
				{
				setState(459);
				match(T__16);
				setState(460);
				cnf_formula();
				setState(461);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class General_listContext extends ParserRuleContext {
		public List<General_termContext> general_term() {
			return getRuleContexts(General_termContext.class);
		}
		public General_termContext general_term(int i) {
			return getRuleContext(General_termContext.class,i);
		}
		public General_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_general_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterGeneral_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitGeneral_list(this);
		}
	}

	public final General_listContext general_list() throws RecognitionException {
		General_listContext _localctx = new General_listContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_general_list);
		int _la;
		try {
			setState(477);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(465);
				match(T__17);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(466);
				match(T__5);
				setState(467);
				general_term();
				setState(472);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(468);
					match(T__4);
					setState(469);
					general_term();
					}
					}
					setState(474);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(475);
				match(T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_formulaContext extends ParserRuleContext {
		public Thf_logic_formulaContext thf_logic_formula() {
			return getRuleContext(Thf_logic_formulaContext.class,0);
		}
		public Thf_sequentContext thf_sequent() {
			return getRuleContext(Thf_sequentContext.class,0);
		}
		public Thf_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_formula(this);
		}
	}

	public final Thf_formulaContext thf_formula() throws RecognitionException {
		Thf_formulaContext _localctx = new Thf_formulaContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_thf_formula);
		try {
			setState(481);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(479);
				thf_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(480);
				thf_sequent();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_logic_formulaContext extends ParserRuleContext {
		public Thf_binary_formulaContext thf_binary_formula() {
			return getRuleContext(Thf_binary_formulaContext.class,0);
		}
		public Thf_unitary_formulaContext thf_unitary_formula() {
			return getRuleContext(Thf_unitary_formulaContext.class,0);
		}
		public Thf_type_formulaContext thf_type_formula() {
			return getRuleContext(Thf_type_formulaContext.class,0);
		}
		public Thf_subtypeContext thf_subtype() {
			return getRuleContext(Thf_subtypeContext.class,0);
		}
		public Thf_logic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_logic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_logic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_logic_formula(this);
		}
	}

	public final Thf_logic_formulaContext thf_logic_formula() throws RecognitionException {
		Thf_logic_formulaContext _localctx = new Thf_logic_formulaContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_thf_logic_formula);
		try {
			setState(487);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(483);
				thf_binary_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(484);
				thf_unitary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(485);
				thf_type_formula();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(486);
				thf_subtype();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_binary_formulaContext extends ParserRuleContext {
		public Thf_binary_pairContext thf_binary_pair() {
			return getRuleContext(Thf_binary_pairContext.class,0);
		}
		public Thf_binary_tupleContext thf_binary_tuple() {
			return getRuleContext(Thf_binary_tupleContext.class,0);
		}
		public Thf_binary_typeContext thf_binary_type() {
			return getRuleContext(Thf_binary_typeContext.class,0);
		}
		public Thf_binary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_binary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_binary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_binary_formula(this);
		}
	}

	public final Thf_binary_formulaContext thf_binary_formula() throws RecognitionException {
		Thf_binary_formulaContext _localctx = new Thf_binary_formulaContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_thf_binary_formula);
		try {
			setState(492);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(489);
				thf_binary_pair();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(490);
				thf_binary_tuple();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(491);
				thf_binary_type();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_binary_pairContext extends ParserRuleContext {
		public List<Thf_unitary_formulaContext> thf_unitary_formula() {
			return getRuleContexts(Thf_unitary_formulaContext.class);
		}
		public Thf_unitary_formulaContext thf_unitary_formula(int i) {
			return getRuleContext(Thf_unitary_formulaContext.class,i);
		}
		public Thf_pair_connectiveContext thf_pair_connective() {
			return getRuleContext(Thf_pair_connectiveContext.class,0);
		}
		public Thf_binary_pairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_binary_pair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_binary_pair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_binary_pair(this);
		}
	}

	public final Thf_binary_pairContext thf_binary_pair() throws RecognitionException {
		Thf_binary_pairContext _localctx = new Thf_binary_pairContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_thf_binary_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(494);
			thf_unitary_formula();
			setState(495);
			thf_pair_connective();
			setState(496);
			thf_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_binary_tupleContext extends ParserRuleContext {
		public Thf_or_formulaContext thf_or_formula() {
			return getRuleContext(Thf_or_formulaContext.class,0);
		}
		public Thf_and_formulaContext thf_and_formula() {
			return getRuleContext(Thf_and_formulaContext.class,0);
		}
		public Thf_apply_formulaContext thf_apply_formula() {
			return getRuleContext(Thf_apply_formulaContext.class,0);
		}
		public Thf_binary_tupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_binary_tuple; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_binary_tuple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_binary_tuple(this);
		}
	}

	public final Thf_binary_tupleContext thf_binary_tuple() throws RecognitionException {
		Thf_binary_tupleContext _localctx = new Thf_binary_tupleContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_thf_binary_tuple);
		try {
			setState(501);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(498);
				thf_or_formula(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(499);
				thf_and_formula(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(500);
				thf_apply_formula(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_or_formulaContext extends ParserRuleContext {
		public List<Thf_unitary_formulaContext> thf_unitary_formula() {
			return getRuleContexts(Thf_unitary_formulaContext.class);
		}
		public Thf_unitary_formulaContext thf_unitary_formula(int i) {
			return getRuleContext(Thf_unitary_formulaContext.class,i);
		}
		public TerminalNode Or() { return getToken(tptpParser.Or, 0); }
		public Thf_or_formulaContext thf_or_formula() {
			return getRuleContext(Thf_or_formulaContext.class,0);
		}
		public Thf_or_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_or_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_or_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_or_formula(this);
		}
	}

	public final Thf_or_formulaContext thf_or_formula() throws RecognitionException {
		return thf_or_formula(0);
	}

	private Thf_or_formulaContext thf_or_formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Thf_or_formulaContext _localctx = new Thf_or_formulaContext(_ctx, _parentState);
		Thf_or_formulaContext _prevctx = _localctx;
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_thf_or_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(504);
			thf_unitary_formula();
			setState(505);
			match(Or);
			setState(506);
			thf_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(513);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_or_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_or_formula);
					setState(508);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(509);
					match(Or);
					setState(510);
					thf_unitary_formula();
					}
					} 
				}
				setState(515);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Thf_and_formulaContext extends ParserRuleContext {
		public List<Thf_unitary_formulaContext> thf_unitary_formula() {
			return getRuleContexts(Thf_unitary_formulaContext.class);
		}
		public Thf_unitary_formulaContext thf_unitary_formula(int i) {
			return getRuleContext(Thf_unitary_formulaContext.class,i);
		}
		public TerminalNode And() { return getToken(tptpParser.And, 0); }
		public Thf_and_formulaContext thf_and_formula() {
			return getRuleContext(Thf_and_formulaContext.class,0);
		}
		public Thf_and_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_and_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_and_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_and_formula(this);
		}
	}

	public final Thf_and_formulaContext thf_and_formula() throws RecognitionException {
		return thf_and_formula(0);
	}

	private Thf_and_formulaContext thf_and_formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Thf_and_formulaContext _localctx = new Thf_and_formulaContext(_ctx, _parentState);
		Thf_and_formulaContext _prevctx = _localctx;
		int _startState = 60;
		enterRecursionRule(_localctx, 60, RULE_thf_and_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(517);
			thf_unitary_formula();
			setState(518);
			match(And);
			setState(519);
			thf_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(526);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_and_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_and_formula);
					setState(521);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(522);
					match(And);
					setState(523);
					thf_unitary_formula();
					}
					} 
				}
				setState(528);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Thf_apply_formulaContext extends ParserRuleContext {
		public List<Thf_unitary_formulaContext> thf_unitary_formula() {
			return getRuleContexts(Thf_unitary_formulaContext.class);
		}
		public Thf_unitary_formulaContext thf_unitary_formula(int i) {
			return getRuleContext(Thf_unitary_formulaContext.class,i);
		}
		public TerminalNode App() { return getToken(tptpParser.App, 0); }
		public Thf_apply_formulaContext thf_apply_formula() {
			return getRuleContext(Thf_apply_formulaContext.class,0);
		}
		public Thf_apply_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_apply_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_apply_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_apply_formula(this);
		}
	}

	public final Thf_apply_formulaContext thf_apply_formula() throws RecognitionException {
		return thf_apply_formula(0);
	}

	private Thf_apply_formulaContext thf_apply_formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Thf_apply_formulaContext _localctx = new Thf_apply_formulaContext(_ctx, _parentState);
		Thf_apply_formulaContext _prevctx = _localctx;
		int _startState = 62;
		enterRecursionRule(_localctx, 62, RULE_thf_apply_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(530);
			thf_unitary_formula();
			setState(531);
			match(App);
			setState(532);
			thf_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(539);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_apply_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_apply_formula);
					setState(534);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(535);
					match(App);
					setState(536);
					thf_unitary_formula();
					}
					} 
				}
				setState(541);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Thf_unitary_formulaContext extends ParserRuleContext {
		public Thf_quantified_formulaContext thf_quantified_formula() {
			return getRuleContext(Thf_quantified_formulaContext.class,0);
		}
		public Thf_unary_formulaContext thf_unary_formula() {
			return getRuleContext(Thf_unary_formulaContext.class,0);
		}
		public Thf_atomContext thf_atom() {
			return getRuleContext(Thf_atomContext.class,0);
		}
		public Thf_conditionalContext thf_conditional() {
			return getRuleContext(Thf_conditionalContext.class,0);
		}
		public Thf_letContext thf_let() {
			return getRuleContext(Thf_letContext.class,0);
		}
		public Thf_tupleContext thf_tuple() {
			return getRuleContext(Thf_tupleContext.class,0);
		}
		public Thf_logic_formulaContext thf_logic_formula() {
			return getRuleContext(Thf_logic_formulaContext.class,0);
		}
		public Thf_unitary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_unitary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_unitary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_unitary_formula(this);
		}
	}

	public final Thf_unitary_formulaContext thf_unitary_formula() throws RecognitionException {
		Thf_unitary_formulaContext _localctx = new Thf_unitary_formulaContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_thf_unitary_formula);
		try {
			setState(552);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(542);
				thf_quantified_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(543);
				thf_unary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(544);
				thf_atom();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(545);
				thf_conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(546);
				thf_let();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(547);
				thf_tuple();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(548);
				match(T__1);
				setState(549);
				thf_logic_formula();
				setState(550);
				match(T__2);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_quantified_formulaContext extends ParserRuleContext {
		public Thf_quantificationContext thf_quantification() {
			return getRuleContext(Thf_quantificationContext.class,0);
		}
		public Thf_unitary_formulaContext thf_unitary_formula() {
			return getRuleContext(Thf_unitary_formulaContext.class,0);
		}
		public Thf_quantified_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_quantified_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_quantified_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_quantified_formula(this);
		}
	}

	public final Thf_quantified_formulaContext thf_quantified_formula() throws RecognitionException {
		Thf_quantified_formulaContext _localctx = new Thf_quantified_formulaContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_thf_quantified_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
			thf_quantification();
			setState(555);
			thf_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_quantificationContext extends ParserRuleContext {
		public Thf_quantifierContext thf_quantifier() {
			return getRuleContext(Thf_quantifierContext.class,0);
		}
		public List<Thf_variableContext> thf_variable() {
			return getRuleContexts(Thf_variableContext.class);
		}
		public Thf_variableContext thf_variable(int i) {
			return getRuleContext(Thf_variableContext.class,i);
		}
		public Thf_quantificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_quantification; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_quantification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_quantification(this);
		}
	}

	public final Thf_quantificationContext thf_quantification() throws RecognitionException {
		Thf_quantificationContext _localctx = new Thf_quantificationContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_thf_quantification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
			thf_quantifier();
			setState(558);
			match(T__5);
			setState(559);
			thf_variable();
			setState(564);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(560);
				match(T__4);
				setState(561);
				thf_variable();
				}
				}
				setState(566);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(567);
			match(T__6);
			setState(568);
			match(T__11);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_variableContext extends ParserRuleContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Thf_top_level_typeContext thf_top_level_type() {
			return getRuleContext(Thf_top_level_typeContext.class,0);
		}
		public Thf_variableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_variable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_variable(this);
		}
	}

	public final Thf_variableContext thf_variable() throws RecognitionException {
		Thf_variableContext _localctx = new Thf_variableContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_thf_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(570);
			variable();
			setState(573);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(571);
				match(T__11);
				setState(572);
				thf_top_level_type();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_unary_formulaContext extends ParserRuleContext {
		public Thf_unary_connectiveContext thf_unary_connective() {
			return getRuleContext(Thf_unary_connectiveContext.class,0);
		}
		public Thf_unitary_formulaContext thf_unitary_formula() {
			return getRuleContext(Thf_unitary_formulaContext.class,0);
		}
		public Thf_unary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_unary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_unary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_unary_formula(this);
		}
	}

	public final Thf_unary_formulaContext thf_unary_formula() throws RecognitionException {
		Thf_unary_formulaContext _localctx = new Thf_unary_formulaContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_thf_unary_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(575);
			thf_unary_connective();
			setState(576);
			thf_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_atomContext extends ParserRuleContext {
		public Thf_functionContext thf_function() {
			return getRuleContext(Thf_functionContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Defined_termContext defined_term() {
			return getRuleContext(Defined_termContext.class,0);
		}
		public Thf_conn_termContext thf_conn_term() {
			return getRuleContext(Thf_conn_termContext.class,0);
		}
		public Thf_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_atom(this);
		}
	}

	public final Thf_atomContext thf_atom() throws RecognitionException {
		Thf_atomContext _localctx = new Thf_atomContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_thf_atom);
		try {
			setState(582);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Dollar_word:
			case Dollar_dollar_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(578);
				thf_function();
				}
				break;
			case Upper_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(579);
				variable();
				}
				break;
			case Real:
			case Rational:
			case Integer:
			case Distinct_object:
				enterOuterAlt(_localctx, 3);
				{
				setState(580);
				defined_term();
				}
				break;
			case Or:
			case And:
			case Iff:
			case Impl:
			case If:
			case Niff:
			case Nor:
			case Nand:
			case Not:
			case ForallComb:
			case Infix_inequality:
			case Infix_equality:
			case ExistsComb:
			case ChoiceComb:
			case DescriptionComb:
			case EqComb:
			case Assignment:
				enterOuterAlt(_localctx, 4);
				{
				setState(581);
				thf_conn_term();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_functionContext extends ParserRuleContext {
		public Thf_plain_termContext thf_plain_term() {
			return getRuleContext(Thf_plain_termContext.class,0);
		}
		public Thf_defined_termContext thf_defined_term() {
			return getRuleContext(Thf_defined_termContext.class,0);
		}
		public Thf_system_termContext thf_system_term() {
			return getRuleContext(Thf_system_termContext.class,0);
		}
		public Thf_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_function(this);
		}
	}

	public final Thf_functionContext thf_function() throws RecognitionException {
		Thf_functionContext _localctx = new Thf_functionContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_thf_function);
		try {
			setState(587);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(584);
				thf_plain_term();
				}
				break;
			case Dollar_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(585);
				thf_defined_term();
				}
				break;
			case Dollar_dollar_word:
				enterOuterAlt(_localctx, 3);
				{
				setState(586);
				thf_system_term();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_plain_termContext extends ParserRuleContext {
		public FunctorContext functor() {
			return getRuleContext(FunctorContext.class,0);
		}
		public Thf_argumentsContext thf_arguments() {
			return getRuleContext(Thf_argumentsContext.class,0);
		}
		public Thf_plain_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_plain_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_plain_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_plain_term(this);
		}
	}

	public final Thf_plain_termContext thf_plain_term() throws RecognitionException {
		Thf_plain_termContext _localctx = new Thf_plain_termContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_thf_plain_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589);
			functor();
			setState(594);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(590);
				match(T__1);
				setState(591);
				thf_arguments();
				setState(592);
				match(T__2);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_defined_termContext extends ParserRuleContext {
		public Defined_functorContext defined_functor() {
			return getRuleContext(Defined_functorContext.class,0);
		}
		public Thf_argumentsContext thf_arguments() {
			return getRuleContext(Thf_argumentsContext.class,0);
		}
		public Thf_defined_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_defined_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_defined_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_defined_term(this);
		}
	}

	public final Thf_defined_termContext thf_defined_term() throws RecognitionException {
		Thf_defined_termContext _localctx = new Thf_defined_termContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_thf_defined_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(596);
			defined_functor();
			setState(601);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				setState(597);
				match(T__1);
				setState(598);
				thf_arguments();
				setState(599);
				match(T__2);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_system_termContext extends ParserRuleContext {
		public System_functorContext system_functor() {
			return getRuleContext(System_functorContext.class,0);
		}
		public Thf_argumentsContext thf_arguments() {
			return getRuleContext(Thf_argumentsContext.class,0);
		}
		public Thf_system_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_system_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_system_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_system_term(this);
		}
	}

	public final Thf_system_termContext thf_system_term() throws RecognitionException {
		Thf_system_termContext _localctx = new Thf_system_termContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_thf_system_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			system_functor();
			setState(608);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				setState(604);
				match(T__1);
				setState(605);
				thf_arguments();
				setState(606);
				match(T__2);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_conn_termContext extends ParserRuleContext {
		public Thf_pair_connectiveContext thf_pair_connective() {
			return getRuleContext(Thf_pair_connectiveContext.class,0);
		}
		public Assoc_connectiveContext assoc_connective() {
			return getRuleContext(Assoc_connectiveContext.class,0);
		}
		public Thf_unary_connectiveContext thf_unary_connective() {
			return getRuleContext(Thf_unary_connectiveContext.class,0);
		}
		public Thf_conn_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_conn_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_conn_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_conn_term(this);
		}
	}

	public final Thf_conn_termContext thf_conn_term() throws RecognitionException {
		Thf_conn_termContext _localctx = new Thf_conn_termContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_thf_conn_term);
		try {
			setState(613);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Iff:
			case Impl:
			case If:
			case Niff:
			case Nor:
			case Nand:
			case Infix_inequality:
			case Infix_equality:
			case Assignment:
				enterOuterAlt(_localctx, 1);
				{
				setState(610);
				thf_pair_connective();
				}
				break;
			case Or:
			case And:
				enterOuterAlt(_localctx, 2);
				{
				setState(611);
				assoc_connective();
				}
				break;
			case Not:
			case ForallComb:
			case ExistsComb:
			case ChoiceComb:
			case DescriptionComb:
			case EqComb:
				enterOuterAlt(_localctx, 3);
				{
				setState(612);
				thf_unary_connective();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_conditionalContext extends ParserRuleContext {
		public List<Thf_logic_formulaContext> thf_logic_formula() {
			return getRuleContexts(Thf_logic_formulaContext.class);
		}
		public Thf_logic_formulaContext thf_logic_formula(int i) {
			return getRuleContext(Thf_logic_formulaContext.class,i);
		}
		public Thf_conditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_conditional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_conditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_conditional(this);
		}
	}

	public final Thf_conditionalContext thf_conditional() throws RecognitionException {
		Thf_conditionalContext _localctx = new Thf_conditionalContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_thf_conditional);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			match(T__18);
			setState(616);
			thf_logic_formula();
			setState(617);
			match(T__4);
			setState(618);
			thf_logic_formula();
			setState(619);
			match(T__4);
			setState(620);
			thf_logic_formula();
			setState(621);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_letContext extends ParserRuleContext {
		public Thf_unitary_formulaContext thf_unitary_formula() {
			return getRuleContext(Thf_unitary_formulaContext.class,0);
		}
		public Thf_formulaContext thf_formula() {
			return getRuleContext(Thf_formulaContext.class,0);
		}
		public Thf_letContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_let; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_let(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_let(this);
		}
	}

	public final Thf_letContext thf_let() throws RecognitionException {
		Thf_letContext _localctx = new Thf_letContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_thf_let);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(623);
			match(T__19);
			setState(624);
			thf_unitary_formula();
			setState(625);
			match(T__4);
			setState(626);
			thf_formula();
			setState(627);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_argumentsContext extends ParserRuleContext {
		public Thf_formula_listContext thf_formula_list() {
			return getRuleContext(Thf_formula_listContext.class,0);
		}
		public Thf_argumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_arguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_arguments(this);
		}
	}

	public final Thf_argumentsContext thf_arguments() throws RecognitionException {
		Thf_argumentsContext _localctx = new Thf_argumentsContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_thf_arguments);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(629);
			thf_formula_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_type_formulaContext extends ParserRuleContext {
		public Thf_typeable_formulaContext thf_typeable_formula() {
			return getRuleContext(Thf_typeable_formulaContext.class,0);
		}
		public Thf_top_level_typeContext thf_top_level_type() {
			return getRuleContext(Thf_top_level_typeContext.class,0);
		}
		public Thf_type_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_type_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_type_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_type_formula(this);
		}
	}

	public final Thf_type_formulaContext thf_type_formula() throws RecognitionException {
		Thf_type_formulaContext _localctx = new Thf_type_formulaContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_thf_type_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(631);
			thf_typeable_formula();
			setState(632);
			match(T__11);
			setState(633);
			thf_top_level_type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_typeable_formulaContext extends ParserRuleContext {
		public Thf_atomContext thf_atom() {
			return getRuleContext(Thf_atomContext.class,0);
		}
		public Thf_logic_formulaContext thf_logic_formula() {
			return getRuleContext(Thf_logic_formulaContext.class,0);
		}
		public Thf_typeable_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_typeable_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_typeable_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_typeable_formula(this);
		}
	}

	public final Thf_typeable_formulaContext thf_typeable_formula() throws RecognitionException {
		Thf_typeable_formulaContext _localctx = new Thf_typeable_formulaContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_thf_typeable_formula);
		try {
			setState(640);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Or:
			case And:
			case Iff:
			case Impl:
			case If:
			case Niff:
			case Nor:
			case Nand:
			case Not:
			case ForallComb:
			case Infix_inequality:
			case Infix_equality:
			case ExistsComb:
			case ChoiceComb:
			case DescriptionComb:
			case EqComb:
			case Assignment:
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Dollar_dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
			case Distinct_object:
				enterOuterAlt(_localctx, 1);
				{
				setState(635);
				thf_atom();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(636);
				match(T__1);
				setState(637);
				thf_logic_formula();
				setState(638);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_subtypeContext extends ParserRuleContext {
		public List<Thf_atomContext> thf_atom() {
			return getRuleContexts(Thf_atomContext.class);
		}
		public Thf_atomContext thf_atom(int i) {
			return getRuleContext(Thf_atomContext.class,i);
		}
		public Thf_subtypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_subtype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_subtype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_subtype(this);
		}
	}

	public final Thf_subtypeContext thf_subtype() throws RecognitionException {
		Thf_subtypeContext _localctx = new Thf_subtypeContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_thf_subtype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(642);
			thf_atom();
			setState(643);
			match(T__20);
			setState(644);
			thf_atom();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_top_level_typeContext extends ParserRuleContext {
		public Thf_unitary_typeContext thf_unitary_type() {
			return getRuleContext(Thf_unitary_typeContext.class,0);
		}
		public Thf_mapping_typeContext thf_mapping_type() {
			return getRuleContext(Thf_mapping_typeContext.class,0);
		}
		public Thf_apply_typeContext thf_apply_type() {
			return getRuleContext(Thf_apply_typeContext.class,0);
		}
		public Thf_top_level_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_top_level_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_top_level_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_top_level_type(this);
		}
	}

	public final Thf_top_level_typeContext thf_top_level_type() throws RecognitionException {
		Thf_top_level_typeContext _localctx = new Thf_top_level_typeContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_thf_top_level_type);
		try {
			setState(649);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(646);
				thf_unitary_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(647);
				thf_mapping_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(648);
				thf_apply_type();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_unitary_typeContext extends ParserRuleContext {
		public Thf_unitary_formulaContext thf_unitary_formula() {
			return getRuleContext(Thf_unitary_formulaContext.class,0);
		}
		public Thf_unitary_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_unitary_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_unitary_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_unitary_type(this);
		}
	}

	public final Thf_unitary_typeContext thf_unitary_type() throws RecognitionException {
		Thf_unitary_typeContext _localctx = new Thf_unitary_typeContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_thf_unitary_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651);
			thf_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_apply_typeContext extends ParserRuleContext {
		public Thf_apply_formulaContext thf_apply_formula() {
			return getRuleContext(Thf_apply_formulaContext.class,0);
		}
		public Thf_apply_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_apply_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_apply_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_apply_type(this);
		}
	}

	public final Thf_apply_typeContext thf_apply_type() throws RecognitionException {
		Thf_apply_typeContext _localctx = new Thf_apply_typeContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_thf_apply_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(653);
			thf_apply_formula(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_binary_typeContext extends ParserRuleContext {
		public Thf_mapping_typeContext thf_mapping_type() {
			return getRuleContext(Thf_mapping_typeContext.class,0);
		}
		public Thf_xprod_typeContext thf_xprod_type() {
			return getRuleContext(Thf_xprod_typeContext.class,0);
		}
		public Thf_union_typeContext thf_union_type() {
			return getRuleContext(Thf_union_typeContext.class,0);
		}
		public Thf_binary_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_binary_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_binary_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_binary_type(this);
		}
	}

	public final Thf_binary_typeContext thf_binary_type() throws RecognitionException {
		Thf_binary_typeContext _localctx = new Thf_binary_typeContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_thf_binary_type);
		try {
			setState(658);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(655);
				thf_mapping_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(656);
				thf_xprod_type(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(657);
				thf_union_type(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_mapping_typeContext extends ParserRuleContext {
		public List<Thf_unitary_typeContext> thf_unitary_type() {
			return getRuleContexts(Thf_unitary_typeContext.class);
		}
		public Thf_unitary_typeContext thf_unitary_type(int i) {
			return getRuleContext(Thf_unitary_typeContext.class,i);
		}
		public Thf_mapping_typeContext thf_mapping_type() {
			return getRuleContext(Thf_mapping_typeContext.class,0);
		}
		public Thf_mapping_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_mapping_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_mapping_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_mapping_type(this);
		}
	}

	public final Thf_mapping_typeContext thf_mapping_type() throws RecognitionException {
		Thf_mapping_typeContext _localctx = new Thf_mapping_typeContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_thf_mapping_type);
		try {
			setState(668);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(660);
				thf_unitary_type();
				setState(661);
				match(T__21);
				setState(662);
				thf_unitary_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(664);
				thf_unitary_type();
				setState(665);
				match(T__21);
				setState(666);
				thf_mapping_type();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_xprod_typeContext extends ParserRuleContext {
		public List<Thf_unitary_typeContext> thf_unitary_type() {
			return getRuleContexts(Thf_unitary_typeContext.class);
		}
		public Thf_unitary_typeContext thf_unitary_type(int i) {
			return getRuleContext(Thf_unitary_typeContext.class,i);
		}
		public Thf_xprod_typeContext thf_xprod_type() {
			return getRuleContext(Thf_xprod_typeContext.class,0);
		}
		public Thf_xprod_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_xprod_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_xprod_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_xprod_type(this);
		}
	}

	public final Thf_xprod_typeContext thf_xprod_type() throws RecognitionException {
		return thf_xprod_type(0);
	}

	private Thf_xprod_typeContext thf_xprod_type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Thf_xprod_typeContext _localctx = new Thf_xprod_typeContext(_ctx, _parentState);
		Thf_xprod_typeContext _prevctx = _localctx;
		int _startState = 108;
		enterRecursionRule(_localctx, 108, RULE_thf_xprod_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(671);
			thf_unitary_type();
			setState(672);
			match(T__22);
			setState(673);
			thf_unitary_type();
			}
			_ctx.stop = _input.LT(-1);
			setState(680);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_xprod_typeContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_xprod_type);
					setState(675);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(676);
					match(T__22);
					setState(677);
					thf_unitary_type();
					}
					} 
				}
				setState(682);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Thf_union_typeContext extends ParserRuleContext {
		public List<Thf_unitary_typeContext> thf_unitary_type() {
			return getRuleContexts(Thf_unitary_typeContext.class);
		}
		public Thf_unitary_typeContext thf_unitary_type(int i) {
			return getRuleContext(Thf_unitary_typeContext.class,i);
		}
		public Thf_union_typeContext thf_union_type() {
			return getRuleContext(Thf_union_typeContext.class,0);
		}
		public Thf_union_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_union_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_union_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_union_type(this);
		}
	}

	public final Thf_union_typeContext thf_union_type() throws RecognitionException {
		return thf_union_type(0);
	}

	private Thf_union_typeContext thf_union_type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Thf_union_typeContext _localctx = new Thf_union_typeContext(_ctx, _parentState);
		Thf_union_typeContext _prevctx = _localctx;
		int _startState = 110;
		enterRecursionRule(_localctx, 110, RULE_thf_union_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(684);
			thf_unitary_type();
			setState(685);
			match(T__23);
			setState(686);
			thf_unitary_type();
			}
			_ctx.stop = _input.LT(-1);
			setState(693);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_union_typeContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_union_type);
					setState(688);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(689);
					match(T__23);
					setState(690);
					thf_unitary_type();
					}
					} 
				}
				setState(695);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,38,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Thf_sequentContext extends ParserRuleContext {
		public List<Thf_tupleContext> thf_tuple() {
			return getRuleContexts(Thf_tupleContext.class);
		}
		public Thf_tupleContext thf_tuple(int i) {
			return getRuleContext(Thf_tupleContext.class,i);
		}
		public Thf_sequentContext thf_sequent() {
			return getRuleContext(Thf_sequentContext.class,0);
		}
		public Thf_sequentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_sequent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_sequent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_sequent(this);
		}
	}

	public final Thf_sequentContext thf_sequent() throws RecognitionException {
		Thf_sequentContext _localctx = new Thf_sequentContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_thf_sequent);
		try {
			setState(704);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
			case T__17:
			case T__25:
			case T__26:
				enterOuterAlt(_localctx, 1);
				{
				setState(696);
				thf_tuple();
				setState(697);
				match(T__24);
				setState(698);
				thf_tuple();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(700);
				match(T__1);
				setState(701);
				thf_sequent();
				setState(702);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_tupleContext extends ParserRuleContext {
		public Thf_formula_listContext thf_formula_list() {
			return getRuleContext(Thf_formula_listContext.class,0);
		}
		public Thf_tupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_tuple; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_tuple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_tuple(this);
		}
	}

	public final Thf_tupleContext thf_tuple() throws RecognitionException {
		Thf_tupleContext _localctx = new Thf_tupleContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_thf_tuple);
		try {
			setState(716);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(706);
				match(T__17);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(707);
				match(T__5);
				setState(708);
				thf_formula_list();
				setState(709);
				match(T__6);
				}
				break;
			case T__25:
				enterOuterAlt(_localctx, 3);
				{
				setState(711);
				match(T__25);
				}
				break;
			case T__26:
				enterOuterAlt(_localctx, 4);
				{
				setState(712);
				match(T__26);
				setState(713);
				thf_formula_list();
				setState(714);
				match(T__27);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_formula_listContext extends ParserRuleContext {
		public List<Thf_logic_formulaContext> thf_logic_formula() {
			return getRuleContexts(Thf_logic_formulaContext.class);
		}
		public Thf_logic_formulaContext thf_logic_formula(int i) {
			return getRuleContext(Thf_logic_formulaContext.class,i);
		}
		public Thf_formula_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_formula_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_formula_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_formula_list(this);
		}
	}

	public final Thf_formula_listContext thf_formula_list() throws RecognitionException {
		Thf_formula_listContext _localctx = new Thf_formula_listContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_thf_formula_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(718);
			thf_logic_formula();
			setState(723);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(719);
				match(T__4);
				setState(720);
				thf_logic_formula();
				}
				}
				setState(725);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_formulaContext extends ParserRuleContext {
		public Tff_logic_formulaContext tff_logic_formula() {
			return getRuleContext(Tff_logic_formulaContext.class,0);
		}
		public Tff_typed_atomContext tff_typed_atom() {
			return getRuleContext(Tff_typed_atomContext.class,0);
		}
		public Tff_sequentContext tff_sequent() {
			return getRuleContext(Tff_sequentContext.class,0);
		}
		public Tff_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_formula(this);
		}
	}

	public final Tff_formulaContext tff_formula() throws RecognitionException {
		Tff_formulaContext _localctx = new Tff_formulaContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_tff_formula);
		try {
			setState(729);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(726);
				tff_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(727);
				tff_typed_atom();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(728);
				tff_sequent();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_logic_formulaContext extends ParserRuleContext {
		public Tff_binary_formulaContext tff_binary_formula() {
			return getRuleContext(Tff_binary_formulaContext.class,0);
		}
		public Tff_unitary_formulaContext tff_unitary_formula() {
			return getRuleContext(Tff_unitary_formulaContext.class,0);
		}
		public Tff_logic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_logic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_logic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_logic_formula(this);
		}
	}

	public final Tff_logic_formulaContext tff_logic_formula() throws RecognitionException {
		Tff_logic_formulaContext _localctx = new Tff_logic_formulaContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_tff_logic_formula);
		try {
			setState(733);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(731);
				tff_binary_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(732);
				tff_unitary_formula();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_binary_formulaContext extends ParserRuleContext {
		public Tff_binary_nonassocContext tff_binary_nonassoc() {
			return getRuleContext(Tff_binary_nonassocContext.class,0);
		}
		public Tff_binary_assocContext tff_binary_assoc() {
			return getRuleContext(Tff_binary_assocContext.class,0);
		}
		public Tff_binary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_binary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_binary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_binary_formula(this);
		}
	}

	public final Tff_binary_formulaContext tff_binary_formula() throws RecognitionException {
		Tff_binary_formulaContext _localctx = new Tff_binary_formulaContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_tff_binary_formula);
		try {
			setState(737);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(735);
				tff_binary_nonassoc();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(736);
				tff_binary_assoc();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_binary_nonassocContext extends ParserRuleContext {
		public List<Tff_unitary_formulaContext> tff_unitary_formula() {
			return getRuleContexts(Tff_unitary_formulaContext.class);
		}
		public Tff_unitary_formulaContext tff_unitary_formula(int i) {
			return getRuleContext(Tff_unitary_formulaContext.class,i);
		}
		public Binary_connectiveContext binary_connective() {
			return getRuleContext(Binary_connectiveContext.class,0);
		}
		public Tff_binary_nonassocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_binary_nonassoc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_binary_nonassoc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_binary_nonassoc(this);
		}
	}

	public final Tff_binary_nonassocContext tff_binary_nonassoc() throws RecognitionException {
		Tff_binary_nonassocContext _localctx = new Tff_binary_nonassocContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_tff_binary_nonassoc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(739);
			tff_unitary_formula();
			setState(740);
			binary_connective();
			setState(741);
			tff_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_binary_assocContext extends ParserRuleContext {
		public Tff_or_formulaContext tff_or_formula() {
			return getRuleContext(Tff_or_formulaContext.class,0);
		}
		public Tff_and_formulaContext tff_and_formula() {
			return getRuleContext(Tff_and_formulaContext.class,0);
		}
		public Tff_binary_assocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_binary_assoc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_binary_assoc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_binary_assoc(this);
		}
	}

	public final Tff_binary_assocContext tff_binary_assoc() throws RecognitionException {
		Tff_binary_assocContext _localctx = new Tff_binary_assocContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_tff_binary_assoc);
		try {
			setState(745);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(743);
				tff_or_formula(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(744);
				tff_and_formula(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_or_formulaContext extends ParserRuleContext {
		public List<Tff_unitary_formulaContext> tff_unitary_formula() {
			return getRuleContexts(Tff_unitary_formulaContext.class);
		}
		public Tff_unitary_formulaContext tff_unitary_formula(int i) {
			return getRuleContext(Tff_unitary_formulaContext.class,i);
		}
		public TerminalNode Or() { return getToken(tptpParser.Or, 0); }
		public Tff_or_formulaContext tff_or_formula() {
			return getRuleContext(Tff_or_formulaContext.class,0);
		}
		public Tff_or_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_or_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_or_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_or_formula(this);
		}
	}

	public final Tff_or_formulaContext tff_or_formula() throws RecognitionException {
		return tff_or_formula(0);
	}

	private Tff_or_formulaContext tff_or_formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Tff_or_formulaContext _localctx = new Tff_or_formulaContext(_ctx, _parentState);
		Tff_or_formulaContext _prevctx = _localctx;
		int _startState = 128;
		enterRecursionRule(_localctx, 128, RULE_tff_or_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(748);
			tff_unitary_formula();
			setState(749);
			match(Or);
			setState(750);
			tff_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(757);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Tff_or_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_tff_or_formula);
					setState(752);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(753);
					match(Or);
					setState(754);
					tff_unitary_formula();
					}
					} 
				}
				setState(759);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Tff_and_formulaContext extends ParserRuleContext {
		public List<Tff_unitary_formulaContext> tff_unitary_formula() {
			return getRuleContexts(Tff_unitary_formulaContext.class);
		}
		public Tff_unitary_formulaContext tff_unitary_formula(int i) {
			return getRuleContext(Tff_unitary_formulaContext.class,i);
		}
		public TerminalNode And() { return getToken(tptpParser.And, 0); }
		public Tff_and_formulaContext tff_and_formula() {
			return getRuleContext(Tff_and_formulaContext.class,0);
		}
		public Tff_and_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_and_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_and_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_and_formula(this);
		}
	}

	public final Tff_and_formulaContext tff_and_formula() throws RecognitionException {
		return tff_and_formula(0);
	}

	private Tff_and_formulaContext tff_and_formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Tff_and_formulaContext _localctx = new Tff_and_formulaContext(_ctx, _parentState);
		Tff_and_formulaContext _prevctx = _localctx;
		int _startState = 130;
		enterRecursionRule(_localctx, 130, RULE_tff_and_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(761);
			tff_unitary_formula();
			setState(762);
			match(And);
			setState(763);
			tff_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(770);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Tff_and_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_tff_and_formula);
					setState(765);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(766);
					match(And);
					setState(767);
					tff_unitary_formula();
					}
					} 
				}
				setState(772);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Tff_unitary_formulaContext extends ParserRuleContext {
		public Tff_quantified_formulaContext tff_quantified_formula() {
			return getRuleContext(Tff_quantified_formulaContext.class,0);
		}
		public Tff_unary_formulaContext tff_unary_formula() {
			return getRuleContext(Tff_unary_formulaContext.class,0);
		}
		public Tff_atomic_formulaContext tff_atomic_formula() {
			return getRuleContext(Tff_atomic_formulaContext.class,0);
		}
		public Tff_conditionalContext tff_conditional() {
			return getRuleContext(Tff_conditionalContext.class,0);
		}
		public Tff_letContext tff_let() {
			return getRuleContext(Tff_letContext.class,0);
		}
		public Tff_logic_formulaContext tff_logic_formula() {
			return getRuleContext(Tff_logic_formulaContext.class,0);
		}
		public Tff_unitary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_unitary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_unitary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_unitary_formula(this);
		}
	}

	public final Tff_unitary_formulaContext tff_unitary_formula() throws RecognitionException {
		Tff_unitary_formulaContext _localctx = new Tff_unitary_formulaContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_tff_unitary_formula);
		try {
			setState(782);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(773);
				tff_quantified_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(774);
				tff_unary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(775);
				tff_atomic_formula();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(776);
				tff_conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(777);
				tff_let();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(778);
				match(T__1);
				setState(779);
				tff_logic_formula();
				setState(780);
				match(T__2);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_quantified_formulaContext extends ParserRuleContext {
		public Fof_quantifierContext fof_quantifier() {
			return getRuleContext(Fof_quantifierContext.class,0);
		}
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
		}
		public Tff_unitary_formulaContext tff_unitary_formula() {
			return getRuleContext(Tff_unitary_formulaContext.class,0);
		}
		public Tff_quantified_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_quantified_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_quantified_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_quantified_formula(this);
		}
	}

	public final Tff_quantified_formulaContext tff_quantified_formula() throws RecognitionException {
		Tff_quantified_formulaContext _localctx = new Tff_quantified_formulaContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_tff_quantified_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(784);
			fof_quantifier();
			setState(785);
			match(T__5);
			setState(786);
			tff_variable_list();
			setState(787);
			match(T__6);
			setState(788);
			match(T__11);
			setState(789);
			tff_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_variable_listContext extends ParserRuleContext {
		public List<Tff_variableContext> tff_variable() {
			return getRuleContexts(Tff_variableContext.class);
		}
		public Tff_variableContext tff_variable(int i) {
			return getRuleContext(Tff_variableContext.class,i);
		}
		public Tff_variable_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_variable_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_variable_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_variable_list(this);
		}
	}

	public final Tff_variable_listContext tff_variable_list() throws RecognitionException {
		Tff_variable_listContext _localctx = new Tff_variable_listContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_tff_variable_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(791);
			tff_variable();
			setState(796);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(792);
				match(T__4);
				setState(793);
				tff_variable();
				}
				}
				setState(798);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_variableContext extends ParserRuleContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_variableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_variable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_variable(this);
		}
	}

	public final Tff_variableContext tff_variable() throws RecognitionException {
		Tff_variableContext _localctx = new Tff_variableContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_tff_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(799);
			variable();
			setState(802);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(800);
				match(T__11);
				setState(801);
				tff_atomic_type();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_unary_formulaContext extends ParserRuleContext {
		public Unary_connectiveContext unary_connective() {
			return getRuleContext(Unary_connectiveContext.class,0);
		}
		public Tff_unitary_formulaContext tff_unitary_formula() {
			return getRuleContext(Tff_unitary_formulaContext.class,0);
		}
		public Fof_infix_unaryContext fof_infix_unary() {
			return getRuleContext(Fof_infix_unaryContext.class,0);
		}
		public Tff_unary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_unary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_unary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_unary_formula(this);
		}
	}

	public final Tff_unary_formulaContext tff_unary_formula() throws RecognitionException {
		Tff_unary_formulaContext _localctx = new Tff_unary_formulaContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_tff_unary_formula);
		try {
			setState(808);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(804);
				unary_connective();
				setState(805);
				tff_unitary_formula();
				}
				break;
			case T__25:
			case T__26:
			case T__32:
			case T__33:
			case T__34:
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Dollar_dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
			case Distinct_object:
				enterOuterAlt(_localctx, 2);
				{
				setState(807);
				fof_infix_unary();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_atomic_formulaContext extends ParserRuleContext {
		public Fof_atomic_formulaContext fof_atomic_formula() {
			return getRuleContext(Fof_atomic_formulaContext.class,0);
		}
		public Tff_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_atomic_formula(this);
		}
	}

	public final Tff_atomic_formulaContext tff_atomic_formula() throws RecognitionException {
		Tff_atomic_formulaContext _localctx = new Tff_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_tff_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(810);
			fof_atomic_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_conditionalContext extends ParserRuleContext {
		public List<Tff_logic_formulaContext> tff_logic_formula() {
			return getRuleContexts(Tff_logic_formulaContext.class);
		}
		public Tff_logic_formulaContext tff_logic_formula(int i) {
			return getRuleContext(Tff_logic_formulaContext.class,i);
		}
		public Tff_conditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_conditional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_conditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_conditional(this);
		}
	}

	public final Tff_conditionalContext tff_conditional() throws RecognitionException {
		Tff_conditionalContext _localctx = new Tff_conditionalContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_tff_conditional);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(812);
			match(T__28);
			setState(813);
			tff_logic_formula();
			setState(814);
			match(T__4);
			setState(815);
			tff_logic_formula();
			setState(816);
			match(T__4);
			setState(817);
			tff_logic_formula();
			setState(818);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_letContext extends ParserRuleContext {
		public Tff_let_term_defnsContext tff_let_term_defns() {
			return getRuleContext(Tff_let_term_defnsContext.class,0);
		}
		public Tff_formulaContext tff_formula() {
			return getRuleContext(Tff_formulaContext.class,0);
		}
		public Tff_let_formula_defnsContext tff_let_formula_defns() {
			return getRuleContext(Tff_let_formula_defnsContext.class,0);
		}
		public Tff_letContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let(this);
		}
	}

	public final Tff_letContext tff_let() throws RecognitionException {
		Tff_letContext _localctx = new Tff_letContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_tff_let);
		try {
			setState(832);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__29:
				enterOuterAlt(_localctx, 1);
				{
				setState(820);
				match(T__29);
				setState(821);
				tff_let_term_defns();
				setState(822);
				match(T__4);
				setState(823);
				tff_formula();
				setState(824);
				match(T__2);
				}
				break;
			case T__30:
				enterOuterAlt(_localctx, 2);
				{
				setState(826);
				match(T__30);
				setState(827);
				tff_let_formula_defns();
				setState(828);
				match(T__4);
				setState(829);
				tff_formula();
				setState(830);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_term_defnsContext extends ParserRuleContext {
		public Tff_let_term_defnContext tff_let_term_defn() {
			return getRuleContext(Tff_let_term_defnContext.class,0);
		}
		public Tff_let_term_listContext tff_let_term_list() {
			return getRuleContext(Tff_let_term_listContext.class,0);
		}
		public Tff_let_term_defnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_term_defns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_term_defns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_term_defns(this);
		}
	}

	public final Tff_let_term_defnsContext tff_let_term_defns() throws RecognitionException {
		Tff_let_term_defnsContext _localctx = new Tff_let_term_defnsContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_tff_let_term_defns);
		try {
			setState(839);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case Forall:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(834);
				tff_let_term_defn();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(835);
				match(T__5);
				setState(836);
				tff_let_term_list();
				setState(837);
				match(T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_term_listContext extends ParserRuleContext {
		public List<Tff_let_term_defnContext> tff_let_term_defn() {
			return getRuleContexts(Tff_let_term_defnContext.class);
		}
		public Tff_let_term_defnContext tff_let_term_defn(int i) {
			return getRuleContext(Tff_let_term_defnContext.class,i);
		}
		public Tff_let_term_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_term_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_term_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_term_list(this);
		}
	}

	public final Tff_let_term_listContext tff_let_term_list() throws RecognitionException {
		Tff_let_term_listContext _localctx = new Tff_let_term_listContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_tff_let_term_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(841);
			tff_let_term_defn();
			setState(846);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(842);
				match(T__4);
				setState(843);
				tff_let_term_defn();
				}
				}
				setState(848);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_term_defnContext extends ParserRuleContext {
		public TerminalNode Forall() { return getToken(tptpParser.Forall, 0); }
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
		}
		public Tff_let_term_bindingContext tff_let_term_binding() {
			return getRuleContext(Tff_let_term_bindingContext.class,0);
		}
		public Tff_let_term_defnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_term_defn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_term_defn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_term_defn(this);
		}
	}

	public final Tff_let_term_defnContext tff_let_term_defn() throws RecognitionException {
		Tff_let_term_defnContext _localctx = new Tff_let_term_defnContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_tff_let_term_defn);
		try {
			setState(857);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Forall:
				enterOuterAlt(_localctx, 1);
				{
				setState(849);
				match(Forall);
				setState(850);
				match(T__5);
				setState(851);
				tff_variable_list();
				setState(852);
				match(T__6);
				setState(853);
				match(T__11);
				setState(854);
				tff_let_term_binding();
				}
				break;
			case T__1:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 2);
				{
				setState(856);
				tff_let_term_binding();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_term_bindingContext extends ParserRuleContext {
		public Fof_plain_termContext fof_plain_term() {
			return getRuleContext(Fof_plain_termContext.class,0);
		}
		public TerminalNode Infix_equality() { return getToken(tptpParser.Infix_equality, 0); }
		public Fof_termContext fof_term() {
			return getRuleContext(Fof_termContext.class,0);
		}
		public Tff_let_term_bindingContext tff_let_term_binding() {
			return getRuleContext(Tff_let_term_bindingContext.class,0);
		}
		public Tff_let_term_bindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_term_binding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_term_binding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_term_binding(this);
		}
	}

	public final Tff_let_term_bindingContext tff_let_term_binding() throws RecognitionException {
		Tff_let_term_bindingContext _localctx = new Tff_let_term_bindingContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_tff_let_term_binding);
		try {
			setState(867);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(859);
				fof_plain_term();
				setState(860);
				match(Infix_equality);
				setState(861);
				fof_term();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(863);
				match(T__1);
				setState(864);
				tff_let_term_binding();
				setState(865);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_formula_defnsContext extends ParserRuleContext {
		public Tff_let_formula_defnContext tff_let_formula_defn() {
			return getRuleContext(Tff_let_formula_defnContext.class,0);
		}
		public Tff_let_formula_listContext tff_let_formula_list() {
			return getRuleContext(Tff_let_formula_listContext.class,0);
		}
		public Tff_let_formula_defnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_formula_defns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_formula_defns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_formula_defns(this);
		}
	}

	public final Tff_let_formula_defnsContext tff_let_formula_defns() throws RecognitionException {
		Tff_let_formula_defnsContext _localctx = new Tff_let_formula_defnsContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_tff_let_formula_defns);
		try {
			setState(874);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case Forall:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(869);
				tff_let_formula_defn();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(870);
				match(T__5);
				setState(871);
				tff_let_formula_list();
				setState(872);
				match(T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_formula_listContext extends ParserRuleContext {
		public List<Tff_let_formula_defnContext> tff_let_formula_defn() {
			return getRuleContexts(Tff_let_formula_defnContext.class);
		}
		public Tff_let_formula_defnContext tff_let_formula_defn(int i) {
			return getRuleContext(Tff_let_formula_defnContext.class,i);
		}
		public Tff_let_formula_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_formula_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_formula_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_formula_list(this);
		}
	}

	public final Tff_let_formula_listContext tff_let_formula_list() throws RecognitionException {
		Tff_let_formula_listContext _localctx = new Tff_let_formula_listContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_tff_let_formula_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(876);
			tff_let_formula_defn();
			setState(881);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(877);
				match(T__4);
				setState(878);
				tff_let_formula_defn();
				}
				}
				setState(883);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_formula_defnContext extends ParserRuleContext {
		public TerminalNode Forall() { return getToken(tptpParser.Forall, 0); }
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
		}
		public Tff_let_formula_bindingContext tff_let_formula_binding() {
			return getRuleContext(Tff_let_formula_bindingContext.class,0);
		}
		public Tff_let_formula_defnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_formula_defn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_formula_defn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_formula_defn(this);
		}
	}

	public final Tff_let_formula_defnContext tff_let_formula_defn() throws RecognitionException {
		Tff_let_formula_defnContext _localctx = new Tff_let_formula_defnContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_tff_let_formula_defn);
		try {
			setState(892);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Forall:
				enterOuterAlt(_localctx, 1);
				{
				setState(884);
				match(Forall);
				setState(885);
				match(T__5);
				setState(886);
				tff_variable_list();
				setState(887);
				match(T__6);
				setState(888);
				match(T__11);
				setState(889);
				tff_let_formula_binding();
				}
				break;
			case T__1:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 2);
				{
				setState(891);
				tff_let_formula_binding();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_formula_bindingContext extends ParserRuleContext {
		public Fof_plain_atomic_formulaContext fof_plain_atomic_formula() {
			return getRuleContext(Fof_plain_atomic_formulaContext.class,0);
		}
		public TerminalNode Iff() { return getToken(tptpParser.Iff, 0); }
		public Tff_unitary_formulaContext tff_unitary_formula() {
			return getRuleContext(Tff_unitary_formulaContext.class,0);
		}
		public Tff_let_formula_bindingContext tff_let_formula_binding() {
			return getRuleContext(Tff_let_formula_bindingContext.class,0);
		}
		public Tff_let_formula_bindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_formula_binding; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_formula_binding(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_formula_binding(this);
		}
	}

	public final Tff_let_formula_bindingContext tff_let_formula_binding() throws RecognitionException {
		Tff_let_formula_bindingContext _localctx = new Tff_let_formula_bindingContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_tff_let_formula_binding);
		try {
			setState(902);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(894);
				fof_plain_atomic_formula();
				setState(895);
				match(Iff);
				setState(896);
				tff_unitary_formula();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(898);
				match(T__1);
				setState(899);
				tff_let_formula_binding();
				setState(900);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_sequentContext extends ParserRuleContext {
		public List<Tff_formula_tupleContext> tff_formula_tuple() {
			return getRuleContexts(Tff_formula_tupleContext.class);
		}
		public Tff_formula_tupleContext tff_formula_tuple(int i) {
			return getRuleContext(Tff_formula_tupleContext.class,i);
		}
		public Tff_sequentContext tff_sequent() {
			return getRuleContext(Tff_sequentContext.class,0);
		}
		public Tff_sequentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_sequent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_sequent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_sequent(this);
		}
	}

	public final Tff_sequentContext tff_sequent() throws RecognitionException {
		Tff_sequentContext _localctx = new Tff_sequentContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_tff_sequent);
		try {
			setState(912);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(904);
				tff_formula_tuple();
				setState(905);
				match(T__31);
				setState(906);
				tff_formula_tuple();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(908);
				match(T__1);
				setState(909);
				tff_sequent();
				setState(910);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_formula_tupleContext extends ParserRuleContext {
		public Tff_formula_tuple_listContext tff_formula_tuple_list() {
			return getRuleContext(Tff_formula_tuple_listContext.class,0);
		}
		public Tff_formula_tupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_formula_tuple; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_formula_tuple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_formula_tuple(this);
		}
	}

	public final Tff_formula_tupleContext tff_formula_tuple() throws RecognitionException {
		Tff_formula_tupleContext _localctx = new Tff_formula_tupleContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_tff_formula_tuple);
		try {
			setState(919);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(914);
				match(T__17);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(915);
				match(T__5);
				setState(916);
				tff_formula_tuple_list();
				setState(917);
				match(T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_formula_tuple_listContext extends ParserRuleContext {
		public List<Tff_logic_formulaContext> tff_logic_formula() {
			return getRuleContexts(Tff_logic_formulaContext.class);
		}
		public Tff_logic_formulaContext tff_logic_formula(int i) {
			return getRuleContext(Tff_logic_formulaContext.class,i);
		}
		public Tff_formula_tuple_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_formula_tuple_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_formula_tuple_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_formula_tuple_list(this);
		}
	}

	public final Tff_formula_tuple_listContext tff_formula_tuple_list() throws RecognitionException {
		Tff_formula_tuple_listContext _localctx = new Tff_formula_tuple_listContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_tff_formula_tuple_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(921);
			tff_logic_formula();
			setState(926);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(922);
				match(T__4);
				setState(923);
				tff_logic_formula();
				}
				}
				setState(928);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_typed_atomContext extends ParserRuleContext {
		public Tff_untyped_atomContext tff_untyped_atom() {
			return getRuleContext(Tff_untyped_atomContext.class,0);
		}
		public Tff_top_level_typeContext tff_top_level_type() {
			return getRuleContext(Tff_top_level_typeContext.class,0);
		}
		public Tff_typed_atomContext tff_typed_atom() {
			return getRuleContext(Tff_typed_atomContext.class,0);
		}
		public Tff_typed_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_typed_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_typed_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_typed_atom(this);
		}
	}

	public final Tff_typed_atomContext tff_typed_atom() throws RecognitionException {
		Tff_typed_atomContext _localctx = new Tff_typed_atomContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_tff_typed_atom);
		try {
			setState(937);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Dollar_dollar_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(929);
				tff_untyped_atom();
				setState(930);
				match(T__11);
				setState(931);
				tff_top_level_type();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(933);
				match(T__1);
				setState(934);
				tff_typed_atom();
				setState(935);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_untyped_atomContext extends ParserRuleContext {
		public FunctorContext functor() {
			return getRuleContext(FunctorContext.class,0);
		}
		public System_functorContext system_functor() {
			return getRuleContext(System_functorContext.class,0);
		}
		public Tff_untyped_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_untyped_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_untyped_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_untyped_atom(this);
		}
	}

	public final Tff_untyped_atomContext tff_untyped_atom() throws RecognitionException {
		Tff_untyped_atomContext _localctx = new Tff_untyped_atomContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_tff_untyped_atom);
		try {
			setState(941);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(939);
				functor();
				}
				break;
			case Dollar_dollar_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(940);
				system_functor();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_top_level_typeContext extends ParserRuleContext {
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_mapping_typeContext tff_mapping_type() {
			return getRuleContext(Tff_mapping_typeContext.class,0);
		}
		public Tf1_quantified_typeContext tf1_quantified_type() {
			return getRuleContext(Tf1_quantified_typeContext.class,0);
		}
		public Tff_top_level_typeContext tff_top_level_type() {
			return getRuleContext(Tff_top_level_typeContext.class,0);
		}
		public Tff_top_level_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_top_level_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_top_level_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_top_level_type(this);
		}
	}

	public final Tff_top_level_typeContext tff_top_level_type() throws RecognitionException {
		Tff_top_level_typeContext _localctx = new Tff_top_level_typeContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_tff_top_level_type);
		try {
			setState(950);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(943);
				tff_atomic_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(944);
				tff_mapping_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(945);
				tf1_quantified_type();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(946);
				match(T__1);
				setState(947);
				tff_top_level_type();
				setState(948);
				match(T__2);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tf1_quantified_typeContext extends ParserRuleContext {
		public TerminalNode TyForall() { return getToken(tptpParser.TyForall, 0); }
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
		}
		public Tff_monotypeContext tff_monotype() {
			return getRuleContext(Tff_monotypeContext.class,0);
		}
		public Tf1_quantified_typeContext tf1_quantified_type() {
			return getRuleContext(Tf1_quantified_typeContext.class,0);
		}
		public Tf1_quantified_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tf1_quantified_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTf1_quantified_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTf1_quantified_type(this);
		}
	}

	public final Tf1_quantified_typeContext tf1_quantified_type() throws RecognitionException {
		Tf1_quantified_typeContext _localctx = new Tf1_quantified_typeContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_tf1_quantified_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(952);
			match(TyForall);
			setState(953);
			match(T__5);
			setState(954);
			tff_variable_list();
			setState(955);
			match(T__6);
			setState(956);
			match(T__11);
			setState(959);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case Dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
				{
				setState(957);
				tff_monotype();
				}
				break;
			case TyForall:
				{
				setState(958);
				tf1_quantified_type();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_monotypeContext extends ParserRuleContext {
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_mapping_typeContext tff_mapping_type() {
			return getRuleContext(Tff_mapping_typeContext.class,0);
		}
		public Tff_monotypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_monotype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_monotype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_monotype(this);
		}
	}

	public final Tff_monotypeContext tff_monotype() throws RecognitionException {
		Tff_monotypeContext _localctx = new Tff_monotypeContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_tff_monotype);
		try {
			setState(966);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(961);
				tff_atomic_type();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(962);
				match(T__1);
				setState(963);
				tff_mapping_type();
				setState(964);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_unitary_typeContext extends ParserRuleContext {
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_xprod_typeContext tff_xprod_type() {
			return getRuleContext(Tff_xprod_typeContext.class,0);
		}
		public Tff_unitary_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_unitary_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_unitary_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_unitary_type(this);
		}
	}

	public final Tff_unitary_typeContext tff_unitary_type() throws RecognitionException {
		Tff_unitary_typeContext _localctx = new Tff_unitary_typeContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_tff_unitary_type);
		try {
			setState(973);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(968);
				tff_atomic_type();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(969);
				match(T__1);
				setState(970);
				tff_xprod_type(0);
				setState(971);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_atomic_typeContext extends ParserRuleContext {
		public Defined_typeContext defined_type() {
			return getRuleContext(Defined_typeContext.class,0);
		}
		public Type_functorContext type_functor() {
			return getRuleContext(Type_functorContext.class,0);
		}
		public Tff_type_argumentsContext tff_type_arguments() {
			return getRuleContext(Tff_type_argumentsContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Tff_atomic_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_atomic_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_atomic_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_atomic_type(this);
		}
	}

	public final Tff_atomic_typeContext tff_atomic_type() throws RecognitionException {
		Tff_atomic_typeContext _localctx = new Tff_atomic_typeContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_tff_atomic_type);
		try {
			setState(984);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Dollar_word:
				enterOuterAlt(_localctx, 1);
				{
				setState(975);
				defined_type();
				}
				break;
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 2);
				{
				setState(976);
				type_functor();
				setState(981);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
				case 1:
					{
					setState(977);
					match(T__1);
					setState(978);
					tff_type_arguments();
					setState(979);
					match(T__2);
					}
					break;
				}
				}
				break;
			case Upper_word:
				enterOuterAlt(_localctx, 3);
				{
				setState(983);
				variable();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_type_argumentsContext extends ParserRuleContext {
		public List<Tff_atomic_typeContext> tff_atomic_type() {
			return getRuleContexts(Tff_atomic_typeContext.class);
		}
		public Tff_atomic_typeContext tff_atomic_type(int i) {
			return getRuleContext(Tff_atomic_typeContext.class,i);
		}
		public Tff_type_argumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_type_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_type_arguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_type_arguments(this);
		}
	}

	public final Tff_type_argumentsContext tff_type_arguments() throws RecognitionException {
		Tff_type_argumentsContext _localctx = new Tff_type_argumentsContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_tff_type_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(986);
			tff_atomic_type();
			setState(991);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(987);
				match(T__4);
				setState(988);
				tff_atomic_type();
				}
				}
				setState(993);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_mapping_typeContext extends ParserRuleContext {
		public Tff_unitary_typeContext tff_unitary_type() {
			return getRuleContext(Tff_unitary_typeContext.class,0);
		}
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_mapping_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_mapping_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_mapping_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_mapping_type(this);
		}
	}

	public final Tff_mapping_typeContext tff_mapping_type() throws RecognitionException {
		Tff_mapping_typeContext _localctx = new Tff_mapping_typeContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_tff_mapping_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(994);
			tff_unitary_type();
			setState(995);
			match(T__21);
			setState(996);
			tff_atomic_type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_xprod_typeContext extends ParserRuleContext {
		public Tff_unitary_typeContext tff_unitary_type() {
			return getRuleContext(Tff_unitary_typeContext.class,0);
		}
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_xprod_typeContext tff_xprod_type() {
			return getRuleContext(Tff_xprod_typeContext.class,0);
		}
		public Tff_xprod_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_xprod_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_xprod_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_xprod_type(this);
		}
	}

	public final Tff_xprod_typeContext tff_xprod_type() throws RecognitionException {
		return tff_xprod_type(0);
	}

	private Tff_xprod_typeContext tff_xprod_type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Tff_xprod_typeContext _localctx = new Tff_xprod_typeContext(_ctx, _parentState);
		Tff_xprod_typeContext _prevctx = _localctx;
		int _startState = 188;
		enterRecursionRule(_localctx, 188, RULE_tff_xprod_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(999);
			tff_unitary_type();
			setState(1000);
			match(T__22);
			setState(1001);
			tff_atomic_type();
			}
			_ctx.stop = _input.LT(-1);
			setState(1008);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Tff_xprod_typeContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_tff_xprod_type);
					setState(1003);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1004);
					match(T__22);
					setState(1005);
					tff_atomic_type();
					}
					} 
				}
				setState(1010);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Fof_formulaContext extends ParserRuleContext {
		public Fof_logic_formulaContext fof_logic_formula() {
			return getRuleContext(Fof_logic_formulaContext.class,0);
		}
		public Fof_sequentContext fof_sequent() {
			return getRuleContext(Fof_sequentContext.class,0);
		}
		public Fof_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_formula(this);
		}
	}

	public final Fof_formulaContext fof_formula() throws RecognitionException {
		Fof_formulaContext _localctx = new Fof_formulaContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_fof_formula);
		try {
			setState(1013);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1011);
				fof_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1012);
				fof_sequent();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_logic_formulaContext extends ParserRuleContext {
		public Fof_binary_formulaContext fof_binary_formula() {
			return getRuleContext(Fof_binary_formulaContext.class,0);
		}
		public Fof_unitary_formulaContext fof_unitary_formula() {
			return getRuleContext(Fof_unitary_formulaContext.class,0);
		}
		public Fof_logic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_logic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_logic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_logic_formula(this);
		}
	}

	public final Fof_logic_formulaContext fof_logic_formula() throws RecognitionException {
		Fof_logic_formulaContext _localctx = new Fof_logic_formulaContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_fof_logic_formula);
		try {
			setState(1017);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1015);
				fof_binary_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1016);
				fof_unitary_formula();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_binary_formulaContext extends ParserRuleContext {
		public Fof_binary_nonassocContext fof_binary_nonassoc() {
			return getRuleContext(Fof_binary_nonassocContext.class,0);
		}
		public Fof_binary_assocContext fof_binary_assoc() {
			return getRuleContext(Fof_binary_assocContext.class,0);
		}
		public Fof_binary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_binary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_binary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_binary_formula(this);
		}
	}

	public final Fof_binary_formulaContext fof_binary_formula() throws RecognitionException {
		Fof_binary_formulaContext _localctx = new Fof_binary_formulaContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_fof_binary_formula);
		try {
			setState(1021);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1019);
				fof_binary_nonassoc();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1020);
				fof_binary_assoc();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_binary_nonassocContext extends ParserRuleContext {
		public List<Fof_unitary_formulaContext> fof_unitary_formula() {
			return getRuleContexts(Fof_unitary_formulaContext.class);
		}
		public Fof_unitary_formulaContext fof_unitary_formula(int i) {
			return getRuleContext(Fof_unitary_formulaContext.class,i);
		}
		public Binary_connectiveContext binary_connective() {
			return getRuleContext(Binary_connectiveContext.class,0);
		}
		public Fof_binary_nonassocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_binary_nonassoc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_binary_nonassoc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_binary_nonassoc(this);
		}
	}

	public final Fof_binary_nonassocContext fof_binary_nonassoc() throws RecognitionException {
		Fof_binary_nonassocContext _localctx = new Fof_binary_nonassocContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_fof_binary_nonassoc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1023);
			fof_unitary_formula();
			setState(1024);
			binary_connective();
			setState(1025);
			fof_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_binary_assocContext extends ParserRuleContext {
		public Fof_or_formulaContext fof_or_formula() {
			return getRuleContext(Fof_or_formulaContext.class,0);
		}
		public Fof_and_formulaContext fof_and_formula() {
			return getRuleContext(Fof_and_formulaContext.class,0);
		}
		public Fof_binary_assocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_binary_assoc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_binary_assoc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_binary_assoc(this);
		}
	}

	public final Fof_binary_assocContext fof_binary_assoc() throws RecognitionException {
		Fof_binary_assocContext _localctx = new Fof_binary_assocContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_fof_binary_assoc);
		try {
			setState(1029);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1027);
				fof_or_formula(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1028);
				fof_and_formula(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_or_formulaContext extends ParserRuleContext {
		public List<Fof_unitary_formulaContext> fof_unitary_formula() {
			return getRuleContexts(Fof_unitary_formulaContext.class);
		}
		public Fof_unitary_formulaContext fof_unitary_formula(int i) {
			return getRuleContext(Fof_unitary_formulaContext.class,i);
		}
		public TerminalNode Or() { return getToken(tptpParser.Or, 0); }
		public Fof_or_formulaContext fof_or_formula() {
			return getRuleContext(Fof_or_formulaContext.class,0);
		}
		public Fof_or_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_or_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_or_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_or_formula(this);
		}
	}

	public final Fof_or_formulaContext fof_or_formula() throws RecognitionException {
		return fof_or_formula(0);
	}

	private Fof_or_formulaContext fof_or_formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Fof_or_formulaContext _localctx = new Fof_or_formulaContext(_ctx, _parentState);
		Fof_or_formulaContext _prevctx = _localctx;
		int _startState = 200;
		enterRecursionRule(_localctx, 200, RULE_fof_or_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1032);
			fof_unitary_formula();
			setState(1033);
			match(Or);
			setState(1034);
			fof_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(1041);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Fof_or_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_fof_or_formula);
					setState(1036);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1037);
					match(Or);
					setState(1038);
					fof_unitary_formula();
					}
					} 
				}
				setState(1043);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,78,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Fof_and_formulaContext extends ParserRuleContext {
		public List<Fof_unitary_formulaContext> fof_unitary_formula() {
			return getRuleContexts(Fof_unitary_formulaContext.class);
		}
		public Fof_unitary_formulaContext fof_unitary_formula(int i) {
			return getRuleContext(Fof_unitary_formulaContext.class,i);
		}
		public TerminalNode And() { return getToken(tptpParser.And, 0); }
		public Fof_and_formulaContext fof_and_formula() {
			return getRuleContext(Fof_and_formulaContext.class,0);
		}
		public Fof_and_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_and_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_and_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_and_formula(this);
		}
	}

	public final Fof_and_formulaContext fof_and_formula() throws RecognitionException {
		return fof_and_formula(0);
	}

	private Fof_and_formulaContext fof_and_formula(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Fof_and_formulaContext _localctx = new Fof_and_formulaContext(_ctx, _parentState);
		Fof_and_formulaContext _prevctx = _localctx;
		int _startState = 202;
		enterRecursionRule(_localctx, 202, RULE_fof_and_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1045);
			fof_unitary_formula();
			setState(1046);
			match(And);
			setState(1047);
			fof_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(1054);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,79,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Fof_and_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_fof_and_formula);
					setState(1049);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1050);
					match(And);
					setState(1051);
					fof_unitary_formula();
					}
					} 
				}
				setState(1056);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,79,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Fof_unitary_formulaContext extends ParserRuleContext {
		public Fof_quantified_formulaContext fof_quantified_formula() {
			return getRuleContext(Fof_quantified_formulaContext.class,0);
		}
		public Fof_unary_formulaContext fof_unary_formula() {
			return getRuleContext(Fof_unary_formulaContext.class,0);
		}
		public Fof_atomic_formulaContext fof_atomic_formula() {
			return getRuleContext(Fof_atomic_formulaContext.class,0);
		}
		public Fof_logic_formulaContext fof_logic_formula() {
			return getRuleContext(Fof_logic_formulaContext.class,0);
		}
		public Fof_unitary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_unitary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_unitary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_unitary_formula(this);
		}
	}

	public final Fof_unitary_formulaContext fof_unitary_formula() throws RecognitionException {
		Fof_unitary_formulaContext _localctx = new Fof_unitary_formulaContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_fof_unitary_formula);
		try {
			setState(1064);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1057);
				fof_quantified_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1058);
				fof_unary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1059);
				fof_atomic_formula();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1060);
				match(T__1);
				setState(1061);
				fof_logic_formula();
				setState(1062);
				match(T__2);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_quantified_formulaContext extends ParserRuleContext {
		public Fof_quantifierContext fof_quantifier() {
			return getRuleContext(Fof_quantifierContext.class,0);
		}
		public Fof_variable_listContext fof_variable_list() {
			return getRuleContext(Fof_variable_listContext.class,0);
		}
		public Fof_unitary_formulaContext fof_unitary_formula() {
			return getRuleContext(Fof_unitary_formulaContext.class,0);
		}
		public Fof_quantified_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_quantified_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_quantified_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_quantified_formula(this);
		}
	}

	public final Fof_quantified_formulaContext fof_quantified_formula() throws RecognitionException {
		Fof_quantified_formulaContext _localctx = new Fof_quantified_formulaContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_fof_quantified_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1066);
			fof_quantifier();
			setState(1067);
			match(T__5);
			setState(1068);
			fof_variable_list();
			setState(1069);
			match(T__6);
			setState(1070);
			match(T__11);
			setState(1071);
			fof_unitary_formula();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_variable_listContext extends ParserRuleContext {
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
		}
		public Fof_variable_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_variable_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_variable_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_variable_list(this);
		}
	}

	public final Fof_variable_listContext fof_variable_list() throws RecognitionException {
		Fof_variable_listContext _localctx = new Fof_variable_listContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_fof_variable_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1073);
			variable();
			setState(1078);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1074);
				match(T__4);
				setState(1075);
				variable();
				}
				}
				setState(1080);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_unary_formulaContext extends ParserRuleContext {
		public Unary_connectiveContext unary_connective() {
			return getRuleContext(Unary_connectiveContext.class,0);
		}
		public Fof_unitary_formulaContext fof_unitary_formula() {
			return getRuleContext(Fof_unitary_formulaContext.class,0);
		}
		public Fof_infix_unaryContext fof_infix_unary() {
			return getRuleContext(Fof_infix_unaryContext.class,0);
		}
		public Fof_unary_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_unary_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_unary_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_unary_formula(this);
		}
	}

	public final Fof_unary_formulaContext fof_unary_formula() throws RecognitionException {
		Fof_unary_formulaContext _localctx = new Fof_unary_formulaContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_fof_unary_formula);
		try {
			setState(1085);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(1081);
				unary_connective();
				setState(1082);
				fof_unitary_formula();
				}
				break;
			case T__25:
			case T__26:
			case T__32:
			case T__33:
			case T__34:
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Dollar_dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
			case Distinct_object:
				enterOuterAlt(_localctx, 2);
				{
				setState(1084);
				fof_infix_unary();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_infix_unaryContext extends ParserRuleContext {
		public List<Fof_termContext> fof_term() {
			return getRuleContexts(Fof_termContext.class);
		}
		public Fof_termContext fof_term(int i) {
			return getRuleContext(Fof_termContext.class,i);
		}
		public TerminalNode Infix_inequality() { return getToken(tptpParser.Infix_inequality, 0); }
		public Fof_infix_unaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_infix_unary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_infix_unary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_infix_unary(this);
		}
	}

	public final Fof_infix_unaryContext fof_infix_unary() throws RecognitionException {
		Fof_infix_unaryContext _localctx = new Fof_infix_unaryContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_fof_infix_unary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1087);
			fof_term();
			setState(1088);
			match(Infix_inequality);
			setState(1089);
			fof_term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_atomic_formulaContext extends ParserRuleContext {
		public Fof_plain_atomic_formulaContext fof_plain_atomic_formula() {
			return getRuleContext(Fof_plain_atomic_formulaContext.class,0);
		}
		public Fof_defined_atomic_formulaContext fof_defined_atomic_formula() {
			return getRuleContext(Fof_defined_atomic_formulaContext.class,0);
		}
		public Fof_system_atomic_formulaContext fof_system_atomic_formula() {
			return getRuleContext(Fof_system_atomic_formulaContext.class,0);
		}
		public Fof_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_atomic_formula(this);
		}
	}

	public final Fof_atomic_formulaContext fof_atomic_formula() throws RecognitionException {
		Fof_atomic_formulaContext _localctx = new Fof_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_fof_atomic_formula);
		try {
			setState(1094);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1091);
				fof_plain_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1092);
				fof_defined_atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1093);
				fof_system_atomic_formula();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_plain_atomic_formulaContext extends ParserRuleContext {
		public Fof_plain_termContext fof_plain_term() {
			return getRuleContext(Fof_plain_termContext.class,0);
		}
		public Fof_plain_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_plain_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_plain_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_plain_atomic_formula(this);
		}
	}

	public final Fof_plain_atomic_formulaContext fof_plain_atomic_formula() throws RecognitionException {
		Fof_plain_atomic_formulaContext _localctx = new Fof_plain_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_fof_plain_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1096);
			fof_plain_term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_defined_atomic_formulaContext extends ParserRuleContext {
		public Fof_defined_plain_formulaContext fof_defined_plain_formula() {
			return getRuleContext(Fof_defined_plain_formulaContext.class,0);
		}
		public Fof_defined_infix_formulaContext fof_defined_infix_formula() {
			return getRuleContext(Fof_defined_infix_formulaContext.class,0);
		}
		public Fof_defined_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_defined_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_defined_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_defined_atomic_formula(this);
		}
	}

	public final Fof_defined_atomic_formulaContext fof_defined_atomic_formula() throws RecognitionException {
		Fof_defined_atomic_formulaContext _localctx = new Fof_defined_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_fof_defined_atomic_formula);
		try {
			setState(1100);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1098);
				fof_defined_plain_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1099);
				fof_defined_infix_formula();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_defined_plain_formulaContext extends ParserRuleContext {
		public Fof_defined_termContext fof_defined_term() {
			return getRuleContext(Fof_defined_termContext.class,0);
		}
		public Fof_defined_plain_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_defined_plain_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_defined_plain_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_defined_plain_formula(this);
		}
	}

	public final Fof_defined_plain_formulaContext fof_defined_plain_formula() throws RecognitionException {
		Fof_defined_plain_formulaContext _localctx = new Fof_defined_plain_formulaContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_fof_defined_plain_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1102);
			fof_defined_term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_defined_infix_formulaContext extends ParserRuleContext {
		public List<Fof_termContext> fof_term() {
			return getRuleContexts(Fof_termContext.class);
		}
		public Fof_termContext fof_term(int i) {
			return getRuleContext(Fof_termContext.class,i);
		}
		public Defined_infix_predContext defined_infix_pred() {
			return getRuleContext(Defined_infix_predContext.class,0);
		}
		public Fof_defined_infix_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_defined_infix_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_defined_infix_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_defined_infix_formula(this);
		}
	}

	public final Fof_defined_infix_formulaContext fof_defined_infix_formula() throws RecognitionException {
		Fof_defined_infix_formulaContext _localctx = new Fof_defined_infix_formulaContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_fof_defined_infix_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1104);
			fof_term();
			setState(1105);
			defined_infix_pred();
			setState(1106);
			fof_term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_system_atomic_formulaContext extends ParserRuleContext {
		public Fof_system_termContext fof_system_term() {
			return getRuleContext(Fof_system_termContext.class,0);
		}
		public Fof_system_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_system_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_system_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_system_atomic_formula(this);
		}
	}

	public final Fof_system_atomic_formulaContext fof_system_atomic_formula() throws RecognitionException {
		Fof_system_atomic_formulaContext _localctx = new Fof_system_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_fof_system_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1108);
			fof_system_term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_plain_termContext extends ParserRuleContext {
		public FunctorContext functor() {
			return getRuleContext(FunctorContext.class,0);
		}
		public Fof_argumentsContext fof_arguments() {
			return getRuleContext(Fof_argumentsContext.class,0);
		}
		public Fof_plain_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_plain_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_plain_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_plain_term(this);
		}
	}

	public final Fof_plain_termContext fof_plain_term() throws RecognitionException {
		Fof_plain_termContext _localctx = new Fof_plain_termContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_fof_plain_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1110);
			functor();
			setState(1115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
			case 1:
				{
				setState(1111);
				match(T__1);
				setState(1112);
				fof_arguments();
				setState(1113);
				match(T__2);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_defined_termContext extends ParserRuleContext {
		public Defined_functorContext defined_functor() {
			return getRuleContext(Defined_functorContext.class,0);
		}
		public Fof_argumentsContext fof_arguments() {
			return getRuleContext(Fof_argumentsContext.class,0);
		}
		public Fof_defined_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_defined_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_defined_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_defined_term(this);
		}
	}

	public final Fof_defined_termContext fof_defined_term() throws RecognitionException {
		Fof_defined_termContext _localctx = new Fof_defined_termContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_fof_defined_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1117);
			defined_functor();
			setState(1122);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				{
				setState(1118);
				match(T__1);
				setState(1119);
				fof_arguments();
				setState(1120);
				match(T__2);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_system_termContext extends ParserRuleContext {
		public System_functorContext system_functor() {
			return getRuleContext(System_functorContext.class,0);
		}
		public Fof_argumentsContext fof_arguments() {
			return getRuleContext(Fof_argumentsContext.class,0);
		}
		public Fof_system_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_system_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_system_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_system_term(this);
		}
	}

	public final Fof_system_termContext fof_system_term() throws RecognitionException {
		Fof_system_termContext _localctx = new Fof_system_termContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_fof_system_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1124);
			system_functor();
			setState(1129);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				{
				setState(1125);
				match(T__1);
				setState(1126);
				fof_arguments();
				setState(1127);
				match(T__2);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_argumentsContext extends ParserRuleContext {
		public List<Fof_termContext> fof_term() {
			return getRuleContexts(Fof_termContext.class);
		}
		public Fof_termContext fof_term(int i) {
			return getRuleContext(Fof_termContext.class,i);
		}
		public Fof_argumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_arguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_arguments(this);
		}
	}

	public final Fof_argumentsContext fof_arguments() throws RecognitionException {
		Fof_argumentsContext _localctx = new Fof_argumentsContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_fof_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1131);
			fof_term();
			setState(1136);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1132);
				match(T__4);
				setState(1133);
				fof_term();
				}
				}
				setState(1138);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_termContext extends ParserRuleContext {
		public Defined_termContext defined_term() {
			return getRuleContext(Defined_termContext.class,0);
		}
		public Fof_function_termContext fof_function_term() {
			return getRuleContext(Fof_function_termContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Tff_conditional_termContext tff_conditional_term() {
			return getRuleContext(Tff_conditional_termContext.class,0);
		}
		public Tff_let_termContext tff_let_term() {
			return getRuleContext(Tff_let_termContext.class,0);
		}
		public Tff_tuple_termContext tff_tuple_term() {
			return getRuleContext(Tff_tuple_termContext.class,0);
		}
		public Fof_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_term(this);
		}
	}

	public final Fof_termContext fof_term() throws RecognitionException {
		Fof_termContext _localctx = new Fof_termContext(_ctx, getState());
		enterRule(_localctx, 234, RULE_fof_term);
		try {
			setState(1145);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Real:
			case Rational:
			case Integer:
			case Distinct_object:
				enterOuterAlt(_localctx, 1);
				{
				setState(1139);
				defined_term();
				}
				break;
			case Dollar_word:
			case Dollar_dollar_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 2);
				{
				setState(1140);
				fof_function_term();
				}
				break;
			case Upper_word:
				enterOuterAlt(_localctx, 3);
				{
				setState(1141);
				variable();
				}
				break;
			case T__32:
				enterOuterAlt(_localctx, 4);
				{
				setState(1142);
				tff_conditional_term();
				}
				break;
			case T__33:
			case T__34:
				enterOuterAlt(_localctx, 5);
				{
				setState(1143);
				tff_let_term();
				}
				break;
			case T__25:
			case T__26:
				enterOuterAlt(_localctx, 6);
				{
				setState(1144);
				tff_tuple_term();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_function_termContext extends ParserRuleContext {
		public Fof_plain_termContext fof_plain_term() {
			return getRuleContext(Fof_plain_termContext.class,0);
		}
		public Fof_defined_termContext fof_defined_term() {
			return getRuleContext(Fof_defined_termContext.class,0);
		}
		public Fof_system_termContext fof_system_term() {
			return getRuleContext(Fof_system_termContext.class,0);
		}
		public Fof_function_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_function_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_function_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_function_term(this);
		}
	}

	public final Fof_function_termContext fof_function_term() throws RecognitionException {
		Fof_function_termContext _localctx = new Fof_function_termContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_fof_function_term);
		try {
			setState(1150);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(1147);
				fof_plain_term();
				}
				break;
			case Dollar_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(1148);
				fof_defined_term();
				}
				break;
			case Dollar_dollar_word:
				enterOuterAlt(_localctx, 3);
				{
				setState(1149);
				fof_system_term();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_conditional_termContext extends ParserRuleContext {
		public Tff_logic_formulaContext tff_logic_formula() {
			return getRuleContext(Tff_logic_formulaContext.class,0);
		}
		public List<Fof_termContext> fof_term() {
			return getRuleContexts(Fof_termContext.class);
		}
		public Fof_termContext fof_term(int i) {
			return getRuleContext(Fof_termContext.class,i);
		}
		public Tff_conditional_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_conditional_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_conditional_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_conditional_term(this);
		}
	}

	public final Tff_conditional_termContext tff_conditional_term() throws RecognitionException {
		Tff_conditional_termContext _localctx = new Tff_conditional_termContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_tff_conditional_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1152);
			match(T__32);
			setState(1153);
			tff_logic_formula();
			setState(1154);
			match(T__4);
			setState(1155);
			fof_term();
			setState(1156);
			match(T__4);
			setState(1157);
			fof_term();
			setState(1158);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_let_termContext extends ParserRuleContext {
		public Tff_let_formula_defnsContext tff_let_formula_defns() {
			return getRuleContext(Tff_let_formula_defnsContext.class,0);
		}
		public Fof_termContext fof_term() {
			return getRuleContext(Fof_termContext.class,0);
		}
		public Tff_let_term_defnsContext tff_let_term_defns() {
			return getRuleContext(Tff_let_term_defnsContext.class,0);
		}
		public Tff_let_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_let_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_let_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_let_term(this);
		}
	}

	public final Tff_let_termContext tff_let_term() throws RecognitionException {
		Tff_let_termContext _localctx = new Tff_let_termContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_tff_let_term);
		try {
			setState(1172);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__33:
				enterOuterAlt(_localctx, 1);
				{
				setState(1160);
				match(T__33);
				setState(1161);
				tff_let_formula_defns();
				setState(1162);
				match(T__4);
				setState(1163);
				fof_term();
				setState(1164);
				match(T__2);
				}
				break;
			case T__34:
				enterOuterAlt(_localctx, 2);
				{
				setState(1166);
				match(T__34);
				setState(1167);
				tff_let_term_defns();
				setState(1168);
				match(T__4);
				setState(1169);
				fof_term();
				setState(1170);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Tff_tuple_termContext extends ParserRuleContext {
		public Fof_argumentsContext fof_arguments() {
			return getRuleContext(Fof_argumentsContext.class,0);
		}
		public Tff_tuple_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_tuple_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_tuple_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_tuple_term(this);
		}
	}

	public final Tff_tuple_termContext tff_tuple_term() throws RecognitionException {
		Tff_tuple_termContext _localctx = new Tff_tuple_termContext(_ctx, getState());
		enterRule(_localctx, 242, RULE_tff_tuple_term);
		try {
			setState(1179);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__25:
				enterOuterAlt(_localctx, 1);
				{
				setState(1174);
				match(T__25);
				}
				break;
			case T__26:
				enterOuterAlt(_localctx, 2);
				{
				setState(1175);
				match(T__26);
				setState(1176);
				fof_arguments();
				setState(1177);
				match(T__27);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_sequentContext extends ParserRuleContext {
		public List<Fof_formula_tupleContext> fof_formula_tuple() {
			return getRuleContexts(Fof_formula_tupleContext.class);
		}
		public Fof_formula_tupleContext fof_formula_tuple(int i) {
			return getRuleContext(Fof_formula_tupleContext.class,i);
		}
		public Fof_sequentContext fof_sequent() {
			return getRuleContext(Fof_sequentContext.class,0);
		}
		public Fof_sequentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_sequent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_sequent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_sequent(this);
		}
	}

	public final Fof_sequentContext fof_sequent() throws RecognitionException {
		Fof_sequentContext _localctx = new Fof_sequentContext(_ctx, getState());
		enterRule(_localctx, 244, RULE_fof_sequent);
		try {
			setState(1189);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__5:
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(1181);
				fof_formula_tuple();
				setState(1182);
				match(T__31);
				setState(1183);
				fof_formula_tuple();
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(1185);
				match(T__1);
				setState(1186);
				fof_sequent();
				setState(1187);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_formula_tupleContext extends ParserRuleContext {
		public Fof_formula_tuple_listContext fof_formula_tuple_list() {
			return getRuleContext(Fof_formula_tuple_listContext.class,0);
		}
		public Fof_formula_tupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_formula_tuple; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_formula_tuple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_formula_tuple(this);
		}
	}

	public final Fof_formula_tupleContext fof_formula_tuple() throws RecognitionException {
		Fof_formula_tupleContext _localctx = new Fof_formula_tupleContext(_ctx, getState());
		enterRule(_localctx, 246, RULE_fof_formula_tuple);
		try {
			setState(1196);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(1191);
				match(T__17);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(1192);
				match(T__5);
				setState(1193);
				fof_formula_tuple_list();
				setState(1194);
				match(T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_formula_tuple_listContext extends ParserRuleContext {
		public List<Fof_logic_formulaContext> fof_logic_formula() {
			return getRuleContexts(Fof_logic_formulaContext.class);
		}
		public Fof_logic_formulaContext fof_logic_formula(int i) {
			return getRuleContext(Fof_logic_formulaContext.class,i);
		}
		public Fof_formula_tuple_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_formula_tuple_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_formula_tuple_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_formula_tuple_list(this);
		}
	}

	public final Fof_formula_tuple_listContext fof_formula_tuple_list() throws RecognitionException {
		Fof_formula_tuple_listContext _localctx = new Fof_formula_tuple_listContext(_ctx, getState());
		enterRule(_localctx, 248, RULE_fof_formula_tuple_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1198);
			fof_logic_formula();
			setState(1203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(1199);
				match(T__4);
				setState(1200);
				fof_logic_formula();
				}
				}
				setState(1205);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cnf_formulaContext extends ParserRuleContext {
		public Cnf_disjunctionContext cnf_disjunction() {
			return getRuleContext(Cnf_disjunctionContext.class,0);
		}
		public Cnf_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cnf_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterCnf_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitCnf_formula(this);
		}
	}

	public final Cnf_formulaContext cnf_formula() throws RecognitionException {
		Cnf_formulaContext _localctx = new Cnf_formulaContext(_ctx, getState());
		enterRule(_localctx, 250, RULE_cnf_formula);
		try {
			setState(1211);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__25:
			case T__26:
			case T__32:
			case T__33:
			case T__34:
			case Not:
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Dollar_dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
			case Distinct_object:
				enterOuterAlt(_localctx, 1);
				{
				setState(1206);
				cnf_disjunction(0);
				}
				break;
			case T__1:
				enterOuterAlt(_localctx, 2);
				{
				setState(1207);
				match(T__1);
				setState(1208);
				cnf_disjunction(0);
				setState(1209);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cnf_disjunctionContext extends ParserRuleContext {
		public Cnf_literalContext cnf_literal() {
			return getRuleContext(Cnf_literalContext.class,0);
		}
		public Cnf_disjunctionContext cnf_disjunction() {
			return getRuleContext(Cnf_disjunctionContext.class,0);
		}
		public TerminalNode Or() { return getToken(tptpParser.Or, 0); }
		public Cnf_disjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cnf_disjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterCnf_disjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitCnf_disjunction(this);
		}
	}

	public final Cnf_disjunctionContext cnf_disjunction() throws RecognitionException {
		return cnf_disjunction(0);
	}

	private Cnf_disjunctionContext cnf_disjunction(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Cnf_disjunctionContext _localctx = new Cnf_disjunctionContext(_ctx, _parentState);
		Cnf_disjunctionContext _prevctx = _localctx;
		int _startState = 252;
		enterRecursionRule(_localctx, 252, RULE_cnf_disjunction, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1214);
			cnf_literal();
			}
			_ctx.stop = _input.LT(-1);
			setState(1221);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,97,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Cnf_disjunctionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_cnf_disjunction);
					setState(1216);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1217);
					match(Or);
					setState(1218);
					cnf_literal();
					}
					} 
				}
				setState(1223);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,97,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Cnf_literalContext extends ParserRuleContext {
		public Fof_atomic_formulaContext fof_atomic_formula() {
			return getRuleContext(Fof_atomic_formulaContext.class,0);
		}
		public TerminalNode Not() { return getToken(tptpParser.Not, 0); }
		public Fof_infix_unaryContext fof_infix_unary() {
			return getRuleContext(Fof_infix_unaryContext.class,0);
		}
		public Cnf_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cnf_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterCnf_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitCnf_literal(this);
		}
	}

	public final Cnf_literalContext cnf_literal() throws RecognitionException {
		Cnf_literalContext _localctx = new Cnf_literalContext(_ctx, getState());
		enterRule(_localctx, 254, RULE_cnf_literal);
		try {
			setState(1228);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1224);
				fof_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1225);
				match(Not);
				setState(1226);
				fof_atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1227);
				fof_infix_unary();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_quantifierContext extends ParserRuleContext {
		public Fof_quantifierContext fof_quantifier() {
			return getRuleContext(Fof_quantifierContext.class,0);
		}
		public Th0_quantifierContext th0_quantifier() {
			return getRuleContext(Th0_quantifierContext.class,0);
		}
		public Th1_quantifierContext th1_quantifier() {
			return getRuleContext(Th1_quantifierContext.class,0);
		}
		public Thf_quantifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_quantifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_quantifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_quantifier(this);
		}
	}

	public final Thf_quantifierContext thf_quantifier() throws RecognitionException {
		Thf_quantifierContext _localctx = new Thf_quantifierContext(_ctx, getState());
		enterRule(_localctx, 256, RULE_thf_quantifier);
		try {
			setState(1233);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Forall:
			case Exists:
				enterOuterAlt(_localctx, 1);
				{
				setState(1230);
				fof_quantifier();
				}
				break;
			case Lambda:
			case Choice:
			case Description:
				enterOuterAlt(_localctx, 2);
				{
				setState(1231);
				th0_quantifier();
				}
				break;
			case TyForall:
			case TyExists:
				enterOuterAlt(_localctx, 3);
				{
				setState(1232);
				th1_quantifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Th0_quantifierContext extends ParserRuleContext {
		public TerminalNode Lambda() { return getToken(tptpParser.Lambda, 0); }
		public TerminalNode Choice() { return getToken(tptpParser.Choice, 0); }
		public TerminalNode Description() { return getToken(tptpParser.Description, 0); }
		public Th0_quantifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_th0_quantifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTh0_quantifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTh0_quantifier(this);
		}
	}

	public final Th0_quantifierContext th0_quantifier() throws RecognitionException {
		Th0_quantifierContext _localctx = new Th0_quantifierContext(_ctx, getState());
		enterRule(_localctx, 258, RULE_th0_quantifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1235);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Lambda) | (1L << Choice) | (1L << Description))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Th1_quantifierContext extends ParserRuleContext {
		public TerminalNode TyForall() { return getToken(tptpParser.TyForall, 0); }
		public TerminalNode TyExists() { return getToken(tptpParser.TyExists, 0); }
		public Th1_quantifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_th1_quantifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTh1_quantifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTh1_quantifier(this);
		}
	}

	public final Th1_quantifierContext th1_quantifier() throws RecognitionException {
		Th1_quantifierContext _localctx = new Th1_quantifierContext(_ctx, getState());
		enterRule(_localctx, 260, RULE_th1_quantifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1237);
			_la = _input.LA(1);
			if ( !(_la==TyForall || _la==TyExists) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_pair_connectiveContext extends ParserRuleContext {
		public TerminalNode Infix_equality() { return getToken(tptpParser.Infix_equality, 0); }
		public TerminalNode Infix_inequality() { return getToken(tptpParser.Infix_inequality, 0); }
		public Binary_connectiveContext binary_connective() {
			return getRuleContext(Binary_connectiveContext.class,0);
		}
		public TerminalNode Assignment() { return getToken(tptpParser.Assignment, 0); }
		public Thf_pair_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_pair_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_pair_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_pair_connective(this);
		}
	}

	public final Thf_pair_connectiveContext thf_pair_connective() throws RecognitionException {
		Thf_pair_connectiveContext _localctx = new Thf_pair_connectiveContext(_ctx, getState());
		enterRule(_localctx, 262, RULE_thf_pair_connective);
		try {
			setState(1243);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Infix_equality:
				enterOuterAlt(_localctx, 1);
				{
				setState(1239);
				match(Infix_equality);
				}
				break;
			case Infix_inequality:
				enterOuterAlt(_localctx, 2);
				{
				setState(1240);
				match(Infix_inequality);
				}
				break;
			case Iff:
			case Impl:
			case If:
			case Niff:
			case Nor:
			case Nand:
				enterOuterAlt(_localctx, 3);
				{
				setState(1241);
				binary_connective();
				}
				break;
			case Assignment:
				enterOuterAlt(_localctx, 4);
				{
				setState(1242);
				match(Assignment);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Thf_unary_connectiveContext extends ParserRuleContext {
		public Unary_connectiveContext unary_connective() {
			return getRuleContext(Unary_connectiveContext.class,0);
		}
		public Th1_unary_connectiveContext th1_unary_connective() {
			return getRuleContext(Th1_unary_connectiveContext.class,0);
		}
		public Thf_unary_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_unary_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_unary_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_unary_connective(this);
		}
	}

	public final Thf_unary_connectiveContext thf_unary_connective() throws RecognitionException {
		Thf_unary_connectiveContext _localctx = new Thf_unary_connectiveContext(_ctx, getState());
		enterRule(_localctx, 264, RULE_thf_unary_connective);
		try {
			setState(1247);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Not:
				enterOuterAlt(_localctx, 1);
				{
				setState(1245);
				unary_connective();
				}
				break;
			case ForallComb:
			case ExistsComb:
			case ChoiceComb:
			case DescriptionComb:
			case EqComb:
				enterOuterAlt(_localctx, 2);
				{
				setState(1246);
				th1_unary_connective();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Th1_unary_connectiveContext extends ParserRuleContext {
		public TerminalNode ForallComb() { return getToken(tptpParser.ForallComb, 0); }
		public TerminalNode ExistsComb() { return getToken(tptpParser.ExistsComb, 0); }
		public TerminalNode ChoiceComb() { return getToken(tptpParser.ChoiceComb, 0); }
		public TerminalNode DescriptionComb() { return getToken(tptpParser.DescriptionComb, 0); }
		public TerminalNode EqComb() { return getToken(tptpParser.EqComb, 0); }
		public Th1_unary_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_th1_unary_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTh1_unary_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTh1_unary_connective(this);
		}
	}

	public final Th1_unary_connectiveContext th1_unary_connective() throws RecognitionException {
		Th1_unary_connectiveContext _localctx = new Th1_unary_connectiveContext(_ctx, getState());
		enterRule(_localctx, 266, RULE_th1_unary_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1249);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ForallComb) | (1L << ExistsComb) | (1L << ChoiceComb) | (1L << DescriptionComb) | (1L << EqComb))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_functorContext extends ParserRuleContext {
		public Atomic_wordContext atomic_word() {
			return getRuleContext(Atomic_wordContext.class,0);
		}
		public Type_functorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_functor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterType_functor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitType_functor(this);
		}
	}

	public final Type_functorContext type_functor() throws RecognitionException {
		Type_functorContext _localctx = new Type_functorContext(_ctx, getState());
		enterRule(_localctx, 268, RULE_type_functor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1251);
			atomic_word();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Defined_typeContext extends ParserRuleContext {
		public Atomic_defined_wordContext atomic_defined_word() {
			return getRuleContext(Atomic_defined_wordContext.class,0);
		}
		public Defined_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_type(this);
		}
	}

	public final Defined_typeContext defined_type() throws RecognitionException {
		Defined_typeContext _localctx = new Defined_typeContext(_ctx, getState());
		enterRule(_localctx, 270, RULE_defined_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1253);
			atomic_defined_word();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fof_quantifierContext extends ParserRuleContext {
		public TerminalNode Forall() { return getToken(tptpParser.Forall, 0); }
		public TerminalNode Exists() { return getToken(tptpParser.Exists, 0); }
		public Fof_quantifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fof_quantifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFof_quantifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFof_quantifier(this);
		}
	}

	public final Fof_quantifierContext fof_quantifier() throws RecognitionException {
		Fof_quantifierContext _localctx = new Fof_quantifierContext(_ctx, getState());
		enterRule(_localctx, 272, RULE_fof_quantifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1255);
			_la = _input.LA(1);
			if ( !(_la==Forall || _la==Exists) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Binary_connectiveContext extends ParserRuleContext {
		public TerminalNode Iff() { return getToken(tptpParser.Iff, 0); }
		public TerminalNode Impl() { return getToken(tptpParser.Impl, 0); }
		public TerminalNode If() { return getToken(tptpParser.If, 0); }
		public TerminalNode Niff() { return getToken(tptpParser.Niff, 0); }
		public TerminalNode Nor() { return getToken(tptpParser.Nor, 0); }
		public TerminalNode Nand() { return getToken(tptpParser.Nand, 0); }
		public Binary_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binary_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterBinary_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitBinary_connective(this);
		}
	}

	public final Binary_connectiveContext binary_connective() throws RecognitionException {
		Binary_connectiveContext _localctx = new Binary_connectiveContext(_ctx, getState());
		enterRule(_localctx, 274, RULE_binary_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1257);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Iff) | (1L << Impl) | (1L << If) | (1L << Niff) | (1L << Nor) | (1L << Nand))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assoc_connectiveContext extends ParserRuleContext {
		public TerminalNode Or() { return getToken(tptpParser.Or, 0); }
		public TerminalNode And() { return getToken(tptpParser.And, 0); }
		public Assoc_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assoc_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterAssoc_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitAssoc_connective(this);
		}
	}

	public final Assoc_connectiveContext assoc_connective() throws RecognitionException {
		Assoc_connectiveContext _localctx = new Assoc_connectiveContext(_ctx, getState());
		enterRule(_localctx, 276, RULE_assoc_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1259);
			_la = _input.LA(1);
			if ( !(_la==Or || _la==And) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unary_connectiveContext extends ParserRuleContext {
		public TerminalNode Not() { return getToken(tptpParser.Not, 0); }
		public Unary_connectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_connective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterUnary_connective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitUnary_connective(this);
		}
	}

	public final Unary_connectiveContext unary_connective() throws RecognitionException {
		Unary_connectiveContext _localctx = new Unary_connectiveContext(_ctx, getState());
		enterRule(_localctx, 278, RULE_unary_connective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1261);
			match(Not);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Defined_infix_predContext extends ParserRuleContext {
		public TerminalNode Infix_equality() { return getToken(tptpParser.Infix_equality, 0); }
		public Defined_infix_predContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_infix_pred; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_infix_pred(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_infix_pred(this);
		}
	}

	public final Defined_infix_predContext defined_infix_pred() throws RecognitionException {
		Defined_infix_predContext _localctx = new Defined_infix_predContext(_ctx, getState());
		enterRule(_localctx, 280, RULE_defined_infix_pred);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1263);
			match(Infix_equality);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantContext extends ParserRuleContext {
		public FunctorContext functor() {
			return getRuleContext(FunctorContext.class,0);
		}
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitConstant(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 282, RULE_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1265);
			functor();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctorContext extends ParserRuleContext {
		public Atomic_wordContext atomic_word() {
			return getRuleContext(Atomic_wordContext.class,0);
		}
		public FunctorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFunctor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFunctor(this);
		}
	}

	public final FunctorContext functor() throws RecognitionException {
		FunctorContext _localctx = new FunctorContext(_ctx, getState());
		enterRule(_localctx, 284, RULE_functor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1267);
			atomic_word();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class System_constantContext extends ParserRuleContext {
		public System_functorContext system_functor() {
			return getRuleContext(System_functorContext.class,0);
		}
		public System_constantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_system_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterSystem_constant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitSystem_constant(this);
		}
	}

	public final System_constantContext system_constant() throws RecognitionException {
		System_constantContext _localctx = new System_constantContext(_ctx, getState());
		enterRule(_localctx, 286, RULE_system_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1269);
			system_functor();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class System_functorContext extends ParserRuleContext {
		public Atomic_system_wordContext atomic_system_word() {
			return getRuleContext(Atomic_system_wordContext.class,0);
		}
		public System_functorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_system_functor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterSystem_functor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitSystem_functor(this);
		}
	}

	public final System_functorContext system_functor() throws RecognitionException {
		System_functorContext _localctx = new System_functorContext(_ctx, getState());
		enterRule(_localctx, 288, RULE_system_functor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1271);
			atomic_system_word();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Defined_constantContext extends ParserRuleContext {
		public Defined_functorContext defined_functor() {
			return getRuleContext(Defined_functorContext.class,0);
		}
		public Defined_constantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_constant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_constant(this);
		}
	}

	public final Defined_constantContext defined_constant() throws RecognitionException {
		Defined_constantContext _localctx = new Defined_constantContext(_ctx, getState());
		enterRule(_localctx, 290, RULE_defined_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1273);
			defined_functor();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Defined_functorContext extends ParserRuleContext {
		public Atomic_defined_wordContext atomic_defined_word() {
			return getRuleContext(Atomic_defined_wordContext.class,0);
		}
		public Defined_functorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_functor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_functor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_functor(this);
		}
	}

	public final Defined_functorContext defined_functor() throws RecognitionException {
		Defined_functorContext _localctx = new Defined_functorContext(_ctx, getState());
		enterRule(_localctx, 292, RULE_defined_functor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1275);
			atomic_defined_word();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Defined_termContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode Distinct_object() { return getToken(tptpParser.Distinct_object, 0); }
		public Defined_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_term(this);
		}
	}

	public final Defined_termContext defined_term() throws RecognitionException {
		Defined_termContext _localctx = new Defined_termContext(_ctx, getState());
		enterRule(_localctx, 294, RULE_defined_term);
		try {
			setState(1279);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Real:
			case Rational:
			case Integer:
				enterOuterAlt(_localctx, 1);
				{
				setState(1277);
				number();
				}
				break;
			case Distinct_object:
				enterOuterAlt(_localctx, 2);
				{
				setState(1278);
				match(Distinct_object);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode Upper_word() { return getToken(tptpParser.Upper_word, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitVariable(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 296, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1281);
			match(Upper_word);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 29:
			return thf_or_formula_sempred((Thf_or_formulaContext)_localctx, predIndex);
		case 30:
			return thf_and_formula_sempred((Thf_and_formulaContext)_localctx, predIndex);
		case 31:
			return thf_apply_formula_sempred((Thf_apply_formulaContext)_localctx, predIndex);
		case 54:
			return thf_xprod_type_sempred((Thf_xprod_typeContext)_localctx, predIndex);
		case 55:
			return thf_union_type_sempred((Thf_union_typeContext)_localctx, predIndex);
		case 64:
			return tff_or_formula_sempred((Tff_or_formulaContext)_localctx, predIndex);
		case 65:
			return tff_and_formula_sempred((Tff_and_formulaContext)_localctx, predIndex);
		case 94:
			return tff_xprod_type_sempred((Tff_xprod_typeContext)_localctx, predIndex);
		case 100:
			return fof_or_formula_sempred((Fof_or_formulaContext)_localctx, predIndex);
		case 101:
			return fof_and_formula_sempred((Fof_and_formulaContext)_localctx, predIndex);
		case 126:
			return cnf_disjunction_sempred((Cnf_disjunctionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean thf_or_formula_sempred(Thf_or_formulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean thf_and_formula_sempred(Thf_and_formulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean thf_apply_formula_sempred(Thf_apply_formulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean thf_xprod_type_sempred(Thf_xprod_typeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean thf_union_type_sempred(Thf_union_typeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean tff_or_formula_sempred(Tff_or_formulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean tff_and_formula_sempred(Tff_and_formulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean tff_xprod_type_sempred(Tff_xprod_typeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean fof_or_formula_sempred(Fof_or_formulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean fof_and_formula_sempred(Fof_and_formulaContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean cnf_disjunction_sempred(Cnf_disjunctionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3X\u0506\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv\4"+
		"w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t\u0080"+
		"\4\u0081\t\u0081\4\u0082\t\u0082\4\u0083\t\u0083\4\u0084\t\u0084\4\u0085"+
		"\t\u0085\4\u0086\t\u0086\4\u0087\t\u0087\4\u0088\t\u0088\4\u0089\t\u0089"+
		"\4\u008a\t\u008a\4\u008b\t\u008b\4\u008c\t\u008c\4\u008d\t\u008d\4\u008e"+
		"\t\u008e\4\u008f\t\u008f\4\u0090\t\u0090\4\u0091\t\u0091\4\u0092\t\u0092"+
		"\4\u0093\t\u0093\4\u0094\t\u0094\4\u0095\t\u0095\4\u0096\t\u0096\3\2\7"+
		"\2\u012e\n\2\f\2\16\2\u0131\13\2\3\2\3\2\3\3\3\3\5\3\u0137\n\3\3\4\3\4"+
		"\3\4\3\4\5\4\u013d\n\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\7\5\u0147\n\5\f"+
		"\5\16\5\u014a\13\5\3\5\3\5\3\6\3\6\5\6\u0150\n\6\3\7\3\7\3\b\3\b\3\t\3"+
		"\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\5\f\u0160\n\f\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\5\r\u016a\n\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\5\16\u0177\n\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\5\17\u0184\n\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\5\20\u0191\n\20\3\20\3\20\3\20\3\21\3\21\3\21\5\21\u0199\n"+
		"\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\5"+
		"\25\u01a8\n\25\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u01b0\n\26\3\27\3\27"+
		"\3\27\3\27\3\27\7\27\u01b7\n\27\f\27\16\27\u01ba\13\27\3\27\3\27\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\5\30\u01d2\n\30\3\31\3\31\3\31\3\31\3\31\7\31"+
		"\u01d9\n\31\f\31\16\31\u01dc\13\31\3\31\3\31\5\31\u01e0\n\31\3\32\3\32"+
		"\5\32\u01e4\n\32\3\33\3\33\3\33\3\33\5\33\u01ea\n\33\3\34\3\34\3\34\5"+
		"\34\u01ef\n\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\5\36\u01f8\n\36\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\7\37\u0202\n\37\f\37\16\37\u0205\13"+
		"\37\3 \3 \3 \3 \3 \3 \3 \3 \7 \u020f\n \f \16 \u0212\13 \3!\3!\3!\3!\3"+
		"!\3!\3!\3!\7!\u021c\n!\f!\16!\u021f\13!\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\5\"\u022b\n\"\3#\3#\3#\3$\3$\3$\3$\3$\7$\u0235\n$\f$\16$\u0238"+
		"\13$\3$\3$\3$\3%\3%\3%\5%\u0240\n%\3&\3&\3&\3\'\3\'\3\'\3\'\5\'\u0249"+
		"\n\'\3(\3(\3(\5(\u024e\n(\3)\3)\3)\3)\3)\5)\u0255\n)\3*\3*\3*\3*\3*\5"+
		"*\u025c\n*\3+\3+\3+\3+\3+\5+\u0263\n+\3,\3,\3,\5,\u0268\n,\3-\3-\3-\3"+
		"-\3-\3-\3-\3-\3.\3.\3.\3.\3.\3.\3/\3/\3\60\3\60\3\60\3\60\3\61\3\61\3"+
		"\61\3\61\3\61\5\61\u0283\n\61\3\62\3\62\3\62\3\62\3\63\3\63\3\63\5\63"+
		"\u028c\n\63\3\64\3\64\3\65\3\65\3\66\3\66\3\66\5\66\u0295\n\66\3\67\3"+
		"\67\3\67\3\67\3\67\3\67\3\67\3\67\5\67\u029f\n\67\38\38\38\38\38\38\3"+
		"8\38\78\u02a9\n8\f8\168\u02ac\138\39\39\39\39\39\39\39\39\79\u02b6\n9"+
		"\f9\169\u02b9\139\3:\3:\3:\3:\3:\3:\3:\3:\5:\u02c3\n:\3;\3;\3;\3;\3;\3"+
		";\3;\3;\3;\3;\5;\u02cf\n;\3<\3<\3<\7<\u02d4\n<\f<\16<\u02d7\13<\3=\3="+
		"\3=\5=\u02dc\n=\3>\3>\5>\u02e0\n>\3?\3?\5?\u02e4\n?\3@\3@\3@\3@\3A\3A"+
		"\5A\u02ec\nA\3B\3B\3B\3B\3B\3B\3B\3B\7B\u02f6\nB\fB\16B\u02f9\13B\3C\3"+
		"C\3C\3C\3C\3C\3C\3C\7C\u0303\nC\fC\16C\u0306\13C\3D\3D\3D\3D\3D\3D\3D"+
		"\3D\3D\5D\u0311\nD\3E\3E\3E\3E\3E\3E\3E\3F\3F\3F\7F\u031d\nF\fF\16F\u0320"+
		"\13F\3G\3G\3G\5G\u0325\nG\3H\3H\3H\3H\5H\u032b\nH\3I\3I\3J\3J\3J\3J\3"+
		"J\3J\3J\3J\3K\3K\3K\3K\3K\3K\3K\3K\3K\3K\3K\3K\5K\u0343\nK\3L\3L\3L\3"+
		"L\3L\5L\u034a\nL\3M\3M\3M\7M\u034f\nM\fM\16M\u0352\13M\3N\3N\3N\3N\3N"+
		"\3N\3N\3N\5N\u035c\nN\3O\3O\3O\3O\3O\3O\3O\3O\5O\u0366\nO\3P\3P\3P\3P"+
		"\3P\5P\u036d\nP\3Q\3Q\3Q\7Q\u0372\nQ\fQ\16Q\u0375\13Q\3R\3R\3R\3R\3R\3"+
		"R\3R\3R\5R\u037f\nR\3S\3S\3S\3S\3S\3S\3S\3S\5S\u0389\nS\3T\3T\3T\3T\3"+
		"T\3T\3T\3T\5T\u0393\nT\3U\3U\3U\3U\3U\5U\u039a\nU\3V\3V\3V\7V\u039f\n"+
		"V\fV\16V\u03a2\13V\3W\3W\3W\3W\3W\3W\3W\3W\5W\u03ac\nW\3X\3X\5X\u03b0"+
		"\nX\3Y\3Y\3Y\3Y\3Y\3Y\3Y\5Y\u03b9\nY\3Z\3Z\3Z\3Z\3Z\3Z\3Z\5Z\u03c2\nZ"+
		"\3[\3[\3[\3[\3[\5[\u03c9\n[\3\\\3\\\3\\\3\\\3\\\5\\\u03d0\n\\\3]\3]\3"+
		"]\3]\3]\3]\5]\u03d8\n]\3]\5]\u03db\n]\3^\3^\3^\7^\u03e0\n^\f^\16^\u03e3"+
		"\13^\3_\3_\3_\3_\3`\3`\3`\3`\3`\3`\3`\3`\7`\u03f1\n`\f`\16`\u03f4\13`"+
		"\3a\3a\5a\u03f8\na\3b\3b\5b\u03fc\nb\3c\3c\5c\u0400\nc\3d\3d\3d\3d\3e"+
		"\3e\5e\u0408\ne\3f\3f\3f\3f\3f\3f\3f\3f\7f\u0412\nf\ff\16f\u0415\13f\3"+
		"g\3g\3g\3g\3g\3g\3g\3g\7g\u041f\ng\fg\16g\u0422\13g\3h\3h\3h\3h\3h\3h"+
		"\3h\5h\u042b\nh\3i\3i\3i\3i\3i\3i\3i\3j\3j\3j\7j\u0437\nj\fj\16j\u043a"+
		"\13j\3k\3k\3k\3k\5k\u0440\nk\3l\3l\3l\3l\3m\3m\3m\5m\u0449\nm\3n\3n\3"+
		"o\3o\5o\u044f\no\3p\3p\3q\3q\3q\3q\3r\3r\3s\3s\3s\3s\3s\5s\u045e\ns\3"+
		"t\3t\3t\3t\3t\5t\u0465\nt\3u\3u\3u\3u\3u\5u\u046c\nu\3v\3v\3v\7v\u0471"+
		"\nv\fv\16v\u0474\13v\3w\3w\3w\3w\3w\3w\5w\u047c\nw\3x\3x\3x\5x\u0481\n"+
		"x\3y\3y\3y\3y\3y\3y\3y\3y\3z\3z\3z\3z\3z\3z\3z\3z\3z\3z\3z\3z\5z\u0497"+
		"\nz\3{\3{\3{\3{\3{\5{\u049e\n{\3|\3|\3|\3|\3|\3|\3|\3|\5|\u04a8\n|\3}"+
		"\3}\3}\3}\3}\5}\u04af\n}\3~\3~\3~\7~\u04b4\n~\f~\16~\u04b7\13~\3\177\3"+
		"\177\3\177\3\177\3\177\5\177\u04be\n\177\3\u0080\3\u0080\3\u0080\3\u0080"+
		"\3\u0080\3\u0080\7\u0080\u04c6\n\u0080\f\u0080\16\u0080\u04c9\13\u0080"+
		"\3\u0081\3\u0081\3\u0081\3\u0081\5\u0081\u04cf\n\u0081\3\u0082\3\u0082"+
		"\3\u0082\5\u0082\u04d4\n\u0082\3\u0083\3\u0083\3\u0084\3\u0084\3\u0085"+
		"\3\u0085\3\u0085\3\u0085\5\u0085\u04de\n\u0085\3\u0086\3\u0086\5\u0086"+
		"\u04e2\n\u0086\3\u0087\3\u0087\3\u0088\3\u0088\3\u0089\3\u0089\3\u008a"+
		"\3\u008a\3\u008b\3\u008b\3\u008c\3\u008c\3\u008d\3\u008d\3\u008e\3\u008e"+
		"\3\u008f\3\u008f\3\u0090\3\u0090\3\u0091\3\u0091\3\u0092\3\u0092\3\u0093"+
		"\3\u0093\3\u0094\3\u0094\3\u0095\3\u0095\5\u0095\u0502\n\u0095\3\u0096"+
		"\3\u0096\3\u0096\2\r<>@np\u0082\u0084\u00be\u00ca\u00cc\u00fe\u0097\2"+
		"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJL"+
		"NPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e"+
		"\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6"+
		"\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be"+
		"\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6"+
		"\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8\u00ea\u00ec\u00ee"+
		"\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe\u0100\u0102\u0104\u0106"+
		"\u0108\u010a\u010c\u010e\u0110\u0112\u0114\u0116\u0118\u011a\u011c\u011e"+
		"\u0120\u0122\u0124\u0126\u0128\u012a\2\n\3\2ST\5\2??BBEE\5\2\67\6799;"+
		";\4\2\60\60\65\65\7\2//\64\6488::<<\4\2\63\63\66\66\3\2(-\3\2&\'\2\u0506"+
		"\2\u012f\3\2\2\2\4\u0136\3\2\2\2\6\u0138\3\2\2\2\b\u0141\3\2\2\2\n\u014f"+
		"\3\2\2\2\f\u0151\3\2\2\2\16\u0153\3\2\2\2\20\u0155\3\2\2\2\22\u0157\3"+
		"\2\2\2\24\u0159\3\2\2\2\26\u015f\3\2\2\2\30\u0161\3\2\2\2\32\u016e\3\2"+
		"\2\2\34\u017b\3\2\2\2\36\u0188\3\2\2\2 \u0195\3\2\2\2\"\u019a\3\2\2\2"+
		"$\u019c\3\2\2\2&\u019e\3\2\2\2(\u01a7\3\2\2\2*\u01af\3\2\2\2,\u01b1\3"+
		"\2\2\2.\u01d1\3\2\2\2\60\u01df\3\2\2\2\62\u01e3\3\2\2\2\64\u01e9\3\2\2"+
		"\2\66\u01ee\3\2\2\28\u01f0\3\2\2\2:\u01f7\3\2\2\2<\u01f9\3\2\2\2>\u0206"+
		"\3\2\2\2@\u0213\3\2\2\2B\u022a\3\2\2\2D\u022c\3\2\2\2F\u022f\3\2\2\2H"+
		"\u023c\3\2\2\2J\u0241\3\2\2\2L\u0248\3\2\2\2N\u024d\3\2\2\2P\u024f\3\2"+
		"\2\2R\u0256\3\2\2\2T\u025d\3\2\2\2V\u0267\3\2\2\2X\u0269\3\2\2\2Z\u0271"+
		"\3\2\2\2\\\u0277\3\2\2\2^\u0279\3\2\2\2`\u0282\3\2\2\2b\u0284\3\2\2\2"+
		"d\u028b\3\2\2\2f\u028d\3\2\2\2h\u028f\3\2\2\2j\u0294\3\2\2\2l\u029e\3"+
		"\2\2\2n\u02a0\3\2\2\2p\u02ad\3\2\2\2r\u02c2\3\2\2\2t\u02ce\3\2\2\2v\u02d0"+
		"\3\2\2\2x\u02db\3\2\2\2z\u02df\3\2\2\2|\u02e3\3\2\2\2~\u02e5\3\2\2\2\u0080"+
		"\u02eb\3\2\2\2\u0082\u02ed\3\2\2\2\u0084\u02fa\3\2\2\2\u0086\u0310\3\2"+
		"\2\2\u0088\u0312\3\2\2\2\u008a\u0319\3\2\2\2\u008c\u0321\3\2\2\2\u008e"+
		"\u032a\3\2\2\2\u0090\u032c\3\2\2\2\u0092\u032e\3\2\2\2\u0094\u0342\3\2"+
		"\2\2\u0096\u0349\3\2\2\2\u0098\u034b\3\2\2\2\u009a\u035b\3\2\2\2\u009c"+
		"\u0365\3\2\2\2\u009e\u036c\3\2\2\2\u00a0\u036e\3\2\2\2\u00a2\u037e\3\2"+
		"\2\2\u00a4\u0388\3\2\2\2\u00a6\u0392\3\2\2\2\u00a8\u0399\3\2\2\2\u00aa"+
		"\u039b\3\2\2\2\u00ac\u03ab\3\2\2\2\u00ae\u03af\3\2\2\2\u00b0\u03b8\3\2"+
		"\2\2\u00b2\u03ba\3\2\2\2\u00b4\u03c8\3\2\2\2\u00b6\u03cf\3\2\2\2\u00b8"+
		"\u03da\3\2\2\2\u00ba\u03dc\3\2\2\2\u00bc\u03e4\3\2\2\2\u00be\u03e8\3\2"+
		"\2\2\u00c0\u03f7\3\2\2\2\u00c2\u03fb\3\2\2\2\u00c4\u03ff\3\2\2\2\u00c6"+
		"\u0401\3\2\2\2\u00c8\u0407\3\2\2\2\u00ca\u0409\3\2\2\2\u00cc\u0416\3\2"+
		"\2\2\u00ce\u042a\3\2\2\2\u00d0\u042c\3\2\2\2\u00d2\u0433\3\2\2\2\u00d4"+
		"\u043f\3\2\2\2\u00d6\u0441\3\2\2\2\u00d8\u0448\3\2\2\2\u00da\u044a\3\2"+
		"\2\2\u00dc\u044e\3\2\2\2\u00de\u0450\3\2\2\2\u00e0\u0452\3\2\2\2\u00e2"+
		"\u0456\3\2\2\2\u00e4\u0458\3\2\2\2\u00e6\u045f\3\2\2\2\u00e8\u0466\3\2"+
		"\2\2\u00ea\u046d\3\2\2\2\u00ec\u047b\3\2\2\2\u00ee\u0480\3\2\2\2\u00f0"+
		"\u0482\3\2\2\2\u00f2\u0496\3\2\2\2\u00f4\u049d\3\2\2\2\u00f6\u04a7\3\2"+
		"\2\2\u00f8\u04ae\3\2\2\2\u00fa\u04b0\3\2\2\2\u00fc\u04bd\3\2\2\2\u00fe"+
		"\u04bf\3\2\2\2\u0100\u04ce\3\2\2\2\u0102\u04d3\3\2\2\2\u0104\u04d5\3\2"+
		"\2\2\u0106\u04d7\3\2\2\2\u0108\u04dd\3\2\2\2\u010a\u04e1\3\2\2\2\u010c"+
		"\u04e3\3\2\2\2\u010e\u04e5\3\2\2\2\u0110\u04e7\3\2\2\2\u0112\u04e9\3\2"+
		"\2\2\u0114\u04eb\3\2\2\2\u0116\u04ed\3\2\2\2\u0118\u04ef\3\2\2\2\u011a"+
		"\u04f1\3\2\2\2\u011c\u04f3\3\2\2\2\u011e\u04f5\3\2\2\2\u0120\u04f7\3\2"+
		"\2\2\u0122\u04f9\3\2\2\2\u0124\u04fb\3\2\2\2\u0126\u04fd\3\2\2\2\u0128"+
		"\u0501\3\2\2\2\u012a\u0503\3\2\2\2\u012c\u012e\5\4\3\2\u012d\u012c\3\2"+
		"\2\2\u012e\u0131\3\2\2\2\u012f\u012d\3\2\2\2\u012f\u0130\3\2\2\2\u0130"+
		"\u0132\3\2\2\2\u0131\u012f\3\2\2\2\u0132\u0133\7\2\2\3\u0133\3\3\2\2\2"+
		"\u0134\u0137\5\26\f\2\u0135\u0137\5\6\4\2\u0136\u0134\3\2\2\2\u0136\u0135"+
		"\3\2\2\2\u0137\5\3\2\2\2\u0138\u0139\7\3\2\2\u0139\u013a\7\4\2\2\u013a"+
		"\u013c\5\24\13\2\u013b\u013d\5\b\5\2\u013c\u013b\3\2\2\2\u013c\u013d\3"+
		"\2\2\2\u013d\u013e\3\2\2\2\u013e\u013f\7\5\2\2\u013f\u0140\7\6\2\2\u0140"+
		"\7\3\2\2\2\u0141\u0142\7\7\2\2\u0142\u0143\7\b\2\2\u0143\u0148\5\n\6\2"+
		"\u0144\u0145\7\7\2\2\u0145\u0147\5\n\6\2\u0146\u0144\3\2\2\2\u0147\u014a"+
		"\3\2\2\2\u0148\u0146\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u014b\3\2\2\2\u014a"+
		"\u0148\3\2\2\2\u014b\u014c\7\t\2\2\u014c\t\3\2\2\2\u014d\u0150\5\f\7\2"+
		"\u014e\u0150\7E\2\2\u014f\u014d\3\2\2\2\u014f\u014e\3\2\2\2\u0150\13\3"+
		"\2\2\2\u0151\u0152\t\2\2\2\u0152\r\3\2\2\2\u0153\u0154\7P\2\2\u0154\17"+
		"\3\2\2\2\u0155\u0156\7Q\2\2\u0156\21\3\2\2\2\u0157\u0158\t\3\2\2\u0158"+
		"\23\3\2\2\2\u0159\u015a\7T\2\2\u015a\25\3\2\2\2\u015b\u0160\5\30\r\2\u015c"+
		"\u0160\5\32\16\2\u015d\u0160\5\34\17\2\u015e\u0160\5\36\20\2\u015f\u015b"+
		"\3\2\2\2\u015f\u015c\3\2\2\2\u015f\u015d\3\2\2\2\u015f\u015e\3\2\2\2\u0160"+
		"\27\3\2\2\2\u0161\u0162\7\n\2\2\u0162\u0163\7\4\2\2\u0163\u0164\5\n\6"+
		"\2\u0164\u0165\7\7\2\2\u0165\u0166\5\"\22\2\u0166\u0167\7\7\2\2\u0167"+
		"\u0169\5\62\32\2\u0168\u016a\5 \21\2\u0169\u0168\3\2\2\2\u0169\u016a\3"+
		"\2\2\2\u016a\u016b\3\2\2\2\u016b\u016c\7\5\2\2\u016c\u016d\7\6\2\2\u016d"+
		"\31\3\2\2\2\u016e\u016f\7\13\2\2\u016f\u0170\7\4\2\2\u0170\u0171\5\n\6"+
		"\2\u0171\u0172\7\7\2\2\u0172\u0173\5\"\22\2\u0173\u0174\7\7\2\2\u0174"+
		"\u0176\5x=\2\u0175\u0177\5 \21\2\u0176\u0175\3\2\2\2\u0176\u0177\3\2\2"+
		"\2\u0177\u0178\3\2\2\2\u0178\u0179\7\5\2\2\u0179\u017a\7\6\2\2\u017a\33"+
		"\3\2\2\2\u017b\u017c\7\f\2\2\u017c\u017d\7\4\2\2\u017d\u017e\5\n\6\2\u017e"+
		"\u017f\7\7\2\2\u017f\u0180\5\"\22\2\u0180\u0181\7\7\2\2\u0181\u0183\5"+
		"\u00c0a\2\u0182\u0184\5 \21\2\u0183\u0182\3\2\2\2\u0183\u0184\3\2\2\2"+
		"\u0184\u0185\3\2\2\2\u0185\u0186\7\5\2\2\u0186\u0187\7\6\2\2\u0187\35"+
		"\3\2\2\2\u0188\u0189\7\r\2\2\u0189\u018a\7\4\2\2\u018a\u018b\5\n\6\2\u018b"+
		"\u018c\7\7\2\2\u018c\u018d\5\"\22\2\u018d\u018e\7\7\2\2\u018e\u0190\5"+
		"\u00fc\177\2\u018f\u0191\5 \21\2\u0190\u018f\3\2\2\2\u0190\u0191\3\2\2"+
		"\2\u0191\u0192\3\2\2\2\u0192\u0193\7\5\2\2\u0193\u0194\7\6\2\2\u0194\37"+
		"\3\2\2\2\u0195\u0196\7\7\2\2\u0196\u0198\5$\23\2\u0197\u0199\5&\24\2\u0198"+
		"\u0197\3\2\2\2\u0198\u0199\3\2\2\2\u0199!\3\2\2\2\u019a\u019b\7S\2\2\u019b"+
		"#\3\2\2\2\u019c\u019d\5(\25\2\u019d%\3\2\2\2\u019e\u019f\7\7\2\2\u019f"+
		"\u01a0\5\60\31\2\u01a0\'\3\2\2\2\u01a1\u01a8\5*\26\2\u01a2\u01a3\5*\26"+
		"\2\u01a3\u01a4\7\16\2\2\u01a4\u01a5\5(\25\2\u01a5\u01a8\3\2\2\2\u01a6"+
		"\u01a8\5\60\31\2\u01a7\u01a1\3\2\2\2\u01a7\u01a2\3\2\2\2\u01a7\u01a6\3"+
		"\2\2\2\u01a8)\3\2\2\2\u01a9\u01b0\5\f\7\2\u01aa\u01b0\5,\27\2\u01ab\u01b0"+
		"\5\u012a\u0096\2\u01ac\u01b0\5\22\n\2\u01ad\u01b0\7U\2\2\u01ae\u01b0\5"+
		".\30\2\u01af\u01a9\3\2\2\2\u01af\u01aa\3\2\2\2\u01af\u01ab\3\2\2\2\u01af"+
		"\u01ac\3\2\2\2\u01af\u01ad\3\2\2\2\u01af\u01ae\3\2\2\2\u01b0+\3\2\2\2"+
		"\u01b1\u01b2\5\f\7\2\u01b2\u01b3\7\4\2\2\u01b3\u01b8\5(\25\2\u01b4\u01b5"+
		"\7\7\2\2\u01b5\u01b7\5(\25\2\u01b6\u01b4\3\2\2\2\u01b7\u01ba\3\2\2\2\u01b8"+
		"\u01b6\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01bb\3\2\2\2\u01ba\u01b8\3\2"+
		"\2\2\u01bb\u01bc\7\5\2\2\u01bc-\3\2\2\2\u01bd\u01be\7\17\2\2\u01be\u01bf"+
		"\5\62\32\2\u01bf\u01c0\7\5\2\2\u01c0\u01d2\3\2\2\2\u01c1\u01c2\7\20\2"+
		"\2\u01c2\u01c3\5x=\2\u01c3\u01c4\7\5\2\2\u01c4\u01d2\3\2\2\2\u01c5\u01c6"+
		"\7\21\2\2\u01c6\u01c7\5\u00c0a\2\u01c7\u01c8\7\5\2\2\u01c8\u01d2\3\2\2"+
		"\2\u01c9\u01ca\7\22\2\2\u01ca\u01cb\5\u00ecw\2\u01cb\u01cc\7\5\2\2\u01cc"+
		"\u01d2\3\2\2\2\u01cd\u01ce\7\23\2\2\u01ce\u01cf\5\u00fc\177\2\u01cf\u01d0"+
		"\7\5\2\2\u01d0\u01d2\3\2\2\2\u01d1\u01bd\3\2\2\2\u01d1\u01c1\3\2\2\2\u01d1"+
		"\u01c5\3\2\2\2\u01d1\u01c9\3\2\2\2\u01d1\u01cd\3\2\2\2\u01d2/\3\2\2\2"+
		"\u01d3\u01e0\7\24\2\2\u01d4\u01d5\7\b\2\2\u01d5\u01da\5(\25\2\u01d6\u01d7"+
		"\7\7\2\2\u01d7\u01d9\5(\25\2\u01d8\u01d6\3\2\2\2\u01d9\u01dc\3\2\2\2\u01da"+
		"\u01d8\3\2\2\2\u01da\u01db\3\2\2\2\u01db\u01dd\3\2\2\2\u01dc\u01da\3\2"+
		"\2\2\u01dd\u01de\7\t\2\2\u01de\u01e0\3\2\2\2\u01df\u01d3\3\2\2\2\u01df"+
		"\u01d4\3\2\2\2\u01e0\61\3\2\2\2\u01e1\u01e4\5\64\33\2\u01e2\u01e4\5r:"+
		"\2\u01e3\u01e1\3\2\2\2\u01e3\u01e2\3\2\2\2\u01e4\63\3\2\2\2\u01e5\u01ea"+
		"\5\66\34\2\u01e6\u01ea\5B\"\2\u01e7\u01ea\5^\60\2\u01e8\u01ea\5b\62\2"+
		"\u01e9\u01e5\3\2\2\2\u01e9\u01e6\3\2\2\2\u01e9\u01e7\3\2\2\2\u01e9\u01e8"+
		"\3\2\2\2\u01ea\65\3\2\2\2\u01eb\u01ef\58\35\2\u01ec\u01ef\5:\36\2\u01ed"+
		"\u01ef\5j\66\2\u01ee\u01eb\3\2\2\2\u01ee\u01ec\3\2\2\2\u01ee\u01ed\3\2"+
		"\2\2\u01ef\67\3\2\2\2\u01f0\u01f1\5B\"\2\u01f1\u01f2\5\u0108\u0085\2\u01f2"+
		"\u01f3\5B\"\2\u01f39\3\2\2\2\u01f4\u01f8\5<\37\2\u01f5\u01f8\5> \2\u01f6"+
		"\u01f8\5@!\2\u01f7\u01f4\3\2\2\2\u01f7\u01f5\3\2\2\2\u01f7\u01f6\3\2\2"+
		"\2\u01f8;\3\2\2\2\u01f9\u01fa\b\37\1\2\u01fa\u01fb\5B\"\2\u01fb\u01fc"+
		"\7&\2\2\u01fc\u01fd\5B\"\2\u01fd\u0203\3\2\2\2\u01fe\u01ff\f\3\2\2\u01ff"+
		"\u0200\7&\2\2\u0200\u0202\5B\"\2\u0201\u01fe\3\2\2\2\u0202\u0205\3\2\2"+
		"\2\u0203\u0201\3\2\2\2\u0203\u0204\3\2\2\2\u0204=\3\2\2\2\u0205\u0203"+
		"\3\2\2\2\u0206\u0207\b \1\2\u0207\u0208\5B\"\2\u0208\u0209\7\'\2\2\u0209"+
		"\u020a\5B\"\2\u020a\u0210\3\2\2\2\u020b\u020c\f\3\2\2\u020c\u020d\7\'"+
		"\2\2\u020d\u020f\5B\"\2\u020e\u020b\3\2\2\2\u020f\u0212\3\2\2\2\u0210"+
		"\u020e\3\2\2\2\u0210\u0211\3\2\2\2\u0211?\3\2\2\2\u0212\u0210\3\2\2\2"+
		"\u0213\u0214\b!\1\2\u0214\u0215\5B\"\2\u0215\u0216\7=\2\2\u0216\u0217"+
		"\5B\"\2\u0217\u021d\3\2\2\2\u0218\u0219\f\3\2\2\u0219\u021a\7=\2\2\u021a"+
		"\u021c\5B\"\2\u021b\u0218\3\2\2\2\u021c\u021f\3\2\2\2\u021d\u021b\3\2"+
		"\2\2\u021d\u021e\3\2\2\2\u021eA\3\2\2\2\u021f\u021d\3\2\2\2\u0220\u022b"+
		"\5D#\2\u0221\u022b\5J&\2\u0222\u022b\5L\'\2\u0223\u022b\5X-\2\u0224\u022b"+
		"\5Z.\2\u0225\u022b\5t;\2\u0226\u0227\7\4\2\2\u0227\u0228\5\64\33\2\u0228"+
		"\u0229\7\5\2\2\u0229\u022b\3\2\2\2\u022a\u0220\3\2\2\2\u022a\u0221\3\2"+
		"\2\2\u022a\u0222\3\2\2\2\u022a\u0223\3\2\2\2\u022a\u0224\3\2\2\2\u022a"+
		"\u0225\3\2\2\2\u022a\u0226\3\2\2\2\u022bC\3\2\2\2\u022c\u022d\5F$\2\u022d"+
		"\u022e\5B\"\2\u022eE\3\2\2\2\u022f\u0230\5\u0102\u0082\2\u0230\u0231\7"+
		"\b\2\2\u0231\u0236\5H%\2\u0232\u0233\7\7\2\2\u0233\u0235\5H%\2\u0234\u0232"+
		"\3\2\2\2\u0235\u0238\3\2\2\2\u0236\u0234\3\2\2\2\u0236\u0237\3\2\2\2\u0237"+
		"\u0239\3\2\2\2\u0238\u0236\3\2\2\2\u0239\u023a\7\t\2\2\u023a\u023b\7\16"+
		"\2\2\u023bG\3\2\2\2\u023c\u023f\5\u012a\u0096\2\u023d\u023e\7\16\2\2\u023e"+
		"\u0240\5d\63\2\u023f\u023d\3\2\2\2\u023f\u0240\3\2\2\2\u0240I\3\2\2\2"+
		"\u0241\u0242\5\u010a\u0086\2\u0242\u0243\5B\"\2\u0243K\3\2\2\2\u0244\u0249"+
		"\5N(\2\u0245\u0249\5\u012a\u0096\2\u0246\u0249\5\u0128\u0095\2\u0247\u0249"+
		"\5V,\2\u0248\u0244\3\2\2\2\u0248\u0245\3\2\2\2\u0248\u0246\3\2\2\2\u0248"+
		"\u0247\3\2\2\2\u0249M\3\2\2\2\u024a\u024e\5P)\2\u024b\u024e\5R*\2\u024c"+
		"\u024e\5T+\2\u024d\u024a\3\2\2\2\u024d\u024b\3\2\2\2\u024d\u024c\3\2\2"+
		"\2\u024eO\3\2\2\2\u024f\u0254\5\u011e\u0090\2\u0250\u0251\7\4\2\2\u0251"+
		"\u0252\5\\/\2\u0252\u0253\7\5\2\2\u0253\u0255\3\2\2\2\u0254\u0250\3\2"+
		"\2\2\u0254\u0255\3\2\2\2\u0255Q\3\2\2\2\u0256\u025b\5\u0126\u0094\2\u0257"+
		"\u0258\7\4\2\2\u0258\u0259\5\\/\2\u0259\u025a\7\5\2\2\u025a\u025c\3\2"+
		"\2\2\u025b\u0257\3\2\2\2\u025b\u025c\3\2\2\2\u025cS\3\2\2\2\u025d\u0262"+
		"\5\u0122\u0092\2\u025e\u025f\7\4\2\2\u025f\u0260\5\\/\2\u0260\u0261\7"+
		"\5\2\2\u0261\u0263\3\2\2\2\u0262\u025e\3\2\2\2\u0262\u0263\3\2\2\2\u0263"+
		"U\3\2\2\2\u0264\u0268\5\u0108\u0085\2\u0265\u0268\5\u0116\u008c\2\u0266"+
		"\u0268\5\u010a\u0086\2\u0267\u0264\3\2\2\2\u0267\u0265\3\2\2\2\u0267\u0266"+
		"\3\2\2\2\u0268W\3\2\2\2\u0269\u026a\7\25\2\2\u026a\u026b\5\64\33\2\u026b"+
		"\u026c\7\7\2\2\u026c\u026d\5\64\33\2\u026d\u026e\7\7\2\2\u026e\u026f\5"+
		"\64\33\2\u026f\u0270\7\5\2\2\u0270Y\3\2\2\2\u0271\u0272\7\26\2\2\u0272"+
		"\u0273\5B\"\2\u0273\u0274\7\7\2\2\u0274\u0275\5\62\32\2\u0275\u0276\7"+
		"\5\2\2\u0276[\3\2\2\2\u0277\u0278\5v<\2\u0278]\3\2\2\2\u0279\u027a\5`"+
		"\61\2\u027a\u027b\7\16\2\2\u027b\u027c\5d\63\2\u027c_\3\2\2\2\u027d\u0283"+
		"\5L\'\2\u027e\u027f\7\4\2\2\u027f\u0280\5\64\33\2\u0280\u0281\7\5\2\2"+
		"\u0281\u0283\3\2\2\2\u0282\u027d\3\2\2\2\u0282\u027e\3\2\2\2\u0283a\3"+
		"\2\2\2\u0284\u0285\5L\'\2\u0285\u0286\7\27\2\2\u0286\u0287\5L\'\2\u0287"+
		"c\3\2\2\2\u0288\u028c\5f\64\2\u0289\u028c\5l\67\2\u028a\u028c\5h\65\2"+
		"\u028b\u0288\3\2\2\2\u028b\u0289\3\2\2\2\u028b\u028a\3\2\2\2\u028ce\3"+
		"\2\2\2\u028d\u028e\5B\"\2\u028eg\3\2\2\2\u028f\u0290\5@!\2\u0290i\3\2"+
		"\2\2\u0291\u0295\5l\67\2\u0292\u0295\5n8\2\u0293\u0295\5p9\2\u0294\u0291"+
		"\3\2\2\2\u0294\u0292\3\2\2\2\u0294\u0293\3\2\2\2\u0295k\3\2\2\2\u0296"+
		"\u0297\5f\64\2\u0297\u0298\7\30\2\2\u0298\u0299\5f\64\2\u0299\u029f\3"+
		"\2\2\2\u029a\u029b\5f\64\2\u029b\u029c\7\30\2\2\u029c\u029d\5l\67\2\u029d"+
		"\u029f\3\2\2\2\u029e\u0296\3\2\2\2\u029e\u029a\3\2\2\2\u029fm\3\2\2\2"+
		"\u02a0\u02a1\b8\1\2\u02a1\u02a2\5f\64\2\u02a2\u02a3\7\31\2\2\u02a3\u02a4"+
		"\5f\64\2\u02a4\u02aa\3\2\2\2\u02a5\u02a6\f\3\2\2\u02a6\u02a7\7\31\2\2"+
		"\u02a7\u02a9\5f\64\2\u02a8\u02a5\3\2\2\2\u02a9\u02ac\3\2\2\2\u02aa\u02a8"+
		"\3\2\2\2\u02aa\u02ab\3\2\2\2\u02abo\3\2\2\2\u02ac\u02aa\3\2\2\2\u02ad"+
		"\u02ae\b9\1\2\u02ae\u02af\5f\64\2\u02af\u02b0\7\32\2\2\u02b0\u02b1\5f"+
		"\64\2\u02b1\u02b7\3\2\2\2\u02b2\u02b3\f\3\2\2\u02b3\u02b4\7\32\2\2\u02b4"+
		"\u02b6\5f\64\2\u02b5\u02b2\3\2\2\2\u02b6\u02b9\3\2\2\2\u02b7\u02b5\3\2"+
		"\2\2\u02b7\u02b8\3\2\2\2\u02b8q\3\2\2\2\u02b9\u02b7\3\2\2\2\u02ba\u02bb"+
		"\5t;\2\u02bb\u02bc\7\33\2\2\u02bc\u02bd\5t;\2\u02bd\u02c3\3\2\2\2\u02be"+
		"\u02bf\7\4\2\2\u02bf\u02c0\5r:\2\u02c0\u02c1\7\5\2\2\u02c1\u02c3\3\2\2"+
		"\2\u02c2\u02ba\3\2\2\2\u02c2\u02be\3\2\2\2\u02c3s\3\2\2\2\u02c4\u02cf"+
		"\7\24\2\2\u02c5\u02c6\7\b\2\2\u02c6\u02c7\5v<\2\u02c7\u02c8\7\t\2\2\u02c8"+
		"\u02cf\3\2\2\2\u02c9\u02cf\7\34\2\2\u02ca\u02cb\7\35\2\2\u02cb\u02cc\5"+
		"v<\2\u02cc\u02cd\7\36\2\2\u02cd\u02cf\3\2\2\2\u02ce\u02c4\3\2\2\2\u02ce"+
		"\u02c5\3\2\2\2\u02ce\u02c9\3\2\2\2\u02ce\u02ca\3\2\2\2\u02cfu\3\2\2\2"+
		"\u02d0\u02d5\5\64\33\2\u02d1\u02d2\7\7\2\2\u02d2\u02d4\5\64\33\2\u02d3"+
		"\u02d1\3\2\2\2\u02d4\u02d7\3\2\2\2\u02d5\u02d3\3\2\2\2\u02d5\u02d6\3\2"+
		"\2\2\u02d6w\3\2\2\2\u02d7\u02d5\3\2\2\2\u02d8\u02dc\5z>\2\u02d9\u02dc"+
		"\5\u00acW\2\u02da\u02dc\5\u00a6T\2\u02db\u02d8\3\2\2\2\u02db\u02d9\3\2"+
		"\2\2\u02db\u02da\3\2\2\2\u02dcy\3\2\2\2\u02dd\u02e0\5|?\2\u02de\u02e0"+
		"\5\u0086D\2\u02df\u02dd\3\2\2\2\u02df\u02de\3\2\2\2\u02e0{\3\2\2\2\u02e1"+
		"\u02e4\5~@\2\u02e2\u02e4\5\u0080A\2\u02e3\u02e1\3\2\2\2\u02e3\u02e2\3"+
		"\2\2\2\u02e4}\3\2\2\2\u02e5\u02e6\5\u0086D\2\u02e6\u02e7\5\u0114\u008b"+
		"\2\u02e7\u02e8\5\u0086D\2\u02e8\177\3\2\2\2\u02e9\u02ec\5\u0082B\2\u02ea"+
		"\u02ec\5\u0084C\2\u02eb\u02e9\3\2\2\2\u02eb\u02ea\3\2\2\2\u02ec\u0081"+
		"\3\2\2\2\u02ed\u02ee\bB\1\2\u02ee\u02ef\5\u0086D\2\u02ef\u02f0\7&\2\2"+
		"\u02f0\u02f1\5\u0086D\2\u02f1\u02f7\3\2\2\2\u02f2\u02f3\f\3\2\2\u02f3"+
		"\u02f4\7&\2\2\u02f4\u02f6\5\u0086D\2\u02f5\u02f2\3\2\2\2\u02f6\u02f9\3"+
		"\2\2\2\u02f7\u02f5\3\2\2\2\u02f7\u02f8\3\2\2\2\u02f8\u0083\3\2\2\2\u02f9"+
		"\u02f7\3\2\2\2\u02fa\u02fb\bC\1\2\u02fb\u02fc\5\u0086D\2\u02fc\u02fd\7"+
		"\'\2\2\u02fd\u02fe\5\u0086D\2\u02fe\u0304\3\2\2\2\u02ff\u0300\f\3\2\2"+
		"\u0300\u0301\7\'\2\2\u0301\u0303\5\u0086D\2\u0302\u02ff\3\2\2\2\u0303"+
		"\u0306\3\2\2\2\u0304\u0302\3\2\2\2\u0304\u0305\3\2\2\2\u0305\u0085\3\2"+
		"\2\2\u0306\u0304\3\2\2\2\u0307\u0311\5\u0088E\2\u0308\u0311\5\u008eH\2"+
		"\u0309\u0311\5\u0090I\2\u030a\u0311\5\u0092J\2\u030b\u0311\5\u0094K\2"+
		"\u030c\u030d\7\4\2\2\u030d\u030e\5z>\2\u030e\u030f\7\5\2\2\u030f\u0311"+
		"\3\2\2\2\u0310\u0307\3\2\2\2\u0310\u0308\3\2\2\2\u0310\u0309\3\2\2\2\u0310"+
		"\u030a\3\2\2\2\u0310\u030b\3\2\2\2\u0310\u030c\3\2\2\2\u0311\u0087\3\2"+
		"\2\2\u0312\u0313\5\u0112\u008a\2\u0313\u0314\7\b\2\2\u0314\u0315\5\u008a"+
		"F\2\u0315\u0316\7\t\2\2\u0316\u0317\7\16\2\2\u0317\u0318\5\u0086D\2\u0318"+
		"\u0089\3\2\2\2\u0319\u031e\5\u008cG\2\u031a\u031b\7\7\2\2\u031b\u031d"+
		"\5\u008cG\2\u031c\u031a\3\2\2\2\u031d\u0320\3\2\2\2\u031e\u031c\3\2\2"+
		"\2\u031e\u031f\3\2\2\2\u031f\u008b\3\2\2\2\u0320\u031e\3\2\2\2\u0321\u0324"+
		"\5\u012a\u0096\2\u0322\u0323\7\16\2\2\u0323\u0325\5\u00b8]\2\u0324\u0322"+
		"\3\2\2\2\u0324\u0325\3\2\2\2\u0325\u008d\3\2\2\2\u0326\u0327\5\u0118\u008d"+
		"\2\u0327\u0328\5\u0086D\2\u0328\u032b\3\2\2\2\u0329\u032b\5\u00d6l\2\u032a"+
		"\u0326\3\2\2\2\u032a\u0329\3\2\2\2\u032b\u008f\3\2\2\2\u032c\u032d\5\u00d8"+
		"m\2\u032d\u0091\3\2\2\2\u032e\u032f\7\37\2\2\u032f\u0330\5z>\2\u0330\u0331"+
		"\7\7\2\2\u0331\u0332\5z>\2\u0332\u0333\7\7\2\2\u0333\u0334\5z>\2\u0334"+
		"\u0335\7\5\2\2\u0335\u0093\3\2\2\2\u0336\u0337\7 \2\2\u0337\u0338\5\u0096"+
		"L\2\u0338\u0339\7\7\2\2\u0339\u033a\5x=\2\u033a\u033b\7\5\2\2\u033b\u0343"+
		"\3\2\2\2\u033c\u033d\7!\2\2\u033d\u033e\5\u009eP\2\u033e\u033f\7\7\2\2"+
		"\u033f\u0340\5x=\2\u0340\u0341\7\5\2\2\u0341\u0343\3\2\2\2\u0342\u0336"+
		"\3\2\2\2\u0342\u033c\3\2\2\2\u0343\u0095\3\2\2\2\u0344\u034a\5\u009aN"+
		"\2\u0345\u0346\7\b\2\2\u0346\u0347\5\u0098M\2\u0347\u0348\7\t\2\2\u0348"+
		"\u034a\3\2\2\2\u0349\u0344\3\2\2\2\u0349\u0345\3\2\2\2\u034a\u0097\3\2"+
		"\2\2\u034b\u0350\5\u009aN\2\u034c\u034d\7\7\2\2\u034d\u034f\5\u009aN\2"+
		"\u034e\u034c\3\2\2\2\u034f\u0352\3\2\2\2\u0350\u034e\3\2\2\2\u0350\u0351"+
		"\3\2\2\2\u0351\u0099\3\2\2\2\u0352\u0350\3\2\2\2\u0353\u0354\7\63\2\2"+
		"\u0354\u0355\7\b\2\2\u0355\u0356\5\u008aF\2\u0356\u0357\7\t\2\2\u0357"+
		"\u0358\7\16\2\2\u0358\u0359\5\u009cO\2\u0359\u035c\3\2\2\2\u035a\u035c"+
		"\5\u009cO\2\u035b\u0353\3\2\2\2\u035b\u035a\3\2\2\2\u035c\u009b\3\2\2"+
		"\2\u035d\u035e\5\u00e4s\2\u035e\u035f\7\62\2\2\u035f\u0360\5\u00ecw\2"+
		"\u0360\u0366\3\2\2\2\u0361\u0362\7\4\2\2\u0362\u0363\5\u009cO\2\u0363"+
		"\u0364\7\5\2\2\u0364\u0366\3\2\2\2\u0365\u035d\3\2\2\2\u0365\u0361\3\2"+
		"\2\2\u0366\u009d\3\2\2\2\u0367\u036d\5\u00a2R\2\u0368\u0369\7\b\2\2\u0369"+
		"\u036a\5\u00a0Q\2\u036a\u036b\7\t\2\2\u036b\u036d\3\2\2\2\u036c\u0367"+
		"\3\2\2\2\u036c\u0368\3\2\2\2\u036d\u009f\3\2\2\2\u036e\u0373\5\u00a2R"+
		"\2\u036f\u0370\7\7\2\2\u0370\u0372\5\u00a2R\2\u0371\u036f\3\2\2\2\u0372"+
		"\u0375\3\2\2\2\u0373\u0371\3\2\2\2\u0373\u0374\3\2\2\2\u0374\u00a1\3\2"+
		"\2\2\u0375\u0373\3\2\2\2\u0376\u0377\7\63\2\2\u0377\u0378\7\b\2\2\u0378"+
		"\u0379\5\u008aF\2\u0379\u037a\7\t\2\2\u037a\u037b\7\16\2\2\u037b\u037c"+
		"\5\u00a4S\2\u037c\u037f\3\2\2\2\u037d\u037f\5\u00a4S\2\u037e\u0376\3\2"+
		"\2\2\u037e\u037d\3\2\2\2\u037f\u00a3\3\2\2\2\u0380\u0381\5\u00dan\2\u0381"+
		"\u0382\7(\2\2\u0382\u0383\5\u0086D\2\u0383\u0389\3\2\2\2\u0384\u0385\7"+
		"\4\2\2\u0385\u0386\5\u00a4S\2\u0386\u0387\7\5\2\2\u0387\u0389\3\2\2\2"+
		"\u0388\u0380\3\2\2\2\u0388\u0384\3\2\2\2\u0389\u00a5\3\2\2\2\u038a\u038b"+
		"\5\u00a8U\2\u038b\u038c\7\"\2\2\u038c\u038d\5\u00a8U\2\u038d\u0393\3\2"+
		"\2\2\u038e\u038f\7\4\2\2\u038f\u0390\5\u00a6T\2\u0390\u0391\7\5\2\2\u0391"+
		"\u0393\3\2\2\2\u0392\u038a\3\2\2\2\u0392\u038e\3\2\2\2\u0393\u00a7\3\2"+
		"\2\2\u0394\u039a\7\24\2\2\u0395\u0396\7\b\2\2\u0396\u0397\5\u00aaV\2\u0397"+
		"\u0398\7\t\2\2\u0398\u039a\3\2\2\2\u0399\u0394\3\2\2\2\u0399\u0395\3\2"+
		"\2\2\u039a\u00a9\3\2\2\2\u039b\u03a0\5z>\2\u039c\u039d\7\7\2\2\u039d\u039f"+
		"\5z>\2\u039e\u039c\3\2\2\2\u039f\u03a2\3\2\2\2\u03a0\u039e\3\2\2\2\u03a0"+
		"\u03a1\3\2\2\2\u03a1\u00ab\3\2\2\2\u03a2\u03a0\3\2\2\2\u03a3\u03a4\5\u00ae"+
		"X\2\u03a4\u03a5\7\16\2\2\u03a5\u03a6\5\u00b0Y\2\u03a6\u03ac\3\2\2\2\u03a7"+
		"\u03a8\7\4\2\2\u03a8\u03a9\5\u00acW\2\u03a9\u03aa\7\5\2\2\u03aa\u03ac"+
		"\3\2\2\2\u03ab\u03a3\3\2\2\2\u03ab\u03a7\3\2\2\2\u03ac\u00ad\3\2\2\2\u03ad"+
		"\u03b0\5\u011e\u0090\2\u03ae\u03b0\5\u0122\u0092\2\u03af\u03ad\3\2\2\2"+
		"\u03af\u03ae\3\2\2\2\u03b0\u00af\3\2\2\2\u03b1\u03b9\5\u00b8]\2\u03b2"+
		"\u03b9\5\u00bc_\2\u03b3\u03b9\5\u00b2Z\2\u03b4\u03b5\7\4\2\2\u03b5\u03b6"+
		"\5\u00b0Y\2\u03b6\u03b7\7\5\2\2\u03b7\u03b9\3\2\2\2\u03b8\u03b1\3\2\2"+
		"\2\u03b8\u03b2\3\2\2\2\u03b8\u03b3\3\2\2\2\u03b8\u03b4\3\2\2\2\u03b9\u00b1"+
		"\3\2\2\2\u03ba\u03bb\7\60\2\2\u03bb\u03bc\7\b\2\2\u03bc\u03bd\5\u008a"+
		"F\2\u03bd\u03be\7\t\2\2\u03be\u03c1\7\16\2\2\u03bf\u03c2\5\u00b4[\2\u03c0"+
		"\u03c2\5\u00b2Z\2\u03c1\u03bf\3\2\2\2\u03c1\u03c0\3\2\2\2\u03c2\u00b3"+
		"\3\2\2\2\u03c3\u03c9\5\u00b8]\2\u03c4\u03c5\7\4\2\2\u03c5\u03c6\5\u00bc"+
		"_\2\u03c6\u03c7\7\5\2\2\u03c7\u03c9\3\2\2\2\u03c8\u03c3\3\2\2\2\u03c8"+
		"\u03c4\3\2\2\2\u03c9\u00b5\3\2\2\2\u03ca\u03d0\5\u00b8]\2\u03cb\u03cc"+
		"\7\4\2\2\u03cc\u03cd\5\u00be`\2\u03cd\u03ce\7\5\2\2\u03ce\u03d0\3\2\2"+
		"\2\u03cf\u03ca\3\2\2\2\u03cf\u03cb\3\2\2\2\u03d0\u00b7\3\2\2\2\u03d1\u03db"+
		"\5\u0110\u0089\2\u03d2\u03d7\5\u010e\u0088\2\u03d3\u03d4\7\4\2\2\u03d4"+
		"\u03d5\5\u00ba^\2\u03d5\u03d6\7\5\2\2\u03d6\u03d8\3\2\2\2\u03d7\u03d3"+
		"\3\2\2\2\u03d7\u03d8\3\2\2\2\u03d8\u03db\3\2\2\2\u03d9\u03db\5\u012a\u0096"+
		"\2\u03da\u03d1\3\2\2\2\u03da\u03d2\3\2\2\2\u03da\u03d9\3\2\2\2\u03db\u00b9"+
		"\3\2\2\2\u03dc\u03e1\5\u00b8]\2\u03dd\u03de\7\7\2\2\u03de\u03e0\5\u00b8"+
		"]\2\u03df\u03dd\3\2\2\2\u03e0\u03e3\3\2\2\2\u03e1\u03df\3\2\2\2\u03e1"+
		"\u03e2\3\2\2\2\u03e2\u00bb\3\2\2\2\u03e3\u03e1\3\2\2\2\u03e4\u03e5\5\u00b6"+
		"\\\2\u03e5\u03e6\7\30\2\2\u03e6\u03e7\5\u00b8]\2\u03e7\u00bd\3\2\2\2\u03e8"+
		"\u03e9\b`\1\2\u03e9\u03ea\5\u00b6\\\2\u03ea\u03eb\7\31\2\2\u03eb\u03ec"+
		"\5\u00b8]\2\u03ec\u03f2\3\2\2\2\u03ed\u03ee\f\3\2\2\u03ee\u03ef\7\31\2"+
		"\2\u03ef\u03f1\5\u00b8]\2\u03f0\u03ed\3\2\2\2\u03f1\u03f4\3\2\2\2\u03f2"+
		"\u03f0\3\2\2\2\u03f2\u03f3\3\2\2\2\u03f3\u00bf\3\2\2\2\u03f4\u03f2\3\2"+
		"\2\2\u03f5\u03f8\5\u00c2b\2\u03f6\u03f8\5\u00f6|\2\u03f7\u03f5\3\2\2\2"+
		"\u03f7\u03f6\3\2\2\2\u03f8\u00c1\3\2\2\2\u03f9\u03fc\5\u00c4c\2\u03fa"+
		"\u03fc\5\u00ceh\2\u03fb\u03f9\3\2\2\2\u03fb\u03fa\3\2\2\2\u03fc\u00c3"+
		"\3\2\2\2\u03fd\u0400\5\u00c6d\2\u03fe\u0400\5\u00c8e\2\u03ff\u03fd\3\2"+
		"\2\2\u03ff\u03fe\3\2\2\2\u0400\u00c5\3\2\2\2\u0401\u0402\5\u00ceh\2\u0402"+
		"\u0403\5\u0114\u008b\2\u0403\u0404\5\u00ceh\2\u0404\u00c7\3\2\2\2\u0405"+
		"\u0408\5\u00caf\2\u0406\u0408\5\u00ccg\2\u0407\u0405\3\2\2\2\u0407\u0406"+
		"\3\2\2\2\u0408\u00c9\3\2\2\2\u0409\u040a\bf\1\2\u040a\u040b\5\u00ceh\2"+
		"\u040b\u040c\7&\2\2\u040c\u040d\5\u00ceh\2\u040d\u0413\3\2\2\2\u040e\u040f"+
		"\f\3\2\2\u040f\u0410\7&\2\2\u0410\u0412\5\u00ceh\2\u0411\u040e\3\2\2\2"+
		"\u0412\u0415\3\2\2\2\u0413\u0411\3\2\2\2\u0413\u0414\3\2\2\2\u0414\u00cb"+
		"\3\2\2\2\u0415\u0413\3\2\2\2\u0416\u0417\bg\1\2\u0417\u0418\5\u00ceh\2"+
		"\u0418\u0419\7\'\2\2\u0419\u041a\5\u00ceh\2\u041a\u0420\3\2\2\2\u041b"+
		"\u041c\f\3\2\2\u041c\u041d\7\'\2\2\u041d\u041f\5\u00ceh\2\u041e\u041b"+
		"\3\2\2\2\u041f\u0422\3\2\2\2\u0420\u041e\3\2\2\2\u0420\u0421\3\2\2\2\u0421"+
		"\u00cd\3\2\2\2\u0422\u0420\3\2\2\2\u0423\u042b\5\u00d0i\2\u0424\u042b"+
		"\5\u00d4k\2\u0425\u042b\5\u00d8m\2\u0426\u0427\7\4\2\2\u0427\u0428\5\u00c2"+
		"b\2\u0428\u0429\7\5\2\2\u0429\u042b\3\2\2\2\u042a\u0423\3\2\2\2\u042a"+
		"\u0424\3\2\2\2\u042a\u0425\3\2\2\2\u042a\u0426\3\2\2\2\u042b\u00cf\3\2"+
		"\2\2\u042c\u042d\5\u0112\u008a\2\u042d\u042e\7\b\2\2\u042e\u042f\5\u00d2"+
		"j\2\u042f\u0430\7\t\2\2\u0430\u0431\7\16\2\2\u0431\u0432\5\u00ceh\2\u0432"+
		"\u00d1\3\2\2\2\u0433\u0438\5\u012a\u0096\2\u0434\u0435\7\7\2\2\u0435\u0437"+
		"\5\u012a\u0096\2\u0436\u0434\3\2\2\2\u0437\u043a\3\2\2\2\u0438\u0436\3"+
		"\2\2\2\u0438\u0439\3\2\2\2\u0439\u00d3\3\2\2\2\u043a\u0438\3\2\2\2\u043b"+
		"\u043c\5\u0118\u008d\2\u043c\u043d\5\u00ceh\2\u043d\u0440\3\2\2\2\u043e"+
		"\u0440\5\u00d6l\2\u043f\u043b\3\2\2\2\u043f\u043e\3\2\2\2\u0440\u00d5"+
		"\3\2\2\2\u0441\u0442\5\u00ecw\2\u0442\u0443\7\61\2\2\u0443\u0444\5\u00ec"+
		"w\2\u0444\u00d7\3\2\2\2\u0445\u0449\5\u00dan\2\u0446\u0449\5\u00dco\2"+
		"\u0447\u0449\5\u00e2r\2\u0448\u0445\3\2\2\2\u0448\u0446\3\2\2\2\u0448"+
		"\u0447\3\2\2\2\u0449\u00d9\3\2\2\2\u044a\u044b\5\u00e4s\2\u044b\u00db"+
		"\3\2\2\2\u044c\u044f\5\u00dep\2\u044d\u044f\5\u00e0q\2\u044e\u044c\3\2"+
		"\2\2\u044e\u044d\3\2\2\2\u044f\u00dd\3\2\2\2\u0450\u0451\5\u00e6t\2\u0451"+
		"\u00df\3\2\2\2\u0452\u0453\5\u00ecw\2\u0453\u0454\5\u011a\u008e\2\u0454"+
		"\u0455\5\u00ecw\2\u0455\u00e1\3\2\2\2\u0456\u0457\5\u00e8u\2\u0457\u00e3"+
		"\3\2\2\2\u0458\u045d\5\u011e\u0090\2\u0459\u045a\7\4\2\2\u045a\u045b\5"+
		"\u00eav\2\u045b\u045c\7\5\2\2\u045c\u045e\3\2\2\2\u045d\u0459\3\2\2\2"+
		"\u045d\u045e\3\2\2\2\u045e\u00e5\3\2\2\2\u045f\u0464\5\u0126\u0094\2\u0460"+
		"\u0461\7\4\2\2\u0461\u0462\5\u00eav\2\u0462\u0463\7\5\2\2\u0463\u0465"+
		"\3\2\2\2\u0464\u0460\3\2\2\2\u0464\u0465\3\2\2\2\u0465\u00e7\3\2\2\2\u0466"+
		"\u046b\5\u0122\u0092\2\u0467\u0468\7\4\2\2\u0468\u0469\5\u00eav\2\u0469"+
		"\u046a\7\5\2\2\u046a\u046c\3\2\2\2\u046b\u0467\3\2\2\2\u046b\u046c\3\2"+
		"\2\2\u046c\u00e9\3\2\2\2\u046d\u0472\5\u00ecw\2\u046e\u046f\7\7\2\2\u046f"+
		"\u0471\5\u00ecw\2\u0470\u046e\3\2\2\2\u0471\u0474\3\2\2\2\u0472\u0470"+
		"\3\2\2\2\u0472\u0473\3\2\2\2\u0473\u00eb\3\2\2\2\u0474\u0472\3\2\2\2\u0475"+
		"\u047c\5\u0128\u0095\2\u0476\u047c\5\u00eex\2\u0477\u047c\5\u012a\u0096"+
		"\2\u0478\u047c\5\u00f0y\2\u0479\u047c\5\u00f2z\2\u047a\u047c\5\u00f4{"+
		"\2\u047b\u0475\3\2\2\2\u047b\u0476\3\2\2\2\u047b\u0477\3\2\2\2\u047b\u0478"+
		"\3\2\2\2\u047b\u0479\3\2\2\2\u047b\u047a\3\2\2\2\u047c\u00ed\3\2\2\2\u047d"+
		"\u0481\5\u00e4s\2\u047e\u0481\5\u00e6t\2\u047f\u0481\5\u00e8u\2\u0480"+
		"\u047d\3\2\2\2\u0480\u047e\3\2\2\2\u0480\u047f\3\2\2\2\u0481\u00ef\3\2"+
		"\2\2\u0482\u0483\7#\2\2\u0483\u0484\5z>\2\u0484\u0485\7\7\2\2\u0485\u0486"+
		"\5\u00ecw\2\u0486\u0487\7\7\2\2\u0487\u0488\5\u00ecw\2\u0488\u0489\7\5"+
		"\2\2\u0489\u00f1\3\2\2\2\u048a\u048b\7$\2\2\u048b\u048c\5\u009eP\2\u048c"+
		"\u048d\7\7\2\2\u048d\u048e\5\u00ecw\2\u048e\u048f\7\5\2\2\u048f\u0497"+
		"\3\2\2\2\u0490\u0491\7%\2\2\u0491\u0492\5\u0096L\2\u0492\u0493\7\7\2\2"+
		"\u0493\u0494\5\u00ecw\2\u0494\u0495\7\5\2\2\u0495\u0497\3\2\2\2\u0496"+
		"\u048a\3\2\2\2\u0496\u0490\3\2\2\2\u0497\u00f3\3\2\2\2\u0498\u049e\7\34"+
		"\2\2\u0499\u049a\7\35\2\2\u049a\u049b\5\u00eav\2\u049b\u049c\7\36\2\2"+
		"\u049c\u049e\3\2\2\2\u049d\u0498\3\2\2\2\u049d\u0499\3\2\2\2\u049e\u00f5"+
		"\3\2\2\2\u049f\u04a0\5\u00f8}\2\u04a0\u04a1\7\"\2\2\u04a1\u04a2\5\u00f8"+
		"}\2\u04a2\u04a8\3\2\2\2\u04a3\u04a4\7\4\2\2\u04a4\u04a5\5\u00f6|\2\u04a5"+
		"\u04a6\7\5\2\2\u04a6\u04a8\3\2\2\2\u04a7\u049f\3\2\2\2\u04a7\u04a3\3\2"+
		"\2\2\u04a8\u00f7\3\2\2\2\u04a9\u04af\7\24\2\2\u04aa\u04ab\7\b\2\2\u04ab"+
		"\u04ac\5\u00fa~\2\u04ac\u04ad\7\t\2\2\u04ad\u04af\3\2\2\2\u04ae\u04a9"+
		"\3\2\2\2\u04ae\u04aa\3\2\2\2\u04af\u00f9\3\2\2\2\u04b0\u04b5\5\u00c2b"+
		"\2\u04b1\u04b2\7\7\2\2\u04b2\u04b4\5\u00c2b\2\u04b3\u04b1\3\2\2\2\u04b4"+
		"\u04b7\3\2\2\2\u04b5\u04b3\3\2\2\2\u04b5\u04b6\3\2\2\2\u04b6\u00fb\3\2"+
		"\2\2\u04b7\u04b5\3\2\2\2\u04b8\u04be\5\u00fe\u0080\2\u04b9\u04ba\7\4\2"+
		"\2\u04ba\u04bb\5\u00fe\u0080\2\u04bb\u04bc\7\5\2\2\u04bc\u04be\3\2\2\2"+
		"\u04bd\u04b8\3\2\2\2\u04bd\u04b9\3\2\2\2\u04be\u00fd\3\2\2\2\u04bf\u04c0"+
		"\b\u0080\1\2\u04c0\u04c1\5\u0100\u0081\2\u04c1\u04c7\3\2\2\2\u04c2\u04c3"+
		"\f\3\2\2\u04c3\u04c4\7&\2\2\u04c4\u04c6\5\u0100\u0081\2\u04c5\u04c2\3"+
		"\2\2\2\u04c6\u04c9\3\2\2\2\u04c7\u04c5\3\2\2\2\u04c7\u04c8\3\2\2\2\u04c8"+
		"\u00ff\3\2\2\2\u04c9\u04c7\3\2\2\2\u04ca\u04cf\5\u00d8m\2\u04cb\u04cc"+
		"\7.\2\2\u04cc\u04cf\5\u00d8m\2\u04cd\u04cf\5\u00d6l\2\u04ce\u04ca\3\2"+
		"\2\2\u04ce\u04cb\3\2\2\2\u04ce\u04cd\3\2\2\2\u04cf\u0101\3\2\2\2\u04d0"+
		"\u04d4\5\u0112\u008a\2\u04d1\u04d4\5\u0104\u0083\2\u04d2\u04d4\5\u0106"+
		"\u0084\2\u04d3\u04d0\3\2\2\2\u04d3\u04d1\3\2\2\2\u04d3\u04d2\3\2\2\2\u04d4"+
		"\u0103\3\2\2\2\u04d5\u04d6\t\4\2\2\u04d6\u0105\3\2\2\2\u04d7\u04d8\t\5"+
		"\2\2\u04d8\u0107\3\2\2\2\u04d9\u04de\7\62\2\2\u04da\u04de\7\61\2\2\u04db"+
		"\u04de\5\u0114\u008b\2\u04dc\u04de\7>\2\2\u04dd\u04d9\3\2\2\2\u04dd\u04da"+
		"\3\2\2\2\u04dd\u04db\3\2\2\2\u04dd\u04dc\3\2\2\2\u04de\u0109\3\2\2\2\u04df"+
		"\u04e2\5\u0118\u008d\2\u04e0\u04e2\5\u010c\u0087\2\u04e1\u04df\3\2\2\2"+
		"\u04e1\u04e0\3\2\2\2\u04e2\u010b\3\2\2\2\u04e3\u04e4\t\6\2\2\u04e4\u010d"+
		"\3\2\2\2\u04e5\u04e6\5\f\7\2\u04e6\u010f\3\2\2\2\u04e7\u04e8\5\16\b\2"+
		"\u04e8\u0111\3\2\2\2\u04e9\u04ea\t\7\2\2\u04ea\u0113\3\2\2\2\u04eb\u04ec"+
		"\t\b\2\2\u04ec\u0115\3\2\2\2\u04ed\u04ee\t\t\2\2\u04ee\u0117\3\2\2\2\u04ef"+
		"\u04f0\7.\2\2\u04f0\u0119\3\2\2\2\u04f1\u04f2\7\62\2\2\u04f2\u011b\3\2"+
		"\2\2\u04f3\u04f4\5\u011e\u0090\2\u04f4\u011d\3\2\2\2\u04f5\u04f6\5\f\7"+
		"\2\u04f6\u011f\3\2\2\2\u04f7\u04f8\5\u0122\u0092\2\u04f8\u0121\3\2\2\2"+
		"\u04f9\u04fa\5\20\t\2\u04fa\u0123\3\2\2\2\u04fb\u04fc\5\u0126\u0094\2"+
		"\u04fc\u0125\3\2\2\2\u04fd\u04fe\5\16\b\2\u04fe\u0127\3\2\2\2\u04ff\u0502"+
		"\5\22\n\2\u0500\u0502\7U\2\2\u0501\u04ff\3\2\2\2\u0501\u0500\3\2\2\2\u0502"+
		"\u0129\3\2\2\2\u0503\u0504\7R\2\2\u0504\u012b\3\2\2\2i\u012f\u0136\u013c"+
		"\u0148\u014f\u015f\u0169\u0176\u0183\u0190\u0198\u01a7\u01af\u01b8\u01d1"+
		"\u01da\u01df\u01e3\u01e9\u01ee\u01f7\u0203\u0210\u021d\u022a\u0236\u023f"+
		"\u0248\u024d\u0254\u025b\u0262\u0267\u0282\u028b\u0294\u029e\u02aa\u02b7"+
		"\u02c2\u02ce\u02d5\u02db\u02df\u02e3\u02eb\u02f7\u0304\u0310\u031e\u0324"+
		"\u032a\u0342\u0349\u0350\u035b\u0365\u036c\u0373\u037e\u0388\u0392\u0399"+
		"\u03a0\u03ab\u03af\u03b8\u03c1\u03c8\u03cf\u03d7\u03da\u03e1\u03f2\u03f7"+
		"\u03fb\u03ff\u0407\u0413\u0420\u042a\u0438\u043f\u0448\u044e\u045d\u0464"+
		"\u046b\u0472\u047b\u0480\u0496\u049d\u04a7\u04ae\u04b5\u04bd\u04c7\u04ce"+
		"\u04d3\u04dd\u04e1\u0501";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}