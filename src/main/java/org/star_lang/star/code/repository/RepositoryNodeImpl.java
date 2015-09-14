package org.star_lang.star.code.repository;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.value.ResourceURI;

/**
 * A node in the code repository
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
