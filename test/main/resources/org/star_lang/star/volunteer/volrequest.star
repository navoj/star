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

volrequest is connections {
  originate(p1,{DEFAULT has type stream of string; TEST has type(string) => integer; DO has type (string)=>()});
  respond(p2,{DATA has type stream of any; TRY has type(string) => integer; OD has type (string)=>()});
  connect(p1,p2,(volunteer X on DEFAULT as (X cast any) on DATA));
  connect(p1,p2,(volunteer DO(A) as OD(A)));
  connect(p1,p2,(volunteer TEST(X) as TRY(X)));
}