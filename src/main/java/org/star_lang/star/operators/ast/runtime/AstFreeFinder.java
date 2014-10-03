package org.star_lang.star.operators.ast.runtime;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.DefaultAbstractVisitor;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.data.IValue;

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
final class AstFreeFinder extends DefaultAbstractVisitor
{
  private final Set<Name> found;
  private final Stack<Set<Name>> excludes = new Stack<Set<Name>>();

  AstFreeFinder(Set<Name> found, Set<Name> excludes)
  {
    this.found = found;
    this.excludes.push(excludes);
  }

  @Override
  public void visitApply(Apply app)
  {
    if (CompilerUtils.isQuoted(app)) {

    } else if (CompilerUtils.isAnonAggConLiteral(app)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.anonAggEls(app))) {
        if (Abstract.isBinary(el, StandardNames.EQUAL) || Abstract.isBinary(el, StandardNames.ASSIGN))
          Abstract.binaryRhs(el).accept(this);
      }
    } else if (CompilerUtils.isBraceTerm(app)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(app))) {
        if (Abstract.isBinary(el, StandardNames.EQUAL) || Abstract.isBinary(el, StandardNames.ASSIGN))
          Abstract.binaryRhs(el).accept(this);
      }
    } else if (CompilerUtils.isSequenceTerm(app)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.sequenceContent(app)))
        el.accept(this);
    } else if (CompilerUtils.isQueryTerm(app)) {
      CompilerUtils.queryQuantifier(app).accept(this);
      CompilerUtils.queryCondition(app).accept(this);
      IAbstract orderBy = CompilerUtils.queryOrderBy(app);
      if (orderBy != null)
        orderBy.accept(this);
      IAbstract using = CompilerUtils.queryOrderUsing(app);
      if (using != null)
        using.accept(this);
    } else if (CompilerUtils.isFieldAccess(app))
      CompilerUtils.fieldRecord(app).accept(this);
    else if (CompilerUtils.isLambdaExp(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.lambdaPtn(app));
      excludes.push(ptnExclusions);
      CompilerUtils.lambdaExp(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isEquation(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.equationLhs(app));
      excludes.push(ptnExclusions);
      CompilerUtils.equationRhs(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isActionRule(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.actionRuleLhs(app));
      excludes.push(ptnExclusions);
      CompilerUtils.actionRuleBody(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isPatternRule(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.patternRuleBody(app));
      excludes.push(ptnExclusions);
      CompilerUtils.patternRuleHead(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isLetTerm(app)) {
      Set<Name> letExcludes = excludeDefs(CompilerUtils.letDefs(app));
      excludes.push(letExcludes);
      CompilerUtils.letBound(app).accept(this);
      excludes.pop();
    } else {
      for (IValue arg : app.getArgs())
        ((IAbstract) arg).accept(this);
    }
  }

  @Override
  public void visitName(Name name)
  {
    if (!excludes.peek().contains(name) && !StandardNames.isKeyword(name) && !StandardNames.isStandard(name))
      found.add(name);
  }

  private Set<Name> exclude(IAbstract term)
  {
    Set<Name> exclusions = new HashSet<Name>(excludes.peek());
    AstFreeFinder finder = new AstFreeFinder(exclusions, found);
    term.accept(finder);
    return exclusions;
  }

  private Set<Name> excludeDefs(IAbstract term)
  {
    Set<Name> exclusions = new HashSet<Name>(excludes.peek());
    AstFreeFinder finder = new AstFreeFinder(exclusions, found);

    for (IAbstract stmt : CompilerUtils.unWrap(term)) {
      if (CompilerUtils.isEquation(stmt)) {
        exclusions.add(CompilerUtils.functionName(stmt));
        CompilerUtils.equationLhs(stmt).accept(finder);
      } else if (CompilerUtils.isActionRule(stmt)) {
        exclusions.add(CompilerUtils.procedureName(stmt));
        CompilerUtils.actionRuleLhs(stmt).accept(finder);
      } else if (CompilerUtils.isPatternRule(stmt)) {
        exclusions.add(CompilerUtils.patternName(stmt));
        CompilerUtils.patternRuleBody(stmt).accept(finder);
      }
    }
    return exclusions;
  }
}