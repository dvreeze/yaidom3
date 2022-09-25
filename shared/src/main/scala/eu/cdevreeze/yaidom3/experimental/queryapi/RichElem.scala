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
 * Extension of arbitrary element API with the element step API.
 *
 * TODO Consider a type class instead.
 *
 * @author
 *   Chris de Vreeze
 */
final class RichElem[E](val underlying: E):
  def selectElems(elemStep: ElemStep[E]): Seq[E] = elemStep(underlying)

extension [E] (elem: E)
  private def richElem: RichElem[E] = RichElem(elem)
  export richElem.*