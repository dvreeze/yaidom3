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

package eu.cdevreeze.yaidom3.examples

import java.io.File

import scala.collection.immutable.ListMap
import scala.language.adhocExtensions
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.ENameProvider.UsingGrowingMap
import eu.cdevreeze.yaidom3.core.Shorthands.*
import eu.cdevreeze.yaidom3.node.clark.DefaultClarkNodes
import eu.cdevreeze.yaidom3.node.common.CommonXbrlQuerySpec
import eu.cdevreeze.yaidom3.node.common.DefaultCommonNodes
import eu.cdevreeze.yaidom3.node.common.given
import eu.cdevreeze.yaidom3.node.saxon.SaxonNodes
import eu.cdevreeze.yaidom3.node.scoped.given
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.streams.Predicates.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

/**
 * Element update API test suite using XBRL instance creation as example.
 *
 * @author
 *   Chris de Vreeze
 */
class XbrlSampleUpdateSpec extends AnyFlatSpec, should.Matchers:

  import XbrlSample.*

  private val saxonProcessor: Processor = Processor(false)

  private val rootElem: DefaultCommonNodes.Elem = loadData()

  "The 'common update API'".should("successfully (functionally) update parts of the XBRL instance").in {
    val segmentElemOption: Option[DefaultCommonNodes.Elem] =
      rootElem
        .findDescendantElem(e => e.hasName(xbrliNs, "context") && e.attr(en("id")) == "D-2007")
        .flatMap(_.findDescendantElem(_.hasName(xbrliNs, "segment")))

    segmentElemOption.isDefined.should(equal(true))

    val segmentElem: DefaultCommonNodes.Elem = segmentElemOption.get

    val updatedSegmentElem: DefaultCommonNodes.Elem =
      segmentElem.updatedWithinTree { e =>
        SegmentBuilder
          .NonEmptySegment(e)
          .addExplicitDimension(
            qn(gaapPref, "ClassOfPreferredStockDescriptionAxis").asPrefixedName,
            gaapNs,
            qn(gaapPref, "AllClassesOfPreferredStockDomain").asPrefixedName,
            gaapNs
          )
          .segmentElem
      }

    val updatedRootElem: DefaultCommonNodes.Elem = updatedSegmentElem.rootElem

    val foundSegmentElemOption: Option[DefaultCommonNodes.Elem] =
      updatedRootElem
        .findDescendantElem(e => e.hasName(xbrliNs, "context") && e.attr(en("id")) == "D-2007")
        .flatMap(_.findDescendantElem(_.hasName(xbrliNs, "segment")))

    foundSegmentElemOption.isDefined.should(equal(true))

    val foundSegmentElem: DefaultCommonNodes.Elem = foundSegmentElemOption.get

    foundSegmentElem.findAllChildElems.size.should(equal(1 + segmentElem.findAllChildElems.size))

    // With empty segments, both instances are the same
    def makeSegmentsEmpty(root: DefaultCommonNodes.Elem): DefaultCommonNodes.Elem =
      DefaultCommonNodes.Elem.ofRoot(
        root.docUriOption,
        root.underlyingElem.transformDescendantElems {
          case e if e.hasName(xbrliNs, "segment") => e.withChildren(Seq.empty)
          case e                                  => e
        }
      )

    makeSegmentsEmpty(updatedRootElem)
      .pipe(DefaultClarkNodes.Elem.from(_))
      .should(
        equal(
          makeSegmentsEmpty(rootElem).pipe(DefaultClarkNodes.Elem.from(_))
        )
      )

    DefaultClarkNodes.Elem
      .from(updatedRootElem)
      .should(
        not(
          equal(
            DefaultClarkNodes.Elem.from(rootElem)
          )
        )
      )
  }

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

end XbrlSampleUpdateSpec
