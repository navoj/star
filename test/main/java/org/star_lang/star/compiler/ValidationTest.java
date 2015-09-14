package org.star_lang.star.compiler;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.CompileDriver;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.catalog.CatalogException;

public class ValidationTest extends SRTest
{
  public void validatorTest(String text, String category)
  {
    try {
      validate(text, category);
    } catch (LanguageException e) {
      Assert.fail("Cannot validate " + text + " as a " + category + "\nbecause " + e.getMessage());
    } catch (Exception e) {
      Assert.fail("exception in validating " + text);
    }
  }

  public void negValidatorTest(String text, String category)
  {
    try {
      validate(text, category);
      Assert.fail(text + " should not be a " + category);
    } catch (LanguageException e) {
    } catch (Exception e) {
      Assert.fail("resource exception in validating " + text);
    }
  }

  @Test
  public void testFloatValidation()
  {
    validatorTest("23.4", "number");
  }

  @Test
  public void testBadExpression()
  {
    negValidatorTest("foo() order", "statement");
  }

  @Test
  public void testStatementValidation()
  {
    validatorTest("def john is 34", "statement");
  }

  @Test
  public void testBadStatementValidation()
  {
    negValidatorTest("john", "statement");
  }

  @Test
  public void testRegexpValidation()
  {
    validatorTest("`.*`", "regexp");
  }

  @Test
  public void testBadRegexpValidation()
  {
    negValidatorTest("\"[.*\"", "regexp");
  }

  public void validate(String text, String validationCategory) throws LanguageException,
      ResourceException, CatalogException, RepositoryException
  {
    ErrorReport errors = new ErrorReport();

    IAbstract parsed = StarRules.parseString(text, Location.nullLoc, errors);
    if (errors.isErrorFree())
      CompileDriver.validate(parsed, errors, Location.nullLoc, validationCategory, repository);

    if (!errors.isErrorFree())
      throw new LanguageException(errors);
  }
}
