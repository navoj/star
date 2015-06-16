/**
 * Implementation of random access arrays using Binary Trees 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
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
