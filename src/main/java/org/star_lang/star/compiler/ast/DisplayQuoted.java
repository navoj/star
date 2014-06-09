package org.star_lang.star.compiler.ast;

import java.util.List;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.operator.Operator;
import org.star_lang.star.compiler.operator.Operators;
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
import com.starview.platform.data.value.BoolWrap.FalseValue;
import com.starview.platform.data.value.BoolWrap.TrueValue;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.StringWrap;

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
public class DisplayQuoted implements IAbstractVisitor, IFunction
{
  protected PrettyPrintDisplay disp;
  private int priority = Operators.STATEMENT_PRIORITY;
  private final Operators operators = Operators.operatorRoot();

  protected DisplayQuoted(PrettyPrintDisplay disp)
  {
    this.disp = disp;
  }

  public static void display(PrettyPrintDisplay disp, IAbstract term)
  {
    DisplayQuoted display = new DisplayQuoted(disp);
    term.accept(display);
  }

  public static String display(IAbstract term)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    DisplayQuoted display = new DisplayQuoted(disp);
    while (Abstract.isBinary(term, StandardNames.TERM)) {
      Abstract.binaryLhs(term).accept(display);
      disp.append(";\n");
      term = Abstract.binaryRhs(term);
    }
    term.accept(display);
    return disp.toString();
  }

  public static void display(PrettyPrintDisplay disp, List<IAbstract> terms, String sep)
  {
    DisplayQuoted display = new DisplayQuoted(disp);
    String sp = "";
    for (IAbstract term : terms) {
      disp.append(sp);
      sp = sep;
      term.accept(display);
    }
  }

  @Override
  public String toString()
  {
    return disp.toString();
  }

  @Override
  public void visitApply(Apply app)
  {
    if (CompilerUtils.isBraceTerm(app)) {
      CompilerUtils.braceLabel(app).accept(this);
      display(CompilerUtils.unWrap(CompilerUtils.braceArg(app), StandardNames.TERM), "{ ", ";\n", "\n}", 2,
          Operators.STATEMENT_PRIORITY);
    } else if (CompilerUtils.isAnonAggConLiteral(app))
      display(CompilerUtils.unWrap(CompilerUtils.anonAggEls(app), StandardNames.TERM), "{ ", ";\n", "\n}", 2,
          Operators.STATEMENT_PRIORITY);
    else if (CompilerUtils.isSquareTerm(app)) {
      CompilerUtils.squareLabel(app).accept(this);
      append("[");
      IAbstract content = CompilerUtils.squareArg(app);
      if (content != null)
        content.accept(this);
      append("]");
    }
    // else if (Abstract.isTupleTerm(app, 1) && Abstract.getArg(app, 0) instanceof Name)
    // Abstract.getArg(app, 0).accept(this);
    else if (Abstract.isTupleTerm(app))
      display(app.getArgs(), " (", ", ", ")");
    else if (Abstract.isBinary(app) && app.getOperator() instanceof Name) {
      String op = Abstract.roundTermName(app);
      Operator infix = operators.isInfixOperator(op, priority);
      if (infix != null) {
        paren(infix.getPriority(), "#(");
        int priority = this.priority;

        this.priority = infix.leftPriority();
        Abstract.binaryLhs(app).accept(this);
        disp.appendWord(op);
        this.priority = infix.rightPriority();
        Abstract.binaryRhs(app).accept(this);
        this.priority = priority;
        paren(infix.getPriority(), ")#");
      } else
        displayApp(app);
    } else if (Abstract.isUnary(app) && ((Apply) app).getOperator() instanceof Name) {
      String op = Abstract.roundTermName(app);

      Operator prefix = operators.isPrefixOperator(op, priority);
      Operator postfix = operators.isPostfixOperator(op, priority);

      if (prefix != null) {
        if (postfix != null) {
          if (prefix.getPriority() <= postfix.getPriority()) {
            int priority = this.priority;

            paren(prefix.getPriority(), "#(");

            this.priority = prefix.rightPriority();
            disp.appendWord(op);
            Abstract.unaryArg(app).accept(this);
            this.priority = priority;
            paren(prefix.getPriority(), ")#");
          } else {
            int priority = this.priority;
            paren(postfix.getPriority(), "#(");

            Abstract.unaryArg(app).accept(this);
            this.priority = postfix.leftPriority();
            disp.appendWord(op);
            this.priority = priority;
            paren(postfix.getPriority(), ")#");
          }
        } else {
          int priority = this.priority;
          paren(prefix.getPriority(), "#(");
          this.priority = prefix.rightPriority();
          disp.appendWord(op);
          Abstract.unaryArg(app).accept(this);
          this.priority = priority;
          paren(prefix.getPriority(), ")#");
        }
      } else if (postfix != null) {
        int priority = this.priority;
        paren(postfix.getPriority(), "#(");

        Abstract.unaryArg(app).accept(this);
        this.priority = postfix.leftPriority();
        disp.appendWord(op);
        this.priority = priority;
        paren(postfix.getPriority(), ")#");
      } else
        displayApp(app);
    } else
      displayApp(app);
  }

  private void paren(int priority, String paren)
  {
    if (priority > this.priority)
      disp.append(paren);
  }

  private void displayApp(Apply app)
  {
    app.getOperator().accept(this);
    display(app.getArgs(), "(", ", ", ")");
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit)
  {
    if (lit.getLit())
      appendWord(TrueValue.name);
    else
      appendWord(FalseValue.name);
  }

  @Override
  public void visitCharLiteral(CharLiteral lit)
  {
    disp.append("'");
    appendChar(lit.getLit());
    disp.append("'");
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt)
  {
    disp.append(flt.getLit());
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit)
  {
    disp.append(lit.getLit().toString());
    disp.append("a");
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit)
  {
    disp.appendWord(lit.getLit());
  }

  @Override
  public void visitLongLiteral(LongLiteral lit)
  {
    disp.appendWord(lit.getLit());
  }

  @Override
  public void visitName(Name name)
  {
    appendName(name.getId());
  }

  @Override
  public void visitStringLiteral(StringLiteral str)
  {
    disp.append(StringUtils.quoteString(str.getLit()));
  }

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
    return TypeUtils.functionType(StandardTypes.astType, StandardTypes.stringType);
  }

  protected void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble)
  {
    int priority = this.priority;
    this.priority = Operators.EXPRESSION_PRIORITY - 1;
    disp.append(preamble);
    String s = "";
    for (IAbstract el : seq) {
      disp.append(s);
      s = sep;
      el.accept(this);
    }
    disp.append(postamble);
    this.priority = priority;
  }

  private void display(IList seq, String preamble, String sep, String postamble)
  {
    int priority = this.priority;
    this.priority = Operators.EXPRESSION_PRIORITY - 1;
    disp.append(preamble);
    String s = "";
    for (IValue el : seq) {
      disp.append(s);
      s = sep;
      ((IAbstract) el).accept(this);
    }
    disp.append(postamble);
    this.priority = priority;
  }

  private void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble, int indent, int priority)
  {
    int currPriority = this.priority;
    this.priority = priority;
    disp.append(preamble);
    int mark = disp.markIndent(indent);
    String s = "";
    for (IAbstract el : seq) {
      disp.append(s);
      s = sep;
      el.accept(this);
    }
    disp.popIndent(mark);
    disp.append(postamble);
    this.priority = currPriority;
  }

  protected void appendWord(String str)
  {
    disp.appendWord(str);
  }

  protected void appendName(String str)
  {
    if (/* StandardNames.isKeyword(str) || */operators.isOperator(str, priority)) {
      disp.append("(");
      disp.appendWord(str);
      disp.append(")");
    } else
      appendWord(str);
  }

  protected void append(String str)
  {
    disp.append(str);
  }

  private void appendChar(int ch)
  {
    disp.appendChar(ch);
  }
}
