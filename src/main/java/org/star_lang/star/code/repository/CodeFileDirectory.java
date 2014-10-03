package org.star_lang.star.code.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.star_lang.star.code.CafeCode;
import org.star_lang.star.compiler.util.NullIterator;
import org.star_lang.star.compiler.util.Pair;

/**
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
      return new NullIterator<Entry<String, CodeTree>>();
  }

}
