package org.star_lang.star.compiler.format;

import java.util.Collection;
import java.util.List;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.BooleanLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.format.rules.FmtProgram;
import org.star_lang.star.compiler.format.rules.FmtRule;
import org.star_lang.star.data.IValue;

final class FormatVisitor implements IAbstractVisitor {
  private final FmtProgram fmtRules;
  private final FormatRanges formats;

  FormatVisitor(FmtProgram fmtRules, FormatRanges formats) {
    this.fmtRules = fmtRules;
    this.formats = formats;
  }

  @Override
  public void visitApply(Apply term) {
    visit(term);
    term.getOperator().accept(this);
    for (IValue arg : term.getArgs())
      ((IAbstract) arg).accept(this);
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit) {
    visit(lit);
  }

  @Override
  public void visitFloatLiteral(FloatLiteral lit) {
    visit(lit);
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit) {
    visit(lit);
  }

  @Override
  public void visitLongLiteral(LongLiteral lit) {
    visit(lit);
  }

  @Override
  public void visitName(Name name) {
    visit(name);
  }

  @Override
  public void visitStringLiteral(StringLiteral str) {
    visit(str);
  }

  private void visit(IAbstract term) {
    List<String> categories = term.getCategories();
    if (categories != null) {
      for (String category : categories) {
        Collection<FmtRule> rules = fmtRules.rulesFor(category);

        if (rules != null)
          for (FmtRule rule : rules)
            rule.applyRule(term, formats);
      }
    }
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit) {
    visit(lit);
  }
}