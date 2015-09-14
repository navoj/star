indirect is package{
 -- test indirect imports
 import beta;
 import gamma;
 
 prc main() do {
   assert alpha(3)=3;
 }
}