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

import eu.cdevreeze.yaidom3.experimental.core.EName
import eu.cdevreeze.yaidom3.experimental.core.QName
import eu.cdevreeze.yaidom3.experimental.core.Scope
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStep
import net.sf.saxon.s9api.XdmNode

/**
 * Element API for Saxon.
 *
 * @author
 *   Chris de Vreeze
 */
final class SaxonElem(val underlying: XdmNode) extends ElemApi[SaxonElem, XdmNode]:

  type E = SaxonElem

  def unwrap: XdmNode = underlying

  def selectElems(step: ElemStep[XdmNode]): Seq[E] =
    selectUnwrappedElems(step).map(SaxonElem(_))

  def selectUnwrappedElems(step: ElemStep[XdmNode]): Seq[XdmNode] =
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

end SaxonElem
