package org.star_lang.star.compiler.util;

import java.io.File;
import java.util.Iterator;

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
public class DirectoryIterator implements Iterator<File>
{
  private final File dir;
  private final String[] list;
  private int next = 0;

  public DirectoryIterator(File dir)
  {
    this.dir = dir;
    assert dir.isDirectory();
    this.list = dir.list();
  }

  @Override
  public boolean hasNext()
  {
    return next < list.length;
  }

  @Override
  public File next()
  {
    return new File(dir, list[next++]);
  }

  @Override
  public void remove()
  {
    new File(dir, list[next - 1]).delete();
  }
}
