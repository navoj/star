package org.star_lang.star.transformer;

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


public class TransformerTest extends SRTest
{
  public TransformerTest()
  {
    super(TransformerTest.class);
  }

  @Test
  public void runWorkedEx()
  {
    runStar("workedex.star");
  }

  @Test
  @Ignore
  public void testBasicTrans()
  {
    runStar("simpleTransforms.star");
  }
}
