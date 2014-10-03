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
persons is package{
  type Person is noone 
	          or someone{
		        name has type string;
		        dob has type date;
		        age has type ()=>long;
		        age() default is timeDiff(today(),dob);
		      };
	
  john is someone{
    name is "john";
	dob is today();
  };
	
  sho(P) is
	"$name:$(age())" using P()'s name 'n age;
	
  disp(P) do
	  logMsg(info,"$name:$(age())") using P's name 'n age;
	
  main() do
  {
	logMsg(info,sho(fn() => john));
	disp(john);
	assert john.name="john";
  }
}
