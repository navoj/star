package org.star_lang.star.compiler.cafe.compile;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

public interface Definer
{
  VarInfo declareArg(Location loc, String name, int varOffset, IType varType, CafeDictionary dict, AccessMode access,
      boolean isInited, ErrorReport errors);
}