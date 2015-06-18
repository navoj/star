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
account is package{
  type account is account{
    id has type actId;
    owner has type string;
    balance has type long;
    lastTx has type txId;
  }
  
  idKey has type (%t)=>long where %t implements { id has type long };
  fun idKey(R) is R.id;
  
  type txId is alias of long;
  type actId is alias of long;
  
  type tx is tx{
    id has type txId;
    timestamp has type date;
    source has type actId;
    dest has type actId;
    amnt has type long;
  }
  
  implementation equality over account is {
    fun X = Y is same_account(X,Y);
  } using {
    fun same_account(account{id=N1},account{id=N2}) is N1=N2
     |  same_account(_,_) default is false;
  }
}