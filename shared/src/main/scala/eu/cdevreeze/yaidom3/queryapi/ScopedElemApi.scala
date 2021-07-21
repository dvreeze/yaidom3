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
 * OO API for ScopedElemQueryApi, implemented by element implementations.
 *
 * @author
 *   Chris de Vreeze
 *
 * @tparam E
 *   The "self type", which typically but not necessarily is a sub-type of the trait (F-bounded polymorphism)
 */
trait ScopedElemApi[E] extends ClarkElemApi[E]:

  def scope: Scope

  def qname: QName

  def attrsByQName: ListMap[QName, String]

  def textAsQName: QName

  def textAsResolvedQName(using enameProvider: ENameProvider): EName

  def attrAsQNameOption(attrName: EName): Option[QName]

  def attrAsQName(attrName: EName): QName

  def attrAsResolvedQNameOption(attrName: EName)(using enameProvider: ENameProvider): Option[EName]

  def attrAsResolvedQName(attrName: EName)(using enameProvider: ENameProvider): EName

end ScopedElemApi
