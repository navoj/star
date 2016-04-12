package org.star_lang.star.compiler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.star_lang.star.actors.ActorTest;
import org.star_lang.star.arith.ArithTests;
import org.star_lang.star.array.ArrayTests;
import org.star_lang.star.asynchio.TestIO;
import org.star_lang.star.atomic.AtomicCellTests;
import org.star_lang.star.compiler.computation.ComputationTests;
import org.star_lang.star.compiler.computation.TaskExpTest;
import org.star_lang.star.compiler.contracts.ContractTest;
import org.star_lang.star.compiler.formatting.FormatTest;
import org.star_lang.star.compiler.library.LibraryTestSuite;
import org.star_lang.star.compiler.lists.ListTests;
import org.star_lang.star.compiler.macro.MacroTests;
import org.star_lang.star.compiler.map.MapTests;
import org.star_lang.star.compiler.misc.MiscTests;
import org.star_lang.star.compiler.overload.OverloadTests;
import org.star_lang.star.compiler.queries.QueryTests;
import org.star_lang.star.compiler.regexp.RegexpTests;
import org.star_lang.star.compiler.sets.SetTests;
import org.star_lang.star.compiler.spawn.SpawnTest;
import org.star_lang.star.compiler.types.TypeJTest;
import org.star_lang.star.compiler.types.TypeTests;
import org.star_lang.star.date.DateTests;
import org.star_lang.star.external.ExternalURITests;
import org.star_lang.star.imports.ImportTest;
import org.star_lang.star.model.MetaModelTest;
import org.star_lang.star.patterns.PtnTest;
import org.star_lang.star.queries.QueryTest;
import org.star_lang.star.queries.TestUpdate;
import org.star_lang.star.relations.TestRelations;
import org.star_lang.star.repository.RepositorySuite;
import org.star_lang.star.sequences.SequenceTest;
import org.star_lang.star.stdlib.StdLibTests;
import org.star_lang.star.string.StringTests;
import org.star_lang.star.task.TaskTest;
import org.star_lang.star.tests.HelloTest;
import org.star_lang.star.transformer.TransformerTest;
import org.star_lang.star.volunteer.VolunteerTest;

/**
 * Copyright (c) 2015. Francis G. McCabe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@RunWith(Suite.class)
@SuiteClasses({ActorTest.class, ArithTests.class, ArrayTests.class, AtomicCellTests.class, CompilerTest.class,
    ContractTest.class, DateTests.class, ExternalURITests.class, ForLoopTests.class, FormatTest.class, HelloTest.class,
    ImportTest.class, JavaImportTest.class, LibraryTestSuite.class, ListTests.class, MacroTests.class, MapTests.class,
    MetaModelTest.class, MiscTests.class, OverloadTests.class, PtnTest.class, PtnTests.class, QueryTest.class,
    QueryTests.class, RdfMacroTest.class, RegexpTests.class, RepositorySuite.class, SequenceTest.class, SpawnTest.class,
    StdLibTests.class, StringTests.class, TestRelations.class, TestUpdate.class, TransformerTest.class, SetTests.class,
    TypesResolutionPOCTest.class, ComputationTests.class, TaskExpTest.class, TaskTest.class, TypeTests.class, TypeJTest.class,
    UnifyTests.class, URITests.class, ValidationTest.class, VolunteerTest.class, TestIO.class})
public class CompilerTestSuite {
}
