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
queryer is package{
  queryer has type actor{
    ageOf has type (string)=>integer;
  } originates {
    nameQ has type relation of {
      name has type string;
      age has type integer;
    }
  }
  
  def queryer is actor{
    fun ageOf(N) is query an yof A where {name=N;age=A} in nameQ;
  };
  
  prc startActors() do
  { 
    logMsg(info,"starting");
    sleep(1000);
    logMsg(info,"asking for the age of fred: $(query queryer with ageOf("fred"))");
  }
}