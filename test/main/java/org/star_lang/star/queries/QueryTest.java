package org.star_lang.star.queries;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.star_lang.star.LanguageException;
import org.star_lang.star.ParseQuery;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.CodeRepositoryImpl;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.TemplateString;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.CatalogException;

/*  * Copyright (c) 2015. Francis G. McCabe  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file  * except in compliance with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language governing  * permissions and limitations under the License.  */
public class QueryTest
{
  private static final String SAMPLE_TYPE = "{names has type list of string}";
  private static final String SAMPLE_DATA = "{names=list of [\"fred\",\"george\"]}";

  private static final String PORT_DATA = "{fun Query(Fn,Qt,Fr) is Fn(" + SAMPLE_DATA + ")}";
  private static final String PORT_TYPE = "{Query has type ((" + SAMPLE_TYPE
      + ")=>%t,()=>quoted,()=>dictionary of (string,any))=>%t}";

  private static final String dataSet = "$pkg is package{\n  $imprt$var has type #($type)#;\n  def $var is #($exp)#;\n}\n";

  protected static IValue parseDataSet(String exp, String imprt, String type, CodeRepository repository)
  {
    /**
     * Construct the following program in a string:
     * 
     * <pre>
     * lbl is package{
     *   <import>
     *   data has type <type>
     *   data is <exp>
     * }
     * </pre>
     */

    String var = GenSym.genSym("v");
    String pkgName = GenSym.genSym("pkg");
    Map<String, String> params = new HashMap<>();

    params.put("pkg", pkgName);
    params.put("type", type);
    params.put("imprt", (imprt == null ? "" : "import " + imprt + ";\n  "));
    params.put("exp", exp);
    params.put("var", var);

    String pkg = TemplateString.stringTemplate(dataSet, params);

    try {
      ResourceURI uri = URIUtils.createQuotedURI(pkgName, pkg);
      ErrorReport errors = new ErrorReport();

      IValue fun = StarCompiler.localCompile(repository, uri, StarRules.starCatalog(), errors);

      if (errors.isErrorFree() && fun instanceof IRecord)
        return ((IRecord) fun).getMember(var);
    } catch (EvaluationException e) {
      e.printStackTrace();
    } catch (ResourceException e) {
      e.printStackTrace();
    } catch (CatalogException e) {
      e.printStackTrace();
    } catch (RepositoryException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Test
  public void testSimpleData()
  {
    ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();
    ErrorReport errors = new ErrorReport();
    CodeRepository repository = new CodeRepositoryImpl(parentLoader, true, errors);
    IValue data = parseDataSet(SAMPLE_DATA, null, SAMPLE_TYPE, repository);
    assert data instanceof IRecord;
    assert ((IRecord) data).getMember("names") instanceof IArray;
    System.out.println(data);
  }

  @Test
  public void testSimpleQuery() throws EvaluationException, LanguageException
  {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    ErrorReport errors = new ErrorReport();
    CodeRepository repository = new CodeRepositoryImpl(loader, true, errors);

    IFunction qFun = ParseQuery.parseExpression("list of {all X where X in names}", null, SAMPLE_TYPE, repository);
    assert qFun != null;

    IValue data = parseDataSet(SAMPLE_DATA, null, SAMPLE_TYPE, repository);

    IValue result = qFun.enter(data);
    assert result instanceof IArray;
    System.out.println(result);
  }

  @Test
  public void testSimpleSAQuery() throws EvaluationException, LanguageException
  {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    ErrorReport errors = new ErrorReport();
    CodeRepository repository = new CodeRepositoryImpl(loader, true, errors);

    IFunction qFun = ParseQuery.parseQuery("all X where X in names", new HashMap<String, IValue>(), null, SAMPLE_TYPE,
        repository);
    assert qFun != null;

    IValue data = parseDataSet(PORT_DATA, null, PORT_TYPE, repository);

    IValue result = qFun.enter(data);
    assert result instanceof IArray;
    System.out.println(result);
  }

  private static final String PAIR_DATA = "{ names=list of [(\"fred\",\"george\"), (\"fred\",\"alfred\")]}";
  private static final String PAIR_TYPE = "{names has type list of ((string,string))}";
  private static final String PAIR_PORT_DATA = "{fun Query(Fn,Qt,Fr) is Fn(" + PAIR_DATA + ")}";
  private static final String PAIR_PORT_TYPE = "{Query has type ((" + PAIR_TYPE
      + ")=>%t,()=>quoted,()=>dictionary of (string,any))=>%t}";

  @Test
  public void testQueryWithFree() throws EvaluationException, LanguageException
  {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    ErrorReport errors = new ErrorReport();
    CodeRepository repository = new CodeRepositoryImpl(loader, true, errors);

    HashMap<String, IValue> freeVars = new HashMap<String, IValue>();
    freeVars.put("g", Factory.newString("george"));
    freeVars.put("m", Factory.newString("mary"));
    IFunction qFun = ParseQuery.parseQuery("all X where (X,g) in names", freeVars, null, PAIR_TYPE, repository);
    assert qFun != null;

    IValue data = parseDataSet(PAIR_PORT_DATA, null, PAIR_PORT_TYPE, repository);

    IValue result = qFun.enter(data);
    assert result instanceof IArray;
    System.out.println(result);
  }
}
