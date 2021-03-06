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

package eu.cdevreeze.yaidom3.node.internal

import scala.collection.immutable.ListMap
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.Namespaces.LocalName
import eu.cdevreeze.yaidom3.core.Namespaces.Namespace
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.ClarkElemQueryApi

/**
 * Generic wrapper elements, delegating to a query API for underlying elements. See the ClarkElemQueryApi and ClarkElemApi for the API
 * offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
transparent trait ClarkWrapperElem[E: ClarkElemQueryApi, W](underlying: E) extends ClarkElemApi[W]:

  def wrap(underlying: E): W

  private def queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]

  def filterChildElems(p: W => Boolean): Seq[W] =
    queryApi.filterChildElems(underlying, wrap.andThen(p)).map(wrap)

  def findAllChildElems: Seq[W] = queryApi.findAllChildElems(underlying).map(wrap)

  def findChildElem(p: W => Boolean): Option[W] =
    queryApi.findChildElem(underlying, wrap.andThen(p)).map(wrap)

  def filterDescendantElems(p: W => Boolean): Seq[W] =
    queryApi.filterDescendantElems(underlying, wrap.andThen(p)).map(wrap)

  def findAllDescendantElems: Seq[W] =
    queryApi.findAllDescendantElems(underlying).map(wrap)

  def findDescendantElem(p: W => Boolean): Option[W] =
    queryApi.findDescendantElem(underlying, wrap.andThen(p)).map(wrap)

  def filterDescendantElemsOrSelf(p: W => Boolean): Seq[W] =
    queryApi.filterDescendantElemsOrSelf(underlying, wrap.andThen(p)).map(wrap)

  def findAllDescendantElemsOrSelf: Seq[W] =
    queryApi.findAllDescendantElemsOrSelf(underlying).map(wrap)

  def findDescendantElemOrSelf(p: W => Boolean): Option[W] =
    queryApi.findDescendantElemOrSelf(underlying, wrap.andThen(p)).map(wrap)

  def findTopmostElems(p: W => Boolean): Seq[W] =
    queryApi.findTopmostElems(underlying, wrap.andThen(p)).map(wrap)

  def findTopmostElemsOrSelf(p: W => Boolean): Seq[W] =
    queryApi.findTopmostElemsOrSelf(underlying, wrap.andThen(p)).map(wrap)

  def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[W] =
    queryApi.findDescendantElemOrSelf(underlying, navigationPath).map(wrap)

  def getDescendantElemOrSelf(navigationPath: NavigationPath): W =
    queryApi.getDescendantElemOrSelf(underlying, navigationPath).pipe(wrap)

  def attrOption(attrName: EName): Option[String] = queryApi.attrOption(underlying, attrName)

  def attr(attrName: EName): String = queryApi.attr(underlying, attrName)

  def text: String = queryApi.text(underlying)

  def normalizedText: String = queryApi.normalizedText(underlying)

  def hasLocalName(localName: LocalName): Boolean = queryApi.hasLocalName(underlying, localName)

  def hasName(name: EName): Boolean = queryApi.hasName(underlying, name)

  def hasName(namespaceOption: Option[Namespace], localName: LocalName): Boolean =
    queryApi.hasName(underlying, namespaceOption, localName)

  def hasName(namespace: Namespace, localName: LocalName): Boolean =
    queryApi.hasName(underlying, namespace, localName)

  def name: EName = queryApi.name(underlying)

  def attrs: ListMap[EName, String] = queryApi.attrs(underlying)

end ClarkWrapperElem
