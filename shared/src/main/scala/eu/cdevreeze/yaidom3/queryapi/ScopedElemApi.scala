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
 * Element node OO query API, knowing about QNames and ENames (and therefore about Scopes resolving QNames as ENames), but not about their
 * context (such as ancestry, document URI etc.). This API is implemented by specific element implementations.
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

  /**
   * Like method `text`, but interpreting the result as QName (or throwing an exception).
   */
  def textAsQName: QName

  /**
   * Like method `text`, but interpreting the result as EName (or throwing an exception).
   */
  def textAsResolvedQName(using enameProvider: ENameProvider): EName

  /**
   * Like method `attrOption`, but interpreting the optional result as QName (or throwing an exception).
   */
  def attrAsQNameOption(attrName: EName): Option[QName]

  /**
   * Returns `attrAsQNameOption(attrName).get`.
   */
  def attrAsQName(attrName: EName): QName

  /**
   * Like method `attrOption`, but interpreting the optional result as EName (or throwing an exception).
   */
  def attrAsResolvedQNameOption(attrName: EName)(using enameProvider: ENameProvider): Option[EName]

  /**
   * Returns `attrAsResolvedQNameOption(attrName).get`.
   */
  def attrAsResolvedQName(attrName: EName)(using enameProvider: ENameProvider): EName

end ScopedElemApi
