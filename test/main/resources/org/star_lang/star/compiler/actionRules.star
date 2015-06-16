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
actionRules is package{

  type weekday is sunday or monday or tuesday or wednesday or thursday or friday or saturday;
  
  showDay has type action(weekday);
  prc showDay(sunday) do logMsg(info,"sunday")
   |  showDay(monday) do logMsg(info,"monday")
   |  showDay(tuesday) do logMsg(info,"tuesday")
   |  showDay(wednesday) do logMsg(info,"wednesday")
   |  showDay(thursday) do logMsg(info,"thursday")
   |  showDay(friday) do logMsg(info,"friday")
   |  showDay(saturday) do logMsg(info,"saturday")
   |  showDay(Other) default do logMsg(info,"some day $Other")
  
  prc main() do {
    showDay(wednesday);
    showDay(saturday);
    showDay(sunday);
  }
}
                