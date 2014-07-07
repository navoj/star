package org.star_lang.star.data.type;

import java.util.Arrays;
import java.util.List;

import org.star_lang.star.compiler.util.StringUtils;

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
