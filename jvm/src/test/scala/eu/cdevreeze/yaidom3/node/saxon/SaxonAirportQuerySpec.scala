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

package eu.cdevreeze.yaidom3.node.saxon

import java.io.File

import scala.util.chaining._

import eu.cdevreeze.yaidom3.node.AirportQuerySpec
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.streams.Predicates._

/**
 * Saxon-based AirportQuerySpec.
 *
 * @author
 *   Chris de Vreeze
 */
final class SaxonAirportQuerySpec extends AirportQuerySpec[SaxonNodes.Elem](SaxonAirportQuerySpec.loadData())

object SaxonAirportQuerySpec:

  private val saxonProcessor: Processor = new Processor(false)

  def loadData(): SaxonNodes.Elem =
    val file = new File(classOf[SaxonAirportQuerySpec].getResource("/airportsGermany.xml").toURI)
    saxonProcessor
      .newDocumentBuilder()
      .build(file)
      .pipe(_.children(isElement.test(_)).iterator.next)
      .pipe(SaxonNodes.Elem.apply)
      .ensuring(_.findAllDescendantElemsOrSelf.sizeIs >= 2000)

end SaxonAirportQuerySpec
