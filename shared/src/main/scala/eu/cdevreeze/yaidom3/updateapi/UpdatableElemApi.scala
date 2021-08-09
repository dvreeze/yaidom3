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

package eu.cdevreeze.yaidom3.updateapi

import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation.*
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import scala.collection.immutable.ListMap

/**
 * Type class extending elements with functional updates. This API does not care about placing update results into the surrounding element
 * tree, so as far as this API is concerned, there is no ancestry of the (updated) elements.
 *
 * @author
 *   Chris de Vreeze
 * @tparam E
 *   The element node type
 */
trait UpdatableElemApi[E]:

  /**
   * The node type, which is a super-type of the element type
   */
  type N >: E

  extension (elem: E)

    /**
     * Functionally updates this element. This function is typically used in combination with other methods like `plusChild` etc., typically
     * using the Scope of this element in the creation of the child node to add.
     *
     * If this element has an ancestry, it is ignored in the update. That is, the update result is not placed within the tree containing
     * ancestors (and siblings).
     */
    def updated(f: E => E): E

    // Functions to enhance scopes, and to replace/add child nodes and attributes

    def unsafeEnhanceScopeCompatibly(prefix: Prefix, namespace: Namespace): E

    def unsafeEnhanceScopeCompatibly(prefixNamespaceMap: ListMap[Prefix, Namespace]): E

    def withChildren(childNodes: Seq[N]): E

    def plusChild(child: N): E

    def plusChildOption(childOption: Option[N]): E

    def plusChildren(additionalChildNodes: Seq[N]): E

    def withAttributes(attrsByQName: ListMap[QName, String]): E

    def plusAttribute(attrQName: QName, attrValue: String): E

    def plusAttributeOption(attrQName: QName, attrValueOption: Option[String]): E

    def plusAttributes(additionalAttrsByQName: ListMap[QName, String]): E

    // Transformation functions (element-centric, like most of yaidom3)

    def transformChildElems(f: E => E): E

    def transformChildElemsToNodeSeq(f: E => Seq[N]): E

    def transformDescendantElems(f: E => E): E

    def transformDescendantElemsOrSelf(f: E => E): E

    // Functional updates using navigation paths

    def updateChildElem(navigationStep: NavigationStep)(f: E => E): E

    def updateChildElemWithNodeSeq(navigationStep: NavigationStep)(f: E => Seq[N]): E

    def updateDescendantElemOrSelf(navigationPath: NavigationPath)(f: E => E): E

    def updateChildElems(navigationSteps: Set[NavigationStep])(f: (E, NavigationStep) => E): E

    def updateChildElemsWithNodeSeq(navigationSteps: Set[NavigationStep])(f: (E, NavigationStep) => Seq[N]): E

    def updateDescendantElemsOrSelf(navigationPaths: Set[NavigationPath])(f: (E, NavigationPath) => E): E

end UpdatableElemApi
