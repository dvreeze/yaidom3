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
 * Element node query API, knowing about QNames and ENames (and therefore about Scopes resolving QNames as ENames), and also about their
 * context (such as ancestry, document URI etc.). See the corresponding methods in `CommonElemApi[E]` (without the first parameter) for an
 * explanation.
 *
 * @author
 *   Chris de Vreeze
 */
trait CommonElemQueryApi[E] extends ScopedElemQueryApi[E]:

  def findParentElem(elem: E, p: E => Boolean): Option[E]

  def parentElemOption(elem: E): Option[E]

  def filterAncestorElems(elem: E, p: E => Boolean): Seq[E]

  def findAllAncestorElems(elem: E): Seq[E]

  def findAncestorElem(elem: E, p: E => Boolean): Option[E]

  def filterAncestorElemsOrSelf(elem: E, p: E => Boolean): Seq[E]

  def findAllAncestorElemsOrSelf(elem: E): Seq[E]

  def findAncestorElemOrSelf(elem: E, p: E => Boolean): Option[E]

  def findAllPrecedingSiblingElems(elem: E): Seq[E]

  def ownNavigationPathRelativeToRootElem(elem: E): NavigationPath

  def baseUriOption(elem: E): Option[URI]

  def baseUri(elem: E): URI

  def docUriOption(elem: E): Option[URI]

  def docUri(elem: E): URI

  def rootElem(elem: E): E

end CommonElemQueryApi
