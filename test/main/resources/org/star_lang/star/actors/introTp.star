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
introTp is package{
  AA has type actor of {
    IN has type occurrence of integer;
  };
  
  def AA is actor{
    private proc has type (integer)=>(integer,integer,integer);
    fun proc(I) is (I,I,I);
    
    on X on IN do{
      logMsg(info,"Got $X - $(proc(X))");
    }
  } 
  
  prc main() do {
    notify AA with 10 on IN;
  }
}