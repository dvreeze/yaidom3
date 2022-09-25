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

package eu.cdevreeze.yaidom3.experimental.saxon

import scala.jdk.StreamConverters.*
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStep
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStepFactory
import net.sf.saxon.s9api.XdmNode
import net.sf.saxon.s9api.XdmNodeKind
import net.sf.saxon.s9api.streams.Predicates.*
import net.sf.saxon.s9api.streams.Step
import net.sf.saxon.s9api.streams.Steps.*

/**
 * Saxon element step factory.
 *
 * @author
 *   Chris de Vreeze
 */
object SaxonElemStepFactory extends ElemStepFactory[XdmNode]:

  def childElems(): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, child()) }

  def childElems(pred: XdmNode => Boolean): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, child(), pred) }

  def childElems(localName: String): ElemStep[XdmNode] =
    require(localName.trim != "*", "Wildcard '*' for local name not allowed")
    { (e: XdmNode) => filterElems(e, child(localName)) }

  def childElems(namespace: String, localName: String): ElemStep[XdmNode] =
    require(namespace.trim.nonEmpty, "Empty namespace not allowed")
    { (e: XdmNode) => filterElems(e, child(namespace, localName)) }

  def childElems(maybeNamespace: Option[String], localName: String): ElemStep[XdmNode] =
    val saxonNs = maybeNamespace.getOrElse("")
    { (e: XdmNode) => filterElems(e, child(saxonNs, localName)) }

  def descendantElems(): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, descendant()) }

  def descendantElems(pred: XdmNode => Boolean): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, descendant(), pred) }

  def descendantElems(localName: String): ElemStep[XdmNode] =
    require(localName.trim != "*", "Wildcard '*' for local name not allowed")
    { (e: XdmNode) => filterElems(e, descendant(localName)) }

  def descendantElems(namespace: String, localName: String): ElemStep[XdmNode] =
    require(namespace.trim.nonEmpty, "Empty namespace not allowed")
    { (e: XdmNode) => filterElems(e, descendant(namespace, localName)) }

  def descendantElems(maybeNamespace: Option[String], localName: String): ElemStep[XdmNode] =
    val saxonNs = maybeNamespace.getOrElse("")
    { (e: XdmNode) => filterElems(e, descendant(saxonNs, localName)) }

  def descendantElemsOrSelf(): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, descendantOrSelf()) }

  def descendantElemsOrSelf(pred: XdmNode => Boolean): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, descendantOrSelf(), pred) }

  def descendantElemsOrSelf(localName: String): ElemStep[XdmNode] =
    require(localName.trim != "*", "Wildcard '*' for local name not allowed")
    { (e: XdmNode) => filterElems(e, descendantOrSelf(localName)) }

  def descendantElemsOrSelf(namespace: String, localName: String): ElemStep[XdmNode] =
    require(namespace.trim.nonEmpty, "Empty namespace not allowed")
    { (e: XdmNode) => filterElems(e, descendantOrSelf(namespace, localName)) }

  def descendantElemsOrSelf(maybeNamespace: Option[String], localName: String): ElemStep[XdmNode] =
    val saxonNs = maybeNamespace.getOrElse("")
    { (e: XdmNode) => filterElems(e, descendantOrSelf(saxonNs, localName)) }

  def parentElems(): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, parent()) }

  def parentElems(pred: XdmNode => Boolean): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, parent(), pred) }

  def parentElems(localName: String): ElemStep[XdmNode] =
    require(localName.trim != "*", "Wildcard '*' for local name not allowed")
    { (e: XdmNode) => filterElems(e, parent(localName)) }

  def parentElems(namespace: String, localName: String): ElemStep[XdmNode] =
    require(namespace.trim.nonEmpty, "Empty namespace not allowed")
    { (e: XdmNode) => filterElems(e, parent(namespace, localName)) }

  def parentElems(maybeNamespace: Option[String], localName: String): ElemStep[XdmNode] =
    val saxonNs = maybeNamespace.getOrElse("")
    { (e: XdmNode) => filterElems(e, parent(saxonNs, localName)) }

  def ancestorElems(): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, ancestor()) }

  def ancestorElems(pred: XdmNode => Boolean): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, ancestor(), pred) }

  def ancestorElems(localName: String): ElemStep[XdmNode] =
    require(localName.trim != "*", "Wildcard '*' for local name not allowed")
    { (e: XdmNode) => filterElems(e, ancestor(localName)) }

  def ancestorElems(namespace: String, localName: String): ElemStep[XdmNode] =
    require(namespace.trim.nonEmpty, "Empty namespace not allowed")
    { (e: XdmNode) => filterElems(e, ancestor(namespace, localName)) }

  def ancestorElems(maybeNamespace: Option[String], localName: String): ElemStep[XdmNode] =
    val saxonNs = maybeNamespace.getOrElse("")
    { (e: XdmNode) => filterElems(e, ancestor(saxonNs, localName)) }

  def ancestorElemsOrSelf(): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, ancestorOrSelf()) }

  def ancestorElemsOrSelf(pred: XdmNode => Boolean): ElemStep[XdmNode] =
    { (e: XdmNode) => filterElems(e, ancestorOrSelf(), pred) }

  def ancestorElemsOrSelf(localName: String): ElemStep[XdmNode] =
    require(localName.trim != "*", "Wildcard '*' for local name not allowed")
    { (e: XdmNode) => filterElems(e, ancestorOrSelf(localName)) }

  def ancestorElemsOrSelf(namespace: String, localName: String): ElemStep[XdmNode] =
    require(namespace.trim.nonEmpty, "Empty namespace not allowed")
    { (e: XdmNode) => filterElems(e, ancestorOrSelf(namespace, localName)) }

  def ancestorElemsOrSelf(maybeNamespace: Option[String], localName: String): ElemStep[XdmNode] =
    val saxonNs = maybeNamespace.getOrElse("")
    { (e: XdmNode) => filterElems(e, ancestorOrSelf(saxonNs, localName)) }

  private def filterElems(elem: XdmNode, step: Step[XdmNode], pred: XdmNode => Boolean): Seq[XdmNode] =
    val stream = elem.select(step.where(n => isElement.test(n) && pred(n)))
    stream.toScala(Vector)

  private def filterElems(elem: XdmNode, step: Step[XdmNode]): Seq[XdmNode] =
    val stream = elem.select(step.where(n => isElement.test(n)))
    stream.toScala(Vector)

end SaxonElemStepFactory
