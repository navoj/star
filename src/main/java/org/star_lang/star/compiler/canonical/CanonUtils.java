package org.star_lang.star.compiler.canonical;

import java.util.List;
import java.util.Map;

import org.star_lang.star.compiler.type.TypeUtils;

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
public class CanonUtils
{
  public static boolean isTuple(IContentExpression exp)
  {
    return exp instanceof ConstructorTerm && TypeUtils.isTupleLabel(((ConstructorTerm) exp).getLabel());
  }

  public static List<IContentExpression> constructorArgs(IContentExpression exp)
  {
    assert exp instanceof ConstructorTerm;

    return ((ConstructorTerm) exp).getElements();
  }

  public static boolean isAnonRecord(IContentExpression exp)
  {
    return exp instanceof RecordTerm && ((RecordTerm) exp).isAnonRecord();
  }

  public static Map<String, IContentExpression> recordElements(RecordTerm record)
  {
    return record.getArguments();
  }
}
