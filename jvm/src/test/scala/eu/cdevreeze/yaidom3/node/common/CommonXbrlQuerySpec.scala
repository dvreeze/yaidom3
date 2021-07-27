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

package eu.cdevreeze.yaidom3.node.common

import java.io.File

import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.UsingGrowingMap
import eu.cdevreeze.yaidom3.node.XbrlQuerySpec
import eu.cdevreeze.yaidom3.node.saxon.SaxonNodes
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.streams.Predicates.*

/**
 * "Common-based" XbrlQuerySpec.
 *
 * @author
 *   Chris de Vreeze
 */
final class CommonXbrlQuerySpec extends XbrlQuerySpec[DefaultCommonNodes.Elem](CommonXbrlQuerySpec.loadData())

object CommonXbrlQuerySpec:

  private val saxonProcessor: Processor = Processor(false)

  def loadData(): DefaultCommonNodes.Elem =
    given enameProvider: ENameProvider = UsingGrowingMap.makeENameProvider

    val file = File(classOf[CommonXbrlQuerySpec].getResource("/sample-xbrl-instance.xml").toURI)
    saxonProcessor
      .newDocumentBuilder()
      .build(file)
      .pipe(_.children(isElement.test(_)).iterator.next)
      .pipe(SaxonNodes.Elem(_))
      .pipe(e => DefaultCommonNodes.Elem.from(e)(using enameProvider))
      .ensuring(_.findAllDescendantElemsOrSelf.sizeIs >= 1000)

end CommonXbrlQuerySpec
