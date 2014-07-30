package org.star_lang.star;

import java.io.StringReader;

import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.format.Formatter;
import org.star_lang.star.compiler.grammar.OpGrammar;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.MemoryCatalog;

/**
 * Master access class for compiling and running StarRules programs
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
public class StarRules
{

  public static String format(CodeRepository repository, ResourceURI uri, Catalog catalog, ErrorReport errors)
      throws LanguageException, ResourceException, CatalogException
  {
    try {
      return Formatter.formatSource(uri, errors, StandardNames.PACKAGE, repository, catalog);
    } catch (Exception e) {
      errors.reportError("problem in formatting ", Location.location(uri));
      return null;
    }
  }

  public static IAbstract parseString(String text, Location loc, ErrorReport errors)
  {
    OpGrammar parser = new OpGrammar(Operators.operatorRoot(), errors);

    return parser.parse(loc.getUri(), new StringReader(text), loc);
  }

  public static void main(String args[])
  {
    StarMain.main(args);
  }

  public static Catalog starCatalog()
  {
    Catalog cat = new MemoryCatalog("star", URIUtils.create("star", "."));

    try {
      cat.addEntry("base", URIUtils.parseUri("std:base.star"));
      cat.addEntry("option", URIUtils.parseUri("std:option.star"));
      cat.addEntry("validate", URIUtils.parseUri("std:validate.star"));
      cat.addEntry("compute", URIUtils.parseUri("std:compute.star"));
      cat.addEntry("maybe", URIUtils.parseUri("std:maybe.star"));
//      cat.addEntry("monad", URIUtils.parseUri("std:monad.star"));
      cat.addEntry("star", URIUtils.parseUri("std:star.star"));
      cat.addEntry("collections", URIUtils.parseUri("std:collections.star"));
      cat.addEntry("sequences", URIUtils.parseUri("std:sequences.star"));
      cat.addEntry("arithmetic", URIUtils.parseUri("std:arithmetic.star"));
      cat.addEntry("iterators", URIUtils.parseUri("std:iterators.star"));
      cat.addEntry("casting", URIUtils.parseUri("std:casting.star"));
      cat.addEntry("bitstring", URIUtils.parseUri("std:bitstring.star"));
      cat.addEntry("dictionary", URIUtils.parseUri("std:dictionary.star"));
      cat.addEntry("arraymap", URIUtils.parseUri("std:arraymap.star"));
      cat.addEntry("arrays", URIUtils.parseUri("std:arrays.star"));
      cat.addEntry("altarrays", URIUtils.parseUri("std:altarrays.star"));
      cat.addEntry("maps", URIUtils.parseUri("std:maps.star"));
      cat.addEntry("range", URIUtils.parseUri("std:range.star"));
      cat.addEntry("relations", URIUtils.parseUri("std:relations.star"));
      cat.addEntry("indexed", URIUtils.parseUri("std:indexed.star"));
      cat.addEntry("queue", URIUtils.parseUri("std:queue.star"));
      cat.addEntry("cons", URIUtils.parseUri("std:cons.star"));
      cat.addEntry("actors", URIUtils.parseUri("std:actors.star"));
      cat.addEntry("macrosupport", URIUtils.parseUri("std:macrosupport.star"));
      cat.addEntry("quoteable", URIUtils.parseUri("std:quoteable.star"));
      cat.addEntry("formatter", URIUtils.parseUri("std:formatter.star"));
      cat.addEntry("fileio", URIUtils.parseUri("std:fileio.star"));
      cat.addEntry("dateNtime", URIUtils.parseUri("std:dateNtime.star"));
      cat.addEntry("iterable", URIUtils.parseUri("std:iterable.star"));
      cat.addEntry("updateable", URIUtils.parseUri("std:updateable.star"));
      cat.addEntry("finger", URIUtils.parseUri("std:finger.star"));
      cat.addEntry("fingerPrelude", URIUtils.parseUri("std:fingerPrelude.star"));
//      cat.addEntry("priority", URIUtils.parseUri("std:priority.star"));
//      cat.addEntry("signatures", URIUtils.parseUri("std:signatures.star"));
      cat.addEntry("strings", URIUtils.parseUri("std:strings.star"));
      cat.addEntry("json", URIUtils.parseUri("std:json.star"));
      cat.addEntry("infoset", URIUtils.parseUri("std:json.star"));
      cat.addEntry("folding", URIUtils.parseUri("std:folding.star"));
      cat.addEntry("threads", URIUtils.parseUri("std:threads.star"));
      cat.addEntry("task", URIUtils.parseUri("std:task.star"));
      cat.addEntry("cml0", URIUtils.parseUri("std:cml0.star"));
      cat.addEntry("cml", URIUtils.parseUri("std:cml.star"));
      cat.addEntry("cmlSpinLock", URIUtils.parseUri("std:cmlSpinLock.star"));
      cat.addEntry("cmlAtomicRef", URIUtils.parseUri("std:cmlAtomicRef.star"));
      cat.addEntry("cmlQueue", URIUtils.parseUri("std:cmlQueue.star"));
      cat.addEntry("fibers", URIUtils.parseUri("std:fibers.star"));
      cat.addEntry("concurrency", URIUtils.parseUri("std:concurrency.star"));
//      cat.addEntry("custodians", URIUtils.parseUri("std:custodians.star"));
//      cat.addEntry("buffer", URIUtils.parseUri("std:io/buffer.star"));
//      cat.addEntry("bytebuffer", URIUtils.parseUri("std:io/bytebuffer.star"));
//      cat.addEntry("primitiveIO", URIUtils.parseUri("std:io/primitiveIO.star"));
//      cat.addEntry("serialization", URIUtils.parseUri("std:io/serialization.star"));
      cat.addEntry("rdf", URIUtils.parseUri("std:rdf.star"));
      cat.addEntry("worksheet", URIUtils.parseUri("std:worksheet.star"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cat;
  }
}
