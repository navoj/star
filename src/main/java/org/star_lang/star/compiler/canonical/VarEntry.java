/**
 * 
 */
package org.star_lang.star.compiler.canonical;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.assignment.runtime.RefCell;

/*
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
 */
/**
 * Description of a variable binding
 * 
 * @author fgm
 * 
 */
@SuppressWarnings("serial")
public class VarEntry extends EnvironmentEntry
{
  private final AccessMode access;
  private final Collection<Variable> defined;
  private final Map<String, IType> types;
  private final IContentPattern varPattern;
  private final IContentExpression value;

  public VarEntry(Collection<Variable> defined, Location loc, IContentPattern ptn, IContentExpression value,
      AccessMode access, Visibility visibility)
  {
    super(loc, visibility);
    this.defined = defined;
    this.varPattern = ptn;
    this.value = value;
    this.access = access;
    this.types = new HashMap<>();
  }

  public VarEntry(Location loc, IContentPattern ptn, IContentExpression value, AccessMode access, Visibility visibility)
  {
    this(findDefinedVars(ptn), loc, ptn, value, access, visibility);
  }

  public IType getType()
  {
    return varPattern.getType();
  }

  public AccessMode isReadOnly()
  {
    return access;
  }

  public IContentExpression getValue()
  {
    return value;
  }

  public IContentPattern getVarPattern()
  {
    return varPattern;
  }

  public Variable getVariable()
  {
    return (Variable) varPattern;
  }

  public Collection<Variable> getDefined()
  {
    return defined;
  }

  @Override
  public boolean defines(String name)
  {
    for (Variable v : defined)
      if (v.getName().equals(name))
        return true;
    return false;
  }

  @Override
  public Collection<String> definedFields()
  {
    Set<String> fields = new HashSet<>();
    for (Variable v : defined)
      fields.add(v.getName());
    return fields;
  }

  @Override
  public Collection<String> definedTypes()
  {
    return types.keySet();
  }

  private void prettyPrintDefinedNames(PrettyPrintDisplay disp)
  {
    varPattern.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitVarEntry(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (getVisibility() == Visibility.priVate)
      disp.appendWord(StandardNames.PRIVATE);
    varPattern.prettyPrint(disp);
    disp.append(" has type ");
    DisplayType.display(disp, getType());
    disp.append(";\n");
    if (!showAsProgram()) {
      prettyPrintDefinedNames(disp);
      if (!TypeUtils.isReferenceType(getType()))
        disp.appendWord(StandardNames.IS);
      else
        disp.append(":=");
    }
    value.prettyPrint(disp);
  }

  private boolean showAsProgram()
  {
    if (value instanceof ProgramLiteral) {
      String name = ((ProgramLiteral) value).getName();
      if (varPattern instanceof Variable && ((Variable) varPattern).getName().equals(name))
        return true;
    }
    return false;
  }

  public static VarEntry createVarEntry(String name, IType type, Location loc, IContentExpression value,
      Visibility visibility)
  {
    return new VarEntry(loc, Variable.create(loc, type, name), value, AccessMode.readOnly, visibility);
  }

  public static VarEntry createVarEntry(Location loc, IContentPattern varPattern, IContentExpression value,
      AccessMode access, Visibility visibility)
  {
    return new VarEntry(loc, varPattern, value, access, visibility);
  }

  public static VarEntry createReassignableVarEntry(Location loc, IContentPattern var, IContentExpression value,
      Variable[] freeVars, Visibility visibility)
  {
    return new VarEntry(loc, var, new ConstructorTerm(loc, RefCell.cellLabel(var.getType()), var.getType(), value),
        AccessMode.readWrite, visibility);
  }

  private static Collection<Variable> findDefinedVars(IContentPattern ptn)
  {
    Collection<Variable> defined = new HashSet<>();
    findDefined(ptn, defined);
    return defined;
  }

  private static void findDefined(IContentPattern ptn, Collection<Variable> defined)
  {
    if (ptn instanceof Variable) {
      Variable var = (Variable) ptn;
      if (!defined.contains(var))
        defined.add(var);
    } else if (ptn instanceof ConstructorPtn) {
      ConstructorPtn posCon = (ConstructorPtn) ptn;
      for (IContentPattern arg : posCon.getElements())
        findDefined(arg, defined);
    } else if (ptn instanceof RecordPtn) {
      RecordPtn record = (RecordPtn) ptn;
      for (Entry<String, IContentPattern> entry : record.getElements().entrySet())
        findDefined(entry.getValue(), defined);
    } else if (!(ptn instanceof VoidExp))
      throw new IllegalArgumentException("cannot find vars in " + ptn);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformVarEntry(this, context);
  }
}