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
  fun primes(Max) is let{
    fun filterMultiples(K,N) is filter((X)=>X%K!=0,N)

    fun sieve([N,..rest]) is [N,..sieve(filterMultiples(N,rest))]
     |  sieve([]) is []

    iota has type (integer,integer)=>list of integer
    fun iota(Mx,St) where Mx>Max is []
     |  iota(Cx,St) is [Cx,..iota(Cx+St,St)]
  } in list of [2,..sieve(iota(3,2))]

  show primes(100)

  assert primes(10) = list of [2,3,5,7]
}