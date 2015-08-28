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

-- implement sieve of erastosthenes -- using concurrent actors
actorSieve is package{
 
  fun filterActor(P) is concurrent actor{
    private var Nx := ((_) => task{});
    
    {Nx := newPrime};
    
    private 
    fun newPrime(X) is let{
      def Fx is filterActor(X);
      
      fun filterPrime(XX) is task{ notify Fx with XX on input};
    } in task {
      logMsg(info,"new prime $X");
      Nx := filterPrime;
    };
    
    on X on input do {
      perform task {
        if X%P!=0 then
          perform Nx(X);
      }
    }
  }

  def S is filterActor(2);
  
  prc main() do {
    perform task{
      for Ix in iota(3,1000,2) do{
        -- logMsg(info,"next number is $Ix");
        notify S with Ix on input
      }
    }
  }
}
 