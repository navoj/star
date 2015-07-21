package org.star_lang.star.code.repository;

import java.io.InputStream;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;


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

public interface CodeParser
{
  /**
   * What is the standard extension for this form of code tree?
   * 
   * @return
   */
  String getExtension();

  /**
   * Parse resource identified by the uri
   * 
   * @param uri
   *          entity to parse
   * @param errors
   * @return
   */
  CodeTree parse(ResourceURI uri, ErrorReport errors) throws ResourceException;

  CodeTree parse(ResourceURI uri, InputStream input, ErrorReport errors) throws ResourceException;
}
