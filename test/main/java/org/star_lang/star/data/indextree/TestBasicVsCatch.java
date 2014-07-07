package org.star_lang.star.data.indextree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.type.Location;

public class TestBasicVsCatch
{
  private static final int count = 500000;

  @Test
  public void testBasicVsCatch()
  {
    IndexTree<Integer, String> map = IndexTree.emptyTree();

    long nanos = System.nanoTime();
    for (int ix = 0; ix < count; ix += 2)
      map = map.insrt(ix, "Data " + ix);
    nanos = System.nanoTime() - nanos;

    System.err.println(StringUtils.msg("took ", (((double) nanos) / 1.0e9), " seconds"));

    Random random = new Random();

    Map<Integer, String> hash = new HashMap<>();
    nanos = System.nanoTime();
    for (int ix = 0; ix < count; ix++) {
      int cx = random.nextInt();
      if (!hash.containsKey(cx))
        hash.put(cx, "Data from " + ix);
    }
    nanos = System.nanoTime() - nanos;

    System.err.println(StringUtils.msg("hash map took ", (((double) nanos) / 1.0e9), " seconds"));

    map = IndexTree.emptyTree();
    nanos = System.nanoTime();
    for (int ix = 0; ix < count; ix++) {
      int cx = random.nextInt();
      if (!map.contains(cx))
        map = map.insrt(cx, "Data from " + ix);
    }
    nanos = System.nanoTime() - nanos;

    System.err.println(StringUtils.msg("random took ", (((double) nanos) / 1.0e9), " seconds"));

    int exceptionCount = 0;
    map = IndexTree.emptyTree();
    nanos = System.nanoTime();
    for (int ix = 0; ix < count; ix++) {
      int cx = random.nextInt();
      try {
        get(ix, map);
      } catch (EvaluationException e) {
        map = map.insrt(cx, "Data from " + ix);
        exceptionCount++;
      }
    }
    nanos = System.nanoTime() - nanos;

    System.err.println(StringUtils.msg("catch/try took ", (((double) nanos) / 1.0e9), " seconds, ", exceptionCount,
        " exceptions"));
  }

  private <K, T> T get(K key, IndexTree<K, T> tree) throws EvaluationException
  {
    T val = tree.find(key);
    if (val == null)
      throw new EvaluationException(key + "not found", Location.noWhereEnum);
    return val;
  }
}
