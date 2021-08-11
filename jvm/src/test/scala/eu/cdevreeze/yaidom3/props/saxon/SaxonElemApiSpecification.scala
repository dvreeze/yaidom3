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

package eu.cdevreeze.yaidom3.props.saxon

import scala.language.adhocExtensions

import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.node.saxon.SaxonNodes
import eu.cdevreeze.yaidom3.props.DefaultElemGenerator
import eu.cdevreeze.yaidom3.props.ClarkElemApiSpecification
import eu.cdevreeze.yaidom3.props.CommonElemApiSpecification
import eu.cdevreeze.yaidom3.props.ScopedElemApiSpecification
import org.scalacheck.Properties

/**
 * CommonElemApiSpecification for Saxon wrapper elements.
 *
 * @author
 *   Chris de Vreeze
 */
class SaxonElemApiSpecification
  extends Properties("CommonElemApiSpecification-SaxonElem"),
    CommonElemApiSpecification[SaxonNodes.Elem](SaxonElemApiSpecification.defaultElemGenerator),
    ScopedElemApiSpecification[SaxonNodes.Elem](SaxonElemApiSpecification.defaultElemGenerator),
    ClarkElemApiSpecification[SaxonNodes.Elem](SaxonElemApiSpecification.defaultElemGenerator)

object SaxonElemApiSpecification:

  object defaultElemGenerator extends DefaultElemGenerator[SaxonNodes.Elem]:

    protected def convertToElemType(e: SaxonNodes.Elem): SaxonNodes.Elem = e

  end defaultElemGenerator

end SaxonElemApiSpecification
