package org.star_lang.star.data.indextree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.star_lang.star.data.indextree.IndexTree;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IMap;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class IndexTreeTest
{
  private final static int count = 100;

  @Test
  public void testTreeBuild()
  {
    IndexTree<Integer, String> tr = buildTree(count);

    System.out.println("tree is " + tr);

    for (int ix = 0; ix < count; ix++) {
      String val = tr.find(ix);
      assert val != null && val.equals(Integer.toString(ix));
    }

    for (int ix = count - 1; ix >= 0; ix--) {
      String val = tr.find(ix);
      assert val != null && val.equals(Integer.toString(ix));
    }
  }

  @Test
  public void testTreeRemove()
  {
    IndexTree<Integer, String> tr = buildTree(count);

    for (int ix = count - 1; ix >= 0; ix--)
      tr = tr.delete(ix);

    assert tr.isEmpty();
  }

  @Test
  public void perfTest()
  {
    int count = 200000;
    long nanos = System.nanoTime();
    IndexTree<Integer, String> tr = buildTree(count, "IndexTree");
    nanos = System.nanoTime() - nanos;
    showTime("index tree create ", nanos);

    long hnanos = System.nanoTime();
    Map<Integer, String> h = buildHash(count, "HashMap");
    hnanos = System.nanoTime() - hnanos;
    showTime("hash map create ", hnanos);

    nanos = System.nanoTime();
    for (int ix = 0; ix < count; ix++) {
      String val = tr.find(ix);
      assert val != null;
    }
    nanos = System.nanoTime() - nanos;
    showTime("index tree search ", nanos);

    hnanos = System.nanoTime();
    for (int ix = 0; ix < count; ix++) {
      String val = h.get(ix);
      assert val != null;
    }
    hnanos = System.nanoTime() - hnanos;
    showTime("hash map search ", hnanos);

    nanos = System.nanoTime();
    for (int ix = count - 1; ix >= 0; ix--)
      tr = tr.delete(ix);
    nanos = System.nanoTime() - nanos;
    showTime("index tree delete ", nanos);

    hnanos = System.nanoTime();
    for (int ix = count - 1; ix >= 0; ix--)
      h.remove(ix);
    hnanos = System.nanoTime() - hnanos;
    showTime("hash map delete ", hnanos);
  }

  @Test
  public void randomTest()
  {
    int count = 50000;
    long nanos = System.nanoTime();
    Random random = new Random();

    IndexTree<Integer, String> tr = IndexTree.emptyTree();

    for (int ix = 0; ix < count; ix++) {
      int key = random.nextInt() % count;

      String val = tr.find(ix);
      if (val == null)
        tr = tr.insrt(key, "random" + ix);
    }
    nanos = System.nanoTime() - nanos;
    showTime("random tree insert/search ", nanos);
    Runtime runtime = Runtime.getRuntime();
    System.out.println("final tree size " + tr.size() + " elements");
    System.out.println(((double) (runtime.totalMemory() - runtime.freeMemory())) / 1.0e9 + "gb memory used");
    assert tr.size()<=count;
  }

  private void showTime(String msg, long nanos)
  {
    System.out.println(msg + ((double) nanos) / 1000000000);
  }

  private IndexTree<Integer, String> buildTree(int count)
  {
    IndexTree<Integer, String> tr = IndexTree.emptyTree();

    for (int ix = 0; ix < count; ix++)
      tr = tr.insrt(ix, Integer.toString(ix));

    return tr;
  }

  private IndexTree<Integer, String> buildTree(int count, String fixed)
  {
    IndexTree<Integer, String> tr = IndexTree.emptyTree();

    for (int ix = 0; ix < count; ix++)
      tr = tr.insrt(ix, fixed);

    return tr;
  }

  @SuppressWarnings("unused")
  private Map<Integer, String> buildHash(int count)
  {
    Map<Integer, String> tr = new HashMap<>();

    for (int ix = 0; ix < count; ix++)
      tr.put(ix, Integer.toString(ix));
    return tr;
  }

  private Map<Integer, String> buildHash(int count, String fixed)
  {
    Map<Integer, String> tr = new HashMap<>();

    for (int ix = 0; ix < count; ix++)
      tr.put(ix, fixed);
    return tr;
  }

  @Test
  public void testAlpha() throws EvaluationException
  {
    IMap map2 = Factory.newMap(StandardTypes.stringType, StandardTypes.stringType);
    map2 = map2.setMember(Factory.newString("STOP"), Factory.newString("STOP_VAL"));
    map2 = map2.setMember(Factory.newString("START"), Factory.newString("START_VAL"));
    map2 = map2.setMember(Factory.newString("PAUSE"), Factory.newString("PAUSE_VAL"));
    map2 = map2.setMember(Factory.newString("PREPARE"), Factory.newString("PREPARE_VAL"));
    map2 = map2.setMember(Factory.newString("RELEASE"), Factory.newString("RELEASE_VAL"));
    map2 = map2.setMember(Factory.newString("RESUME"), Factory.newString("RESUME_VAL"));

    System.out.println("map is " + map2);

    System.out.println(map2.getMember(Factory.newString("STOP")));
    System.out.println(map2.getMember(Factory.newString("START")));
    System.out.println(map2.getMember(Factory.newString("PAUSE")));
    System.out.println(map2.getMember(Factory.newString("PREPARE")));
    System.out.println(map2.getMember(Factory.newString("RELEASE")));
    System.out.println(map2.getMember(Factory.newString("RESUME")));

    assert map2.getMember(Factory.newString("STOP")).equals(Factory.newString("STOP_VAL"));
  }
}
