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

package eu.cdevreeze.yaidom3.node.internal

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.queryapi.ScopedElemApi
import eu.cdevreeze.yaidom3.queryapi.ScopedElemQueryApi

/**
 * Delegating implementation of ScopedElemQueryApi, delegating to a ScopedElemApi implementation. This implementation trait is intentionally
 * completely invisible in the public API.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The ScopedElemApi implementation to which this trait delegates
 */
private[node] transparent trait DelegatingScopedElemQueryApi[E <: ScopedElemApi[E]]
    extends DelegatingClarkElemQueryApi[E],
      ScopedElemQueryApi[E]:

  def scope(elem: E): Scope = elem.scope

  def qname(elem: E): QName = elem.qname

  def attrsByQName(elem: E): ListMap[QName, String] = elem.attrsByQName

  def textAsQName(elem: E): QName = elem.textAsQName

  def textAsResolvedQName(elem: E)(using enameProvider: ENameProvider): EName =
    elem.textAsResolvedQName(using enameProvider)

  def attrAsQNameOption(elem: E, attrName: EName): Option[QName] = elem.attrAsQNameOption(attrName)

  def attrAsQName(elem: E, attrName: EName): QName = elem.attrAsQName(attrName)

  def attrAsResolvedQNameOption(elem: E, attrName: EName)(using enameProvider: ENameProvider): Option[EName] =
    elem.attrAsResolvedQNameOption(attrName)(using enameProvider)

  def attrAsResolvedQName(elem: E, attrName: EName)(using enameProvider: ENameProvider): EName =
    elem.attrAsResolvedQName(attrName)(using enameProvider)

end DelegatingScopedElemQueryApi
