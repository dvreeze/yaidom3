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
import scala.util.chaining._

import eu.cdevreeze.yaidom3.core.EName
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

  def filterChildElems(p: W => Boolean): Seq[W] = {
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.filterChildElems(underlying, node => p(wrap(node))).map(wrap(_))
  }

  def findAllChildElems: Seq[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findAllChildElems(underlying).map(wrap(_))

  def findChildElem(p: W => Boolean): Option[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findChildElem(underlying, node => p(wrap(node))).map(wrap(_))

  def filterDescendantElems(p: W => Boolean): Seq[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.filterDescendantElems(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllDescendantElems: Seq[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findAllDescendantElems(underlying).map(wrap(_))

  def findDescendantElem(p: W => Boolean): Option[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findDescendantElem(underlying, node => p(wrap(node))).map(wrap(_))

  def filterDescendantElemsOrSelf(p: W => Boolean): Seq[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.filterDescendantElemsOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllDescendantElemsOrSelf: Seq[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findAllDescendantElemsOrSelf(underlying).map(wrap(_))

  def findDescendantElemOrSelf(p: W => Boolean): Option[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findDescendantElemOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findTopmostElems(p: W => Boolean): Seq[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findTopmostElems(underlying, node => p(wrap(node))).map(wrap(_))

  def findTopmostElemsOrSelf(p: W => Boolean): Seq[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findTopmostElemsOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[W] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.findDescendantElemOrSelf(underlying, navigationPath).map(wrap(_))

  def getDescendantElemOrSelf(navigationPath: NavigationPath): W =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.getDescendantElemOrSelf(underlying, navigationPath).pipe(wrap(_))

  def attrOption(attrName: EName): Option[String] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.attrOption(underlying, attrName)

  def text: String =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.text(underlying)

  def normalizedText: String =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.normalizedText(underlying)

  def hasLocalName(localName: String): Boolean =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.hasLocalName(underlying, localName)

  def hasName(namespaceOption: Option[String], localName: String): Boolean =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.hasName(underlying, namespaceOption, localName)

  def hasName(namespace: String, localName: String): Boolean =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.hasName(underlying, namespace, localName)

  def hasName(localName: String): Boolean =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.hasName(underlying, localName)

  def name: EName =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.name(underlying)

  def attrs: ListMap[EName, String] =
    val queryApi: ClarkElemQueryApi[E] = summon[ClarkElemQueryApi[E]]
    queryApi.attrs(underlying)

end ClarkWrapperElem
