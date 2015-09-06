package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
public class ComputationContext implements PrettyPrintable
{
  private final IContentExpression exp;
  private final IType mType;
  private final List<Pair<Variable, IContentExpression>> extras;
  private final Dictionary dict;
  private final Dictionary outer;
  private final ErrorReport errors;

  public ComputationContext(IType mType, IContentExpression exp, List<Pair<Variable, IContentExpression>> tempVars,
      Dictionary dict, Dictionary outer, ErrorReport errors)
  {
    this.exp = exp;
    this.mType = mType;
    this.extras = tempVars;
    this.dict = dict;
    this.outer = outer;
    this.errors = errors;
  }

  public ComputationContext(IType mType, Dictionary dict, Dictionary outer, ErrorReport errors)
  {
    this(mType, null, new ArrayList<>(), dict, outer, errors);
  }

  public IContentExpression getExp()
  {
    return exp;
  }

  public IType getmType()
  {
    return mType;
  }

  public Dictionary getDict()
  {
    return dict;
  }

  public Dictionary getOuter()
  {
    return outer;
  }

  public ErrorReport getErrors()
  {
    return errors;
  }

  public void reportError(String msg, Location... locs)
  {
    errors.reportError(msg, locs);
  }

  public ComputationContext fork(IContentExpression exp)
  {
    return new ComputationContext(mType, exp, new ArrayList<>(), dict, outer, errors);
  }

  public ComputationContext fork()
  {
    return fork((IContentExpression) null);
  }

  public ComputationContext fork(List<Pair<Variable, IContentExpression>> extras)
  {
    return new ComputationContext(mType, exp, extras, dict, outer, errors);
  }

  public Variable declareTempVar(IContentExpression val)
  {
    IType valType = val.getType();
    IType tmpType = TypeUtils.getTypeArg(valType, 0);
    Variable tmp = new Variable(val.getLoc(), tmpType, GenSym.genSym("_"));
    extras.add(0, Pair.pair(tmp, val));
    return tmp;
  }

  public Collection<Pair<Variable, IContentExpression>> getTempVars()
  {
    return extras;
  }

  public boolean anyTemps()
  {
    return !extras.isEmpty();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("<<");
    DisplayType.display(disp, mType);
    if (getExp() != null) {
      disp.append(":");
      getExp().prettyPrint(disp);
    }
    disp.append(">>");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
