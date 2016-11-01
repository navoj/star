package org.star_lang.star.compiler.formatting;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarMain;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.MemoryCatalog;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

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
public class FormatTest extends SRTest
{
  protected String formatTest(String original)
  {
    ResourceURI quoted = URIUtils.createQuotedURI("FmtTest1", original);
    ErrorReport errors = new ErrorReport();

    try {
      return StarRules.format(StarMain.standardRepository(), quoted, StarRules.starCatalog(), errors);
    } catch (LanguageException | ResourceException | CatalogException e) {
      fail(e.getMessage());
      return null;
    }
  }

  protected void formatTest(String original, String target)
  {
    String result = formatTest(original);
    System.err.println(result);
    assertEquals(result, target);
  }

  @Test
  public void simpleFormatTest() throws LanguageException, ResourceException, CatalogException
  {
    String original = "FmtTest1 is package {\n      type Person is Person {\n        age has type integer;\n        };\ntype Person2 is P2 {\n        age has type integer;\n        }\n        }";
    String target = "FmtTest1 is package {\n  type Person is Person {\n    age has type integer;\n  };\n  type Person2 is P2 {\n    age has type integer;\n  }\n}\n";

    formatTest(original, target);
  }

  @Test
  public void formatWithImport() throws LanguageException, ResourceException, CatalogException, RepositoryException
  {
    String original = "FmtTest2 is package {\n  import foo;\nspecial{var I:=0;var J:=I+1;}}";
    String expected = "FmtTest2 is package {\n  import foo;\n  special{\n            var I:=0;\n            var J:=I+1;\n  }\n}\n";
    ResourceURI quoted = URIUtils.createQuotedURI("FmtTest2", original);
    ErrorReport errors = new ErrorReport();

    Catalog testCatalog = new MemoryCatalog("catalog", null, null, StarRules.starCatalog(), null);

    String fooSrc = "foo is package{ #special{ ?B } :: statement :- B ;* statement; #special{?B}::statement --> B:: {indent:+10; breakBefore:true; breakAfter:true}; #special{?B}==>B; }";
    ResourceURI fooUri = URIUtils.createQuotedURI("foo", fooSrc);

    testCatalog.addEntry("foo", fooUri);

    StarCompiler.compile(fooUri, testCatalog, repository);

    String result = StarRules.format(StarMain.standardRepository(), quoted, testCatalog, errors);

    System.err.println(result);
    Assert.assertEquals(result, expected);
  }

  @Test
  public void testPort() {
    String original = "def CPUEventsIn is respond {\n        /* This event rule fires with the CPU usage is > 95% */\n        on (V where V.Usage > 95) on DATA do {\n            logMsg(info,\"CPU Alert $V\");\n            notify CPUAlerts with (V cast any) on DATA;\n        }\n    };";
    String expected = "def CPUEventsIn is respond {\n/* This event rule fires with the CPU usage is > 95% */\n  on (V where\n      V.Usage > 95) on DATA do {\n    logMsg(info,\"CPU Alert $V\");\n    notify CPUAlerts with (V cast any) on DATA;\n  }\n};\n";

    formatTest(original, expected);
  }
}
