// Generated from tptp.g4 by ANTLR 4.5.3

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
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, Real=56, Signed_real=57, Unsigned_real=58, 
		Rational=59, Signed_rational=60, Unsigned_rational=61, Integer=62, Signed_integer=63, 
		Unsigned_integer=64, Decimal=65, Positive_decimal=66, Decimal_exponent=67, 
		Decimal_fraction=68, Dot_decimal=69, Exp_integer=70, Signed_exp_integer=71, 
		Unsigned_exp_integer=72, Dollar_word=73, Dollar_dollar_word=74, Upper_word=75, 
		Lower_word=76, Single_quoted=77, Distinct_object=78, Whitespace=79, Line_comment=80, 
		Block_comment=81;
	public static final int
		RULE_tptp_file = 0, RULE_tptp_input = 1, RULE_include = 2, RULE_formula_selection = 3, 
		RULE_name_list = 4, RULE_name = 5, RULE_atomic_word = 6, RULE_atomic_defined_word = 7, 
		RULE_atomic_system_word = 8, RULE_number = 9, RULE_file_name = 10, RULE_annotated_formula = 11, 
		RULE_thf_annotated = 12, RULE_tff_annotated = 13, RULE_fof_annotated = 14, 
		RULE_annotations = 15, RULE_formula_role = 16, RULE_source = 17, RULE_optional_info = 18, 
		RULE_general_term = 19, RULE_general_data = 20, RULE_general_function = 21, 
		RULE_formula_data = 22, RULE_general_list = 23, RULE_general_terms = 24, 
		RULE_thf_formula = 25, RULE_thf_logic_formula = 26, RULE_thf_binary_formula = 27, 
		RULE_thf_binary_pair = 28, RULE_thf_binary_tuple = 29, RULE_thf_or_formula = 30, 
		RULE_thf_and_formula = 31, RULE_thf_apply_formula = 32, RULE_thf_unitary_formula = 33, 
		RULE_thf_quantified_formula = 34, RULE_thf_quantification = 35, RULE_thf_variable_list = 36, 
		RULE_thf_variable = 37, RULE_thf_typed_variable = 38, RULE_thf_unary_formula = 39, 
		RULE_thf_atom = 40, RULE_thf_function = 41, RULE_thf_plain_term = 42, 
		RULE_thf_defined_term = 43, RULE_thf_system_term = 44, RULE_thf_conditional = 45, 
		RULE_thf_let = 46, RULE_thf_arguments = 47, RULE_thf_type_formula = 48, 
		RULE_thf_typeable_formula = 49, RULE_thf_subtype = 50, RULE_thf_top_level_type = 51, 
		RULE_thf_unitary_type = 52, RULE_thf_binary_type = 53, RULE_thf_mapping_type = 54, 
		RULE_thf_xprod_type = 55, RULE_thf_union_type = 56, RULE_thf_sequent = 57, 
		RULE_thf_tuple = 58, RULE_thf_formula_list = 59, RULE_thf_conn_term = 60, 
		RULE_thf_quantifier = 61, RULE_th0_quantifier = 62, RULE_th1_quantifier = 63, 
		RULE_thf_pair_connective = 64, RULE_thf_unary_connective = 65, RULE_th1_unary_connective = 66, 
		RULE_defined_type = 67, RULE_tff_formula = 68, RULE_tff_logic_formula = 69, 
		RULE_tff_binary_formula = 70, RULE_tff_binary_nonassoc = 71, RULE_tff_binary_assoc = 72, 
		RULE_tff_or_formula = 73, RULE_tff_and_formula = 74, RULE_tff_unitary_formula = 75, 
		RULE_tff_quantified_formula = 76, RULE_tff_variable_list = 77, RULE_tff_variable = 78, 
		RULE_tff_typed_variable = 79, RULE_tff_unary_formula = 80, RULE_tff_conditional = 81, 
		RULE_tff_let = 82, RULE_tff_let_term_defns = 83, RULE_tff_let_term_list = 84, 
		RULE_tff_let_term_defn = 85, RULE_tff_let_term_binding = 86, RULE_tff_let_formula_defns = 87, 
		RULE_tff_let_formula_list = 88, RULE_tff_let_formula_defn = 89, RULE_tff_let_formula_binding = 90, 
		RULE_tff_sequent = 91, RULE_tff_formula_tuple = 92, RULE_tff_formula_tuple_list = 93, 
		RULE_tff_typed_atom = 94, RULE_tff_untyped_atom = 95, RULE_tff_top_level_type = 96, 
		RULE_tf1_quantified_type = 97, RULE_tff_monotype = 98, RULE_tff_unitary_type = 99, 
		RULE_tff_atomic_type = 100, RULE_tff_type_arguments = 101, RULE_tff_mapping_type = 102, 
		RULE_tff_xprod_type = 103, RULE_fof_formula = 104, RULE_fof_logic_formula = 105, 
		RULE_fof_binary_formula = 106, RULE_fof_binary_nonassoc = 107, RULE_fof_binary_assoc = 108, 
		RULE_fof_or_formula = 109, RULE_fof_and_formula = 110, RULE_fof_unitary_formula = 111, 
		RULE_fof_quantified_formula = 112, RULE_fof_variable_list = 113, RULE_fof_unary_formula = 114, 
		RULE_fof_sequent = 115, RULE_fof_formula_tuple = 116, RULE_fof_formula_tuple_list = 117, 
		RULE_fol_infix_unary = 118, RULE_atomic_formula = 119, RULE_plain_atomic_formula = 120, 
		RULE_defined_atomic_formula = 121, RULE_defined_plain_formula = 122, RULE_system_atomic_formula = 123, 
		RULE_fol_quantifier = 124, RULE_binary_connective = 125, RULE_assoc_connective = 126, 
		RULE_unary_connective = 127, RULE_defined_infix_formula = 128, RULE_defined_infix_pred = 129, 
		RULE_infix_equality = 130, RULE_infix_inequality = 131, RULE_term = 132, 
		RULE_function_term = 133, RULE_plain_term = 134, RULE_constant = 135, 
		RULE_functor = 136, RULE_defined_term = 137, RULE_defined_atom = 138, 
		RULE_defined_atomic_term = 139, RULE_defined_plain_term = 140, RULE_defined_constant = 141, 
		RULE_defined_functor = 142, RULE_system_term = 143, RULE_system_constant = 144, 
		RULE_system_functor = 145, RULE_conditional_term = 146, RULE_let_term = 147, 
		RULE_arguments = 148, RULE_variable = 149;
	public static final String[] ruleNames = {
		"tptp_file", "tptp_input", "include", "formula_selection", "name_list", 
		"name", "atomic_word", "atomic_defined_word", "atomic_system_word", "number", 
		"file_name", "annotated_formula", "thf_annotated", "tff_annotated", "fof_annotated", 
		"annotations", "formula_role", "source", "optional_info", "general_term", 
		"general_data", "general_function", "formula_data", "general_list", "general_terms", 
		"thf_formula", "thf_logic_formula", "thf_binary_formula", "thf_binary_pair", 
		"thf_binary_tuple", "thf_or_formula", "thf_and_formula", "thf_apply_formula", 
		"thf_unitary_formula", "thf_quantified_formula", "thf_quantification", 
		"thf_variable_list", "thf_variable", "thf_typed_variable", "thf_unary_formula", 
		"thf_atom", "thf_function", "thf_plain_term", "thf_defined_term", "thf_system_term", 
		"thf_conditional", "thf_let", "thf_arguments", "thf_type_formula", "thf_typeable_formula", 
		"thf_subtype", "thf_top_level_type", "thf_unitary_type", "thf_binary_type", 
		"thf_mapping_type", "thf_xprod_type", "thf_union_type", "thf_sequent", 
		"thf_tuple", "thf_formula_list", "thf_conn_term", "thf_quantifier", "th0_quantifier", 
		"th1_quantifier", "thf_pair_connective", "thf_unary_connective", "th1_unary_connective", 
		"defined_type", "tff_formula", "tff_logic_formula", "tff_binary_formula", 
		"tff_binary_nonassoc", "tff_binary_assoc", "tff_or_formula", "tff_and_formula", 
		"tff_unitary_formula", "tff_quantified_formula", "tff_variable_list", 
		"tff_variable", "tff_typed_variable", "tff_unary_formula", "tff_conditional", 
		"tff_let", "tff_let_term_defns", "tff_let_term_list", "tff_let_term_defn", 
		"tff_let_term_binding", "tff_let_formula_defns", "tff_let_formula_list", 
		"tff_let_formula_defn", "tff_let_formula_binding", "tff_sequent", "tff_formula_tuple", 
		"tff_formula_tuple_list", "tff_typed_atom", "tff_untyped_atom", "tff_top_level_type", 
		"tf1_quantified_type", "tff_monotype", "tff_unitary_type", "tff_atomic_type", 
		"tff_type_arguments", "tff_mapping_type", "tff_xprod_type", "fof_formula", 
		"fof_logic_formula", "fof_binary_formula", "fof_binary_nonassoc", "fof_binary_assoc", 
		"fof_or_formula", "fof_and_formula", "fof_unitary_formula", "fof_quantified_formula", 
		"fof_variable_list", "fof_unary_formula", "fof_sequent", "fof_formula_tuple", 
		"fof_formula_tuple_list", "fol_infix_unary", "atomic_formula", "plain_atomic_formula", 
		"defined_atomic_formula", "defined_plain_formula", "system_atomic_formula", 
		"fol_quantifier", "binary_connective", "assoc_connective", "unary_connective", 
		"defined_infix_formula", "defined_infix_pred", "infix_equality", "infix_inequality", 
		"term", "function_term", "plain_term", "constant", "functor", "defined_term", 
		"defined_atom", "defined_atomic_term", "defined_plain_term", "defined_constant", 
		"defined_functor", "system_term", "system_constant", "system_functor", 
		"conditional_term", "let_term", "arguments", "variable"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'include('", "').'", "','", "'['", "']'", "'thf('", "'tff('", "'fof('", 
		"':'", "'('", "')'", "'$thf('", "'$tff('", "'$fof('", "'$fot('", "'[]'", 
		"'|'", "'&'", "'@'", "'$ite('", "'$let('", "'<<'", "'>'", "'*'", "'+'", 
		"'-->'", "'^'", "'@+'", "'@-'", "'!>'", "'?*'", "':='", "'!!'", "'??'", 
		"'@@+'", "'@@-'", "'@='", "'$ite_f('", "'$let_tf('", "'$let_ff('", "'!'", 
		"'='", "'<=>'", "'>>'", "'?'", "'=>'", "'<='", "'<~>'", "'~|'", "'~&'", 
		"'~'", "'!='", "'$ite_t('", "'$let_ft('", "'$let_tt('"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, "Real", "Signed_real", 
		"Unsigned_real", "Rational", "Signed_rational", "Unsigned_rational", "Integer", 
		"Signed_integer", "Unsigned_integer", "Decimal", "Positive_decimal", "Decimal_exponent", 
		"Decimal_fraction", "Dot_decimal", "Exp_integer", "Signed_exp_integer", 
		"Unsigned_exp_integer", "Dollar_word", "Dollar_dollar_word", "Upper_word", 
		"Lower_word", "Single_quoted", "Distinct_object", "Whitespace", "Line_comment", 
		"Block_comment"
	};
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
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__5) | (1L << T__6) | (1L << T__7))) != 0)) {
				{
				{
				setState(300);
				tptp_input();
				}
				}
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(306);
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
			setState(310);
			switch (_input.LA(1)) {
			case T__5:
			case T__6:
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(308);
				annotated_formula();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(309);
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
			setState(312);
			match(T__0);
			setState(313);
			file_name();
			setState(315);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(314);
				formula_selection();
				}
			}

			setState(317);
			match(T__1);
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
		public Name_listContext name_list() {
			return getRuleContext(Name_listContext.class,0);
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			match(T__2);
			setState(320);
			match(T__3);
			setState(321);
			name_list();
			setState(322);
			match(T__4);
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

	public static class Name_listContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Name_listContext name_list() {
			return getRuleContext(Name_listContext.class,0);
		}
		public Name_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterName_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitName_list(this);
		}
	}

	public final Name_listContext name_list() throws RecognitionException {
		Name_listContext _localctx = new Name_listContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_name_list);
		try {
			setState(329);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(324);
				name();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(325);
				name();
				setState(326);
				match(T__2);
				setState(327);
				name_list();
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
		enterRule(_localctx, 10, RULE_name);
		try {
			setState(333);
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
		enterRule(_localctx, 12, RULE_atomic_word);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			_la = _input.LA(1);
			if ( !(_la==Lower_word || _la==Single_quoted) ) {
			_errHandler.recoverInline(this);
			} else {
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
		enterRule(_localctx, 14, RULE_atomic_defined_word);
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
		enterRule(_localctx, 16, RULE_atomic_system_word);
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
		enterRule(_localctx, 18, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(341);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Real) | (1L << Rational) | (1L << Integer))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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
		enterRule(_localctx, 20, RULE_file_name);
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
		enterRule(_localctx, 22, RULE_annotated_formula);
		try {
			setState(348);
			switch (_input.LA(1)) {
			case T__5:
				enterOuterAlt(_localctx, 1);
				{
				setState(345);
				thf_annotated();
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(346);
				tff_annotated();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 3);
				{
				setState(347);
				fof_annotated();
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
		enterRule(_localctx, 24, RULE_thf_annotated);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(350);
			match(T__5);
			setState(351);
			name();
			setState(352);
			match(T__2);
			setState(353);
			formula_role();
			setState(354);
			match(T__2);
			setState(355);
			thf_formula();
			setState(357);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(356);
				annotations();
				}
			}

			setState(359);
			match(T__1);
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
		enterRule(_localctx, 26, RULE_tff_annotated);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(361);
			match(T__6);
			setState(362);
			name();
			setState(363);
			match(T__2);
			setState(364);
			formula_role();
			setState(365);
			match(T__2);
			setState(366);
			tff_formula();
			setState(368);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(367);
				annotations();
				}
			}

			setState(370);
			match(T__1);
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
		enterRule(_localctx, 28, RULE_fof_annotated);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			match(T__7);
			setState(373);
			name();
			setState(374);
			match(T__2);
			setState(375);
			formula_role();
			setState(376);
			match(T__2);
			setState(377);
			fof_formula();
			setState(379);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(378);
				annotations();
				}
			}

			setState(381);
			match(T__1);
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
			setState(383);
			match(T__2);
			setState(384);
			source();
			setState(386);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(385);
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
			setState(388);
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
			setState(390);
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
			setState(392);
			match(T__2);
			setState(393);
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
			setState(401);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(395);
				general_data();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(396);
				general_data();
				setState(397);
				match(T__8);
				setState(398);
				general_term();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(400);
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
			setState(409);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(403);
				atomic_word();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(404);
				general_function();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(405);
				variable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(406);
				number();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(407);
				match(Distinct_object);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(408);
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
		public General_termsContext general_terms() {
			return getRuleContext(General_termsContext.class,0);
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(411);
			atomic_word();
			setState(412);
			match(T__9);
			setState(413);
			general_terms();
			setState(414);
			match(T__10);
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
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
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
			setState(432);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(416);
				match(T__11);
				setState(417);
				thf_formula();
				setState(418);
				match(T__10);
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 2);
				{
				setState(420);
				match(T__12);
				setState(421);
				tff_formula();
				setState(422);
				match(T__10);
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 3);
				{
				setState(424);
				match(T__13);
				setState(425);
				fof_formula();
				setState(426);
				match(T__10);
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 4);
				{
				setState(428);
				match(T__14);
				setState(429);
				term();
				setState(430);
				match(T__10);
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
		public General_termsContext general_terms() {
			return getRuleContext(General_termsContext.class,0);
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
		try {
			setState(439);
			switch (_input.LA(1)) {
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(434);
				match(T__15);
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(435);
				match(T__3);
				setState(436);
				general_terms();
				setState(437);
				match(T__4);
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

	public static class General_termsContext extends ParserRuleContext {
		public General_termContext general_term() {
			return getRuleContext(General_termContext.class,0);
		}
		public General_termsContext general_terms() {
			return getRuleContext(General_termsContext.class,0);
		}
		public General_termsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_general_terms; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterGeneral_terms(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitGeneral_terms(this);
		}
	}

	public final General_termsContext general_terms() throws RecognitionException {
		General_termsContext _localctx = new General_termsContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_general_terms);
		try {
			setState(446);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(441);
				general_term();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(442);
				general_term();
				setState(443);
				match(T__2);
				setState(444);
				general_terms();
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
		enterRule(_localctx, 50, RULE_thf_formula);
		try {
			setState(450);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(448);
				thf_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(449);
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
		enterRule(_localctx, 52, RULE_thf_logic_formula);
		try {
			setState(456);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(452);
				thf_binary_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(453);
				thf_unitary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(454);
				thf_type_formula();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(455);
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
		enterRule(_localctx, 54, RULE_thf_binary_formula);
		try {
			setState(460);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(458);
				thf_binary_pair();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(459);
				thf_binary_tuple();
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
		enterRule(_localctx, 56, RULE_thf_binary_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(462);
			thf_unitary_formula();
			setState(463);
			thf_pair_connective();
			setState(464);
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
		enterRule(_localctx, 58, RULE_thf_binary_tuple);
		try {
			setState(469);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(466);
				thf_or_formula(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(467);
				thf_and_formula(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(468);
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
		int _startState = 60;
		enterRecursionRule(_localctx, 60, RULE_thf_or_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(472);
			thf_unitary_formula();
			setState(473);
			match(T__16);
			setState(474);
			thf_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(481);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_or_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_or_formula);
					setState(476);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(477);
					match(T__16);
					setState(478);
					thf_unitary_formula();
					}
					} 
				}
				setState(483);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
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
		int _startState = 62;
		enterRecursionRule(_localctx, 62, RULE_thf_and_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(485);
			thf_unitary_formula();
			setState(486);
			match(T__17);
			setState(487);
			thf_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(494);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_and_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_and_formula);
					setState(489);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(490);
					match(T__17);
					setState(491);
					thf_unitary_formula();
					}
					} 
				}
				setState(496);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
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
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_thf_apply_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(498);
			thf_unitary_formula();
			setState(499);
			match(T__18);
			setState(500);
			thf_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(507);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_apply_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_apply_formula);
					setState(502);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(503);
					match(T__18);
					setState(504);
					thf_unitary_formula();
					}
					} 
				}
				setState(509);
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
		enterRule(_localctx, 66, RULE_thf_unitary_formula);
		try {
			setState(520);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(510);
				thf_quantified_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(511);
				thf_unary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(512);
				thf_atom();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(513);
				thf_conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(514);
				thf_let();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(515);
				thf_tuple();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(516);
				match(T__9);
				setState(517);
				thf_logic_formula();
				setState(518);
				match(T__10);
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
		enterRule(_localctx, 68, RULE_thf_quantified_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(522);
			thf_quantification();
			setState(523);
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
		public Thf_variable_listContext thf_variable_list() {
			return getRuleContext(Thf_variable_listContext.class,0);
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
		enterRule(_localctx, 70, RULE_thf_quantification);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(525);
			thf_quantifier();
			setState(526);
			match(T__3);
			setState(527);
			thf_variable_list();
			setState(528);
			match(T__4);
			setState(529);
			match(T__8);
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

	public static class Thf_variable_listContext extends ParserRuleContext {
		public Thf_variableContext thf_variable() {
			return getRuleContext(Thf_variableContext.class,0);
		}
		public Thf_variable_listContext thf_variable_list() {
			return getRuleContext(Thf_variable_listContext.class,0);
		}
		public Thf_variable_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_variable_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_variable_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_variable_list(this);
		}
	}

	public final Thf_variable_listContext thf_variable_list() throws RecognitionException {
		Thf_variable_listContext _localctx = new Thf_variable_listContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_thf_variable_list);
		try {
			setState(536);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(531);
				thf_variable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(532);
				thf_variable();
				setState(533);
				match(T__2);
				setState(534);
				thf_variable_list();
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

	public static class Thf_variableContext extends ParserRuleContext {
		public Thf_typed_variableContext thf_typed_variable() {
			return getRuleContext(Thf_typed_variableContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
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
		enterRule(_localctx, 74, RULE_thf_variable);
		try {
			setState(540);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(538);
				thf_typed_variable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(539);
				variable();
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

	public static class Thf_typed_variableContext extends ParserRuleContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Thf_top_level_typeContext thf_top_level_type() {
			return getRuleContext(Thf_top_level_typeContext.class,0);
		}
		public Thf_typed_variableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_thf_typed_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterThf_typed_variable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitThf_typed_variable(this);
		}
	}

	public final Thf_typed_variableContext thf_typed_variable() throws RecognitionException {
		Thf_typed_variableContext _localctx = new Thf_typed_variableContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_thf_typed_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			variable();
			setState(543);
			match(T__8);
			setState(544);
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

	public static class Thf_unary_formulaContext extends ParserRuleContext {
		public Thf_unary_connectiveContext thf_unary_connective() {
			return getRuleContext(Thf_unary_connectiveContext.class,0);
		}
		public Thf_logic_formulaContext thf_logic_formula() {
			return getRuleContext(Thf_logic_formulaContext.class,0);
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
		enterRule(_localctx, 78, RULE_thf_unary_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
			thf_unary_connective();
			setState(547);
			match(T__9);
			setState(548);
			thf_logic_formula();
			setState(549);
			match(T__10);
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
		enterRule(_localctx, 80, RULE_thf_atom);
		try {
			setState(554);
			switch (_input.LA(1)) {
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Dollar_dollar_word:
			case Lower_word:
			case Single_quoted:
			case Distinct_object:
				enterOuterAlt(_localctx, 1);
				{
				setState(551);
				thf_function();
				}
				break;
			case Upper_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(552);
				variable();
				}
				break;
			case T__16:
			case T__17:
			case T__31:
			case T__32:
			case T__33:
			case T__34:
			case T__35:
			case T__36:
			case T__41:
			case T__42:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
				enterOuterAlt(_localctx, 3);
				{
				setState(553);
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
		enterRule(_localctx, 82, RULE_thf_function);
		try {
			setState(559);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(556);
				thf_plain_term();
				}
				break;
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Distinct_object:
				enterOuterAlt(_localctx, 2);
				{
				setState(557);
				thf_defined_term();
				}
				break;
			case Dollar_dollar_word:
				enterOuterAlt(_localctx, 3);
				{
				setState(558);
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
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
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
		enterRule(_localctx, 84, RULE_thf_plain_term);
		try {
			setState(567);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(561);
				constant();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(562);
				functor();
				setState(563);
				match(T__9);
				setState(564);
				thf_arguments();
				setState(565);
				match(T__10);
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

	public static class Thf_defined_termContext extends ParserRuleContext {
		public Defined_atomContext defined_atom() {
			return getRuleContext(Defined_atomContext.class,0);
		}
		public Defined_constantContext defined_constant() {
			return getRuleContext(Defined_constantContext.class,0);
		}
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
		enterRule(_localctx, 86, RULE_thf_defined_term);
		try {
			setState(576);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(569);
				defined_atom();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(570);
				defined_constant();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(571);
				defined_functor();
				setState(572);
				match(T__9);
				setState(573);
				thf_arguments();
				setState(574);
				match(T__10);
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

	public static class Thf_system_termContext extends ParserRuleContext {
		public System_constantContext system_constant() {
			return getRuleContext(System_constantContext.class,0);
		}
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
		enterRule(_localctx, 88, RULE_thf_system_term);
		try {
			setState(584);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(578);
				system_constant();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(579);
				system_functor();
				setState(580);
				match(T__9);
				setState(581);
				thf_arguments();
				setState(582);
				match(T__10);
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
		enterRule(_localctx, 90, RULE_thf_conditional);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			match(T__19);
			setState(587);
			thf_logic_formula();
			setState(588);
			match(T__2);
			setState(589);
			thf_logic_formula();
			setState(590);
			match(T__2);
			setState(591);
			thf_logic_formula();
			setState(592);
			match(T__10);
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
		enterRule(_localctx, 92, RULE_thf_let);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(594);
			match(T__20);
			setState(595);
			thf_unitary_formula();
			setState(596);
			match(T__2);
			setState(597);
			thf_formula();
			setState(598);
			match(T__10);
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
		enterRule(_localctx, 94, RULE_thf_arguments);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(600);
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
		enterRule(_localctx, 96, RULE_thf_type_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(602);
			thf_typeable_formula();
			setState(603);
			match(T__8);
			setState(604);
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
		enterRule(_localctx, 98, RULE_thf_typeable_formula);
		try {
			setState(611);
			switch (_input.LA(1)) {
			case T__16:
			case T__17:
			case T__31:
			case T__32:
			case T__33:
			case T__34:
			case T__35:
			case T__36:
			case T__41:
			case T__42:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
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
				setState(606);
				thf_atom();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(607);
				match(T__9);
				setState(608);
				thf_logic_formula();
				setState(609);
				match(T__10);
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
		public List<ConstantContext> constant() {
			return getRuleContexts(ConstantContext.class);
		}
		public ConstantContext constant(int i) {
			return getRuleContext(ConstantContext.class,i);
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
		enterRule(_localctx, 100, RULE_thf_subtype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(613);
			constant();
			setState(614);
			match(T__21);
			setState(615);
			constant();
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
		enterRule(_localctx, 102, RULE_thf_top_level_type);
		try {
			setState(619);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(617);
				thf_unitary_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(618);
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

	public static class Thf_unitary_typeContext extends ParserRuleContext {
		public Thf_unitary_formulaContext thf_unitary_formula() {
			return getRuleContext(Thf_unitary_formulaContext.class,0);
		}
		public Thf_binary_typeContext thf_binary_type() {
			return getRuleContext(Thf_binary_typeContext.class,0);
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
		enterRule(_localctx, 104, RULE_thf_unitary_type);
		try {
			setState(626);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(621);
				thf_unitary_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(622);
				match(T__9);
				setState(623);
				thf_binary_type();
				setState(624);
				match(T__10);
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
		enterRule(_localctx, 106, RULE_thf_binary_type);
		try {
			setState(631);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(628);
				thf_mapping_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(629);
				thf_xprod_type(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(630);
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
		enterRule(_localctx, 108, RULE_thf_mapping_type);
		try {
			setState(641);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(633);
				thf_unitary_type();
				setState(634);
				match(T__22);
				setState(635);
				thf_unitary_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(637);
				thf_unitary_type();
				setState(638);
				match(T__22);
				setState(639);
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
		int _startState = 110;
		enterRecursionRule(_localctx, 110, RULE_thf_xprod_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(644);
			thf_unitary_type();
			setState(645);
			match(T__23);
			setState(646);
			thf_unitary_type();
			}
			_ctx.stop = _input.LT(-1);
			setState(653);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_xprod_typeContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_xprod_type);
					setState(648);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(649);
					match(T__23);
					setState(650);
					thf_unitary_type();
					}
					} 
				}
				setState(655);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
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
		int _startState = 112;
		enterRecursionRule(_localctx, 112, RULE_thf_union_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(657);
			thf_unitary_type();
			setState(658);
			match(T__24);
			setState(659);
			thf_unitary_type();
			}
			_ctx.stop = _input.LT(-1);
			setState(666);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Thf_union_typeContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_thf_union_type);
					setState(661);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(662);
					match(T__24);
					setState(663);
					thf_unitary_type();
					}
					} 
				}
				setState(668);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
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
		enterRule(_localctx, 114, RULE_thf_sequent);
		try {
			setState(677);
			switch (_input.LA(1)) {
			case T__3:
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(669);
				thf_tuple();
				setState(670);
				match(T__25);
				setState(671);
				thf_tuple();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(673);
				match(T__9);
				setState(674);
				thf_sequent();
				setState(675);
				match(T__10);
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
		enterRule(_localctx, 116, RULE_thf_tuple);
		try {
			setState(684);
			switch (_input.LA(1)) {
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(679);
				match(T__15);
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(680);
				match(T__3);
				setState(681);
				thf_formula_list();
				setState(682);
				match(T__4);
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
		public Thf_logic_formulaContext thf_logic_formula() {
			return getRuleContext(Thf_logic_formulaContext.class,0);
		}
		public Thf_formula_listContext thf_formula_list() {
			return getRuleContext(Thf_formula_listContext.class,0);
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
		enterRule(_localctx, 118, RULE_thf_formula_list);
		try {
			setState(691);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(686);
				thf_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(687);
				thf_logic_formula();
				setState(688);
				match(T__2);
				setState(689);
				thf_formula_list();
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
		enterRule(_localctx, 120, RULE_thf_conn_term);
		try {
			setState(696);
			switch (_input.LA(1)) {
			case T__31:
			case T__41:
			case T__42:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__51:
				enterOuterAlt(_localctx, 1);
				{
				setState(693);
				thf_pair_connective();
				}
				break;
			case T__16:
			case T__17:
				enterOuterAlt(_localctx, 2);
				{
				setState(694);
				assoc_connective();
				}
				break;
			case T__32:
			case T__33:
			case T__34:
			case T__35:
			case T__36:
			case T__50:
				enterOuterAlt(_localctx, 3);
				{
				setState(695);
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

	public static class Thf_quantifierContext extends ParserRuleContext {
		public Fol_quantifierContext fol_quantifier() {
			return getRuleContext(Fol_quantifierContext.class,0);
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
		enterRule(_localctx, 122, RULE_thf_quantifier);
		try {
			setState(701);
			switch (_input.LA(1)) {
			case T__40:
			case T__44:
				enterOuterAlt(_localctx, 1);
				{
				setState(698);
				fol_quantifier();
				}
				break;
			case T__26:
			case T__27:
			case T__28:
				enterOuterAlt(_localctx, 2);
				{
				setState(699);
				th0_quantifier();
				}
				break;
			case T__29:
			case T__30:
				enterOuterAlt(_localctx, 3);
				{
				setState(700);
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
		enterRule(_localctx, 124, RULE_th0_quantifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(703);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__26) | (1L << T__27) | (1L << T__28))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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
		enterRule(_localctx, 126, RULE_th1_quantifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(705);
			_la = _input.LA(1);
			if ( !(_la==T__29 || _la==T__30) ) {
			_errHandler.recoverInline(this);
			} else {
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
		public Infix_equalityContext infix_equality() {
			return getRuleContext(Infix_equalityContext.class,0);
		}
		public Infix_inequalityContext infix_inequality() {
			return getRuleContext(Infix_inequalityContext.class,0);
		}
		public Binary_connectiveContext binary_connective() {
			return getRuleContext(Binary_connectiveContext.class,0);
		}
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
		enterRule(_localctx, 128, RULE_thf_pair_connective);
		try {
			setState(711);
			switch (_input.LA(1)) {
			case T__41:
				enterOuterAlt(_localctx, 1);
				{
				setState(707);
				infix_equality();
				}
				break;
			case T__51:
				enterOuterAlt(_localctx, 2);
				{
				setState(708);
				infix_inequality();
				}
				break;
			case T__42:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
				enterOuterAlt(_localctx, 3);
				{
				setState(709);
				binary_connective();
				}
				break;
			case T__31:
				enterOuterAlt(_localctx, 4);
				{
				setState(710);
				match(T__31);
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
		enterRule(_localctx, 130, RULE_thf_unary_connective);
		try {
			setState(715);
			switch (_input.LA(1)) {
			case T__50:
				enterOuterAlt(_localctx, 1);
				{
				setState(713);
				unary_connective();
				}
				break;
			case T__32:
			case T__33:
			case T__34:
			case T__35:
			case T__36:
				enterOuterAlt(_localctx, 2);
				{
				setState(714);
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
		enterRule(_localctx, 132, RULE_th1_unary_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(717);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__32) | (1L << T__33) | (1L << T__34) | (1L << T__35) | (1L << T__36))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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
		enterRule(_localctx, 134, RULE_defined_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(719);
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
		enterRule(_localctx, 136, RULE_tff_formula);
		try {
			setState(724);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(721);
				tff_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(722);
				tff_typed_atom();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(723);
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
		enterRule(_localctx, 138, RULE_tff_logic_formula);
		try {
			setState(728);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(726);
				tff_binary_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(727);
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
		enterRule(_localctx, 140, RULE_tff_binary_formula);
		try {
			setState(732);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(730);
				tff_binary_nonassoc();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(731);
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
		enterRule(_localctx, 142, RULE_tff_binary_nonassoc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(734);
			tff_unitary_formula();
			setState(735);
			binary_connective();
			setState(736);
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
		enterRule(_localctx, 144, RULE_tff_binary_assoc);
		try {
			setState(740);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(738);
				tff_or_formula(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(739);
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
		int _startState = 146;
		enterRecursionRule(_localctx, 146, RULE_tff_or_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(743);
			tff_unitary_formula();
			setState(744);
			match(T__16);
			setState(745);
			tff_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(752);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Tff_or_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_tff_or_formula);
					setState(747);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(748);
					match(T__16);
					setState(749);
					tff_unitary_formula();
					}
					} 
				}
				setState(754);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
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
		int _startState = 148;
		enterRecursionRule(_localctx, 148, RULE_tff_and_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(756);
			tff_unitary_formula();
			setState(757);
			match(T__17);
			setState(758);
			tff_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(765);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Tff_and_formulaContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_tff_and_formula);
					setState(760);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(761);
					match(T__17);
					setState(762);
					tff_unitary_formula();
					}
					} 
				}
				setState(767);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
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
		public Atomic_formulaContext atomic_formula() {
			return getRuleContext(Atomic_formulaContext.class,0);
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
		enterRule(_localctx, 150, RULE_tff_unitary_formula);
		try {
			setState(777);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(768);
				tff_quantified_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(769);
				tff_unary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(770);
				atomic_formula();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(771);
				tff_conditional();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(772);
				tff_let();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(773);
				match(T__9);
				setState(774);
				tff_logic_formula();
				setState(775);
				match(T__10);
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
		public Fol_quantifierContext fol_quantifier() {
			return getRuleContext(Fol_quantifierContext.class,0);
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
		enterRule(_localctx, 152, RULE_tff_quantified_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(779);
			fol_quantifier();
			setState(780);
			match(T__3);
			setState(781);
			tff_variable_list();
			setState(782);
			match(T__4);
			setState(783);
			match(T__8);
			setState(784);
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
		public Tff_variableContext tff_variable() {
			return getRuleContext(Tff_variableContext.class,0);
		}
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
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
		enterRule(_localctx, 154, RULE_tff_variable_list);
		try {
			setState(791);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(786);
				tff_variable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(787);
				tff_variable();
				setState(788);
				match(T__2);
				setState(789);
				tff_variable_list();
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

	public static class Tff_variableContext extends ParserRuleContext {
		public Tff_typed_variableContext tff_typed_variable() {
			return getRuleContext(Tff_typed_variableContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
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
		enterRule(_localctx, 156, RULE_tff_variable);
		try {
			setState(795);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(793);
				tff_typed_variable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(794);
				variable();
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

	public static class Tff_typed_variableContext extends ParserRuleContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_typed_variableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tff_typed_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTff_typed_variable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTff_typed_variable(this);
		}
	}

	public final Tff_typed_variableContext tff_typed_variable() throws RecognitionException {
		Tff_typed_variableContext _localctx = new Tff_typed_variableContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_tff_typed_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(797);
			variable();
			setState(798);
			match(T__8);
			setState(799);
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

	public static class Tff_unary_formulaContext extends ParserRuleContext {
		public Unary_connectiveContext unary_connective() {
			return getRuleContext(Unary_connectiveContext.class,0);
		}
		public Tff_unitary_formulaContext tff_unitary_formula() {
			return getRuleContext(Tff_unitary_formulaContext.class,0);
		}
		public Fol_infix_unaryContext fol_infix_unary() {
			return getRuleContext(Fol_infix_unaryContext.class,0);
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
		enterRule(_localctx, 160, RULE_tff_unary_formula);
		try {
			setState(805);
			switch (_input.LA(1)) {
			case T__50:
				enterOuterAlt(_localctx, 1);
				{
				setState(801);
				unary_connective();
				setState(802);
				tff_unitary_formula();
				}
				break;
			case T__52:
			case T__53:
			case T__54:
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
				setState(804);
				fol_infix_unary();
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
		enterRule(_localctx, 162, RULE_tff_conditional);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(807);
			match(T__37);
			setState(808);
			tff_logic_formula();
			setState(809);
			match(T__2);
			setState(810);
			tff_logic_formula();
			setState(811);
			match(T__2);
			setState(812);
			tff_logic_formula();
			setState(813);
			match(T__10);
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
		enterRule(_localctx, 164, RULE_tff_let);
		try {
			setState(827);
			switch (_input.LA(1)) {
			case T__38:
				enterOuterAlt(_localctx, 1);
				{
				setState(815);
				match(T__38);
				setState(816);
				tff_let_term_defns();
				setState(817);
				match(T__2);
				setState(818);
				tff_formula();
				setState(819);
				match(T__10);
				}
				break;
			case T__39:
				enterOuterAlt(_localctx, 2);
				{
				setState(821);
				match(T__39);
				setState(822);
				tff_let_formula_defns();
				setState(823);
				match(T__2);
				setState(824);
				tff_formula();
				setState(825);
				match(T__10);
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
		enterRule(_localctx, 166, RULE_tff_let_term_defns);
		try {
			setState(834);
			switch (_input.LA(1)) {
			case T__9:
			case T__40:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(829);
				tff_let_term_defn();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(830);
				match(T__3);
				setState(831);
				tff_let_term_list();
				setState(832);
				match(T__4);
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
		public Tff_let_term_defnContext tff_let_term_defn() {
			return getRuleContext(Tff_let_term_defnContext.class,0);
		}
		public Tff_let_term_listContext tff_let_term_list() {
			return getRuleContext(Tff_let_term_listContext.class,0);
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
		enterRule(_localctx, 168, RULE_tff_let_term_list);
		try {
			setState(841);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(836);
				tff_let_term_defn();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(837);
				tff_let_term_defn();
				setState(838);
				match(T__2);
				setState(839);
				tff_let_term_list();
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

	public static class Tff_let_term_defnContext extends ParserRuleContext {
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
		}
		public Tff_let_term_defnContext tff_let_term_defn() {
			return getRuleContext(Tff_let_term_defnContext.class,0);
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
		enterRule(_localctx, 170, RULE_tff_let_term_defn);
		try {
			setState(851);
			switch (_input.LA(1)) {
			case T__40:
				enterOuterAlt(_localctx, 1);
				{
				setState(843);
				match(T__40);
				setState(844);
				match(T__3);
				setState(845);
				tff_variable_list();
				setState(846);
				match(T__4);
				setState(847);
				match(T__8);
				setState(848);
				tff_let_term_defn();
				}
				break;
			case T__9:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 2);
				{
				setState(850);
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
		public Plain_termContext plain_term() {
			return getRuleContext(Plain_termContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
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
		enterRule(_localctx, 172, RULE_tff_let_term_binding);
		try {
			setState(861);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(853);
				plain_term();
				setState(854);
				match(T__41);
				setState(855);
				term();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(857);
				match(T__9);
				setState(858);
				tff_let_term_binding();
				setState(859);
				match(T__10);
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
		enterRule(_localctx, 174, RULE_tff_let_formula_defns);
		try {
			setState(868);
			switch (_input.LA(1)) {
			case T__9:
			case T__40:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(863);
				tff_let_formula_defn();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(864);
				match(T__3);
				setState(865);
				tff_let_formula_list();
				setState(866);
				match(T__4);
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
		public Tff_let_formula_defnContext tff_let_formula_defn() {
			return getRuleContext(Tff_let_formula_defnContext.class,0);
		}
		public Tff_let_formula_listContext tff_let_formula_list() {
			return getRuleContext(Tff_let_formula_listContext.class,0);
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
		enterRule(_localctx, 176, RULE_tff_let_formula_list);
		try {
			setState(875);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(870);
				tff_let_formula_defn();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(871);
				tff_let_formula_defn();
				setState(872);
				match(T__2);
				setState(873);
				tff_let_formula_list();
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

	public static class Tff_let_formula_defnContext extends ParserRuleContext {
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
		}
		public Tff_let_formula_defnContext tff_let_formula_defn() {
			return getRuleContext(Tff_let_formula_defnContext.class,0);
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
		enterRule(_localctx, 178, RULE_tff_let_formula_defn);
		try {
			setState(885);
			switch (_input.LA(1)) {
			case T__40:
				enterOuterAlt(_localctx, 1);
				{
				setState(877);
				match(T__40);
				setState(878);
				match(T__3);
				setState(879);
				tff_variable_list();
				setState(880);
				match(T__4);
				setState(881);
				match(T__8);
				setState(882);
				tff_let_formula_defn();
				}
				break;
			case T__9:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 2);
				{
				setState(884);
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
		public Plain_atomic_formulaContext plain_atomic_formula() {
			return getRuleContext(Plain_atomic_formulaContext.class,0);
		}
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
		enterRule(_localctx, 180, RULE_tff_let_formula_binding);
		try {
			setState(895);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(887);
				plain_atomic_formula();
				setState(888);
				match(T__42);
				setState(889);
				tff_unitary_formula();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(891);
				match(T__9);
				setState(892);
				tff_let_formula_binding();
				setState(893);
				match(T__10);
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
		enterRule(_localctx, 182, RULE_tff_sequent);
		try {
			setState(905);
			switch (_input.LA(1)) {
			case T__3:
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(897);
				tff_formula_tuple();
				setState(898);
				match(T__43);
				setState(899);
				tff_formula_tuple();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(901);
				match(T__9);
				setState(902);
				tff_sequent();
				setState(903);
				match(T__10);
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
		enterRule(_localctx, 184, RULE_tff_formula_tuple);
		try {
			setState(912);
			switch (_input.LA(1)) {
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(907);
				match(T__15);
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(908);
				match(T__3);
				setState(909);
				tff_formula_tuple_list();
				setState(910);
				match(T__4);
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
		public Tff_logic_formulaContext tff_logic_formula() {
			return getRuleContext(Tff_logic_formulaContext.class,0);
		}
		public Tff_formula_tuple_listContext tff_formula_tuple_list() {
			return getRuleContext(Tff_formula_tuple_listContext.class,0);
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
		enterRule(_localctx, 186, RULE_tff_formula_tuple_list);
		try {
			setState(919);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(914);
				tff_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(915);
				tff_logic_formula();
				setState(916);
				match(T__2);
				setState(917);
				tff_formula_tuple_list();
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
		enterRule(_localctx, 188, RULE_tff_typed_atom);
		try {
			setState(929);
			switch (_input.LA(1)) {
			case Dollar_dollar_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(921);
				tff_untyped_atom();
				setState(922);
				match(T__8);
				setState(923);
				tff_top_level_type();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(925);
				match(T__9);
				setState(926);
				tff_typed_atom();
				setState(927);
				match(T__10);
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
		enterRule(_localctx, 190, RULE_tff_untyped_atom);
		try {
			setState(933);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(931);
				functor();
				}
				break;
			case Dollar_dollar_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(932);
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
		enterRule(_localctx, 192, RULE_tff_top_level_type);
		try {
			setState(942);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(935);
				tff_atomic_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(936);
				tff_mapping_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(937);
				tf1_quantified_type();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(938);
				match(T__9);
				setState(939);
				tff_top_level_type();
				setState(940);
				match(T__10);
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
		public Tff_variable_listContext tff_variable_list() {
			return getRuleContext(Tff_variable_listContext.class,0);
		}
		public Tff_monotypeContext tff_monotype() {
			return getRuleContext(Tff_monotypeContext.class,0);
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
		enterRule(_localctx, 194, RULE_tf1_quantified_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(944);
			match(T__29);
			setState(945);
			match(T__3);
			setState(946);
			tff_variable_list();
			setState(947);
			match(T__4);
			setState(948);
			match(T__8);
			setState(949);
			tff_monotype();
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
		enterRule(_localctx, 196, RULE_tff_monotype);
		try {
			setState(956);
			switch (_input.LA(1)) {
			case Dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(951);
				tff_atomic_type();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(952);
				match(T__9);
				setState(953);
				tff_mapping_type();
				setState(954);
				match(T__10);
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
		enterRule(_localctx, 198, RULE_tff_unitary_type);
		try {
			setState(963);
			switch (_input.LA(1)) {
			case Dollar_word:
			case Upper_word:
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(958);
				tff_atomic_type();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(959);
				match(T__9);
				setState(960);
				tff_xprod_type(0);
				setState(961);
				match(T__10);
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
		public Atomic_wordContext atomic_word() {
			return getRuleContext(Atomic_wordContext.class,0);
		}
		public Defined_typeContext defined_type() {
			return getRuleContext(Defined_typeContext.class,0);
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
		enterRule(_localctx, 200, RULE_tff_atomic_type);
		try {
			setState(973);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(965);
				atomic_word();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(966);
				defined_type();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(967);
				atomic_word();
				setState(968);
				match(T__9);
				setState(969);
				tff_type_arguments();
				setState(970);
				match(T__10);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(972);
				variable();
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

	public static class Tff_type_argumentsContext extends ParserRuleContext {
		public Tff_atomic_typeContext tff_atomic_type() {
			return getRuleContext(Tff_atomic_typeContext.class,0);
		}
		public Tff_type_argumentsContext tff_type_arguments() {
			return getRuleContext(Tff_type_argumentsContext.class,0);
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
		enterRule(_localctx, 202, RULE_tff_type_arguments);
		try {
			setState(980);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(975);
				tff_atomic_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(976);
				tff_atomic_type();
				setState(977);
				match(T__2);
				setState(978);
				tff_type_arguments();
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
		enterRule(_localctx, 204, RULE_tff_mapping_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(982);
			tff_unitary_type();
			setState(983);
			match(T__22);
			setState(984);
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
		int _startState = 206;
		enterRecursionRule(_localctx, 206, RULE_tff_xprod_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(987);
			tff_unitary_type();
			setState(988);
			match(T__23);
			setState(989);
			tff_atomic_type();
			}
			_ctx.stop = _input.LT(-1);
			setState(996);
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
					setState(991);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(992);
					match(T__23);
					setState(993);
					tff_atomic_type();
					}
					} 
				}
				setState(998);
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
		enterRule(_localctx, 208, RULE_fof_formula);
		try {
			setState(1001);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(999);
				fof_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1000);
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
		enterRule(_localctx, 210, RULE_fof_logic_formula);
		try {
			setState(1005);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1003);
				fof_binary_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1004);
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
		enterRule(_localctx, 212, RULE_fof_binary_formula);
		try {
			setState(1009);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1007);
				fof_binary_nonassoc();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1008);
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
		enterRule(_localctx, 214, RULE_fof_binary_nonassoc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1011);
			fof_unitary_formula();
			setState(1012);
			binary_connective();
			setState(1013);
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
		enterRule(_localctx, 216, RULE_fof_binary_assoc);
		try {
			setState(1017);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1015);
				fof_or_formula(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1016);
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
		int _startState = 218;
		enterRecursionRule(_localctx, 218, RULE_fof_or_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1020);
			fof_unitary_formula();
			setState(1021);
			match(T__16);
			setState(1022);
			fof_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(1029);
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
					setState(1024);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1025);
					match(T__16);
					setState(1026);
					fof_unitary_formula();
					}
					} 
				}
				setState(1031);
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
		int _startState = 220;
		enterRecursionRule(_localctx, 220, RULE_fof_and_formula, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(1033);
			fof_unitary_formula();
			setState(1034);
			match(T__17);
			setState(1035);
			fof_unitary_formula();
			}
			_ctx.stop = _input.LT(-1);
			setState(1042);
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
					setState(1037);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(1038);
					match(T__17);
					setState(1039);
					fof_unitary_formula();
					}
					} 
				}
				setState(1044);
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
		public Atomic_formulaContext atomic_formula() {
			return getRuleContext(Atomic_formulaContext.class,0);
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
		enterRule(_localctx, 222, RULE_fof_unitary_formula);
		try {
			setState(1052);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1045);
				fof_quantified_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1046);
				fof_unary_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1047);
				atomic_formula();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1048);
				match(T__9);
				setState(1049);
				fof_logic_formula();
				setState(1050);
				match(T__10);
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
		public Fol_quantifierContext fol_quantifier() {
			return getRuleContext(Fol_quantifierContext.class,0);
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
		enterRule(_localctx, 224, RULE_fof_quantified_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1054);
			fol_quantifier();
			setState(1055);
			match(T__3);
			setState(1056);
			fof_variable_list();
			setState(1057);
			match(T__4);
			setState(1058);
			match(T__8);
			setState(1059);
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
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Fof_variable_listContext fof_variable_list() {
			return getRuleContext(Fof_variable_listContext.class,0);
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
		enterRule(_localctx, 226, RULE_fof_variable_list);
		try {
			setState(1066);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1061);
				variable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1062);
				variable();
				setState(1063);
				match(T__2);
				setState(1064);
				fof_variable_list();
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

	public static class Fof_unary_formulaContext extends ParserRuleContext {
		public Unary_connectiveContext unary_connective() {
			return getRuleContext(Unary_connectiveContext.class,0);
		}
		public Fof_unitary_formulaContext fof_unitary_formula() {
			return getRuleContext(Fof_unitary_formulaContext.class,0);
		}
		public Fol_infix_unaryContext fol_infix_unary() {
			return getRuleContext(Fol_infix_unaryContext.class,0);
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
		enterRule(_localctx, 228, RULE_fof_unary_formula);
		try {
			setState(1072);
			switch (_input.LA(1)) {
			case T__50:
				enterOuterAlt(_localctx, 1);
				{
				setState(1068);
				unary_connective();
				setState(1069);
				fof_unitary_formula();
				}
				break;
			case T__52:
			case T__53:
			case T__54:
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
				setState(1071);
				fol_infix_unary();
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
		enterRule(_localctx, 230, RULE_fof_sequent);
		try {
			setState(1082);
			switch (_input.LA(1)) {
			case T__3:
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(1074);
				fof_formula_tuple();
				setState(1075);
				match(T__43);
				setState(1076);
				fof_formula_tuple();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(1078);
				match(T__9);
				setState(1079);
				fof_sequent();
				setState(1080);
				match(T__10);
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
		enterRule(_localctx, 232, RULE_fof_formula_tuple);
		try {
			setState(1089);
			switch (_input.LA(1)) {
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(1084);
				match(T__15);
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(1085);
				match(T__3);
				setState(1086);
				fof_formula_tuple_list();
				setState(1087);
				match(T__4);
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
		public Fof_logic_formulaContext fof_logic_formula() {
			return getRuleContext(Fof_logic_formulaContext.class,0);
		}
		public Fof_formula_tuple_listContext fof_formula_tuple_list() {
			return getRuleContext(Fof_formula_tuple_listContext.class,0);
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
		enterRule(_localctx, 234, RULE_fof_formula_tuple_list);
		try {
			setState(1096);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1091);
				fof_logic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1092);
				fof_logic_formula();
				setState(1093);
				match(T__2);
				setState(1094);
				fof_formula_tuple_list();
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

	public static class Fol_infix_unaryContext extends ParserRuleContext {
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public Infix_inequalityContext infix_inequality() {
			return getRuleContext(Infix_inequalityContext.class,0);
		}
		public Fol_infix_unaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fol_infix_unary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFol_infix_unary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFol_infix_unary(this);
		}
	}

	public final Fol_infix_unaryContext fol_infix_unary() throws RecognitionException {
		Fol_infix_unaryContext _localctx = new Fol_infix_unaryContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_fol_infix_unary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1098);
			term();
			setState(1099);
			infix_inequality();
			setState(1100);
			term();
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

	public static class Atomic_formulaContext extends ParserRuleContext {
		public Plain_atomic_formulaContext plain_atomic_formula() {
			return getRuleContext(Plain_atomic_formulaContext.class,0);
		}
		public Defined_atomic_formulaContext defined_atomic_formula() {
			return getRuleContext(Defined_atomic_formulaContext.class,0);
		}
		public System_atomic_formulaContext system_atomic_formula() {
			return getRuleContext(System_atomic_formulaContext.class,0);
		}
		public Atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterAtomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitAtomic_formula(this);
		}
	}

	public final Atomic_formulaContext atomic_formula() throws RecognitionException {
		Atomic_formulaContext _localctx = new Atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_atomic_formula);
		try {
			setState(1105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1102);
				plain_atomic_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1103);
				defined_atomic_formula();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1104);
				system_atomic_formula();
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

	public static class Plain_atomic_formulaContext extends ParserRuleContext {
		public Plain_termContext plain_term() {
			return getRuleContext(Plain_termContext.class,0);
		}
		public Plain_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plain_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterPlain_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitPlain_atomic_formula(this);
		}
	}

	public final Plain_atomic_formulaContext plain_atomic_formula() throws RecognitionException {
		Plain_atomic_formulaContext _localctx = new Plain_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_plain_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1107);
			plain_term();
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

	public static class Defined_atomic_formulaContext extends ParserRuleContext {
		public Defined_plain_formulaContext defined_plain_formula() {
			return getRuleContext(Defined_plain_formulaContext.class,0);
		}
		public Defined_infix_formulaContext defined_infix_formula() {
			return getRuleContext(Defined_infix_formulaContext.class,0);
		}
		public Defined_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_atomic_formula(this);
		}
	}

	public final Defined_atomic_formulaContext defined_atomic_formula() throws RecognitionException {
		Defined_atomic_formulaContext _localctx = new Defined_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 242, RULE_defined_atomic_formula);
		try {
			setState(1111);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1109);
				defined_plain_formula();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1110);
				defined_infix_formula();
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

	public static class Defined_plain_formulaContext extends ParserRuleContext {
		public Defined_plain_termContext defined_plain_term() {
			return getRuleContext(Defined_plain_termContext.class,0);
		}
		public Defined_plain_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_plain_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_plain_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_plain_formula(this);
		}
	}

	public final Defined_plain_formulaContext defined_plain_formula() throws RecognitionException {
		Defined_plain_formulaContext _localctx = new Defined_plain_formulaContext(_ctx, getState());
		enterRule(_localctx, 244, RULE_defined_plain_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1113);
			defined_plain_term();
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

	public static class System_atomic_formulaContext extends ParserRuleContext {
		public System_termContext system_term() {
			return getRuleContext(System_termContext.class,0);
		}
		public System_atomic_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_system_atomic_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterSystem_atomic_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitSystem_atomic_formula(this);
		}
	}

	public final System_atomic_formulaContext system_atomic_formula() throws RecognitionException {
		System_atomic_formulaContext _localctx = new System_atomic_formulaContext(_ctx, getState());
		enterRule(_localctx, 246, RULE_system_atomic_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1115);
			system_term();
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

	public static class Fol_quantifierContext extends ParserRuleContext {
		public Fol_quantifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fol_quantifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFol_quantifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFol_quantifier(this);
		}
	}

	public final Fol_quantifierContext fol_quantifier() throws RecognitionException {
		Fol_quantifierContext _localctx = new Fol_quantifierContext(_ctx, getState());
		enterRule(_localctx, 248, RULE_fol_quantifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1117);
			_la = _input.LA(1);
			if ( !(_la==T__40 || _la==T__44) ) {
			_errHandler.recoverInline(this);
			} else {
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
		enterRule(_localctx, 250, RULE_binary_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1119);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__42) | (1L << T__45) | (1L << T__46) | (1L << T__47) | (1L << T__48) | (1L << T__49))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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
		enterRule(_localctx, 252, RULE_assoc_connective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1121);
			_la = _input.LA(1);
			if ( !(_la==T__16 || _la==T__17) ) {
			_errHandler.recoverInline(this);
			} else {
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
		enterRule(_localctx, 254, RULE_unary_connective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1123);
			match(T__50);
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

	public static class Defined_infix_formulaContext extends ParserRuleContext {
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public Defined_infix_predContext defined_infix_pred() {
			return getRuleContext(Defined_infix_predContext.class,0);
		}
		public Defined_infix_formulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_infix_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_infix_formula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_infix_formula(this);
		}
	}

	public final Defined_infix_formulaContext defined_infix_formula() throws RecognitionException {
		Defined_infix_formulaContext _localctx = new Defined_infix_formulaContext(_ctx, getState());
		enterRule(_localctx, 256, RULE_defined_infix_formula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1125);
			term();
			setState(1126);
			defined_infix_pred();
			setState(1127);
			term();
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
		public Infix_equalityContext infix_equality() {
			return getRuleContext(Infix_equalityContext.class,0);
		}
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
		enterRule(_localctx, 258, RULE_defined_infix_pred);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1129);
			infix_equality();
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

	public static class Infix_equalityContext extends ParserRuleContext {
		public Infix_equalityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infix_equality; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterInfix_equality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitInfix_equality(this);
		}
	}

	public final Infix_equalityContext infix_equality() throws RecognitionException {
		Infix_equalityContext _localctx = new Infix_equalityContext(_ctx, getState());
		enterRule(_localctx, 260, RULE_infix_equality);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1131);
			match(T__41);
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

	public static class Infix_inequalityContext extends ParserRuleContext {
		public Infix_inequalityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infix_inequality; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterInfix_inequality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitInfix_inequality(this);
		}
	}

	public final Infix_inequalityContext infix_inequality() throws RecognitionException {
		Infix_inequalityContext _localctx = new Infix_inequalityContext(_ctx, getState());
		enterRule(_localctx, 262, RULE_infix_inequality);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1133);
			match(T__51);
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

	public static class TermContext extends ParserRuleContext {
		public Function_termContext function_term() {
			return getRuleContext(Function_termContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public Conditional_termContext conditional_term() {
			return getRuleContext(Conditional_termContext.class,0);
		}
		public Let_termContext let_term() {
			return getRuleContext(Let_termContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitTerm(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 264, RULE_term);
		try {
			setState(1139);
			switch (_input.LA(1)) {
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Dollar_dollar_word:
			case Lower_word:
			case Single_quoted:
			case Distinct_object:
				enterOuterAlt(_localctx, 1);
				{
				setState(1135);
				function_term();
				}
				break;
			case Upper_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(1136);
				variable();
				}
				break;
			case T__52:
				enterOuterAlt(_localctx, 3);
				{
				setState(1137);
				conditional_term();
				}
				break;
			case T__53:
			case T__54:
				enterOuterAlt(_localctx, 4);
				{
				setState(1138);
				let_term();
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

	public static class Function_termContext extends ParserRuleContext {
		public Plain_termContext plain_term() {
			return getRuleContext(Plain_termContext.class,0);
		}
		public Defined_termContext defined_term() {
			return getRuleContext(Defined_termContext.class,0);
		}
		public System_termContext system_term() {
			return getRuleContext(System_termContext.class,0);
		}
		public Function_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterFunction_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitFunction_term(this);
		}
	}

	public final Function_termContext function_term() throws RecognitionException {
		Function_termContext _localctx = new Function_termContext(_ctx, getState());
		enterRule(_localctx, 266, RULE_function_term);
		try {
			setState(1144);
			switch (_input.LA(1)) {
			case Lower_word:
			case Single_quoted:
				enterOuterAlt(_localctx, 1);
				{
				setState(1141);
				plain_term();
				}
				break;
			case Real:
			case Rational:
			case Integer:
			case Dollar_word:
			case Distinct_object:
				enterOuterAlt(_localctx, 2);
				{
				setState(1142);
				defined_term();
				}
				break;
			case Dollar_dollar_word:
				enterOuterAlt(_localctx, 3);
				{
				setState(1143);
				system_term();
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

	public static class Plain_termContext extends ParserRuleContext {
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public FunctorContext functor() {
			return getRuleContext(FunctorContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public Plain_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plain_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterPlain_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitPlain_term(this);
		}
	}

	public final Plain_termContext plain_term() throws RecognitionException {
		Plain_termContext _localctx = new Plain_termContext(_ctx, getState());
		enterRule(_localctx, 268, RULE_plain_term);
		try {
			setState(1152);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1146);
				constant();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1147);
				functor();
				setState(1148);
				match(T__9);
				setState(1149);
				arguments();
				setState(1150);
				match(T__10);
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
		enterRule(_localctx, 270, RULE_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1154);
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
		enterRule(_localctx, 272, RULE_functor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1156);
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

	public static class Defined_termContext extends ParserRuleContext {
		public Defined_atomContext defined_atom() {
			return getRuleContext(Defined_atomContext.class,0);
		}
		public Defined_atomic_termContext defined_atomic_term() {
			return getRuleContext(Defined_atomic_termContext.class,0);
		}
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
		enterRule(_localctx, 274, RULE_defined_term);
		try {
			setState(1160);
			switch (_input.LA(1)) {
			case Real:
			case Rational:
			case Integer:
			case Distinct_object:
				enterOuterAlt(_localctx, 1);
				{
				setState(1158);
				defined_atom();
				}
				break;
			case Dollar_word:
				enterOuterAlt(_localctx, 2);
				{
				setState(1159);
				defined_atomic_term();
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

	public static class Defined_atomContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode Distinct_object() { return getToken(tptpParser.Distinct_object, 0); }
		public Defined_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_atom(this);
		}
	}

	public final Defined_atomContext defined_atom() throws RecognitionException {
		Defined_atomContext _localctx = new Defined_atomContext(_ctx, getState());
		enterRule(_localctx, 276, RULE_defined_atom);
		try {
			setState(1164);
			switch (_input.LA(1)) {
			case Real:
			case Rational:
			case Integer:
				enterOuterAlt(_localctx, 1);
				{
				setState(1162);
				number();
				}
				break;
			case Distinct_object:
				enterOuterAlt(_localctx, 2);
				{
				setState(1163);
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

	public static class Defined_atomic_termContext extends ParserRuleContext {
		public Defined_plain_termContext defined_plain_term() {
			return getRuleContext(Defined_plain_termContext.class,0);
		}
		public Defined_atomic_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_atomic_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_atomic_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_atomic_term(this);
		}
	}

	public final Defined_atomic_termContext defined_atomic_term() throws RecognitionException {
		Defined_atomic_termContext _localctx = new Defined_atomic_termContext(_ctx, getState());
		enterRule(_localctx, 278, RULE_defined_atomic_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1166);
			defined_plain_term();
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

	public static class Defined_plain_termContext extends ParserRuleContext {
		public Defined_constantContext defined_constant() {
			return getRuleContext(Defined_constantContext.class,0);
		}
		public Defined_functorContext defined_functor() {
			return getRuleContext(Defined_functorContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public Defined_plain_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_plain_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterDefined_plain_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitDefined_plain_term(this);
		}
	}

	public final Defined_plain_termContext defined_plain_term() throws RecognitionException {
		Defined_plain_termContext _localctx = new Defined_plain_termContext(_ctx, getState());
		enterRule(_localctx, 280, RULE_defined_plain_term);
		try {
			setState(1174);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1168);
				defined_constant();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1169);
				defined_functor();
				setState(1170);
				match(T__9);
				setState(1171);
				arguments();
				setState(1172);
				match(T__10);
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
		enterRule(_localctx, 282, RULE_defined_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1176);
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
		enterRule(_localctx, 284, RULE_defined_functor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1178);
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

	public static class System_termContext extends ParserRuleContext {
		public System_constantContext system_constant() {
			return getRuleContext(System_constantContext.class,0);
		}
		public System_functorContext system_functor() {
			return getRuleContext(System_functorContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public System_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_system_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterSystem_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitSystem_term(this);
		}
	}

	public final System_termContext system_term() throws RecognitionException {
		System_termContext _localctx = new System_termContext(_ctx, getState());
		enterRule(_localctx, 286, RULE_system_term);
		try {
			setState(1186);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1180);
				system_constant();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1181);
				system_functor();
				setState(1182);
				match(T__9);
				setState(1183);
				arguments();
				setState(1184);
				match(T__10);
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
		enterRule(_localctx, 288, RULE_system_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1188);
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
		enterRule(_localctx, 290, RULE_system_functor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1190);
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

	public static class Conditional_termContext extends ParserRuleContext {
		public Tff_logic_formulaContext tff_logic_formula() {
			return getRuleContext(Tff_logic_formulaContext.class,0);
		}
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public Conditional_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterConditional_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitConditional_term(this);
		}
	}

	public final Conditional_termContext conditional_term() throws RecognitionException {
		Conditional_termContext _localctx = new Conditional_termContext(_ctx, getState());
		enterRule(_localctx, 292, RULE_conditional_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1192);
			match(T__52);
			setState(1193);
			tff_logic_formula();
			setState(1194);
			match(T__2);
			setState(1195);
			term();
			setState(1196);
			match(T__2);
			setState(1197);
			term();
			setState(1198);
			match(T__10);
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

	public static class Let_termContext extends ParserRuleContext {
		public Tff_let_formula_defnsContext tff_let_formula_defns() {
			return getRuleContext(Tff_let_formula_defnsContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public Tff_let_term_defnsContext tff_let_term_defns() {
			return getRuleContext(Tff_let_term_defnsContext.class,0);
		}
		public Let_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_let_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterLet_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitLet_term(this);
		}
	}

	public final Let_termContext let_term() throws RecognitionException {
		Let_termContext _localctx = new Let_termContext(_ctx, getState());
		enterRule(_localctx, 294, RULE_let_term);
		try {
			setState(1212);
			switch (_input.LA(1)) {
			case T__53:
				enterOuterAlt(_localctx, 1);
				{
				setState(1200);
				match(T__53);
				setState(1201);
				tff_let_formula_defns();
				setState(1202);
				match(T__2);
				setState(1203);
				term();
				setState(1204);
				match(T__10);
				}
				break;
			case T__54:
				enterOuterAlt(_localctx, 2);
				{
				setState(1206);
				match(T__54);
				setState(1207);
				tff_let_term_defns();
				setState(1208);
				match(T__2);
				setState(1209);
				term();
				setState(1210);
				match(T__10);
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

	public static class ArgumentsContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tptpListener ) ((tptpListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 296, RULE_arguments);
		try {
			setState(1219);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1214);
				term();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1215);
				term();
				setState(1216);
				match(T__2);
				setState(1217);
				arguments();
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
		enterRule(_localctx, 298, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1221);
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
		case 30:
			return thf_or_formula_sempred((Thf_or_formulaContext)_localctx, predIndex);
		case 31:
			return thf_and_formula_sempred((Thf_and_formulaContext)_localctx, predIndex);
		case 32:
			return thf_apply_formula_sempred((Thf_apply_formulaContext)_localctx, predIndex);
		case 55:
			return thf_xprod_type_sempred((Thf_xprod_typeContext)_localctx, predIndex);
		case 56:
			return thf_union_type_sempred((Thf_union_typeContext)_localctx, predIndex);
		case 73:
			return tff_or_formula_sempred((Tff_or_formulaContext)_localctx, predIndex);
		case 74:
			return tff_and_formula_sempred((Tff_and_formulaContext)_localctx, predIndex);
		case 103:
			return tff_xprod_type_sempred((Tff_xprod_typeContext)_localctx, predIndex);
		case 109:
			return fof_or_formula_sempred((Fof_or_formulaContext)_localctx, predIndex);
		case 110:
			return fof_and_formula_sempred((Fof_and_formulaContext)_localctx, predIndex);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3S\u04ca\4\2\t\2\4"+
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
		"\4\u0093\t\u0093\4\u0094\t\u0094\4\u0095\t\u0095\4\u0096\t\u0096\4\u0097"+
		"\t\u0097\3\2\7\2\u0130\n\2\f\2\16\2\u0133\13\2\3\2\3\2\3\3\3\3\5\3\u0139"+
		"\n\3\3\4\3\4\3\4\5\4\u013e\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\5\6\u014c\n\6\3\7\3\7\5\7\u0150\n\7\3\b\3\b\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\f\3\f\3\r\3\r\3\r\5\r\u015f\n\r\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\5\16\u0168\n\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\5\17\u0173\n\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u017e"+
		"\n\20\3\20\3\20\3\21\3\21\3\21\5\21\u0185\n\21\3\22\3\22\3\23\3\23\3\24"+
		"\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u0194\n\25\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\5\26\u019c\n\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30"+
		"\u01b3\n\30\3\31\3\31\3\31\3\31\3\31\5\31\u01ba\n\31\3\32\3\32\3\32\3"+
		"\32\3\32\5\32\u01c1\n\32\3\33\3\33\5\33\u01c5\n\33\3\34\3\34\3\34\3\34"+
		"\5\34\u01cb\n\34\3\35\3\35\5\35\u01cf\n\35\3\36\3\36\3\36\3\36\3\37\3"+
		"\37\3\37\5\37\u01d8\n\37\3 \3 \3 \3 \3 \3 \3 \3 \7 \u01e2\n \f \16 \u01e5"+
		"\13 \3!\3!\3!\3!\3!\3!\3!\3!\7!\u01ef\n!\f!\16!\u01f2\13!\3\"\3\"\3\""+
		"\3\"\3\"\3\"\3\"\3\"\7\"\u01fc\n\"\f\"\16\"\u01ff\13\"\3#\3#\3#\3#\3#"+
		"\3#\3#\3#\3#\3#\5#\u020b\n#\3$\3$\3$\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&"+
		"\5&\u021b\n&\3\'\3\'\5\'\u021f\n\'\3(\3(\3(\3(\3)\3)\3)\3)\3)\3*\3*\3"+
		"*\5*\u022d\n*\3+\3+\3+\5+\u0232\n+\3,\3,\3,\3,\3,\3,\5,\u023a\n,\3-\3"+
		"-\3-\3-\3-\3-\3-\5-\u0243\n-\3.\3.\3.\3.\3.\3.\5.\u024b\n.\3/\3/\3/\3"+
		"/\3/\3/\3/\3/\3\60\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\62\3\62\3\62\3"+
		"\62\3\63\3\63\3\63\3\63\3\63\5\63\u0266\n\63\3\64\3\64\3\64\3\64\3\65"+
		"\3\65\5\65\u026e\n\65\3\66\3\66\3\66\3\66\3\66\5\66\u0275\n\66\3\67\3"+
		"\67\3\67\5\67\u027a\n\67\38\38\38\38\38\38\38\38\58\u0284\n8\39\39\39"+
		"\39\39\39\39\39\79\u028e\n9\f9\169\u0291\139\3:\3:\3:\3:\3:\3:\3:\3:\7"+
		":\u029b\n:\f:\16:\u029e\13:\3;\3;\3;\3;\3;\3;\3;\3;\5;\u02a8\n;\3<\3<"+
		"\3<\3<\3<\5<\u02af\n<\3=\3=\3=\3=\3=\5=\u02b6\n=\3>\3>\3>\5>\u02bb\n>"+
		"\3?\3?\3?\5?\u02c0\n?\3@\3@\3A\3A\3B\3B\3B\3B\5B\u02ca\nB\3C\3C\5C\u02ce"+
		"\nC\3D\3D\3E\3E\3F\3F\3F\5F\u02d7\nF\3G\3G\5G\u02db\nG\3H\3H\5H\u02df"+
		"\nH\3I\3I\3I\3I\3J\3J\5J\u02e7\nJ\3K\3K\3K\3K\3K\3K\3K\3K\7K\u02f1\nK"+
		"\fK\16K\u02f4\13K\3L\3L\3L\3L\3L\3L\3L\3L\7L\u02fe\nL\fL\16L\u0301\13"+
		"L\3M\3M\3M\3M\3M\3M\3M\3M\3M\5M\u030c\nM\3N\3N\3N\3N\3N\3N\3N\3O\3O\3"+
		"O\3O\3O\5O\u031a\nO\3P\3P\5P\u031e\nP\3Q\3Q\3Q\3Q\3R\3R\3R\3R\5R\u0328"+
		"\nR\3S\3S\3S\3S\3S\3S\3S\3S\3T\3T\3T\3T\3T\3T\3T\3T\3T\3T\3T\3T\5T\u033e"+
		"\nT\3U\3U\3U\3U\3U\5U\u0345\nU\3V\3V\3V\3V\3V\5V\u034c\nV\3W\3W\3W\3W"+
		"\3W\3W\3W\3W\5W\u0356\nW\3X\3X\3X\3X\3X\3X\3X\3X\5X\u0360\nX\3Y\3Y\3Y"+
		"\3Y\3Y\5Y\u0367\nY\3Z\3Z\3Z\3Z\3Z\5Z\u036e\nZ\3[\3[\3[\3[\3[\3[\3[\3["+
		"\5[\u0378\n[\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\5\\\u0382\n\\\3]\3]\3]\3"+
		"]\3]\3]\3]\3]\5]\u038c\n]\3^\3^\3^\3^\3^\5^\u0393\n^\3_\3_\3_\3_\3_\5"+
		"_\u039a\n_\3`\3`\3`\3`\3`\3`\3`\3`\5`\u03a4\n`\3a\3a\5a\u03a8\na\3b\3"+
		"b\3b\3b\3b\3b\3b\5b\u03b1\nb\3c\3c\3c\3c\3c\3c\3c\3d\3d\3d\3d\3d\5d\u03bf"+
		"\nd\3e\3e\3e\3e\3e\5e\u03c6\ne\3f\3f\3f\3f\3f\3f\3f\3f\5f\u03d0\nf\3g"+
		"\3g\3g\3g\3g\5g\u03d7\ng\3h\3h\3h\3h\3i\3i\3i\3i\3i\3i\3i\3i\7i\u03e5"+
		"\ni\fi\16i\u03e8\13i\3j\3j\5j\u03ec\nj\3k\3k\5k\u03f0\nk\3l\3l\5l\u03f4"+
		"\nl\3m\3m\3m\3m\3n\3n\5n\u03fc\nn\3o\3o\3o\3o\3o\3o\3o\3o\7o\u0406\no"+
		"\fo\16o\u0409\13o\3p\3p\3p\3p\3p\3p\3p\3p\7p\u0413\np\fp\16p\u0416\13"+
		"p\3q\3q\3q\3q\3q\3q\3q\5q\u041f\nq\3r\3r\3r\3r\3r\3r\3r\3s\3s\3s\3s\3"+
		"s\5s\u042d\ns\3t\3t\3t\3t\5t\u0433\nt\3u\3u\3u\3u\3u\3u\3u\3u\5u\u043d"+
		"\nu\3v\3v\3v\3v\3v\5v\u0444\nv\3w\3w\3w\3w\3w\5w\u044b\nw\3x\3x\3x\3x"+
		"\3y\3y\3y\5y\u0454\ny\3z\3z\3{\3{\5{\u045a\n{\3|\3|\3}\3}\3~\3~\3\177"+
		"\3\177\3\u0080\3\u0080\3\u0081\3\u0081\3\u0082\3\u0082\3\u0082\3\u0082"+
		"\3\u0083\3\u0083\3\u0084\3\u0084\3\u0085\3\u0085\3\u0086\3\u0086\3\u0086"+
		"\3\u0086\5\u0086\u0476\n\u0086\3\u0087\3\u0087\3\u0087\5\u0087\u047b\n"+
		"\u0087\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\5\u0088\u0483\n"+
		"\u0088\3\u0089\3\u0089\3\u008a\3\u008a\3\u008b\3\u008b\5\u008b\u048b\n"+
		"\u008b\3\u008c\3\u008c\5\u008c\u048f\n\u008c\3\u008d\3\u008d\3\u008e\3"+
		"\u008e\3\u008e\3\u008e\3\u008e\3\u008e\5\u008e\u0499\n\u008e\3\u008f\3"+
		"\u008f\3\u0090\3\u0090\3\u0091\3\u0091\3\u0091\3\u0091\3\u0091\3\u0091"+
		"\5\u0091\u04a5\n\u0091\3\u0092\3\u0092\3\u0093\3\u0093\3\u0094\3\u0094"+
		"\3\u0094\3\u0094\3\u0094\3\u0094\3\u0094\3\u0094\3\u0095\3\u0095\3\u0095"+
		"\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095"+
		"\5\u0095\u04bf\n\u0095\3\u0096\3\u0096\3\u0096\3\u0096\3\u0096\5\u0096"+
		"\u04c6\n\u0096\3\u0097\3\u0097\3\u0097\2\f>@Bpr\u0094\u0096\u00d0\u00dc"+
		"\u00de\u0098\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64"+
		"\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088"+
		"\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0"+
		"\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8"+
		"\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0"+
		"\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8"+
		"\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa\u00fc\u00fe\u0100"+
		"\u0102\u0104\u0106\u0108\u010a\u010c\u010e\u0110\u0112\u0114\u0116\u0118"+
		"\u011a\u011c\u011e\u0120\u0122\u0124\u0126\u0128\u012a\u012c\2\n\3\2N"+
		"O\5\2::==@@\3\2\35\37\3\2 !\3\2#\'\4\2++//\4\2--\60\64\3\2\23\24\u04bb"+
		"\2\u0131\3\2\2\2\4\u0138\3\2\2\2\6\u013a\3\2\2\2\b\u0141\3\2\2\2\n\u014b"+
		"\3\2\2\2\f\u014f\3\2\2\2\16\u0151\3\2\2\2\20\u0153\3\2\2\2\22\u0155\3"+
		"\2\2\2\24\u0157\3\2\2\2\26\u0159\3\2\2\2\30\u015e\3\2\2\2\32\u0160\3\2"+
		"\2\2\34\u016b\3\2\2\2\36\u0176\3\2\2\2 \u0181\3\2\2\2\"\u0186\3\2\2\2"+
		"$\u0188\3\2\2\2&\u018a\3\2\2\2(\u0193\3\2\2\2*\u019b\3\2\2\2,\u019d\3"+
		"\2\2\2.\u01b2\3\2\2\2\60\u01b9\3\2\2\2\62\u01c0\3\2\2\2\64\u01c4\3\2\2"+
		"\2\66\u01ca\3\2\2\28\u01ce\3\2\2\2:\u01d0\3\2\2\2<\u01d7\3\2\2\2>\u01d9"+
		"\3\2\2\2@\u01e6\3\2\2\2B\u01f3\3\2\2\2D\u020a\3\2\2\2F\u020c\3\2\2\2H"+
		"\u020f\3\2\2\2J\u021a\3\2\2\2L\u021e\3\2\2\2N\u0220\3\2\2\2P\u0224\3\2"+
		"\2\2R\u022c\3\2\2\2T\u0231\3\2\2\2V\u0239\3\2\2\2X\u0242\3\2\2\2Z\u024a"+
		"\3\2\2\2\\\u024c\3\2\2\2^\u0254\3\2\2\2`\u025a\3\2\2\2b\u025c\3\2\2\2"+
		"d\u0265\3\2\2\2f\u0267\3\2\2\2h\u026d\3\2\2\2j\u0274\3\2\2\2l\u0279\3"+
		"\2\2\2n\u0283\3\2\2\2p\u0285\3\2\2\2r\u0292\3\2\2\2t\u02a7\3\2\2\2v\u02ae"+
		"\3\2\2\2x\u02b5\3\2\2\2z\u02ba\3\2\2\2|\u02bf\3\2\2\2~\u02c1\3\2\2\2\u0080"+
		"\u02c3\3\2\2\2\u0082\u02c9\3\2\2\2\u0084\u02cd\3\2\2\2\u0086\u02cf\3\2"+
		"\2\2\u0088\u02d1\3\2\2\2\u008a\u02d6\3\2\2\2\u008c\u02da\3\2\2\2\u008e"+
		"\u02de\3\2\2\2\u0090\u02e0\3\2\2\2\u0092\u02e6\3\2\2\2\u0094\u02e8\3\2"+
		"\2\2\u0096\u02f5\3\2\2\2\u0098\u030b\3\2\2\2\u009a\u030d\3\2\2\2\u009c"+
		"\u0319\3\2\2\2\u009e\u031d\3\2\2\2\u00a0\u031f\3\2\2\2\u00a2\u0327\3\2"+
		"\2\2\u00a4\u0329\3\2\2\2\u00a6\u033d\3\2\2\2\u00a8\u0344\3\2\2\2\u00aa"+
		"\u034b\3\2\2\2\u00ac\u0355\3\2\2\2\u00ae\u035f\3\2\2\2\u00b0\u0366\3\2"+
		"\2\2\u00b2\u036d\3\2\2\2\u00b4\u0377\3\2\2\2\u00b6\u0381\3\2\2\2\u00b8"+
		"\u038b\3\2\2\2\u00ba\u0392\3\2\2\2\u00bc\u0399\3\2\2\2\u00be\u03a3\3\2"+
		"\2\2\u00c0\u03a7\3\2\2\2\u00c2\u03b0\3\2\2\2\u00c4\u03b2\3\2\2\2\u00c6"+
		"\u03be\3\2\2\2\u00c8\u03c5\3\2\2\2\u00ca\u03cf\3\2\2\2\u00cc\u03d6\3\2"+
		"\2\2\u00ce\u03d8\3\2\2\2\u00d0\u03dc\3\2\2\2\u00d2\u03eb\3\2\2\2\u00d4"+
		"\u03ef\3\2\2\2\u00d6\u03f3\3\2\2\2\u00d8\u03f5\3\2\2\2\u00da\u03fb\3\2"+
		"\2\2\u00dc\u03fd\3\2\2\2\u00de\u040a\3\2\2\2\u00e0\u041e\3\2\2\2\u00e2"+
		"\u0420\3\2\2\2\u00e4\u042c\3\2\2\2\u00e6\u0432\3\2\2\2\u00e8\u043c\3\2"+
		"\2\2\u00ea\u0443\3\2\2\2\u00ec\u044a\3\2\2\2\u00ee\u044c\3\2\2\2\u00f0"+
		"\u0453\3\2\2\2\u00f2\u0455\3\2\2\2\u00f4\u0459\3\2\2\2\u00f6\u045b\3\2"+
		"\2\2\u00f8\u045d\3\2\2\2\u00fa\u045f\3\2\2\2\u00fc\u0461\3\2\2\2\u00fe"+
		"\u0463\3\2\2\2\u0100\u0465\3\2\2\2\u0102\u0467\3\2\2\2\u0104\u046b\3\2"+
		"\2\2\u0106\u046d\3\2\2\2\u0108\u046f\3\2\2\2\u010a\u0475\3\2\2\2\u010c"+
		"\u047a\3\2\2\2\u010e\u0482\3\2\2\2\u0110\u0484\3\2\2\2\u0112\u0486\3\2"+
		"\2\2\u0114\u048a\3\2\2\2\u0116\u048e\3\2\2\2\u0118\u0490\3\2\2\2\u011a"+
		"\u0498\3\2\2\2\u011c\u049a\3\2\2\2\u011e\u049c\3\2\2\2\u0120\u04a4\3\2"+
		"\2\2\u0122\u04a6\3\2\2\2\u0124\u04a8\3\2\2\2\u0126\u04aa\3\2\2\2\u0128"+
		"\u04be\3\2\2\2\u012a\u04c5\3\2\2\2\u012c\u04c7\3\2\2\2\u012e\u0130\5\4"+
		"\3\2\u012f\u012e\3\2\2\2\u0130\u0133\3\2\2\2\u0131\u012f\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0134\3\2\2\2\u0133\u0131\3\2\2\2\u0134\u0135\7\2"+
		"\2\3\u0135\3\3\2\2\2\u0136\u0139\5\30\r\2\u0137\u0139\5\6\4\2\u0138\u0136"+
		"\3\2\2\2\u0138\u0137\3\2\2\2\u0139\5\3\2\2\2\u013a\u013b\7\3\2\2\u013b"+
		"\u013d\5\26\f\2\u013c\u013e\5\b\5\2\u013d\u013c\3\2\2\2\u013d\u013e\3"+
		"\2\2\2\u013e\u013f\3\2\2\2\u013f\u0140\7\4\2\2\u0140\7\3\2\2\2\u0141\u0142"+
		"\7\5\2\2\u0142\u0143\7\6\2\2\u0143\u0144\5\n\6\2\u0144\u0145\7\7\2\2\u0145"+
		"\t\3\2\2\2\u0146\u014c\5\f\7\2\u0147\u0148\5\f\7\2\u0148\u0149\7\5\2\2"+
		"\u0149\u014a\5\n\6\2\u014a\u014c\3\2\2\2\u014b\u0146\3\2\2\2\u014b\u0147"+
		"\3\2\2\2\u014c\13\3\2\2\2\u014d\u0150\5\16\b\2\u014e\u0150\7@\2\2\u014f"+
		"\u014d\3\2\2\2\u014f\u014e\3\2\2\2\u0150\r\3\2\2\2\u0151\u0152\t\2\2\2"+
		"\u0152\17\3\2\2\2\u0153\u0154\7K\2\2\u0154\21\3\2\2\2\u0155\u0156\7L\2"+
		"\2\u0156\23\3\2\2\2\u0157\u0158\t\3\2\2\u0158\25\3\2\2\2\u0159\u015a\7"+
		"O\2\2\u015a\27\3\2\2\2\u015b\u015f\5\32\16\2\u015c\u015f\5\34\17\2\u015d"+
		"\u015f\5\36\20\2\u015e\u015b\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015d\3"+
		"\2\2\2\u015f\31\3\2\2\2\u0160\u0161\7\b\2\2\u0161\u0162\5\f\7\2\u0162"+
		"\u0163\7\5\2\2\u0163\u0164\5\"\22\2\u0164\u0165\7\5\2\2\u0165\u0167\5"+
		"\64\33\2\u0166\u0168\5 \21\2\u0167\u0166\3\2\2\2\u0167\u0168\3\2\2\2\u0168"+
		"\u0169\3\2\2\2\u0169\u016a\7\4\2\2\u016a\33\3\2\2\2\u016b\u016c\7\t\2"+
		"\2\u016c\u016d\5\f\7\2\u016d\u016e\7\5\2\2\u016e\u016f\5\"\22\2\u016f"+
		"\u0170\7\5\2\2\u0170\u0172\5\u008aF\2\u0171\u0173\5 \21\2\u0172\u0171"+
		"\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0175\7\4\2\2\u0175"+
		"\35\3\2\2\2\u0176\u0177\7\n\2\2\u0177\u0178\5\f\7\2\u0178\u0179\7\5\2"+
		"\2\u0179\u017a\5\"\22\2\u017a\u017b\7\5\2\2\u017b\u017d\5\u00d2j\2\u017c"+
		"\u017e\5 \21\2\u017d\u017c\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u017f\3\2"+
		"\2\2\u017f\u0180\7\4\2\2\u0180\37\3\2\2\2\u0181\u0182\7\5\2\2\u0182\u0184"+
		"\5$\23\2\u0183\u0185\5&\24\2\u0184\u0183\3\2\2\2\u0184\u0185\3\2\2\2\u0185"+
		"!\3\2\2\2\u0186\u0187\7N\2\2\u0187#\3\2\2\2\u0188\u0189\5(\25\2\u0189"+
		"%\3\2\2\2\u018a\u018b\7\5\2\2\u018b\u018c\5\60\31\2\u018c\'\3\2\2\2\u018d"+
		"\u0194\5*\26\2\u018e\u018f\5*\26\2\u018f\u0190\7\13\2\2\u0190\u0191\5"+
		"(\25\2\u0191\u0194\3\2\2\2\u0192\u0194\5\60\31\2\u0193\u018d\3\2\2\2\u0193"+
		"\u018e\3\2\2\2\u0193\u0192\3\2\2\2\u0194)\3\2\2\2\u0195\u019c\5\16\b\2"+
		"\u0196\u019c\5,\27\2\u0197\u019c\5\u012c\u0097\2\u0198\u019c\5\24\13\2"+
		"\u0199\u019c\7P\2\2\u019a\u019c\5.\30\2\u019b\u0195\3\2\2\2\u019b\u0196"+
		"\3\2\2\2\u019b\u0197\3\2\2\2\u019b\u0198\3\2\2\2\u019b\u0199\3\2\2\2\u019b"+
		"\u019a\3\2\2\2\u019c+\3\2\2\2\u019d\u019e\5\16\b\2\u019e\u019f\7\f\2\2"+
		"\u019f\u01a0\5\62\32\2\u01a0\u01a1\7\r\2\2\u01a1-\3\2\2\2\u01a2\u01a3"+
		"\7\16\2\2\u01a3\u01a4\5\64\33\2\u01a4\u01a5\7\r\2\2\u01a5\u01b3\3\2\2"+
		"\2\u01a6\u01a7\7\17\2\2\u01a7\u01a8\5\u008aF\2\u01a8\u01a9\7\r\2\2\u01a9"+
		"\u01b3\3\2\2\2\u01aa\u01ab\7\20\2\2\u01ab\u01ac\5\u00d2j\2\u01ac\u01ad"+
		"\7\r\2\2\u01ad\u01b3\3\2\2\2\u01ae\u01af\7\21\2\2\u01af\u01b0\5\u010a"+
		"\u0086\2\u01b0\u01b1\7\r\2\2\u01b1\u01b3\3\2\2\2\u01b2\u01a2\3\2\2\2\u01b2"+
		"\u01a6\3\2\2\2\u01b2\u01aa\3\2\2\2\u01b2\u01ae\3\2\2\2\u01b3/\3\2\2\2"+
		"\u01b4\u01ba\7\22\2\2\u01b5\u01b6\7\6\2\2\u01b6\u01b7\5\62\32\2\u01b7"+
		"\u01b8\7\7\2\2\u01b8\u01ba\3\2\2\2\u01b9\u01b4\3\2\2\2\u01b9\u01b5\3\2"+
		"\2\2\u01ba\61\3\2\2\2\u01bb\u01c1\5(\25\2\u01bc\u01bd\5(\25\2\u01bd\u01be"+
		"\7\5\2\2\u01be\u01bf\5\62\32\2\u01bf\u01c1\3\2\2\2\u01c0\u01bb\3\2\2\2"+
		"\u01c0\u01bc\3\2\2\2\u01c1\63\3\2\2\2\u01c2\u01c5\5\66\34\2\u01c3\u01c5"+
		"\5t;\2\u01c4\u01c2\3\2\2\2\u01c4\u01c3\3\2\2\2\u01c5\65\3\2\2\2\u01c6"+
		"\u01cb\58\35\2\u01c7\u01cb\5D#\2\u01c8\u01cb\5b\62\2\u01c9\u01cb\5f\64"+
		"\2\u01ca\u01c6\3\2\2\2\u01ca\u01c7\3\2\2\2\u01ca\u01c8\3\2\2\2\u01ca\u01c9"+
		"\3\2\2\2\u01cb\67\3\2\2\2\u01cc\u01cf\5:\36\2\u01cd\u01cf\5<\37\2\u01ce"+
		"\u01cc\3\2\2\2\u01ce\u01cd\3\2\2\2\u01cf9\3\2\2\2\u01d0\u01d1\5D#\2\u01d1"+
		"\u01d2\5\u0082B\2\u01d2\u01d3\5D#\2\u01d3;\3\2\2\2\u01d4\u01d8\5> \2\u01d5"+
		"\u01d8\5@!\2\u01d6\u01d8\5B\"\2\u01d7\u01d4\3\2\2\2\u01d7\u01d5\3\2\2"+
		"\2\u01d7\u01d6\3\2\2\2\u01d8=\3\2\2\2\u01d9\u01da\b \1\2\u01da\u01db\5"+
		"D#\2\u01db\u01dc\7\23\2\2\u01dc\u01dd\5D#\2\u01dd\u01e3\3\2\2\2\u01de"+
		"\u01df\f\3\2\2\u01df\u01e0\7\23\2\2\u01e0\u01e2\5D#\2\u01e1\u01de\3\2"+
		"\2\2\u01e2\u01e5\3\2\2\2\u01e3\u01e1\3\2\2\2\u01e3\u01e4\3\2\2\2\u01e4"+
		"?\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e6\u01e7\b!\1\2\u01e7\u01e8\5D#\2\u01e8"+
		"\u01e9\7\24\2\2\u01e9\u01ea\5D#\2\u01ea\u01f0\3\2\2\2\u01eb\u01ec\f\3"+
		"\2\2\u01ec\u01ed\7\24\2\2\u01ed\u01ef\5D#\2\u01ee\u01eb\3\2\2\2\u01ef"+
		"\u01f2\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f0\u01f1\3\2\2\2\u01f1A\3\2\2\2"+
		"\u01f2\u01f0\3\2\2\2\u01f3\u01f4\b\"\1\2\u01f4\u01f5\5D#\2\u01f5\u01f6"+
		"\7\25\2\2\u01f6\u01f7\5D#\2\u01f7\u01fd\3\2\2\2\u01f8\u01f9\f\3\2\2\u01f9"+
		"\u01fa\7\25\2\2\u01fa\u01fc\5D#\2\u01fb\u01f8\3\2\2\2\u01fc\u01ff\3\2"+
		"\2\2\u01fd\u01fb\3\2\2\2\u01fd\u01fe\3\2\2\2\u01feC\3\2\2\2\u01ff\u01fd"+
		"\3\2\2\2\u0200\u020b\5F$\2\u0201\u020b\5P)\2\u0202\u020b\5R*\2\u0203\u020b"+
		"\5\\/\2\u0204\u020b\5^\60\2\u0205\u020b\5v<\2\u0206\u0207\7\f\2\2\u0207"+
		"\u0208\5\66\34\2\u0208\u0209\7\r\2\2\u0209\u020b\3\2\2\2\u020a\u0200\3"+
		"\2\2\2\u020a\u0201\3\2\2\2\u020a\u0202\3\2\2\2\u020a\u0203\3\2\2\2\u020a"+
		"\u0204\3\2\2\2\u020a\u0205\3\2\2\2\u020a\u0206\3\2\2\2\u020bE\3\2\2\2"+
		"\u020c\u020d\5H%\2\u020d\u020e\5D#\2\u020eG\3\2\2\2\u020f\u0210\5|?\2"+
		"\u0210\u0211\7\6\2\2\u0211\u0212\5J&\2\u0212\u0213\7\7\2\2\u0213\u0214"+
		"\7\13\2\2\u0214I\3\2\2\2\u0215\u021b\5L\'\2\u0216\u0217\5L\'\2\u0217\u0218"+
		"\7\5\2\2\u0218\u0219\5J&\2\u0219\u021b\3\2\2\2\u021a\u0215\3\2\2\2\u021a"+
		"\u0216\3\2\2\2\u021bK\3\2\2\2\u021c\u021f\5N(\2\u021d\u021f\5\u012c\u0097"+
		"\2\u021e\u021c\3\2\2\2\u021e\u021d\3\2\2\2\u021fM\3\2\2\2\u0220\u0221"+
		"\5\u012c\u0097\2\u0221\u0222\7\13\2\2\u0222\u0223\5h\65\2\u0223O\3\2\2"+
		"\2\u0224\u0225\5\u0084C\2\u0225\u0226\7\f\2\2\u0226\u0227\5\66\34\2\u0227"+
		"\u0228\7\r\2\2\u0228Q\3\2\2\2\u0229\u022d\5T+\2\u022a\u022d\5\u012c\u0097"+
		"\2\u022b\u022d\5z>\2\u022c\u0229\3\2\2\2\u022c\u022a\3\2\2\2\u022c\u022b"+
		"\3\2\2\2\u022dS\3\2\2\2\u022e\u0232\5V,\2\u022f\u0232\5X-\2\u0230\u0232"+
		"\5Z.\2\u0231\u022e\3\2\2\2\u0231\u022f\3\2\2\2\u0231\u0230\3\2\2\2\u0232"+
		"U\3\2\2\2\u0233\u023a\5\u0110\u0089\2\u0234\u0235\5\u0112\u008a\2\u0235"+
		"\u0236\7\f\2\2\u0236\u0237\5`\61\2\u0237\u0238\7\r\2\2\u0238\u023a\3\2"+
		"\2\2\u0239\u0233\3\2\2\2\u0239\u0234\3\2\2\2\u023aW\3\2\2\2\u023b\u0243"+
		"\5\u0116\u008c\2\u023c\u0243\5\u011c\u008f\2\u023d\u023e\5\u011e\u0090"+
		"\2\u023e\u023f\7\f\2\2\u023f\u0240\5`\61\2\u0240\u0241\7\r\2\2\u0241\u0243"+
		"\3\2\2\2\u0242\u023b\3\2\2\2\u0242\u023c\3\2\2\2\u0242\u023d\3\2\2\2\u0243"+
		"Y\3\2\2\2\u0244\u024b\5\u0122\u0092\2\u0245\u0246\5\u0124\u0093\2\u0246"+
		"\u0247\7\f\2\2\u0247\u0248\5`\61\2\u0248\u0249\7\r\2\2\u0249\u024b\3\2"+
		"\2\2\u024a\u0244\3\2\2\2\u024a\u0245\3\2\2\2\u024b[\3\2\2\2\u024c\u024d"+
		"\7\26\2\2\u024d\u024e\5\66\34\2\u024e\u024f\7\5\2\2\u024f\u0250\5\66\34"+
		"\2\u0250\u0251\7\5\2\2\u0251\u0252\5\66\34\2\u0252\u0253\7\r\2\2\u0253"+
		"]\3\2\2\2\u0254\u0255\7\27\2\2\u0255\u0256\5D#\2\u0256\u0257\7\5\2\2\u0257"+
		"\u0258\5\64\33\2\u0258\u0259\7\r\2\2\u0259_\3\2\2\2\u025a\u025b\5x=\2"+
		"\u025ba\3\2\2\2\u025c\u025d\5d\63\2\u025d\u025e\7\13\2\2\u025e\u025f\5"+
		"h\65\2\u025fc\3\2\2\2\u0260\u0266\5R*\2\u0261\u0262\7\f\2\2\u0262\u0263"+
		"\5\66\34\2\u0263\u0264\7\r\2\2\u0264\u0266\3\2\2\2\u0265\u0260\3\2\2\2"+
		"\u0265\u0261\3\2\2\2\u0266e\3\2\2\2\u0267\u0268\5\u0110\u0089\2\u0268"+
		"\u0269\7\30\2\2\u0269\u026a\5\u0110\u0089\2\u026ag\3\2\2\2\u026b\u026e"+
		"\5j\66\2\u026c\u026e\5n8\2\u026d\u026b\3\2\2\2\u026d\u026c\3\2\2\2\u026e"+
		"i\3\2\2\2\u026f\u0275\5D#\2\u0270\u0271\7\f\2\2\u0271\u0272\5l\67\2\u0272"+
		"\u0273\7\r\2\2\u0273\u0275\3\2\2\2\u0274\u026f\3\2\2\2\u0274\u0270\3\2"+
		"\2\2\u0275k\3\2\2\2\u0276\u027a\5n8\2\u0277\u027a\5p9\2\u0278\u027a\5"+
		"r:\2\u0279\u0276\3\2\2\2\u0279\u0277\3\2\2\2\u0279\u0278\3\2\2\2\u027a"+
		"m\3\2\2\2\u027b\u027c\5j\66\2\u027c\u027d\7\31\2\2\u027d\u027e\5j\66\2"+
		"\u027e\u0284\3\2\2\2\u027f\u0280\5j\66\2\u0280\u0281\7\31\2\2\u0281\u0282"+
		"\5n8\2\u0282\u0284\3\2\2\2\u0283\u027b\3\2\2\2\u0283\u027f\3\2\2\2\u0284"+
		"o\3\2\2\2\u0285\u0286\b9\1\2\u0286\u0287\5j\66\2\u0287\u0288\7\32\2\2"+
		"\u0288\u0289\5j\66\2\u0289\u028f\3\2\2\2\u028a\u028b\f\3\2\2\u028b\u028c"+
		"\7\32\2\2\u028c\u028e\5j\66\2\u028d\u028a\3\2\2\2\u028e\u0291\3\2\2\2"+
		"\u028f\u028d\3\2\2\2\u028f\u0290\3\2\2\2\u0290q\3\2\2\2\u0291\u028f\3"+
		"\2\2\2\u0292\u0293\b:\1\2\u0293\u0294\5j\66\2\u0294\u0295\7\33\2\2\u0295"+
		"\u0296\5j\66\2\u0296\u029c\3\2\2\2\u0297\u0298\f\3\2\2\u0298\u0299\7\33"+
		"\2\2\u0299\u029b\5j\66\2\u029a\u0297\3\2\2\2\u029b\u029e\3\2\2\2\u029c"+
		"\u029a\3\2\2\2\u029c\u029d\3\2\2\2\u029ds\3\2\2\2\u029e\u029c\3\2\2\2"+
		"\u029f\u02a0\5v<\2\u02a0\u02a1\7\34\2\2\u02a1\u02a2\5v<\2\u02a2\u02a8"+
		"\3\2\2\2\u02a3\u02a4\7\f\2\2\u02a4\u02a5\5t;\2\u02a5\u02a6\7\r\2\2\u02a6"+
		"\u02a8\3\2\2\2\u02a7\u029f\3\2\2\2\u02a7\u02a3\3\2\2\2\u02a8u\3\2\2\2"+
		"\u02a9\u02af\7\22\2\2\u02aa\u02ab\7\6\2\2\u02ab\u02ac\5x=\2\u02ac\u02ad"+
		"\7\7\2\2\u02ad\u02af\3\2\2\2\u02ae\u02a9\3\2\2\2\u02ae\u02aa\3\2\2\2\u02af"+
		"w\3\2\2\2\u02b0\u02b6\5\66\34\2\u02b1\u02b2\5\66\34\2\u02b2\u02b3\7\5"+
		"\2\2\u02b3\u02b4\5x=\2\u02b4\u02b6\3\2\2\2\u02b5\u02b0\3\2\2\2\u02b5\u02b1"+
		"\3\2\2\2\u02b6y\3\2\2\2\u02b7\u02bb\5\u0082B\2\u02b8\u02bb\5\u00fe\u0080"+
		"\2\u02b9\u02bb\5\u0084C\2\u02ba\u02b7\3\2\2\2\u02ba\u02b8\3\2\2\2\u02ba"+
		"\u02b9\3\2\2\2\u02bb{\3\2\2\2\u02bc\u02c0\5\u00fa~\2\u02bd\u02c0\5~@\2"+
		"\u02be\u02c0\5\u0080A\2\u02bf\u02bc\3\2\2\2\u02bf\u02bd\3\2\2\2\u02bf"+
		"\u02be\3\2\2\2\u02c0}\3\2\2\2\u02c1\u02c2\t\4\2\2\u02c2\177\3\2\2\2\u02c3"+
		"\u02c4\t\5\2\2\u02c4\u0081\3\2\2\2\u02c5\u02ca\5\u0106\u0084\2\u02c6\u02ca"+
		"\5\u0108\u0085\2\u02c7\u02ca\5\u00fc\177\2\u02c8\u02ca\7\"\2\2\u02c9\u02c5"+
		"\3\2\2\2\u02c9\u02c6\3\2\2\2\u02c9\u02c7\3\2\2\2\u02c9\u02c8\3\2\2\2\u02ca"+
		"\u0083\3\2\2\2\u02cb\u02ce\5\u0100\u0081\2\u02cc\u02ce\5\u0086D\2\u02cd"+
		"\u02cb\3\2\2\2\u02cd\u02cc\3\2\2\2\u02ce\u0085\3\2\2\2\u02cf\u02d0\t\6"+
		"\2\2\u02d0\u0087\3\2\2\2\u02d1\u02d2\5\20\t\2\u02d2\u0089\3\2\2\2\u02d3"+
		"\u02d7\5\u008cG\2\u02d4\u02d7\5\u00be`\2\u02d5\u02d7\5\u00b8]\2\u02d6"+
		"\u02d3\3\2\2\2\u02d6\u02d4\3\2\2\2\u02d6\u02d5\3\2\2\2\u02d7\u008b\3\2"+
		"\2\2\u02d8\u02db\5\u008eH\2\u02d9\u02db\5\u0098M\2\u02da\u02d8\3\2\2\2"+
		"\u02da\u02d9\3\2\2\2\u02db\u008d\3\2\2\2\u02dc\u02df\5\u0090I\2\u02dd"+
		"\u02df\5\u0092J\2\u02de\u02dc\3\2\2\2\u02de\u02dd\3\2\2\2\u02df\u008f"+
		"\3\2\2\2\u02e0\u02e1\5\u0098M\2\u02e1\u02e2\5\u00fc\177\2\u02e2\u02e3"+
		"\5\u0098M\2\u02e3\u0091\3\2\2\2\u02e4\u02e7\5\u0094K\2\u02e5\u02e7\5\u0096"+
		"L\2\u02e6\u02e4\3\2\2\2\u02e6\u02e5\3\2\2\2\u02e7\u0093\3\2\2\2\u02e8"+
		"\u02e9\bK\1\2\u02e9\u02ea\5\u0098M\2\u02ea\u02eb\7\23\2\2\u02eb\u02ec"+
		"\5\u0098M\2\u02ec\u02f2\3\2\2\2\u02ed\u02ee\f\3\2\2\u02ee\u02ef\7\23\2"+
		"\2\u02ef\u02f1\5\u0098M\2\u02f0\u02ed\3\2\2\2\u02f1\u02f4\3\2\2\2\u02f2"+
		"\u02f0\3\2\2\2\u02f2\u02f3\3\2\2\2\u02f3\u0095\3\2\2\2\u02f4\u02f2\3\2"+
		"\2\2\u02f5\u02f6\bL\1\2\u02f6\u02f7\5\u0098M\2\u02f7\u02f8\7\24\2\2\u02f8"+
		"\u02f9\5\u0098M\2\u02f9\u02ff\3\2\2\2\u02fa\u02fb\f\3\2\2\u02fb\u02fc"+
		"\7\24\2\2\u02fc\u02fe\5\u0098M\2\u02fd\u02fa\3\2\2\2\u02fe\u0301\3\2\2"+
		"\2\u02ff\u02fd\3\2\2\2\u02ff\u0300\3\2\2\2\u0300\u0097\3\2\2\2\u0301\u02ff"+
		"\3\2\2\2\u0302\u030c\5\u009aN\2\u0303\u030c\5\u00a2R\2\u0304\u030c\5\u00f0"+
		"y\2\u0305\u030c\5\u00a4S\2\u0306\u030c\5\u00a6T\2\u0307\u0308\7\f\2\2"+
		"\u0308\u0309\5\u008cG\2\u0309\u030a\7\r\2\2\u030a\u030c\3\2\2\2\u030b"+
		"\u0302\3\2\2\2\u030b\u0303\3\2\2\2\u030b\u0304\3\2\2\2\u030b\u0305\3\2"+
		"\2\2\u030b\u0306\3\2\2\2\u030b\u0307\3\2\2\2\u030c\u0099\3\2\2\2\u030d"+
		"\u030e\5\u00fa~\2\u030e\u030f\7\6\2\2\u030f\u0310\5\u009cO\2\u0310\u0311"+
		"\7\7\2\2\u0311\u0312\7\13\2\2\u0312\u0313\5\u0098M\2\u0313\u009b\3\2\2"+
		"\2\u0314\u031a\5\u009eP\2\u0315\u0316\5\u009eP\2\u0316\u0317\7\5\2\2\u0317"+
		"\u0318\5\u009cO\2\u0318\u031a\3\2\2\2\u0319\u0314\3\2\2\2\u0319\u0315"+
		"\3\2\2\2\u031a\u009d\3\2\2\2\u031b\u031e\5\u00a0Q\2\u031c\u031e\5\u012c"+
		"\u0097\2\u031d\u031b\3\2\2\2\u031d\u031c\3\2\2\2\u031e\u009f\3\2\2\2\u031f"+
		"\u0320\5\u012c\u0097\2\u0320\u0321\7\13\2\2\u0321\u0322\5\u00caf\2\u0322"+
		"\u00a1\3\2\2\2\u0323\u0324\5\u0100\u0081\2\u0324\u0325\5\u0098M\2\u0325"+
		"\u0328\3\2\2\2\u0326\u0328\5\u00eex\2\u0327\u0323\3\2\2\2\u0327\u0326"+
		"\3\2\2\2\u0328\u00a3\3\2\2\2\u0329\u032a\7(\2\2\u032a\u032b\5\u008cG\2"+
		"\u032b\u032c\7\5\2\2\u032c\u032d\5\u008cG\2\u032d\u032e\7\5\2\2\u032e"+
		"\u032f\5\u008cG\2\u032f\u0330\7\r\2\2\u0330\u00a5\3\2\2\2\u0331\u0332"+
		"\7)\2\2\u0332\u0333\5\u00a8U\2\u0333\u0334\7\5\2\2\u0334\u0335\5\u008a"+
		"F\2\u0335\u0336\7\r\2\2\u0336\u033e\3\2\2\2\u0337\u0338\7*\2\2\u0338\u0339"+
		"\5\u00b0Y\2\u0339\u033a\7\5\2\2\u033a\u033b\5\u008aF\2\u033b\u033c\7\r"+
		"\2\2\u033c\u033e\3\2\2\2\u033d\u0331\3\2\2\2\u033d\u0337\3\2\2\2\u033e"+
		"\u00a7\3\2\2\2\u033f\u0345\5\u00acW\2\u0340\u0341\7\6\2\2\u0341\u0342"+
		"\5\u00aaV\2\u0342\u0343\7\7\2\2\u0343\u0345\3\2\2\2\u0344\u033f\3\2\2"+
		"\2\u0344\u0340\3\2\2\2\u0345\u00a9\3\2\2\2\u0346\u034c\5\u00acW\2\u0347"+
		"\u0348\5\u00acW\2\u0348\u0349\7\5\2\2\u0349\u034a\5\u00aaV\2\u034a\u034c"+
		"\3\2\2\2\u034b\u0346\3\2\2\2\u034b\u0347\3\2\2\2\u034c\u00ab\3\2\2\2\u034d"+
		"\u034e\7+\2\2\u034e\u034f\7\6\2\2\u034f\u0350\5\u009cO\2\u0350\u0351\7"+
		"\7\2\2\u0351\u0352\7\13\2\2\u0352\u0353\5\u00acW\2\u0353\u0356\3\2\2\2"+
		"\u0354\u0356\5\u00aeX\2\u0355\u034d\3\2\2\2\u0355\u0354\3\2\2\2\u0356"+
		"\u00ad\3\2\2\2\u0357\u0358\5\u010e\u0088\2\u0358\u0359\7,\2\2\u0359\u035a"+
		"\5\u010a\u0086\2\u035a\u0360\3\2\2\2\u035b\u035c\7\f\2\2\u035c\u035d\5"+
		"\u00aeX\2\u035d\u035e\7\r\2\2\u035e\u0360\3\2\2\2\u035f\u0357\3\2\2\2"+
		"\u035f\u035b\3\2\2\2\u0360\u00af\3\2\2\2\u0361\u0367\5\u00b4[\2\u0362"+
		"\u0363\7\6\2\2\u0363\u0364\5\u00b2Z\2\u0364\u0365\7\7\2\2\u0365\u0367"+
		"\3\2\2\2\u0366\u0361\3\2\2\2\u0366\u0362\3\2\2\2\u0367\u00b1\3\2\2\2\u0368"+
		"\u036e\5\u00b4[\2\u0369\u036a\5\u00b4[\2\u036a\u036b\7\5\2\2\u036b\u036c"+
		"\5\u00b2Z\2\u036c\u036e\3\2\2\2\u036d\u0368\3\2\2\2\u036d\u0369\3\2\2"+
		"\2\u036e\u00b3\3\2\2\2\u036f\u0370\7+\2\2\u0370\u0371\7\6\2\2\u0371\u0372"+
		"\5\u009cO\2\u0372\u0373\7\7\2\2\u0373\u0374\7\13\2\2\u0374\u0375\5\u00b4"+
		"[\2\u0375\u0378\3\2\2\2\u0376\u0378\5\u00b6\\\2\u0377\u036f\3\2\2\2\u0377"+
		"\u0376\3\2\2\2\u0378\u00b5\3\2\2\2\u0379\u037a\5\u00f2z\2\u037a\u037b"+
		"\7-\2\2\u037b\u037c\5\u0098M\2\u037c\u0382\3\2\2\2\u037d\u037e\7\f\2\2"+
		"\u037e\u037f\5\u00b6\\\2\u037f\u0380\7\r\2\2\u0380\u0382\3\2\2\2\u0381"+
		"\u0379\3\2\2\2\u0381\u037d\3\2\2\2\u0382\u00b7\3\2\2\2\u0383\u0384\5\u00ba"+
		"^\2\u0384\u0385\7.\2\2\u0385\u0386\5\u00ba^\2\u0386\u038c\3\2\2\2\u0387"+
		"\u0388\7\f\2\2\u0388\u0389\5\u00b8]\2\u0389\u038a\7\r\2\2\u038a\u038c"+
		"\3\2\2\2\u038b\u0383\3\2\2\2\u038b\u0387\3\2\2\2\u038c\u00b9\3\2\2\2\u038d"+
		"\u0393\7\22\2\2\u038e\u038f\7\6\2\2\u038f\u0390\5\u00bc_\2\u0390\u0391"+
		"\7\7\2\2\u0391\u0393\3\2\2\2\u0392\u038d\3\2\2\2\u0392\u038e\3\2\2\2\u0393"+
		"\u00bb\3\2\2\2\u0394\u039a\5\u008cG\2\u0395\u0396\5\u008cG\2\u0396\u0397"+
		"\7\5\2\2\u0397\u0398\5\u00bc_\2\u0398\u039a\3\2\2\2\u0399\u0394\3\2\2"+
		"\2\u0399\u0395\3\2\2\2\u039a\u00bd\3\2\2\2\u039b\u039c\5\u00c0a\2\u039c"+
		"\u039d\7\13\2\2\u039d\u039e\5\u00c2b\2\u039e\u03a4\3\2\2\2\u039f\u03a0"+
		"\7\f\2\2\u03a0\u03a1\5\u00be`\2\u03a1\u03a2\7\r\2\2\u03a2\u03a4\3\2\2"+
		"\2\u03a3\u039b\3\2\2\2\u03a3\u039f\3\2\2\2\u03a4\u00bf\3\2\2\2\u03a5\u03a8"+
		"\5\u0112\u008a\2\u03a6\u03a8\5\u0124\u0093\2\u03a7\u03a5\3\2\2\2\u03a7"+
		"\u03a6\3\2\2\2\u03a8\u00c1\3\2\2\2\u03a9\u03b1\5\u00caf\2\u03aa\u03b1"+
		"\5\u00ceh\2\u03ab\u03b1\5\u00c4c\2\u03ac\u03ad\7\f\2\2\u03ad\u03ae\5\u00c2"+
		"b\2\u03ae\u03af\7\r\2\2\u03af\u03b1\3\2\2\2\u03b0\u03a9\3\2\2\2\u03b0"+
		"\u03aa\3\2\2\2\u03b0\u03ab\3\2\2\2\u03b0\u03ac\3\2\2\2\u03b1\u00c3\3\2"+
		"\2\2\u03b2\u03b3\7 \2\2\u03b3\u03b4\7\6\2\2\u03b4\u03b5\5\u009cO\2\u03b5"+
		"\u03b6\7\7\2\2\u03b6\u03b7\7\13\2\2\u03b7\u03b8\5\u00c6d\2\u03b8\u00c5"+
		"\3\2\2\2\u03b9\u03bf\5\u00caf\2\u03ba\u03bb\7\f\2\2\u03bb\u03bc\5\u00ce"+
		"h\2\u03bc\u03bd\7\r\2\2\u03bd\u03bf\3\2\2\2\u03be\u03b9\3\2\2\2\u03be"+
		"\u03ba\3\2\2\2\u03bf\u00c7\3\2\2\2\u03c0\u03c6\5\u00caf\2\u03c1\u03c2"+
		"\7\f\2\2\u03c2\u03c3\5\u00d0i\2\u03c3\u03c4\7\r\2\2\u03c4\u03c6\3\2\2"+
		"\2\u03c5\u03c0\3\2\2\2\u03c5\u03c1\3\2\2\2\u03c6\u00c9\3\2\2\2\u03c7\u03d0"+
		"\5\16\b\2\u03c8\u03d0\5\u0088E\2\u03c9\u03ca\5\16\b\2\u03ca\u03cb\7\f"+
		"\2\2\u03cb\u03cc\5\u00ccg\2\u03cc\u03cd\7\r\2\2\u03cd\u03d0\3\2\2\2\u03ce"+
		"\u03d0\5\u012c\u0097\2\u03cf\u03c7\3\2\2\2\u03cf\u03c8\3\2\2\2\u03cf\u03c9"+
		"\3\2\2\2\u03cf\u03ce\3\2\2\2\u03d0\u00cb\3\2\2\2\u03d1\u03d7\5\u00caf"+
		"\2\u03d2\u03d3\5\u00caf\2\u03d3\u03d4\7\5\2\2\u03d4\u03d5\5\u00ccg\2\u03d5"+
		"\u03d7\3\2\2\2\u03d6\u03d1\3\2\2\2\u03d6\u03d2\3\2\2\2\u03d7\u00cd\3\2"+
		"\2\2\u03d8\u03d9\5\u00c8e\2\u03d9\u03da\7\31\2\2\u03da\u03db\5\u00caf"+
		"\2\u03db\u00cf\3\2\2\2\u03dc\u03dd\bi\1\2\u03dd\u03de\5\u00c8e\2\u03de"+
		"\u03df\7\32\2\2\u03df\u03e0\5\u00caf\2\u03e0\u03e6\3\2\2\2\u03e1\u03e2"+
		"\f\3\2\2\u03e2\u03e3\7\32\2\2\u03e3\u03e5\5\u00caf\2\u03e4\u03e1\3\2\2"+
		"\2\u03e5\u03e8\3\2\2\2\u03e6\u03e4\3\2\2\2\u03e6\u03e7\3\2\2\2\u03e7\u00d1"+
		"\3\2\2\2\u03e8\u03e6\3\2\2\2\u03e9\u03ec\5\u00d4k\2\u03ea\u03ec\5\u00e8"+
		"u\2\u03eb\u03e9\3\2\2\2\u03eb\u03ea\3\2\2\2\u03ec\u00d3\3\2\2\2\u03ed"+
		"\u03f0\5\u00d6l\2\u03ee\u03f0\5\u00e0q\2\u03ef\u03ed\3\2\2\2\u03ef\u03ee"+
		"\3\2\2\2\u03f0\u00d5\3\2\2\2\u03f1\u03f4\5\u00d8m\2\u03f2\u03f4\5\u00da"+
		"n\2\u03f3\u03f1\3\2\2\2\u03f3\u03f2\3\2\2\2\u03f4\u00d7\3\2\2\2\u03f5"+
		"\u03f6\5\u00e0q\2\u03f6\u03f7\5\u00fc\177\2\u03f7\u03f8\5\u00e0q\2\u03f8"+
		"\u00d9\3\2\2\2\u03f9\u03fc\5\u00dco\2\u03fa\u03fc\5\u00dep\2\u03fb\u03f9"+
		"\3\2\2\2\u03fb\u03fa\3\2\2\2\u03fc\u00db\3\2\2\2\u03fd\u03fe\bo\1\2\u03fe"+
		"\u03ff\5\u00e0q\2\u03ff\u0400\7\23\2\2\u0400\u0401\5\u00e0q\2\u0401\u0407"+
		"\3\2\2\2\u0402\u0403\f\3\2\2\u0403\u0404\7\23\2\2\u0404\u0406\5\u00e0"+
		"q\2\u0405\u0402\3\2\2\2\u0406\u0409\3\2\2\2\u0407\u0405\3\2\2\2\u0407"+
		"\u0408\3\2\2\2\u0408\u00dd\3\2\2\2\u0409\u0407\3\2\2\2\u040a\u040b\bp"+
		"\1\2\u040b\u040c\5\u00e0q\2\u040c\u040d\7\24\2\2\u040d\u040e\5\u00e0q"+
		"\2\u040e\u0414\3\2\2\2\u040f\u0410\f\3\2\2\u0410\u0411\7\24\2\2\u0411"+
		"\u0413\5\u00e0q\2\u0412\u040f\3\2\2\2\u0413\u0416\3\2\2\2\u0414\u0412"+
		"\3\2\2\2\u0414\u0415\3\2\2\2\u0415\u00df\3\2\2\2\u0416\u0414\3\2\2\2\u0417"+
		"\u041f\5\u00e2r\2\u0418\u041f\5\u00e6t\2\u0419\u041f\5\u00f0y\2\u041a"+
		"\u041b\7\f\2\2\u041b\u041c\5\u00d4k\2\u041c\u041d\7\r\2\2\u041d\u041f"+
		"\3\2\2\2\u041e\u0417\3\2\2\2\u041e\u0418\3\2\2\2\u041e\u0419\3\2\2\2\u041e"+
		"\u041a\3\2\2\2\u041f\u00e1\3\2\2\2\u0420\u0421\5\u00fa~\2\u0421\u0422"+
		"\7\6\2\2\u0422\u0423\5\u00e4s\2\u0423\u0424\7\7\2\2\u0424\u0425\7\13\2"+
		"\2\u0425\u0426\5\u00e0q\2\u0426\u00e3\3\2\2\2\u0427\u042d\5\u012c\u0097"+
		"\2\u0428\u0429\5\u012c\u0097\2\u0429\u042a\7\5\2\2\u042a\u042b\5\u00e4"+
		"s\2\u042b\u042d\3\2\2\2\u042c\u0427\3\2\2\2\u042c\u0428\3\2\2\2\u042d"+
		"\u00e5\3\2\2\2\u042e\u042f\5\u0100\u0081\2\u042f\u0430\5\u00e0q\2\u0430"+
		"\u0433\3\2\2\2\u0431\u0433\5\u00eex\2\u0432\u042e\3\2\2\2\u0432\u0431"+
		"\3\2\2\2\u0433\u00e7\3\2\2\2\u0434\u0435\5\u00eav\2\u0435\u0436\7.\2\2"+
		"\u0436\u0437\5\u00eav\2\u0437\u043d\3\2\2\2\u0438\u0439\7\f\2\2\u0439"+
		"\u043a\5\u00e8u\2\u043a\u043b\7\r\2\2\u043b\u043d\3\2\2\2\u043c\u0434"+
		"\3\2\2\2\u043c\u0438\3\2\2\2\u043d\u00e9\3\2\2\2\u043e\u0444\7\22\2\2"+
		"\u043f\u0440\7\6\2\2\u0440\u0441\5\u00ecw\2\u0441\u0442\7\7\2\2\u0442"+
		"\u0444\3\2\2\2\u0443\u043e\3\2\2\2\u0443\u043f\3\2\2\2\u0444\u00eb\3\2"+
		"\2\2\u0445\u044b\5\u00d4k\2\u0446\u0447\5\u00d4k\2\u0447\u0448\7\5\2\2"+
		"\u0448\u0449\5\u00ecw\2\u0449\u044b\3\2\2\2\u044a\u0445\3\2\2\2\u044a"+
		"\u0446\3\2\2\2\u044b\u00ed\3\2\2\2\u044c\u044d\5\u010a\u0086\2\u044d\u044e"+
		"\5\u0108\u0085\2\u044e\u044f\5\u010a\u0086\2\u044f\u00ef\3\2\2\2\u0450"+
		"\u0454\5\u00f2z\2\u0451\u0454\5\u00f4{\2\u0452\u0454\5\u00f8}\2\u0453"+
		"\u0450\3\2\2\2\u0453\u0451\3\2\2\2\u0453\u0452\3\2\2\2\u0454\u00f1\3\2"+
		"\2\2\u0455\u0456\5\u010e\u0088\2\u0456\u00f3\3\2\2\2\u0457\u045a\5\u00f6"+
		"|\2\u0458\u045a\5\u0102\u0082\2\u0459\u0457\3\2\2\2\u0459\u0458\3\2\2"+
		"\2\u045a\u00f5\3\2\2\2\u045b\u045c\5\u011a\u008e\2\u045c\u00f7\3\2\2\2"+
		"\u045d\u045e\5\u0120\u0091\2\u045e\u00f9\3\2\2\2\u045f\u0460\t\7\2\2\u0460"+
		"\u00fb\3\2\2\2\u0461\u0462\t\b\2\2\u0462\u00fd\3\2\2\2\u0463\u0464\t\t"+
		"\2\2\u0464\u00ff\3\2\2\2\u0465\u0466\7\65\2\2\u0466\u0101\3\2\2\2\u0467"+
		"\u0468\5\u010a\u0086\2\u0468\u0469\5\u0104\u0083\2\u0469\u046a\5\u010a"+
		"\u0086\2\u046a\u0103\3\2\2\2\u046b\u046c\5\u0106\u0084\2\u046c\u0105\3"+
		"\2\2\2\u046d\u046e\7,\2\2\u046e\u0107\3\2\2\2\u046f\u0470\7\66\2\2\u0470"+
		"\u0109\3\2\2\2\u0471\u0476\5\u010c\u0087\2\u0472\u0476\5\u012c\u0097\2"+
		"\u0473\u0476\5\u0126\u0094\2\u0474\u0476\5\u0128\u0095\2\u0475\u0471\3"+
		"\2\2\2\u0475\u0472\3\2\2\2\u0475\u0473\3\2\2\2\u0475\u0474\3\2\2\2\u0476"+
		"\u010b\3\2\2\2\u0477\u047b\5\u010e\u0088\2\u0478\u047b\5\u0114\u008b\2"+
		"\u0479\u047b\5\u0120\u0091\2\u047a\u0477\3\2\2\2\u047a\u0478\3\2\2\2\u047a"+
		"\u0479\3\2\2\2\u047b\u010d\3\2\2\2\u047c\u0483\5\u0110\u0089\2\u047d\u047e"+
		"\5\u0112\u008a\2\u047e\u047f\7\f\2\2\u047f\u0480\5\u012a\u0096\2\u0480"+
		"\u0481\7\r\2\2\u0481\u0483\3\2\2\2\u0482\u047c\3\2\2\2\u0482\u047d\3\2"+
		"\2\2\u0483\u010f\3\2\2\2\u0484\u0485\5\u0112\u008a\2\u0485\u0111\3\2\2"+
		"\2\u0486\u0487\5\16\b\2\u0487\u0113\3\2\2\2\u0488\u048b\5\u0116\u008c"+
		"\2\u0489\u048b\5\u0118\u008d\2\u048a\u0488\3\2\2\2\u048a\u0489\3\2\2\2"+
		"\u048b\u0115\3\2\2\2\u048c\u048f\5\24\13\2\u048d\u048f\7P\2\2\u048e\u048c"+
		"\3\2\2\2\u048e\u048d\3\2\2\2\u048f\u0117\3\2\2\2\u0490\u0491\5\u011a\u008e"+
		"\2\u0491\u0119\3\2\2\2\u0492\u0499\5\u011c\u008f\2\u0493\u0494\5\u011e"+
		"\u0090\2\u0494\u0495\7\f\2\2\u0495\u0496\5\u012a\u0096\2\u0496\u0497\7"+
		"\r\2\2\u0497\u0499\3\2\2\2\u0498\u0492\3\2\2\2\u0498\u0493\3\2\2\2\u0499"+
		"\u011b\3\2\2\2\u049a\u049b\5\u011e\u0090\2\u049b\u011d\3\2\2\2\u049c\u049d"+
		"\5\20\t\2\u049d\u011f\3\2\2\2\u049e\u04a5\5\u0122\u0092\2\u049f\u04a0"+
		"\5\u0124\u0093\2\u04a0\u04a1\7\f\2\2\u04a1\u04a2\5\u012a\u0096\2\u04a2"+
		"\u04a3\7\r\2\2\u04a3\u04a5\3\2\2\2\u04a4\u049e\3\2\2\2\u04a4\u049f\3\2"+
		"\2\2\u04a5\u0121\3\2\2\2\u04a6\u04a7\5\u0124\u0093\2\u04a7\u0123\3\2\2"+
		"\2\u04a8\u04a9\5\22\n\2\u04a9\u0125\3\2\2\2\u04aa\u04ab\7\67\2\2\u04ab"+
		"\u04ac\5\u008cG\2\u04ac\u04ad\7\5\2\2\u04ad\u04ae\5\u010a\u0086\2\u04ae"+
		"\u04af\7\5\2\2\u04af\u04b0\5\u010a\u0086\2\u04b0\u04b1\7\r\2\2\u04b1\u0127"+
		"\3\2\2\2\u04b2\u04b3\78\2\2\u04b3\u04b4\5\u00b0Y\2\u04b4\u04b5\7\5\2\2"+
		"\u04b5\u04b6\5\u010a\u0086\2\u04b6\u04b7\7\r\2\2\u04b7\u04bf\3\2\2\2\u04b8"+
		"\u04b9\79\2\2\u04b9\u04ba\5\u00a8U\2\u04ba\u04bb\7\5\2\2\u04bb\u04bc\5"+
		"\u010a\u0086\2\u04bc\u04bd\7\r\2\2\u04bd\u04bf\3\2\2\2\u04be\u04b2\3\2"+
		"\2\2\u04be\u04b8\3\2\2\2\u04bf\u0129\3\2\2\2\u04c0\u04c6\5\u010a\u0086"+
		"\2\u04c1\u04c2\5\u010a\u0086\2\u04c2\u04c3\7\5\2\2\u04c3\u04c4\5\u012a"+
		"\u0096\2\u04c4\u04c6\3\2\2\2\u04c5\u04c0\3\2\2\2\u04c5\u04c1\3\2\2\2\u04c6"+
		"\u012b\3\2\2\2\u04c7\u04c8\7M\2\2\u04c8\u012d\3\2\2\2c\u0131\u0138\u013d"+
		"\u014b\u014f\u015e\u0167\u0172\u017d\u0184\u0193\u019b\u01b2\u01b9\u01c0"+
		"\u01c4\u01ca\u01ce\u01d7\u01e3\u01f0\u01fd\u020a\u021a\u021e\u022c\u0231"+
		"\u0239\u0242\u024a\u0265\u026d\u0274\u0279\u0283\u028f\u029c\u02a7\u02ae"+
		"\u02b5\u02ba\u02bf\u02c9\u02cd\u02d6\u02da\u02de\u02e6\u02f2\u02ff\u030b"+
		"\u0319\u031d\u0327\u033d\u0344\u034b\u0355\u035f\u0366\u036d\u0377\u0381"+
		"\u038b\u0392\u0399\u03a3\u03a7\u03b0\u03be\u03c5\u03cf\u03d6\u03e6\u03eb"+
		"\u03ef\u03f3\u03fb\u0407\u0414\u041e\u042c\u0432\u043c\u0443\u044a\u0453"+
		"\u0459\u0475\u047a\u0482\u048a\u048e\u0498\u04a4\u04be\u04c5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}