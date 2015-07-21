package org.star_lang.star.code.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.star_lang.star.code.CafeCode;
import org.star_lang.star.compiler.util.NullIterator;
import org.star_lang.star.compiler.util.Pair;


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
public class CodeFileDirectory extends CodeDirectory
{

  private final File dir;

  public CodeFileDirectory(File dir) throws RepositoryException
  {
    super(dir);
    this.dir = dir;
  }

  public CodeFileDirectory(CodeDirectory cd) throws RepositoryException
  {
    super(cd.getDir());
    this.dir = cd.getDir();
  }

  @Override
  public Iterator<Entry<String, CodeTree>> iterator()
  {
    if (dir.exists()) {
      final String files[] = dir.list();

      return new Iterator<Entry<String, CodeTree>>() {
        int ix = 0;

        @Override
        public boolean hasNext()
        {
          return ix < files.length;
        }

        @Override
        public Entry<String, CodeTree> next()
        {
          String fileName = files[ix++];
          File entry = new File(dir, fileName);
          if (!entry.isDirectory()) {
            try (FileInputStream str = new FileInputStream(entry)) {
              try {
                byte[] bt = new byte[str.available()];
                str.read(bt);
                return Pair.pair(fileName, (CodeTree) new CafeCode(fileName, bt));
              } catch (IOException e) {
                throw new IllegalStateException(e);
              }
            } catch (IOException e) {
              throw new IllegalStateException(e);
            }
          }
          return null;
        }

        @Override
        public void remove()
        {
          throw new UnsupportedOperationException("not permitted");
        }

      };

    } else
      return new NullIterator<>();
  }

}
