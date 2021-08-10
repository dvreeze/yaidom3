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

package eu.cdevreeze.yaidom3.creationapi

import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope

import scala.collection.immutable.ListMap

/**
 * API for creation of elements. Note that this API does not conveniently hide Scopes, because they are seen as essential to the API.
 *
 * @author
 *   Chris de Vreeze
 * @tparam E
 *   The element node type
 */
trait ElemCreationApi[E]:

  /**
   * The node type, which is a super-type of the element type
   */
  type N >: E

  def emptyElem(qname: QName, scope: Scope): E

  def emptyElem(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope): E

  def textElem(qname: QName, scope: Scope, text: String): E

  def textElem(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope, text: String): E

  def elem(qname: QName, scope: Scope, children: Seq[N]): E

  def elem(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope, children: Seq[N]): E

end ElemCreationApi
