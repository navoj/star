package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;

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

public interface Canonical extends PrettyPrintable
{
  /**
   * Every term has a potential location. This enables debugging and error reporting to be sensitive
   * to the actual element itself.
   * 
   * @return a Location object that denotes the source location of the term.
   */

  Location getLoc();

  /**
   * Every canonical is visitable
   * 
   * @param visitor
   */
  void accept(CanonicalVisitor visitor);
}
