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

import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import org.scalacheck.Gen
import org.scalacheck.Properties
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

  def findAllDescendantElemsOrSelf(elem: E): Seq[E] =
    elem.findAllChildElems.flatMap(che => findAllDescendantElemsOrSelf(che)).prepended(elem)

  property("findAllDescendantElemsOrSelf") = forAll(genElem) { (elem: E) =>
    elem.findAllDescendantElemsOrSelf == findAllDescendantElemsOrSelf(elem)
  }

  property("filterDescendantElemsOrSelf") = forAll(genElem, genElemPred) { (elem: E, p: E => Boolean) =>
    elem.filterDescendantElemsOrSelf(p) == elem.findAllDescendantElemsOrSelf.filter(p)
  }

end ClarkElemApiSpecification
