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
-- A test of SR concept graphs

import rdf;

peopleGraph is package{

  def people is graph{
    :john ! :parent $ :sam;
    :john ! :parent $ :jim;
    
    :jane ! :parent $ [:sam, :jim];
    
    :peter ! :parent $ :jj;
    :peter ! :address $ "2 smart place";
    :peter ! :address $ "1 holiday Dr";
    
    :peter ! [ :parent $ :jj, :address $ [ "2 smart place", "1 holiday Dr"]];
    
   -- ( lives $ john ) ! address $ "1 smart Place":"en";
  };
  
  var others := graph{};
  
  prc main() do { -- X is john ! address;
    -- XX is ( john ! address) $ village;
    for Tr in people do
      logMsg(info,display(Tr));
    logMsg(info,display(others));
  }
}   