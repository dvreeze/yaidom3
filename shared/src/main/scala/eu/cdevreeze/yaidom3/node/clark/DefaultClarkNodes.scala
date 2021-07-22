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

package eu.cdevreeze.yaidom3.node.clark

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.EName
import eu.cdevreeze.yaidom3.internal.StringUtil
import eu.cdevreeze.yaidom3.node.internal.DelegatingClarkElemQueryApi
import eu.cdevreeze.yaidom3.node.internal.PartialClarkElem
import eu.cdevreeze.yaidom3.queryapi.ClarkElemApi
import eu.cdevreeze.yaidom3.queryapi.ClarkElemQueryApi
import eu.cdevreeze.yaidom3.queryapi.Nodes

/**
 * "Clark nodes". See the ClarkElemQueryApi and ClarkElemApi for the API offered for these nodes.
 *
 * @author
 *   Chris de Vreeze
 */
object DefaultClarkNodes extends DelegatingClarkElemQueryApi[DefaultClarkNodes.Elem], ClarkElemQueryApi[DefaultClarkNodes.Elem]:

  sealed trait Node extends Nodes.Node

  final case class Text(value: String) extends Node, Nodes.Text

  final case class Elem(name: EName, attrs: ListMap[EName, String], children: Seq[Node])
      extends PartialClarkElem[Elem](name, attrs, children.collect { case e: Elem => e }),
        Node,
        Nodes.Elem,
        ClarkElemApi[Elem]:

    def text: String = children.collect { case t: Text => t.value }.mkString

    def normalizedText: String = StringUtil.normalizeString(text)

  object Elem:

    def from(otherElem: ClarkElemApi[?] & Nodes.Elem): Elem =
      val children: Seq[Node] = otherElem.children.collect {
        case t: Nodes.Text                     => Text(t.value)
        case e: (ClarkElemApi[?] & Nodes.Elem) =>
          // Recursive call
          from(e)
      }
      Elem(otherElem.name, otherElem.attrs, children)
    end from

  end Elem

end DefaultClarkNodes
