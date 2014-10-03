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
arithtest is package{
  square has type (%t) => %t where arithmetic over %t and comparable over %t;
  square(X) is times(X,X);
  
  times(X,Y) where X>=Y is X*Y;
  
  main has type action();
  main() do {
    logMsg(info,"square of 4 is $(square(4))");
    logMsg(info,"times of 4 is $(times(4,4))");
    
    assert square(4)=16;
    assert times(5,4) = 20;
    
    logMsg(info,"sqrt(9)=$(sqrt(9.0))");
    assert sqrt(9.0)=3 as float;
    
    logMsg(info,"funky","9.0**0.5=$(9.0**0.5)");
    
    logMsg(info,"important","you need a tune-up");
    
    assert 9.0**0.5=3.0;
  }
};
