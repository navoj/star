package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * A TypeContract is a specification of the set of functions that are available for a given type.
 * 
 * Type contracts may be realized by any type. Some contracts are built into StarRules, other
 * contracts are introduced either implicitly or explicitly.
 * 
 * Type contracts are structured so that both OO-style interfaces and non OO-style contracts can be
 * represented within the same framweork.
 * 
 * A type contract has one or more <emph>bound types</emph> that denotes the actual concrete type.
 * For an OO-style interface, the bound type variable denotes the equivalent of <code>this</code>.
 * 
 * Apart from member methods, a type contract is also associated with zero or more super-contracts
 * and zero <emph>dependencies</emph>.
 * 
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
public class TypeContract implements PrettyPrintable
{
  private final Location loc;
  private final String name;
  private final IAlgebraicType contractType;

  // this contract
  public TypeContract(Location loc, String name, IAlgebraicType contractType)
  {
    this.loc = loc;
    this.name = name;
    this.contractType = contractType;
  }

  public IAlgebraicType getContractType()
  {
    return contractType;
  }

  public String getName()
  {
    return name;
  }

  public int getArity()
  {
    return contractType.typeArity();
  }

  public Location getLoc()
  {
    return loc;
  }

  public TypeInterface contractFunctions()
  {
    IValueSpecifier spec = contractType.getValueSpecifier(name);
    if (spec instanceof RecordSpecifier)
      return ((RecordSpecifier) spec).getTypeInterface();
    else
      return null;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mrk = disp.markIndent();
    DisplayType displayType = new DisplayType(disp);

    IValueSpecifier spec = contractType.getValueSpecifier(name);
    IType conType = spec.getConType();

    disp.appendWord(AbstractType.CONTRACT);
    disp.appendChar(' ');
    DisplayType.displayContract(disp, TypeUtils.getConstructorResultType(conType));

    disp.append(" is ");
    if (spec instanceof RecordSpecifier)
      ((TypeInterfaceType) (((RecordSpecifier) spec).getTypeInterface())).accept(displayType, null);

    disp.popIndent(mrk);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
