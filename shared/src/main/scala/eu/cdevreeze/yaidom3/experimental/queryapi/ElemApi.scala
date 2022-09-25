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

package eu.cdevreeze.yaidom3.experimental.queryapi

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.experimental.core.EName
import eu.cdevreeze.yaidom3.experimental.core.QName
import eu.cdevreeze.yaidom3.experimental.core.Scope

/**
 * Element API type class.
 *
 * @author
 *   Chris de Vreeze
 */
trait ElemApi[E]:

  def selectElems(elem: E, step: ElemStep[E]): Seq[E]

  def name(elem: E): EName

  def attrs(elem: E): ListMap[EName, String]

  def attrOption(elem: E, attrName: EName): Option[String]

  def attrOption(elem: E, attrNoNsName: String): Option[String]

  def attr(elem: E, attrName: EName): String

  def attr(elem: E, attrNoNsName: String): String

  def text(elem: E): String

  def normalizedText(elem: E): String

  def hasLocalName(elem: E, localName: String): Boolean

  def hasName(elem: E, name: EName): Boolean

  def hasName(elem: E, namespaceOption: Option[String], localName: String): Boolean

  def hasName(elem: E, namespace: String, localName: String): Boolean

  def scope(elem: E): Scope

  def qname(elem: E): QName

  def attrsByQName(elem: E): ListMap[QName, String]

  def textAsQName(elem: E): QName

  def textAsResolvedQName(elem: E): EName

  def attrAsQNameOption(elem: E, attrName: EName): Option[QName]

  def attrAsQName(elem: E, attrName: EName): QName

  def attrAsResolvedQNameOption(elem: E, attrName: EName): Option[EName]

  def attrAsResolvedQName(elem: E, attrName: EName): EName

end ElemApi
