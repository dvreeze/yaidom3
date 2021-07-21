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

package eu.cdevreeze.yaidom3.queryapi

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope

/**
 * Element node query API, knowing about QNames and ENames (and therefore about Scopes resolving QNames as ENames), but not about their
 * context (such as ancestry, document URI etc.).
 *
 * @author
 *   Chris de Vreeze
 */
trait ScopedElemQueryApi[E] extends ClarkElemQueryApi[E]:

  def scope(elem: E): Scope

  def qname(elem: E): QName

  def attrsByQName(elem: E): ListMap[QName, String]

  def textAsQName(elem: E): QName

  def textAsResolvedQName(elem: E)(using enameProvider: ENameProvider): EName

  def attrAsQNameOption(elem: E, attrName: EName): Option[QName]

  def attrAsQName(elem: E, attrName: EName): QName

  def attrAsResolvedQNameOption(elem: E, attrName: EName)(using enameProvider: ENameProvider): Option[EName]

  def attrAsResolvedQName(elem: E, attrName: EName)(using enameProvider: ENameProvider): EName

end ScopedElemQueryApi
