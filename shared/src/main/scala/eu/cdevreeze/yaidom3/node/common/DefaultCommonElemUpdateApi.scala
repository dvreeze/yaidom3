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

package eu.cdevreeze.yaidom3.node.common

import eu.cdevreeze.yaidom3.core.Navigation.*
import eu.cdevreeze.yaidom3.node.scoped.DefaultScopedNodes
import eu.cdevreeze.yaidom3.node.scoped.given
import eu.cdevreeze.yaidom3.updateapi.UpdatableCommonElemApi
import eu.cdevreeze.yaidom3.updateapi.UpdatableElemApi

/**
 * UpdatableCommonElemApi type class instance for DefaultCommonNodes.Elem.
 *
 * @author
 *   Chris de Vreeze
 */
given DefaultCommonElemUpdateApi: UpdatableCommonElemApi[DefaultCommonNodes.Elem] with

  type UE = DefaultScopedNodes.Elem

  extension (elem: DefaultCommonNodes.Elem)

    def updatedWithinTree(f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem): DefaultCommonNodes.Elem =
      val ownPath: NavigationPath = elem.elemNavigationPathFromRoot
      val updatedUnderlyingRootElem: DefaultScopedNodes.Elem = elem.underlyingRootElem.updateDescendantElemOrSelf(ownPath)(f)
      DefaultCommonNodes.Elem.of(elem.docUriOption, updatedUnderlyingRootElem, ownPath)
    end updatedWithinTree

    def updateFilteredDescendantsOrSelfWithinTree(p: DefaultCommonNodes.Elem => Boolean)(
        f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem
    ): DefaultCommonNodes.Elem =
      val ownPath: NavigationPath = elem.elemNavigationPathFromRoot
      val paths: Set[NavigationPath] = elem.filterDescendantElemsOrSelf(p).map(_.elemNavigationPathFromRoot).toSet
      val updatedUnderlyingRootElem: DefaultScopedNodes.Elem =
        elem.underlyingRootElem.updateDescendantElemsOrSelf(paths) { (e, path) => f(e) }
      DefaultCommonNodes.Elem.of(elem.docUriOption, updatedUnderlyingRootElem, ownPath)
    end updateFilteredDescendantsOrSelfWithinTree

end DefaultCommonElemUpdateApi
