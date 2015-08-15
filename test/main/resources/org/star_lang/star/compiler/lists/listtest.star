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
-- test contracts with embedded requirements
listtest is package{
  
  implementation comparable over list of %t where comparable over %t is {
   (<) = listLess;
   (=<) = listLessEq;
   (>) = listGt;
   (>=) = listGtEq;
  } using{
    listLess(list of [],list of [_,.._]) is true;
    listLess(list of [X,..L1],list of [X,..L2]) is listLess(L1,L2);
    listLess(list of [X,.._], list of [Y,.._]) where X<Y is true;
    listLess(_,_) default is false;
    
    listLessEq(list of [],_) is true;
    listLessEq(list of [X,..L1],list of [Y,..L2]) where X=<Y is listLessEq(L1,L2);
    listLessEq(_,_) default is false;
    
    listGt(X,Y) is listLess(Y,X);
    
    listGtEq(X,Y) is listLessEq(Y,X);
  }
  
  main() do {
    assert list of []<list of [1];
    assert list of [1,2,3] < list of [1,2,4];
    assert list of [1,2,3] =< list of [1,2,3];
    assert list of [1,2,3] =< list of [1,2,4];
    
    assert not list of [1]<list of [];
    assert not list of [1,2,4] < list of [1,2,3];
    assert not list of [1,2,4] =< list of [1,2,3];
  }
}
   