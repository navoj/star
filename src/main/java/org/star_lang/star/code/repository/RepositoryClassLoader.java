package org.star_lang.star.code.repository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;
import org.star_lang.star.code.CafeCode;
import org.star_lang.star.code.HasCode;
import org.star_lang.star.code.repository.CodeRepository.RepositoryListener;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.ClassRoot;


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

public class RepositoryClassLoader extends ClassLoader implements RepositoryListener
{
  private final CodeRepository repository;
  private final Map<String, RepositoryNode> roots = new HashMap<>();

  private int classesLoaded = 0;

  public RepositoryClassLoader(CodeRepository repository, ClassLoader parent)
  {
    super(parent);
    this.repository = repository;
  }

  public CodeRepository getRepository()
  {
    return repository;
  }

  @Override
  protected synchronized Class<?> findClass(String name) throws ClassNotFoundException
  {
    CodeTree entry = resolve(internalClassName(name));

    if (entry == null)
      return super.findClass(name);
    else if (entry instanceof HasCode) {
      HasCode code = (HasCode) entry;
      byte[] byteCode = code.getCode();

      // showClassData(name, byteCode);

      classesLoaded++;
      return defineClass(name, byteCode, 0, byteCode.length);
    } else
      throw new ClassNotFoundException(name + " not in loadable form");
  }

  private synchronized void updateFromRepository(RepositoryNode node)
  {
    try {
      CodeTree code = node.getCode();
      if (code instanceof CodeCatalog) {
        CodeTree entry = ((CodeCatalog) code).resolve(Names.CLASS_ROOT, ClassRoot.EXTENSION);
        if (entry instanceof ClassRoot) {
          ClassRoot manifest = (ClassRoot) entry;
          roots.put(manifest.getClassRoot(), node);
        }
      }
    } catch (RepositoryException e) {
    } finally {
    }
  }

  private synchronized boolean removeFromRepository(RepositoryNode node)
  {
    try {
      CodeTree code = node.getCode();
      if (code instanceof CodeCatalog) {
        CodeTree manifestEntry = ((CodeCatalog) code).resolve(Names.CLASS_ROOT, ClassRoot.EXTENSION);
        if (manifestEntry instanceof ClassRoot) {
          ClassRoot manifest = (ClassRoot) manifestEntry;
          roots.remove(manifest.getClassRoot());
        }
      }
    } catch (RepositoryException e) {
    } finally {
    }
    return true;
  }

  private CodeTree resolve(String className)
  {
    try {
      int slashPos = className.indexOf('/');
      if (slashPos > 0) {
        String topPath = className.substring(0, slashPos);
        RepositoryNode node = roots.get(topPath);

        if (node != null) {
          CodeTree code = node.getCode();
          if (code instanceof CodeCatalog) {
            CodeTree compiledCode = ((CodeCatalog) code).resolve(RepositoryManager.COMPILED, null);
            if (compiledCode instanceof CodeCatalog) {
              CodeTree resolved = ((CodeCatalog) compiledCode).resolve(className, CafeCode.EXTENSION);
              if (resolved != null)
                return resolved;
            }
          }
        }
      }

      CodeCatalog genCode = repository.synthCodeCatalog();
      if (genCode != null)
        return genCode.resolve(className, CafeCode.EXTENSION);
    } catch (RepositoryException e) {
    }
    return null;
  }

  public int getClassesLoaded()
  {
    return classesLoaded;
  }

  // Implement repository listener
  @Override
  public void nodeUpdated(RepositoryNode node)
  {
    updateFromRepository(node);
  }

  @Override
  public boolean removeNode(RepositoryNode node)
  {
    return removeFromRepository(node);
  }

  /**
   * Call close when the class loader is no longer needed.
   */
  public void close()
  {
    repository.removeListener(this);
  }

  @Override
  protected void finalize() throws Throwable
  {
    close();
    super.finalize();
  }

  @Override
  protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
  {
    return super.loadClass(name, resolve);
  }

  private static String internalClassName(String name)
  {
    return name.replace('.', '/');
  }
}
