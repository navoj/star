/**
 *
 */
package org.star_lang.star.compiler.type;

import java.util.Map;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Quantifier;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeVar;

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
public class VarInfo implements DictInfo {
  private AccessMode access;
  private final Variable var;
  private boolean initialized;
  private final IType face;
  private final boolean typeClosed;

  VarInfo(Variable ref, AccessMode readOnly, boolean initialized) {
    this.access = readOnly;
    this.var = ref;
    this.initialized = initialized;
    this.face = new TypeVar();
    typeClosed = TypeClosed.isTypeClosed(ref.getType());
  }

  @Override
  public Variable getVariable() {
    return var;
  }

  @Override
  public AccessMode getAccess() {
    return access;
  }

  public void setAccess(AccessMode access) {
    this.access = access;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  @Override
  public Location getLoc() {
    return var.getLoc();
  }

  @Override
  public String getName() {
    return var.getName();
  }

  @Override
  public IType getType() {
    return var.getType();
  }

  public IType getFace(ErrorReport errors, Dictionary dict) {
    if (TypeUtils.isTypeVar(face)) {
      Location loc = var.getLoc();
      IType face = Freshen.freshenForUse(TypeUtils.interfaceOfType(loc, var.getType(), dict, errors));

      try {
        Subsume.subsume(this.face, face, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("cannot determine interface of ", getVariable()), loc);
        return new TypeVar();
      }
    }

    return face;
  }

  @Override
  public boolean isTypeVarInScope(TypeVar var) {
    return !typeClosed && OccursCheck.occursIn(getType(), var);
  }

  public IType localType(Location loc, String field, Dictionary dict, ErrorReport errors) {
    IType type = TypeUtils.deRef(getType());

    if (TypeUtils.isTypeVar(face) && !TypeUtils.isTypeVar(type)) {
      try {
        IType face = TypeUtils.interfaceOfType(loc, type, dict,errors);
        Pair<IType, Map<String, Quantifier>> fface = Freshen.freshen(face, AccessMode.readOnly, AccessMode.readWrite);
        Subsume.subsume(this.face, fface.left, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("cannot determine interface of ", getVariable()), loc);
        return new TypeVar();
      }
    }

    if (TypeUtils.isTypeVar(face)) {
      IType fieldType = new TypeVar(field);
      try {
        TypeUtils.addTypeConstraint((TypeVar) face, loc, field, fieldType, dict, false);
      } catch (TypeConstraintException e) {
        errors.reportError("type " + field + " from " + getVariable() + " is not valid here", loc);
      }
      return fieldType;
    }
    return TypeUtils.getInterfaceMemberType(face, field);
  }

  public IType typeOfField(Location loc, String field, Dictionary dict, ErrorReport errors) {
    IType type = TypeUtils.deRef(getType());

    if (TypeUtils.isTypeInterface(type))
      return TypeUtils.getInterfaceField(type, field);
    else if (TypeUtils.isTypeVar(type)) {
      IType fieldType = new TypeVar();
      try {
        TypeUtils.addFieldConstraint((TypeVar) type, loc, field, fieldType, dict, false);
      } catch (TypeConstraintException e) {
        errors.reportError("type " + field + " from " + getVariable() + " is not valid here", loc);
      }
      return fieldType;
    } else if (TypeUtils.isTypeVar(face))
      return TypeUtils.getInterfaceField(getFace(errors, dict), field);
    else
      return ((TypeInterface) TypeUtils.deRef(face)).getFieldType(field);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    var.prettyPrint(disp);

    if (!initialized)
      disp.append("[u]");

    disp.appendWord(StandardNames.HAS_TYPE);
    disp.append(" ");
    DisplayType.display(disp, getType());
  }

  @Override
  public String toString() {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    prettyPrint(disp);
    return disp.toString();
  }
}