/*
 * Copyright 2021-2021 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.yaidom3.updateapi

/**
 * Type class extending "common" elements with functional updates within surrounding element trees (thus updating them as well).
 *
 * @author
 *   Chris de Vreeze
 * @tparam E
 *   The element node type
 */
trait UpdatableCommonElemApi[E]:

  /**
   * The underlying element type, which may or may not be the same as the element type. Typically type class UpdatableElemApi has an
   * instance for that underlying element type.
   */
  type UE

  extension (elem: E)
    /**
     * Functionally updates this element, within the surrounding element tree.
     *
     * Note that the root of the tree changes by calling this method, and it must somehow be obtained from the function result.
     */
    def updatedWithinTree(f: UE => UE): E

    /**
     * Filters descendant-or-self elements of this element, and updates them functionally within the surrounding element tree. The passed
     * predicate is used to filter descendant-or-self elements of this element that are updated using the 2nd parameter function.
     *
     * Note that the root of the tree changes by calling this method, and it must somehow be obtained from the function result. Typically
     * this method is called on the root element, though.
     */
    def filterAndUpdateDescendantElemsOrSelfWithinTree(p: E => Boolean)(f: UE => UE): E

end UpdatableCommonElemApi
