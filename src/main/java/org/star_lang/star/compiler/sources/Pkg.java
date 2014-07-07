package org.star_lang.star.compiler.sources;

import java.util.Map;

import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.catalog.Catalog;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
public class Pkg
{
  private final ResourceURI uri;
  private final Catalog srcCatalog;
  private final ErrorReport errors;
  private final CodeRepository repository;
  private final Map<String, JavaInfo> javaImports;

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
}
