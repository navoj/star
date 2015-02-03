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
philoshopers is package{
  import semaphore;
    
  -- Dining philosophers using concurrency primitives
  
  
  table(Count) do let{
      T is semaphore(3);
      
      phil has type (integer, sem, sem)=>task of (());
      phil(n,L,R) is task{
        for Ix in range(0,Count,1) do{
          -- sleep(random(15L));
          perform T.grab();  -- get permission first
          perform L.grab();
          perform R.grab();
          -- logMsg(info,"Phil $n is eating for the $(Ix)th time");
          perform T.release();
          -- logMsg(info,"Table released");
          perform L.release();
          -- logMsg(info,"Left released");
          perform R.release();
          -- logMsg(info,"Right released");
        }
        logMsg(info,"Phil $n ate for $(Count) times");
      };
    } in {
      fork1 is semaphore(1);
      fork2 is semaphore(1);
      fork3 is semaphore(1);
      fork4 is semaphore(1);
    
      phil1 is background phil(1,fork1,fork2);
      phil2 is background phil(2,fork2,fork3);
      phil3 is background phil(3,fork3,fork4);
      phil4 is background phil(4,fork4,fork1);
    
      perform phil1;
      perform phil2;
      perform phil3;
      perform phil4;
    };
    
  main() do {
    table(10000);
  }
}
