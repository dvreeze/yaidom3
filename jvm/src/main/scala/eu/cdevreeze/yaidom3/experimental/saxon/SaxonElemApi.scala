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
import scala.jdk.OptionConverters.*
import scala.jdk.StreamConverters.*
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.experimental.core.EName
import eu.cdevreeze.yaidom3.experimental.core.QName
import eu.cdevreeze.yaidom3.experimental.core.Scope
import eu.cdevreeze.yaidom3.experimental.internal.StringUtil
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStep
import net.sf.saxon.s9api.XdmNode
import net.sf.saxon.s9api.streams.Predicates.*
import net.sf.saxon.s9api.streams.Steps.*

/**
 * Element API type class instance for Saxon.
 *
 * @author
 *   Chris de Vreeze
 */
object SaxonElemApi extends ElemApi[XdmNode]:

  type E = XdmNode

  def selectElems(elem: E, step: ElemStep[E]): Seq[E] = step(elem)

  def name(elem: E): EName = SaxonElemApi.extractEName(elem)

  def attrs(elem: E): ListMap[EName, String] =
    val stream = elem.select(attribute())
    stream.toScala(List).map(n => SaxonElemApi.extractEName(n) -> n.getStringValue).to(ListMap)

  def attrOption(elem: E, attrName: EName): Option[String] =
    val stream = elem.select(attribute(attrName.namespaceOption.getOrElse(""), attrName.localPart))
    stream.asOptionalNode.toScala.map(_.getStringValue)

  def attrOption(elem: E, attrNoNsName: String): Option[String] = attrOption(elem, EName.of(None, attrNoNsName))

  def attr(elem: E, attrName: EName): String =
    attrOption(elem, attrName).getOrElse(sys.error(s"Missing attribute '$attrName' in element '${name(elem)}"))

  def attr(elem: E, attrNoNsName: String) = attr(elem, EName.of(None, attrNoNsName))

  def text(elem: E): String =
    val stream = elem.select(child(isText))
    stream.toScala(List).map(_.getUnderlyingNode.getStringValue).mkString

  def normalizedText(elem: E): String = StringUtil.normalizeString(text(elem))

  def hasLocalName(elem: E, localName: String): Boolean =
    name(elem).localPart == localName

  def hasName(elem: E, name: EName): Boolean =
    val nm: EName = SaxonElemApi.extractEName(elem)
    nm == name

  def hasName(elem: E, namespaceOption: Option[String], localName: String): Boolean =
    val nm: EName = name(elem)
    nm.namespaceOption == namespaceOption && nm.localPart == localName

  def hasName(elem: E, namespace: String, localName: String): Boolean =
    val nm: EName = name(elem)
    nm.namespaceOption.contains(namespace) && nm.localPart == localName

  def scope(elem: E): Scope =
    val stream = elem.select(namespace())

    val result = stream.toScala(List).map { n =>
      // Not very transparent: prefix is "display name" and namespace URI is "string value"
      val prefix = n.getUnderlyingNode.getDisplayName
      val nsUri = n.getUnderlyingNode.getStringValue
      val prefixOption = if prefix.isEmpty then None else Some(prefix)
      prefixOption -> String(nsUri)
    }
    Scope.from(result.to(ListMap))
  end scope

  def qname(elem: E): QName = SaxonElemApi.extractQName(elem)

  def attrsByQName(elem: E): ListMap[QName, String] =
    val stream = elem.select(attribute())
    stream.toScala(List).map(n => SaxonElemApi.extractQName(n) -> n.getStringValue).to(ListMap)

  def textAsQName(elem: E): QName = QName.parse(text(elem).trim)

  def textAsResolvedQName(elem: E): EName =
    scope(elem)
      .resolveOption(textAsQName(elem))
      .getOrElse(sys.error(s"Could not resolve QName-valued element text ${textAsQName(elem)}, given scope [${scope(elem)}]"))

  def attrAsQNameOption(elem: E, attrName: EName): Option[QName] = attrOption(elem, attrName).map(v => QName.parse(v.trim))

  def attrAsQName(elem: E, attrName: EName): QName =
    attrAsQNameOption(elem, attrName).getOrElse(sys.error(s"Missing QName-valued attribute $attrName"))

  def attrAsResolvedQNameOption(elem: E, attrName: EName): Option[EName] =
    attrAsQNameOption(elem, attrName).map { qn =>
      scope(elem)
        .resolveOption(qn)
        .getOrElse(sys.error(s"Could not resolve QName-valued attribute value $qn, given scope [${scope(elem)}]"))
    }

  def attrAsResolvedQName(elem: E, attrName: EName): EName =
    attrAsResolvedQNameOption(elem, attrName)
      .getOrElse(sys.error(s"Missing QName-valued attribute $attrName"))

  def extractEName(xdmNode: XdmNode): EName =
    val saxonQName = xdmNode.getNodeName.ensuring(_ != null, s"Node '$xdmNode' has no node name as Saxon QName")
    val ns: String = saxonQName.getNamespaceURI
    val nsOption: Option[String] = if ns.isEmpty then None else Some(ns)
    EName.of(nsOption, saxonQName.getLocalName)

  def extractQName(xdmNode: XdmNode): QName =
    val saxonQName = xdmNode.getNodeName.ensuring(_ != null, s"Node '$xdmNode' has no node name as Saxon QName")
    val pref: String = saxonQName.getPrefix
    val prefOption: Option[String] = if pref.isEmpty then None else Some(pref)
    QName.of(prefOption, saxonQName.getLocalName)

end SaxonElemApi
