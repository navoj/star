package org.star_lang.star.compiler;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
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
@SuppressWarnings("serial")
public class CompilerMessage implements PrettyPrintable
{
  private final MsgType msgType;
  private final String msg;
  private final Location[] locs;

  public CompilerMessage(MsgType msgType, String msg, Location... locs)
  {
    assert msgType != null && msg != null && locs != null;
    this.msgType = msgType;
    this.msg = msg;
    this.locs = locs;
  }

  public MsgType getMsgType()
  {
    return msgType;
  }

  public String getMsg()
  {
    return msg;
  }

  public Location[] getLocs()
  {
    return locs;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof CompilerMessage) {
      CompilerMessage other = (CompilerMessage) obj;
      if (other.msg.equals(msg) && other.locs.length == locs.length) {
        for (int ix = 0; ix < locs.length; ix++)
          if (!locs[ix].equals(other.locs[ix]))
            return false;
        return true;
      }
    }
    return false;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(msgType.name());
    disp.append(": ");
    disp.append(msg);
    for (Location loc : locs) {
      disp.append("\n at ");
      loc.prettyPrint(disp);
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public int hashCode()
  {
    int hash = 37 * msg.hashCode();
    for (Location loc : locs)
      hash = hash * 37 + loc.hashCode();
    return hash;
  }
}
