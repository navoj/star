package org.star_lang.star.compiler;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.CompileDriver;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;

import com.starview.platform.data.type.Location;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.catalog.CatalogException;

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
    validatorTest("john is 34", "statement");
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
