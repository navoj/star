package org.star_lang.star;

import java.io.StringReader;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.grammar.OpGrammar;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.MemoryCatalog;

/**
 * Master access class for compiling and running StarRules programs
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
public class StarRules {

  public static IAbstract parseString(String text, Location loc, ErrorReport errors) {
    OpGrammar parser = new OpGrammar(Operators.operatorRoot(), errors);

    return parser.parse(loc.getUri(), new StringReader(text), loc);
  }

  public static void main(String args[]) {
    StarMain.main(args);
  }

  public static Catalog starCatalog() {
    Catalog cat = new MemoryCatalog("star", URIUtils.create("star", "."));

    try {
      cat.addEntry("base", URIUtils.parseUri("std:base.star"));
      cat.addEntry("option", URIUtils.parseUri("std:option.star"));
      cat.addEntry("validate", URIUtils.parseUri("std:validate.star"));
      cat.addEntry("compute", URIUtils.parseUri("std:compute.star"));
      cat.addEntry("maybe", URIUtils.parseUri("std:maybe.star"));
      // cat.addEntry("monad", URIUtils.parseUri("std:monad.star"));
      cat.addEntry("star", URIUtils.parseUri("std:star.star"));
      cat.addEntry("collections", URIUtils.parseUri("std:collections.star"));
      cat.addEntry("sequences", URIUtils.parseUri("std:sequences.star"));
      cat.addEntry("arithmetic", URIUtils.parseUri("std:arithmetic.star"));
      cat.addEntry("iterators", URIUtils.parseUri("std:iterators.star"));
      cat.addEntry("casting", URIUtils.parseUri("std:casting.star"));
      cat.addEntry("bitstring", URIUtils.parseUri("std:bitstring.star"));
      cat.addEntry("treemap", URIUtils.parseUri("std:treemap.star"));
      cat.addEntry("arraymap", URIUtils.parseUri("std:arraymap.star"));
      cat.addEntry("arrays", URIUtils.parseUri("std:arrays.star"));
      cat.addEntry("altarrays", URIUtils.parseUri("std:altarrays.star"));
      cat.addEntry("maps", URIUtils.parseUri("std:maps.star"));
      cat.addEntry("sets", URIUtils.parseUri("std:sets.star"));
      cat.addEntry("range", URIUtils.parseUri("std:range.star"));
      cat.addEntry("indexed", URIUtils.parseUri("std:indexed.star"));
      cat.addEntry("queue", URIUtils.parseUri("std:queue.star"));
      cat.addEntry("cons", URIUtils.parseUri("std:cons.star"));
      cat.addEntry("actors", URIUtils.parseUri("std:actors.star"));
      cat.addEntry("macrosupport", URIUtils.parseUri("std:macrosupport.star"));
      cat.addEntry("quoteable", URIUtils.parseUri("std:quoteable.star"));
      cat.addEntry("fileio", URIUtils.parseUri("std:fileio.star"));
      cat.addEntry("dateNtime", URIUtils.parseUri("std:dateNtime.star"));
      cat.addEntry("iterable", URIUtils.parseUri("std:iterable.star"));
      cat.addEntry("updateable", URIUtils.parseUri("std:updateable.star"));
      cat.addEntry("finger", URIUtils.parseUri("std:finger.star"));
      cat.addEntry("fingerPrelude", URIUtils.parseUri("std:fingerPrelude.star"));
      // cat.addEntry("priority", URIUtils.parseUri("std:priority.star"));
      // cat.addEntry("signatures", URIUtils.parseUri("std:signatures.star"));
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
      // cat.addEntry("custodians", URIUtils.parseUri("std:custodians.star"));
      // cat.addEntry("buffer", URIUtils.parseUri("std:io/buffer.star"));
      // cat.addEntry("bytebuffer",
      // URIUtils.parseUri("std:io/bytebuffer.star"));
      // cat.addEntry("primitiveIO",
      // URIUtils.parseUri("std:io/primitiveIO.star"));
      // cat.addEntry("serialization",
      // URIUtils.parseUri("std:io/serialization.star"));
      cat.addEntry("rdf", URIUtils.parseUri("std:rdf.star"));
      cat.addEntry("worksheet", URIUtils.parseUri("std:worksheet.star"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cat;
  }
}
