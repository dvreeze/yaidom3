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

import java.io.File

import scala.util.chaining.*

import eu.cdevreeze.yaidom3.experimental.core.EName
import eu.cdevreeze.yaidom3.experimental.queryapi.selectElems
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemQueryApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStepFactory
import eu.cdevreeze.yaidom3.experimental.saxon.SaxonGivens.given
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.XdmNode
import net.sf.saxon.s9api.streams.Predicates.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

/**
 * Saxon-based XBRL query spec.
 *
 * @author
 *   Chris de Vreeze
 */
class SaxonXbrlQuerySpec extends AnyFlatSpec, should.Matchers:

  private val xbrliNs = "http://www.xbrl.org/2003/instance"
  private val linkNs = "http://www.xbrl.org/2003/linkbase"
  private val xlinkNs = "http://www.w3.org/1999/xlink"
  private val xbrldiNs = "http://xbrl.org/2006/xbrldi"
  private val iso4217Ns = "http://www.xbrl.org/2003/iso4217"
  private val gaapNs = "http://xasb.org/gaap"

  private val rootElem: XdmNode = SaxonXbrlQuerySpec.loadData()

  private type E = XdmNode

  private val elemStepFactory: ElemStepFactory[XdmNode] = summon[ElemStepFactory[XdmNode]]
  import elemStepFactory.*

  private val elemQueryApi: ElemQueryApi[XdmNode] = summon[ElemQueryApi[XdmNode]]
  import elemQueryApi.*

  behavior.of("The summoned ElemStepFactory (used for XBRL instances)")

  it.should("find specific context IDs").in {
    val contexts: Seq[E] =
      rootElem.selectElems {
        childElems(xbrliNs, "context").where(e => attr(e, "id").startsWith("I-2007"))
      }

    contexts.should(have(size(26)))
  }

  it.should("find dimension names").in {
    val dimensionElems: Seq[E] =
      rootElem.selectElems {
        descendantElems(xbrliNs, "context")
          .next(descendantElems(xbrldiNs, "explicitMember"))
      }

    val dimensions: Set[EName] = dimensionElems.map(e => attrAsResolvedQName(e, EName.of("dimension"))).toSet

    val someExpectedDimensions: Set[EName] =
      Set(
        EName.of(gaapNs, "EntityAxis"),
        EName.of(gaapNs, "BusinessSegmentAxis"),
        EName.of(gaapNs, "VerificationAxis"),
        EName.of(gaapNs, "PremiseAxis"),
        EName.of(gaapNs, "ReportDateAxis"),
        EName.of(gaapNs, "ClassOfPreferredStockDescriptionAxis"),
        EName.of(gaapNs, "ClassOfCommonStockDescriptionAxis"),
        EName.of(gaapNs, "LeaseholdLandAndBuildingIdentifierAxis"),
        EName.of(gaapNs, "LeaseholdLandAndBuildingStateAxis"),
        EName.of(gaapNs, "DebtInstrumentIdentifierAxis"),
        EName.of(gaapNs, "ShareOwnershipPlanIdentifierAxis"),
        EName.of(gaapNs, "SubsequentEventCategoryAxis"),
        EName.of(gaapNs, "RelatedPartyNameAxis"),
        EName.of(gaapNs, "RelatedPartyTransactionTypeAxis"),
        EName.of(gaapNs, "DirectorNameAxis"),
        EName.of(gaapNs, "ReconcilingItemTypeAxis")
      )

    dimensions.filter(someExpectedDimensions).should(equal(someExpectedDimensions))
  }

  it.should("find dimension and their member names").in {
    val dimensionElems: Seq[E] =
      rootElem.selectElems {
        descendantElems(xbrliNs, "context").where(e => attr(e, "id") == "D-2007-ABC1")
          .next(descendantElems(xbrldiNs, "explicitMember"))
      }

    val dimensionMembers: Map[EName, EName] =
      dimensionElems.map(e => attrAsResolvedQName(e, EName.of("dimension")) -> textAsResolvedQName(e)).toMap

    dimensionMembers.should(
      equal(
        Map(
          EName.of(gaapNs, "EntityAxis") -> EName.of(gaapNs, "ABCCompanyDomain"),
          EName.of(gaapNs, "CustomerAxis") -> EName.of(gaapNs, "CustomerAMember"),
          EName.of(gaapNs, "VerificationAxis") -> EName.of(gaapNs, "UnqualifiedOpinionMember"),
          EName.of(gaapNs, "PremiseAxis") -> EName.of(gaapNs, "ActualMember"),
          EName.of(gaapNs, "ReportDateAxis") -> EName.of(gaapNs, "ReportedAsOfMarch182008Member")
        )
      )
    )
  }

  it.should("find units as ENames").in {
    val unitMeasureElems: Seq[E] =
      rootElem.selectElems {
        childElems(xbrliNs, "unit")
          .next(childElems(xbrliNs, "measure"))
      }

    val measures: Set[EName] = unitMeasureElems.map(e => textAsResolvedQName(e)).toSet

    measures.should(equal(Set(EName.of(iso4217Ns, "USD"), EName.of(xbrliNs, "shares"), EName.of(xbrliNs, "pure"))))
  }

  it.should("find all facts").in {
    val facts: Seq[E] = findAllFacts

    val factNamespaces: Set[String] = facts.flatMap(e => name(e).namespaceOption).toSet

    factNamespaces.should(equal(Set(gaapNs)))
    facts.should(equal(rootElem.selectElems { descendantElems(e => name(e).namespaceOption.contains(gaapNs)) }))
  }

  private def findAllFacts: Seq[E] =
    rootElem.selectElems {
      descendantElems { e =>
        !Set(Option(xbrliNs), Option(linkNs)).contains(name(e).namespaceOption) &&
          e.selectElems {
            ancestorElems { ae =>
              Set(Option(xbrliNs), Option(linkNs)).contains(name(ae).namespaceOption) && name(ae) != EName.of(xbrliNs, "xbrl")
            }
          }.isEmpty
      }
    }

object SaxonXbrlQuerySpec:

  private val saxonProcessor: Processor = Processor(false)

  def loadData(): XdmNode =
    val file = File(classOf[SaxonXbrlQuerySpec].getResource("/sample-xbrl-instance.xml").toURI)
    saxonProcessor
      .newDocumentBuilder()
      .build(file)
      .pipe(_.children(isElement.test(_)).iterator.next)
      .ensuring(_.selectElems(summon[ElemStepFactory[XdmNode]].descendantElemsOrSelf()).sizeIs >= 1000)

end SaxonXbrlQuerySpec
