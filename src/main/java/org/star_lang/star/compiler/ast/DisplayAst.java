package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.operator.Operator;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.StringWrap;
import org.star_lang.star.data.value.BoolWrap.FalseValue;
import org.star_lang.star.data.value.BoolWrap.TrueValue;
import org.star_lang.star.operators.CafeEnter;


/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class DisplayAst implements IFunction {
  private final static Operators operators = Operators.operatorRoot();

  public static void display(PrettyPrintDisplay disp, IAbstract term) {
    display(disp, term, 2000);
  }

  public static String display(IAbstract term) {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    display(disp, term, 2000);
    return disp.toString();
  }

  protected static void display(PrettyPrintDisplay disp, IAbstract term, int priority) {
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

  protected static void displayApply(PrettyPrintDisplay disp, Apply app, int priority) {
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
    } else if (CompilerUtils.isSquareSequenceTerm(app)) {
      display(disp, CompilerUtils.unWrap(CompilerUtils.squareContent(app), StandardNames.COMMA), "[", ", ", "]", 2,
          1100);
    } else if (Abstract.isTupleTerm(app))
      display(disp, app.getArgs(), " (", ", ", ")", 0, 1000);
    else if (Abstract.arity(app) == 2 && Abstract.isIdentifier(operator)) {
      String op = Abstract.getId(operator);
      Operator infix = operators.isInfixOperator(op, priority);
      if (infix != null) {
        int infixPriority = infix.getPriority();
        paren(disp, infixPriority, priority, "(");

        display(disp, Abstract.binaryLhs(app), infix.leftPriority());
        disp.appendWord(Abstract.getOp(app));
        display(disp, Abstract.binaryRhs(app), infix.rightPriority());
        paren(disp, infixPriority, priority, ")");
      } else
        display0(disp, app);
    } else if (Abstract.arity(app) == 1 && Abstract.isIdentifier(operator)) {
      String op = Abstract.getId(operator);
      Operator prefix = operators.isPrefixOperator(op, priority);
      Operator postfix = operators.isPostfixOperator(op, priority);
      IAbstract arg = Abstract.unaryArg(app);

      if (prefix != null) {
        int prefixPriority = prefix.getPriority();

        if (postfix != null) {
          int postfixPriority = postfix.getPriority();

          if (prefixPriority <= postfixPriority) {
            paren(disp, prefixPriority, priority, "(");
            disp.appendWord(op);
            display(disp, arg, prefix.rightPriority());
            paren(disp, prefixPriority, priority, ")");
          } else {
            paren(disp, postfixPriority, priority, "(");
            display(disp, arg, postfix.leftPriority());
            disp.appendWord(op);
            paren(disp, postfixPriority, priority, ")");
          }
        } else {
          paren(disp, prefixPriority, priority, "(");
          disp.appendWord(op);
          disp.space();
          display(disp, arg, prefix.rightPriority());
          paren(disp, prefixPriority, priority, ")");
        }
      } else if (postfix != null) {
        int postfixPriority = postfix.getPriority();

        paren(disp, postfixPriority, priority, "(");
        display(disp, arg, postfix.leftPriority());
        disp.appendWord(op);
        paren(disp, postfixPriority, priority, ")");
      } else
        display0(disp, app);
    } else
      display0(disp, app);
  }

  private static void display0(PrettyPrintDisplay disp, Apply app) {
    display(disp, app.getOperator(), 0);
    display(disp, app.getArgs(), "(", ", ", ")", 0, 1000);
  }

  private static void paren(PrettyPrintDisplay disp, int priority, int limit, String paren) {
    if (priority > limit)
      disp.append(paren);
  }

  protected static void display(PrettyPrintDisplay disp, Iterable<IAbstract> seq, String preamble, String sep,
                                String postamble, int indent, int priority) {
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
                                int indent, int priority) {
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
  public static StringWrap enter(IValue value) throws EvaluationException {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    display(disp, (IAbstract) value);
    return Factory.newString(disp.toString());
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException {
    return enter(args[0]);
  }

  @Override
  public IType getType() {
    return type();
  }

  public static IType type() {
    return TypeUtils.functionType(StandardTypes.astType, StandardTypes.stringType);
  }
}
