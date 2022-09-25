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
 * Element step, which is an arbitrary function from some element to a sequence of elements. Typical examples are
 * similar to XPath axes, with the exception that only element nodes are considered.
 *
 * Inspired by the Saxon stream API, but only for elements, and using Scala collections, and clearly distinguishing
 * between individual elements and collections of elements (unlike XPath).
 *
 * @author
 *   Chris de Vreeze
 */
trait ElemStep[E] extends (E => Seq[E]):

  final def followedBy(nextStep: ElemStep[E]): ElemStep[E] =
    { (e: E) => this(e).flatMap(nextStep) }

  final def where(pred: E => Boolean): ElemStep[E] =
    { (e: E) => this(e).filter(pred) }

  final def cat(otherStep: ElemStep[E]): ElemStep[E] =
    { (e: E) => this(e).appendedAll(otherStep(e)) }
    
  final def \(nextStep: ElemStep[E]): ElemStep[E] = followedBy(nextStep)

end ElemStep
