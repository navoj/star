package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.ITypeAlias;
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
public class TypeAliasEntry extends EnvironmentEntry {
  final private ITypeAlias typeAlias;
  final private String name;

  public TypeAliasEntry(String name, Location loc, ITypeAlias typeAlias, Visibility visibility) {
    super(loc, visibility);
    this.name = name;
    this.typeAlias = typeAlias;
  }

  public String getName() {
    return name;
  }

  public ITypeAlias getTypeAlias() {
    return typeAlias;
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitTypeAliasEntry(this);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context) {
    return transform.transformTypeAliasEntry(this, context);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    typeAlias.prettyPrint(disp);
  }

  @Override
  public boolean defines(String name) {
    return this.name.equals(name);
  }

  @Override
  public Collection<String> definedFields() {
    return FixedList.create();
  }

  @Override
  public Collection<String> definedTypes() {
    return FixedList.create(name);
  }
}
