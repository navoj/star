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

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;

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
    this(mType, null, new ArrayList<Pair<Variable, IContentExpression>>(), dict, outer, errors);
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
    return new ComputationContext(mType, exp, new ArrayList<Pair<Variable, IContentExpression>>(), dict, outer, errors);
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
