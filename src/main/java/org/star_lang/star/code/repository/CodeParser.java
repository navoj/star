package org.star_lang.star.code.repository;

import java.io.InputStream;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;

/**
 * Implemented by parsers for specific forms of code tree.
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
   *          TODO
   * @param errors
   * @return
   */
  CodeTree parse(ResourceURI uri, ErrorReport errors) throws ResourceException;

  CodeTree parse(ResourceURI uri, InputStream input, ErrorReport errors) throws ResourceException;
}
