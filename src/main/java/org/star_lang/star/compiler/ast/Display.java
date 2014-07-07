package org.star_lang.star.compiler.ast;

import java.util.Collection;
import java.util.regex.Pattern;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.operator.OpFormAttribute;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;

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
public class Display implements IAbstractVisitor
{
  protected PrettyPrintDisplay disp;

  protected Display(PrettyPrintDisplay disp)
  {
    this.disp = disp;
  }

  public static void display(PrettyPrintDisplay disp, IAbstract term)
  {
    Display display = new Display(disp);
    term.accept(display);
  }

  public static String display(IAbstract term)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    Display display = new Display(disp);
    term.accept(display);
    return disp.toString();
  }

  public static void display(PrettyPrintDisplay disp, Collection<IAbstract> terms, String sep)
  {
    Display display = new Display(disp);
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
        return;
      }
    } else {
      app.getOperator().accept(this);
      display(app.getArgs(), "(", ", ", ")");
    }
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit)
  {
    if (lit.getLit())
      appendWord(Names.TRUE);
    else
      appendWord(Names.FALSE);
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

  protected void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble)
  {
    disp.append(preamble);
    String s = "";
    for (IAbstract el : seq) {
      disp.append(s);
      s = sep;
      el.accept(this);
    }
    disp.append(postamble);
  }

  protected void display(Iterable<IAbstract> seq, String preamble, String sep, String postamble, int indent)
  {
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

  protected void display(IList seq, String preamble, String sep, String postamble)
  {
    disp.append(preamble);
    String s = "";
    for (IValue el : seq) {
      disp.append(s);
      s = sep;
      ((IAbstract) el).accept(this);
    }
    disp.append(postamble);
  }

  protected void display(IList seq, String preamble, String sep, String postamble, int indent)
  {
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

  protected void appendWord(String str)
  {
    disp.appendWord(str);
  }

  protected void appendName(String str)
  {
    if (!Pattern.matches("[a-zA-Z_#$.@][a-zA-Z_#$.@0-9]*", str)) {
      disp.append("'");
      disp.append(str);
      disp.append("'");
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
