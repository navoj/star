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
complexorder is package {
  
  type EventItem is event{
    createdTimestamp has type long;
  };
  
--  AA is actor{
    LocalizedActualEventItems has type list of {containerId has type string;
      event has type EventItem;
      locale has type string;
    };
    
    def LocalizedActualEventItems is list of [
      { containerId = "first"; event=event{createdTimestamp=0L}; locale="somewhere"},
      { containerId = "second"; event=event{createdTimestamp=1L}; locale="nowhere"}
    ];
--  };
    
  prc main() do {
   def  QQ is -- query AA's LocalizedActualEventItems with
      all x.event where x in LocalizedActualEventItems order by x.event.createdTimestamp;
      
    logMsg(info,"$QQ");
  }
}