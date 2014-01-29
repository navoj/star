package org.star_lang.star.compiler.cafe.type;

import java.util.SortedMap;

import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.RecordSpecifier;

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
@SuppressWarnings("serial")
public class CafeRecordSpecifier extends RecordSpecifier
{
  public CafeRecordSpecifier(Location loc, String label, int conIx, String javaClassName, String javaOwner,
      String javaConSig, String javaSafeName, SortedMap<String, Integer> index, IType conType)
  {
    super(loc, label, conIx, conType, javaClassName, javaConSig, javaOwner, javaSafeName);
  }

  public CafeRecordSpecifier(Location loc, String name, int conIx, SortedMap<String, Integer> index, IType conType)
  {
    super(loc, name, conIx, conType, null, null, null, null);
  }

  public CafeRecordSpecifier(Location loc, String label, String javaSafeName, int conIx,
      SortedMap<String, Integer> index, IType conType, Class<?> cafeClass, Class<?> cafeOwner)
  {
    super(loc, label, conIx, null, conType, cafeClass, cafeOwner);
  }

  @Override
  public ICafeConstructorSpecifier cleanCopy()
  {
    return new CafeRecordSpecifier(getLoc(), getLabel(), getConIx(), getJavaClassName(), getJavaOwner(),
        getJavaConSig(), getJavaSafeName(), getIndex(), getConType());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(getLabel());
    DisplayType.display(disp, TypeUtils.getConstructorArgType(TypeUtils.unwrap(getConType())));
  }
}
