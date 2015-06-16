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
memoBug is package {
    def actorA is memo actor{
       prc replan() do request actorB()'s replan to replan();

       prc restock() do nothing;
    } using {
        prc dummy() do nothing;
    };
    
    def actorB is memo actor{
        prc removeLot(L) do request actorA()'s restock to restock();
        
        prc replan() do nothing; 
    };
}
