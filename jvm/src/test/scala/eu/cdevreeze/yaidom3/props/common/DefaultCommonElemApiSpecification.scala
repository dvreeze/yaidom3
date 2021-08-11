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

package eu.cdevreeze.yaidom3.props.common

import scala.language.adhocExtensions

import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.node.common.DefaultCommonNodes
import eu.cdevreeze.yaidom3.node.saxon.SaxonNodes
import eu.cdevreeze.yaidom3.props.DefaultElemGenerator
import eu.cdevreeze.yaidom3.props.ClarkElemApiSpecification
import eu.cdevreeze.yaidom3.props.CommonElemApiSpecification
import eu.cdevreeze.yaidom3.props.ScopedElemApiSpecification
import eu.cdevreeze.yaidom3.props.common.DefaultCommonElemApiSpecification
import org.scalacheck.Properties

/**
 * CommonElemApiSpecification for default Common elements.
 *
 * @author
 *   Chris de Vreeze
 */
class DefaultCommonElemApiSpecification
    extends Properties("CommonElemApiSpecification-DefaultCommonElem"),
      CommonElemApiSpecification[DefaultCommonNodes.Elem](DefaultCommonElemApiSpecification.defaultElemGenerator),
      ScopedElemApiSpecification[DefaultCommonNodes.Elem](DefaultCommonElemApiSpecification.defaultElemGenerator),
      ClarkElemApiSpecification[DefaultCommonNodes.Elem](DefaultCommonElemApiSpecification.defaultElemGenerator)

object DefaultCommonElemApiSpecification:

  object defaultElemGenerator extends DefaultElemGenerator[DefaultCommonNodes.Elem]:

    protected def convertToElemType(e: SaxonNodes.Elem): DefaultCommonNodes.Elem =
      DefaultCommonNodes.Elem.from(e)(using summon[ENameProvider])

  end defaultElemGenerator

end DefaultCommonElemApiSpecification
