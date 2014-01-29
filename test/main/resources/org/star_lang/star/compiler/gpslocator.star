/** *  * Copyright (C) 2013 Starview Inc *  * This library is free software; you can redistribute it and/or modify it under the terms of the * GNU Lesser General Public License as published by the Free Software Foundation; either version * 2.1 of the License, or (at your option) any later version. *  * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU * Lesser General Public License for more details. *  * You should have received a copy of the GNU Lesser General Public License along with this library; * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA * 02110-1301 USA *  * @author fgm * */gpslocator is package{
  type gpsLocation is alias of {latitude has type integer;longitude has type integer}; 
  calculateCity has type  (gpsLocation)=>string;
  calculateCity({latitude=lt;longitude=ln}) where 28<=ln and ln<=31 and 120<=lt and lt<=130 is "San Jose";
  calculateCity({latitude=lt;longitude=ln}) where 30<=ln and ln<=32 and 120<=lt and lt<=130 is "San Francisco";
  calculateCity({latitude=lt;longitude=ln}) where ln=40  and lt=130 is "Fremont";
  calculateCity({latitude=lt;longitude=ln}) where ln=50  and lt=140 is "Cupertino";
  calculateCity(_) default is "unknown";
  main() do
  {
    logMsg(info,"$(calculateCity({latitude=125;longitude=30})) at (125,30)");
    logMsg(info,"$(calculateCity({latitude=125;longitude=32})) at (125,32)");
    logMsg(info,"$(calculateCity({latitude=130;longitude=40})) at (130,40)");
    logMsg(info,"$(calculateCity({latitude=140;longitude=50})) at (140,50)");
    logMsg(info,"$(calculateCity({latitude=150;longitude=30})) at (150,30)");
    assert calculateCity({latitude=125;longitude=30}) = "San Jose";
    assert calculateCity({latitude=125;longitude=32}) = "San Francisco";
    assert calculateCity({latitude=130;longitude=40}) = "Fremont";
    assert calculateCity({latitude=140;longitude=50}) = "Cupertino";
    assert calculateCity({latitude=150;longitude=30}) = "unknown";
  }
}