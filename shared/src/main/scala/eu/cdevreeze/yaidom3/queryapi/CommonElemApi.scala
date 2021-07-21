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
 * OO API for ElemQueryApi, implemented by element implementations.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The "self type", which typically but not necessarily is a sub-type of the trait (F-bounded polymorphism)
 */
trait CommonElemApi[E] extends ScopedElemApi[E]:

  def findParentElem(p: E => Boolean): Option[E]

  def findParentElem: Option[E]

  def filterAncestorElems(p: E => Boolean): Seq[E]

  def findAllAncestorElems: Seq[E]

  def findAncestorElem(p: E => Boolean): Option[E]

  def filterAncestorElemsOrSelf(p: E => Boolean): Seq[E]

  def findAllAncestorElemsOrSelf: Seq[E]

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
