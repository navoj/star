package org.star_lang.star.compiler.util;

/**
 * Use this enumeration to distinguish readWrite from readOnly.
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

public enum AccessMode implements PrettyPrintable {
  readWrite {
    @Override
    public AccessMode downGrade()
    {
      return this;
    }
  },
  readOnly {
    @Override
    public AccessMode downGrade()
    {
      return this;
    }
  },
  unknown {
    @Override
    public AccessMode downGrade()
    {
      return this;
    }
  };

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(this.name());
  }

  public abstract AccessMode downGrade();
}
