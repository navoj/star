package org.star_lang.star.compiler.canonical;

import static com.starview.platform.data.type.Location.merge;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;

import com.starview.platform.data.type.ContractConstraint;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeConstraint;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.Quantifier;
import com.starview.platform.data.type.TypeConstraintException;
import com.starview.platform.data.type.TypeExp;
import com.starview.platform.data.type.TypeVar;

/**
 * The core type expression in a MethodVariable is an overload type expression:
 * 
 * <Contract> $=> <type>
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
public class MethodVariable extends Variable
{
  private final IType contract;
  private final String contractName;

  public MethodVariable(Location loc, String name, IType type, String contractName, IType contract)
  {
    super(loc, type, name);
    this.contract = contract;
    this.contractName = contractName;
    assert TypeUtils.isOverloadedType(contract);
  }

  public static MethodVariable create(Location loc, String name, IType type, String contractName, IType contractType)
  {
    return new MethodVariable(loc, name, type, contractName, contractType);
  }

  @Override
  public Variable copy()
  {
    return new MethodVariable(getLoc(), GenSym.genSym(getName()), getType(), contractName, contract);
  }

  @Override
  public IContentExpression verifyType(Location loc, ErrorReport errors, IType expectedType, Dictionary dict,
      boolean checkForRaw)
  {
    Pair<IType, Map<String, Quantifier>> f = Freshen.freshen(contract, AccessMode.readOnly, AccessMode.readWrite);
    IType freshened = f.left;

    IType refreshedContract = TypeUtils.getContract(freshened);
    IType[] refreshedArgs = ((TypeExp) refreshedContract).getTypeArgs();
    ITypeConstraint constraint = new ContractConstraint((TypeExp) refreshedContract);

    // Be slightly careful because not all variables in a method belong to the contract.
    bindingLoop: for (Entry<String, Quantifier> entry : f.right.entrySet()) {
      TypeVar tVar = entry.getValue().getVar();
      for (int ix = 0; ix < refreshedArgs.length; ix++)
        if (refreshedArgs[ix].equals(tVar)) {
          tVar.setConstraint(constraint);
          continue bindingLoop;
        } else if (refreshedArgs[ix] instanceof TypeExp && ((TypeExp) refreshedArgs[ix]).getTypeCon().equals(tVar)) {
          tVar.setConstraint(constraint);
          continue bindingLoop;
        }
    }

    IType overloadedType = TypeUtils.getOverloadedType(freshened);
    MethodVariable methodVar = new MethodVariable(loc, getName(), expectedType, contractName, freshened);

    if (TypeUtils.hasContractDependencies(overloadedType)) {
      IType overType = TypeUtils.refreshOverloaded(Over
          .computeDictionaryType(overloadedType, loc, AccessMode.readWrite));
      overloadedType = TypeUtils.getOverloadedType(overType);
      IContentExpression var = new Overloaded(loc, overloadedType, overType, methodVar);

      try {
        Subsume.subsume(expectedType, var.getType(), loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(getName(), " has type ", getType(), "\nwhich is not consistent with ",
            expectedType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return var;
    } else {
      overloadedType = Freshen.freshenForUse(overloadedType); // double refresh since this is a
                                                              // field access

      try {
        Subsume.subsume(expectedType, overloadedType, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(getName(), " has type ", getType(), "\nwhich is not consistent with ",
            expectedType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }

      return methodVar;
    }
  }

  public IType getContract()
  {
    return TypeUtils.getContract(contract);
  }

  public String getContractName()
  {
    return contractName;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    super.prettyPrint(disp);
    disp.append(" from ");
    DisplayType.displayContract(disp, getContract());
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitMethodVariable(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformMethodVariable(this, context);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (obj instanceof MethodVariable) {
      MethodVariable other = (MethodVariable) obj;
      return other.getName().equals(getName()) && other.getContract().equals(getContract());
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return (getName().hashCode() * 43) + getContractName().hashCode();
  }

  @Override
  public boolean isRealVariable()
  {
    return false;
  }
}
