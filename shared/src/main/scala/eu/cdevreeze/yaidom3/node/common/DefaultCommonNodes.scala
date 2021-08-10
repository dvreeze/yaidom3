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
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.Navigation.NavigationStep
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.node.internal.DelegatingCommonElemQueryApi
import eu.cdevreeze.yaidom3.node.internal.PartialClarkElem
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
object DefaultCommonNodes extends DelegatingCommonElemQueryApi[DefaultCommonNodes.Elem], CommonElemQueryApi[DefaultCommonNodes.Elem]:

  sealed trait Node extends Nodes.Node

  final case class Text(value: String, isCData: Boolean) extends Node, Nodes.Text

  final case class Comment(value: String) extends Node, Nodes.Comment

  final case class ProcessingInstruction(target: String, data: String) extends Node, Nodes.ProcessingInstruction

  final case class Elem private (
      docUriOption: Option[URI],
      underlyingRootElem: DefaultScopedNodes.Elem,
      elemNavigationPathFromRoot: Navigation.NavigationPath,
      underlyingElem: DefaultScopedNodes.Elem
  ) extends PartialClarkElem[Elem](
        underlyingElem.name,
        underlyingElem.attrs,
        Elem.findAllChildElems(docUriOption, underlyingRootElem, elemNavigationPathFromRoot, underlyingElem)
      ),
        Node,
        Nodes.Elem,
        CommonElemApi[Elem]:

    def children: Seq[Node] =
      var childElemIdx = 0

      underlyingElem.children.map {
        case che: DefaultScopedNodes.Elem =>
          val step = NavigationStep(childElemIdx)
          childElemIdx += 1
          Elem(docUriOption, underlyingRootElem, elemNavigationPathFromRoot.appended(step), che)
        case DefaultScopedNodes.Text(value, isCData) =>
          Text(value, isCData)
        case DefaultScopedNodes.Comment(value) =>
          Comment(value)
        case DefaultScopedNodes.ProcessingInstruction(target, data) =>
          ProcessingInstruction(target, data)
      }
    end children

    override def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[Elem] =
      val underlyingTargetElemOption: Option[DefaultScopedNodes.Elem] = underlyingElem.findDescendantElemOrSelf(navigationPath)
      val targetOwnNavigationPathOption = underlyingTargetElemOption.map(_ => elemNavigationPathFromRoot.appendedAll(navigationPath))

      targetOwnNavigationPathOption.map { path =>
        Elem(docUriOption, underlyingRootElem, path, underlyingTargetElemOption.get)
      }
    end findDescendantElemOrSelf

    def name: EName = underlyingElem.name

    def attrs: ListMap[EName, String] = underlyingElem.attrs

    def text: String = underlyingElem.text

    def normalizedText: String = underlyingElem.normalizedText

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
        val parentElem = Elem(docUriOption, underlyingRootElem, parentPath, underlyingParentElem)
        Some(parentElem).filter(p)
    end findParentElem

    def parentElemOption: Option[Elem] = findParentElem(_ => true)

    def filterAncestorElems(p: Elem => Boolean): Seq[Elem] =
      parentElemOption.toSeq.flatMap(_.filterAncestorElemsOrSelf(p))

    def findAllAncestorElems: Seq[Elem] = filterAncestorElems(_ => true)

    def findAncestorElem(p: Elem => Boolean): Option[Elem] =
      filterAncestorElems(p).headOption // TODO Improve performance!

    def filterAncestorElemsOrSelf(p: Elem => Boolean): Seq[Elem] =
      Seq(this).filter(p).appendedAll(parentElemOption.toSeq.flatMap(_.filterAncestorElemsOrSelf(p)))

    def findAllAncestorElemsOrSelf: Seq[Elem] = filterAncestorElemsOrSelf(_ => true)

    def findAncestorElemOrSelf(p: Elem => Boolean): Option[Elem] =
      filterAncestorElemsOrSelf(p).headOption // TODO Improve performance!

    def findAllPrecedingSiblingElems: Seq[Elem] =
      val parentElemOpt = parentElemOption

      if parentElemOpt.isEmpty then Seq.empty
      else parentElemOpt.get.findAllChildElems.takeWhile(_ != this).reverse // TODO Unreliable equality
    end findAllPrecedingSiblingElems

    def ownNavigationPathRelativeToRootElem: Navigation.NavigationPath = elemNavigationPathFromRoot

    def baseUriOption: Option[URI] =
      val parentBaseUriOption: Option[URI] = parentElemOption.flatMap(_.baseUriOption).orElse(docUriOption)
      this
        .attrOption(XmlBaseEName)
        .map(URI.create)
        .map(u => parentBaseUriOption.map(_.resolve(u)).getOrElse(u))
        .orElse(parentBaseUriOption)
    end baseUriOption

    def baseUri: URI = baseUriOption.getOrElse(emptyUri)

    def docUri: URI = docUriOption.getOrElse(emptyUri)

    def rootElem: Elem = Elem(docUriOption, underlyingRootElem, Navigation.NavigationPath.empty, underlyingRootElem)

  object Elem:

    def from[E <: CommonElemApi[E] & Nodes.Elem](otherElem: E)(using enameProvider: ENameProvider): Elem =
      val docUriOption: Option[URI] = otherElem.docUriOption
      val underlyingRootElem: DefaultScopedNodes.Elem =
        DefaultScopedNodes.Elem.from(otherElem.rootElem)(using enameProvider)
      val elemNavigationPathFromRoot: NavigationPath = otherElem.ownNavigationPathRelativeToRootElem

      of(docUriOption, underlyingRootElem, elemNavigationPathFromRoot)
    end from

    def of(docUriOption: Option[URI], underlyingRootElem: DefaultScopedNodes.Elem, elemNavigationPathFromRoot: NavigationPath): Elem =
      Elem(
        docUriOption,
        underlyingRootElem,
        elemNavigationPathFromRoot,
        underlyingRootElem.getDescendantElemOrSelf(elemNavigationPathFromRoot)
      )

    def ofRoot(docUriOption: Option[URI], underlyingRootElem: DefaultScopedNodes.Elem): Elem =
      of(docUriOption, underlyingRootElem, NavigationPath.empty)

    private def findAllChildElems(
        docUriOption: Option[URI],
        underlyingRootElem: DefaultScopedNodes.Elem,
        elemNavigationPathFromRoot: Navigation.NavigationPath,
        underlyingElem: DefaultScopedNodes.Elem
    ): Seq[Elem] =
      underlyingElem.findAllChildElems.zipWithIndex
        .map { (che, idx) =>
          Elem(docUriOption, underlyingRootElem, elemNavigationPathFromRoot.appended(Navigation.NavigationStep(idx)), che)
        }

  end Elem

  private val emptyUri: URI = URI.create("")

  private val XmlBaseEName: EName = EName.of(XmlNamespace, LocalName("base"))

end DefaultCommonNodes
