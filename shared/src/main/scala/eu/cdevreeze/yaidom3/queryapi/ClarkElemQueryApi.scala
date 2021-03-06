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
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath

/**
 * Element node query API, knowing only about "Clark elements", having expanded names, and not knowing anything about namespaces scopes and
 * QNames. See the corresponding methods in `ClarkElemApi[E]` (without the first parameter) for an explanation.
 *
 * @author
 *   Chris de Vreeze
 */
trait ClarkElemQueryApi[E]:

  def filterChildElems(elem: E, p: E => Boolean): Seq[E]

  def findAllChildElems(elem: E): Seq[E]

  def findChildElem(elem: E, p: E => Boolean): Option[E]

  def filterDescendantElems(elem: E, p: E => Boolean): Seq[E]

  def findAllDescendantElems(elem: E): Seq[E]

  def findDescendantElem(elem: E, p: E => Boolean): Option[E]

  def filterDescendantElemsOrSelf(elem: E, p: E => Boolean): Seq[E]

  def findAllDescendantElemsOrSelf(elem: E): Seq[E]

  def findDescendantElemOrSelf(elem: E, p: E => Boolean): Option[E]

  def findTopmostElems(elem: E, p: E => Boolean): Seq[E]

  def findTopmostElemsOrSelf(elem: E, p: E => Boolean): Seq[E]

  def findDescendantElemOrSelf(elem: E, navigationPath: NavigationPath): Option[E]

  def getDescendantElemOrSelf(elem: E, navigationPath: NavigationPath): E

  def name(elem: E): EName

  def attrs(elem: E): ListMap[EName, String]

  def attrOption(elem: E, attrName: EName): Option[String]

  def attr(elem: E, attrName: EName): String

  def text(elem: E): String

  def normalizedText(elem: E): String

  def hasLocalName(elem: E, localName: LocalName): Boolean

  def hasName(elem: E, name: EName): Boolean

  def hasName(elem: E, namespaceOption: Option[Namespace], localName: LocalName): Boolean

  def hasName(elem: E, namespace: Namespace, localName: LocalName): Boolean

end ClarkElemQueryApi
