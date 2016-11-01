package org.star_lang.star.compiler.ast;

import java.util.Collection;
import java.util.regex.Pattern;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.operator.OpFormAttribute;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;

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

public class Display implements IAbstractVisitor {
  protected PrettyPrintDisplay disp;

  protected Display(PrettyPrintDisplay disp) {
    this.disp = disp;
  }

  public static void display(PrettyPrintDisplay disp, IAbstract term) {
    Display display = new Display(disp);
    term.accept(display);
  }

  public static String display(IAbstract term) {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    Display display = new Display(disp);
    term.accept(display);
    return disp.toString();
  }

  public static void display(PrettyPrintDisplay disp, Collection<IAbstract> terms, String sep) {
    Display display = new Display(disp);
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
  public void visitTuple(AsTuple tpl) {
    String brks = tpl.getBrackets().getLit();
    int ln = brks.length();
    display(tpl.getArgs(), brks.substring(0, ln / 2), ", ", brks.substring(ln / 2));
  }

  @Override
  public void visitApply(AApply app) {
    if (CompilerUtils.isBraceTerm(app)) {
      CompilerUtils.braceLabel(app).accept(this);
      display(CompilerUtils.unWrap(CompilerUtils.braceArg(app)), "{ ", ";\n", "\n}", 2);
    } else if (CompilerUtils.isAnonAggConLiteral(app))
      display(CompilerUtils.unWrap(CompilerUtils.anonAggEls(app)), "{ ", ";\n", "\n}", 2);
    else if (CompilerUtils.isSquareTerm(app)) {
      CompilerUtils.squareLabel(app).accept(this);
      append("[");
      IAbstract content = CompilerUtils.squareArg(app);
      if (content != null)
        content.accept(this);
      append("]");
    } else if (CompilerUtils.isSquareSequenceTerm(app)) {
      append("[");
      IAbstract content = CompilerUtils.squareContent(app);
      if (content != null)
        content.accept(this);
      append("]");
    } else if (Abstract.isTupleTerm(app))
      display(app.getArgs(), " (", ", ", ")");
    else if (app.hasAttribute(OpFormAttribute.name)) {
      OpFormAttribute opForm = (OpFormAttribute) app.getAttribute(OpFormAttribute.name);
      switch (opForm.getForm()) {
      case infix:
        assert Abstract.isBinary(app);
        Abstract.binaryLhs(app).accept(this);
        appendWord(Abstract.getOp(app));
        Abstract.binaryRhs(app).accept(this);
        return;
      default:
      case none:
        app.getOperator().accept(this);
        display(app.getArgs(), "(", ", ", ")");
        return;
      case prefix:
        assert Abstract.isUnary(app);
        appendWord(Abstract.getOp(app));
        Abstract.unaryArg(app).accept(this);
        return;
      case postfix:
        assert Abstract.isUnary(app);
        Abstract.unaryArg(app).accept(this);
        appendWord(Abstract.getOp(app));
      }
    } else {
      app.getOperator().accept(this);
      display(app.getArgs(), "(", ", ", ")");
    }
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit) {
    if (lit.getLit())
      appendWord(Names.TRUE);
    else
      appendWord(Names.FALSE);
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

  protected void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble) {
    disp.append(preamble);
    String s = "";
    for (IAbstract el : seq) {
      disp.append(s);
      s = sep;
      el.accept(this);
    }
    disp.append(postamble);
  }

  protected void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble, int indent) {
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
  }

  protected void display(IList seq, String preamble, String sep, String postamble) {
    disp.append(preamble);
    String s = "";
    for (IValue el : seq) {
      disp.append(s);
      s = sep;
      ((IAbstract) el).accept(this);
    }
    disp.append(postamble);
  }

  protected void display(IList seq, String preamble, String sep, String postamble, int indent) {
    disp.append(preamble);
    int mark = disp.markIndent(indent);
    String s = "";
    for (IValue el : seq) {
      disp.append(s);
      s = sep;
      ((IAbstract) el).accept(this);
    }
    disp.popIndent(mark);
    disp.append(postamble);
  }

  protected void appendWord(String str) {
    disp.appendWord(str);
  }

  protected void appendName(String str) {
    if (StandardNames.SQUARE.equals(str) || StandardNames.BRACES.equals(str))
      appendWord(str);
    else if (!Pattern.matches("[a-zA-Z_#$.@][a-zA-Z_#$.@0-9]*", str)) {
      disp.append("'");
      disp.append(str);
      disp.append("'");
    } else
      appendWord(str);
  }

  protected void append(String str) {
    disp.append(str);
  }
}
