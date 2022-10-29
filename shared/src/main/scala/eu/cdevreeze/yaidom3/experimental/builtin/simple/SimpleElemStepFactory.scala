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

package eu.cdevreeze.yaidom3.experimental.builtin.simple

import eu.cdevreeze.yaidom3.experimental.queryapi.BaseElemStepFactory
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStep

/**
 * BaseElemStepFactory implementation for SimpleNode.Elem elements.
 *
 * @author
 *   Chris de Vreeze
 */
object SimpleElemStepFactory extends BaseElemStepFactory[SimpleNode.Elem]:

  type E = SimpleNode.Elem

  def childElems(): ElemStep[E] = childElems(_ => true)

  def childElems(pred: E => Boolean): ElemStep[E] = filterChildElems(_, pred)

  def childElems(localName: String): ElemStep[E] = childElems(_.hasLocalName(localName))

  def childElems(namespace: String, localName: String): ElemStep[E] =
    childElems(_.hasName(namespace, localName))

  def childElems(maybeNamespace: Option[String], localName: String): ElemStep[E] =
    childElems(_.hasName(maybeNamespace, localName))

  def descendantElems(): ElemStep[E] = descendantElems(_ => true)

  def descendantElems(pred: E => Boolean): ElemStep[E] = filterDescendantElems(_, pred)

  def descendantElems(localName: String): ElemStep[E] = descendantElems(_.hasLocalName(localName))

  def descendantElems(namespace: String, localName: String): ElemStep[E] =
    descendantElems(_.hasName(namespace, localName))

  def descendantElems(maybeNamespace: Option[String], localName: String): ElemStep[E] =
    descendantElems(_.hasName(maybeNamespace, localName))

  def descendantElemsOrSelf(): ElemStep[E] = descendantElemsOrSelf(_ => true)

  def descendantElemsOrSelf(pred: E => Boolean): ElemStep[E] = filterDescendantElemsOrSelf(_, pred)

  def descendantElemsOrSelf(localName: String): ElemStep[E] = descendantElemsOrSelf(_.hasLocalName(localName))

  def descendantElemsOrSelf(namespace: String, localName: String): ElemStep[E] =
    descendantElemsOrSelf(_.hasName(namespace, localName))

  def descendantElemsOrSelf(maybeNamespace: Option[String], localName: String): ElemStep[E] =
    descendantElemsOrSelf(_.hasName(maybeNamespace, localName))

  private def filterChildElems(e: E, pred: E => Boolean): Seq[E] =
    e.children.collect { case e: SimpleNode.Elem if pred(e) => e }

  private def filterDescendantElems(e: E, pred: E => Boolean): Seq[E] =
    filterChildElems(e, _ => true).flatMap(che => filterDescendantElemsOrSelf(che, pred))

  private def filterDescendantElemsOrSelf(e: E, pred: E => Boolean): Seq[E] =
    Seq(e).filter(pred).appendedAll(
      filterChildElems(e, _ => true).flatMap(che => filterDescendantElemsOrSelf(che, pred))
    )

end SimpleElemStepFactory
