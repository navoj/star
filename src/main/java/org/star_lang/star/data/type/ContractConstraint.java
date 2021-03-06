package org.star_lang.star.data.type;

/**
 * Type constraint that requires that the type has a certain attribute
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.transform.PrimitiveOverloader;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeContracts;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;

@SuppressWarnings("serial")
public class ContractConstraint implements ITypeConstraint
{
  private final TypeExp contract;

  public ContractConstraint(TypeExp contract)
  {
    this.contract = contract;
    assert contract != null;
  }

  public ContractConstraint(String contract, List<IType> argTypes)
  {
    this.contract = (TypeExp) TypeUtils.typeExp(contract, argTypes);
  }

  public ContractConstraint(String contract, IType... argTypes)
  {
    this.contract = (TypeExp) TypeUtils.typeExp(contract, argTypes);
  }

  public TypeExp getContract()
  {
    return contract;
  }

  public String getContractName()
  {
    return contract.typeLabel();
  }

  public IType[] getContractTypes()
  {
    return contract.getTypeArgs();
  }

  @Override
  public Collection<TypeVar> affectedVars()
  {
    List<TypeVar> vars = new ArrayList<>();
    for (IType t : contract.getTypeArgs()) {
      t = TypeUtils.deRef(t);
      if (t instanceof TypeVar)
        vars.add((TypeVar) t);
    }
    return vars;
  }

  @Override
  public <C> void accept(ITypeVisitor<C> visitor, C cxt)
  {
    contract.accept(visitor, cxt);
  }

  @Override
  public <T, C, X> C transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformContractConstraint(this, cxt);
  }

  @Override
  public boolean sameConstraint(ITypeConstraint other, Location loc, Dictionary dict) throws TypeConstraintException
  {
    if (other instanceof ContractConstraint) {
      try {
        ContractConstraint otherCon = (ContractConstraint) other;
        Subsume.same(otherCon.contract, contract, loc, dict);
        return true;
      } catch (TypeConstraintException e) {
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof ContractConstraint && ((ContractConstraint) obj).getContract().equals(getContract());
  }

  @Override
  public int hashCode()
  {
    if (getContract() == null)
      return tempHashCode;
    int hashCode = getContract().hashCode();
    assert (tempHashCode == 0 || tempHashCode == hashCode);
    return hashCode;
  }

  @Override
  public void checkBinding(IType candidate, Location loc, Dictionary dict) throws TypeConstraintException
  {
    candidate = TypeUtils.deRef(candidate);

    IType fallbackType = dict.getVarType(TypeContracts.contractFallbackName(contract));

    if (TypeUtils.isFunType(candidate) && fallbackType == null)
      throw new TypeConstraintException("contract cannot be implemented for function types");
    else if (TypeUtils.isRawType(candidate)
        && /* fallbackType == null && */!PrimitiveOverloader.hasPrimitiveImplementation(getContract()))
      throw new TypeConstraintException(StringUtils.msg("contract ", getContract(),
          " is not implemented for raw type ", candidate));
    else {
      TypeExp contract = getContract();
      if (TypeUtils.isGroundSurface(contract)) {
        String instanceName = Over.instanceFunName(TypeContracts.contractImplType(contract));
        IType instanceType = dict.getVarType(instanceName);
        if (instanceType == null) {
          if (fallbackType == null)
            throw new TypeConstraintException(FixedList.create(DisplayType.showContract(contract),
                " not known to be implemented"));
          else if (TypeUtils.isOverloadedType(fallbackType)) {
            IType instType = TypeUtils.refreshOverloaded(fallbackType);
            Subsume.subsume(TypeUtils.getOverloadedType(instType), contract, loc, dict);
          }
        } else if (TypeUtils.isOverloadedType(instanceType)) {
          IType instType = TypeUtils.refreshOverloaded(instanceType);
          Subsume.subsume(TypeUtils.getOverloadedType(instType), contract, loc, dict, false);
        }
      }
    }
  }

  @Override
  public void showConstraint(DisplayType disp)
  {
    IType[] contractArgs = contract.getTypeArgs();

    disp.getDisp().appendId(contract.typeLabel());
    disp.getDisp().appendWord(AbstractType.OVER);
    disp.typeArgs(contractArgs);
  }

  @Override
  public String toString()
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    DisplayType.displayContract(disp, contract);

    return disp.toString();
  }

  int tempHashCode = 0;

  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.writeInt(hashCode());
    os.defaultWriteObject();
  }

  private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException
  {
    tempHashCode = is.readInt();
    is.defaultReadObject();
  }
}