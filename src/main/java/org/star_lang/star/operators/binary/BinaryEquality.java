package org.star_lang.star.operators.binary;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.binary.runtime.BinaryEquals;

public class BinaryEquality
{
  public static final String binaryequal = "__binary_equal";

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(binaryequal,
        TypeUtils.functionType(StandardTypes.binaryType, StandardTypes.binaryType, StandardTypes.booleanType), BinaryEquals.class));
  }

}
