package org.star_lang.star.compiler.canonical.compile;

import org.star_lang.star.compiler.canonical.CastPtn;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.MatchingPattern;
import org.star_lang.star.compiler.canonical.PatternApplication;
import org.star_lang.star.compiler.canonical.RecordPtn;
import org.star_lang.star.compiler.canonical.RegExpPattern;
import org.star_lang.star.compiler.canonical.ScalarPtn;
import org.star_lang.star.compiler.canonical.TransformPattern;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.WherePattern;

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
public class PatternCompile implements
    TransformPattern<FrameState, FrameState, FrameState, FrameState, FrameState, CompileContext>
{

  @Override
  public FrameState transformRecordPtn(RecordPtn aggregatePtn, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformCastPtn(CastPtn ptn, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformMatchingPtn(MatchingPattern matches, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformPatternApplication(PatternApplication apply, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformRegexpPtn(RegExpPattern ptn, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformScalarPtn(ScalarPtn scalar, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformConstructorPtn(ConstructorPtn tuplePtn, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformVariablePtn(Variable variable, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformWherePattern(WherePattern wherePattern, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
