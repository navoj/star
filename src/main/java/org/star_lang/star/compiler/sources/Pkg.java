package org.star_lang.star.compiler.sources;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.catalog.Catalog;

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

public class Pkg
{
  private final ResourceURI uri;
  private final Catalog srcCatalog;
  private final ErrorReport errors;
  private final CodeRepository repository;
  private final Map<String, JavaInfo> javaImports;
  private final Set<ResourceURI> imports = new HashSet<>();

  public Pkg(ResourceURI uri, Catalog srcCatalog, Map<String, JavaInfo> javaImports, CodeRepository repository,
      ErrorReport errors)
  {
    this.uri = uri;
    this.srcCatalog = srcCatalog;
    this.repository = repository;
    this.javaImports = javaImports;
    this.errors = errors;
  }

  public ResourceURI getUri()
  {
    return uri;
  }

  public Catalog getSrcCatalog()
  {
    return srcCatalog;
  }

  public CodeRepository getRepository()
  {
    return repository;
  }

  public Map<String, JavaInfo> getJavaImports()
  {
    return javaImports;
  }

  public ErrorReport getErrors()
  {
    return errors;
  }

  public void addImport(ResourceURI uri){
    imports.add(uri);
  }

  public Set<ResourceURI> getImports(){
    return imports;
  }
}
