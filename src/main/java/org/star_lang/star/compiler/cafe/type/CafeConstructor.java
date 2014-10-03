package org.star_lang.star.compiler.cafe.type;

import org.star_lang.star.data.type.ConstructorSpecifier;
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
@SuppressWarnings("serial")
public class CafeConstructor extends ConstructorSpecifier
{
  public CafeConstructor(Location loc, String label, int conIx, IType conType, Class<?> cafeClass, Class<?> cafeOwner)
  {
    super(loc, null, label, conIx, conType, cafeClass, cafeOwner);
  }

  public CafeConstructor(Location loc, String label, int conIx, IType conType, String javaSafeName,
      String javaClassName, String javaConSig, String javaOwner)
  {
    super(loc, label, conIx, null, conType, javaSafeName, javaClassName, javaConSig, javaOwner);
  }
}
