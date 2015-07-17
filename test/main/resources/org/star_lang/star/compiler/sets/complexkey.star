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
complexkey is package{
  type foo of %t is nf or foo(%t);
  
  prc main() do {
    var S := set of [];

    extend S with foo(3)
    extend S with foo(4)
    
    assert contains_element(S,foo(3))

    logMsg(info,"$S")

    assert not contains_element(S,nf)

    extend S with nf

    assert contains_element(S,nf)

    logMsg(info,"$S")
  }
}