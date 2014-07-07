package org.star_lang.star.data.type;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

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
public class Kind implements PrettyPrintable
{
  public enum Mode {
    unknown, type, typefunction
  };

  private final Mode mode;

  public static Kind unknown = new Kind(Mode.unknown);
  public static Kind type = new Kind(Mode.type);

  private final int arity;

  private Kind(int arity)
  {
    this.arity = arity;
    this.mode = Mode.typefunction;
  }

  private Kind(Mode mode)
  {
    this.arity = 0;
    this.mode = mode;
  }

  public Mode mode()
  {
    return mode;
  }

  public int arity()
  {
    return arity;
  }

  public boolean checkKind(Kind other)
  {
    switch (mode) {
    case unknown:
      return true;
    case type:
      switch (other.mode) {
      case typefunction:
        return false;
      case unknown:
      case type:
      default:
        return true;
      }
    case typefunction:
      switch (other.mode) {
      case unknown:
        return true;
      case typefunction:
      default:
        return other.arity() == arity;
      case type:
        return false;
      }
    default:
      return false;
    }
  }

  public boolean check(int arity)
  {
    switch (mode) {
    case unknown:
      return true;
    case type:
      return arity == 0;
    case typefunction:
    default:
      return arity == this.arity;
    }
  }

  @Override
  public int hashCode()
  {
    return mode.name().hashCode() * 37 + arity;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Kind) {
      Kind k = (Kind) obj;
      return k.mode == mode && k.arity == arity;
    }
    return false;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    switch (mode) {
    case type:
      disp.appendWord(StandardNames.TYPE);
      return;
    case unknown:
      disp.appendWord("unknown kind");
      return;
    case typefunction:
      switch (arity) {
      case 0:
        disp.appendWord(StandardNames.TYPE);
        return;
      case 1:
        disp.appendWord(StandardNames.TYPE);
        disp.appendWord(StandardNames.OF);
        disp.appendWord(StandardNames.TYPE);
        return;
      default:
        disp.appendWord(StandardNames.TYPE);
        disp.appendWord(StandardNames.OF);
        disp.append("(");
        String sep = "";
        for (int ix = 0; ix < arity; ix++) {
          disp.append(sep);
          sep = ",";
          disp.appendWord(StandardNames.TYPE);
        }
        disp.append(")");
      }
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  public static Kind kind(int arity)
  {
    if (arity == 0)
      return type;
    else
      return new Kind(arity);
  }
}
