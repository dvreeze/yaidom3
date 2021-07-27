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
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Shorthands.*
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.Navigation.NavigationStep
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

/**
 * Element query API test suite using an airports example XML file.
 *
 * @author
 *   Chris de Vreeze
 */
abstract class AirportQuerySpec[E <: ClarkElemApi[E] & Nodes.Elem](val rootElem: E) extends AnyFlatSpec with should.Matchers:

  private val ns = Namespace("http://www.webserviceX.NET")

  private def isInBremen1(e: E): Boolean = e.findChildElem(_.hasLocalName("AirportCode")).exists(_.text == "BRE")
  private def isInBremen2(e: E): Boolean = e.hasLocalName("AirportCode") && e.text == "BRE"
  private def isRoot(e: E): Boolean = e.hasLocalName("NewDataSet")

  "Function findAllChildElems" should "find all child element nodes" in {
    val childElems: Seq[E] = rootElem.findAllChildElems

    childElems should have size 144
    childElems.map(_.name).distinct should equal(Seq(EName.of(ns, ln("Table"))))
    childElems should equal(rootElem.children.filter(_.isInstanceOf[ClarkElemApi[?]]))
  }

  it should "return the same as function filterChildElems with a predicate that accepts all elements" in {
    val childElems: Seq[E] = rootElem.findAllChildElems

    childElems should equal(rootElem.filterChildElems(_ => true))
  }

  "Function filterChildElems" should "return the same as the combi findAllChildElems.filter" in {
    val childElems: Seq[E] = rootElem.filterChildElems(isInBremen1)

    childElems should have size 2
    childElems should equal(rootElem.findAllChildElems.filter(isInBremen1))
  }

  "Function findChildElem" should "return the same as the combi filterChildElems.headOption" in {
    val childElemOpt: Option[E] = rootElem.findChildElem(isInBremen1).ensuring(_.nonEmpty)

    childElemOpt should equal(rootElem.filterChildElems(isInBremen1).headOption)
  }

  "Function findAllDescendantElems" should "find all descendant element nodes" in {
    val elems: Seq[E] = rootElem.findAllDescendantElems

    val groupedElems: Map[EName, Seq[E]] = elems.groupBy(_.name)

    val expectedChildElemCount = 144
    groupedElems.getOrElse(EName.of(ns, ln("Table")), Seq.empty) should have size expectedChildElemCount

    groupedElems.getOrElse(EName.of(ns, ln("AirportCode")), Seq.empty) should have size expectedChildElemCount
    groupedElems.getOrElse(EName.of(ns, ln("Country")), Seq.empty) should have size expectedChildElemCount
    groupedElems.getOrElse(EName.of(ns, ln("CountryAbbrviation")), Seq.empty) should have size expectedChildElemCount
    groupedElems.getOrElse(EName.of(ns, ln("CountryCode")), Seq.empty) should have size expectedChildElemCount

    groupedElems.keySet should not contain (EName.of(ns, ln("NewDataSet")))
  }

  it should "return the same elements as function findAllDescendantElemsOrSelf minus the root element" in {
    val elems: Seq[E] = rootElem.findAllDescendantElems

    elems should equal(rootElem.findAllDescendantElemsOrSelf.tail)
  }

  "Function filterDescendantElems" should "return the same as the combi findAllDescendantElems.filter" in {
    val elems: Seq[E] = rootElem.filterDescendantElems(isInBremen2)

    elems should have size 2
    elems should equal(rootElem.findAllDescendantElems.filter(isInBremen2))
  }

  it should "never return the root element" in {
    require(isRoot(rootElem))
    require(rootElem.findAllChildElems.forall(che => !isRoot(che)))
    val elems: Seq[E] = rootElem.filterDescendantElems(isRoot)

    elems should have size 0
    elems should equal(rootElem.findAllDescendantElems.filter(isRoot))
  }

  "Function findDescendantElem" should "return the same as the combi filterDescendantElems.headOption" in {
    val elemOpt: Option[E] = rootElem.findDescendantElem(isInBremen2).ensuring(_.nonEmpty)

    elemOpt should equal(rootElem.filterDescendantElems(isInBremen2).headOption)
  }

  "Function findAllDescendantElemsOrSelf" should "find all descendant-or-self element nodes" in {
    val elems: Seq[E] = rootElem.findAllDescendantElemsOrSelf

    val groupedElems: Map[EName, Seq[E]] = elems.groupBy(_.name)

    groupedElems.getOrElse(EName.of(ns, ln("NewDataSet")), Seq.empty) should have size 1

    val expectedChildElemCount = 144
    groupedElems.getOrElse(EName.of(ns, ln("Table")), Seq.empty) should have size expectedChildElemCount

    groupedElems.getOrElse(EName.of(ns, ln("AirportCode")), Seq.empty) should have size expectedChildElemCount
    groupedElems.getOrElse(EName.of(ns, ln("Country")), Seq.empty) should have size expectedChildElemCount
    groupedElems.getOrElse(EName.of(ns, ln("CountryAbbrviation")), Seq.empty) should have size expectedChildElemCount
    groupedElems.getOrElse(EName.of(ns, ln("CountryCode")), Seq.empty) should have size expectedChildElemCount
  }

  it should "return one more element (namely the root element) than function findAllDescendantElems" in {
    val elems: Seq[E] = rootElem.findAllDescendantElemsOrSelf

    elems should have size ((rootElem.findAllDescendantElems.size) + 1)

    val groupedElems: Map[EName, Seq[E]] = elems.groupBy(_.name)

    groupedElems.keySet should contain(EName.of(ns, ln("NewDataSet")))
  }

  "Function filterDescendantElemsOrSelf" should "return the same as the combi findAllDescendantElemsOrSelf.filter" in {
    val elems: Seq[E] = rootElem.filterDescendantElemsOrSelf(isInBremen2)

    elems should have size 2
    elems should equal(rootElem.findAllDescendantElemsOrSelf.filter(isInBremen2))
  }

  it should "return the root element if the root obeys the predicate" in {
    require(isRoot(rootElem))
    require(rootElem.findAllChildElems.forall(che => !isRoot(che)))
    val elems: Seq[E] = rootElem.filterDescendantElemsOrSelf(isRoot)

    elems should have size 1
    elems should equal(rootElem.findAllDescendantElemsOrSelf.filter(isRoot))
  }

  "Function findDescendantElemOrSelf" should "return the same as the combi filterDescendantElemsOrSelf.headOption" in {
    val elemOpt: Option[E] = rootElem.findDescendantElemOrSelf(isInBremen2).ensuring(_.nonEmpty)

    elemOpt should equal(rootElem.filterDescendantElemsOrSelf(isInBremen2).headOption)
  }

  it should "return the same as the expression Option(e).filter(p).orElse(e.findDescendantElem(p))" in {
    val elemOpt: Option[E] = rootElem.findDescendantElemOrSelf(isInBremen2).ensuring(_.nonEmpty)

    elemOpt should equal(Option(rootElem).filter(isInBremen2).orElse(rootElem.findDescendantElem(isInBremen2)))
  }

  it should "return the same as the expression Option(e).filter(p).orElse(e.findDescendantElem(p)) if p holds for e" in {
    val elemOpt: Option[E] = rootElem.findDescendantElemOrSelf(isRoot).ensuring(_.nonEmpty)

    elemOpt should equal(Option(rootElem).filter(isRoot).orElse(rootElem.findDescendantElem(isRoot)))
  }

  "Function getDescendantElemOrSelf(NavigationPath)" should "return the same element if the path is empty" in {
    val elems: Seq[E] = rootElem.findAllDescendantElemsOrSelf.map(_.getDescendantElemOrSelf(NavigationPath.empty))

    elems should equal(rootElem.findAllDescendantElemsOrSelf)
  }

  it should "return the appropriate descendant element (if the path is non-empty)" in {
    val expectedChildElem: E = rootElem.findAllChildElems(2)
    val expectedDescendantElems: Seq[E] = expectedChildElem.findAllChildElems.take(5)

    val navigationPaths: Seq[NavigationPath] =
      Seq(NavigationPath.empty.appended(NavigationStep(2))).flatMap(p => (0 until 5).map(i => p.appended(NavigationStep(i))))

    val elems: Seq[E] = navigationPaths.map(p => rootElem.getDescendantElemOrSelf(p))

    elems should equal(expectedDescendantElems)
  }

  "Overloaded function hasName" should "correctly determine if an element has the given name" in {
    val elems: Seq[E] = rootElem.filterDescendantElems(_.name == EName.of(ns, ln("RunwayLengthFeet"))).ensuring(_.sizeIs > 100)

    elems should equal(rootElem.filterDescendantElems(_.hasName(Option(ns), "RunwayLengthFeet")))
    elems should equal(rootElem.filterDescendantElems(_.hasName(ns, "RunwayLengthFeet")))

    rootElem.filterDescendantElems(_.hasName(None, "RunwayLengthFeet")) should have size 0
  }

end AirportQuerySpec
