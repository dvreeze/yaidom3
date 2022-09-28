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
 * Element query API.
 *
 * @author
 *   Chris de Vreeze
 */
trait ElemApi[E, U]:

  def underlying: U

  /**
   * Alias for underlying
   */
  def unwrap: U

  def selectElems(step: ElemStep[U]): Seq[E]

  def name: EName

  def attrs: ListMap[EName, String]

  def attrOption(attrName: EName): Option[String]

  def attrOption(attrNoNsName: String): Option[String]

  def attr(attrName: EName): String

  def attr(attrNoNsName: String): String

  def text: String

  def normalizedText: String

  def hasLocalName(localName: String): Boolean

  def hasName(name: EName): Boolean

  def hasName(namespaceOption: Option[String], localName: String): Boolean

  def hasName(namespace: String, localName: String): Boolean

  def scope: Scope

  def qname: QName

  def attrsByQName: ListMap[QName, String]

  def textAsQName: QName

  def textAsResolvedQName: EName

  def attrAsQNameOption(attrName: EName): Option[QName]

  def attrAsQName(attrName: EName): QName

  def attrAsResolvedQNameOption(attrName: EName): Option[EName]

  def attrAsResolvedQName(attrName: EName): EName

end ElemApi
