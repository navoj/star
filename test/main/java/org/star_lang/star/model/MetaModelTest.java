package org.star_lang.star.model;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.string.runtime.ValueDisplay;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.URIBasedCatalog;

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

public class MetaModelTest extends SRTest
{
  public MetaModelTest()
  {
    super(MetaModelTest.class);
  }

  @Test
  public void testMetaModel()
  {
    runStar("metamodeldefn.star");
  }

  @Test
  public void testModelTypes()
  {
    runStar("modeltypes.star");
  }

  @Test
  public void testTypes() throws EvaluationException, CatalogException, ResourceException, RepositoryException
  {
    ResourceURI uri = ResourceURI.parseURI("test:modelperson.star");

    ErrorReport errors = new ErrorReport();

    StarCompiler.localCompile(repository, uri, new URIBasedCatalog(uri, StarRules.starCatalog()), errors);

    ITypeContext ctx = repository.loaderContext(uri);

    ITypeDescription desc = ctx.getTypeDescription("Chap");

    IRecord record = Factory.newRecord((IAlgebraicType) desc, "someone", "name", Factory.newString("MyName"), "age",
        Factory.newInt(12));

    Assert.assertEquals("MyName", Factory.stringValue(record.getMember("name")));

    record.setMember("name", Factory.newString("!!!"));

    record = record.copy();
    System.err.println(ValueDisplay.display(record));

    Assert.assertEquals("!!!", Factory.stringValue(record.getMember("name")));

    if (!errors.isErrorFree())
      Assert.fail(errors.toString());
  }
}
