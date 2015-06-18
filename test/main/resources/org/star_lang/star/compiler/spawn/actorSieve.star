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
-- implement sieve of erastosthenes -- using concurrent actors
actorSieve is package{
 
  fun filterActor(P) is concurrent actor{
    private var Nx := ((_) => task{});
    
    {Nx := newPrime};
    
    private 
    fun newPrime(X) is let{
      def Fx is filterActor(X);
      
      fun filterPrime(XX) is task{ notify Fx with XX on input};
    } in task {
      logMsg(info,"new prime $X");
      Nx := filterPrime;
    };
    
    on X on input do {
      perform task {
        if X%P!=0 then
          perform Nx(X);
      }
    }
  }

  def S is filterActor(2);
  
  prc main() do {
    perform task{
      for Ix in iota(3,1000,2) do{
        -- logMsg(info,"next number is $Ix");
        notify S with Ix on input
      }
    }
  }
}
 