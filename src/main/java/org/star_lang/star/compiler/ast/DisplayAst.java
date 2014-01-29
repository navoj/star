package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.operator.InfixOperator;
import org.star_lang.star.compiler.operator.OperatorForm;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.operator.PostfixOperator;
import org.star_lang.star.compiler.operator.PrefixOperator;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IList;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.StringWrap;
import com.starview.platform.data.value.BoolWrap.FalseValue;
import com.starview.platform.data.value.BoolWrap.TrueValue;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class DisplayAst implements IFunction
{
  private final static Operators operators = Operators.operatorRoot();

  public static void display(PrettyPrintDisplay disp, IAbstract term)
  {
    display(disp, term, 2000);
  }

  public static String display(IAbstract term)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    display(disp, term, 2000);
    return disp.toString();
  }

  protected static void display(PrettyPrintDisplay disp, IAbstract term, int priority)
  {
    switch (term.astType()) {
    case Apply: {
      Apply apply = (Apply) term;
      displayApply(disp, apply, priority);
      break;
    }
    case Bool: {
      BooleanLiteral lit = (BooleanLiteral) term;
      if (lit.getLit())
        disp.appendWord(TrueValue.name);
      else
        disp.appendWord(FalseValue.name);
      break;
    }
    case Char: {
      CharLiteral lit = (CharLiteral) term;
      disp.append("'");
      disp.appendChar(lit.getLit());
      disp.append("'");
      break;
    }
    case Dec: {
      BigDecimalLiteral lit = (BigDecimalLiteral) term;
      disp.append(lit.getLit().toString());
      disp.append("a");
      break;
    }
    case Flt: {
      FloatLiteral flt = (FloatLiteral) term;
      disp.append(flt.getLit());
      break;
    }
    case Int: {
      IntegerLiteral lit = (IntegerLiteral) term;
      disp.appendWord(lit.getLit());
      break;
    }
    case Long: {
      LongLiteral lit = (LongLiteral) term;
      disp.appendWord(lit.getLit());
      break;
    }
    case Name: {
      String id = ((Name) term).getId();
      if (id.equals(StandardNames.BRACES) || id.equals(StandardNames.SQUARE) || id.equals(StandardNames.PARENS))
        disp.append(id);
      else
        disp.appendIden(id);
      break;
    }
    case Str: {
      StringLiteral str = (StringLiteral) term;
      disp.append(StringUtils.quoteString(str.getLit()));
      break;
    }
    default:
      assert false : "should not happen";
      break;
    }
  }

  protected static void displayApply(PrettyPrintDisplay disp, Apply app, int priority)
  {
    IAbstract operator = app.getOperator();

    if (CompilerUtils.isBraceTerm(app)) {
      display(disp, CompilerUtils.braceLabel(app), 0);
      display(disp, CompilerUtils.unWrap(CompilerUtils.braceArg(app), StandardNames.TERM), "{\n  ", ";\n", "\n}", 2,
          2000);
    } else if (CompilerUtils.isAnonAggConLiteral(app))
      display(disp, CompilerUtils.unWrap(CompilerUtils.anonAggEls(app), StandardNames.TERM), "{ ", ";\n", "\n}", 2,
          2000);
    else if (CompilerUtils.isSquareTerm(app)) {
      display(disp, CompilerUtils.squareLabel(app), 0);
      disp.append("[");
      IAbstract content = CompilerUtils.squareArg(app);
      if (content != null)
        display(disp, content, 1000);
      disp.append("]");
    } else if (Abstract.isTupleTerm(app))
      display(disp, app.getArgs(), " (", ", ", ")", 0, 1000);
    else if (Abstract.arity(app) == 2 && Abstract.isIdentifier(operator)) {
      String op = Abstract.getId(operator);
      InfixOperator infix = operators.isInfixOperator(op, priority);
      if (infix != null) {
        OperatorForm form = infix.getForm();
        int infixPriority = infix.getPriority();
        paren(disp, infixPriority, priority, "(");

        display(disp, Abstract.binaryLhs(app), leftPrior(form, infixPriority));
        disp.appendWord(Abstract.getOp(app));
        display(disp, Abstract.binaryRhs(app), rightPrior(form, infixPriority));
        paren(disp, infixPriority, priority, ")");
      } else
        display0(disp, app);
    } else if (Abstract.arity(app) == 1 && Abstract.isIdentifier(operator)) {
      String op = Abstract.getId(operator);
      PrefixOperator prefix = operators.isPrefixOperator(op, priority);
      PostfixOperator postfix = operators.isPostfixOperator(op, priority);
      IAbstract arg = Abstract.unaryArg(app);

      if (prefix != null) {
        int prefixPriority = prefix.getPriority();

        if (postfix != null) {
          int postfixPriority = postfix.getPriority();

          if (prefixPriority <= postfixPriority) {
            paren(disp, prefixPriority, priority, "(");
            disp.appendWord(op);
            display(disp, arg, rightPrior(prefix.getForm(), prefixPriority));
            paren(disp, prefixPriority, priority, ")");
          } else {
            paren(disp, postfixPriority, priority, "(");
            display(disp, arg, leftPrior(postfix.getForm(), postfixPriority));
            disp.appendWord(op);
            paren(disp, postfixPriority, priority, ")");
          }
        } else {
          paren(disp, prefixPriority, priority, "(");
          disp.appendWord(op);
          display(disp, arg, rightPrior(prefix.getForm(), prefixPriority));
          paren(disp, prefixPriority, priority, ")");
        }
      } else if (postfix != null) {
        int postfixPriority = postfix.getPriority();

        paren(disp, postfixPriority, priority, "(");
        display(disp, arg, leftPrior(postfix.getForm(), postfixPriority));
        disp.appendWord(op);
        paren(disp, postfixPriority, priority, ")");
      } else
        display0(disp, app);
    } else
      display0(disp, app);
  }

  private static void display0(PrettyPrintDisplay disp, Apply app)
  {
    display(disp, app.getOperator(), 0);
    display(disp, app.getArgs(), "(", ", ", ")", 0, 1000);
  }

  private static int leftPrior(OperatorForm form, int priority)
  {
    switch (form) {
    case infix:
    case right:
      return priority - 1;
    case left:
      return priority;
    case postfix:
      return priority - 1;
    case postfixAssociative:
      return priority;
    default:
      return 0;
    }
  }

  private static int rightPrior(OperatorForm form, int priority)
  {
    switch (form) {
    case infix:
    case left:
      return priority - 1;
    case right:
      return priority;
    case prefix:
      return priority - 1;
    case prefixAssociative:
      return priority;
    default:
      return 0;
    }
  }

  private static void paren(PrettyPrintDisplay disp, int priority, int limit, String paren)
  {
    if (priority > limit)
      disp.append(paren);
  }

  protected static void display(PrettyPrintDisplay disp, Iterable<IAbstract> seq, String preamble, String sep,
      String postamble, int indent, int priority)
  {
    disp.append(preamble);
    int mark = disp.markIndent(indent);
    String s = "";
    for (IAbstract el : seq) {
      disp.append(s);
      s = sep;
      display(disp, el, priority);
    }
    disp.popIndent(mark);
    disp.append(postamble);
  }

  protected static void display(PrettyPrintDisplay disp, IList seq, String preamble, String sep, String postamble,
      int indent, int priority)
  {
    disp.append(preamble);
    int mark = disp.markIndent(indent);
    String s = "";
    for (IValue el : seq) {
      disp.append(s);
      s = sep;
      display(disp, (IAbstract) el, priority);
    }
    disp.popIndent(mark);
    disp.append(postamble);
  }

  public static final String name = "display_quoted";

  @CafeEnter
  public static StringWrap enter(IValue value) throws EvaluationException
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    display(disp, (IAbstract) value);
    return Factory.newString(disp.toString());
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter(args[0]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.functionType(StandardTypes.astType, StandardTypes.stringType);
  }
}
