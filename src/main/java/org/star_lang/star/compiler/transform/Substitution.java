package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.star_lang.star.compiler.canonical.ExpressionTransformer;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.UndoableHash;
import org.star_lang.star.compiler.util.UndoableMap;

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
public class Substitution extends ExpressionTransformer
{
  private final UndoableMap<String, IContentExpression> substitutions;
  private final Stack<String> exclusions = new Stack<>();

  public Substitution(Dictionary cxt, UndoableMap<String, IContentExpression> substitute)
  {
    super(cxt);
    this.substitutions = substitute;

    install(new VariableTransform());
    install(new LetTermTransform());
    install(new LetActionTransform());
  }

  public Substitution(Dictionary cxt)
  {
    this(cxt, new UndoableHash<>());
  }

  public int substitutionState()
  {
    return substitutions.undoState();
  }

  public void undoSubstitutionState(int mark)
  {
    substitutions.undo(mark);
  }

  protected IContentExpression defineSubstitution(String orig, IContentExpression substitute)
  {
    if (substitute == null)
      return substitutions.remove(orig);
    else
      return substitutions.put(orig, substitute);
  }

  protected IContentExpression substitute(String orig)
  {
    return substitutions.get(orig);
  }

  protected int markSubstitutions()
  {
    return substitutions.size();
  }

  protected class VariableTransform implements TransformExpression
  {
    @Override
    public Class<? extends IContentExpression> transformClass()
    {
      return Variable.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp)
    {
      Variable var = (Variable) exp;
      String varName = var.getName();
      IContentExpression sub = substitutions.get(varName);
      if (!exclusions.contains(varName) && sub != null)
        return sub;
      else
        return exp;
    }
  }

  private class LetTermTransform implements TransformExpression
  {

    @Override
    public Class<? extends IContentExpression> transformClass()
    {
      return LetTerm.class;
    }

    @Override
    public IContentExpression transformExp(IContentExpression exp)
    {
      LetTerm let = (LetTerm) exp;
      List<IStatement> newEnv = transformEnv(let.getEnvironment());
      return new LetTerm(exp.getLoc(), transform(let.getBoundExp()), newEnv);
    }
  }

  private List<IStatement> transformEnv(List<IStatement> stmts)
  {
    int mark = exclusions.size();

    for (IStatement entry : stmts) {
      if (entry instanceof VarEntry) {
        VarEntry varEntry = (VarEntry) entry;
        for (Variable v : varEntry.getDefined())
          exclusions.push(v.getName());
      }
    }

    List<IStatement> newEnv = new ArrayList<>();
    for (IStatement entry : stmts)
      newEnv.add(transform(entry));
    exclusions.setSize(mark);
    return newEnv;
  }

  private class LetActionTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      LetAction let = (LetAction) act;
      List<IStatement> newEnv = transformEnv(let.getEnvironment());
      return new LetAction(act.getLoc(), newEnv, transform(let.getBoundAction()));
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return LetAction.class;
    }
  }
}
