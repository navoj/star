package org.star_lang.star.code.repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.util.FileUtil;
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
public class CodeMemory implements CodeCatalog
{
  private final String path;
  private final Map<String, CodeTree> subTrees = new HashMap<>();

  public CodeMemory(String path)
  {
    this.path = path;
  }

  public CodeMemory()
  {
    this("");
  }

  public CodeMemory(CodeCatalog clone)
  {
    this.path = clone.getPath();
    for (Entry<String, CodeTree> entry : clone)
      subTrees.put(entry.getKey(), entry.getValue());
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
  public void addCodeEntry(String path, CodeTree code) throws RepositoryException
  {
    int slashIx = path.indexOf('/');

    if (slashIx > 0) {
      String sub = path.substring(0, slashIx);
      String tail = path.substring(slashIx + 1);

      CodeTree child = subTrees.get(sub);
      if (child instanceof CodeCatalog)
        ((CodeCatalog) child).addCodeEntry(tail, code);
      else if (child == null) {
        CodeMemory subCat = new CodeMemory(sub);
        subTrees.put(sub, subCat);
        subCat.addCodeEntry(tail, code);
      } else
        throw new RepositoryException("could not add " + path + " to catalog because " + sub + " is not a catalog");
    } else
      subTrees.put(path, code);
  }

  @Override
  public CodeTree fork(String path) throws RepositoryException
  {
    int slashIx = path.indexOf('/');

    if (slashIx > 0) {
      String sub = path.substring(0, slashIx);
      String tail = path.substring(slashIx + 1);

      CodeTree child = subTrees.get(sub);
      if (child instanceof CodeCatalog)
        return ((CodeCatalog) child).fork(tail);
      else if (child == null) {
        CodeMemory subCat = new CodeMemory(sub);
        subTrees.put(sub, subCat);
        return subCat.fork(tail);
      } else
        throw new RepositoryException("could not add " + path + " to catalog because " + sub + " is not a catalog");
    } else {
      CodeTree child = subTrees.get(path);
      if (child instanceof CodeCatalog)
        return child;
      else if (child == null) {
        CodeMemory subCat = new CodeMemory(path);
        subTrees.put(path, subCat);
        return subCat;
      } else
        throw new RepositoryException("could not add " + path + " to catalog because it is not a catalog");
    }
  }

  @Override
  public CodeTree resolve(String path, String extension) throws RepositoryException
  {
    CodeTree code = subTrees.get(path);

    if (code == null) {
      int slashIx = path.indexOf('/');

      if (slashIx > 0) {
        String prefix = path.substring(0, slashIx);
        CodeTree child = subTrees.get(prefix);
        if (child instanceof CodeCatalog)
          return ((CodeCatalog) child).resolve(path.substring(slashIx + 1), extension);
        else
          return null;
      }
    }
    return code;
  }

  @Override
  public CodeCatalog subCatalog(String path) throws RepositoryException
  {
    return (CodeCatalog) resolve(path, null);
  }

  @Override
  public boolean isPresent(String path)
  {
    int slashIx = path.indexOf('/');

    if (slashIx > 0) {
      String prefix = path.substring(0, slashIx);
      CodeTree child = subTrees.get(prefix);
      if (child instanceof CodeCatalog)
        return ((CodeCatalog) child).isPresent(path.substring(slashIx + 1));
      else
        return false;
    } else
      return subTrees.containsKey(path);
  }

  @Override
  public boolean isPresent(String path, String extension)
  {
    return isPresent(path);
  }

  @Override
  public boolean isReadWrite()
  {
    return true;
  }

  @Override
  public Iterator<Entry<String, CodeTree>> iterator()
  {
    return subTrees.entrySet().iterator();
  }

  @Override
  public void mergeEntries(CodeCatalog other) throws RepositoryException
  {
    for (Entry<String, CodeTree> entry : other) {
      String name = entry.getKey();
      CodeTree newRef = entry.getValue();
      CodeTree oldRef = subTrees.get(name);

      if (oldRef != null && oldRef != newRef) {
        if (oldRef instanceof CodeCatalog) {
          if (newRef instanceof CodeCatalog)
            ((CodeCatalog) oldRef).mergeEntries((CodeCatalog) newRef);
        }
      } else if (oldRef == null)
        subTrees.put(name, newRef);
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(getPath());
    int mark = disp.markIndent(2);
    disp.append("{\n");
    for (Entry<String, CodeTree> entry : this) {
      disp.appendQuoted(entry.getKey());
      disp.append("->");
      entry.getValue().prettyPrint(disp);
      disp.append(";\n");
    }
    disp.popIndent(mark);
    disp.append("}\n");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void write(File dir) throws IOException
  {
    if (dir.exists()) {
      if (!dir.isDirectory())
        throw new IOException("target is not a directory");
      else
        FileUtil.rmRf(dir);
    } else if (!dir.mkdirs())
      throw new IOException("cannot create target directory " + dir);

    for (Entry<String, CodeTree> entry : subTrees.entrySet()) {
      CodeTree code = entry.getValue();
      String ext = code.getExtension();
      String name = code.getPath();
      File subEntry = new File(dir, ext != null ? name + "." + ext : name);
      code.write(subEntry);
    }
  }
}
