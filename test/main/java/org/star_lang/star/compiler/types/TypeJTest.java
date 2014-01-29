package org.star_lang.star.compiler.types;

import org.junit.Test;
import org.star_lang.star.StarMain;
import org.star_lang.star.StarMake;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.CodeRepositoryImpl;
import org.star_lang.star.code.repository.CompositeRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.type.IAlgebraicType;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeContext;
import com.starview.platform.data.type.IValueSpecifier;
import com.starview.platform.data.type.TypeInterface;
import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.URIUtils;

public class TypeJTest extends SRTest
{
  @Test
  public void recordTypeTest() throws EvaluationException, RepositoryException
  {
    String myTypes = "MyTypes is package {type test is test{field has type string}}";
    ResourceURI uri = URIUtils.createQuotedURI("MyTypes", myTypes);
    ErrorReport errors = new ErrorReport();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    CodeRepository localRepo = new CodeRepositoryImpl(loader, false, errors);
    CodeRepository repository = new CompositeRepository(FixedList.create(StarMain.standardRepository()), localRepo,
        COMPILE_ONLY, loader, errors);

    StarMake.compile(repository, uri, StarRules.starCatalog(), errors);

    final ITypeContext context = repository.loaderContext(uri);

    final IAlgebraicType test = (IAlgebraicType) context.getTypeDescription("test");
    final IValueSpecifier valueSpecifier = test.getValueSpecifier("test");
    final IType type = valueSpecifier.getConType();

    assert TypeUtils.isConstructorType(type); // this is unexpected!
    assert TypeUtils.isRecordConstructorType(type); // this is unexpected!
    final TypeInterface record = TypeUtils.getRecordConstructorArgs(type);
    assert 1 == record.getAllFields().size(); // field is visible
    final IType[] arguments = TypeUtils.getConstructorArgTypes(type);
    assert 1 == arguments.length;
  }
}
