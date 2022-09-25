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
 * Base element step factory type class. Only "downward axes" are introduced in this base element step factory API.
 *
 * @author
 *   Chris de Vreeze
 */
trait BaseElemStepFactory[E]:

  // TODO Type-safe local names and namespaces

  // No more "Clark", "scoped" and "backing" elements. All axes must be supported.

  def childElems(): ElemStep[E]

  def childElems(pred: E => Boolean): ElemStep[E]

  def childElems(localName: String): ElemStep[E]

  def childElems(namespace: String, localName: String): ElemStep[E]

  def childElems(maybeNamespace: Option[String], localName: String): ElemStep[E]

  def descendantElems(): ElemStep[E]

  def descendantElems(pred: E => Boolean): ElemStep[E]

  def descendantElems(localName: String): ElemStep[E]

  def descendantElems(namespace: String, localName: String): ElemStep[E]

  def descendantElems(maybeNamespace: Option[String], localName: String): ElemStep[E]

  def descendantElemsOrSelf(): ElemStep[E]

  def descendantElemsOrSelf(pred: E => Boolean): ElemStep[E]

  def descendantElemsOrSelf(localName: String): ElemStep[E]

  def descendantElemsOrSelf(namespace: String, localName: String): ElemStep[E]

  def descendantElemsOrSelf(maybeNamespace: Option[String], localName: String): ElemStep[E]

end BaseElemStepFactory
