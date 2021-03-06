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

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.Nodes
import org.scalacheck.Gen

/**
 * Element and element predicate generator, to be used in Scalacheck properties.
 *
 * @author
 *   Chris de Vreeze
 */
trait ElemGenerator[E <: ClarkElemApi[E] & Nodes.Elem]:

  def genElem: Gen[E]

  def genElemPred: Gen[E => Boolean]

  def genElemName: Gen[EName]

  def genElemLocalName: Gen[LocalName]

  def genNavigationPath: Gen[NavigationPath]

end ElemGenerator
