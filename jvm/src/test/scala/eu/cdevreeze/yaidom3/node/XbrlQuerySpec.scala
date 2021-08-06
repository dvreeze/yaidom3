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

package eu.cdevreeze.yaidom3.node

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.Navigation.NavigationStep
import eu.cdevreeze.yaidom3.core.Shorthands.*
import eu.cdevreeze.yaidom3.queryapi.CommonElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

import scala.language.adhocExtensions

/**
 * Element query API test suite using an XBRL instance example XML file.
 *
 * @author
 *   Chris de Vreeze
 */
abstract class XbrlQuerySpec[E <: CommonElemApi[E] & Nodes.Elem](val rootElem: E) extends AnyFlatSpec, should.Matchers:

  private val xbrliNs: Namespace = ns("http://www.xbrl.org/2003/instance")
  private val linkNs: Namespace = ns("http://www.xbrl.org/2003/linkbase")
  private val xlinkNs: Namespace = ns("http://www.w3.org/1999/xlink")
  private val xbrldiNs: Namespace = ns("http://xbrl.org/2006/xbrldi")
  private val iso4217Ns: Namespace = ns("http://www.xbrl.org/2003/iso4217")
  private val gaapNs: Namespace = ns("http://xasb.org/gaap")

  "The CommonElemApi of elements (used for XBRL instances)".should("find specific context IDs").in {
    val contexts: Seq[E] =
      rootElem.filterChildElems(e => e.hasName(xbrliNs, "context") && e.attr(en("id")).startsWith("I-2007"))

    contexts.should(have(size(26)))
  }

  it.should("find dimension names").in {
    val dimensionElems: Seq[E] =
      for
        context <- rootElem.filterDescendantElems(_.hasName(xbrliNs, "context"))
        explicitMember <- context.filterDescendantElems(_.hasName(xbrldiNs, "explicitMember"))
      yield explicitMember

    val dimensions: Set[EName] = dimensionElems.map(_.attrAsResolvedQName(en("dimension"))).toSet

    val someExpectedDimensions: Set[EName] =
      Set(
        en(gaapNs, "EntityAxis"),
        en(gaapNs, "BusinessSegmentAxis"),
        en(gaapNs, "VerificationAxis"),
        en(gaapNs, "PremiseAxis"),
        en(gaapNs, "ReportDateAxis"),
        en(gaapNs, "ClassOfPreferredStockDescriptionAxis"),
        en(gaapNs, "ClassOfCommonStockDescriptionAxis"),
        en(gaapNs, "LeaseholdLandAndBuildingIdentifierAxis"),
        en(gaapNs, "LeaseholdLandAndBuildingStateAxis"),
        en(gaapNs, "DebtInstrumentIdentifierAxis"),
        en(gaapNs, "ShareOwnershipPlanIdentifierAxis"),
        en(gaapNs, "SubsequentEventCategoryAxis"),
        en(gaapNs, "RelatedPartyNameAxis"),
        en(gaapNs, "RelatedPartyTransactionTypeAxis"),
        en(gaapNs, "DirectorNameAxis"),
        en(gaapNs, "ReconcilingItemTypeAxis")
      )

    dimensions.filter(someExpectedDimensions).should(equal(someExpectedDimensions))
  }

  it.should("find dimension and their member names").in {
    val dimensionElems: Seq[E] =
      for
        context <- rootElem.filterDescendantElems(e => e.hasName(xbrliNs, "context") && e.attr(en("id")) == "D-2007-ABC1")
        explicitMember <- context.filterDescendantElems(_.hasName(xbrldiNs, "explicitMember"))
      yield explicitMember

    val dimensionMembers: Map[EName, EName] =
      dimensionElems.map(e => e.attrAsResolvedQName(en("dimension")) -> e.textAsResolvedQName).toMap

    dimensionMembers.should(
      equal(
        Map(
          en(gaapNs, "EntityAxis") -> en(gaapNs, "ABCCompanyDomain"),
          en(gaapNs, "CustomerAxis") -> en(gaapNs, "CustomerAMember"),
          en(gaapNs, "VerificationAxis") -> en(gaapNs, "UnqualifiedOpinionMember"),
          en(gaapNs, "PremiseAxis") -> en(gaapNs, "ActualMember"),
          en(gaapNs, "ReportDateAxis") -> en(gaapNs, "ReportedAsOfMarch182008Member")
        )
      )
    )
  }

  it.should("find units as ENames").in {
    val unitMeasureElems: Seq[E] =
      for
        unitElem <- rootElem.filterChildElems(_.hasName(xbrliNs, "unit"))
        measureElem <- unitElem.filterChildElems(_.hasName(xbrliNs, "measure"))
      yield measureElem

    val measures: Set[EName] = unitMeasureElems.map(_.textAsResolvedQName).toSet

    measures.should(equal(Set(en(iso4217Ns, "USD"), en(xbrliNs, "shares"), en(xbrliNs, "pure"))))
  }

  it.should("find all facts").in {
    val facts: Seq[E] = findAllFacts

    val factNamespaces: Set[Namespace] = facts.flatMap(_.name.namespaceOption).toSet

    factNamespaces.should(equal(Set(gaapNs)))
    facts.should(equal(rootElem.filterDescendantElems(_.name.namespaceOption.contains(gaapNs))))
  }

  private def findAllFacts: Seq[E] =
    rootElem.filterDescendantElems { e =>
      !Set(Option(xbrliNs), Option(linkNs)).contains(e.name.namespaceOption) &&
      e.findAncestorElem { ae =>
        Set(Option(xbrliNs), Option(linkNs)).contains(ae.name.namespaceOption) && ae.name != en(xbrliNs, "xbrl")
      }.isEmpty
    }

end XbrlQuerySpec
