package org.star_lang.star.compiler.canonical;

import static org.star_lang.star.data.type.Location.merge;

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
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Quantifier;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;

/**
 * The core type expression in a MethodVariable is an overload type expression:
 * 
 * <Contract> $=> <type>
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
