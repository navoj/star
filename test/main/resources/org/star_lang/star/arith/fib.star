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

fib is package{
  fun fib(0) is 1
   |  fib(1) is 1
   |  fib(N) is fib(N-1)+fib(N-2)
  
  fun nfib(0) is 1
   |  nfib(1) is 1
   |  nfib(N) is nfib(N-1)+nfib(N-2)+1
  
  fun ifib(0) is 1
   |  ifib(1) is 1
   |  ifib(N) is valof{
        var prevPrev := 0;
        var prev := 1;
        var result := 0;
    
        for ix in iota(2,N,1) do{
          result := prev+prevPrev;
          prevPrev := prev;
          prev := result;
        }
    
        valis result
      }
}  