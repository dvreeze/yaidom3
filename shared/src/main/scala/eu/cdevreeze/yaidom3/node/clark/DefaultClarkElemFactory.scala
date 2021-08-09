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

import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.creationapi.ElemCreationApi

/**
 * API for creation of default Clark elements.
 *
 * @author
 *   Chris de Vreeze
 */
object DefaultClarkElemFactory extends ElemCreationApi[DefaultClarkNodes.Elem]:

  type N = DefaultClarkNodes.Node

  def emptyElem(qname: QName, scope: Scope): DefaultClarkNodes.Elem =
    emptyElem(qname, ListMap.empty, scope)

  def emptyElem(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope): DefaultClarkNodes.Elem =
    elem(qname, attrsByQName, scope, Seq.empty)

  def textElem(qname: QName, scope: Scope, text: String): DefaultClarkNodes.Elem =
    textElem(qname, ListMap.empty, scope, text)

  def textElem(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope, text: String): DefaultClarkNodes.Elem =
    elem(qname, attrsByQName, scope, Seq(DefaultClarkNodes.Text(text)))

  def elem(qname: QName, scope: Scope, children: Seq[DefaultClarkNodes.Node]): DefaultClarkNodes.Elem =
    elem(qname, ListMap.empty, scope, children)

  def elem(
      qname: QName,
      attrsByQName: ListMap[QName, String],
      scope: Scope,
      children: Seq[DefaultClarkNodes.Node]
  ): DefaultClarkNodes.Elem =
    val attrScope: Scope = scope.withoutDefaultNamespace
    DefaultClarkNodes.Elem(
      scope.resolve(qname),
      attrsByQName.toSeq.map((qn, value) => attrScope.resolve(qn) -> value).to(ListMap),
      children
    )

end DefaultClarkElemFactory
