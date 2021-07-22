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
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.queryapi.ScopedElemApi
import eu.cdevreeze.yaidom3.queryapi.ScopedElemQueryApi

/**
 * Generic wrapper elements, delegating to a query API for underlying elements. See the ScopedElemQueryApi and ScopedElemApi for the API
 * offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
transparent trait ScopedWrapperElem[E: ScopedElemQueryApi, W](underlying: E) extends ScopedElemApi[W]:

  def wrap(underlying: E): W

  def filterChildElems(p: W => Boolean): Seq[W] = {
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.filterChildElems(underlying, node => p(wrap(node))).map(wrap(_))
  }

  def findAllChildElems: Seq[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findAllChildElems(underlying).map(wrap(_))

  def findChildElem(p: W => Boolean): Option[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findChildElem(underlying, node => p(wrap(node))).map(wrap(_))

  def filterDescendantElems(p: W => Boolean): Seq[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.filterDescendantElems(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllDescendantElems: Seq[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findAllDescendantElems(underlying).map(wrap(_))

  def findDescendantElem(p: W => Boolean): Option[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findDescendantElem(underlying, node => p(wrap(node))).map(wrap(_))

  def filterDescendantElemsOrSelf(p: W => Boolean): Seq[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.filterDescendantElemsOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllDescendantElemsOrSelf: Seq[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findAllDescendantElemsOrSelf(underlying).map(wrap(_))

  def findDescendantElemOrSelf(p: W => Boolean): Option[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findDescendantElemOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findTopmostElems(p: W => Boolean): Seq[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findTopmostElems(underlying, node => p(wrap(node))).map(wrap(_))

  def findTopmostElemsOrSelf(p: W => Boolean): Seq[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findTopmostElemsOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[W] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.findDescendantElemOrSelf(underlying, navigationPath).map(wrap(_))

  def getDescendantElemOrSelf(navigationPath: NavigationPath): W =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.getDescendantElemOrSelf(underlying, navigationPath).pipe(wrap(_))

  def attrOption(attrName: EName): Option[String] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.attrOption(underlying, attrName)

  def text: String =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.text(underlying)

  def normalizedText: String =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.normalizedText(underlying)

  def hasLocalName(localName: String): Boolean =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.hasLocalName(underlying, localName)

  def hasName(namespaceOption: Option[String], localName: String): Boolean =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.hasName(underlying, namespaceOption, localName)

  def hasName(namespace: String, localName: String): Boolean =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.hasName(underlying, namespace, localName)

  def hasName(localName: String): Boolean =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.hasName(underlying, localName)

  def name: EName =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.name(underlying)

  def attrs: ListMap[EName, String] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.attrs(underlying)

  def scope: Scope =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.scope(underlying)

  def qname: QName =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.qname(underlying)

  def attrsByQName: ListMap[QName, String] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.attrsByQName(underlying)

  def textAsQName: QName =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.textAsQName(underlying)

  def textAsResolvedQName(using enameProvider: ENameProvider): EName =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.textAsResolvedQName(underlying)(using enameProvider)

  def attrAsQNameOption(attrName: EName): Option[QName] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.attrAsQNameOption(underlying, attrName)

  def attrAsQName(attrName: EName): QName =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.attrAsQName(underlying, attrName)

  def attrAsResolvedQNameOption(attrName: EName)(using enameProvider: ENameProvider): Option[EName] =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.attrAsResolvedQNameOption(underlying, attrName)(using enameProvider)

  def attrAsResolvedQName(attrName: EName)(using enameProvider: ENameProvider): EName =
    val queryApi: ScopedElemQueryApi[E] = summon[ScopedElemQueryApi[E]]
    queryApi.attrAsResolvedQName(underlying, attrName)(using enameProvider)

end ScopedWrapperElem
