package org.star_lang.star.compiler.queries;

import org.junit.Ignore;
import org.junit.Test;
import org.star_lang.star.compiler.SRTest;
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
