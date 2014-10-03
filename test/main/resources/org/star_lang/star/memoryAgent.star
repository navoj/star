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
memory is package{

  memory has type actor{
    married has type relation of((string,string));
    marry has type action(string,string);
    divorce has type action(string,string);
  };
  
  memory is actor{ 
     -- (H,W) in married if (H,W) in marriages;
    } using {
    
    relation of((string,string)) var married := indexed{};
    
    marry(H,W){
      logMsg(info,"Marrying $H to $W");
      extend married with (H,W);
      logMsg(info,"Married is now $married");
    };
    
    divorce(H,W){
      logMsg(info,"$H divorcing $W");
      delete (H,W) in married;
      logMsg(info,"Married is now $married");
    };
  };
  
  
  main() do {
    request marry("J","M") to memory;
    request marry("F","K") to memory;
    
    logMsg(info,"$(query all (H,W) where (H,W) in married to memory) who is married?");
     
    request { marry("D","S"); divorce("J","M") } to memory;
    
    logMsg(info,"$(query all (H,W) where (H,W) in married to memory) who is married now?");
  }
}