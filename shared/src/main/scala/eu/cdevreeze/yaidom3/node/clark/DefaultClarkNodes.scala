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

package eu.cdevreeze.yaidom3.node.clark

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.Namespaces._
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.internal.StringUtil
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.ClarkElemQueryApi
import eu.cdevreeze.yaidom3.queryapi.Nodes

/**
 * "Clark nodes". See the ClarkElemQueryApi and ClarkElemApi for the API offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
object DefaultClarkNodes extends ClarkElemQueryApi[DefaultClarkNodes.Elem]:

  sealed trait Node extends Nodes.Node

  final case class Text(value: String) extends Node, Nodes.Text

  final case class Elem(name: EName, attrs: ListMap[EName, String], children: Seq[Node]) extends Node, Nodes.Elem, ClarkElemApi[Elem]:

    def filterChildElems(p: Elem => Boolean): Seq[Elem] =
      children.collect { case e: Elem if p(e) => e }

    def findAllChildElems: Seq[Elem] = filterChildElems(_ => true)

    def findChildElem(p: Elem => Boolean): Option[Elem] =
      children.collectFirst { case e: Elem if p(e) => e }

    def filterDescendantElems(p: Elem => Boolean): Seq[Elem] =
      findAllChildElems.flatMap(_.filterDescendantElemsOrSelf(p))

    def findAllDescendantElems: Seq[Elem] = filterDescendantElems(_ => true)

    def findDescendantElem(p: Elem => Boolean): Option[Elem] =
      findAllChildElems.view.flatMap(_.filterDescendantElemsOrSelf(p)).headOption

    def filterDescendantElemsOrSelf(p: Elem => Boolean): Seq[Elem] =
      Seq(this)
        .filter(p)
        .appendedAll(findAllChildElems.flatMap(_.filterDescendantElemsOrSelf(p)))

    def findAllDescendantElemsOrSelf: Seq[Elem] = filterDescendantElemsOrSelf(_ => true)

    def findDescendantElemOrSelf(p: Elem => Boolean): Option[Elem] =
      if p(this) then Some(this)
      else findAllChildElems.view.flatMap(_.filterDescendantElemsOrSelf(p)).headOption

    def findTopmostElems(p: Elem => Boolean): Seq[Elem] =
      findAllChildElems.flatMap(_.findTopmostElemsOrSelf(p))

    def findTopmostElemsOrSelf(p: Elem => Boolean): Seq[Elem] =
      if p(this) then Seq(this)
      else findAllChildElems.flatMap(_.findTopmostElemsOrSelf(p))

    def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[Elem] =
      if (navigationPath.isEmpty) then Some(this)
      else
        val childStep = navigationPath.head
        val childElems: Seq[Elem] = findAllChildElems

        if childStep.toInt >= 0 && childStep.toInt < childElems.size then
          Option(childElems(childStep.toInt)).flatMap(_.findDescendantElemOrSelf(navigationPath.tail))
        else None
    end findDescendantElemOrSelf

    def getDescendantElemOrSelf(navigationPath: NavigationPath): Elem =
      findDescendantElemOrSelf(navigationPath)
        .getOrElse(sys.error(s"Could not find descendant-or-self element with relative navigation path '$navigationPath'"))

    def attrOption(attrName: EName): Option[String] = attrs.get(attrName)

    def text: String = children.collect { case t: Text => t.value }.mkString

    def normalizedText: String = StringUtil.normalizeString(text)

    def hasLocalName(localName: String): Boolean = name.localPart.localNameAsString == localName

    def hasName(namespaceOption: Option[String], localName: String): Boolean =
      name.namespaceOption.map(_.namespaceAsString) == namespaceOption && name.localPart.localNameAsString == localName

    def hasName(namespace: String, localName: String): Boolean =
      name.namespaceOption.map(_.namespaceAsString).contains(namespace) && name.localPart.localNameAsString == localName

    def hasName(localName: String): Boolean =
      name.namespaceOption.isEmpty && name.localPart.localNameAsString == localName

  object Elem:

    def from(otherElem: ClarkElemApi[?] & Nodes.Elem): Elem =
      val children: Seq[Node] = otherElem.children.collect {
        case t: Nodes.Text                     => Text(t.value)
        case e: (ClarkElemApi[?] & Nodes.Elem) =>
          // Recursive call
          from(e)
      }
      Elem(otherElem.name, otherElem.attrs, children)
    end from

  end Elem

  private type E = Elem

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

  def text(elem: E): String = elem.text

  def normalizedText(elem: E): String = elem.normalizedText

  def hasLocalName(elem: E, localName: String): Boolean = elem.hasLocalName(localName)

  def hasName(elem: E, namespaceOption: Option[String], localName: String): Boolean = elem.hasName(namespaceOption, localName)

  def hasName(elem: E, namespace: String, localName: String): Boolean = elem.hasName(namespace, localName)

  def hasName(elem: E, localName: String): Boolean = elem.hasName(localName)

end DefaultClarkNodes
