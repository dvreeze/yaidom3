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

package eu.cdevreeze.yaidom3.queryapi

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath

/**
 * OO API for ClarkElemQueryApi, implemented by element implementations.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The "self type", which typically but not necessarily is a sub-type of the trait (F-bounded polymorphism)
 */
trait ClarkElemApi[E]:

  def filterChildElems(p: E => Boolean): Seq[E]

  def findAllChildElems: Seq[E]

  def findChildElem(p: E => Boolean): Option[E]

  def filterDescendantElems(p: E => Boolean): Seq[E]

  def findAllDescendantElems: Seq[E]

  def findDescendantElem(p: E => Boolean): Option[E]

  def filterDescendantElemsOrSelf(p: E => Boolean): Seq[E]

  def findAllDescendantElemsOrSelf: Seq[E]

  def findDescendantElemOrSelf(p: E => Boolean): Option[E]

  def findTopmostElems(p: E => Boolean): Seq[E]

  def findTopmostElemsOrSelf(p: E => Boolean): Seq[E]

  /**
   * Returns the optional descendant-or-self element at the given navigation path. If the navigation path is Seq(3, 5, 0), the first
   * navigation step is to the child element at (element) index 3, zero-based, the next navigation step is to its child element at
   * zero-based (element) index 5, and the last navigation step is to the latter's child element at zero-based (element) index 0.
   *
   * If the navigation path is out of bounds in one of the steps, None is returned.
   */
  def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[E]

  /**
   * Returns the descendant-or-self element at the given navigation path. If the navigation path is Seq(3, 5, 0), the first navigation step
   * is to the child element at (element) index 3, zero-based, the next navigation step is to its child element at zero-based (element)
   * index 5, and the last navigation step is to the latter's child element at zero-based (element) index 0.
   *
   * If the navigation path is out of bounds in one of the steps, an exception is thrown.
   */
  def getDescendantElemOrSelf(navigationPath: NavigationPath): E

  def name: EName

  def attrs: ListMap[EName, String]

  def attrOption(attrName: EName): Option[String]

  def attr(attrName: EName): String

  def text: String

  def normalizedText: String

  def hasLocalName(localName: String): Boolean

  def hasName(namespaceOption: Option[String], localName: String): Boolean

  def hasName(namespace: String, localName: String): Boolean

  def hasName(localName: String): Boolean

end ClarkElemApi
