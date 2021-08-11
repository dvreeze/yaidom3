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

import scala.language.adhocExtensions
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import org.scalacheck.Gen
import org.scalacheck.Properties
import org.scalacheck.Prop.propBoolean
import org.scalacheck.Prop.forAll

/**
 * ClarkElemApi properties that must hold for all element implementations.
 *
 * @author
 *   Chris de Vreeze
 */
trait ClarkElemApiSpecification[E <: ClarkElemApi[E] & Nodes.Elem](elemGenerator: ElemGenerator[E]) extends Properties:

  import elemGenerator.genElem
  import elemGenerator.genElemPred
  import elemGenerator.genElemName
  import elemGenerator.genElemLocalName

  // Child axis element queries

  property("findAllChildElems") = forAll(genElem) { (elem: E) =>
    elem.findAllChildElems == elem.children.collect { case e: ClarkElemApi[?] => e }
  }

  property("findAllChildElems-as-filterChildElems") = forAll(genElem) { (elem: E) =>
    elem.findAllChildElems == elem.filterChildElems(_ => true)
  }

  property("filterChildElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterChildElems(p) == elem.children.collect { case e: ClarkElemApi[?] if p(e.asInstanceOf[E]) => e }
  }

  property("filterChildElems-as-findAllChildElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterChildElems(p) == elem.findAllChildElems.filter(p)
  }

  property("findChildElem") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.findChildElem(p) == elem.filterChildElems(p).headOption
  }

  // Descendant-or-self axis element queries

  def findAllDescendantElemsOrSelf(elem: E): Seq[E] =
    elem.findAllChildElems.flatMap(che => findAllDescendantElemsOrSelf(che)).prepended(elem)

  property("findAllDescendantElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElemsOrSelf == findAllDescendantElemsOrSelf(elem)
  }

  property("findAllDescendantElemsOrSelf-as-filterDescendantElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElemsOrSelf == elem.filterDescendantElemsOrSelf(_ => true)
  }

  property("filterDescendantElemsOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterDescendantElemsOrSelf(p) == findAllDescendantElemsOrSelf(elem).filter(p)
  }

  property("filterDescendantElemsOrSelf-as-findAllDescendantElemsOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterDescendantElemsOrSelf(p) == elem.findAllDescendantElemsOrSelf.filter(p)
  }

  property("findDescendantElemOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.findDescendantElemOrSelf(p) == elem.filterDescendantElemsOrSelf(p).headOption
  }

  // Descendant axis element queries

  def findAllDescendantElems(elem: E): Seq[E] =
    elem.findAllChildElems.flatMap(che => findAllDescendantElemsOrSelf(che))

  property("findAllDescendantElems") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElems == findAllDescendantElems(elem)
  }

  property("findAllDescendantElems-as-filterDescendantElems") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElems == elem.filterDescendantElems(_ => true)
  }

  property("filterDescendantElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterDescendantElems(p) == findAllDescendantElems(elem).filter(p)
  }

  property("filterDescendantElems-as-findAllDescendantElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterDescendantElems(p) == elem.findAllDescendantElems.filter(p)
  }

  property("findDescendantElem") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.findDescendantElem(p) == elem.filterDescendantElems(p).headOption
  }

  // Descendant and descendant-or-self axis relationships

  property("findAllDescendantElemsOrSelf-related-to-findAllDescendantElems") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElemsOrSelf == elem.findAllDescendantElems.prepended(elem)
  }

  property("findAllDescendantElems-related-to-findAllDescendantElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElems == elem.findAllDescendantElemsOrSelf.tail
  }

  // Topmost descendant-or-self axis element queries

  def findTopmostElemsOrSelf(elem: E, p: E => Boolean): Seq[E] =
    if p(elem) then Seq(elem) else elem.findAllChildElems.flatMap(che => findTopmostElemsOrSelf(che, p))

  property("findTopmostElemsOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.findTopmostElemsOrSelf(p) == findTopmostElemsOrSelf(elem, p)
  }

  // Topmost descendant axis element queries

  def findTopmostElems(elem: E, p: E => Boolean): Seq[E] =
    elem.findAllChildElems.flatMap(che => findTopmostElemsOrSelf(che, p))

  property("findTopmostElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.findTopmostElems(p) == findTopmostElems(elem, p)
  }

  // Relationships involving topmost (descendant/descendant-or-self) elements

  property("findAllDescendantElemsOrSelf-related-to-findTopmostElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElemsOrSelf == elem.findTopmostElemsOrSelf(_ => true).flatMap(_.findAllDescendantElemsOrSelf)
  }

  property("findAllDescendantElems-related-to-findTopmostElems") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElems == elem.findTopmostElems(_ => true).flatMap(_.findAllDescendantElemsOrSelf)
  }

  property("filterDescendantElemsOrSelf-related-to-findTopmostElemsOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterDescendantElemsOrSelf(p) == elem.findTopmostElemsOrSelf(p).flatMap(_.filterDescendantElemsOrSelf(p))
  }

  property("filterDescendantElems-related-to-findTopmostElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterDescendantElems(p) == elem.findTopmostElems(p).flatMap(_.filterDescendantElemsOrSelf(p))
  }

  // Querying for elements based on navigation paths not tested here, but tested for "common elements"

  // Name-based queries

  property("hasName-passing-EName") = forAll(genElem, genElemName) { (elem: E, name: EName) =>
    elem.hasName(name) == (elem.name == name)
  }

  property("hasName-passing-opt-namespace-and-localname") = forAll(genElem, genElemName) { (elem: E, name: EName) =>
    elem.hasName(name.namespaceOption, name.localPart.toString) == (elem.name == name)
  }

  property("hasName-passing-namespace-and-localname") = forAll(genElem, genElemName) { (elem: E, name: EName) =>
    name.namespaceOption.nonEmpty ==> { elem.hasName(name.namespaceOption.get, name.localPart.toString) == (elem.name == name) }
  }

  property("hasLocalName") = forAll(genElem, genElemLocalName) { (elem: E, localName: LocalName) =>
    elem.hasLocalName(localName.toString) == (elem.name.localPart == localName)
  }

  // TODO Attribute queries

  def text(elem: E): String = elem.children.collect { case t: Nodes.Text => t.value }.mkString

  property("text") = forAll(genElem) { (elem: E) =>
    elem.text == text(elem)
  }

end ClarkElemApiSpecification
