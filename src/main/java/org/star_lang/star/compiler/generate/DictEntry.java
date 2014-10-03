package org.star_lang.star.compiler.generate;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.type.BindingKind;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
public class DictEntry
{
  private final String name;
  final AccessMode access;
  private final Variable var;
  private final Location loc;
  private final BindingKind where;

  public DictEntry(String name, Variable var, Location loc, AccessMode access, BindingKind where)
  {
    this.name = name;
    this.access = access;
    this.var = var;
    this.where = where;
    this.loc = loc;
  }

  public Variable getVariable()
  {
    return var;
  }

  public String getName()
  {
    return name;
  }

  public AccessMode getAccess()
  {
    return access;
  }

  public BindingKind getBindingKind()
  {
    return where;
  }

  public IType getType()
  {
    return var.getType();
  }

  public Location getLoc()
  {
    return loc;
  }
}