package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
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
public class TypeWitness extends EnvironmentEntry
{
  private final IType type;
  private final IType witness;

  public TypeWitness(Location loc, IType type, IType witness, Visibility visibility)
  {
    super(loc, visibility);
    this.type = type;
    this.witness = witness;
  }

  public IType getType()
  {
    return type;
  }

  public IType getWitness()
  {
    return witness;
  }

  @Override
  public boolean defines(String name)
  {
    return name.equals(this.type.typeLabel());
  }

  @Override
  public Collection<String> definedFields()
  {
    return FixedList.create();
  }

  @Override
  public Collection<String> definedTypes()
  {
    return FixedList.create(type.typeLabel());
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformWitness(this, context);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitTypeWitness(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    getVisibility().display(disp);
    disp.appendWord(StandardNames.TYPE);
    DisplayType tpDisp = new DisplayType(disp);
    witness.accept(tpDisp, null);
    disp.appendWord(StandardNames.COUNTS_AS);
    type.accept(tpDisp, null);
  }

}
