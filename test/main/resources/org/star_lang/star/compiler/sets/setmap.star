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
worksheet{
  var S := set of [1,2,3]

  show map((K)=>display(K),S)

  assert map((K)=>display(K),S) = set of ["1","3","2"]

  assert leftFold((X,I)=>X+I,0,S) = 6

  assert leftFold((+),0,S) = 6

  def KK is valof{
    var K := 0
    for X in S do
      K := K+X
    valis K
  }

  assert KK = 6
}