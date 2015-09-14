package org.star_lang.star.operators.binary;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.binary.runtime.BinaryCoercion.Binary2String;
import org.star_lang.star.operators.binary.runtime.BinaryCoercion.String2Binary;

public abstract class BinaryCoercion
{
  public static final String binary2String = "__binary_string";
  public static final String string2binary = "__string_binary";

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(binary2String, TypeUtils.functionType(StandardTypes.binaryType,StandardTypes.stringType), Binary2String.class));
    cxt.declareBuiltin(new Builtin(string2binary, TypeUtils.functionType(StandardTypes.stringType ,StandardTypes.binaryType), String2Binary.class));
  }
}
