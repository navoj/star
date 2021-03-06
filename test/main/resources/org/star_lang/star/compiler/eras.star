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

eras is package{  
  fun append(nil,X) is X
  |  append(cons(H,T),X) is cons(H,append(T,X))
  
  fun reverse(nil) is nil
   |  reverse(cons(H,T)) is append(reverse(T),cons(H,nil))
  
  fun iot(K,K2,_) where K>=K2 is cons(K,nil)
   |  iot(M,X,S) is cons(M,iot(M+S,X,S))
  
  fun length(nil) is 0
   |  length(cons(_,T)) is length(T)+1
  
  fun sieve(nil) is nil
   |  sieve(cons(H,T)) is cons(H,sieve(filter(T,H)))
  
  fun filter(nil,_) is nil
   |  filter(cons(H,T),K) where H%K=0 is filter(T,K)
   |  filter(cons(H,T),K) default is cons(H,filter(T,K))
  
  fun init(Length) is
    iot(3,Length,2)
    
  prc run(Data) do {
    def Pr is sieve(Data);
  }
  
  prc NoTh() do {};
  
  fun bench(Run,Data) is valof{
    def EpS is nanos();
    for Ix in iota(1,Run,1) do{
      NoTh();
    };
    def ETm is nanos()-EpS;
    logMsg(info,"empty time is $ETm");
    
    def St is nanos();
    for Ix in iota(1,Run,1) do{
      run(Data);
    }
    valis ((nanos()-St-ETm)as float)/(Run as float);
  }
  
  prc main() do
  {
    def data is iot(3,1000,2);
    def primes is sieve(data);
    -- logMsg(info,"primes up 1000 is $primes");  
    def PrCount is length(primes);
    assert PrCount=167; -- 167 primes between 3 and 1000
    
    def Tm is bench(1000,iot(3,1000,2));
    
    logMsg(info,"$PrCount primes in $Tm nanos at $(Tm/(PrCount as float)) nanos/prime");
  }
}