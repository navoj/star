package org.star_lang.star.compiler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.star_lang.star.actors.ActorTest;
import org.star_lang.star.arith.ArithTests;
import org.star_lang.star.array.ArrayTests;
import org.star_lang.star.asynchio.TestIO;
import org.star_lang.star.atomic.AtomicCellTests;
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
import org.star_lang.star.sequences.SortTest;
import org.star_lang.star.stdlib.StdLibTests;
import org.star_lang.star.string.StringTests;
import org.star_lang.star.task.TaskTest;
import org.star_lang.star.tests.HelloTest;
import org.star_lang.star.transformer.TransformerTest;
import org.star_lang.star.volunteer.VolunteerTest;

/**
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
@RunWith(Suite.class)
@SuiteClasses({ ActorTest.class, ArithTests.class, ArrayTests.class, AtomicCellTests.class, CompilerTest.class,
    ContractTest.class, DateTests.class, ExternalURITests.class, ForLoopTests.class, FormatTest.class, HelloTest.class,
    ImportTest.class, JavaImportTest.class, LibraryTestSuite.class, ListTests.class, MacroTests.class, MapTests.class,
    MetaModelTest.class, MiscTests.class, OverloadTests.class, PtnTest.class, PtnTests.class, QueryTest.class,
    QueryTests.class, RdfMacroTest.class, RegexpTests.class, RepositorySuite.class, SortTest.class, SpawnTest.class,
    StdLibTests.class, StringTests.class, TestRelations.class, TestUpdate.class, TransformerTest.class,
    TypesResolutionPOCTest.class, TaskExpTest.class, TaskTest.class, TypeTests.class, TypeJTest.class,
    UnifyTests.class, URITests.class, ValidationTest.class, VolunteerTest.class, TestIO.class })
public class CompilerTestSuite
{
}
