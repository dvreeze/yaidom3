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

package eu.cdevreeze.yaidom3.node.scoped

import scala.collection.immutable.ListMap
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.Namespaces.Namespace
import eu.cdevreeze.yaidom3.core.Namespaces.Prefix
import eu.cdevreeze.yaidom3.core.Navigation.*
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.updateapi.UpdatableElemApi

/**
 * UpdatableElemApi type class instance for DefaultScopedNodes.Elem.
 *
 * @author
 *   Chris de Vreeze
 */
given DefaultScopedElemUpdateApi: UpdatableElemApi[DefaultScopedNodes.Elem] with

  type N = DefaultScopedNodes.Node

  extension (elem: DefaultScopedNodes.Elem)

    def updated(f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem): DefaultScopedNodes.Elem = elem.pipe(f)

    // Functions to enhance scopes, and to replace/add child nodes and attributes

    def unsafeEnhanceScopeCompatibly(prefix: Prefix, namespace: Namespace): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, elem.attrsByQName, elem.scope.unsafeAppendCompatibly(prefix, namespace), elem.children)

    def unsafeEnhanceScopeCompatibly(prefixNamespaceMap: ListMap[Prefix, Namespace]): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, elem.attrsByQName, elem.scope.unsafeAppendCompatibly(prefixNamespaceMap), elem.children)

    def withChildren(childNodes: Seq[N]): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, elem.attrsByQName, elem.scope, childNodes)

    def plusChild(child: N): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, elem.attrsByQName, elem.scope, elem.children.appended(child))

    def plusChildOption(childOption: Option[N]): DefaultScopedNodes.Elem =
      childOption.map(ch => plusChild(ch)).getOrElse(elem)

    def plusChildren(additionalChildNodes: Seq[N]): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, elem.attrsByQName, elem.scope, elem.children.appendedAll(additionalChildNodes))

    def withAttributes(attrsByQName: ListMap[QName, String]): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, attrsByQName, elem.scope, elem.children)

    def plusAttribute(attrQName: QName, attrValue: String): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, elem.attrsByQName.updated(attrQName, attrValue), elem.scope, elem.children)

    def plusAttributeOption(attrQName: QName, attrValueOption: Option[String]): DefaultScopedNodes.Elem =
      attrValueOption.map(attrValue => plusAttribute(attrQName, attrValue)).getOrElse(elem)

    def plusAttributes(additionalAttrsByQName: ListMap[QName, String]): DefaultScopedNodes.Elem =
      DefaultScopedNodes.Elem(elem.qname, elem.attrsByQName.concat(additionalAttrsByQName), elem.scope, elem.children)

    // Transformation functions (element-centric, like most of yaidom3)

    def transformChildElems(f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem): DefaultScopedNodes.Elem =
      val updatedChildren = elem.children.map {
        case che: DefaultScopedNodes.Elem => f(che)
        case n                            => n
      }
      withChildren(updatedChildren)
    end transformChildElems

    def transformChildElemsToNodeSeq(f: DefaultScopedNodes.Elem => Seq[N]): DefaultScopedNodes.Elem =
      val updatedChildren = elem.children.flatMap {
        case che: DefaultScopedNodes.Elem => f(che)
        case n                            => Seq(n)
      }
      withChildren(updatedChildren)
    end transformChildElemsToNodeSeq

    def transformDescendantElems(f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem): DefaultScopedNodes.Elem =
      transformChildElems(_.transformDescendantElemsOrSelf(f))

    def transformDescendantElemsOrSelf(f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem): DefaultScopedNodes.Elem =
      transformChildElems(_.transformDescendantElemsOrSelf(f)).pipe(f)

    // Functional updates using navigation paths

    def updateChildElem(navigationStep: NavigationStep)(f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem): DefaultScopedNodes.Elem =
      val childNodeIndex: Int = convertNavigationStepToChildNodeIndex(navigationStep)
      val updatedChildElem: DefaultScopedNodes.Elem = f(elem.children(childNodeIndex).asInstanceOf[DefaultScopedNodes.Elem])
      withChildren(elem.children.updated(childNodeIndex, updatedChildElem))
    end updateChildElem

    def updateChildElemWithNodeSeq(navigationStep: NavigationStep)(f: DefaultScopedNodes.Elem => Seq[N]): DefaultScopedNodes.Elem =
      val childNodeIndex: Int = convertNavigationStepToChildNodeIndex(navigationStep)
      val updatedChildNodes: Seq[DefaultScopedNodes.Node] = f(elem.children(childNodeIndex).asInstanceOf[DefaultScopedNodes.Elem])
      withChildren(elem.children.patch(childNodeIndex, updatedChildNodes, 1))
    end updateChildElemWithNodeSeq

    def updateDescendantElemOrSelf(navigationPath: NavigationPath)(
        f: DefaultScopedNodes.Elem => DefaultScopedNodes.Elem
    ): DefaultScopedNodes.Elem =
      updateDescendantElemsOrSelf(Set(navigationPath)) { (e: DefaultScopedNodes.Elem, _) => f(e) }

    def updateChildElems(navigationSteps: Set[NavigationStep])(
        f: (DefaultScopedNodes.Elem, NavigationStep) => DefaultScopedNodes.Elem
    ): DefaultScopedNodes.Elem =
      val childNodeIndexMap: ListMap[NavigationStep, Int] = convertNavigationStepsToChildNodeIndices(navigationSteps)
      val updatedChildren: Seq[DefaultScopedNodes.Node] = childNodeIndexMap.foldLeft(elem.children) { (accChildren, mapping) =>
        val (navStep, childNodeIdx) = mapping
        val childElem: DefaultScopedNodes.Elem = accChildren(childNodeIdx).asInstanceOf[DefaultScopedNodes.Elem]
        accChildren.updated(childNodeIdx, f(childElem, navStep))
      }
      withChildren(updatedChildren)
    end updateChildElems

    def updateChildElemsWithNodeSeq(navigationSteps: Set[NavigationStep])(
        f: (DefaultScopedNodes.Elem, NavigationStep) => Seq[N]
    ): DefaultScopedNodes.Elem =
      // The right-to-left index order is essential here, or else they may become invalid before use
      val childNodeIndexMap: ListMap[NavigationStep, Int] = convertNavigationStepsToChildNodeIndices(navigationSteps)
      val updatedChildren: Seq[DefaultScopedNodes.Node] = childNodeIndexMap.foldLeft(elem.children) { (accChildren, mapping) =>
        val (navStep, childNodeIdx) = mapping
        val childElem: DefaultScopedNodes.Elem = accChildren(childNodeIdx).asInstanceOf[DefaultScopedNodes.Elem]
        accChildren.patch(childNodeIdx, f(childElem, navStep), 1)
      }
      withChildren(updatedChildren)
    end updateChildElemsWithNodeSeq

    def updateDescendantElemsOrSelf(navigationPaths: Set[NavigationPath])(
        f: (DefaultScopedNodes.Elem, NavigationPath) => DefaultScopedNodes.Elem
    ): DefaultScopedNodes.Elem =
      val navigationPathsByFirstStep: Map[NavigationStep, Set[NavigationPath]] =
        navigationPaths.filter(_.nonEmpty).groupBy(_.head)

      val descendantUpdateResult: DefaultScopedNodes.Elem =
        updateChildElems(navigationPathsByFirstStep.keySet) { (che, step) =>
          // Recursive (but not tail-recursive) call
          che.updateDescendantElemsOrSelf(navigationPathsByFirstStep(step).map(_.tail)) { (de, path) =>
            f(de, path.prepended(step))
          }
        }

      if navigationPaths.contains(NavigationPath.empty) then f(descendantUpdateResult, NavigationPath.empty) else descendantUpdateResult
    end updateDescendantElemsOrSelf

    private def convertNavigationStepToChildNodeIndex(navigationStep: NavigationStep): Int =
      var childNodeIdx = -1
      var childElemIdx = -1
      val numberOfChildren = elem.children.size

      while childElemIdx < navigationStep.toInt do
        require(childNodeIdx < numberOfChildren, s"Navigation step out of bounds")
        childNodeIdx += 1
        if elem.children(childNodeIdx).isInstanceOf[DefaultScopedNodes.Elem] then childElemIdx += 1
      end while

      childNodeIdx
    end convertNavigationStepToChildNodeIndex

    private def convertNavigationStepsToChildNodeIndices(navigationSteps: Set[NavigationStep]): ListMap[NavigationStep, Int] =
      if navigationSteps.isEmpty then ListMap.empty
      else
        var childNodeIdx = -1
        var childElemIdx = -1
        val numberOfChildren = elem.children.size
        val maxChildElemIdx = navigationSteps.maxBy(_.toInt).toInt
        var mappings: List[(NavigationStep, Int)] = Nil

        while childElemIdx < maxChildElemIdx do
          require(childNodeIdx < numberOfChildren, s"Navigation step out of bounds")
          childNodeIdx += 1
          if elem.children(childNodeIdx).isInstanceOf[DefaultScopedNodes.Elem] then
            childElemIdx += 1
            if navigationSteps.contains(NavigationStep(childElemIdx)) then
              mappings = (NavigationStep(childElemIdx), childNodeIdx) :: mappings
          end if
        end while
        mappings.to(ListMap) // Note the right-to-left order, which is essential for correct updates
      end if
    end convertNavigationStepsToChildNodeIndices

end DefaultScopedElemUpdateApi
