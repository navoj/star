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
import volunteers;
import ports;

voltestreduce is connections{
  originate(p1,{notes has type stream of string;
                reqs has type action(string,integer);
                ques has type (string)=>integer
               });
  respond(p2,{notes has type stream of string;
                otherNots has type stream of integer;
                reqs has type action(string,integer);
                otherreqs has type action(integer,string);
                ques has type (string)=>integer;
                otherqueries has type ()=>integer;
               });
  connect(p1,p2,(volunteer notify X as X));
  connect(p1,p2,(volunteer query X as X));
}