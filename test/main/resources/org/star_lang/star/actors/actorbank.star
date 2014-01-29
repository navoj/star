/**
 * 
 * Copyright (C) 2013 Starview Inc
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
actorbank is package{  
  type tx is deposit(float) or withdraw(float);
  
  account((Nm has type string)) is actor {
    private var bal := 0.0;
    
    on deposit(Amnt) on Tx do
      logMsg(info,"depositing $Amnt in #Nm's account");
    on deposit(Amnt) on Tx where Amnt>=0.0 do
      bal := bal+Amnt;
    on deposit(Amnt) on Tx where Amnt<0.0 do
      logMsg(info,"Cannot deposit negative amnt: $Amnt into $Nm");
    
    on withdraw(Amnt) on Tx do
      logMsg(info,"withdrawing $Amnt from #Nm's account");
    on withdraw(Amnt) on Tx where Amnt>=0.0 and Amnt<=bal do
      bal := bal-Amnt;
    on withdraw(Amnt) on Tx where Amnt<0.0 or Amnt>bal do
      logMsg(info,"Cannot withdraw amnt: $Amnt from $Nm");
   
    balance() is bal;
    name is Nm;
  }
  
  bank(Nm) is actor{
    var accounts := relation{};
    
    custBals() is all (Nm,Bal) where A in accounts and (query A's name 'n balance with (name,balance())) matches (Nm,Bal);
  } 

  main() do {
    B is bank("Super");
    
    JJ is account("joe");
    
    request B to extend accounts with JJ;
    
    logMsg(info,"customers of bank are $(query B's custBals with custBals())");
    
    notify JJ with deposit(10.0) on Tx;
    notify JJ with withdraw(5.0) on Tx;
    
    logMsg(info,"Joe's account has $(query JJ's balance with balance())");
    assert (query JJ's balance with balance())=5.0;
    
    notify JJ with deposit(-1.0) on Tx;
    
    notify JJ with withdraw(10.0) on Tx;
    
    assert (query JJ's balance with balance())=5.0;
    
    logMsg(info,"customers of bank are $(query B's custBals with custBals())");
  }
}