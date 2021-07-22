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

package eu.cdevreeze.yaidom3.node.saxon

import java.net.URI

import scala.collection.immutable.ListMap
import scala.jdk.OptionConverters._
import scala.jdk.StreamConverters._
import scala.util.chaining._

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.Namespaces.LocalName
import eu.cdevreeze.yaidom3.core.Namespaces.Namespace
import eu.cdevreeze.yaidom3.core.Namespaces.Prefix
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.Navigation.NavigationStep
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.internal.StringUtil
import eu.cdevreeze.yaidom3.node.internal.CommonWrapperElem
import eu.cdevreeze.yaidom3.queryapi.CommonElemApi
import eu.cdevreeze.yaidom3.queryapi.CommonElemQueryApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import net.sf.saxon.s9api.XdmNode
import net.sf.saxon.s9api.XdmNodeKind
import net.sf.saxon.s9api.streams.Predicates._
import net.sf.saxon.s9api.streams.Step
import net.sf.saxon.s9api.streams.Steps._

/**
 * Saxon wrapper elements. See the CommonElemQueryApi and CommonElemApi for the API offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
object SaxonNodes extends CommonElemQueryApi[XdmNode]:

  sealed trait Node extends Nodes.Node:
    def xdmNode: XdmNode

  object Node:

    def extractEName(xdmNode: XdmNode): EName =
      val saxonQName = xdmNode.getNodeName.ensuring(_ != null, s"Node '$xdmNode' has no node name as Saxon QName")
      val ns: String = saxonQName.getNamespaceURI
      val nsOption: Option[String] = if ns.isEmpty then None else Some(ns)
      EName.of(nsOption.map(Namespace.apply), LocalName(saxonQName.getLocalName))

    def extractQName(xdmNode: XdmNode): QName =
      val saxonQName = xdmNode.getNodeName.ensuring(_ != null, s"Node '$xdmNode' has no node name as Saxon QName")
      val pref: String = saxonQName.getPrefix
      val prefOption: Option[String] = if pref.isEmpty then None else Some(pref)
      QName.of(prefOption.map(Prefix.apply), LocalName(saxonQName.getLocalName))

    def opt(xdmNode: XdmNode): Option[Node] =
      xdmNode.getNodeKind match
        case XdmNodeKind.TEXT                   => Some(Text(xdmNode))
        case XdmNodeKind.COMMENT                => Some(Comment(xdmNode))
        case XdmNodeKind.PROCESSING_INSTRUCTION => Some(ProcessingInstruction(xdmNode))
        case XdmNodeKind.ELEMENT                => Some(Elem(xdmNode))
        case _                                  => None
    end opt

  end Node

  final case class Text(xdmNode: XdmNode) extends Node, Nodes.Text:
    require(xdmNode.getNodeKind == XdmNodeKind.TEXT, s"Not a text node: $xdmNode")

    def value: String = xdmNode.getUnderlyingNode.getStringValue
  end Text

  final case class Comment(xdmNode: XdmNode) extends Node, Nodes.Comment:
    require(xdmNode.getNodeKind == XdmNodeKind.COMMENT, s"Not a comment node: $xdmNode")

    def value: String = xdmNode.getUnderlyingNode.getStringValue
  end Comment

  final case class ProcessingInstruction(xdmNode: XdmNode) extends Node, Nodes.ProcessingInstruction:
    require(xdmNode.getNodeKind == XdmNodeKind.PROCESSING_INSTRUCTION, s"Not a processing instruction node: $xdmNode")

    def target: String = xdmNode.getUnderlyingNode.getDisplayName
    def data: String = xdmNode.getUnderlyingNode.getStringValue
  end ProcessingInstruction

  final class Elem(val xdmNode: XdmNode)
      extends CommonWrapperElem[XdmNode, Elem](xdmNode)(using SaxonNodes),
        Node,
        Nodes.Elem,
        CommonElemApi[Elem]:

    def wrap(underlying: XdmNode): Elem = Elem(underlying)

    def children: Seq[Node] = SaxonNodes.children(xdmNode).flatMap(Node.opt)

    override def equals(other: Any): Boolean = other match
      case otherElem: Elem => xdmNode.getUnderlyingNode == otherElem.xdmNode.getUnderlyingNode
      case _               => false

    override def hashCode: Int = xdmNode.getUnderlyingNode.hashCode

  end Elem

  private val emptyUri: URI = URI.create("")

  private type E = XdmNode

  def children(elem: E): Seq[XdmNode] = elem.select(child()).toScala(Vector).filter(ch => Node.opt(ch).nonEmpty)

  def filterChildElems(elem: E, p: E => Boolean): Seq[E] = filterElems(elem, child(), p)

  def findAllChildElems(elem: E): Seq[E] = filterElems(elem, child(), _ => true)

  def findChildElem(elem: E, p: E => Boolean): Option[E] = findElem(elem, child(), p)

  def filterDescendantElems(elem: E, p: E => Boolean): Seq[E] = filterElems(elem, descendant(), p)

  def findAllDescendantElems(elem: E): Seq[E] = filterElems(elem, descendant(), _ => true)

  def findDescendantElem(elem: E, p: E => Boolean): Option[E] = findElem(elem, descendant(), p)

  def filterDescendantElemsOrSelf(elem: E, p: E => Boolean): Seq[E] = filterElems(elem, descendantOrSelf(), p)

  def findAllDescendantElemsOrSelf(elem: E): Seq[E] = filterElems(elem, descendantOrSelf(), _ => true)

  def findDescendantElemOrSelf(elem: E, p: E => Boolean): Option[E] = findElem(elem, descendantOrSelf(), p)

  def findTopmostElems(elem: E, p: E => Boolean): Seq[E] =
    val childElemStream = elem.select(child().where(n => isElement.test(n)))
    childElemStream.flatMap(e => findTopmostElemsOrSelfAsStream(e, p)).toScala(Vector)

  def findTopmostElemsOrSelf(elem: E, p: E => Boolean): Seq[E] = findTopmostElemsOrSelfAsStream(elem, p).toScala(Vector)

  def findDescendantElemOrSelf(elem: E, navigationPath: NavigationPath): Option[E] =
    if navigationPath.isEmpty then Some(elem)
    else
      val step = navigationPath.head
      val childElems: Seq[E] = findAllChildElems(elem)

      if step.toInt >= 0 && step.toInt < childElems.size then
        // Recursive call
        Option(childElems(step.toInt)).flatMap(che => findDescendantElemOrSelf(che, navigationPath.tail))
      else None
  end findDescendantElemOrSelf

  def getDescendantElemOrSelf(elem: E, navigationPath: NavigationPath): E =
    findDescendantElemOrSelf(elem, navigationPath)
      .getOrElse(sys.error(s"Missing element at navigation path $navigationPath"))

  def name(elem: E): EName = Node.extractEName(elem)

  def attrs(elem: E): ListMap[EName, String] =
    val stream = elem.select(attribute())
    stream.toScala(List).map(n => Node.extractEName(n) -> n.getStringValue).to(ListMap)

  def attrOption(elem: E, attrName: EName): Option[String] =
    val stream = elem.select(attribute(attrName.namespaceOption.map(_.toString).getOrElse(""), attrName.localPart.toString))
    stream.asOptionalNode.toScala.map(_.getStringValue)

  def text(elem: E): String =
    val stream = elem.select(child(isText))
    stream.toScala(List).map(_.getUnderlyingNode.getStringValue).mkString

  def normalizedText(elem: E): String = StringUtil.normalizeString(text(elem))

  def hasLocalName(elem: E, localName: String): Boolean =
    name(elem).localPart.localNameAsString == localName

  def hasName(elem: E, namespaceOption: Option[String], localName: String): Boolean =
    val nm: EName = name(elem)
    nm.namespaceOption.map(_.namespaceAsString) == namespaceOption && nm.localPart.localNameAsString == localName

  def hasName(elem: E, namespace: String, localName: String): Boolean =
    val nm: EName = name(elem)
    nm.namespaceOption.map(_.namespaceAsString).contains(namespace) && nm.localPart.localNameAsString == localName

  def hasName(elem: E, localName: String): Boolean =
    val nm: EName = name(elem)
    nm.namespaceOption.isEmpty && nm.localPart.localNameAsString == localName

  def scope(elem: E): Scope =
    val stream = elem.select(namespace())

    val result = stream.toScala(List).map { n =>
      // Not very transparent: prefix is "display name" and namespace URI is "string value"
      val prefix = n.getUnderlyingNode.getDisplayName
      val nsUri = n.getUnderlyingNode.getStringValue
      val prefixOption = if prefix.isEmpty then None else Some(Prefix(prefix))
      prefixOption -> Namespace(nsUri)
    }
    Scope.from(result.to(ListMap))
  end scope

  def qname(elem: E): QName = Node.extractQName(elem)

  def attrsByQName(elem: E): ListMap[QName, String] =
    val stream = elem.select(attribute())
    stream.toScala(List).map(n => Node.extractQName(n) -> n.getStringValue).to(ListMap)

  def textAsQName(elem: E): QName = QName.parse(text(elem).trim)

  def textAsResolvedQName(elem: E)(using enameProvider: ENameProvider): EName =
    scope(elem)
      .resolveOption(textAsQName(elem))(using enameProvider)
      .getOrElse(sys.error(s"Could not resolve QName-valued element text ${textAsQName(elem)}, given scope [${scope(elem)}]"))

  def attrAsQNameOption(elem: E, attrName: EName): Option[QName] = attrOption(elem, attrName).map(v => QName.parse(v.trim))

  def attrAsQName(elem: E, attrName: EName): QName =
    attrAsQNameOption(elem, attrName).getOrElse(sys.error(s"Missing QName-valued attribute $attrName"))

  def attrAsResolvedQNameOption(elem: E, attrName: EName)(using enameProvider: ENameProvider): Option[EName] =
    attrAsQNameOption(elem, attrName).map { qn =>
      scope(elem)
        .resolveOption(qn)(using enameProvider)
        .getOrElse(sys.error(s"Could not resolve QName-valued attribute value $qn, given scope [${scope(elem)}]"))
    }

  def attrAsResolvedQName(elem: E, attrName: EName)(using enameProvider: ENameProvider): EName =
    attrAsResolvedQNameOption(elem, attrName)(using enameProvider)
      .getOrElse(sys.error(s"Missing QName-valued attribute $attrName"))

  def findParentElem(elem: E, p: E => Boolean): Option[E] = findElem(elem, parent(), p)

  def findParentElem(elem: E): Option[E] = findElem(elem, parent(), _ => true)

  def filterAncestorElems(elem: E, p: E => Boolean): Seq[E] = filterElems(elem, ancestor(), p)

  def findAllAncestorElems(elem: E): Seq[E] = filterElems(elem, ancestor(), _ => true)

  def findAncestorElem(elem: E, p: E => Boolean): Option[E] = findElem(elem, ancestor(), p)

  def filterAncestorElemsOrSelf(elem: E, p: E => Boolean): Seq[E] = filterElems(elem, ancestorOrSelf(), p)

  def findAllAncestorElemsOrSelf(elem: E): Seq[E] = filterElems(elem, ancestorOrSelf(), _ => true)

  def findAncestorElemOrSelf(elem: E, p: E => Boolean): Option[E] = findElem(elem, ancestorOrSelf(), p)

  def findAllPrecedingSiblingElems(elem: E): Seq[E] = filterElems(elem, precedingSibling(), _ => true)

  def ownNavigationPathRelativeToRootElem(elem: E): NavigationPath =
    def ownNavigationPath(elem: E): NavigationPath =
      findParentElem(elem)
        .map { pe =>
          // Recursive call
          ownNavigationPath(pe).appended(NavigationStep(findAllPrecedingSiblingElems(elem).size))
        }
        .getOrElse(NavigationPath.empty)
    end ownNavigationPath

    ownNavigationPath(elem)
  end ownNavigationPathRelativeToRootElem

  def baseUriOption(elem: E): Option[URI] = Option(elem.getUnderlyingNode.getBaseURI).map(u => URI.create(u))

  def baseUri(elem: E): URI = baseUriOption(elem).getOrElse(emptyUri)

  def docUriOption(elem: E): Option[URI] = Option(elem.getUnderlyingNode.getSystemId).map(u => URI.create(u))

  def docUri(elem: E): URI = docUriOption(elem).getOrElse(emptyUri)

  def rootElem(elem: E): E = findAllAncestorElemsOrSelf(elem).last

  private def filterElems(elem: E, step: Step[XdmNode], p: E => Boolean): Seq[E] =
    val stream = elem.select(step.where(n => isElement.test(n) && p(n)))
    stream.toScala(Vector)

  private def findElem(elem: E, step: Step[XdmNode], p: E => Boolean): Option[E] =
    val stream = elem.select(step.where(n => isElement.test(n) && p(n)))
    stream.findFirst.toScala

  private def findTopmostElemsOrSelfAsStream(elem: E, p: E => Boolean): java.util.stream.Stream[E] =
    if p(elem) then java.util.stream.Stream.of(elem)
    else
      val childElemStream = elem.select(child().where(n => isElement.test(n)))
      // Recursive calls
      childElemStream.flatMap(che => findTopmostElemsOrSelfAsStream(che, p))
  end findTopmostElemsOrSelfAsStream

end SaxonNodes
