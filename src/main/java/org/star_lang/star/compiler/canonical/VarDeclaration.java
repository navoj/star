package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.assignment.runtime.RefCell;

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
public class VarDeclaration extends Action
{
  private final IContentPattern pattern;
  private final IContentExpression value;
  private final AccessMode readOnly;

  public VarDeclaration(Location loc, IContentPattern ptn, AccessMode readOnly, IContentExpression value)
  {
    super(loc, StandardTypes.unitType);
    this.pattern = ptn;
    this.readOnly = readOnly;
    this.value = value;
  }

  public IContentExpression getValue()
  {
    return value;
  }

  public AccessMode isReadOnly()
  {
    return readOnly;
  }

  public IContentPattern getPattern()
  {
    return pattern;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    pattern.prettyPrint(disp);
    disp.append(" has type ");
    DisplayType.display(disp, pattern.getType());
    disp.append(";\n");
    if (readOnly == AccessMode.readOnly) {
      pattern.prettyPrint(disp);
      disp.appendWord(StandardNames.IS);
    } else {
      disp.append("var ");
      pattern.prettyPrint(disp);
      disp.append(" := ");
    }
    value.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitVarDeclaration(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitVarDeclaration(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformVarDeclaration(this, context);
  }

  // Factory convenience method for re-assignable variable declaration
  public static VarDeclaration varDecl(Location loc, Variable var, IContentExpression value)
  {
    return new VarDeclaration(loc, var, AccessMode.readWrite, new ConstructorTerm(loc,
        RefCell.cellLabel(var.getType()), var.getType(), value));
  }
}
