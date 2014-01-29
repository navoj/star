package org.star_lang.star.model;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IRecord;
import com.starview.platform.data.type.IAlgebraicType;
import com.starview.platform.data.type.ITypeContext;
import com.starview.platform.data.type.ITypeDescription;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.catalog.CatalogException;
import com.starview.platform.resource.catalog.URIBasedCatalog;

/**
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
