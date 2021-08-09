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

import eu.cdevreeze.yaidom3.node.clark.DefaultClarkElemFactory
import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation.*
import eu.cdevreeze.yaidom3.core.Shorthands.*
import eu.cdevreeze.yaidom3.node.clark.DefaultClarkNodes
import eu.cdevreeze.yaidom3.node.scoped.DefaultScopedNodes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

import scala.collection.immutable.ListMap
import scala.language.adhocExtensions
import scala.util.chaining.*

/**
 * Element creation and update API test suite using XBRL instance creation as example.
 *
 * @author
 *   Chris de Vreeze
 */
class XbrlSampleSpec extends AnyFlatSpec, should.Matchers:

  import XbrlSample.*

  "The UnitBuilder".should("create simple units").in {
    val xbrliUnit: DefaultScopedNodes.Elem =
      UnitBuilder.createUnit("U-Monetary", minimalScope).addMeasure(qn(iso4217Pref, "USD").asPrefixedName, iso4217Ns).unitElem

    xbrliUnit
      .findChildElem(_.hasName(xbrliNs, "measure"))
      .map(_.text)
      .should(equal(Some("iso4217:USD")))

    xbrliUnit
      .findChildElem(_.hasName(xbrliNs, "measure"))
      .map(_.textAsQName)
      .should(equal(Some(qn(iso4217Pref, "USD"))))

    xbrliUnit
      .findChildElem(_.hasName(xbrliNs, "measure"))
      .map(_.textAsResolvedQName)
      .should(equal(Some(en(iso4217Ns, "USD"))))

    val expectedClarkElem: DefaultClarkNodes.Elem =
      import DefaultClarkElemFactory.*
      val scope = minimalScope
      elem(
        qn(xbrliPref, "unit"),
        ListMap(qn("id") -> "U-Monetary"),
        scope,
        Seq(textElem(qn(xbrliPref, "measure"), ListMap.empty, scope, "iso4217:USD"))
      )

    DefaultClarkNodes.Elem.from(xbrliUnit).should(equal(expectedClarkElem))
  }

  "The ContextBuilder".should("create simple contexts").in {
    val xbrliContext: DefaultScopedNodes.Elem =
      ContextBuilder
        .createContext("D-2007", minimalScope)
        .addIdentifier("http://www.sec.gov/CIK", "1234567890")
        .addSegment(
          SegmentBuilder
            .createSegment(minimalScope)
            .addExplicitDimension(
              qn(gaapPref, "EntityAxis").asPrefixedName,
              gaapNs,
              qn(gaapPref, "ABCCompanyDomain").asPrefixedName,
              gaapNs
            )
            .addExplicitDimension(
              qn(gaapPref, "BusinessSegmentAxis").asPrefixedName,
              gaapNs,
              qn(gaapPref, "ConsolidatedGroupDomain").asPrefixedName,
              gaapNs
            )
            .addExplicitDimension(
              qn(gaapPref, "VerificationAxis").asPrefixedName,
              gaapNs,
              qn(gaapPref, "UnqualifiedOpinionMember").asPrefixedName,
              gaapNs
            )
            .addExplicitDimension(qn(gaapPref, "PremiseAxis").asPrefixedName, gaapNs, qn(gaapPref, "ActualMember").asPrefixedName, gaapNs)
            .addExplicitDimension(
              qn(gaapPref, "ReportDateAxis").asPrefixedName,
              gaapNs,
              qn(gaapPref, "ReportedAsOfMarch182008Member").asPrefixedName,
              gaapNs
            )
        )
        .addDurationPeriod("2007-01-01", "2007-12-31")
        .contextElem

    xbrliContext.attrOption(en("id")).should(equal(Some("D-2007")))

    xbrliContext
      .findDescendantElem(_.hasName(xbrliNs, "identifier"))
      .flatMap(_.attrOption(en("scheme")))
      .should(equal(Some("http://www.sec.gov/CIK")))
    xbrliContext
      .findDescendantElem(_.hasName(xbrliNs, "identifier"))
      .map(_.text)
      .should(equal(Some("1234567890")))

    xbrliContext.findDescendantElem(_.hasName(xbrliNs, "startDate")).map(_.text).should(equal(Some("2007-01-01")))
    xbrliContext.findDescendantElem(_.hasName(xbrliNs, "endDate")).map(_.text).should(equal(Some("2007-12-31")))

    val explicitDimElems: Seq[DefaultScopedNodes.Elem] = xbrliContext.filterDescendantElems(_.hasName(xbrldiNs, "explicitMember"))
    val dimMembers: Map[EName, EName] = explicitDimElems.map(e => e.attrAsResolvedQName(en("dimension")) -> e.textAsResolvedQName).toMap

    dimMembers.should(
      equal(
        Map(
          en(gaapNs, "EntityAxis") -> en(gaapNs, "ABCCompanyDomain"),
          en(gaapNs, "BusinessSegmentAxis") -> en(gaapNs, "ConsolidatedGroupDomain"),
          en(gaapNs, "VerificationAxis") -> en(gaapNs, "UnqualifiedOpinionMember"),
          en(gaapNs, "PremiseAxis") -> en(gaapNs, "ActualMember"),
          en(gaapNs, "ReportDateAxis") -> en(gaapNs, "ReportedAsOfMarch182008Member")
        )
      )
    )

    val expectedClarkElem: DefaultClarkNodes.Elem =
      import DefaultClarkElemFactory.*
      val scope = minimalScope
      elem(
        qn(xbrliPref, "context"),
        ListMap(qn("id") -> "D-2007"),
        scope,
        Seq(
          elem(
            qn(xbrliPref, "entity"),
            ListMap.empty,
            scope,
            Seq(
              textElem(qn(xbrliPref, "identifier"), ListMap(qn("scheme") -> "http://www.sec.gov/CIK"), scope, "1234567890"),
              elem(
                qn(xbrliPref, "segment"),
                ListMap.empty,
                scope,
                Seq(
                  textElem(qn(xbrldiPref, "explicitMember"), ListMap(qn("dimension") -> "gaap:EntityAxis"), scope, "gaap:ABCCompanyDomain"),
                  textElem(
                    qn(xbrldiPref, "explicitMember"),
                    ListMap(qn("dimension") -> "gaap:BusinessSegmentAxis"),
                    scope,
                    "gaap:ConsolidatedGroupDomain"
                  ),
                  textElem(
                    qn(xbrldiPref, "explicitMember"),
                    ListMap(qn("dimension") -> "gaap:VerificationAxis"),
                    scope,
                    "gaap:UnqualifiedOpinionMember"
                  ),
                  textElem(qn(xbrldiPref, "explicitMember"), ListMap(qn("dimension") -> "gaap:PremiseAxis"), scope, "gaap:ActualMember"),
                  textElem(
                    qn(xbrldiPref, "explicitMember"),
                    ListMap(qn("dimension") -> "gaap:ReportDateAxis"),
                    scope,
                    "gaap:ReportedAsOfMarch182008Member"
                  )
                )
              )
            )
          ),
          elem(
            qn(xbrliPref, "period"),
            ListMap.empty,
            scope,
            Seq(
              textElem(qn(xbrliPref, "startDate"), scope, "2007-01-01"),
              textElem(qn(xbrliPref, "endDate"), scope, "2007-12-31")
            )
          )
        )
      )
    end expectedClarkElem

    DefaultClarkNodes.Elem.from(xbrliContext).should(equal(expectedClarkElem))
  }

end XbrlSampleSpec
