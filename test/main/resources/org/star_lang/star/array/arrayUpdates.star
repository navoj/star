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
arrayUpdates is package{
  -- test list updating
  
  AA has type ref list of integer;
  var AA := iota(1,10,1);
  
  prc main() do {
    update (X where X%2=0) in AA with -1;
    
    logMsg(info,"AA=$AA");
    
    assert AA=list of [1,-1,3,-1,5,-1,7,-1,9,-1];
    
    delete (X where X<0) in AA;
    
    logMsg(info,"AA is $AA");
    
    assert AA=list of [1,3,5,7,9];
    
    extend AA with -1;
    
    merge AA with list of [-2,-3];
    
    logMsg(info,"AA is now $AA");
    
    assert AA = list of [1,3,5,7,9,-1,-2,-3];
  }
}