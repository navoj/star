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
        e.printStackTrace();
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
