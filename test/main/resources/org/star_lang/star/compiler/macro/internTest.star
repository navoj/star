/**
 * 
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
internTest is package {

 # makeEmpty(#(#(?F)#{?init})#(?X)) ==> let {
   #$empty has type #~#(F#+"State")# of X;
   #$empty is #(#~#("empty"#+F)#)#{init}; }
 in #$empty;

 type initializedIdentityState of %t is initIdentity {
   res has type %t;
   count has type integer;
 } or emptyinitializedIdentity {
   res has type %t;
 };

 main() do {
   foo has type initializedIdentityState of integer;
   foo is emptyinitializedIdentity{res=10};
   e is makeEmpty(initializedIdentity{res=10}(integer));
   logMsg(info, "$e");
   assert e.res = 10;
 }

}