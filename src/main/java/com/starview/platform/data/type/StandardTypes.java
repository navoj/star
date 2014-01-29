package com.starview.platform.data.type;

import java.util.Map.Entry;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.Intrinsics;

import com.starview.platform.data.IValue;
import com.starview.platform.data.value.NTuple;
import com.starview.platform.data.value.VoidWrap;
import com.starview.platform.data.value.StringWrap.NonStringWrapper;

/*
 * The fixed, language level, types.
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
 */
public class StandardTypes
{
  public static final String STRING = "string";
  public static final String RAW_STRING = "_string";
  public static final String NON_STRING = NonStringWrapper.name;
  public static final String CHAR = "char";
  public static final String RAW_CHAR = "_char";
  public static final String NON_CHAR = "nonChar";
  public static final String INTEGER = "integer";
  public static final String RAW_INTEGER = "_integer";
  public static final String NON_INTEGER = "nonInteger";
  public static final String LONG = "long";
  public static final String RAW_LONG = "_long";
  public static final String NON_LONG = "nonLong";
  public static final String FLOAT = "float";
  public static final String RAW_FLOAT = "_float";
  public static final String NON_FLOAT = "nonFloat";
  public static final String DECIMAL = "decimal";
  public static final String RAW_DECIMAL = "_decimal";
  public static final String NON_DECIMAL = "nonDecimal";
  public static final String BOOLEAN = "boolean";
  public static final String RAW_BOOLEAN = "_bool";
  public static final String RAW_FILE_TYPE = "_file";
  public static final String ANY = "any";
  public static final String QUOTED = "quoted";
  public static final String LIST = "list";
  public static final String RELATION = "relation";
  public static final String BINARY = "binary";
  public static final String NON_BINARY = "nonBinary";
  public static final String RAW_BINARY = "_binary";
  public static final String EXCEPTION = "exception";

  public static final IType charType = TypeUtils.typeExp(CHAR);
  public static final IType longType = TypeUtils.typeExp(LONG);
  public static final IType floatType = TypeUtils.typeExp(FLOAT);
  public static final IType integerType = TypeUtils.typeExp(INTEGER);
  public static final IType decimalType = TypeUtils.typeExp(DECIMAL);
  public static final IType stringType = TypeUtils.typeExp(STRING);
  public static final IType fileType = TypeUtils.typeExp(RAW_FILE_TYPE);
  public static final IType binaryType = TypeUtils.typeExp(BINARY);

  public static final IType rawBoolType = TypeUtils.typeExp(RAW_BOOLEAN);
  public static final IType rawCharType = TypeUtils.typeExp(RAW_CHAR);
  public static final IType rawLongType = TypeUtils.typeExp(RAW_LONG);
  public static final IType rawFloatType = TypeUtils.typeExp(RAW_FLOAT);
  public static final IType rawIntegerType = TypeUtils.typeExp(RAW_INTEGER);
  public static final IType rawStringType = TypeUtils.typeExp(RAW_STRING);
  public static final IType rawDecimalType = TypeUtils.typeExp(RAW_DECIMAL);
  public static final IType rawFileType = TypeUtils.typeExp(RAW_FILE_TYPE);
  public static final IType rawBinaryType = TypeUtils.typeExp(RAW_BINARY);

  public static final IType astType = TypeUtils.typeExp(QUOTED);
  public static final IType locationType = TypeUtils.typeExp(Location.location);

  public final static IType anyType = TypeUtils.typeExp(ANY);
  public static final IType booleanType = TypeUtils.typeExp(BOOLEAN);

  public static final IType exceptionType = TypeUtils.typeExp(EXCEPTION);

  public static final IType voidType = VoidWrap.voidType;

  public static final IType unitType = NTuple.unitType;
  public static final IValue unit = NTuple.$0Enum;

  public static void main(String[] args)
  {
    boolean eol = false;
    for (Entry<String, IType> entry : Intrinsics.standardTypes().entrySet()) {
      System.out.print(StandardNames.latexString(TypeUtils.unwrap(entry.getValue()).toString()));

      if (eol)
        System.out.println("\\\\");
      else
        System.out.print("&");

      eol = !eol;
    }
  }
}
