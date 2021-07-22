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

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.internal.StringUtil
import eu.cdevreeze.yaidom3.node.internal.DelegatingScopedElemQueryApi
import eu.cdevreeze.yaidom3.node.internal.PartialClarkElem
import eu.cdevreeze.yaidom3.queryapi.Nodes
import eu.cdevreeze.yaidom3.queryapi.ScopedElemApi
import eu.cdevreeze.yaidom3.queryapi.ScopedElemQueryApi

/**
 * "Scoped nodes". See the ScopedElemQueryApi and ScopedElemApi for the API offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
object DefaultScopedNodes extends DelegatingScopedElemQueryApi[DefaultScopedNodes.Elem], ScopedElemQueryApi[DefaultScopedNodes.Elem]:

  sealed trait Node extends Nodes.Node

  final case class Text(value: String, isCData: Boolean) extends Node, Nodes.Text

  final case class Comment(value: String) extends Node, Nodes.Comment

  final case class ProcessingInstruction(target: String, data: String) extends Node, Nodes.ProcessingInstruction

  final case class Elem private (
      qname: QName,
      name: EName,
      attrsByQName: ListMap[QName, String],
      attrs: ListMap[EName, String],
      scope: Scope,
      children: Seq[Node]
  ) extends PartialClarkElem[Elem](name, attrs, children.collect { case e: Elem => e }),
        Node,
        Nodes.Elem,
        ScopedElemApi[Elem]:

    def text: String = children.collect { case t: Text => t.value }.mkString

    def normalizedText: String = StringUtil.normalizeString(text)

    def textAsQName: QName = QName.parse(text.trim)

    def textAsResolvedQName(using enameProvider: ENameProvider): EName =
      scope
        .resolveOption(textAsQName)(using enameProvider)
        .getOrElse(sys.error(s"Could not resolve QName-valued element text '$textAsQName', given scope [$scope]"))

    def attrAsQNameOption(attrName: EName): Option[QName] =
      this.attrOption(attrName).map(v => QName.parse(v.trim))

    def attrAsQName(attrName: EName): QName =
      attrAsQNameOption(attrName).getOrElse(sys.error(s"Missing QName-valued attribute $attrName"))

    def attrAsResolvedQNameOption(attrName: EName)(using enameProvider: ENameProvider): Option[EName] =
      attrAsQNameOption(attrName).map { qn =>
        scope
          .resolveOption(qn)(using enameProvider)
          .getOrElse(sys.error(s"Could not resolve QName-valued attribute value $qn, given scope [$scope]"))
      }

    def attrAsResolvedQName(attrName: EName)(using enameProvider: ENameProvider): EName =
      attrAsResolvedQNameOption(attrName)(using enameProvider).getOrElse(sys.error(s"Missing QName-valued attribute $attrName"))

  object Elem:

    def apply(qname: QName, attrsByQName: ListMap[QName, String], scope: Scope, children: Seq[Node])(using
        enameProvider: ENameProvider
    ): Elem =
      val name: EName = scope
        .resolveOption(qname)(using enameProvider)
        .getOrElse(sys.error(s"Element name '$qname' could not be resolved to an EName in scope [$scope]"))
      val attrs: ListMap[EName, String] = collectAttributes(attrsByQName, scope, (_, _) => true)

      Elem(qname, name, attrsByQName, attrs, scope, children)
    end apply

    def from(otherElem: ScopedElemApi[?] & Nodes.Elem): Elem =
      val children: Seq[Node] = otherElem.children.collect {
        case t: Nodes.Text                      => Text(t.value, isCData = false)
        case c: Nodes.Comment                   => Comment(c.value)
        case pi: Nodes.ProcessingInstruction    => ProcessingInstruction(pi.target, pi.data)
        case e: (ScopedElemApi[?] & Nodes.Elem) =>
          // Recursive call
          from(e)
      }
      Elem(otherElem.qname, otherElem.name, otherElem.attrsByQName, otherElem.attrs, otherElem.scope, children)
    end from

    private def collectAttributes(
        attrsByQName: ListMap[QName, String],
        scope: Scope,
        p: (QName, String) => Boolean
    )(using enameProvider: ENameProvider): ListMap[EName, String] =
      val attrScope: Scope = scope.withoutDefaultNamespace

      attrsByQName.collect {
        case (attrQName, attrValue) if p(attrQName, attrValue) =>
          val attrEName: EName = attrScope
            .resolveOption(attrQName)(using enameProvider)
            .getOrElse(sys.error(s"Attribute name '$attrQName' could not be resolved to an EName in scope [$attrScope]"))

          attrEName -> attrValue
      }
    end collectAttributes

  end Elem

end DefaultScopedNodes
