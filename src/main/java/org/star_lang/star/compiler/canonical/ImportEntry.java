package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;

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
public class ImportEntry extends EnvironmentEntry {
  private final String pkgName;
  private final IType pkgType;
  private final ResourceURI uri;

  public ImportEntry(Location loc, String name, IType pkgType, ResourceURI uri, Visibility visibility) {
    super(loc, visibility);
    this.pkgName = name;
    this.pkgType = pkgType;
    this.uri = uri;
    assert name != null;
  }

  public String getPkgName() {
    return pkgName;
  }

  public IType getPkgType() {
    return pkgType;
  }

  public ResourceURI getUri() {
    return uri;
  }

  @Override
  public boolean defines(String name) {
    return pkgName.equals(name);
  }

  @Override
  public Collection<String> definedFields() {
    return FixedList.create(pkgName);
  }

  @Override
  public Collection<String> definedTypes() {
    return FixedList.create();
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitImportEntry(this);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context) {
    return transform.transformImportEntry(this, context);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.appendWord(StandardNames.IMPORT);
    disp.appendWord(pkgName);
  }
}
