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

package eu.cdevreeze.yaidom3.node.scoped

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.creationapi.ElemCreationApi

import scala.util.chaining.*

/**
 * API for creation of default scoped elements.
 *
 * @author
 *   Chris de Vreeze
 */
object DefaultScopedElemFactory extends ElemCreationApi[DefaultScopedNodes.Elem]:

  type N = DefaultScopedNodes.Node

  def emptyElem(qname: QName, scope: Scope): DefaultScopedNodes.Elem =
    emptyElem(qname, ListMap.empty, scope)

  def emptyElem(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope): DefaultScopedNodes.Elem =
    elem(qname, attrsByQName, scope, Seq.empty)

  def textElem(qname: QName, scope: Scope, text: String): DefaultScopedNodes.Elem =
    textElem(qname, ListMap.empty, scope, text)

  def textElem(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope, text: String): DefaultScopedNodes.Elem =
    elem(qname, attrsByQName, scope, Seq(DefaultScopedNodes.Text(text, isCData = false)))

  def elem(qname: QName, scope: Scope, children: Seq[DefaultScopedNodes.Node]): DefaultScopedNodes.Elem =
    elem(qname, ListMap.empty, scope, children)

  /**
   * Creates an element from the given QName, attributes, scope and child nodes. This method then calls method notUndeclaringPrefixes,
   * passing the scope returned by method commonPrefixedScopeOfDescendantsOrSelf. This may be an expensive method if the result contains
   * many descendant-or-self elements.
   *
   * All other methods in this API ultimately delegate their implementation to this method.
   */
  def elem(
      qname: QName,
      attrsByQName: ListMap[QName, String],
      scope: Scope,
      children: Seq[DefaultScopedNodes.Node]
  ): DefaultScopedNodes.Elem =
    DefaultScopedNodes.Elem
      .apply(qname, attrsByQName, scope, children)(using ENameProvider.Trivial.TrivialENameProvider)
      .pipe(e => e.notUndeclaringPrefixes(e.commonPrefixedScopeOfDescendantsOrSelf))

end DefaultScopedElemFactory
