package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IAlgebraicType;
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
 */ /**
 * The definition of an algebraic type
 * 
 * @author fgm
 * 
 */
@SuppressWarnings("serial")
public class TypeDefinition extends EnvironmentEntry
{
  final IType type;
  final IAlgebraicType typeDescription;
  final private String name;
  final private boolean imported;
  final private boolean fromContract;

  public TypeDefinition(String name, Location loc, IType type, IAlgebraicType typeDescription, Visibility visibility,
      boolean imported, boolean fromContract)
  {
    super(loc, visibility);
    this.type = type;
    this.name = name;
    this.typeDescription = typeDescription;
    this.imported = imported;
    this.fromContract = fromContract;
  }

  public String getName()
  {
    return name;
  }

  public IType getType()
  {
    return type;
  }

  public boolean isImported()
  {
    return imported;
  }

  public boolean isFromContract()
  {
    return fromContract;
  }

  public IAlgebraicType getTypeDescription()
  {
    return typeDescription;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitTypeEntry(this);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformTypeEntry(this, context);
  }

  @Override
  public boolean defines(String name)
  {
    return this.name.equals(name);
  }

  @Override
  public Collection<String> definedFields()
  {
    return FixedList.create();
  }

  @Override
  public Collection<String> definedTypes()
  {
    return FixedList.create(name);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    typeDescription.prettyPrint(disp);
  }
}