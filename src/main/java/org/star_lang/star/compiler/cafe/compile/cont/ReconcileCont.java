package org.star_lang.star.compiler.cafe.compile.cont;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Types;
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

public class ReconcileCont implements IContinuation
{
  IContinuation outward;
  ISpec spec;

  public ISpec getSpec()
  {
    return spec;
  }

  public ReconcileCont(IContinuation outward)
  {
    this.outward = outward;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    if (spec == null)
      spec = src;
    else if (!src.getJavaType().equals(spec.getJavaType())) {
      ErrorReport errors = ccxt.getErrors();
      if (!src.getJavaType().equals(Types.IVALUE) && !spec.getJavaType().equals(Types.IVALUE))
        errors.reportError("returned type: " + src + " not consistent with earlier case, of type " + spec + " at "
            + loc, src.getLoc(), spec.getLoc());
    }
    return outward.cont(src, cxt, loc, ccxt);
  }

  @Override
  public boolean isJump()
  {
    return outward.isJump();
  }
}