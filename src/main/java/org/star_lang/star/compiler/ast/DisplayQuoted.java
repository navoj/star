package org.star_lang.star.compiler.ast;

import java.util.List;

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

public class DisplayQuoted implements IAbstractVisitor, IFunction {
  protected PrettyPrintDisplay disp;
  private int priority = Operators.STATEMENT_PRIORITY;
  private final Operators operators = Operators.operatorRoot();

  private DisplayQuoted(PrettyPrintDisplay disp) {
    this.disp = disp;
  }

  public static void display(PrettyPrintDisplay disp, IAbstract term) {
    DisplayQuoted display = new DisplayQuoted(disp);
    term.accept(display);
  }

  public static String display(IAbstract term) {
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

  public static void display(PrettyPrintDisplay disp, List<IAbstract> terms, String sep) {
    DisplayQuoted display = new DisplayQuoted(disp);
    String sp = "";
    for (IAbstract term : terms) {
      disp.append(sp);
      sp = sep;
      term.accept(display);
    }
  }

  @Override
  public String toString() {
    return disp.toString();
  }

  @Override
  public void visitApply(Apply app) {
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
    } else if (Abstract.isUnary(app) && app.getOperator() instanceof Name) {
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

  private void paren(int priority, String paren) {
    if (priority > this.priority)
      disp.append(paren);
  }

  private void displayApp(Apply app) {
    app.getOperator().accept(this);
    display(app.getArgs(), "(", ", ", ")");
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit) {
    if (lit.getLit())
      appendWord(TrueValue.name);
    else
      appendWord(FalseValue.name);
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt) {
    disp.append(flt.getLit());
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit) {
    disp.appendWord(lit.getLit());
  }

  @Override
  public void visitLongLiteral(LongLiteral lit) {
    disp.appendWord(lit.getLit());
  }

  @Override
  public void visitName(Name name) {
    appendName(name.getId());
  }

  @Override
  public void visitStringLiteral(StringLiteral str) {
    disp.append(StringUtils.quoteString(str.getLit()));
  }

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
    return TypeUtils.functionType(StandardTypes.astType, StandardTypes.stringType);
  }

  protected void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble) {
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

  private void display(IList seq, String preamble, String sep, String postamble) {
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

  private void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble, int indent, int priority) {
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

  protected void appendWord(String str) {
    disp.appendWord(str);
  }

  private void appendName(String str) {
    if (/* StandardNames.isKeyword(str) || */operators.isOperator(str, priority)) {
      disp.append("(");
      disp.appendWord(str);
      disp.append(")");
    } else
      appendWord(str);
  }

  protected void append(String str) {
    disp.append(str);
  }
}
