package org.star_lang.star;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.data.type.Location;

/**
 * LanguageException is used when reporting something wrong with a StarRules compilation
 * 
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
public class LanguageException extends Exception
{
  private static final long serialVersionUID = 1L;
  private final ErrorReport messages;

  public LanguageException(ErrorReport messages)
  {
    super(messages.toString());
    this.messages = messages;
  }

  public LanguageException(ErrorReport messages, String msg, Location... locs)
  {
    super(messages.toString());
    this.messages = messages;
    messages.reportError(msg, locs);
  }

  public LanguageException(String msg)
  {
    this(msg, Location.nullLoc);
  }

  public LanguageException(String msg, Location... locs)
  {
    super(msg);

    messages = new ErrorReport();
    messages.reportError(msg, locs);
  }

  public ErrorReport getMessages()
  {
    return messages;
  }

  @Override
  public String getMessage()
  {
    return messages.toString();
  }

}
