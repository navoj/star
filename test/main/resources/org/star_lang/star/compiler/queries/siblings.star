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
siblings is package{

  def siblings is list of [
    ("john", "peter"),
    ("john", "mary"),
    ("mary", "peter"),
    ("mary","john"),
    ("peter","john"),
    ("peter","mary")];
  
  ages has type list of ((string,integer));
  def ages is list of [
    ("john",10),
    ("peter",12),
    ("mary",8)
  ];
  
  def JS is all A where ("john",S) in siblings and (S,A) in ages order by A;
  
  def JSS is all (S,A) where ("john",S) in siblings and (S,A) in ages order by A using (<);
  
  prc main() do {
    logMsg(info,"John's siblings' ages are: $JS");
    logMsg(info,"John's siblings are: $JSS");
  }
}