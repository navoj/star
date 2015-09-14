/**
 * 
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
 constructorExp is package{
 
  type c1 of t is c1(integer,t);
  
  type c2 of t is c2(integer,t);
  
  type encap is encap{
    el has kind type where pPrint over el and equality over el;
    c has type for all t such that (integer,t)<=>el
  }
  
  def C1 is encap{
    type c1 of string counts as el;
    def c is c1;
    
    implementation pPrint over el is {
      ppDisp = ppDisp
    }
    
    implementation equality over el is {
      (=) = (=)
    }
  }
  
  def C2 is encap{
    type c2 of integer counts as el;
    def c is c2;
    
    implementation pPrint over el is {
      ppDisp = ppDisp
    }
    
    implementation equality over el is {
      (=) = (=)
    }
  }
  
  prc main() do {
    def R1 is C1.c(34,"peter");
    def R2 is C2.c(23,56);
    
    logMsg(info,"R1=$R1");
    logMsg(info,"R2=$R2");
    
    assert R1 matches C1.c(34,"peter")
  }
}