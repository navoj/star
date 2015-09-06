package org.star_lang.star.data.indextree;

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

/**
 * Define the fold function that is applied to elements of a collection
 * 
 * @param <T>
 *          The element to examine
 * @param <S>
 *          The input state value
 * @return the result state after examining this key/value pair
 */
public interface Fold<T, S>
{
  S apply(T value, S init);
}