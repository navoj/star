package org.star_lang.star.code.repository;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.value.ResourceURI;

/**
 * A node in the code repository
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
public class RepositoryNodeImpl implements RepositoryNode
{
  protected String hash;
  private CodeCatalog code;
  private final ResourceURI uri;

  public RepositoryNodeImpl(ResourceURI uri, String hash, CodeCatalog code)
  {
    this.uri = uri;
    this.hash = hash;
    this.code = code;
  }

  @Override
  public CodeCatalog getCode()
  {
    return code;
  }

  @Override
  public ResourceURI getUri()
  {
    return uri;
  }

  @Override
  public String getHash()
  {
    return hash;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (hash != null)
      disp.appendQuoted(hash);
    else
      disp.append("??");
    disp.append("->");
    code.prettyPrint(disp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
