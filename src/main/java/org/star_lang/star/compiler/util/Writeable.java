package org.star_lang.star.compiler.util;

import java.io.File;
import java.io.IOException;

/**
 * An extension to the catalog entry that encodes an ability to write the
 * catalog entry's contents to an output on demand.
 *
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
