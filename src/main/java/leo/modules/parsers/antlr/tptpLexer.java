// Generated from /home/lex/dev/Leo-III/contrib/tptp.g4 by ANTLR 4.7

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
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

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

	public static final String[] ruleNames = {
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

	private static final String[] _LITERAL_NAMES = {
		null, "'include('", "').'", "','", "'['", "']'", "'thf('", "'tff('", "'fof('", 
		"'cnf('", "':'", "'('", "')'", "'$thf('", "'$tff('", "'$fof('", "'$fot('", 
		"'$cnf('", "'[]'", "'$ite('", "'$let('", "'<<'", "'>'", "'*'", "'+'", 
		"'-->'", "'{}'", "'{'", "'}'", "'$ite_f('", "'$let_tf('", "'$let_ff('", 
		"'>>'", "'$ite_t('", "'$let_ft('", "'$let_tt('", "'|'", "'&'", "'<=>'", 
		"'=>'", "'<='", "'<~>'", "'~|'", "'~&'", "'~'", "'!!'", "'!>'", "'!='", 
		"'='", "'!'", "'??'", "'?*'", "'?'", "'^'", "'@@+'", "'@+'", "'@@-'", 
		"'@-'", "'@='", "'@'", "':='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"Or", "And", "Iff", "Impl", "If", "Niff", "Nor", "Nand", "Not", "ForallComb", 
		"TyForall", "Infix_inequality", "Infix_equality", "Forall", "ExistsComb", 
		"TyExists", "Exists", "Lambda", "ChoiceComb", "Choice", "DescriptionComb", 
		"Description", "EqComb", "App", "Assignment", "Real", "Signed_real", "Unsigned_real", 
		"Rational", "Signed_rational", "Unsigned_rational", "Integer", "Signed_integer", 
		"Unsigned_integer", "Decimal", "Positive_decimal", "Decimal_exponent", 
		"Decimal_fraction", "Dot_decimal", "Exp_integer", "Signed_exp_integer", 
		"Unsigned_exp_integer", "Dollar_word", "Dollar_dollar_word", "Upper_word", 
		"Lower_word", "Single_quoted", "Distinct_object", "WS", "Line_comment", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2X\u0256\b\1\4\2\t"+
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
		"`\t`\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3"+
		"\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3"+
		"\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3"+
		"\34\3\34\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3"+
		"\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3 \3 \3!\3!\3!"+
		"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\3$\3$\3$\3"+
		"$\3$\3$\3$\3$\3$\3%\3%\3%\5%\u0169\n%\3&\3&\3&\5&\u016e\n&\3\'\3\'\3("+
		"\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3-\3-\5-\u0180\n-\3.\3.\3/\3/\3\60\3"+
		"\60\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\64\3"+
		"\64\3\64\3\65\3\65\3\65\3\66\3\66\3\67\3\67\3\67\38\38\38\39\39\39\3:"+
		"\3:\3;\3;\3<\3<\3<\3=\3=\3=\3>\3>\3?\3?\3@\3@\3@\3@\3A\3A\3A\3B\3B\3B"+
		"\3B\3C\3C\3C\3D\3D\3D\3E\3E\3F\3F\3F\3G\3G\5G\u01cb\nG\3H\3H\3H\3I\3I"+
		"\5I\u01d2\nI\3J\3J\5J\u01d6\nJ\3K\3K\3K\3L\3L\3L\3L\3M\3M\5M\u01e1\nM"+
		"\3N\3N\3N\3O\3O\3P\3P\5P\u01ea\nP\3Q\3Q\7Q\u01ee\nQ\fQ\16Q\u01f1\13Q\3"+
		"R\3R\5R\u01f5\nR\3R\3R\3R\3S\3S\3S\3T\3T\3T\7T\u0200\nT\fT\16T\u0203\13"+
		"T\3U\3U\5U\u0207\nU\3V\3V\3V\3W\3W\7W\u020e\nW\fW\16W\u0211\13W\3X\3X"+
		"\3X\3Y\3Y\3Y\3Y\3Y\3Z\3Z\7Z\u021d\nZ\fZ\16Z\u0220\13Z\3[\3[\7[\u0224\n"+
		"[\f[\16[\u0227\13[\3\\\3\\\6\\\u022b\n\\\r\\\16\\\u022c\3\\\3\\\3]\3]"+
		"\6]\u0233\n]\r]\16]\u0234\3]\3]\3^\6^\u023a\n^\r^\16^\u023b\3^\3^\3_\3"+
		"_\7_\u0242\n_\f_\16_\u0245\13_\3_\3_\3`\3`\3`\3`\7`\u024d\n`\f`\16`\u0250"+
		"\13`\3`\3`\3`\3`\3`\3\u024e\2a\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61"+
		"\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I\2K\2M\2O\2Q\2S\2U\2W\2Y\2"+
		"[&]\'_(a)c*e+g,i-k.m/o\60q\61s\62u\63w\64y\65{\66}\67\1778\u00819\u0083"+
		":\u0085;\u0087<\u0089=\u008b>\u008d?\u008f@\u0091A\u0093B\u0095C\u0097"+
		"D\u0099E\u009bF\u009dG\u009fH\u00a1I\u00a3J\u00a5K\u00a7L\u00a9M\u00ab"+
		"N\u00adO\u00afP\u00b1Q\u00b3R\u00b5S\u00b7T\u00b9U\u00bbV\u00bdW\u00bf"+
		"X\3\2\16\5\2\"#%]_\u0080\4\2$$^^\5\2\"(*]_\u0080\4\2))^^\4\2--//\4\2G"+
		"Ggg\3\2\63;\3\2\62;\3\2c|\3\2C\\\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2\u0262"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
		"\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
		"\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2"+
		"g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3"+
		"\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3"+
		"\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2"+
		"\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091"+
		"\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2"+
		"\2\2\u009b\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3"+
		"\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2"+
		"\2\2\u00ad\3\2\2\2\2\u00af\3\2\2\2\2\u00b1\3\2\2\2\2\u00b3\3\2\2\2\2\u00b5"+
		"\3\2\2\2\2\u00b7\3\2\2\2\2\u00b9\3\2\2\2\2\u00bb\3\2\2\2\2\u00bd\3\2\2"+
		"\2\2\u00bf\3\2\2\2\3\u00c1\3\2\2\2\5\u00ca\3\2\2\2\7\u00cd\3\2\2\2\t\u00cf"+
		"\3\2\2\2\13\u00d1\3\2\2\2\r\u00d3\3\2\2\2\17\u00d8\3\2\2\2\21\u00dd\3"+
		"\2\2\2\23\u00e2\3\2\2\2\25\u00e7\3\2\2\2\27\u00e9\3\2\2\2\31\u00eb\3\2"+
		"\2\2\33\u00ed\3\2\2\2\35\u00f3\3\2\2\2\37\u00f9\3\2\2\2!\u00ff\3\2\2\2"+
		"#\u0105\3\2\2\2%\u010b\3\2\2\2\'\u010e\3\2\2\2)\u0114\3\2\2\2+\u011a\3"+
		"\2\2\2-\u011d\3\2\2\2/\u011f\3\2\2\2\61\u0121\3\2\2\2\63\u0123\3\2\2\2"+
		"\65\u0127\3\2\2\2\67\u012a\3\2\2\29\u012c\3\2\2\2;\u012e\3\2\2\2=\u0136"+
		"\3\2\2\2?\u013f\3\2\2\2A\u0148\3\2\2\2C\u014b\3\2\2\2E\u0153\3\2\2\2G"+
		"\u015c\3\2\2\2I\u0168\3\2\2\2K\u016d\3\2\2\2M\u016f\3\2\2\2O\u0171\3\2"+
		"\2\2Q\u0173\3\2\2\2S\u0175\3\2\2\2U\u0177\3\2\2\2W\u0179\3\2\2\2Y\u017f"+
		"\3\2\2\2[\u0181\3\2\2\2]\u0183\3\2\2\2_\u0185\3\2\2\2a\u0189\3\2\2\2c"+
		"\u018c\3\2\2\2e\u018f\3\2\2\2g\u0193\3\2\2\2i\u0196\3\2\2\2k\u0199\3\2"+
		"\2\2m\u019b\3\2\2\2o\u019e\3\2\2\2q\u01a1\3\2\2\2s\u01a4\3\2\2\2u\u01a6"+
		"\3\2\2\2w\u01a8\3\2\2\2y\u01ab\3\2\2\2{\u01ae\3\2\2\2}\u01b0\3\2\2\2\177"+
		"\u01b2\3\2\2\2\u0081\u01b6\3\2\2\2\u0083\u01b9\3\2\2\2\u0085\u01bd\3\2"+
		"\2\2\u0087\u01c0\3\2\2\2\u0089\u01c3\3\2\2\2\u008b\u01c5\3\2\2\2\u008d"+
		"\u01ca\3\2\2\2\u008f\u01cc\3\2\2\2\u0091\u01d1\3\2\2\2\u0093\u01d5\3\2"+
		"\2\2\u0095\u01d7\3\2\2\2\u0097\u01da\3\2\2\2\u0099\u01e0\3\2\2\2\u009b"+
		"\u01e2\3\2\2\2\u009d\u01e5\3\2\2\2\u009f\u01e9\3\2\2\2\u00a1\u01eb\3\2"+
		"\2\2\u00a3\u01f4\3\2\2\2\u00a5\u01f9\3\2\2\2\u00a7\u01fc\3\2\2\2\u00a9"+
		"\u0206\3\2\2\2\u00ab\u0208\3\2\2\2\u00ad\u020b\3\2\2\2\u00af\u0212\3\2"+
		"\2\2\u00b1\u0215\3\2\2\2\u00b3\u021a\3\2\2\2\u00b5\u0221\3\2\2\2\u00b7"+
		"\u0228\3\2\2\2\u00b9\u0230\3\2\2\2\u00bb\u0239\3\2\2\2\u00bd\u023f\3\2"+
		"\2\2\u00bf\u0248\3\2\2\2\u00c1\u00c2\7k\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4"+
		"\7e\2\2\u00c4\u00c5\7n\2\2\u00c5\u00c6\7w\2\2\u00c6\u00c7\7f\2\2\u00c7"+
		"\u00c8\7g\2\2\u00c8\u00c9\7*\2\2\u00c9\4\3\2\2\2\u00ca\u00cb\7+\2\2\u00cb"+
		"\u00cc\7\60\2\2\u00cc\6\3\2\2\2\u00cd\u00ce\7.\2\2\u00ce\b\3\2\2\2\u00cf"+
		"\u00d0\7]\2\2\u00d0\n\3\2\2\2\u00d1\u00d2\7_\2\2\u00d2\f\3\2\2\2\u00d3"+
		"\u00d4\7v\2\2\u00d4\u00d5\7j\2\2\u00d5\u00d6\7h\2\2\u00d6\u00d7\7*\2\2"+
		"\u00d7\16\3\2\2\2\u00d8\u00d9\7v\2\2\u00d9\u00da\7h\2\2\u00da\u00db\7"+
		"h\2\2\u00db\u00dc\7*\2\2\u00dc\20\3\2\2\2\u00dd\u00de\7h\2\2\u00de\u00df"+
		"\7q\2\2\u00df\u00e0\7h\2\2\u00e0\u00e1\7*\2\2\u00e1\22\3\2\2\2\u00e2\u00e3"+
		"\7e\2\2\u00e3\u00e4\7p\2\2\u00e4\u00e5\7h\2\2\u00e5\u00e6\7*\2\2\u00e6"+
		"\24\3\2\2\2\u00e7\u00e8\7<\2\2\u00e8\26\3\2\2\2\u00e9\u00ea\7*\2\2\u00ea"+
		"\30\3\2\2\2\u00eb\u00ec\7+\2\2\u00ec\32\3\2\2\2\u00ed\u00ee\7&\2\2\u00ee"+
		"\u00ef\7v\2\2\u00ef\u00f0\7j\2\2\u00f0\u00f1\7h\2\2\u00f1\u00f2\7*\2\2"+
		"\u00f2\34\3\2\2\2\u00f3\u00f4\7&\2\2\u00f4\u00f5\7v\2\2\u00f5\u00f6\7"+
		"h\2\2\u00f6\u00f7\7h\2\2\u00f7\u00f8\7*\2\2\u00f8\36\3\2\2\2\u00f9\u00fa"+
		"\7&\2\2\u00fa\u00fb\7h\2\2\u00fb\u00fc\7q\2\2\u00fc\u00fd\7h\2\2\u00fd"+
		"\u00fe\7*\2\2\u00fe \3\2\2\2\u00ff\u0100\7&\2\2\u0100\u0101\7h\2\2\u0101"+
		"\u0102\7q\2\2\u0102\u0103\7v\2\2\u0103\u0104\7*\2\2\u0104\"\3\2\2\2\u0105"+
		"\u0106\7&\2\2\u0106\u0107\7e\2\2\u0107\u0108\7p\2\2\u0108\u0109\7h\2\2"+
		"\u0109\u010a\7*\2\2\u010a$\3\2\2\2\u010b\u010c\7]\2\2\u010c\u010d\7_\2"+
		"\2\u010d&\3\2\2\2\u010e\u010f\7&\2\2\u010f\u0110\7k\2\2\u0110\u0111\7"+
		"v\2\2\u0111\u0112\7g\2\2\u0112\u0113\7*\2\2\u0113(\3\2\2\2\u0114\u0115"+
		"\7&\2\2\u0115\u0116\7n\2\2\u0116\u0117\7g\2\2\u0117\u0118\7v\2\2\u0118"+
		"\u0119\7*\2\2\u0119*\3\2\2\2\u011a\u011b\7>\2\2\u011b\u011c\7>\2\2\u011c"+
		",\3\2\2\2\u011d\u011e\7@\2\2\u011e.\3\2\2\2\u011f\u0120\7,\2\2\u0120\60"+
		"\3\2\2\2\u0121\u0122\7-\2\2\u0122\62\3\2\2\2\u0123\u0124\7/\2\2\u0124"+
		"\u0125\7/\2\2\u0125\u0126\7@\2\2\u0126\64\3\2\2\2\u0127\u0128\7}\2\2\u0128"+
		"\u0129\7\177\2\2\u0129\66\3\2\2\2\u012a\u012b\7}\2\2\u012b8\3\2\2\2\u012c"+
		"\u012d\7\177\2\2\u012d:\3\2\2\2\u012e\u012f\7&\2\2\u012f\u0130\7k\2\2"+
		"\u0130\u0131\7v\2\2\u0131\u0132\7g\2\2\u0132\u0133\7a\2\2\u0133\u0134"+
		"\7h\2\2\u0134\u0135\7*\2\2\u0135<\3\2\2\2\u0136\u0137\7&\2\2\u0137\u0138"+
		"\7n\2\2\u0138\u0139\7g\2\2\u0139\u013a\7v\2\2\u013a\u013b\7a\2\2\u013b"+
		"\u013c\7v\2\2\u013c\u013d\7h\2\2\u013d\u013e\7*\2\2\u013e>\3\2\2\2\u013f"+
		"\u0140\7&\2\2\u0140\u0141\7n\2\2\u0141\u0142\7g\2\2\u0142\u0143\7v\2\2"+
		"\u0143\u0144\7a\2\2\u0144\u0145\7h\2\2\u0145\u0146\7h\2\2\u0146\u0147"+
		"\7*\2\2\u0147@\3\2\2\2\u0148\u0149\7@\2\2\u0149\u014a\7@\2\2\u014aB\3"+
		"\2\2\2\u014b\u014c\7&\2\2\u014c\u014d\7k\2\2\u014d\u014e\7v\2\2\u014e"+
		"\u014f\7g\2\2\u014f\u0150\7a\2\2\u0150\u0151\7v\2\2\u0151\u0152\7*\2\2"+
		"\u0152D\3\2\2\2\u0153\u0154\7&\2\2\u0154\u0155\7n\2\2\u0155\u0156\7g\2"+
		"\2\u0156\u0157\7v\2\2\u0157\u0158\7a\2\2\u0158\u0159\7h\2\2\u0159\u015a"+
		"\7v\2\2\u015a\u015b\7*\2\2\u015bF\3\2\2\2\u015c\u015d\7&\2\2\u015d\u015e"+
		"\7n\2\2\u015e\u015f\7g\2\2\u015f\u0160\7v\2\2\u0160\u0161\7a\2\2\u0161"+
		"\u0162\7v\2\2\u0162\u0163\7v\2\2\u0163\u0164\7*\2\2\u0164H\3\2\2\2\u0165"+
		"\u0169\t\2\2\2\u0166\u0167\7^\2\2\u0167\u0169\t\3\2\2\u0168\u0165\3\2"+
		"\2\2\u0168\u0166\3\2\2\2\u0169J\3\2\2\2\u016a\u016e\t\4\2\2\u016b\u016c"+
		"\7^\2\2\u016c\u016e\t\5\2\2\u016d\u016a\3\2\2\2\u016d\u016b\3\2\2\2\u016e"+
		"L\3\2\2\2\u016f\u0170\t\6\2\2\u0170N\3\2\2\2\u0171\u0172\t\7\2\2\u0172"+
		"P\3\2\2\2\u0173\u0174\t\b\2\2\u0174R\3\2\2\2\u0175\u0176\t\t\2\2\u0176"+
		"T\3\2\2\2\u0177\u0178\t\n\2\2\u0178V\3\2\2\2\u0179\u017a\t\13\2\2\u017a"+
		"X\3\2\2\2\u017b\u0180\5U+\2\u017c\u0180\5W,\2\u017d\u0180\5S*\2\u017e"+
		"\u0180\7a\2\2\u017f\u017b\3\2\2\2\u017f\u017c\3\2\2\2\u017f\u017d\3\2"+
		"\2\2\u017f\u017e\3\2\2\2\u0180Z\3\2\2\2\u0181\u0182\7~\2\2\u0182\\\3\2"+
		"\2\2\u0183\u0184\7(\2\2\u0184^\3\2\2\2\u0185\u0186\7>\2\2\u0186\u0187"+
		"\7?\2\2\u0187\u0188\7@\2\2\u0188`\3\2\2\2\u0189\u018a\7?\2\2\u018a\u018b"+
		"\7@\2\2\u018bb\3\2\2\2\u018c\u018d\7>\2\2\u018d\u018e\7?\2\2\u018ed\3"+
		"\2\2\2\u018f\u0190\7>\2\2\u0190\u0191\7\u0080\2\2\u0191\u0192\7@\2\2\u0192"+
		"f\3\2\2\2\u0193\u0194\7\u0080\2\2\u0194\u0195\7~\2\2\u0195h\3\2\2\2\u0196"+
		"\u0197\7\u0080\2\2\u0197\u0198\7(\2\2\u0198j\3\2\2\2\u0199\u019a\7\u0080"+
		"\2\2\u019al\3\2\2\2\u019b\u019c\7#\2\2\u019c\u019d\7#\2\2\u019dn\3\2\2"+
		"\2\u019e\u019f\7#\2\2\u019f\u01a0\7@\2\2\u01a0p\3\2\2\2\u01a1\u01a2\7"+
		"#\2\2\u01a2\u01a3\7?\2\2\u01a3r\3\2\2\2\u01a4\u01a5\7?\2\2\u01a5t\3\2"+
		"\2\2\u01a6\u01a7\7#\2\2\u01a7v\3\2\2\2\u01a8\u01a9\7A\2\2\u01a9\u01aa"+
		"\7A\2\2\u01aax\3\2\2\2\u01ab\u01ac\7A\2\2\u01ac\u01ad\7,\2\2\u01adz\3"+
		"\2\2\2\u01ae\u01af\7A\2\2\u01af|\3\2\2\2\u01b0\u01b1\7`\2\2\u01b1~\3\2"+
		"\2\2\u01b2\u01b3\7B\2\2\u01b3\u01b4\7B\2\2\u01b4\u01b5\7-\2\2\u01b5\u0080"+
		"\3\2\2\2\u01b6\u01b7\7B\2\2\u01b7\u01b8\7-\2\2\u01b8\u0082\3\2\2\2\u01b9"+
		"\u01ba\7B\2\2\u01ba\u01bb\7B\2\2\u01bb\u01bc\7/\2\2\u01bc\u0084\3\2\2"+
		"\2\u01bd\u01be\7B\2\2\u01be\u01bf\7/\2\2\u01bf\u0086\3\2\2\2\u01c0\u01c1"+
		"\7B\2\2\u01c1\u01c2\7?\2\2\u01c2\u0088\3\2\2\2\u01c3\u01c4\7B\2\2\u01c4"+
		"\u008a\3\2\2\2\u01c5\u01c6\7<\2\2\u01c6\u01c7\7?\2\2\u01c7\u008c\3\2\2"+
		"\2\u01c8\u01cb\5\u008fH\2\u01c9\u01cb\5\u0091I\2\u01ca\u01c8\3\2\2\2\u01ca"+
		"\u01c9\3\2\2\2\u01cb\u008e\3\2\2\2\u01cc\u01cd\5M\'\2\u01cd\u01ce\5\u0091"+
		"I\2\u01ce\u0090\3\2\2\2\u01cf\u01d2\5\u00a5S\2\u01d0\u01d2\5\u00a3R\2"+
		"\u01d1\u01cf\3\2\2\2\u01d1\u01d0\3\2\2\2\u01d2\u0092\3\2\2\2\u01d3\u01d6"+
		"\5\u0095K\2\u01d4\u01d6\5\u0097L\2\u01d5\u01d3\3\2\2\2\u01d5\u01d4\3\2"+
		"\2\2\u01d6\u0094\3\2\2\2\u01d7\u01d8\5M\'\2\u01d8\u01d9\5\u0097L\2\u01d9"+
		"\u0096\3\2\2\2\u01da\u01db\5\u009fP\2\u01db\u01dc\7\61\2\2\u01dc\u01dd"+
		"\5\u00a1Q\2\u01dd\u0098\3\2\2\2\u01de\u01e1\5\u009bN\2\u01df\u01e1\5\u009d"+
		"O\2\u01e0\u01de\3\2\2\2\u01e0\u01df\3\2\2\2\u01e1\u009a\3\2\2\2\u01e2"+
		"\u01e3\5M\'\2\u01e3\u01e4\5\u009dO\2\u01e4\u009c\3\2\2\2\u01e5\u01e6\5"+
		"\u009fP\2\u01e6\u009e\3\2\2\2\u01e7\u01ea\7\62\2\2\u01e8\u01ea\5\u00a1"+
		"Q\2\u01e9\u01e7\3\2\2\2\u01e9\u01e8\3\2\2\2\u01ea\u00a0\3\2\2\2\u01eb"+
		"\u01ef\5Q)\2\u01ec\u01ee\5S*\2\u01ed\u01ec\3\2\2\2\u01ee\u01f1\3\2\2\2"+
		"\u01ef\u01ed\3\2\2\2\u01ef\u01f0\3\2\2\2\u01f0\u00a2\3\2\2\2\u01f1\u01ef"+
		"\3\2\2\2\u01f2\u01f5\5\u009fP\2\u01f3\u01f5\5\u00a5S\2\u01f4\u01f2\3\2"+
		"\2\2\u01f4\u01f3\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f7\5O(\2\u01f7\u01f8"+
		"\5\u00a9U\2\u01f8\u00a4\3\2\2\2\u01f9\u01fa\5\u009fP\2\u01fa\u01fb\5\u00a7"+
		"T\2\u01fb\u00a6\3\2\2\2\u01fc\u01fd\7\60\2\2\u01fd\u0201\5S*\2\u01fe\u0200"+
		"\5S*\2\u01ff\u01fe\3\2\2\2\u0200\u0203\3\2\2\2\u0201\u01ff\3\2\2\2\u0201"+
		"\u0202\3\2\2\2\u0202\u00a8\3\2\2\2\u0203\u0201\3\2\2\2\u0204\u0207\5\u00ab"+
		"V\2\u0205\u0207\5\u00adW\2\u0206\u0204\3\2\2\2\u0206\u0205\3\2\2\2\u0207"+
		"\u00aa\3\2\2\2\u0208\u0209\5M\'\2\u0209\u020a\5\u00adW\2\u020a\u00ac\3"+
		"\2\2\2\u020b\u020f\5S*\2\u020c\u020e\5S*\2\u020d\u020c\3\2\2\2\u020e\u0211"+
		"\3\2\2\2\u020f\u020d\3\2\2\2\u020f\u0210\3\2\2\2\u0210\u00ae\3\2\2\2\u0211"+
		"\u020f\3\2\2\2\u0212\u0213\7&\2\2\u0213\u0214\5\u00b5[\2\u0214\u00b0\3"+
		"\2\2\2\u0215\u0216\7&\2\2\u0216\u0217\7&\2\2\u0217\u0218\3\2\2\2\u0218"+
		"\u0219\5\u00b5[\2\u0219\u00b2\3\2\2\2\u021a\u021e\5W,\2\u021b\u021d\5"+
		"Y-\2\u021c\u021b\3\2\2\2\u021d\u0220\3\2\2\2\u021e\u021c\3\2\2\2\u021e"+
		"\u021f\3\2\2\2\u021f\u00b4\3\2\2\2\u0220\u021e\3\2\2\2\u0221\u0225\5U"+
		"+\2\u0222\u0224\5Y-\2\u0223\u0222\3\2\2\2\u0224\u0227\3\2\2\2\u0225\u0223"+
		"\3\2\2\2\u0225\u0226\3\2\2\2\u0226\u00b6\3\2\2\2\u0227\u0225\3\2\2\2\u0228"+
		"\u022a\7)\2\2\u0229\u022b\5K&\2\u022a\u0229\3\2\2\2\u022b\u022c\3\2\2"+
		"\2\u022c\u022a\3\2\2\2\u022c\u022d\3\2\2\2\u022d\u022e\3\2\2\2\u022e\u022f"+
		"\7)\2\2\u022f\u00b8\3\2\2\2\u0230\u0232\7$\2\2\u0231\u0233\5I%\2\u0232"+
		"\u0231\3\2\2\2\u0233\u0234\3\2\2\2\u0234\u0232\3\2\2\2\u0234\u0235\3\2"+
		"\2\2\u0235\u0236\3\2\2\2\u0236\u0237\7$\2\2\u0237\u00ba\3\2\2\2\u0238"+
		"\u023a\t\f\2\2\u0239\u0238\3\2\2\2\u023a\u023b\3\2\2\2\u023b\u0239\3\2"+
		"\2\2\u023b\u023c\3\2\2\2\u023c\u023d\3\2\2\2\u023d\u023e\b^\2\2\u023e"+
		"\u00bc\3\2\2\2\u023f\u0243\7\'\2\2\u0240\u0242\n\r\2\2\u0241\u0240\3\2"+
		"\2\2\u0242\u0245\3\2\2\2\u0243\u0241\3\2\2\2\u0243\u0244\3\2\2\2\u0244"+
		"\u0246\3\2\2\2\u0245\u0243\3\2\2\2\u0246\u0247\b_\2\2\u0247\u00be\3\2"+
		"\2\2\u0248\u0249\7\61\2\2\u0249\u024a\7,\2\2\u024a\u024e\3\2\2\2\u024b"+
		"\u024d\13\2\2\2\u024c\u024b\3\2\2\2\u024d\u0250\3\2\2\2\u024e\u024f\3"+
		"\2\2\2\u024e\u024c\3\2\2\2\u024f\u0251\3\2\2\2\u0250\u024e\3\2\2\2\u0251"+
		"\u0252\7,\2\2\u0252\u0253\7\61\2\2\u0253\u0254\3\2\2\2\u0254\u0255\b`"+
		"\2\2\u0255\u00c0\3\2\2\2\27\2\u0168\u016d\u017f\u01ca\u01d1\u01d5\u01e0"+
		"\u01e9\u01ef\u01f4\u0201\u0206\u020f\u021e\u0225\u022c\u0234\u023b\u0243"+
		"\u024e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}