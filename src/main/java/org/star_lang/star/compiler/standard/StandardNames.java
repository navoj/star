package org.star_lang.star.compiler.standard;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.transform.Computations;
import org.star_lang.star.data.type.AbstractType;
import org.star_lang.star.data.value.BoolWrap.FalseValue;
import org.star_lang.star.data.value.BoolWrap.TrueValue;
import org.star_lang.star.data.value.Option.Some;
import org.star_lang.star.operators.ast.runtime.AstMacroKey;

/*
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
public class StandardNames
{
  public static final Set<String> keywords = new TreeSet<>();
  public static final Set<String> graphic = new TreeSet<>();
  public static final Set<String> standard = new TreeSet<>();

  public static final String TERM = graphic(";");
  public static final String TYPE = keyword(AbstractType.TYPE);
  public static final String KIND = keyword(AbstractType.KIND);
  public static final String HAS_KIND = keyword(AbstractType.HAS_KIND);
  public static final String ALIAS = keyword(AbstractType.ALIAS);
  public static final String COUNTS_AS = keyword("counts as");

  public static final String CONTRACT = keyword(AbstractType.CONTRACT);
  public static final String IMPLEMENTATION = keyword(AbstractType.IMPLEMENTATION);
  public static final String IMPLEMENTS = keyword(AbstractType.IMPLEMENTS);
  public static final String TUPLE = keyword(AbstractType.TUPLE);
  public static final String IS_TUPLE = keyword(AbstractType.IS_TUPLE);
  public static final String INSTANCE_OF = keyword(AbstractType.INSTANCE_OF);
  public static final String OVER = keyword(AbstractType.OVER);
  public static final String DETERMINES = keyword(AbstractType.DETERMINES);

  public static final String PRIVATE = keyword("private");

  public static final String ASSERT = keyword("assert");
  public static final String IGNORE = keyword("ignore");
  public static final String YIELD = keyword("yield");
  public static final String EQUALITY = standard("equality");
  public static final String COMPARABLE = standard("comparable");
  public static final String ARITHMETIC = standard("arithmetic");
  public static final String EQUAL = graphic("=");
  public static final String NOT_EQUAL = graphic("!=");
  public static final String LESS = graphic("<");
  public static final String LESS_EQUAL = graphic("<=");
  public static final String GREATER = graphic(">");
  public static final String GREATER_EQUAL = graphic(">=");

  public static final String AND = keyword("and");
  public static final String OR = keyword("or");
  public static final String NOT = keyword("not");
  public static final String IMPLIES = keyword("implies");

  public static final String WHEN = keyword("when");
  public static final String FATBAR = graphic("|_|");

  public static final String NUMBER = standard("number");

  public static final String TRUE = standard(TrueValue.name);
  public static final String FALSE = standard(FalseValue.name);

  public static final String HASH = standard("hash");
  public static final String DICTIONARY = standard("dictionary");
  public static final String LIST = standard("list");
  public static final String CONS = graphic(";..");
  public static final String ENDCONS = graphic("..;");
  public static final String ANY = keyword("any");
  public static final String VOID = standard("void");

  public static final String COMPUTATION = keyword(Computations.COMPUTATION);

  public static final String ITERABLE = "iterable";
  public static final String ITERATE = "_iterate";
  public static final String IXITERABLE = "indexed_iterable";
  public static final String IXITERATE = "_ixiterate";
  public static final String ITERSTATE = "IterState";
  public static final String CONTINUEWITH = "ContinueWith";
  public static final String NOMORE = "NoMore";
  public static final String NONEFOUND = "NoneFound";
  public static final String ABORT_ITER = "AbortIter";
  public static final String CHECKITERFUNC = "_checkIterState";
  public static final String NEGATE = "_negate";
  public static final String OTHER = "_otherwise";
  public static final String POSSIBLE = "_possible";
  public static final String IMPOSSIBLE = "_impossible";
  public static final String LEFTFOLD1 = "leftFold1";

  public static final String SOME = Some.label;
  public static final String SOMEVALUE = standard("someValue");

  public static final String MAIN = "main";
  public static final String UMAIN = "_main";

  public static final String ACTION_TYPE = standard(AbstractType.ACTION);
  public static final String PROC_LAMBDA = keyword("procedure");
  public static final String FUN_ARROW = graphic(AbstractType.FUN_TYPE);
  public static final String FUNCTION = keyword("function");
  public static final String MEMO = keyword("memo");
  public static final String LAMBDA = keyword("fn");
  public static final String UNI_TILDA = graphic("~");
  public static final String PATTERN = keyword("pattern");
  public static final String PTN_TYPE = graphic(AbstractType.PTN_TYPE);
  public static final String OVERLOADED_TYPE = graphic(AbstractType.OVERLOADED_TYPE);
  public static final String CONSTRUCTOR_TYPE = graphic(AbstractType.CONSTRUCTOR_TYPE);

  public static final String FORALL = keyword(AbstractType.FORALL);
  public static final String FOR_ALL = keyword(AbstractType.FOR_ALL);
  public static final String ST = keyword(AbstractType.ST);
  public static final String S_T = keyword(AbstractType.S_T);
  public static final String EXISTS = keyword(AbstractType.EXISTS);

  public static final String EXTEND = keyword("extend");
  public static final String MERGE = keyword("merge");
  public static final String UPDATE = keyword("update");
  public static final String DELETE = keyword("delete");

  public static final String ANYOF = keyword("anyof");
  public static final String ANY_OF = keyword("any of");
  public static final String ALL = keyword("all");
  public static final String UNIQUE = keyword("unique");
  public static final String UNIQUE_F = standard("__unique");
  public static final String SORT = standard("sort");
  public static final String ORDERBY = keyword("order by");
  public static final String ORDERDESCENDINBY = keyword("order descending by");
  public static final String DESCENDINGBY = keyword("descending by");

  public static final String GROUPBY = standard("group by");
  public static final String PROJECT_0 = "__project0";
  public static final String REDUCTION = keyword("reduction");

  public static final String VALIS = keyword("valis");
  public static final String VALOF = keyword("valof");
  public static final String PERFORM = keyword("perform");
  public static final String ON_ABORT = keyword("on abort");

  public static final String OF = keyword("of");
  public static final String SUBSTITUTE = keyword("substitute");
  public static final String DOWN = keyword("down");

  public static final String REF = keyword("ref");
  public static final String VAR = keyword("var");
  public static final String FUN = keyword("fun");

  public static final String SHRIEK = graphic("!");

  public static final String QUOTE = keyword("quote");
  public static final String UNQUOTE = keyword("unquote");
  public static final String LQUOTE = graphic("<|");
  public static final String RQUOTE = graphic("|>");

  public static final String QTNAME = standard("_qtName");
  public static final String QTINTEGER = standard("_qtInteger");
  public static final String QTLONG = standard("_qtLong");
  public static final String QTFLOAT = standard("_qtFLoat");
  public static final String QTDECIMAL = standard("_qtDecimal");
  public static final String QTCHAR = standard("_qtChar");
  public static final String QTSTRING = standard("_qtString");
  public static final String QTTUPLE = standard("_qtTuple");
  public static final String QTAPPLY = standard("_qtApply");

  public static final String QTNAME_P = standard("_qtNameP");
  public static final String QTINTEGER_P = standard("_qtIntegerP");
  public static final String QTLONG_P = standard("_qtLongP");
  public static final String QTFLOAT_P = standard("_qtFLoatP");
  public static final String QTDECIMAL_P = standard("_qtDecimalP");
  public static final String QTCHAR_P = standard("_qtCharP");
  public static final String QTSTRING_P = standard("_qtStringP");
  public static final String QTTUPLE_P = standard("_qtTupleP");
  public static final String QTAPPLY_P = standard("_qtApplyP");

  public static final String SPAWN = keyword("spawn");
  public static final String WAITFOR = keyword("waitfor");
  public static final String THREAD = standard("thread");
  public static final String SYNC = keyword("sync");
  public static final String NON_THREAD = standard("noThread");
  public static final String PARALLEL = graphic("//");

  public static final String IS = keyword("is");
  public static final String DO = keyword("do");

  public static final String ACTION = "action";
  public static final String TO = keyword("to");
  public static final String DEFAULT = keyword("default");
  public static final String HAS = keyword("has");
  public static final String HAS_TYPE = keyword("has type");
  public static final String HASTYPE = keyword("hastype");
  public static final String WHERE = keyword("where");
  public static final String IN = keyword("in");
  public static final String CASE = keyword("case");
  public static final String SWITCH = keyword("switch");

  public static final String CAST = keyword("cast");
  public static final String COERCE = standard("coerce");
  public static final String COERCION = standard("coercion");
  public static final String AS = keyword("as");

  public static final String IF = keyword("if");
  public static final String THEN = keyword("then");
  public static final String ELSE = keyword("else");
  public static final String OTHERWISE = keyword("otherwise");

  public static final String FOR = keyword("for");
  public static final String WHILE = keyword("while");
  public static final String NOTHING = keyword("nothing");

  public static final String ON = keyword("on");

  // speech actions
  public static final String NOTIFY = keyword("notify");
  public static final String QUERY = keyword("query");
  public static final String WITH = keyword("with");
  public static final String REQUEST = keyword("request");

  // map names
  public static final String MAP_ARROW = graphic("->");

  public static final String REMOVE = keyword("remove");
  public static final String WITHOUT = keyword("without");

  // error handling
  public static final String RAISE = keyword("raise");
  public static final String RAISE_FUN = standard("raiser_fun");
  public static final String TRY = keyword("try");
  public static final String CATCH = keyword("catch");
  public static final String EXCEPTION = standard("__exception");

  public static final String ASSIGN = graphic(":=");

  public static final String DBLCENT = graphic("%%");

  public static final String PIPE = graphic("|");

  public static final String PLUS = graphic("+");
  public static final String MINUS = graphic("-");
  public static final String TIMES = graphic("*");
  public static final String DIVIDE = graphic("/");
  public static final String PCENT = graphic("%");
  public static final String POWER = graphic("**");
  public static final String MATCHING = keyword("matching");
  public static final String MATCHES = keyword("matches");
  public static final String BOUND_TO = keyword("bound to");
  public static final String FROM = keyword("from");

  public static final String IMPORT = keyword("import");
  public static final String OPEN = keyword("open");
  public static final String JAVA = keyword("java");

  public static final String COMMA = graphic(",");
  public static final String SCONS = graphic(",..");
  public static final String ENDSCONS = graphic("..,");
  public static final String PARENS = "()";
  public static final String PARENS_LABEL = "$1";
  public static final String TUPLE_LABEL = "$";
  public static final String RECORD_LABEL = "__face_";
  public static final String AGGREGATE = "{}";
  public static final String SQUARE = "[]";
  public static final String BRACES = "{}";

  public static final String PERIOD = graphic(".");

  public static final String COLON = graphic(":");

  public static final String INDEX = standard("_index");
  public static final String SLICE = standard("_slice");

  public static final String APPLY = graphic("@");

  public static final String QUESTION = graphic("?");
  public static final String TYPEVAR = PCENT;
  public static final String ANONYMOUS = graphic("_");
  
  public static final String OPTIONPERIOD = standard("?.");
  public static final String OPTIONINDEX = standard("??");

  public static final String STRING_CATENATE = graphic("++");
  public static final String DISPLAY = "display";
  public static final String FORMAT = standard("format");

  public static final String RAW = "_%_";

  // The pp contract
  public static final String PP = "pP";
  public static final String PPRINT = "pPrint";
  public static final String PPDISP = "ppDisp";

  public static final String PPSTRING = "ppStr";

  public static final String IDENTIFIER = keyword("identifier");
  public static final String ERROR = standard("error");
  public static final String WARNING = standard("warning");
  public static final String INFO = standard("info");

  public static final String LET = keyword("let");
  public static final String USING = keyword("using");
  public static final String PACKAGE = keyword("package");
  public static final String EXPORTS = keyword("'s");
  public static final String ALSO = keyword("'n");

  public static final String SEQUENCE = standard("sequence");

  public static final String EMPTY = standard("_empty");
  public static final String PAIR = standard("_pair");
  public static final String BACK = standard("_back");
  public static final String APND = standard("_apnd");
  public static final String NIL = standard("_nil");
  public static final String ADD_TO_FRONT = standard("_cons");

  public static final String DOLLAR = graphic("$");

  public static final String DOTSLASH = graphic("./");

  public static final String WFF_RULE = graphic(":-");
  public static final String WFF_OR = graphic(":|");
  public static final String WFF_AND = graphic(":&");
  public static final String WFF_NOT = graphic(":!");
  public static final String WFF_DEFINES = graphic("::");
  public static final String WFF_STAR = graphic(":*");
  public static final String WFF_TERM = graphic(";*");
  public static final String WFF_VAR = graphic("?");
  public static final String WFF_STATEMENT = "statement";
  public static final String WFF_IDENTIFIER = "identifier";
  public static final String WFF_SYMBOL = "symbol";
  public static final String WFF_KEYWORD = "keyword";
  public static final String WFF_INTEGER = standard("integer");
  public static final String WFF_FLOAT = standard("float");
  public static final String WFF_NUMBER = "number";
  public static final String WFF_ERROR = "error";
  public static final String WFF_WARNING = "warning";
  public static final String WFF_CHAR = standard("char");
  public static final String WFF_STRING = standard("string");
  public static final String WFF_TUPLE = "tuple";
  public static final String WFF_REGEXP = "regexp";

  public static final String META_HASH = graphic("#");
  public static final String MACRO_RULE = graphic("==>");
  public static final String MACRO_WHERE = graphic("##");
  public static final String MACRO_GEN = graphic("#$");
  public static final String MACRO_IDENT = graphic("$$");
  public static final String MACRO_INTERN = graphic("#~");
  public static final String MACRO_FORCE = graphic("#*");
  public static final String MACRO_LOCATION = "__location__";
  public static final String MACRO_LOC = graphic("@@");
  public static final String MACRO_APPLY = graphic("#@");
  public static final String MACRO_CATENATE = graphic("#+");
  public static final String MACRO_LANGLE = graphic("#<");
  public static final String MACRO_RANGLE = graphic(">#");
  public static final String MACRO_TUPLE = graphic("#<>");
  public static final String MACRO_DETUPLE = graphic("#:");
  public static final String MACRO_LOG = "_macro_log";
  public static final String MACRO_EXPLODE = "# explode";

  public static final String MAC_KEY = AstMacroKey.name;
  public static final String MAC_KEY_PREFIX = "%_";

  public static final String FMT_RULE = graphic("-->");

  public static final String FMT_INDENT = "indent";
  public static final String FMT_LINES = "blankLines";
  public static final String FMT_COMMENT_COLUMN = "commentColumn";
  public static final String FMT_WRAP_COLUMN = "wrapColumn";
  public static final String FMT_COMMENT_WRAP = "commentWrap";
  public static final String FMT_BREAK_BEFORE = "breakBefore";
  public static final String FMT_BREAK_AFTER = "breakAfter";
  public static final String FMT_INCREMENT = graphic(":+");
  public static final String FMT_DECREMENT = graphic(":-");

  public static final String INFIX = "infix";
  public static final String LEFT = "left";
  public static final String RIGHT = "right";
  public static final String PREFIX = "prefix";
  public static final String PREFIXA = "prefixAssoc";
  public static final String POSTFIX = "postfix";
  public static final String POSTFIXA = "postfixAss";
  public static final String TOKEN = "token";
  public static final String FORCE = "force";
  public static final String BRACKETS = "pair";

  public static final String MACRORULE = "==>";
  public static final String REGEXP = "`";

  public static final String ANONYMOUS_PREFIX = "__@";

  private static String keyword(String word)
  {
    word = word.intern();
    assert !keywords.contains(word);
    keywords.add(word);
    return word;
  }

  public static boolean isKeyword(String word)
  {
    return keywords.contains(word);
  }

  public static boolean isKeyword(IAbstract term)
  {
    return term instanceof Name && isKeyword(((Name) term).getId());
  }

  private static String standard(String word)
  {
    word = word.intern();

    assert !standard.contains(word) && !isKeyword(word);
    standard.add(word);
    return word;
  }

  public static boolean isStandard(IAbstract name)
  {
    return Abstract.isName(name) && standard.contains(Abstract.getId(name));
  }

  private static String graphic(String word)
  {
    graphic.add(word);
    return word;
  }

  private static String[] words(Collection<String> set)
  {
    return set.toArray(new String[set.size()]);
  }

  private static String[] reservedWords()
  {
    return words(keywords);
  }

  private static String[] graphics()
  {
    return words(graphic);
  }

  public static void main(String args[])
  {
    showList(reservedWords(), 4);

    showList(graphics(), 8);

    showList(words(standard), 4);
  }

  public static void showList(String[] keyList, int cols)
  {
    int numberKeywords = keyList.length;

    int split = (numberKeywords + cols - 1) / cols;

    for (int ix = 0; ix < split; ix++) {
      for (int jx = 0; jx < cols; jx++) {
        if (jx > 0)
          System.out.print("&");
        if (ix + jx * split < numberKeywords) {
          System.out.print("\\tt ");
          System.out.print(latexString(keyList[ix + jx * split]));
        }
      }
      System.out.println("\\\\");
    }
  }

  public static String latexString(String str)
  {
    StringBuilder bldr = new StringBuilder();
    for (int ix = 0; ix < str.length(); ix = str.offsetByCodePoints(ix, 1)) {
      int ch = str.codePointAt(ix);
      switch (ch) {
      case ' ':
        bldr.append("\\spce{}");
        continue;
      case '%':
        bldr.append("\\%");
        continue;
      case '#':
        bldr.append("\\#");
        continue;
      case '{':
        bldr.append("\\{");
        continue;
      case '}':
        bldr.append("\\}");
        continue;
      case '\\':
        bldr.append("\\bsl{}");
        continue;
      case '^':
        bldr.append("\\uphat{}");
        continue;
      case '~':
        bldr.append("\\tlda{}");
        continue;
      case '&':
        bldr.append("\\&");
        continue;
      case '$':
        bldr.append("\\$");
        continue;
      case '_':
        bldr.append("\\_");
        continue;
      default:
        bldr.appendCodePoint(ch);
      }
    }
    return bldr.toString();
  }

  public static final String METAENTRY = RepositoryManager.METAENTRY;
  public static final String MANIFEST = RepositoryManager.MANIFEST;
  public static final String COMPILED = RepositoryManager.COMPILED;
  public static final String VERSION = "version";
}
