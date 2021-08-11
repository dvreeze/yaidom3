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
import eu.cdevreeze.yaidom3.queryapi.ScopedElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import org.scalacheck.Gen
import org.scalacheck.Properties
import org.scalacheck.Prop.propBoolean
import org.scalacheck.Prop.forAll

/**
 * ScopedElemApi properties that must hold for all "scoped" element implementations.
 *
 * @author
 *   Chris de Vreeze
 */
trait ScopedElemApiSpecification[E <: ScopedElemApi[E] & Nodes.Elem](elemGenerator: ElemGenerator[E])
    extends Properties,
      ClarkElemApiSpecification[E]:

  import elemGenerator.genElem

  // QName resolution queries

  property("resolve-QName") = forAll(genElem) { (elem: E) =>
    elem.scope.resolve(elem.qname)(using summon[ENameProvider]) == elem.name
  }

  property("resolve-attributes") = forAll(genElem) { (elem: E) =>
    val attrScope = elem.scope.withoutDefaultNamespace
    val resolvedAttrs: ListMap[EName, String] =
      elem.attrsByQName.toSeq.map { (qn, value) => attrScope.resolve(qn)(using summon[ENameProvider]) -> value }.to(ListMap)
    resolvedAttrs == elem.attrs
  }

end ScopedElemApiSpecification
