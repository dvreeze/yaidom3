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

import java.net.URI

import scala.collection.immutable.ListMap
import scala.util.chaining._

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.Navigation.NavigationPath
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.queryapi.CommonElemApi
import eu.cdevreeze.yaidom3.queryapi.CommonElemQueryApi

/**
 * Generic wrapper elements, delegating to a query API for underlying elements. See the CommonElemQueryApi and CommonElemApi for the API
 * offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
transparent trait CommonWrapperElem[E: CommonElemQueryApi, W](underlying: E) extends CommonElemApi[W]:

  def wrap(underlying: E): W

  def filterChildElems(p: W => Boolean): Seq[W] = {
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.filterChildElems(underlying, node => p(wrap(node))).map(wrap(_))
  }

  def findAllChildElems: Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAllChildElems(underlying).map(wrap(_))

  def findChildElem(p: W => Boolean): Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findChildElem(underlying, node => p(wrap(node))).map(wrap(_))

  def filterDescendantElems(p: W => Boolean): Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.filterDescendantElems(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllDescendantElems: Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAllDescendantElems(underlying).map(wrap(_))

  def findDescendantElem(p: W => Boolean): Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findDescendantElem(underlying, node => p(wrap(node))).map(wrap(_))

  def filterDescendantElemsOrSelf(p: W => Boolean): Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.filterDescendantElemsOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllDescendantElemsOrSelf: Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAllDescendantElemsOrSelf(underlying).map(wrap(_))

  def findDescendantElemOrSelf(p: W => Boolean): Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findDescendantElemOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findTopmostElems(p: W => Boolean): Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findTopmostElems(underlying, node => p(wrap(node))).map(wrap(_))

  def findTopmostElemsOrSelf(p: W => Boolean): Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findTopmostElemsOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findDescendantElemOrSelf(navigationPath: NavigationPath): Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findDescendantElemOrSelf(underlying, navigationPath).map(wrap(_))

  def getDescendantElemOrSelf(navigationPath: NavigationPath): W =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.getDescendantElemOrSelf(underlying, navigationPath).pipe(wrap(_))

  def attrOption(attrName: EName): Option[String] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.attrOption(underlying, attrName)

  def text: String =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.text(underlying)

  def normalizedText: String =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.normalizedText(underlying)

  def hasLocalName(localName: String): Boolean =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.hasLocalName(underlying, localName)

  def hasName(namespaceOption: Option[String], localName: String): Boolean =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.hasName(underlying, namespaceOption, localName)

  def hasName(namespace: String, localName: String): Boolean =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.hasName(underlying, namespace, localName)

  def hasName(localName: String): Boolean =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.hasName(underlying, localName)

  def name: EName =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.name(underlying)

  def attrs: ListMap[EName, String] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.attrs(underlying)

  def scope: Scope =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.scope(underlying)

  def qname: QName =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.qname(underlying)

  def attrsByQName: ListMap[QName, String] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.attrsByQName(underlying)

  def textAsQName: QName =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.textAsQName(underlying)

  def textAsResolvedQName(using enameProvider: ENameProvider): EName =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.textAsResolvedQName(underlying)(using enameProvider)

  def attrAsQNameOption(attrName: EName): Option[QName] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.attrAsQNameOption(underlying, attrName)

  def attrAsQName(attrName: EName): QName =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.attrAsQName(underlying, attrName)

  def attrAsResolvedQNameOption(attrName: EName)(using enameProvider: ENameProvider): Option[EName] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.attrAsResolvedQNameOption(underlying, attrName)(using enameProvider)

  def attrAsResolvedQName(attrName: EName)(using enameProvider: ENameProvider): EName =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.attrAsResolvedQName(underlying, attrName)(using enameProvider)

  def findParentElem(p: W => Boolean): Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findParentElem(underlying, node => p(wrap(node))).map(wrap(_))

  def findParentElem: Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findParentElem(underlying).map(wrap(_))

  def filterAncestorElems(p: W => Boolean): Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.filterAncestorElems(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllAncestorElems: Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAllAncestorElems(underlying).map(wrap(_))

  def findAncestorElem(p: W => Boolean): Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAncestorElem(underlying, node => p(wrap(node))).map(wrap(_))

  def filterAncestorElemsOrSelf(p: W => Boolean): Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.filterAncestorElemsOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllAncestorElemsOrSelf: Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAllAncestorElemsOrSelf(underlying).map(wrap(_))

  def findAncestorElemOrSelf(p: W => Boolean): Option[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAncestorElemOrSelf(underlying, node => p(wrap(node))).map(wrap(_))

  def findAllPrecedingSiblingElems: Seq[W] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.findAllPrecedingSiblingElems(underlying).map(wrap(_))

  def ownNavigationPathRelativeToRootElem: NavigationPath =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.ownNavigationPathRelativeToRootElem(underlying)

  def baseUriOption: Option[URI] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.baseUriOption(underlying)

  def baseUri: URI =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.baseUri(underlying)

  def docUriOption: Option[URI] =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.docUriOption(underlying)

  def docUri: URI =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.docUri(underlying)

  def rootElem: W =
    val queryApi: CommonElemQueryApi[E] = summon[CommonElemQueryApi[E]]
    queryApi.rootElem(underlying).pipe(wrap(_))

end CommonWrapperElem
