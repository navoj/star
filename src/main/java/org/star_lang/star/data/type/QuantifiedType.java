package org.star_lang.star.data.type;

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
public abstract class QuantifiedType extends AbstractType
{
  protected final TypeVar boundVar;
  protected final IType boundType;

  public QuantifiedType(String label, TypeVar boundVar, IType boundType)
  {
    super(boundType.typeLabel(), boundType.kind());
    this.boundVar = boundVar;
    this.boundType = boundType;
  }

  public TypeVar getBoundVar()
  {
    return boundVar;
  }

  public IType getBoundType()
  {
    return boundType;
  }
}