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

package eu.cdevreeze.yaidom3.node.internal

import java.net.URI

import eu.cdevreeze.yaidom3.core.Navigation
import eu.cdevreeze.yaidom3.queryapi.CommonElemApi
import eu.cdevreeze.yaidom3.queryapi.CommonElemQueryApi

/**
 * Delegating implementation of CommonElemQueryApi, delegating to a CommonElemApi implementation. This implementation trait is intentionally
 * completely invisible in the public API.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The CommonElemApi implementation to which this trait delegates
 */
private[node] transparent trait DelegatingCommonElemQueryApi[E <: CommonElemApi[E]]
    extends DelegatingScopedElemQueryApi[E],
      CommonElemQueryApi[E]:

  def findParentElem(elem: E, p: E => Boolean): Option[E] = elem.findParentElem(p)

  def findParentElem(elem: E): Option[E] = elem.findParentElem

  def filterAncestorElems(elem: E, p: E => Boolean): Seq[E] = elem.filterAncestorElems(p)

  def findAllAncestorElems(elem: E): Seq[E] = elem.findAllAncestorElems

  def findAncestorElem(elem: E, p: E => Boolean): Option[E] = elem.findAncestorElem(p)

  def filterAncestorElemsOrSelf(elem: E, p: E => Boolean): Seq[E] = elem.filterAncestorElemsOrSelf(p)

  def findAllAncestorElemsOrSelf(elem: E): Seq[E] = elem.findAllAncestorElemsOrSelf

  def findAncestorElemOrSelf(elem: E, p: E => Boolean): Option[E] = elem.findAncestorElemOrSelf(p)

  def findAllPrecedingSiblingElems(elem: E): Seq[E] = elem.findAllPrecedingSiblingElems

  def ownNavigationPathRelativeToRootElem(elem: E): Navigation.NavigationPath = elem.ownNavigationPathRelativeToRootElem

  def baseUriOption(elem: E): Option[URI] = elem.baseUriOption

  def baseUri(elem: E): URI = elem.baseUri

  def docUriOption(elem: E): Option[URI] = elem.docUriOption

  def docUri(elem: E): URI = elem.docUri

  def rootElem(elem: E): E = elem.rootElem

end DelegatingCommonElemQueryApi
