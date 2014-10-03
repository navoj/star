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
doubleAlias is package{
  type NewsEvent is NewsEvent{
    time has type double;
    symbol has type string;
    sentiment has type long;
    news has type string;
  }
  
  main() do {
    N is NewsEvent{
      time is 6.5;
      symbol is "AAA";
      sentiment is nonLong;
      news is "no news is good news"
    };
    
    assert N.time=6.5;
  }
}