package org.star_lang.star.code.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.CatalogException;



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

public class DirectoryRepository extends AbstractCodeRepository
{
  public static final String GENERIC_NODE_NAME = "__";
  private final File tgtDir;
  private final boolean overwrite;
  private final Map<String, RepositoryNode> cache = new HashMap<>();
  private final RepositoryNode generics;
  private final List<String> files = new ArrayList<>();

  public DirectoryRepository(File tgtDir, boolean overwrite, boolean hasStarRoot, ClassLoader loader, ErrorReport errors)
      throws RepositoryException
  {
    super(loader, errors);
    this.tgtDir = tgtDir;
    this.overwrite = overwrite;

    if (tgtDir.isDirectory())
      ;
    else if (tgtDir.exists())
      throw new RepositoryException("target: " + tgtDir.getPath() + " exists and is not a directory");
    else if (!tgtDir.mkdirs())
      throw new RepositoryException("cannot create target directory: " + tgtDir.getPath());

    generics = setupGenericsNode();
    cacheDir();

    if (hasStarRoot) {
      if (findNode(StarCompiler.starRulesURI) == null)
        setupStarLib();
    }
  }

  public DirectoryRepository(File tgtDir, boolean overwrite, boolean hasStarRoot, ErrorReport errors)
      throws RepositoryException
  {
    this(tgtDir, overwrite, hasStarRoot, Thread.currentThread().getContextClassLoader(), errors);
  }

  private void cacheDir()
  {
    files.clear();
    for (String file : tgtDir.list()) {
      findNode(file);
    }
  }

  @Override
  public RepositoryNode addRepositoryNode(ResourceURI uri, String hash, CodeCatalog code) throws RepositoryException
  {
    try {
      String path = URIUtils.rootPath(uri);
      File codeDr = new File(tgtDir, path);

      CodeDirectory codeDir = new CodeDirectory(path, codeDr, overwrite);
      codeDir.mergeEntries(code);

      FileUtil.writeFile(new File(codeDr, CodeHash.HASH), hash + "#" + uri);
      RepositoryNode node = new RepositoryNodeImpl(uri, hash, codeDir);
      cache.put(path, node);
      files.add(path);
      mergeStdTypes(code);
      recordVersion(uri);

      triggerUpdates(node);
      return node;
    } catch (IOException e) {
      throw new RepositoryException(e.getMessage());
    }
  }

  private RepositoryNode setupGenericsNode() throws RepositoryException
  {
    try {
      String path = "__std";

      RepositoryNode stdNode = findNode(path);

      if (stdNode == null) {
        File codeDr = new File(tgtDir, path);

        CodeDirectory codeDir = new CodeDirectory(path, codeDr, overwrite);

        File hashFile = new File(codeDr, CodeHash.HASH);

        String hash = "#" + ResourceURI.noUriEnum;
        FileUtil.writeFile(hashFile, hash);

        RepositoryNode node = new RepositoryNodeImpl(ResourceURI.noUriEnum, hash, codeDir);
        triggerUpdates(node);
        return node;
      } else
        return stdNode;
    } catch (IOException e) {
      throw new RepositoryException(e.getMessage());
    }
  }

  @Override
  public void removeRepositoryNode(ResourceURI uri) throws CatalogException
  {
    RepositoryNode node = findNode(uri);
    if (node != null) {
      if (triggerDelete(node)) {
        removeVersion(uri);
        CodeTree nodeCode = node.getCode();
        if (nodeCode instanceof CodeDirectory) {
          File child = ((CodeDirectory) nodeCode).getDir();
          cache.remove(child.getName());
          files.remove(child.getName());
          FileUtil.rmRf(child);
        }
      }
    }
  }

  @Override
  public RepositoryNode findNode(ResourceURI uri)
  {
    return findNode(URIUtils.rootPath(uri));
  }

  private RepositoryNode findNode(String name)
  {
    RepositoryNode node = cache.get(name);

    if (node != null)
      return node;
    else {
      try {
        File uriFile = new File(tgtDir, name);
        if (uriFile.isDirectory()) {
          node = readRepositoryNodeHash(uriFile);
          if (node != null) {
            cache.put(name, node);
            files.add(name);
            // mergeStdTypes(node.getCode());
            triggerUpdates(node);
          }
          return node;
        }
      } catch (IOException | RepositoryException e) {
      }
    }
    return null;
  }

  private RepositoryNode loadNode(String name)
  {
    try {
      File uriFile = new File(tgtDir, name);
      if (uriFile.isDirectory()) {
        RepositoryNode node = readRepositoryNodeHash(uriFile);
        if (node != null) {
          cache.put(name, node);
          files.add(name);
          // mergeStdTypes(node.getCode());
          triggerUpdates(node);
        }
        return node;
      }
    } catch (Exception e) {
    }
    return null;
  }

  @Override
  public CodeCatalog synthCodeCatalog()
  {
    return generics.getCode();
  }

  private RepositoryNode readRepositoryNodeHash(final File sub) throws IOException, RepositoryException
  {
    final File hashFile = new File(sub, CodeHash.HASH);

    if (hashFile.canRead()) {
      String hashText = FileUtil.readFileIntoString(hashFile).trim();
      int hashPos = hashText.indexOf("#");
      if (hashPos < 0)
        throw new RepositoryException("invalid hash signature file");

      try {
        String hash = hashText.substring(0, hashPos);
        ResourceURI uri = ResourceURI.parseURI(hashText.substring(hashPos + 1));

        CodeDirectory code = new CodeDirectory(sub);
        return new RepositoryNodeImpl(uri, hash, code);
      } catch (ResourceException e) {
        throw new RepositoryException(e.getMessage());
      }
    } else
      throw new IllegalStateException("missing " + CodeHash.HASH + " file");
  }

  @Override
  public Iterator<RepositoryNode> iterator()
  {
    return new Iterator<RepositoryNode>() {
      String[] files = tgtDir.list();
      int numFiles = files.length;
      int ix = 0;

      @Override
      public boolean hasNext()
      {
        return ix < numFiles;
      }

      @Override
      public RepositoryNode next()
      {
        String child = files[ix++];
        return loadNode(child);
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("not permitted");
      }
    };
  }
}
