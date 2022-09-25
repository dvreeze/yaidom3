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

/**
 * Element step factory type class, adding other "axes" to BaseElemStepFactory.
 *
 * @author
 *   Chris de Vreeze
 */
trait ElemStepFactory[E] extends BaseElemStepFactory[E]:

  // TODO Type-safe local names and namespaces

  // No more "Clark", "scoped" and "backing" elements. All axes must be supported.

  def parentElems(): ElemStep[E]

  def parentElems(pred: E => Boolean): ElemStep[E]

  def parentElems(localName: String): ElemStep[E]

  def parentElems(namespace: String, localName: String): ElemStep[E]

  def parentElems(maybeNamespace: Option[String], localName: String): ElemStep[E]

  def ancestorElems(): ElemStep[E]

  def ancestorElems(pred: E => Boolean): ElemStep[E]

  def ancestorElems(localName: String): ElemStep[E]

  def ancestorElems(namespace: String, localName: String): ElemStep[E]

  def ancestorElems(maybeNamespace: Option[String], localName: String): ElemStep[E]

  def ancestorElemsOrSelf(): ElemStep[E]

  def ancestorElemsOrSelf(pred: E => Boolean): ElemStep[E]

  def ancestorElemsOrSelf(localName: String): ElemStep[E]

  def ancestorElemsOrSelf(namespace: String, localName: String): ElemStep[E]

  def ancestorElemsOrSelf(maybeNamespace: Option[String], localName: String): ElemStep[E]

end ElemStepFactory
