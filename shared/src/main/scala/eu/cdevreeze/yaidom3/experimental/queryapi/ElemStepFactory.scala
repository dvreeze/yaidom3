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
 * Element API.
 *
 * Inspired by the Saxon stream API, but only for elements, and using Scala collections, and clearly distinguishing
 * between individual elements and collections of elements (unlike XPath).
 *
 * @author
 *   Chris de Vreeze
 */
trait ElemStepFactory[E]:

  // TODO Type-safe local names and namespaces

  def childElems(): ElemStep[E]

  def childElems(localName: String): ElemStep[E]

  def childElems(namespace: String, localName: String): ElemStep[E]

  def childElems(maybeNamespace: Option[String], localName: String): ElemStep[E]

  def descendantElems(): ElemStep[E]

  def descendantElems(localName: String): ElemStep[E]

  def descendantElems(namespace: String, localName: String): ElemStep[E]

  def descendantElems(maybeNamespace: Option[String], localName: String): ElemStep[E]

  def descendantElemsOrSelf(): ElemStep[E]

  def descendantElemsOrSelf(localName: String): ElemStep[E]

  def descendantElemsOrSelf(namespace: String, localName: String): ElemStep[E]

  def descendantElemsOrSelf(maybeNamespace: Option[String], localName: String): ElemStep[E]

end ElemStepFactory
