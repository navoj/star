/**
 * 
 * Copyright (C) 2013 Starview Inc
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
injectTest is package{
  import task;
  
  aa(X) is action computation{
      valis X+2;
  }

  tt(X) is task{
    valis valof aa(X);
  }
  
  uu(X) is task{
    logMsg(info,"We got $X");
    var Y := 1;
    Y:=Y+valof tt(3);
    logMsg(info,"Y is $Y");
    valis X+Y;
  };
  
  main() do {    
    YY is valof uu(3);
    assert YY=9;
  }
}