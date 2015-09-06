package org.star_lang.star;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.data.type.Location;

/**
 * LanguageException is used when reporting something wrong with a StarRules compilation
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
