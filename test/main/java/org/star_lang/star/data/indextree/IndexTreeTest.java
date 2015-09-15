package org.star_lang.star.data.indextree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.indextree.IndexTree;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;

/*
  * Copyright (c) 2015. Francis G. McCabe
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the
  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied. See the License for the specific language governing
  * permissions and limitations under the License.
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
