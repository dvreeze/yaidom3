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

package eu.cdevreeze.yaidom3.props

import java.io.File

import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation.*
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import eu.cdevreeze.yaidom3.node.saxon.SaxonNodes
import eu.cdevreeze.yaidom3.props.DefaultElemGenerator.rootElemPaths
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.streams.Predicates.*
import org.scalacheck.Gen

/**
 * Default abstract ElemGenerator, leaving only the element type open.
 *
 * @author
 *   Chris de Vreeze
 */
abstract class DefaultElemGenerator[E <: ClarkElemApi[E] & Nodes.Elem] extends ElemGenerator[E]:

  protected def convertToElemType(e: SaxonNodes.Elem): E

  val genElem: Gen[E] =
    val rootElems: Seq[E] = rootElemPaths.map(DefaultElemGenerator.loadXmlFile).map(convertToElemType)
    val allElems: Seq[E] = rootElems.flatMap(_.findAllDescendantElemsOrSelf)
    require(allElems.size >= 100, s"Expected at least 100 elements")
    Gen.oneOf(Gen.oneOf(allElems), Gen.oneOf(rootElems))

  val genElemPred: Gen[E => Boolean] =
    Gen.oneOf(Seq(predTrue, predLocalNameSizeGt7, predLocalNameContainsCapital, predLocalNameContainsNoCapital))

  val genElemName: Gen[EName] =
    genElem.flatMap(e => Gen.oneOf(e.name, e.findAllChildElems.headOption.map(_.name).getOrElse(EName.parse("dummyName"))))

  val genElemLocalName: Gen[LocalName] = genElemName.map(_.localPart)

  val genNavigationPath: Gen[NavigationPath] =
    genElem.flatMap { e =>
      val lastChildElemStep = NavigationStep((e.findAllChildElems.size - 1).max(0)) // Could point to nothing
      val secondChildElemOfLastChildElemPath = NavigationPath.from(Seq(lastChildElemStep.toInt, 1)) // Could point to nothing
      Gen.oneOf(Seq(NavigationPath.empty, NavigationPath.from(Seq(0, 0)), NavigationPath.from(Seq(2)), secondChildElemOfLastChildElemPath))
    }

  private def predTrue(e: E): Boolean = true
  private def predLocalNameSizeGt7(e: E): Boolean = e.name.localPart.toString.size > 7
  private def predLocalNameContainsCapital(e: E): Boolean = e.name.localPart.toString.exists(c => Character.isUpperCase(c))
  private def predLocalNameContainsNoCapital(e: E): Boolean = !predLocalNameContainsCapital(e)

object DefaultElemGenerator:

  private val rootElemPaths: Seq[String] = Seq(
    "/cars.xml",
    "/feed1.xml",
    "/feed2.xml",
    "/feed3.xml",
    "/sample-xbrl-instance.xml",
    "/airportsGermany.xml",
    "/miniXmlBaseTestFile.xml",
    "/xmlBaseTestFile.xml"
  )

  private val saxonProcessor: Processor = Processor(false)

  private def loadXmlFile(classpathResource: String): SaxonNodes.Elem =
    val file = File(classOf[DefaultElemGenerator[?]].getResource(classpathResource).toURI)
    saxonProcessor
      .newDocumentBuilder()
      .build(file)
      .pipe(_.children(isElement.test(_)).iterator.next)
      .pipe(SaxonNodes.Elem(_))

end DefaultElemGenerator
