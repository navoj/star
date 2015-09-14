package org.star_lang.star.compiler.cafe.compile.cont;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.data.type.Location;

/*
  * Copyright (c) 2015. Francis G. McCabe
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the
  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied. See the License for the specific language governing
  * permissions and limitations under the License.
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