package org.star_lang.star.compiler.type;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.data.type.*;
import org.star_lang.star.data.value.Factory;

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
public class TypeCheckerUtils{

  public static IContentExpression charLiteral(Location loc, int ix){
    return new ConstructorTerm(loc, StandardTypes.CHAR, StandardTypes.charType, new Scalar(loc,
            StandardTypes.rawCharType, ix));
  }

  public static IContentPattern charPtn(Location loc, int ch){
    return new ConstructorPtn(loc, StandardTypes.CHAR, StandardTypes.charType, new ScalarPtn(loc,
            StandardTypes.rawCharType, Factory.newChar(ch)));
  }

  public static IContentExpression integerLiteral(Location loc, int ix){
    return new ConstructorTerm(loc, StandardTypes.INTEGER, StandardTypes.integerType, new Scalar(loc,
            StandardTypes.integerType, Factory.newInt(ix)));
  }

  public static IContentPattern integerPtn(Location loc, int ix){
    return new ConstructorPtn(loc, StandardTypes.INTEGER, StandardTypes.integerType, new ScalarPtn(loc,
            StandardTypes.rawIntegerType, Factory.newInt(ix)));
  }

  public static IContentExpression longLiteral(Location loc, long lx){
    return new ConstructorTerm(loc, StandardTypes.LONG, StandardTypes.longType, new Scalar(loc, StandardTypes.longType,
            Factory.newLong(lx)));
  }

  public static IContentPattern longPtn(Location loc, long ix){
    return new ConstructorPtn(loc, StandardTypes.LONG, StandardTypes.longType, new ScalarPtn(loc,
            StandardTypes.rawLongType, Factory.newLong(ix)));
  }

  public static IContentExpression floatLiteral(Location loc, double dx){
    return new ConstructorTerm(loc, StandardTypes.FLOAT, StandardTypes.floatType, new Scalar(loc,
            StandardTypes.floatType, Factory.newFloat(dx)));
  }

  public static IContentPattern floatPtn(Location loc, double dx){
    return new ConstructorPtn(loc, StandardTypes.FLOAT, StandardTypes.floatType, new ScalarPtn(loc,
            StandardTypes.rawFloatType, Factory.newFloat(dx)));
  }

  public static IContentExpression stringLiteral(Location loc, String str){
    return new ConstructorTerm(loc, StandardTypes.STRING, StandardTypes.stringType, new Scalar(loc,
            StandardTypes.stringType, Factory.newString(str)));
  }

  public static IContentPattern stringPtn(Location loc, String str){
    return new ConstructorPtn(loc, StandardTypes.STRING, StandardTypes.stringType, new ScalarPtn(loc,
            StandardTypes.rawStringType, Factory.newString(str)));
  }

  public static IContentExpression decimalLiteral(Location loc, BigDecimal ax){
    return new ConstructorTerm(loc, StandardTypes.DECIMAL, StandardTypes.decimalType, new Scalar(loc,
            StandardTypes.decimalType, Factory.newDecimal(ax)));
  }

  public static IContentPattern decimalPtn(Location loc, BigDecimal ax){
    return new ConstructorPtn(loc, StandardTypes.DECIMAL, StandardTypes.decimalType, new ScalarPtn(loc,
            StandardTypes.rawDecimalType, Factory.newDecimal(ax)));
  }

  public static Collection<ContractConstraint> findAllContracts(Map<String, IType> types){
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
