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

package eu.cdevreeze.yaidom3.experimental.builtin.simple

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.experimental.core.EName
import eu.cdevreeze.yaidom3.experimental.core.QName
import eu.cdevreeze.yaidom3.experimental.core.Scope
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemQueryApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStep

/**
 * ElemQueryApi implementation for SimpleNode.Elem elements.
 *
 * @author
 *   Chris de Vreeze
 */
object SimpleElemQueryApi extends ElemQueryApi[SimpleNode.Elem]:

  type E = SimpleNode.Elem

  def selectElems(elem: E, step: ElemStep[E]): Seq[E] = elem.selectElems(step)

  def name(elem: E): EName = elem.name

  def attrs(elem: E): ListMap[EName, String] = elem.attrs

  def attrOption(elem: E, attrName: EName): Option[String] = elem.attrOption(attrName)

  def attrOption(elem: E, attrNoNsName: String): Option[String] = elem.attrOption(attrNoNsName)

  def attr(elem: E, attrName: EName): String = elem.attr(attrName)

  def attr(elem: E, attrNoNsName: String): String = elem.attr(attrNoNsName)

  def text(elem: E): String = elem.text

  def normalizedText(elem: E): String = elem.normalizedText

  def hasLocalName(elem: E, localName: String): Boolean = elem.hasLocalName(localName)

  def hasName(elem: E, name: EName): Boolean = elem.hasName(name)

  def hasName(elem: E, namespaceOption: Option[String], localName: String): Boolean =
    elem.hasName(namespaceOption, localName)

  def hasName(elem: E, namespace: String, localName: String): Boolean = elem.hasName(namespace, localName)

  def scope(elem: E): Scope = elem.scope

  def qname(elem: E): QName = elem.qname

  def attrsByQName(elem: E): ListMap[QName, String] = elem.attrsByQName

  def textAsQName(elem: E): QName = elem.textAsQName

  def textAsResolvedQName(elem: E): EName = elem.textAsResolvedQName

  def attrAsQNameOption(elem: E, attrName: EName): Option[QName] = elem.attrAsQNameOption(attrName)

  def attrAsQName(elem: E, attrName: EName): QName = elem.attrAsQName(attrName)

  def attrAsResolvedQNameOption(elem: E, attrName: EName): Option[EName] = elem.attrAsResolvedQNameOption(attrName)

  def attrAsResolvedQName(elem: E, attrName: EName): EName = elem.attrAsResolvedQName(attrName)

end SimpleElemQueryApi
