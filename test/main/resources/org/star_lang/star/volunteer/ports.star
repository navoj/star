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
ports is package{
  import actors;
  
  type port of %u is port{
    _query has type for all %s such that ((%u)=>%s,()=>quoted,()=>dictionary of (string,quoted))=> %s;
    _request has type ((%u)=>(),()=>quoted,()=>dictionary of (string,quoted)) => ();
    _notify has type ((%u)=>()) => ();
  };
  
  #p0rt{?S} ==> let{ #$"Schema" is actorTheta(S); } in port{
    _notify(Fn) do Fn(#$"Schema");
    _request(Fn,Qt,Fr) do Fn(#$"Schema");
    _query(Fn,Qt,Fr) is Fn(#$"Schema");
  };
  
  type p0rt of %t is alias of port of %t;
  
  implementation speech over port of %schema determines (%schema,action) is {
    _query(P,Qf,Qt,Fr) is action{ valis P._query(Qf,Qt,Fr)};
    _request(P,Qf,Qt,Fr) is action{ P._request(Qf,Qt,Fr)};
    _notify(P,Np) is action{ P._notify(Np)};
  };
}