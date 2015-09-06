package org.star_lang.star.compiler.util;

import java.util.Map;

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

public interface UndoableMap<K, V> extends Map<K, V>
{
  /**
   * Get the current undo state
   * 
   * @return
   */
  int undoState();

  /**
   * Undo the map to a previous version
   * 
   * @param state
   */
  void undo(int state) throws IllegalStateException;
}
