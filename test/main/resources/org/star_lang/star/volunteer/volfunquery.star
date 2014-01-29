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
-- import ports;
import volunteers;
/*
 * set up three ports with two respond and one originate.
 * The two respond each respond to different functions
 */
 
volfunquery is connections {
  originate(Or,{testFun1 has type(string) => integer; testFun2 has type(string) => string});
  respond(R1,{testFun1 has type(string) => integer});
  respond(R2,{testFun2 has type(string) => string});
  connect(Or,R1,(volunteer testFun1(X) as testFun1(X)));
  connect(Or,R2,(volunteer testFun2(X) as testFun2(X)));
}