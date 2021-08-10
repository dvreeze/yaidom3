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

import java.net.URI

import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath

/**
 * Element node OO query API, knowing about QNames and ENames (and therefore about Scopes resolving QNames as ENames), and also about their
 * context (such as ancestry, document URI etc.). This API is implemented by specific element implementations.
 *
 * There are element-centric query methods for the parent, ancestor and ancestor-or-self axes, in XPath terms. There are also query methods
 * for the root element (uppermost ancestor-or-self), document URI, base URI, etc.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The "self type", which typically but not necessarily is a sub-type of the trait (F-bounded polymorphism)
 */
trait CommonElemApi[E] extends ScopedElemApi[E]:

  /**
   * In XPath terms, queries for the parent axis, but only returning an optional element node, and only if it meets the parameter element
   * predicate.
   */
  def findParentElem(p: E => Boolean): Option[E]

  /**
   * Returns the same as `findParentElem(_ => true)`.
   */
  def parentElemOption: Option[E]

  /**
   * In XPath terms, queries for the ancestor axis, but only returning element nodes, and only those element nodes that meet the parameter
   * element predicate. The first returned element, if any, is the parent element, and the last, if any, is the root element.
   */
  def filterAncestorElems(p: E => Boolean): Seq[E]

  /**
   * Returns the same as `filterAncestorElems(_ => true)`.
   */
  def findAllAncestorElems: Seq[E]

  /**
   * Returns the equivalent of `filterAncestorElems(p).headOption`.
   */
  def findAncestorElem(p: E => Boolean): Option[E]

  /**
   * In XPath terms, queries for the ancestor-or-self axis, but only returning element nodes, and only those element nodes that meet the
   * parameter element predicate. The first returned element is this element, and the last is the root element (they might be the same).
   */
  def filterAncestorElemsOrSelf(p: E => Boolean): Seq[E]

  /**
   * Returns the same as `filterAncestorElemsOrSelf(_ => true)`.
   */
  def findAllAncestorElemsOrSelf: Seq[E]

  /**
   * Returns the equivalent of `filterAncestorElemsOrSelf(p).headOption`.
   */
  def findAncestorElemOrSelf(p: E => Boolean): Option[E]

  /**
   * Returns all preceding sibling element nodes. This method is needed for computing the relative "navigation path" to the root.
   */
  def findAllPrecedingSiblingElems: Seq[E]

  /**
   * Returns the own navigation path relative to the root element. For example, if it is Seq(3, 5, 0), this means that this element can be
   * found from the root element as follows: from the root, take the child element with zero-based element index 3, from there take its
   * child element with zero-based element index 5, and finally from there take its child element with zero-based element index 0.
   */
  def ownNavigationPathRelativeToRootElem: NavigationPath

  /**
   * Returns the optional base URI, computed from the document URI, if any, and the XML base attributes of the ancestors, if any.
   */
  def baseUriOption: Option[URI]

  /**
   * The base URI, defaulting to the empty URI if absent
   */
  def baseUri: URI

  /**
   * The optional document URI of the containing document, if any
   */
  def docUriOption: Option[URI]

  /**
   * The document URI, defaulting to the empty URI if absent
   */
  def docUri: URI

  /**
   * The root element
   */
  def rootElem: E

end CommonElemApi
