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
salaries is package{

  type employeeType is alias of relation of{
    name has type string;
    role has type string;
    salary has type integer;
  };
  
  employees has type employeeType;
  var employees := indexed{
    {name="alpha"; role="manager"; salary=10000};
    {name="beta"; role="manager"; salary=9000};
    {name="zeta"; role="worker"; salary=1000};
    {name="iota"; role="worker";salary=20000}
  }
  
  managerOf has type relation of {name has type string; member has type string};
  var managerOf := indexed{
    {name="alpha"; member="beta"};
    {name="beta";member="zeta"};
    {name="beta";member="iota"}
  };
  
  alphaDog has type relation of ((string));
  M in alphaDog if {name=M;role="manager";salary=MS} in employees and 
      not( {name=M;member=E} in managerOf and {name=E;salary=ES} in employees and ES>MS);
  
  main() do {
    logMsg(info,"is alpha an alphaDog? $(\"alpha\" in alphaDog)");
    logMsg(info,"is beta an alphaDog? $(\"beta\" in alphaDog)");
    
    logMsg(info,"all alphaDogs: $(all A where A in alphaDog)");
  }
  
}