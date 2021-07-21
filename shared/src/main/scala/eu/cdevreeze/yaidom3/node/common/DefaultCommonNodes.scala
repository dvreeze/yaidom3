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

package eu.cdevreeze.yaidom3.node.common

import java.net.URI

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.Namespaces._
import eu.cdevreeze.yaidom3.core.Navigation
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.Navigation.NavigationStep
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.node.scoped.DefaultScopedNodes
import eu.cdevreeze.yaidom3.queryapi.CommonElemApi
import eu.cdevreeze.yaidom3.queryapi.CommonElemQueryApi
import eu.cdevreeze.yaidom3.queryapi.Nodes

/**
 * "Common nodes". See the CommonElemQueryApi and CommonElemApi for the API offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
object DefaultCommonNodes extends CommonElemQueryApi[DefaultCommonNodes.Elem]:

  sealed trait Node extends Nodes.Node

  final case class Text(value: String, isCData: Boolean) extends Node, Nodes.Text

  final case class Comment(value: String) extends Node, Nodes.Comment

  final case class ProcessingInstruction(target: String, data: String) extends Node, Nodes.ProcessingInstruction

  final case class Elem private (
      docUriOption: Option[URI],
      underlyingRootElem: DefaultScopedNodes.Elem,
      elemNavigationPathFromRoot: Navigation.NavigationPath,
      underlyingElem: DefaultScopedNodes.Elem
  ) extends Node,
        Nodes.Elem,
        CommonElemApi[Elem]:

    def children: Seq[Node] =
      var childElemIdx = 0

      underlyingElem.children.map {
        case che: DefaultScopedNodes.Elem =>
          val step = NavigationStep(childElemIdx)
          childElemIdx += 1
          new Elem(docUriOption, underlyingRootElem, elemNavigationPathFromRoot.appended(step), che)
        case DefaultScopedNodes.Text(value, isCData) =>
          Text(value, isCData)
        case DefaultScopedNodes.Comment(value) =>
          Comment(value)
        case DefaultScopedNodes.ProcessingInstruction(target, data) =>
          ProcessingInstruction(target, data)
      }
    end children

    def filterChildElems(p: Elem => Boolean): Seq[Elem] =
      underlyingElem.findAllChildElems.zipWithIndex
        .map { (che, idx) =>
          new Elem(docUriOption, underlyingRootElem, elemNavigationPathFromRoot.appended(Navigation.NavigationStep(idx)), che)
        }
        .filter(p)

    def findAllChildElems: Seq[Elem] = filterChildElems(_ => true)

    def findChildElem(p: Elem => Boolean): Option[Elem] =
      underlyingElem.findAllChildElems.zipWithIndex.view
        .map { (che, idx) =>
          new Elem(docUriOption, underlyingRootElem, elemNavigationPathFromRoot.appended(Navigation.NavigationStep(idx)), che)
        }
        .find(p)

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
      val underlyingTargetElemOption: Option[DefaultScopedNodes.Elem] = underlyingElem.findDescendantElemOrSelf(navigationPath)
      val targetOwnNavigationPathOption = underlyingTargetElemOption.map(_ => elemNavigationPathFromRoot.appendedAll(navigationPath))

      targetOwnNavigationPathOption.map { path =>
        new Elem(docUriOption, underlyingRootElem, path, underlyingTargetElemOption.get)
      }
    end findDescendantElemOrSelf

    def getDescendantElemOrSelf(navigationPath: NavigationPath): Elem =
      findDescendantElemOrSelf(navigationPath)
        .getOrElse(sys.error(s"Could not find descendant-or-self element with relative navigation path '$navigationPath'"))

    def attrOption(attrName: EName): Option[String] = underlyingElem.attrOption(attrName)

    def text: String = underlyingElem.text

    def normalizedText: String = underlyingElem.normalizedText

    def hasLocalName(localName: String): Boolean = name.localPart.localNameAsString == localName

    def hasName(namespaceOption: Option[String], localName: String): Boolean =
      name.namespaceOption.map(_.namespaceAsString) == namespaceOption && name.localPart.localNameAsString == localName

    def hasName(namespace: String, localName: String): Boolean =
      name.namespaceOption.map(_.namespaceAsString).contains(namespace) && name.localPart.localNameAsString == localName

    def hasName(localName: String): Boolean =
      name.namespaceOption.isEmpty && name.localPart.localNameAsString == localName

    def name: EName = underlyingElem.name

    def attrs: ListMap[EName, String] = underlyingElem.attrs

    def scope: Scope = underlyingElem.scope

    def qname: QName = underlyingElem.qname

    def attrsByQName: ListMap[QName, String] = underlyingElem.attrsByQName

    def textAsQName: QName = underlyingElem.textAsQName

    def textAsResolvedQName(using enameProvider: ENameProvider): EName = underlyingElem.textAsResolvedQName(using enameProvider)

    def attrAsQNameOption(attrName: EName): Option[QName] = underlyingElem.attrAsQNameOption(attrName)

    def attrAsQName(attrName: EName): QName = underlyingElem.attrAsQName(attrName)

    def attrAsResolvedQNameOption(attrName: EName)(using enameProvider: ENameProvider): Option[EName] =
      underlyingElem.attrAsResolvedQNameOption(attrName)(using enameProvider)

    def attrAsResolvedQName(attrName: EName)(using enameProvider: ENameProvider): EName =
      underlyingElem.attrAsResolvedQName(attrName)(using enameProvider)

    def findParentElem(p: Elem => Boolean): Option[Elem] =
      if elemNavigationPathFromRoot.isEmpty then None
      else
        val parentPath = elemNavigationPathFromRoot.init
        val underlyingParentElem = underlyingRootElem.getDescendantElemOrSelf(parentPath)
        val parentElem = new Elem(docUriOption, underlyingRootElem, parentPath, underlyingParentElem)
        Some(parentElem).filter(p)
    end findParentElem

    def findParentElem: Option[Elem] = findParentElem(_ => true)

    def filterAncestorElems(p: Elem => Boolean): Seq[Elem] =
      findParentElem.toSeq.flatMap(_.filterAncestorElemsOrSelf(p))

    def findAllAncestorElems: Seq[Elem] = filterAncestorElems(_ => true)

    def findAncestorElem(p: Elem => Boolean): Option[Elem] =
      filterAncestorElems(p).headOption // TODO Improve performance!

    def filterAncestorElemsOrSelf(p: Elem => Boolean): Seq[Elem] =
      Seq(this).filter(p).appendedAll(findParentElem.toSeq.flatMap(_.filterAncestorElemsOrSelf(p)))

    def findAllAncestorElemsOrSelf: Seq[Elem] = filterAncestorElemsOrSelf(_ => true)

    def findAncestorElemOrSelf(p: Elem => Boolean): Option[Elem] =
      filterAncestorElemsOrSelf(p).headOption // TODO Improve performance!

    def findAllPrecedingSiblingElems: Seq[Elem] =
      val parentElemOption = findParentElem

      if parentElemOption.isEmpty then Seq.empty
      else parentElemOption.get.findAllChildElems.takeWhile(_ != this).reverse // TODO Unreliable equality
    end findAllPrecedingSiblingElems

    def ownNavigationPathRelativeToRootElem: Navigation.NavigationPath = elemNavigationPathFromRoot

    def baseUriOption: Option[URI] =
      val parentBaseUriOption: Option[URI] = findParentElem.flatMap(_.baseUriOption).orElse(docUriOption)
      attrOption(XmlBaseEName)
        .map(URI.create)
        .map(u => parentBaseUriOption.map(_.resolve(u)).getOrElse(u))
        .orElse(parentBaseUriOption)
    end baseUriOption

    def baseUri: URI = baseUriOption.getOrElse(emptyUri)

    def docUri: URI = docUriOption.getOrElse(emptyUri)

    def rootElem: Elem = new Elem(docUriOption, underlyingRootElem, Navigation.NavigationPath.empty, underlyingRootElem)

  object Elem:

    def from[E <: CommonElemApi[E] & Nodes.Elem](otherElem: E): Elem =
      val docUriOption: Option[URI] = otherElem.docUriOption
      val underlyingRootElem: DefaultScopedNodes.Elem =
        DefaultScopedNodes.Elem.from(otherElem.rootElem)
      val elemNavigationPathFromRoot: NavigationPath = otherElem.ownNavigationPathRelativeToRootElem

      of(docUriOption, underlyingRootElem, elemNavigationPathFromRoot)
    end from

    def of(docUriOption: Option[URI], underlyingRootElem: DefaultScopedNodes.Elem, elemNavigationPathFromRoot: NavigationPath): Elem =
      new Elem(
        docUriOption,
        underlyingRootElem,
        elemNavigationPathFromRoot,
        underlyingRootElem.getDescendantElemOrSelf(elemNavigationPathFromRoot)
      )

    def ofRoot(docUriOption: Option[URI], underlyingRootElem: DefaultScopedNodes.Elem): Elem =
      of(docUriOption, underlyingRootElem, NavigationPath.empty)

  end Elem

  private val emptyUri: URI = URI.create("")

  private val XmlBaseEName: EName = EName.of(XmlNamespace, LocalName("base"))

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

  def scope(elem: E): Scope = elem.scope

  def qname(elem: E): QName = elem.qname

  def attrsByQName(elem: E): ListMap[QName, String] = elem.attrsByQName

  def textAsQName(elem: E): QName = elem.textAsQName

  def textAsResolvedQName(elem: E)(using enameProvider: ENameProvider): EName =
    elem.textAsResolvedQName(using enameProvider)

  def attrAsQNameOption(elem: E, attrName: EName): Option[QName] = elem.attrAsQNameOption(attrName)

  def attrAsQName(elem: E, attrName: EName): QName = elem.attrAsQName(attrName)

  def attrAsResolvedQNameOption(elem: E, attrName: EName)(using enameProvider: ENameProvider): Option[EName] =
    elem.attrAsResolvedQNameOption(attrName)(using enameProvider)

  def attrAsResolvedQName(elem: E, attrName: EName)(using enameProvider: ENameProvider): EName =
    elem.attrAsResolvedQName(attrName)(using enameProvider)

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

end DefaultCommonNodes
