package org.star_lang.star.compiler.transform;

import java.util.Collection;
import java.util.List;

import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.TopologySort.IDefinition;

/**
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
 * 
 */
@SuppressWarnings("serial")
public class FlowInfo implements PrettyPrintable, IDefinition<Variable>
{
  ICondition term;
  List<Variable> requiredVars;
  List<Variable> definedVars; // these may overlap

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