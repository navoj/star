package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.ContractImplementation;
import org.star_lang.star.data.type.Location;

import java.util.Collection;

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

/**
 * Created by fgm on 9/14/15.
 */
@SuppressWarnings("serial")
public class ImplementationEntry extends EnvironmentEntry {
  private final ContractImplementation implementation;

  public ImplementationEntry(Location loc, ContractImplementation implementation, Visibility visibility) {
    super(loc, visibility);
    this.implementation = implementation;
  }

  public ContractImplementation getImplementation() {
    return implementation;
  }

  @Override
  public boolean defines(String name) {
    return false;
  }

  @Override
  public Collection<String> definedFields() {
    return FixedList.create(implementation.getImplementation().getName());
  }

  @Override
  public Collection<String> definedTypes() {
    return FixedList.create();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    implementation.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitContractImplementation(this);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context) {
    return transform.transformContractImplementation(this, context);
  }
}
