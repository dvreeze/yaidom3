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

import scala.collection.immutable.ListMap
import scala.language.adhocExtensions
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.queryapi.CommonElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import org.scalacheck.Gen
import org.scalacheck.Properties
import org.scalacheck.Prop.propBoolean
import org.scalacheck.Prop.forAll

/**
 * CommonElemApi properties that must hold for all "common" element implementations.
 *
 * @author
 *   Chris de Vreeze
 */
trait CommonElemApiSpecification[E <: CommonElemApi[E] & Nodes.Elem](elemGenerator: ElemGenerator[E])
    extends Properties,
      ScopedElemApiSpecification[E]:

  import elemGenerator.genElem
  import elemGenerator.genElemPred

  // Ancestor-or-self axis element queries

  def findAllAncestorElemsOrSelf(elem: E): Seq[E] =
    elem.parentElemOption.toSeq.flatMap(pe => findAllAncestorElemsOrSelf(pe)).prepended(elem)

  property("findAllAncestorElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllAncestorElemsOrSelf == findAllAncestorElemsOrSelf(elem)
  }

  property("findAllAncestorElemsOrSelf-as-filterAncestorElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllAncestorElemsOrSelf == elem.filterAncestorElemsOrSelf(_ => true)
  }

  property("filterAncestorElemsOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterAncestorElemsOrSelf(p) == findAllAncestorElemsOrSelf(elem).filter(p)
  }

  property("filterAncestorElemsOrSelf-as-findAllAncestorElemsOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterAncestorElemsOrSelf(p) == elem.findAllAncestorElemsOrSelf.filter(p)
  }

  property("findAncestorElemOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.findAncestorElemOrSelf(p) == elem.filterAncestorElemsOrSelf(p).headOption
  }

  // Ancestor axis element queries

  def findAllAncestorElems(elem: E): Seq[E] =
    elem.parentElemOption.toSeq.flatMap(pe => findAllAncestorElemsOrSelf(pe))

  property("findAllAncestorElems") = forAll(genElem) { (elem: E) =>
    elem.findAllAncestorElems == findAllAncestorElems(elem)
  }

  property("findAllAncestorElems-as-filterAncestorElems") = forAll(genElem) { (elem: E) =>
    elem.findAllAncestorElems == elem.filterAncestorElems(_ => true)
  }

  property("filterAncestorElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterAncestorElems(p) == findAllAncestorElems(elem).filter(p)
  }

  property("filterAncestorElems-as-findAllAncestorElems") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterAncestorElems(p) == elem.findAllAncestorElems.filter(p)
  }

  property("findAncestorElem") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.findAncestorElem(p) == elem.filterAncestorElems(p).headOption
  }

  // Ancestor and ancestor-or-self axis relationships

  property("findAllAncestorElemsOrSelf-related-to-findAllAncestorElems") = forAll(genElem) { (elem: E) =>
    elem.findAllAncestorElemsOrSelf == elem.findAllAncestorElems.prepended(elem)
  }

  property("findAllAncestorElems-related-to-findAllAncestorElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllAncestorElems == elem.findAllAncestorElemsOrSelf.tail
  }

// TODO Remaining methods to test

end CommonElemApiSpecification
