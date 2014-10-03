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
selfSend is package{
  type nullable of %t is nil or real(%t);
  
  unreal(real(X)) is X;
  
  XX is actor{
    on X on SS do{
      logMsg(info,"We got $X");
      notify unreal(mySelf) with "$X!" on SS;
    };
    
    setMyself(A){
       mySelf := real(A);
    }
  } using {
    var mySelf := nil;
  }
  
  main() do {
    request XX to setMyself(XX);
    
    notify XX with "1" on SS;
    sleep(2000);
  }
}
 