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
dateFormat is package{
  prc main() do {
    def D is "3/12/2012 00:00 AM" as date;
    logMsg(info,"$D");
    logMsg(info,"$(__display(D))");
    def DD is parse_date("3/12/2012","MM/dd/yyyy")
    logMsg(info,"$(__display(DD))");
    assert D=DD;
    
    def SS is format_date(D,"MM/dd/yyyy");
    logMsg(info,"SS=$SS");
    assert SS="03/12/2012";
    
    assert "$D:MM/dd/yyy;"="03/12/2012"
    
    logMsg(info,"now: $(now()):MM-dd-yyy;");
  }
}