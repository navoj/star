package org.star_lang.star.compiler.operator;

import java.util.*;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.*;
import org.star_lang.star.compiler.grammar.Tokenizer;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

/**
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
@SuppressWarnings("serial")
public class Operators implements PrettyPrintable
{
  public static final int FOR_ALL_PRIORITY = 1005;
  public static final int INSTANCE_OF_PRIORITY = 900;
  public static final int IMPLEMENTS_PRIORITY = 900;
  public static final int EQUAL_PRIORITY = 900;
  public static final int S_T_PRIORITY = 1010;
  public static final int DETERMINES_PRIORITY = 895;
  public static final int HAS_TYPE_PRIORITY = 1020;
  public static final int OF_PRIORITY = 840;
  public static final int REF_PRIORITY = 900;
  public static final int PTN_TYPE_PRIORITY = 900;
  public static final int FUN_TYPE_PRIORITY = 910;

  public final static int STATEMENT_PRIORITY = 2000;
  public final static int EXPRESSION_PRIORITY = 1200;
  public final static int CATENATE_PRIORITY = 850;
  public static final int ARG_PRIORITY = 999;

  private final Map<String, Collection<Operator>> operators;
  private final Map<String, BracketPair> bracketPairs;
  private final Collection<String> specialTokens;

  private static final Operators root = new Operators();

  public Operators()
  {
    this(new HashMap<String, Collection<Operator>>(), new HashMap<String, BracketPair>(), new HashSet<String>());
  }

  private Operators(Map<String, Collection<Operator>> operators, Map<String, BracketPair> brackets,
      Collection<String> specialTokens)
  {
    this.operators = operators;
    this.bracketPairs = brackets;
    this.specialTokens = specialTokens;
  }

  static {
    // Define the standard operators
    defineRight(StandardNames.TERM, STATEMENT_PRIORITY);
    definePostfix(StandardNames.TERM, STATEMENT_PRIORITY);

    defineInfix(StandardNames.CONS, STATEMENT_PRIORITY - 1);
    defineInfix(StandardNames.ENDCONS, STATEMENT_PRIORITY - 2);

    defineInfix(StandardNames.SCONS, 1099);
    defineInfix(StandardNames.ENDSCONS, 1098);

    definePrefix(StandardNames.META_HASH, 1350);

    defineInfix(StandardNames.FMT_RULE, 1347);
    defineInfix(StandardNames.MACRO_RULE, 1347);
    defineInfix(StandardNames.WFF_RULE, 1347);

    defineRight(StandardNames.WFF_OR, 1345);
    defineRight(StandardNames.WFF_AND, 1344);

    definePrefix(StandardNames.WFF_NOT, 1343);

    defineInfix(StandardNames.MACRO_WHERE, 1342);

    defineInfix(StandardNames.FMT_INCREMENT, 1340);
    defineInfix(StandardNames.WFF_STAR, 1340);
    defineInfix(StandardNames.WFF_TERM, 1340);
    defineInfix(StandardNames.WFF_RULES, 1340);
    defineInfix(StandardNames.WFF_DEFINES, 1341);

    definePrefixAssoc(StandardNames.PRIVATE, 1320);

    defineRight(StandardNames.HAS, 1300);
    definePrefix(StandardNames.TYPE, 1250, 1201); // type is not an operator when expected priority
    // is 1200 or less
    definePrefix(StandardNames.KIND, 900);

    definePrefix(StandardNames.ON, 1300);

    definePrefix(StandardNames.CONTRACT, 1300);
    definePrefix(StandardNames.IMPLEMENTATION, 1300);

    definePrefix(StandardNames.DEF, 1300);
    definePrefix(StandardNames.VAR, 1300);
    definePrefix(StandardNames.FUN, 1300);
    definePrefix(StandardNames.PRC, 1300);
    definePrefix(StandardNames.PTN, 1300);

    definePrefix(StandardNames.JAVA, 1300);
    definePrefix(StandardNames.OPEN, 1300);
    
    defineRight(StandardNames.PIPE, 1290);


    defineRight(StandardNames.DO, 1200);

    defineInfix(StandardNames.IS, 1200);
    defineInfix(StandardNames.COUNTS_AS, 1200);

    defineRight(StandardNames.ELSE, 1200);
    defineInfix(StandardNames.THEN, 1180);
    definePrefix(StandardNames.IF, 1175);

    definePrefix(StandardNames.FOR, 1175);
    definePrefix(StandardNames.WHILE, 1175);

    definePrefix(StandardNames.REMOVE, 1200);

    definePrefix(StandardNames.YIELD, 1150);

    defineInfix(StandardNames.FROM, 1130);
    defineInfix(StandardNames.TO, 1130);

    defineInfix(StandardNames.ASSIGN, 1120);
    definePrefix(StandardNames.PERFORM, 1120);

    definePrefix(StandardNames.NOTIFY, 1100);

    definePrefix(StandardNames.REQUEST, 1050);

    definePrefix(StandardNames.ASSERT, 1100);
    definePrefix(StandardNames.IGNORE, 1100);
    definePrefix(StandardNames.VALIS, 1100);

    definePostfix(StandardNames.DEFAULT, 1100);

    definePrefix(StandardNames.EXTEND, 1100);
    definePrefix(StandardNames.MERGE, 1100);
    definePrefix(StandardNames.DELETE, 1100);
    definePrefix(StandardNames.UPDATE, 1100);

    defineInfix(StandardNames.PARALLEL, 1100);

    definePrefix(StandardNames.TRY, 1100);
    defineInfix(StandardNames.CATCH, 1050);

    defineInfix(StandardNames.WITH, 1050);

    definePrefix(StandardNames.CASE, 1020);
    definePrefix(StandardNames.SWITCH, 1020);

    defineInfix(StandardNames.HAS_TYPE, HAS_TYPE_PRIORITY);
    defineInfix(StandardNames.HASTYPE, 1020);

    definePrefix(StandardNames.IMPORT, 1000);

    definePrefix(StandardNames.WITH, 999);
    definePrefix(StandardNames.WITHOUT, 999);

    defineInfix(StandardNames.COMPUTATION, 999);

    defineRight(StandardNames.COMMA, 1000);

    definePrefix(StandardNames.RAISE, 1000);

    definePrefix(StandardNames.QUERY, 1000);

    defineInfix(StandardNames.DEFAULT, 1000);

    definePrefix(StandardNames.MEMO, 999);

    defineInfix(StandardNames.QUESTION, 950);

    definePrefix(StandardNames.SPAWN, 950);
    definePrefix(StandardNames.WAITFOR, 950);
    definePrefix(StandardNames.WHEN, 950);
    
    defineRight(StandardNames.COLON, 940);
    definePostfix(StandardNames.COLON, 940);

    defineInfix(StandardNames.DOTSLASH, 999);

    defineLeft(StandardNames.GROUPBY, 960);
    defineInfix(StandardNames.ORDERBY, 950);
    defineInfix(StandardNames.ORDERDESCENDINBY, 950);
    defineInfix(StandardNames.DESCENDINGBY, 950);
    defineInfix(StandardNames.WHERE, 940);

    defineRight(StandardNames.OR, 930);
    defineRight(StandardNames.OTHERWISE, 930);

    defineRight(StandardNames.AND, 920);
    defineRight(StandardNames.IMPLIES, 920);

    definePrefix(StandardNames.NOT, 910);
    
    definePrefix(StandardNames.LET, 909);
    defineLeft(StandardNames.USING, 908);
    defineInfix(StandardNames.IN, 908);
    defineInfix(StandardNames.DOWN, 908);

    defineInfix(StandardNames.EXPORTS, 907);
    defineRight(StandardNames.ALSO, 906);

    defineRight(StandardNames.IMPLEMENTS, IMPLEMENTS_PRIORITY);
    definePostfix(StandardNames.IS_TUPLE, 900);
    defineInfix(StandardNames.INSTANCE_OF, INSTANCE_OF_PRIORITY);
    defineInfix(StandardNames.HAS_KIND, 900);

    defineInfix(StandardNames.ON, 900);

    defineInfix(StandardNames.EQUAL, EQUAL_PRIORITY);
    defineInfix(StandardNames.NOT_EQUAL, 900);
    defineInfix(StandardNames.LESS, 900);
    defineInfix(StandardNames.LESS_EQUAL, PTN_TYPE_PRIORITY);

    defineInfix(StandardNames.GREATER_EQUAL, 900);
    defineInfix(StandardNames.GREATER, 900);

    defineInfix(StandardNames.MAP_ARROW, 900);

    definePrefixAssoc(StandardNames.EXISTS, 1005);
    definePrefixAssoc(StandardNames.FORALL, 1005);
    definePrefixAssoc(StandardNames.FOR_ALL, FOR_ALL_PRIORITY);
    defineRight(StandardNames.ST, S_T_PRIORITY);
    defineRight(StandardNames.S_T, S_T_PRIORITY);

    defineRight(StandardNames.UNI_TILDA, 950);
    defineRight(StandardNames.FUN_ARROW, FUN_TYPE_PRIORITY);
    defineRight(StandardNames.OVERLOADED_TYPE, FUN_TYPE_PRIORITY);
    defineRight(StandardNames.CONSTRUCTOR_TYPE, FUN_TYPE_PRIORITY);

    defineInfix(StandardNames.SUBSTITUTE, 900);

    defineInfix(StandardNames.MATCHES, 900);
    defineInfix(StandardNames.BOUND_TO, 900);
    defineInfix(StandardNames.HAS_VALUE, 900);
    defineRight(StandardNames.OR_ELSE, 900);

    defineRight(StandardNames.OVER, 900);

    definePrefix(StandardNames.LAMBDA, 900);

    definePrefix(StandardNames.REF, REF_PRIORITY);

    defineInfix(StandardNames.DETERMINES, DETERMINES_PRIORITY);

    defineRight(StandardNames.STRING_CATENATE, CATENATE_PRIORITY);

    defineRight(StandardNames.OF, OF_PRIORITY);

    definePrefix(StandardNames.REDUCTION, 830);

    defineInfix(StandardNames.MATCHING, 800);

    defineLeft(StandardNames.PLUS, 720);
    defineLeft(StandardNames.MINUS, 720);
    defineLeft(StandardNames.TIMES, 700);
    defineLeft(StandardNames.DIVIDE, 700);
    defineLeft(StandardNames.PCENT, 700);

    defineInfix(StandardNames.POWER, 650);

    definePrefix(StandardNames.VALOF, 500);
    defineInfix(StandardNames.ON_ABORT, 475);

    defineInfix(StandardNames.CAST, 420);
    defineInfix(StandardNames.AS, 420);

    definePrefix(StandardNames.UNIQUE, 400);
    definePrefix(StandardNames.ALL, 400);
    definePrefix(StandardNames.ANYOF, 400);
    definePrefix(StandardNames.ANY_OF, 400);

    defineInfix(StandardNames.APPLY, 200);

    defineInfix(StandardNames.MACRO_APPLY, 200);

    defineInfix(StandardNames.MACRO_LOC, 200);

    defineLeft(StandardNames.PERIOD, 175);
    defineLeft(StandardNames.OPTIONPERIOD, 175);

    definePrefix(StandardNames.SHRIEK, 150);

    definePrefix(StandardNames.MINUS, 100);
    definePrefix(StandardNames.PLUS, 100);

    definePrefix(StandardNames.QUESTION, 75);
    definePrefix(StandardNames.PCENT, 75);
    definePrefix(StandardNames.DBLCENT, 75);
    definePrefix(StandardNames.DOLLAR, 75);

    definePrefix(StandardNames.MACRO_GEN, 50);
    definePrefix(StandardNames.MACRO_INTERN, 50);
    definePrefix(StandardNames.MACRO_IDENT, 50);
    definePrefix(StandardNames.MACRO_FORCE, 50);
    defineRight(StandardNames.MACRO_IDENT, 50);
    defineRight(StandardNames.MACRO_CATENATE, 50);
    definePrefix(StandardNames.MACRO_DETUPLE, 50);
    definePrefix(StandardNames.MACRO_EXPLODE, 50);

    defineBrackets(2000, "{", "}", StandardNames.BRACES);
    defineBrackets(1100, "[", "]", StandardNames.SQUARE);
    defineBrackets(1200, "(", ")", StandardNames.PARENS);
    defineBrackets(2000, "#(", ")#", "");
    defineBrackets(2000, "#<", ">#", StandardNames.MACRO_TUPLE);
    defineBrackets(2000, StandardNames.LQUOTE, StandardNames.RQUOTE, StandardNames.QUOTE);
  }

  public static Operators operatorRoot()
  {
    return root;
  }

  public boolean isOperator(String operator, int priority)
  {
    Collection<Operator> opSpec = operators.get(operator);
    if (opSpec != null) {
      for (Operator op : opSpec)
        if (op.getMinPriority() <= priority)
          return true;
    }
    return false;
  }

  public Operator isPrefixOperator(String operator, int priority)
  {
    if (operators.containsKey(operator)) {
      for (Operator op : operators.get(operator)) {
        if (op.getForm() == OperatorForm.prefix && op.getMinPriority() <= priority)
          return op;
      }
    }

    return null;
  }

  public static Operator isRootPrefixOperator(String op)
  {
    return operatorRoot().isPrefixOperator(op, 0);
  }

  public Operator isPostfixOperator(String operator, int priority)
  {
    if (operators.containsKey(operator)) {
      for (Operator op : operators.get(operator)) {
        if (op.getForm() == OperatorForm.postfix && op.getMinPriority() <= priority)
          return op;
      }
    }

    return null;
  }

  public static Operator isRootPostfixOperator(String op)
  {
    return operatorRoot().isPostfixOperator(op, 0);
  }

  public Operator isInfixOperator(String operator, int priority)
  {
    if (operators.containsKey(operator)) {
      for (Operator op : operators.get(operator)) {
        if (op.getForm() == OperatorForm.infix && op.getMinPriority() <= priority)
          return op;
      }
    }

    return null;
  }

  public static Operator isRootInfixOperator(String op)
  {
    return operatorRoot().isInfixOperator(op, 0);
  }

  public boolean canDominate(String name, int priority)
  {
    if (operators.containsKey(name)) {
      for (Operator op : operators.get(name))
        if (op.getPriority() > priority)
          return true;
    }
    return false;
  }

  private void defineOperator(String operator, Operator spec, boolean force, ErrorReport errors, Location loc)
  {
    defineToken(operator);

    Collection<Operator> ops = operators.get(operator);
    if (ops == null) {
      ops = new HashSet<>();
      operators.put(operator, ops);
    } else {
      for (Iterator<Operator> it = ops.iterator(); it.hasNext();) {
        Operator op = it.next();
        if (spec.getForm() == op.getForm()) {
          if (!force && spec.getPriority() != op.getPriority())
            errors.reportError(operator + " already an " + op.getForm() + " operator of priority " + op.getPriority(),
                loc);
          it.remove();
        }
      }
    }
    ops.add(spec);
  }

  public void defineToken(String name)
  {
    if (name.contains(" ")) {
      String fragments[] = name.split(" ");
      Tokenizer.declareMultiToken(fragments, name);
    } else
      Tokenizer.recordStdToken(name);
    specialTokens.add(name);
  }

  public void definePrefixOperator(String operator, int priority, boolean force, ErrorReport errors, Location loc)
  {
    definePrefixOperator(operator, priority, 0, force, errors, loc);
  }

  public void definePrefixOperator(String operator, int priority, int minPriority, boolean force, ErrorReport errors,
      Location loc)
  {
    defineOperator(operator, new Operator(operator, OperatorForm.prefix, -1, priority, priority - 1, minPriority),
        force, errors, loc);
  }

  public void definePrefixAssocOperator(String operator, int priority, boolean force, ErrorReport errors, Location loc)
  {
    defineOperator(operator, new Operator(operator, -1, priority, priority, OperatorForm.prefix), force, errors, loc);
  }

  public void definePostfixOperator(String operator, int priority, boolean force, ErrorReport errors, Location loc)
  {
    defineOperator(operator, new Operator(operator, priority - 1, priority, -1, OperatorForm.postfix), force, errors,
        loc);
  }

  public void definePostfixAssocOperator(String operator, int priority, boolean force, ErrorReport errors, Location loc)
  {
    defineOperator(operator, new Operator(operator, priority, priority, -1, OperatorForm.postfix), force, errors, loc);
  }

  public void defineInfixOperator(String operator, int priority, boolean force, ErrorReport errors, Location loc)
  {
    defineOperator(operator, new Operator(operator, priority - 1, priority, priority - 1, OperatorForm.infix), force,
        errors, loc);
  }

  public void defineInfixOperator(String operator, int priority, int minPriority, boolean force, ErrorReport errors,
      Location loc)
  {
    defineOperator(operator, new Operator(operator, OperatorForm.infix, priority - 1, priority, priority - 1,
        minPriority), force, errors, loc);
  }

  public void defineLeftOperator(String operator, int priority, boolean force, ErrorReport errors, Location loc)
  {
    defineOperator(operator, new Operator(operator, priority, priority, priority - 1, OperatorForm.infix), force,
        errors, loc);
  }

  public void defineRightOperator(String operator, int priority, boolean force, ErrorReport errors, Location loc)
  {
    defineOperator(operator, new Operator(operator, priority - 1, priority, priority, OperatorForm.infix), force,
        errors, loc);
  }

  public BracketPair getBracketPair(String left)
  {
    if (bracketPairs.containsKey(left))
      return bracketPairs.get(left);
    else
      return null;
  }

  public boolean isLeftBracket(String left)
  {
    return bracketPairs.containsKey(left);
  }

  public boolean isRightBracket(String right)
  {
    if (right.equals(")") || right.equals("]"))
      return true;
    for (Entry<String, BracketPair> entry : bracketPairs.entrySet()) {
      BracketPair pair = entry.getValue();
      if (pair.rightBracket.equals(right))
        return true;
    }

    return false;
  }

  public boolean isABracket(String str)
  {
    return isLeftBracket(str) || isRightBracket(str);
  }

  public void defineBracketPair(int inner, String left, String right, String operator) throws OperatorException
  {
    if (bracketPairs.containsKey(left)) {
      BracketPair existing = bracketPairs.get(left);
      if (existing.innerPriority != inner || !existing.leftBracket.equals(left) || !existing.rightBracket.equals(right)
          || !existing.operator.equals(operator))
        throw new OperatorException(left + " already defined as a bracket pair");
    } else if (operators.containsKey(left))
      throw new OperatorException(left + " already defined as an operator");
    else if (operators.containsKey(right))
      throw new OperatorException(right + " already defined as an operator");
    else {
      BracketPair pair = new BracketPair(inner, left, right, operator);
      Tokenizer.recordStdToken(left);
      Tokenizer.recordStdToken(right);

      bracketPairs.put(left, pair);
    }
  }

  private static void defineBrackets(int inner, String left, String right, String operator)
  {
    try {
      root.defineBracketPair(inner, left, right, operator);
    } catch (OperatorException e) {
      assert false : "bracket pair already defined";
    }
  }

  private static void defineInfix(String op, int priority)
  {
    root.defineInfixOperator(op, priority, false, null, Location.nullLoc);
  }

  private static void defineRight(String op, int priority)
  {
    root.defineRightOperator(op, priority, false, null, Location.nullLoc);
  }

  private static void defineLeft(String op, int priority)
  {
    root.defineLeftOperator(op, priority, false, null, Location.nullLoc);
  }

  private static void definePrefix(String op, int priority)
  {
    root.definePrefixOperator(op, priority, false, null, Location.nullLoc);
  }

  private static void definePrefix(String op, int priority, int minPriority)
  {
    root.definePrefixOperator(op, priority, minPriority, false, null, Location.nullLoc);
  }

  private static void definePrefixAssoc(String op, int priority)
  {
    root.definePrefixAssocOperator(op, priority, false, null, Location.nullLoc);
  }

  private static void definePostfix(String op, int priority)
  {
    root.definePostfixOperator(op, priority, false, null, Location.nullLoc);
  }

  public void declareOperator(ErrorReport errors, IAbstract stmt)
  {
    final Location loc = stmt.getLoc();
    final boolean force;
    if (Abstract.isUnary(stmt, StandardNames.FORCE)) {
      force = true;
      stmt = Abstract.unaryArg(stmt);
    } else
      force = false;

    if (Abstract.isUnary(stmt, StandardNames.TOKEN)) {
      String op = getOperator(errors, Abstract.unaryArg(stmt));

      if (op != null) {
        defineToken(op);
      } else
        errors.reportError("missing token specification", loc);
    } else if (Abstract.isBinary(stmt, StandardNames.INFIX)) {
      IAbstract o1 = Abstract.getArg(stmt, 1);
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(o1);

      if (op != null && priority >= 0 && priority < STATEMENT_PRIORITY) {
        defineInfixOperator(op, priority, force, errors, loc);
      } else
        errors.reportError("infix operator must have priority in range 0..1999", loc);
    } else if (Abstract.isBinary(stmt, StandardNames.LEFT)) {
      IAbstract o1 = Abstract.getArg(stmt, 1);
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(o1);

      if (op != null && priority >= 0 && priority < STATEMENT_PRIORITY) {
        defineLeftOperator(op, priority, force, errors, loc);
      } else
        errors.reportError("left operator must have priority in range 0..1999", loc);
    } else if (Abstract.isBinary(stmt, StandardNames.RIGHT)) {
      IAbstract o1 = Abstract.getArg(stmt, 1);
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(o1);

      if (op != null && priority >= 0 && priority < STATEMENT_PRIORITY) {
        defineRightOperator(op, priority, force, errors, loc);
      } else
        errors.reportError("right operator must have priority in range 0..1999", loc);
    } else if (Abstract.isBinary(stmt, StandardNames.PREFIX)) {
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(Abstract.getArg(stmt, 1));

      if (op != null && priority >= 0 && priority < STATEMENT_PRIORITY) {
        definePrefixOperator(op, priority, force, errors, loc);
      } else
        errors.reportError("prefix operator must have priority in range 0..1999", loc);
    } else if (Abstract.isTernary(stmt, StandardNames.PREFIX)) {
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(Abstract.getArg(stmt, 1));
      int minPrior = getPriority(Abstract.getArg(stmt, 2));

      if (op != null && priority >= 0 && minPrior >= 0 && priority < STATEMENT_PRIORITY) {
        definePrefixOperator(op, priority, minPrior, force, errors, loc);
      } else
        errors.reportError("prefix operator must have priority in range 0..1999", loc);
    } else if (Abstract.isBinary(stmt, StandardNames.POSTFIX)) {
      IAbstract o1 = Abstract.getArg(stmt, 1);
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(o1);

      if (op != null && priority >= 0 && priority < STATEMENT_PRIORITY) {
        definePostfixOperator(op, priority, force, errors, loc);
      } else
        errors.reportError("postfix operator must have priority in range 0..1999", loc);
    } else if (Abstract.isBinary(stmt, StandardNames.PREFIXA)) {
      IAbstract o1 = Abstract.getArg(stmt, 1);
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(o1);

      if (op != null && priority >= 0 && priority < STATEMENT_PRIORITY) {
        definePrefixAssocOperator(op, priority, force, errors, loc);
      } else
        errors.reportError("associative prefix operator must have priority in range 0..1999", loc);
    } else if (Abstract.isBinary(stmt, StandardNames.POSTFIXA)) {
      IAbstract o1 = Abstract.getArg(stmt, 1);
      String op = getOperator(errors, Abstract.getArg(stmt, 0));
      int priority = getPriority(o1);

      if (op != null && priority >= 0 && priority < STATEMENT_PRIORITY) {
        definePostfixAssocOperator(op, priority, force, errors, loc);
      } else
        errors.reportError("associative postfix operator must have priority in range 0..1999", loc);
    } else if (Abstract.isApply(stmt, StandardNames.BRACKETS, 3)) {
      IAbstract o2 = Abstract.getArg(stmt, 2);
      String left = getOperator(errors, Abstract.getArg(stmt, 0));
      String right = getOperator(errors, Abstract.getArg(stmt, 1));
      int priority = getPriority(o2);

      if (left != null && right != null && priority >= 0 && priority <= STATEMENT_PRIORITY) {
        try {
          defineBracketPair(priority, left, right, left + right);
        } catch (OperatorException e) {
          errors.reportError(e.getMessage(), loc);
        }
      } else
        errors.reportError("bracket pair priority must be between 0 and 2000", loc);
    } else
      errors.reportError("cannot understand meta statement: " + stmt, loc);
  }

  private static String getOperator(ErrorReport errors, IAbstract term)
  {
    if (term instanceof Name)
      return ((Name) term).getId();
    else if (Abstract.isParenTerm(term))
      return getOperator(errors, Abstract.getArg(term, 0));
    else if (term instanceof StringLiteral)
      return Abstract.getString(term);
    else {
      errors.reportError("expecting an operator name", term.getLoc());
      return null;
    }
  }

  private static int getPriority(IAbstract term)
  {
    if (Abstract.isUnary(term, StandardTypes.INTEGER))
      term = Abstract.unaryArg(term);
    if (term instanceof IntegerLiteral)
      return ((IntegerLiteral) term).getLit();
    else
      return -1;
  }

  public Operators copy()
  {
    Operators copy = new Operators();

    for (Entry<String, Collection<Operator>> entry : operators.entrySet()) {
      final String opName = entry.getKey();
      Collection<Operator> ops = operators.get(opName);
      Collection<Operator> copyOps = new HashSet<>(ops);
      copy.operators.put(opName, copyOps);
    }

    for (Entry<String, BracketPair> entry : bracketPairs.entrySet()) {
      copy.bracketPairs.put(entry.getKey(), entry.getValue());
    }

    return copy;
  }

  // Merge a set of operators with this one
  public void importOperators(Operators others, ErrorReport errors, Location loc)
  {
    for (Entry<String, Collection<Operator>> entry : others.operators.entrySet()) {
      for (Operator op : entry.getValue())
        defineOperator(entry.getKey(), op, true, errors, loc);
    }

    for (String token : others.specialTokens)
      defineToken(token);

    for (Entry<String, BracketPair> entry : others.bracketPairs.entrySet()) {
      BracketPair pair = (BracketPair) entry.getValue();
      try {
        defineBracketPair(pair.innerPriority, pair.leftBracket, pair.rightBracket, pair.operator);
      } catch (OperatorException e) {
        errors.reportError(StringUtils.msg("cannot import pair definition ", pair, "\nbecause ", e.getMessage()), loc);
      }
    }
  }

  public static void main(String[] args)
  {
    boolean eol = false;
    for (int ix = 2000; ix > 0; ix--) {
      for (Entry<String, Collection<Operator>> entry : root.operators.entrySet()) {
        for (Operator op : entry.getValue()) {
          if (op.getPriority() == ix) {
            System.out.print(op.toLatex());

            if (eol)
              System.out.println("\\\\");
            else
              System.out.print("&");
            eol = !eol;
          }
        }
      }
    }

    for (Entry<String, BracketPair> entry : root.bracketPairs.entrySet()) {
      System.out.println(entry.getValue().toString());
    }

    // Generate table for emacs mode
    for (int ix = 2000; ix > 0; ix--) {
      for (Entry<String, Collection<Operator>> entry : root.operators.entrySet()) {
        // We use an artificial ordering between styles: nonassoc, left, right

        for (OperatorForm form : OperatorForm.values()) {
          boolean triggered = false;
          for (Operator op : entry.getValue()) {
            if (op.getPriority() == ix && op.getForm() == form) {
              if (!triggered) {
                triggered = true;
                System.out.print("(");
                switch (form) {
                case prefix:
                  if (op.isRightAssoc())
                    System.out.print("right");
                  else
                    System.out.print("nonassoc");
                  break;
                case infix:
                  if (op.isLeftAssoc())
                    System.out.print("left");
                  else if (op.isRightAssoc())
                    System.out.print("right");
                  else
                    System.out.print("nonassoc");
                  break;
                case postfix:
                  if (op.isLeftAssoc())
                    System.out.print("left");
                  else
                    System.out.print("nonassoc");
                  break;
                case none:
                  System.out.print("nonassoc");
                  break;
                }
              }
              System.out.print(" \"" + op.getOperator() + "\"");
            }
          }
          if (triggered)
            System.out.println(")");
        }
      }
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    for (Entry<String, Collection<Operator>> entry : operators.entrySet()) {
      for (Operator op : entry.getValue()) {
        op.prettyPrint(disp);
        disp.append(";\n");
      }
    }

    for (Entry<String, BracketPair> entry : bracketPairs.entrySet()) {
      entry.getValue().prettyPrint(disp);
      disp.append("\n");
    }

    for (String special : specialTokens) {
      disp.append("#");
      disp.append(StandardNames.TOKEN);
      disp.append("(");
      disp.appendQuoted(special);
      disp.append(");\n");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
