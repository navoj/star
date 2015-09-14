 othertest is package{
   def ttVal is valof{
     if (true otherwise true) then {
            valis true; 
        } else {
            valis false;
        };
     };
   prc main() do {
     assert ttVal;
   }
 }