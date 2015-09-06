package org.star_lang.star.compiler.type;

import java.util.List;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.transform.VarAnalysis.VarChecker;

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

public class DictionaryChecker implements VarChecker
{
  private final Dictionary dict;
  private final List<Variable> defined;

  public DictionaryChecker(Dictionary dict, List<Variable> defined)
  {
    this.dict = dict;
    this.defined = defined;
  }

  @Override
  public boolean isThisOk(Variable var)
  {
    return defined.contains(var) || !dict.isDefinedVar(var.getName());
  }

}
