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
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi

/**
 * Partial implementation of ClarkElemApi, useful for element implementations that are not wrappers around other "XML backends". This
 * implementation trait is intentionally completely invisible in the public API.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The "self type", using F-bounded polymorphism
 */
private[node] transparent trait PartialClarkElem[E <: PartialClarkElem[E]](
    name: EName,
    attrs: ListMap[EName, String],
    childElems: Seq[E]
) extends ClarkElemApi[E]:

  self: E =>

  def filterChildElems(p: E => Boolean): Seq[E] = childElems.filter(p)

  def findAllChildElems: Seq[E] = childElems

  def findChildElem(p: E => Boolean): Option[E] = childElems.find(p)

  def filterDescendantElems(p: E => Boolean): Seq[E] =
    childElems.flatMap(_.filterDescendantElemsOrSelf(p))

  def findAllDescendantElems: Seq[E] = filterDescendantElems(_ => true)

  def findDescendantElem(p: E => Boolean): Option[E] =
    childElems.view.flatMap(_.findDescendantElemOrSelf(p)).headOption

  def filterDescendantElemsOrSelf(p: E => Boolean): Seq[E] =
    Vector(self)
      .filter(p)
      .appendedAll(
        childElems.flatMap(_.filterDescendantElemsOrSelf(p))
      )

  def findAllDescendantElemsOrSelf: Seq[E] = filterDescendantElemsOrSelf(_ => true)

  def findDescendantElemOrSelf(p: E => Boolean): Option[E] =
    Option(self)
      .filter(p)
      .orElse(
        childElems.view.flatMap(_.findDescendantElemOrSelf(p)).headOption
      )

  def findTopmostElems(p: E => Boolean): Seq[E] =
    childElems.flatMap(_.findTopmostElemsOrSelf(p))

  def findTopmostElemsOrSelf(p: E => Boolean): Seq[E] =
    if p(self) then Seq(self)
    else childElems.flatMap(_.findTopmostElemsOrSelf(p))

  def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[E] =
    if (navigationPath.isEmpty) then Some(this)
    else
      val childStep = navigationPath.head

      if childStep.toInt >= 0 && childStep.toInt < childElems.size then
        Option(childElems(childStep.toInt)).flatMap(_.findDescendantElemOrSelf(navigationPath.tail))
      else None
  end findDescendantElemOrSelf

  def getDescendantElemOrSelf(navigationPath: NavigationPath): E =
    findDescendantElemOrSelf(navigationPath)
      .getOrElse(sys.error(s"Could not find descendant-or-self element with relative navigation path '$navigationPath'"))

  def attrOption(attrName: EName): Option[String] = attrs.get(attrName)

  def hasLocalName(localName: String): Boolean = name.localPart.localNameAsString == localName

  def hasName(namespaceOption: Option[String], localName: String): Boolean =
    name.namespaceOption.map(_.namespaceAsString) == namespaceOption && name.localPart.localNameAsString == localName

  def hasName(namespace: String, localName: String): Boolean =
    name.namespaceOption.map(_.namespaceAsString).contains(namespace) && name.localPart.localNameAsString == localName

  def hasName(localName: String): Boolean =
    name.namespaceOption.isEmpty && name.localPart.localNameAsString == localName

end PartialClarkElem
