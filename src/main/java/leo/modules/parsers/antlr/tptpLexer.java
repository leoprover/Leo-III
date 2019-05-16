// Generated from /home/lex/dev/Leo-III/contrib/tptp.g4 by ANTLR 4.7.2

package leo.modules.parsers.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class tptpLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
			"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
			"T__33", "T__34", "Do_char", "Sq_char", "Sign", "Exponent", "Non_zero_numeric", 
			"Numeric", "Lower_alpha", "Upper_alpha", "Alpha_numeric", "Or", "And", 
			"Iff", "Impl", "If", "Niff", "Nor", "Nand", "Not", "ForallComb", "TyForall", 
			"Infix_inequality", "Infix_equality", "Forall", "ExistsComb", "TyExists", 
			"Exists", "Lambda", "ChoiceComb", "Choice", "DescriptionComb", "Description", 
			"EqComb", "App", "Assignment", "Real", "Signed_real", "Unsigned_real", 
			"Rational", "Signed_rational", "Unsigned_rational", "Integer", "Signed_integer", 
			"Unsigned_integer", "Decimal", "Positive_decimal", "Decimal_exponent", 
			"Decimal_fraction", "Dot_decimal", "Exp_integer", "Signed_exp_integer", 
			"Unsigned_exp_integer", "Dollar_word", "Dollar_dollar_word", "Upper_word", 
			"Lower_word", "Single_quoted", "Distinct_object", "WS", "Line_comment", 
			"Block_comment"
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


	public tptpLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "tptp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2X\u0250\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3"+
		"\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f"+
		"\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3 \3 \3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\""+
		"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3$\3$\3$\3$\3%\3%\3"+
		"%\5%\u0163\n%\3&\3&\3&\5&\u0168\n&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,"+
		"\3,\3-\3-\3-\3-\5-\u017a\n-\3.\3.\3/\3/\3\60\3\60\3\60\3\60\3\61\3\61"+
		"\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65"+
		"\3\66\3\66\3\67\3\67\3\67\38\38\38\39\39\39\3:\3:\3;\3;\3<\3<\3<\3=\3"+
		"=\3=\3>\3>\3?\3?\3@\3@\3@\3@\3A\3A\3A\3B\3B\3B\3B\3C\3C\3C\3D\3D\3D\3"+
		"E\3E\3F\3F\3F\3G\3G\5G\u01c5\nG\3H\3H\3H\3I\3I\5I\u01cc\nI\3J\3J\5J\u01d0"+
		"\nJ\3K\3K\3K\3L\3L\3L\3L\3M\3M\5M\u01db\nM\3N\3N\3N\3O\3O\3P\3P\5P\u01e4"+
		"\nP\3Q\3Q\7Q\u01e8\nQ\fQ\16Q\u01eb\13Q\3R\3R\5R\u01ef\nR\3R\3R\3R\3S\3"+
		"S\3S\3T\3T\3T\7T\u01fa\nT\fT\16T\u01fd\13T\3U\3U\5U\u0201\nU\3V\3V\3V"+
		"\3W\3W\7W\u0208\nW\fW\16W\u020b\13W\3X\3X\3X\3Y\3Y\3Y\3Y\3Y\3Z\3Z\7Z\u0217"+
		"\nZ\fZ\16Z\u021a\13Z\3[\3[\7[\u021e\n[\f[\16[\u0221\13[\3\\\3\\\6\\\u0225"+
		"\n\\\r\\\16\\\u0226\3\\\3\\\3]\3]\6]\u022d\n]\r]\16]\u022e\3]\3]\3^\6"+
		"^\u0234\n^\r^\16^\u0235\3^\3^\3_\3_\7_\u023c\n_\f_\16_\u023f\13_\3_\3"+
		"_\3`\3`\3`\3`\7`\u0247\n`\f`\16`\u024a\13`\3`\3`\3`\3`\3`\3\u0248\2a\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37="+
		" ?!A\"C#E$G%I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[&]\'_(a)c*e+g,i-k.m/o\60q\61s"+
		"\62u\63w\64y\65{\66}\67\1778\u00819\u0083:\u0085;\u0087<\u0089=\u008b"+
		">\u008d?\u008f@\u0091A\u0093B\u0095C\u0097D\u0099E\u009bF\u009dG\u009f"+
		"H\u00a1I\u00a3J\u00a5K\u00a7L\u00a9M\u00abN\u00adO\u00afP\u00b1Q\u00b3"+
		"R\u00b5S\u00b7T\u00b9U\u00bbV\u00bdW\u00bfX\3\2\16\5\2\"#%]_\u0080\4\2"+
		"$$^^\5\2\"(*]_\u0080\4\2))^^\4\2--//\4\2GGgg\3\2\63;\3\2\62;\3\2c|\3\2"+
		"C\\\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2\u025c\2\3\3\2\2\2\2\5\3\2\2\2\2"+
		"\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2"+
		"\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2"+
		"\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2"+
		"\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2"+
		"\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2"+
		"_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3"+
		"\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2"+
		"\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083"+
		"\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2"+
		"\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095"+
		"\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d\3\2\2"+
		"\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7"+
		"\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\2\u00af\3\2\2"+
		"\2\2\u00b1\3\2\2\2\2\u00b3\3\2\2\2\2\u00b5\3\2\2\2\2\u00b7\3\2\2\2\2\u00b9"+
		"\3\2\2\2\2\u00bb\3\2\2\2\2\u00bd\3\2\2\2\2\u00bf\3\2\2\2\3\u00c1\3\2\2"+
		"\2\5\u00c9\3\2\2\2\7\u00cb\3\2\2\2\t\u00cd\3\2\2\2\13\u00cf\3\2\2\2\r"+
		"\u00d1\3\2\2\2\17\u00d3\3\2\2\2\21\u00d5\3\2\2\2\23\u00d9\3\2\2\2\25\u00dd"+
		"\3\2\2\2\27\u00e1\3\2\2\2\31\u00e5\3\2\2\2\33\u00e7\3\2\2\2\35\u00ed\3"+
		"\2\2\2\37\u00f3\3\2\2\2!\u00f9\3\2\2\2#\u00ff\3\2\2\2%\u0105\3\2\2\2\'"+
		"\u0108\3\2\2\2)\u010e\3\2\2\2+\u0114\3\2\2\2-\u0117\3\2\2\2/\u0119\3\2"+
		"\2\2\61\u011b\3\2\2\2\63\u011d\3\2\2\2\65\u0121\3\2\2\2\67\u0124\3\2\2"+
		"\29\u0126\3\2\2\2;\u0128\3\2\2\2=\u0130\3\2\2\2?\u0139\3\2\2\2A\u0142"+
		"\3\2\2\2C\u0145\3\2\2\2E\u014d\3\2\2\2G\u0156\3\2\2\2I\u0162\3\2\2\2K"+
		"\u0167\3\2\2\2M\u0169\3\2\2\2O\u016b\3\2\2\2Q\u016d\3\2\2\2S\u016f\3\2"+
		"\2\2U\u0171\3\2\2\2W\u0173\3\2\2\2Y\u0179\3\2\2\2[\u017b\3\2\2\2]\u017d"+
		"\3\2\2\2_\u017f\3\2\2\2a\u0183\3\2\2\2c\u0186\3\2\2\2e\u0189\3\2\2\2g"+
		"\u018d\3\2\2\2i\u0190\3\2\2\2k\u0193\3\2\2\2m\u0195\3\2\2\2o\u0198\3\2"+
		"\2\2q\u019b\3\2\2\2s\u019e\3\2\2\2u\u01a0\3\2\2\2w\u01a2\3\2\2\2y\u01a5"+
		"\3\2\2\2{\u01a8\3\2\2\2}\u01aa\3\2\2\2\177\u01ac\3\2\2\2\u0081\u01b0\3"+
		"\2\2\2\u0083\u01b3\3\2\2\2\u0085\u01b7\3\2\2\2\u0087\u01ba\3\2\2\2\u0089"+
		"\u01bd\3\2\2\2\u008b\u01bf\3\2\2\2\u008d\u01c4\3\2\2\2\u008f\u01c6\3\2"+
		"\2\2\u0091\u01cb\3\2\2\2\u0093\u01cf\3\2\2\2\u0095\u01d1\3\2\2\2\u0097"+
		"\u01d4\3\2\2\2\u0099\u01da\3\2\2\2\u009b\u01dc\3\2\2\2\u009d\u01df\3\2"+
		"\2\2\u009f\u01e3\3\2\2\2\u00a1\u01e5\3\2\2\2\u00a3\u01ee\3\2\2\2\u00a5"+
		"\u01f3\3\2\2\2\u00a7\u01f6\3\2\2\2\u00a9\u0200\3\2\2\2\u00ab\u0202\3\2"+
		"\2\2\u00ad\u0205\3\2\2\2\u00af\u020c\3\2\2\2\u00b1\u020f\3\2\2\2\u00b3"+
		"\u0214\3\2\2\2\u00b5\u021b\3\2\2\2\u00b7\u0222\3\2\2\2\u00b9\u022a\3\2"+
		"\2\2\u00bb\u0233\3\2\2\2\u00bd\u0239\3\2\2\2\u00bf\u0242\3\2\2\2\u00c1"+
		"\u00c2\7k\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4\7e\2\2\u00c4\u00c5\7n\2\2"+
		"\u00c5\u00c6\7w\2\2\u00c6\u00c7\7f\2\2\u00c7\u00c8\7g\2\2\u00c8\4\3\2"+
		"\2\2\u00c9\u00ca\7*\2\2\u00ca\6\3\2\2\2\u00cb\u00cc\7+\2\2\u00cc\b\3\2"+
		"\2\2\u00cd\u00ce\7\60\2\2\u00ce\n\3\2\2\2\u00cf\u00d0\7.\2\2\u00d0\f\3"+
		"\2\2\2\u00d1\u00d2\7]\2\2\u00d2\16\3\2\2\2\u00d3\u00d4\7_\2\2\u00d4\20"+
		"\3\2\2\2\u00d5\u00d6\7v\2\2\u00d6\u00d7\7j\2\2\u00d7\u00d8\7h\2\2\u00d8"+
		"\22\3\2\2\2\u00d9\u00da\7v\2\2\u00da\u00db\7h\2\2\u00db\u00dc\7h\2\2\u00dc"+
		"\24\3\2\2\2\u00dd\u00de\7h\2\2\u00de\u00df\7q\2\2\u00df\u00e0\7h\2\2\u00e0"+
		"\26\3\2\2\2\u00e1\u00e2\7e\2\2\u00e2\u00e3\7p\2\2\u00e3\u00e4\7h\2\2\u00e4"+
		"\30\3\2\2\2\u00e5\u00e6\7<\2\2\u00e6\32\3\2\2\2\u00e7\u00e8\7&\2\2\u00e8"+
		"\u00e9\7v\2\2\u00e9\u00ea\7j\2\2\u00ea\u00eb\7h\2\2\u00eb\u00ec\7*\2\2"+
		"\u00ec\34\3\2\2\2\u00ed\u00ee\7&\2\2\u00ee\u00ef\7v\2\2\u00ef\u00f0\7"+
		"h\2\2\u00f0\u00f1\7h\2\2\u00f1\u00f2\7*\2\2\u00f2\36\3\2\2\2\u00f3\u00f4"+
		"\7&\2\2\u00f4\u00f5\7h\2\2\u00f5\u00f6\7q\2\2\u00f6\u00f7\7h\2\2\u00f7"+
		"\u00f8\7*\2\2\u00f8 \3\2\2\2\u00f9\u00fa\7&\2\2\u00fa\u00fb\7h\2\2\u00fb"+
		"\u00fc\7q\2\2\u00fc\u00fd\7v\2\2\u00fd\u00fe\7*\2\2\u00fe\"\3\2\2\2\u00ff"+
		"\u0100\7&\2\2\u0100\u0101\7e\2\2\u0101\u0102\7p\2\2\u0102\u0103\7h\2\2"+
		"\u0103\u0104\7*\2\2\u0104$\3\2\2\2\u0105\u0106\7]\2\2\u0106\u0107\7_\2"+
		"\2\u0107&\3\2\2\2\u0108\u0109\7&\2\2\u0109\u010a\7k\2\2\u010a\u010b\7"+
		"v\2\2\u010b\u010c\7g\2\2\u010c\u010d\7*\2\2\u010d(\3\2\2\2\u010e\u010f"+
		"\7&\2\2\u010f\u0110\7n\2\2\u0110\u0111\7g\2\2\u0111\u0112\7v\2\2\u0112"+
		"\u0113\7*\2\2\u0113*\3\2\2\2\u0114\u0115\7>\2\2\u0115\u0116\7>\2\2\u0116"+
		",\3\2\2\2\u0117\u0118\7@\2\2\u0118.\3\2\2\2\u0119\u011a\7,\2\2\u011a\60"+
		"\3\2\2\2\u011b\u011c\7-\2\2\u011c\62\3\2\2\2\u011d\u011e\7/\2\2\u011e"+
		"\u011f\7/\2\2\u011f\u0120\7@\2\2\u0120\64\3\2\2\2\u0121\u0122\7}\2\2\u0122"+
		"\u0123\7\177\2\2\u0123\66\3\2\2\2\u0124\u0125\7}\2\2\u01258\3\2\2\2\u0126"+
		"\u0127\7\177\2\2\u0127:\3\2\2\2\u0128\u0129\7&\2\2\u0129\u012a\7k\2\2"+
		"\u012a\u012b\7v\2\2\u012b\u012c\7g\2\2\u012c\u012d\7a\2\2\u012d\u012e"+
		"\7h\2\2\u012e\u012f\7*\2\2\u012f<\3\2\2\2\u0130\u0131\7&\2\2\u0131\u0132"+
		"\7n\2\2\u0132\u0133\7g\2\2\u0133\u0134\7v\2\2\u0134\u0135\7a\2\2\u0135"+
		"\u0136\7v\2\2\u0136\u0137\7h\2\2\u0137\u0138\7*\2\2\u0138>\3\2\2\2\u0139"+
		"\u013a\7&\2\2\u013a\u013b\7n\2\2\u013b\u013c\7g\2\2\u013c\u013d\7v\2\2"+
		"\u013d\u013e\7a\2\2\u013e\u013f\7h\2\2\u013f\u0140\7h\2\2\u0140\u0141"+
		"\7*\2\2\u0141@\3\2\2\2\u0142\u0143\7@\2\2\u0143\u0144\7@\2\2\u0144B\3"+
		"\2\2\2\u0145\u0146\7&\2\2\u0146\u0147\7k\2\2\u0147\u0148\7v\2\2\u0148"+
		"\u0149\7g\2\2\u0149\u014a\7a\2\2\u014a\u014b\7v\2\2\u014b\u014c\7*\2\2"+
		"\u014cD\3\2\2\2\u014d\u014e\7&\2\2\u014e\u014f\7n\2\2\u014f\u0150\7g\2"+
		"\2\u0150\u0151\7v\2\2\u0151\u0152\7a\2\2\u0152\u0153\7h\2\2\u0153\u0154"+
		"\7v\2\2\u0154\u0155\7*\2\2\u0155F\3\2\2\2\u0156\u0157\7&\2\2\u0157\u0158"+
		"\7n\2\2\u0158\u0159\7g\2\2\u0159\u015a\7v\2\2\u015a\u015b\7a\2\2\u015b"+
		"\u015c\7v\2\2\u015c\u015d\7v\2\2\u015d\u015e\7*\2\2\u015eH\3\2\2\2\u015f"+
		"\u0163\t\2\2\2\u0160\u0161\7^\2\2\u0161\u0163\t\3\2\2\u0162\u015f\3\2"+
		"\2\2\u0162\u0160\3\2\2\2\u0163J\3\2\2\2\u0164\u0168\t\4\2\2\u0165\u0166"+
		"\7^\2\2\u0166\u0168\t\5\2\2\u0167\u0164\3\2\2\2\u0167\u0165\3\2\2\2\u0168"+
		"L\3\2\2\2\u0169\u016a\t\6\2\2\u016aN\3\2\2\2\u016b\u016c\t\7\2\2\u016c"+
		"P\3\2\2\2\u016d\u016e\t\b\2\2\u016eR\3\2\2\2\u016f\u0170\t\t\2\2\u0170"+
		"T\3\2\2\2\u0171\u0172\t\n\2\2\u0172V\3\2\2\2\u0173\u0174\t\13\2\2\u0174"+
		"X\3\2\2\2\u0175\u017a\5U+\2\u0176\u017a\5W,\2\u0177\u017a\5S*\2\u0178"+
		"\u017a\7a\2\2\u0179\u0175\3\2\2\2\u0179\u0176\3\2\2\2\u0179\u0177\3\2"+
		"\2\2\u0179\u0178\3\2\2\2\u017aZ\3\2\2\2\u017b\u017c\7~\2\2\u017c\\\3\2"+
		"\2\2\u017d\u017e\7(\2\2\u017e^\3\2\2\2\u017f\u0180\7>\2\2\u0180\u0181"+
		"\7?\2\2\u0181\u0182\7@\2\2\u0182`\3\2\2\2\u0183\u0184\7?\2\2\u0184\u0185"+
		"\7@\2\2\u0185b\3\2\2\2\u0186\u0187\7>\2\2\u0187\u0188\7?\2\2\u0188d\3"+
		"\2\2\2\u0189\u018a\7>\2\2\u018a\u018b\7\u0080\2\2\u018b\u018c\7@\2\2\u018c"+
		"f\3\2\2\2\u018d\u018e\7\u0080\2\2\u018e\u018f\7~\2\2\u018fh\3\2\2\2\u0190"+
		"\u0191\7\u0080\2\2\u0191\u0192\7(\2\2\u0192j\3\2\2\2\u0193\u0194\7\u0080"+
		"\2\2\u0194l\3\2\2\2\u0195\u0196\7#\2\2\u0196\u0197\7#\2\2\u0197n\3\2\2"+
		"\2\u0198\u0199\7#\2\2\u0199\u019a\7@\2\2\u019ap\3\2\2\2\u019b\u019c\7"+
		"#\2\2\u019c\u019d\7?\2\2\u019dr\3\2\2\2\u019e\u019f\7?\2\2\u019ft\3\2"+
		"\2\2\u01a0\u01a1\7#\2\2\u01a1v\3\2\2\2\u01a2\u01a3\7A\2\2\u01a3\u01a4"+
		"\7A\2\2\u01a4x\3\2\2\2\u01a5\u01a6\7A\2\2\u01a6\u01a7\7,\2\2\u01a7z\3"+
		"\2\2\2\u01a8\u01a9\7A\2\2\u01a9|\3\2\2\2\u01aa\u01ab\7`\2\2\u01ab~\3\2"+
		"\2\2\u01ac\u01ad\7B\2\2\u01ad\u01ae\7B\2\2\u01ae\u01af\7-\2\2\u01af\u0080"+
		"\3\2\2\2\u01b0\u01b1\7B\2\2\u01b1\u01b2\7-\2\2\u01b2\u0082\3\2\2\2\u01b3"+
		"\u01b4\7B\2\2\u01b4\u01b5\7B\2\2\u01b5\u01b6\7/\2\2\u01b6\u0084\3\2\2"+
		"\2\u01b7\u01b8\7B\2\2\u01b8\u01b9\7/\2\2\u01b9\u0086\3\2\2\2\u01ba\u01bb"+
		"\7B\2\2\u01bb\u01bc\7?\2\2\u01bc\u0088\3\2\2\2\u01bd\u01be\7B\2\2\u01be"+
		"\u008a\3\2\2\2\u01bf\u01c0\7<\2\2\u01c0\u01c1\7?\2\2\u01c1\u008c\3\2\2"+
		"\2\u01c2\u01c5\5\u008fH\2\u01c3\u01c5\5\u0091I\2\u01c4\u01c2\3\2\2\2\u01c4"+
		"\u01c3\3\2\2\2\u01c5\u008e\3\2\2\2\u01c6\u01c7\5M\'\2\u01c7\u01c8\5\u0091"+
		"I\2\u01c8\u0090\3\2\2\2\u01c9\u01cc\5\u00a5S\2\u01ca\u01cc\5\u00a3R\2"+
		"\u01cb\u01c9\3\2\2\2\u01cb\u01ca\3\2\2\2\u01cc\u0092\3\2\2\2\u01cd\u01d0"+
		"\5\u0095K\2\u01ce\u01d0\5\u0097L\2\u01cf\u01cd\3\2\2\2\u01cf\u01ce\3\2"+
		"\2\2\u01d0\u0094\3\2\2\2\u01d1\u01d2\5M\'\2\u01d2\u01d3\5\u0097L\2\u01d3"+
		"\u0096\3\2\2\2\u01d4\u01d5\5\u009fP\2\u01d5\u01d6\7\61\2\2\u01d6\u01d7"+
		"\5\u00a1Q\2\u01d7\u0098\3\2\2\2\u01d8\u01db\5\u009bN\2\u01d9\u01db\5\u009d"+
		"O\2\u01da\u01d8\3\2\2\2\u01da\u01d9\3\2\2\2\u01db\u009a\3\2\2\2\u01dc"+
		"\u01dd\5M\'\2\u01dd\u01de\5\u009dO\2\u01de\u009c\3\2\2\2\u01df\u01e0\5"+
		"\u009fP\2\u01e0\u009e\3\2\2\2\u01e1\u01e4\7\62\2\2\u01e2\u01e4\5\u00a1"+
		"Q\2\u01e3\u01e1\3\2\2\2\u01e3\u01e2\3\2\2\2\u01e4\u00a0\3\2\2\2\u01e5"+
		"\u01e9\5Q)\2\u01e6\u01e8\5S*\2\u01e7\u01e6\3\2\2\2\u01e8\u01eb\3\2\2\2"+
		"\u01e9\u01e7\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u00a2\3\2\2\2\u01eb\u01e9"+
		"\3\2\2\2\u01ec\u01ef\5\u009fP\2\u01ed\u01ef\5\u00a5S\2\u01ee\u01ec\3\2"+
		"\2\2\u01ee\u01ed\3\2\2\2\u01ef\u01f0\3\2\2\2\u01f0\u01f1\5O(\2\u01f1\u01f2"+
		"\5\u00a9U\2\u01f2\u00a4\3\2\2\2\u01f3\u01f4\5\u009fP\2\u01f4\u01f5\5\u00a7"+
		"T\2\u01f5\u00a6\3\2\2\2\u01f6\u01f7\7\60\2\2\u01f7\u01fb\5S*\2\u01f8\u01fa"+
		"\5S*\2\u01f9\u01f8\3\2\2\2\u01fa\u01fd\3\2\2\2\u01fb\u01f9\3\2\2\2\u01fb"+
		"\u01fc\3\2\2\2\u01fc\u00a8\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fe\u0201\5\u00ab"+
		"V\2\u01ff\u0201\5\u00adW\2\u0200\u01fe\3\2\2\2\u0200\u01ff\3\2\2\2\u0201"+
		"\u00aa\3\2\2\2\u0202\u0203\5M\'\2\u0203\u0204\5\u00adW\2\u0204\u00ac\3"+
		"\2\2\2\u0205\u0209\5S*\2\u0206\u0208\5S*\2\u0207\u0206\3\2\2\2\u0208\u020b"+
		"\3\2\2\2\u0209\u0207\3\2\2\2\u0209\u020a\3\2\2\2\u020a\u00ae\3\2\2\2\u020b"+
		"\u0209\3\2\2\2\u020c\u020d\7&\2\2\u020d\u020e\5\u00b5[\2\u020e\u00b0\3"+
		"\2\2\2\u020f\u0210\7&\2\2\u0210\u0211\7&\2\2\u0211\u0212\3\2\2\2\u0212"+
		"\u0213\5\u00b5[\2\u0213\u00b2\3\2\2\2\u0214\u0218\5W,\2\u0215\u0217\5"+
		"Y-\2\u0216\u0215\3\2\2\2\u0217\u021a\3\2\2\2\u0218\u0216\3\2\2\2\u0218"+
		"\u0219\3\2\2\2\u0219\u00b4\3\2\2\2\u021a\u0218\3\2\2\2\u021b\u021f\5U"+
		"+\2\u021c\u021e\5Y-\2\u021d\u021c\3\2\2\2\u021e\u0221\3\2\2\2\u021f\u021d"+
		"\3\2\2\2\u021f\u0220\3\2\2\2\u0220\u00b6\3\2\2\2\u0221\u021f\3\2\2\2\u0222"+
		"\u0224\7)\2\2\u0223\u0225\5K&\2\u0224\u0223\3\2\2\2\u0225\u0226\3\2\2"+
		"\2\u0226\u0224\3\2\2\2\u0226\u0227\3\2\2\2\u0227\u0228\3\2\2\2\u0228\u0229"+
		"\7)\2\2\u0229\u00b8\3\2\2\2\u022a\u022c\7$\2\2\u022b\u022d\5I%\2\u022c"+
		"\u022b\3\2\2\2\u022d\u022e\3\2\2\2\u022e\u022c\3\2\2\2\u022e\u022f\3\2"+
		"\2\2\u022f\u0230\3\2\2\2\u0230\u0231\7$\2\2\u0231\u00ba\3\2\2\2\u0232"+
		"\u0234\t\f\2\2\u0233\u0232\3\2\2\2\u0234\u0235\3\2\2\2\u0235\u0233\3\2"+
		"\2\2\u0235\u0236\3\2\2\2\u0236\u0237\3\2\2\2\u0237\u0238\b^\2\2\u0238"+
		"\u00bc\3\2\2\2\u0239\u023d\7\'\2\2\u023a\u023c\n\r\2\2\u023b\u023a\3\2"+
		"\2\2\u023c\u023f\3\2\2\2\u023d\u023b\3\2\2\2\u023d\u023e\3\2\2\2\u023e"+
		"\u0240\3\2\2\2\u023f\u023d\3\2\2\2\u0240\u0241\b_\2\2\u0241\u00be\3\2"+
		"\2\2\u0242\u0243\7\61\2\2\u0243\u0244\7,\2\2\u0244\u0248\3\2\2\2\u0245"+
		"\u0247\13\2\2\2\u0246\u0245\3\2\2\2\u0247\u024a\3\2\2\2\u0248\u0249\3"+
		"\2\2\2\u0248\u0246\3\2\2\2\u0249\u024b\3\2\2\2\u024a\u0248\3\2\2\2\u024b"+
		"\u024c\7,\2\2\u024c\u024d\7\61\2\2\u024d\u024e\3\2\2\2\u024e\u024f\b`"+
		"\2\2\u024f\u00c0\3\2\2\2\27\2\u0162\u0167\u0179\u01c4\u01cb\u01cf\u01da"+
		"\u01e3\u01e9\u01ee\u01fb\u0200\u0209\u0218\u021f\u0226\u022e\u0235\u023d"+
		"\u0248\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}