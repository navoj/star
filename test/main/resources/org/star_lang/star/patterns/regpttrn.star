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
regpttrn is package{
  
  email has type (string,string) <= string;
  ptn email(N,H) from `mailto\:(.*:N)@(.*:H)`
   |  email(N,H) from `(.*:N)@(.*:H)`
  
  fun hostOfEmail(`(.*:N)@(.*:H)`) is H
   |  hostOfEmail(`mailto\:(.*:N)@(.*:H)`) is H
  
  type emailPart is name or host;
  
  fun emailFragment(`(.*:N)@(.*:H)`,host) is H
   |  emailFragment(`(.*:N)@(.*:H)`,name) is N
   |  emailFragment(`mailto\:(.*:N)@(.*:H)`,host) is H
   |  emailFragment(`mailto\:(.*:N)@(.*:H)`,name) is N
  
  prc main() do {
    assert "foo@bar.com" matches email("foo","bar.com");
    
    if "mailto:foo@bar.com" matches email(U,H) then 
 	  logMsg(info,"U=$(__display(U)), H=$(__display(H))");
 	  
    assert "mailto:foo@bar.com" matches email("foo","bar.com");
    
    assert hostOfEmail("mailto:foo@bar.com")="bar.com";
    logMsg(info,"$(hostOfEmail("mailto:foo@bar.com"))");
    
    logMsg(info,"name is $(emailFragment("mailto:foo@bar.com",name))");
    logMsg(info,"host is $(emailFragment("mailto:foo@bar.com",host))");
    
    assert emailFragment("mailto:foo@bar.com",host)="bar.com";
  }
}