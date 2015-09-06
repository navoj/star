package org.star_lang.star.compiler.transform;

import java.util.Collection;
import java.util.List;

import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.TopologySort.IDefinition;
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

@SuppressWarnings("serial")
public class FlowInfo implements PrettyPrintable, IDefinition<Variable>
{
  ICondition term;
  private List<Variable> requiredVars;
  private List<Variable> definedVars; // these may overlap

  FlowInfo(ICondition term, List<Variable> requiredVars, List<Variable> definedVars)
  {
    this.term = term;
    this.requiredVars = requiredVars;
    this.definedVars = definedVars;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    term.prettyPrint(disp);
    disp.append(":");
    disp.append("[");
    disp.prettyPrint(requiredVars, ", ");
    disp.append("]/[");
    disp.prettyPrint(definedVars, ", ");
    disp.append("]");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public boolean defines(Variable obj)
  {
    return definedVars.contains(obj);
  }

  @Override
  public Collection<Variable> definitions()
  {
    return definedVars;
  }

  @Override
  public List<Variable> references()
  {
    return requiredVars;
  }
}