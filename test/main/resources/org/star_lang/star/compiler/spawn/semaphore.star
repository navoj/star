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
semaphore is package{
  
  type sem is alias of  { grab has type ()=>task of (()); release has type ()=>task of (()) };

  semaphore has type (integer) => sem;
  fun semaphore(Count) is {
    private def grabCh is channel();
    private def releaseCh is channel();
    
    { ignore background semLoop(Count); }
    
    private
    fun releaseR(x) is choose wrap incoming releaseCh in ((_) => semLoop(x+1));
    private
    fun grabR(x) is choose wrap incoming grabCh in ((_) => semLoop(x-1));
   
    private 
    fun semLoop(0) is wait for releaseR(0)
     |  semLoop(x) default is wait for grabR(x) or releaseR(x)

    fun grab() is wait for put () on grabCh;
    fun release() is wait for put () on releaseCh;
  };
}
    