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
import eu.cdevreeze.yaidom3.core.Namespaces.LocalName
import eu.cdevreeze.yaidom3.core.Namespaces.Namespace
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath

/**
 * Element node OO query API, knowing only about "Clark elements", having expanded names, and not knowing anything about namespaces scopes
 * and QNames. This API is implemented by specific element implementations.
 *
 * There are element-centric query methods for the child, descendant and descendant-or-self axes, in XPath terms. There are also query
 * methods for the name and the attributes of the element.
 *
 * The query methods for child, descendant and descendant-or-self elements return the resulting elements in document order. Note that in the
 * case of the descendant and descendant-or-self axes, later element trees in the returned result are parts of earlier element trees in the
 * returned result.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The "self type", which typically but not necessarily is a sub-type of the trait (F-bounded polymorphism)
 */
trait ClarkElemApi[E]:

  /**
   * In XPath terms, queries for the child axis, but only returning element nodes, and only those element nodes that meet the parameter
   * element predicate.
   */
  def filterChildElems(p: E => Boolean): Seq[E]

  /**
   * Returns the same as `filterChildElems(_ => true)`.
   */
  def findAllChildElems: Seq[E]

  /**
   * Returns the equivalent of `filterChildElems(p).headOption`.
   */
  def findChildElem(p: E => Boolean): Option[E]

  /**
   * In XPath terms, queries for the descendant axis, but only returning element nodes, and only those element nodes that meet the parameter
   * element predicate.
   */
  def filterDescendantElems(p: E => Boolean): Seq[E]

  /**
   * Returns the same as `filterDescendantElems(_ => true)`.
   */
  def findAllDescendantElems: Seq[E]

  /**
   * Returns the equivalent of `filterDescendantElems(p).headOption`.
   */
  def findDescendantElem(p: E => Boolean): Option[E]

  /**
   * In XPath terms, queries for the descendant-or-self axis, but only returning element nodes, and only those element nodes that meet the
   * parameter element predicate.
   */
  def filterDescendantElemsOrSelf(p: E => Boolean): Seq[E]

  /**
   * Returns the same as `filterDescendantElemsOrSelf(_ => true)`.
   */
  def findAllDescendantElemsOrSelf: Seq[E]

  /**
   * Returns the equivalent of `filterDescendantElemsOrSelf(p).headOption`.
   */
  def findDescendantElemOrSelf(p: E => Boolean): Option[E]

  /**
   * Returns the topmost results in `filterDescendantElems(p)`, so for each found result element, its descendants are skipped.
   */
  def findTopmostElems(p: E => Boolean): Seq[E]

  /**
   * Returns the topmost results in `filterDescendantElemsOrSelf(p)`, so for each found result element, its descendants are skipped. Hence,
   * if this element itself meets the parameter predicate, processing stops.
   */
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

  /**
   * Returns the string concatenation of the text child node values.
   */
  def text: String

  /**
   * Normalizes the result of function `text`, removing surrounding whitespace and normalizing internal whitespace to a single space.
   * Whitespace includes #x20 (space), #x9 (tab), #xD (carriage return), #xA (line feed). If there is only whitespace, the empty string is
   * returned. Inspired by the JDOM library.
   */
  def normalizedText: String

  /**
   * Returns true if the local part of the element name equals the parameter local name.
   */
  def hasLocalName(localName: LocalName): Boolean

  def hasName(name: EName): Boolean

  /**
   * Returns true if the element name matches the parameter optional namespace and local name.
   */
  def hasName(namespaceOption: Option[Namespace], localName: LocalName): Boolean

  /**
   * Returns true if the element name matches the parameter namespace and local name.
   */
  def hasName(namespace: Namespace, localName: LocalName): Boolean

end ClarkElemApi
