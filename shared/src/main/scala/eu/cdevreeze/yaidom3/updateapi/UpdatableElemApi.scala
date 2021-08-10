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
     * Functionally updates this element, like `elem.pipe(f)`. This function is typically used in combination with other methods like
     * `plusChild` etc., typically using the Scope of this element in the creation of the child node to add.
     *
     * If this element has an ancestry, it is ignored in the update. That is, the update result is not placed within the tree containing
     * ancestors (and siblings).
     */
    def updated(f: E => E): E

    // Functions to enhance scopes, and to replace/add child nodes and attributes

    /**
     * Updates the Scope by calling function `unsafeAppendCompatibly` with the given prefix and namespace.
     */
    def unsafeEnhanceScopeCompatibly(prefix: Prefix, namespace: Namespace): E

    /**
     * Updates the Scope by calling function `unsafeAppendCompatibly` with the given prefix-namespace mappings.
     */
    def unsafeEnhanceScopeCompatibly(prefixNamespaceMap: ListMap[Prefix, Namespace]): E

    /**
     * Replaces all child nodes by the parameter child node collection.
     */
    def withChildren(childNodes: Seq[N]): E

    def plusChild(child: N): E

    /**
     * Calls function `plusChild` if the optional child is present.
     */
    def plusChildOption(childOption: Option[N]): E

    def plusChildren(additionalChildNodes: Seq[N]): E

    /**
     * Replaces all attributes by the parameter attribute collection.
     */
    def withAttributes(attrsByQName: ListMap[QName, String]): E

    def plusAttribute(attrQName: QName, attrValue: String): E

    /**
     * Calls function `plusAttribute` if the optional attribute value is present.
     */
    def plusAttributeOption(attrQName: QName, attrValueOption: Option[String]): E

    def plusAttributes(additionalAttrsByQName: ListMap[QName, String]): E

    // Transformation functions (element-centric, like most of yaidom3)

    /**
     * Replaces the child element nodes by calling the parameter function on them.
     */
    def transformChildElems(f: E => E): E

    /**
     * Replaces the child element nodes by calling the parameter function on them.
     */
    def transformChildElemsToNodeSeq(f: E => Seq[N]): E

    /**
     * Replaces the descendant element nodes by calling the parameter function on them. This method creates the result element tree in a
     * bottom-up fashion, starting from the leaves.
     */
    def transformDescendantElems(f: E => E): E

    /**
     * Replaces the descendant-or-self element nodes by calling the parameter function on them. This method creates the result element tree
     * in a bottom-up fashion, starting from the leaves.
     */
    def transformDescendantElemsOrSelf(f: E => E): E

    // Functional updates using navigation paths

    /**
     * Replaces the child element at the given relative path (step) by calling the parameter function on it.
     */
    def updateChildElem(navigationStep: NavigationStep)(f: E => E): E

    /**
     * Replaces the child element at the given relative path (step) by calling the parameter function on it.
     */
    def updateChildElemWithNodeSeq(navigationStep: NavigationStep)(f: E => Seq[N]): E

    /**
     * Replaces the descendant-or-self element at the given relative path by calling the parameter function on it.
     */
    def updateDescendantElemOrSelf(navigationPath: NavigationPath)(f: E => E): E

    /**
     * Replaces the child elements at the given relative path steps by calling the parameter function on them. This function works in
     * reverse document order, in order for navigation paths not to become stale before use.
     */
    def updateChildElems(navigationSteps: Set[NavigationStep])(f: (E, NavigationStep) => E): E

    /**
     * Replaces the child elements at the given relative path steps by calling the parameter function on them. This function works in
     * reverse document order, in order for navigation paths not to become stale before use.
     */
    def updateChildElemsWithNodeSeq(navigationSteps: Set[NavigationStep])(f: (E, NavigationStep) => Seq[N]): E

    /**
     * Replaces the descendant-or-self elements at the given relative paths by calling the parameter function on them. This function works
     * in reverse document order, in order for navigation paths not to become stale before use.
     */
    def updateDescendantElemsOrSelf(navigationPaths: Set[NavigationPath])(f: (E, NavigationPath) => E): E

end UpdatableElemApi
