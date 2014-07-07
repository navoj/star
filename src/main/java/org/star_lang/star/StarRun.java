package org.star_lang.star;

import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.catalog.CatalogException;

/**
 * Top-level driver for running StarRules programs
 * 
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
public class StarRun
{
  private static CodeRepository standardRepository;

  /**
   * Entry point when all you need to do is run a pre-compiled program
   * 
   * @param args
   */
  public static void main(String args[])
  {
    if (args.length > 0) {
      String source = args[0];
      try {
        String nArgs[] = new String[args.length - 1];
        for (int ix = 0; ix < nArgs.length; ix++)
          nArgs[ix] = args[ix + 1];

        ResourceURI uri = ResourceURI.parseURI(source);
        ResourceURI sourceURI = ApplicationProperties.wdURI.resolve(uri);

        IValue pkgArgs[] = new IValue[nArgs.length];
        for (int ix = 0; ix < nArgs.length; ix++)
          pkgArgs[ix] = Factory.newString(nArgs[ix]);

        StarMain.run(standardRepository(), sourceURI, pkgArgs);
      } catch (LanguageException e) {
        System.err.println(e.getMessages().toString());
      } catch (EvaluationException e) {
        System.err.println("Run-time error in " + args[0] + "\n" + e.getMessage() + " at " + e.getLoc());
      } catch (ResourceException e) {
        System.err.println(source + " cannot be parsed as a uri");
      } catch (CatalogException | RepositoryException e) {
        System.err.println(source + " not accessible");
      }
    } else
      System.err.println("usage: <> sourceFile");
  }


  public synchronized static CodeRepository standardRepository()
  {
    if (standardRepository == null)
      standardRepository = RepositoryManager.setupStandardRepository(Thread.currentThread().getContextClassLoader());
    return standardRepository;
  }
}
