package org.star_lang.star.data.type;

import java.util.Arrays;
import java.util.List;

import org.star_lang.star.compiler.util.StringUtils;

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
public class TypeConstraintException extends Exception
{
  private final Location locs[];
  private final List<?> words;

  public TypeConstraintException(String detail, Location... locs)
  {
    super(detail);
    this.locs = locs;
    this.words = Arrays.asList(detail);
  }

  public TypeConstraintException(List<?> words, Location... locs)
  {
    super(StringUtils.msg(words));
    this.locs = locs;
    this.words = words;
  }

  public Location[] getLocs()
  {
    return locs;
  }

  public List<?> getWords()
  {
    return words;
  }

}
