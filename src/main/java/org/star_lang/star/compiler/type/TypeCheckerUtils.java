package org.star_lang.star.compiler.type;

import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.data.type.*;
import org.star_lang.star.data.value.Factory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

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

public class TypeCheckerUtils {

  public static IContentExpression integerLiteral(Location loc, int ix) {
    return new ConstructorTerm(loc, StandardTypes.INTEGER, StandardTypes.integerType, new Scalar(loc,
        StandardTypes.integerType, Factory.newInt(ix)));
  }

  public static IContentPattern integerPtn(Location loc, int ix) {
    return new ConstructorPtn(loc, StandardTypes.INTEGER, StandardTypes.integerType, new ScalarPtn(loc,
        StandardTypes.rawIntegerType, Factory.newInt(ix)));
  }

  public static IContentExpression longLiteral(Location loc, long lx) {
    return new ConstructorTerm(loc, StandardTypes.LONG, StandardTypes.longType, new Scalar(loc, StandardTypes.longType,
        Factory.newLong(lx)));
  }

  public static IContentPattern longPtn(Location loc, long ix) {
    return new ConstructorPtn(loc, StandardTypes.LONG, StandardTypes.longType, new ScalarPtn(loc,
        StandardTypes.rawLongType, Factory.newLong(ix)));
  }

  public static IContentExpression floatLiteral(Location loc, double dx) {
    return new ConstructorTerm(loc, StandardTypes.FLOAT, StandardTypes.floatType, new Scalar(loc,
        StandardTypes.floatType, Factory.newFloat(dx)));
  }

  public static IContentPattern floatPtn(Location loc, double dx) {
    return new ConstructorPtn(loc, StandardTypes.FLOAT, StandardTypes.floatType, new ScalarPtn(loc,
        StandardTypes.rawFloatType, Factory.newFloat(dx)));
  }

  public static IContentExpression stringLiteral(Location loc, String str) {
    return new ConstructorTerm(loc, StandardTypes.STRING, StandardTypes.stringType, new Scalar(loc,
        StandardTypes.stringType, Factory.newString(str)));
  }

  public static IContentPattern stringPtn(Location loc, String str) {
    return new ConstructorPtn(loc, StandardTypes.STRING, StandardTypes.stringType, new ScalarPtn(loc,
        StandardTypes.rawStringType, Factory.newString(str)));
  }

  public static Collection<ContractConstraint> findAllContracts(Map<String, IType> types) {
    Collection<ContractConstraint> constraints = new HashSet<>();

    for (Entry<String, IType> entry : types.entrySet()) {
      IType type = TypeUtils.deRef(entry.getValue());
      if (type instanceof TypeVar) {
        for (ITypeConstraint con : ((TypeVar) type)) {
          if (con instanceof ContractConstraint)
            constraints.add((ContractConstraint) con);
        }
      }
    }

    return constraints;
  }
}
