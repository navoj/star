package org.star_lang.star.code.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.util.ComboIterator;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;


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
public class CompositeCodeCatalog implements CodeCatalog
{
  private final String path;
  private final List<CodeCatalog> catalogs = new ArrayList<>();

  public CompositeCodeCatalog(String path)
  {
    this.path = path;
  }

  @Override
  public String getPath()
  {
    return path;
  }

  @Override
  public String getExtension()
  {
    return null;
  }

  @Override
  public void write(File output) throws IOException
  {
    throw new IllegalArgumentException("not permitted");
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("[");
    String sep = "";
    for (CodeCatalog cat : catalogs) {
      disp.append(sep);
      sep = ", ";
      cat.prettyPrint(disp);
    }
    disp.append("]");
  }

  @Override
  public Iterator<Entry<String, CodeTree>> iterator()
  {
    return new ComboIterator<>(catalogs);
  }

  @Override
  public void addCodeEntry(String path, CodeTree code) throws RepositoryException
  {
    if (code instanceof CodeCatalog)
      catalogs.add((CodeCatalog) code);
    else {
      for (CodeCatalog cat : catalogs)
        if (cat.isReadWrite()) {
          cat.addCodeEntry(path, code);
          return;
        }
      throw new RepositoryException("cannot add code to catalog");
    }
  }

  @Override
  public CodeTree resolve(String path, String extension) throws RepositoryException
  {
    for (CodeCatalog cat : catalogs) {
      CodeTree res = cat.resolve(path, extension);
      if (res != null)
        return res;
    }
    return null;
  }

  @Override
  public CodeCatalog subCatalog(String path) throws RepositoryException
  {
    for (CodeCatalog cat : catalogs) {
      CodeCatalog res = cat.subCatalog(path);
      if (res != null)
        return res;
    }
    return null;
  }

  @Override
  public CodeTree fork(String path) throws RepositoryException
  {
    throw new IllegalArgumentException("not permitted");
  }

  @Override
  public boolean isPresent(String path)
  {
    for (CodeCatalog cat : catalogs) {
      if (cat.isPresent(path))
        return true;
    }
    return false;
  }

  @Override
  public boolean isPresent(String path, String extension)
  {
    for (CodeCatalog cat : catalogs) {
      if (cat.isPresent(path, extension))
        return true;
    }
    return false;
  }

  @Override
  public boolean isReadWrite()
  {
    for (CodeCatalog cat : catalogs)
      if (cat.isReadWrite())
        return true;
    return false;
  }

  @Override
  public void mergeEntries(CodeCatalog other) throws RepositoryException
  {
    catalogs.add(other);
  }

}
