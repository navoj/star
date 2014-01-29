package org.star_lang.star.compiler.util;

import java.io.File;
import java.io.IOException;

/**
 * An extension to the catalog entry that encodes an ability to write the
 * catalog entry's contents to an output on demand.
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

public interface Writeable{
  /**
   * Base method to write the contents of the catalog entry to an output file.
   * <p/>
   * When the write method is called, the entire contents should be written, and
   * any temporary resources should be released.
   *
   * @param output where to write to.
   * @throws IOException when something goes wrong
   */
  void write(File output) throws IOException;
}
