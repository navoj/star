/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
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


private import base;
private import sequences;
private import strings;
private import iterable;
private import updateable;
private import folding;
private import casting;
private import arithmetic;

type rList of t is
    eList or
    zList(rList of ((t,t))) or
    oList(t,rList of ((t,t)));

def emptyRlist is eList;

implementation sizeable over rList of %t is {
  fun isEmpty(eList) is true
   |  isEmpty(_) default is false

  fun size(A) is rSize(A);
} using {
  rSize has type for all tt such that (rList of tt)=>integer;
  fun rSize(eList) is 0
   |  rSize(zList(P)) is 2*rSize(P)
   |  rSize(oList(_,P)) is 2*rSize(P)+1
}

rCons has type for all t such that (t,rList of t) => rList of t;
fun rCons(x,eList) is oList(x,eList)
 |  rCons(x,zList(P)) is oList(x,P)
 |  rCons(x,oList(y,ps)) is zList(rCons((x,y),ps))

rHead has type for all t such that (rList of t)=>t;
fun rHead(X) is valof{
  def (x,_) is uncons(X);
  valis x;
}
rTail has type for all t such that (rList of t)=>rList of t;
fun rTail(X) is valof{
  def (_,t) is uncons(X);
  valis t;
}

private uncons has type for all t such that (rList of t)=>(t,rList of t);
fun uncons(oList(x,eList)) is (x,eList)
 |  uncons(oList(x,ps)) is (x,zList(ps))
 |  uncons(zList(ps)) is valof{
      def ((x,y),ps1) is uncons(ps);
      valis (x,oList(y,ps1))
    }
