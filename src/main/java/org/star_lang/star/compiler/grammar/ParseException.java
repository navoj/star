package org.star_lang.star.compiler.grammar;

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

public class ParseException extends Exception
{
  private static final long serialVersionUID = 611L;
  private final Location loc;

  public ParseException(String msg, Location loc)
  {
    super(msg);
    this.loc = loc;
  }

  public Location getLoc()
  {
    return loc;
  }

}
