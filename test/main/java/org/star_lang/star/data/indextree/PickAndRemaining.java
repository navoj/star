package org.star_lang.star.data.indextree;

/*
 * Copyright (c) 2016. Francis G. McCabe
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

import org.junit.Test;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;

/**
 * Created by fgm on 3/30/16.
 */
public class PickAndRemaining {
  @Test
  public void pickTest() throws EvaluationException {
    IMap map = Factory.newMap(StandardTypes.stringType, StandardTypes.stringType);
    map = map.setMember(Factory.newString("STOP"), Factory.newString("STOP_VAL"));
    map = map.setMember(Factory.newString("START"), Factory.newString("START_VAL"));
    map = map.setMember(Factory.newString("PAUSE"), Factory.newString("PAUSE_VAL"));
    map = map.setMember(Factory.newString("PREPARE"), Factory.newString("PREPARE_VAL"));
    map = map.setMember(Factory.newString("RELEASE"), Factory.newString("RELEASE_VAL"));
    map = map.setMember(Factory.newString("RESUME"), Factory.newString("RESUME_VAL"));

    System.out.println("map is " + map);

    while (!map.isEmpty()) {
      IValue pair = map.pick();

      System.out.println("Pick "+pair);

      assert pair.equals(map.pick());
      map = map.remaining();
    }
  }
}
