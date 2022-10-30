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

package eu.cdevreeze.yaidom3.experimental.testsaxon

import java.io.File

import scala.util.chaining.*

import eu.cdevreeze.yaidom3.experimental.queryapi.ElemQueryApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStepFactory
import eu.cdevreeze.yaidom3.experimental.saxon.SaxonNode.Elem
import eu.cdevreeze.yaidom3.experimental.saxon.SaxonGivens.elemStepFactory
import eu.cdevreeze.yaidom3.experimental.saxon.SaxonGivens.toYaidom
import eu.cdevreeze.yaidom3.experimental.saxon.SaxonNode
import eu.cdevreeze.yaidom3.experimental.test.XbrlQuerySpec
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.XdmNode
import net.sf.saxon.s9api.streams.Predicates.*

/**
 * Saxon-based XBRL query spec.
 *
 * @author
 *   Chris de Vreeze
 */
class SaxonXbrlQuerySpec extends XbrlQuerySpec[XdmNode, SaxonNode.Elem](SaxonXbrlQuerySpec.loadData())(using elemStepFactory, SaxonXbrlQuerySpec.conversion)

object SaxonXbrlQuerySpec:

  private val saxonProcessor: Processor = Processor(false)

  def loadData(): XdmNode =
    val file = File(classOf[SaxonXbrlQuerySpec].getResource("/sample-xbrl-instance.xml").toURI)
    saxonProcessor
      .newDocumentBuilder()
      .build(file)
      .pipe(_.children(isElement.test(_)).iterator.next)
      .ensuring(_.wrap.selectElems(elemStepFactory.descendantElemsOrSelf()).sizeIs >= 1000)

  given conversion: Conversion[XdmNode, SaxonNode.Elem] with
    def apply(v: XdmNode): SaxonNode.Elem = SaxonNode.Elem(v)

end SaxonXbrlQuerySpec
