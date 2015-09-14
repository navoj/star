castTest is package{
  intervalRel is list of [1.2, 1.3, 3.0, 4.0];
  
  result is ((FSUM(intervalRel) cast double)/size(intervalRel));
            
  FSUM has type (relation of float) => float
  FSUM(rel) is FSUM_HELPER(sort(rel,(function(x,y) is false)), 0);

  FSUM_HELPER has type (list of float, float) => float
  FSUM_HELPER([], sum) is sum;
  FSUM_HELPER([qty]++qtys, sum) is FSUM_HELPER(qtys, (sum+qty));
  
  main() do {
    logMsg(info,"result = $result");
  }
}