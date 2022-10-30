/*
 * Copyright 2022-2022 Chris de Vreeze
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

package eu.cdevreeze.yaidom3.experimental.saxon

import scala.collection.immutable.ListMap
import scala.jdk.StreamConverters.*

import eu.cdevreeze.yaidom3.experimental.core.EName
import eu.cdevreeze.yaidom3.experimental.core.QName
import eu.cdevreeze.yaidom3.experimental.core.Scope
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStep
import eu.cdevreeze.yaidom3.experimental.queryapi.Nodes
import net.sf.saxon.s9api.XdmNode
import net.sf.saxon.s9api.XdmNodeKind
import net.sf.saxon.s9api.streams.Steps

/**
 * Saxon nodes.
 *
 * @author
 *   Chris de Vreeze
 */
enum SaxonNode(val underlying: XdmNode) extends Nodes.Node:

  case Text(override val underlying: XdmNode) extends SaxonNode(underlying.ensuring(SaxonNode.isText)), Nodes.Text
    def textString: String = underlying.getUnderlyingNode.getStringValue

  case Comment(override val underlying: XdmNode) extends SaxonNode(underlying.ensuring(SaxonNode.isComment)), Nodes.Comment
    def commentString: String = underlying.getUnderlyingNode.getStringValue

  case ProcessingInstruction(override val underlying: XdmNode) extends SaxonNode(underlying.ensuring(SaxonNode.isPI)), Nodes.ProcessingInstruction
    def target: String = underlying.getUnderlyingNode.getDisplayName
    def data: String = underlying.getUnderlyingNode.getStringValue

  case Elem(override val underlying: XdmNode) extends SaxonNode(underlying.ensuring(SaxonNode.isElem)), Nodes.Elem, ElemApi[SaxonNode.Elem, XdmNode]

    def children: Seq[SaxonNode] =
      underlying.select(Steps.child()).toScala(Vector).flatMap(SaxonNode.maybeFrom)

    def unwrap: XdmNode = underlying

    def selectElems(step: ElemStep[XdmNode]): Seq[SaxonNode.Elem] =
      selectUnderlyingElems(step).map(SaxonNode.Elem(_))

    def selectUnderlyingElems(step: ElemStep[XdmNode]): Seq[XdmNode] =
      SaxonElemQueryApi.selectElems(underlying, step)

    def name: EName = SaxonElemQueryApi.name(underlying)

    def attrs: ListMap[EName, String] = SaxonElemQueryApi.attrs(underlying)

    def attrOption(attrName: EName): Option[String] = SaxonElemQueryApi.attrOption(underlying, attrName)

    def attrOption(attrNoNsName: String): Option[String] = SaxonElemQueryApi.attrOption(underlying, attrNoNsName)

    def attr(attrName: EName): String = SaxonElemQueryApi.attr(underlying, attrName)

    def attr(attrNoNsName: String): String = SaxonElemQueryApi.attr(underlying, attrNoNsName)

    def text: String = SaxonElemQueryApi.text(underlying)

    def normalizedText: String = SaxonElemQueryApi.normalizedText(underlying)

    def hasLocalName(localName: String): Boolean = SaxonElemQueryApi.hasLocalName(underlying, localName)

    def hasName(name: EName): Boolean = SaxonElemQueryApi.hasName(underlying, name)

    def hasName(namespaceOption: Option[String], localName: String): Boolean =
      SaxonElemQueryApi.hasName(underlying, namespaceOption, localName)

    def hasName(namespace: String, localName: String): Boolean = SaxonElemQueryApi.hasName(underlying, namespace, localName)

    def scope: Scope = SaxonElemQueryApi.scope(underlying)

    def qname: QName = SaxonElemQueryApi.qname(underlying)

    def attrsByQName: ListMap[QName, String] = SaxonElemQueryApi.attrsByQName(underlying)

    def textAsQName: QName = SaxonElemQueryApi.textAsQName(underlying)

    def textAsResolvedQName: EName = SaxonElemQueryApi.textAsResolvedQName(underlying)

    def attrAsQNameOption(attrName: EName): Option[QName] = SaxonElemQueryApi.attrAsQNameOption(underlying, attrName)

    def attrAsQName(attrName: EName): QName = SaxonElemQueryApi.attrAsQName(underlying, attrName)

    def attrAsResolvedQNameOption(attrName: EName): Option[EName] =
      SaxonElemQueryApi.attrAsResolvedQNameOption(underlying, attrName)

    def attrAsResolvedQName(attrName: EName): EName = SaxonElemQueryApi.attrAsResolvedQName(underlying, attrName)

object SaxonNode:

  def maybeFrom(underlying: XdmNode): Option[SaxonNode] =
    underlying.getNodeKind match
      case XdmNodeKind.TEXT => Some(SaxonNode.Text(underlying))
      case XdmNodeKind.COMMENT => Some(SaxonNode.Comment(underlying))
      case XdmNodeKind.PROCESSING_INSTRUCTION => Some(SaxonNode.ProcessingInstruction(underlying))
      case XdmNodeKind.ELEMENT => Some(SaxonNode.Elem(underlying))
      case _ => None

  def isText(xdmNode: XdmNode): Boolean = xdmNode.getNodeKind == XdmNodeKind.TEXT

  def isComment(xdmNode: XdmNode): Boolean = xdmNode.getNodeKind == XdmNodeKind.COMMENT

  def isPI(xdmNode: XdmNode): Boolean = xdmNode.getNodeKind == XdmNodeKind.PROCESSING_INSTRUCTION

  def isElem(xdmNode: XdmNode): Boolean = xdmNode.getNodeKind == XdmNodeKind.ELEMENT

end SaxonNode
