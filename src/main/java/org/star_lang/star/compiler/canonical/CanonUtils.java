package org.star_lang.star.compiler.canonical;

import java.util.List;
import java.util.Map;

import org.star_lang.star.compiler.type.TypeUtils;

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
