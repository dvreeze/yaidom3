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

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.Namespaces.LocalName
import eu.cdevreeze.yaidom3.core.Namespaces.Namespace
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.ClarkElemQueryApi

/**
 * Delegating implementation of ClarkElemQueryApi, delegating to a ClarkElemApi implementation. This implementation trait is intentionally
 * completely invisible in the public API.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The ClarkElemApi implementation to which this trait delegates
 */
private[node] transparent trait DelegatingClarkElemQueryApi[E <: ClarkElemApi[E]] extends ClarkElemQueryApi[E]:

  def filterChildElems(elem: E, p: E => Boolean): Seq[E] = elem.filterChildElems(p)

  def findAllChildElems(elem: E): Seq[E] = elem.findAllChildElems

  def findChildElem(elem: E, p: E => Boolean): Option[E] = elem.findChildElem(p)

  def filterDescendantElems(elem: E, p: E => Boolean): Seq[E] = elem.filterDescendantElems(p)

  def findAllDescendantElems(elem: E): Seq[E] = elem.findAllDescendantElems

  def findDescendantElem(elem: E, p: E => Boolean): Option[E] = elem.findDescendantElem(p)

  def filterDescendantElemsOrSelf(elem: E, p: E => Boolean): Seq[E] = elem.filterDescendantElemsOrSelf(p)

  def findAllDescendantElemsOrSelf(elem: E): Seq[E] = elem.findAllDescendantElemsOrSelf

  def findDescendantElemOrSelf(elem: E, p: E => Boolean): Option[E] = elem.findDescendantElemOrSelf(p)

  def findTopmostElems(elem: E, p: E => Boolean): Seq[E] = elem.findTopmostElems(p)

  def findTopmostElemsOrSelf(elem: E, p: E => Boolean): Seq[E] = elem.findTopmostElemsOrSelf(p)

  def findDescendantElemOrSelf(elem: E, navigationPath: NavigationPath): Option[E] =
    elem.findDescendantElemOrSelf(navigationPath)

  def getDescendantElemOrSelf(elem: E, navigationPath: NavigationPath): E =
    elem.getDescendantElemOrSelf(navigationPath)

  def name(elem: E): EName = elem.name

  def attrs(elem: E): ListMap[EName, String] = elem.attrs

  def attrOption(elem: E, attrName: EName): Option[String] = elem.attrOption(attrName)

  def attr(elem: E, attrName: EName): String = elem.attr(attrName)

  def text(elem: E): String = elem.text

  def normalizedText(elem: E): String = elem.normalizedText

  def hasLocalName(elem: E, localName: LocalName): Boolean = elem.hasLocalName(localName)

  def hasName(elem: E, name: EName): Boolean = elem.hasName(name)

  def hasName(elem: E, namespaceOption: Option[Namespace], localName: LocalName): Boolean = elem.hasName(namespaceOption, localName)

  def hasName(elem: E, namespace: Namespace, localName: LocalName): Boolean = elem.hasName(namespace, localName)

end DelegatingClarkElemQueryApi
