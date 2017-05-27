package org.star_lang.star.engine;

/*
 * Copyright (c) 2017. Francis G. McCabe
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

import org.star_lang.star.data.IValue;

import java.util.Stack;

/**
 * Created by fgm on 5/3/17.
 */
public class Machine {
  private final Stack<Frame> stack = new Stack<>();
  private final IValue[] expStack = new IValue[1024];

  private int expStkPt = 0;

  protected void pushStack(IValue term){
    expStack[expStkPt++] = term;
  }

  protected IValue popStack(){
    return expStack[--expStkPt];
  }

  protected Frame currFrame(){
    return stack.peek();
  }

}
