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
 testJava is package{
  java org.star_lang.star.compiler.SimpleFuns;
  java (java).lang.System;
    
  main() do {
    logMsg(info,"invoking javaFoo(23,45): $(javaFoo(23,45))");
    logMsg(info,"invoking javaString(34): $(javaString(34))");
    doSomething("hello",34.56D);
    doSomething(javaFoo(23,45),45.23);

    logMsg(info,"current time: $(currentTimeMillis())");
    
    logMsg(info,"current properties: $(getProperties())");
    
    logMsg(info,"ifunc(4,5)=$(ifunc(4,5))");
  }
}