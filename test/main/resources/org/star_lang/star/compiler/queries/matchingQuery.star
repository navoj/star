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
matchingQuery is package{  
  type employee is empl{
    name has type string;
    id has type integer;
    ssn has type string;
  }
  
  Joe is empl{ name="joe"; id=0; ssn="001-01-0001"};
  Peter is empl{ name="peter"; id=1; ssn="002-02-0002"};
  John is empl{ name="john";id=2;ssn="003-03-0003"};
  
  E is list of [ Joe,  Peter, John ];
                  
  findEmpl(N,I,S,EE) is 
    list of { all Em where Em matching empl{ name = Nm; id = ID; ssn = SSN} in EE and (N=Nm or N=nonString) and (ID=I or I=nonInteger) and (SSN=S or S=nonString)};
     
  main() do {
    assert findEmpl("joe",nonInteger,nonString,E) = list of { Joe };
    assert findEmpl(nonString,1,nonString,E) = list of {Peter};
    assert findEmpl(nonString,4,nonString,E) = list of {}
  }
}