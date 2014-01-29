package org.star_lang.star.code.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.util.ComboIterator;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
        if (!cat.isReadOnly()) {
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
  public boolean isReadOnly()
  {
    for (CodeCatalog cat : catalogs)
      if (!cat.isReadOnly())
        return false;
    return true;
  }

  @Override
  public void mergeEntries(CodeCatalog other) throws RepositoryException
  {
    catalogs.add(other);
  }

}
