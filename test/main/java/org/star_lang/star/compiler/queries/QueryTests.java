package org.star_lang.star.compiler.queries;

import org.junit.Ignore;
import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

/**
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author fgm
 */
public class QueryTests extends SRTest {
  public QueryTests() {
    super(QueryTests.class);
  }

  @Test
  public void testIterQuery() {
    runStar("iterquery.star");
  }

  @Test
  public void testRelations() {
    runStar("tables.star");
  }

  @Test
  public void testSimpleQueries() {
    runStar("queries.star");
  }

  @Test
  public void testReducing() {
    runStar("reducingQueries.star");
  }

  @Test
  public void testRelationInsert() {
    runStar("tableInserts.star");
  }

  @Test
  public void testRelationDelete() {
    runStar("tableDeletes.star");
  }

  @Test
  public void testRelationUpdate() {
    runStar("tableReplace.star");
  }

  @Test
  public void testRelationMerge() {
    runStar("mergerelation.star");
  }

  @Test
  public void testOrderedQueries() {
    runStar("siblings.star");
  }

  @Test
  public void setDisjunctives() {
    runStar("disjunctives.star");
  }

  @Test
  public void testDescending() {
    runStar("descendingAny.star");
  }

  @Test
  @Ignore
  public void testPerf() {
    runStar("perftest.star");
  }

  @Test
  public void testUnique() {
    runStar("uniqueQuery.star");
  }

  @Test
  public void testRecordQueries() {
    runStar("recordqueries.star");
  }

  @Test
  public void testOrderQueries() {
    runStar("complexorder.star");
  }

  @Test
  public void testAnyOfDeflt() {
    runStar("anydeflt.star");
  }

  @Test
  public void testConsUnique() {
    runStar("consUnique.star");
  }

  @Test
  public void testOthers() {
    runStar("others.star");
  }

  @Test
  public void testForms() {
    runStar("queryforms.star");
  }

  @Test
  public void testQueryRelations() {
    runStar("aggRelations.star");
  }

  @Test
  public void testFibRelations() {
    runStar("fibSets.star");
  }

  @Test
  public void testDotName() {
    runStar("dotacc.star");
  }

  @Test
  public void testOtherwise() {
    runStar("otherwiseTest.star");
  }

  @Test
  public void testQueryRef() {
    runStar("queryref.star");
  }

  @Test
  public void testMatching() {
    runStar("matchingQuery.star");
  }

  @Test
  public void whereQuery() {
    runStar("whereQuery.star");
  }

  @Test
  public void dotPtnQuery() {
    runStar("dotPtn.star");
  }

  @Test
  public void testEmployeeSkills() {
    runStar("employeeSkills.star");
  }

  @Test
  public void testConsGrouping() {
    runStar("consGroupBy.star");
  }
}
