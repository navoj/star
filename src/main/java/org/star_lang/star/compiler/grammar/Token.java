package org.star_lang.star.compiler.grammar;

import org.star_lang.star.data.type.Location;

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
public class Token
{
  private final String image;
  private final Object value;
  private final TokenType type;
  private final Location loc;
  private final boolean raw;

  public Token(TokenType type, String image, Location loc, boolean raw)
  {
    this.type = type;
    this.image = image;
    this.value = image;
    this.loc = loc;
    this.raw = raw;
  }

  public Token(TokenType type, long intVal, Location loc, boolean raw)
  {
    this.type = type;
    this.image = null;
    this.value = intVal;
    this.loc = loc;
    this.raw = raw;
  }

  public Token(TokenType type, double fltVal, Location loc, boolean raw)
  {
    this.type = type;
    this.image = null;
    this.loc = loc;
    this.value = fltVal;
    this.raw = raw;
  }

  public Token(TokenType type, Object value, Location loc, boolean raw)
  {
    this.type = type;
    this.image = null;
    this.value = value;
    this.loc = loc;
    this.raw = raw;
  }

  public enum TokenType {
    identifier, integer, longint, decimal, floating, character, string, blob, regexp, terminal
  }

  public String getImage()
  {
    return image;
  }

  public boolean isRaw()
  {
    return raw;
  }

  public boolean isIdentifier(String id)
  {
    return type == TokenType.identifier && image.equals(id);
  }

  public long getIntVal()
  {
    return (Long) value;
  }

  public Double getFloatingValue()
  {
    return (Double) value;
  }

  public Object getValue()
  {
    return value;
  }

  public TokenType getType()
  {
    return type;
  }

  public Location getLoc()
  {
    return loc;
  }

  public void display()
  {
    System.out.println(loc.toString() + ":" + toString());
  }

  @Override
  public String toString()
  {
    switch (type) {
    case identifier:
      return image;
    case integer:
    case longint:
      return Long.toString((Long) value);
    case floating:
      return value.toString();
    case decimal:
      return value.toString() + "a";
    case character:
      return "'" + image + "'";
    case string:
      return "\"" + image + "\"";
    case regexp:
      return "`" + image + "`";
    case blob:
      return "0'" + image.length() + "\"" + image;
    case terminal:
      return "<eof>";
    default:
      return "<unknown>";
    }
  }
}
