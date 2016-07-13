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

public class Token {
  private final String image;
  private final Object value;
  private final TokenType type;
  private final Location loc;
  private final boolean raw;

  public Token(TokenType type, String image, Location loc, boolean raw) {
    this.type = type;
    this.image = image;
    this.value = image;
    this.loc = loc;
    this.raw = raw;
  }

  public Token(TokenType type, long intVal, Location loc, boolean raw) {
    this.type = type;
    this.image = null;
    this.value = intVal;
    this.loc = loc;
    this.raw = raw;
  }

  public Token(TokenType type, double fltVal, Location loc, boolean raw) {
    this.type = type;
    this.image = null;
    this.loc = loc;
    this.value = fltVal;
    this.raw = raw;
  }

  public Token(TokenType type, Object value, Location loc, boolean raw) {
    this.type = type;
    this.image = null;
    this.value = value;
    this.loc = loc;
    this.raw = raw;
  }

  public enum TokenType {
    identifier, integer, longint, floating, string, blob, regexp, terminal
  }

  public String getImage() {
    return image;
  }

  public boolean isRaw() {
    return raw;
  }

  public boolean isIdentifier(String id) {
    return type == TokenType.identifier && image.equals(id);
  }

  public long getIntVal() {
    return (Long) value;
  }

  public Double getFloatingValue() {
    return (Double) value;
  }

  public Object getValue() {
    return value;
  }

  public TokenType getType() {
    return type;
  }

  public Location getLoc() {
    return loc;
  }

  public void display() {
    System.out.println(loc.toString() + ":" + toString());
  }

  @Override
  public String toString() {
    switch (type) {
      case identifier:
        return image;
      case integer:
      case longint:
        return Long.toString((Long) value);
      case floating:
        return value.toString();
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
