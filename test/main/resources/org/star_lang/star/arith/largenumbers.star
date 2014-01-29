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
largenumbers is package{
  plus has type (decimal,long) =>decimal;
  plus(X,Y) is X+(Y as decimal);
  
  A is 1a;
  
  sL has type long;
  sL is smallest;
  
  lL has type long;
  lL is largest;
  
  sF has type float;
  sF is smallest;
  
  lF has type float
  lF is largest;
  
  lD has type date;
  lD is largest;
  
  main has type action();
  main() do {
    logMsg(info,"a large now $(plus(A,long(_now())))");
    
    logMsg(info, "smallest long $sL");
    logMsg(info, "largest long $lL");
    
    logMsg(info, "smallest float $sF");
    logMsg(info, "largest float $lF");
    
    logMsg(info,"end of time is $lD");
  };
};