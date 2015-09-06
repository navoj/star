package org.star_lang.star.compiler.type;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeExists;
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

public class TypeCollector implements ITypeCollector
{
  private final Map<String, IType> elements;
  private final Map<String, IType> exists;
  private final ErrorReport errors;
  private final Dictionary dict;
  private final ITypeCollector parent;

  public TypeCollector(Map<String, IType> elements, Map<String, IType> types, ErrorReport errors, Dictionary dict,
      ITypeCollector parent)
  {
    this.elements = elements;
    this.exists = types;
    this.errors = errors;
    this.dict = dict;
    this.parent = parent;
  }

  @Override
  public void fieldAnnotation(Location loc, String name, IType type)
  {
    elements.put(name, type);
    dict.declareVar(name, new Variable(loc, type, name), AccessMode.readOnly, Visibility.priVate, true);
    parent.fieldAnnotation(loc, name, type);
  }

  @Override
  public void kindAnnotation(Location loc, String name, IType type)
  {
    IType t = exists.get(name);
    if (t == null) {
      exists.put(name, type);
      dict.defineType(new TypeExists(loc, name, type));
    } else {
      assert t instanceof TypeVar;
      try {
        Subsume.subsume(t, type, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("kind of ", name, " already declared to be ", t.kind()));
      }
    }
    parent.kindAnnotation(loc, name, type);
  }

  @Override
  public void completeInterface(Location loc)
  {
    for (Entry<String, IType> e : exists.entrySet()) {
      if (e.getValue() instanceof TypeVar) {
        TypeVar v = (TypeVar) e.getValue();
        for (ITypeConstraint con : v) {
          if (con instanceof ContractConstraint) {
            ContractConstraint contract = (ContractConstraint) con;
            String instanceName = Over.instanceFunName(contract.getContract());
            IType instanceType = Over.computeDictionaryType(contract.getContract(), loc, AccessMode.readWrite);
            fieldAnnotation(loc, instanceName, instanceType);
          }
        }
      }
    }
  }
}