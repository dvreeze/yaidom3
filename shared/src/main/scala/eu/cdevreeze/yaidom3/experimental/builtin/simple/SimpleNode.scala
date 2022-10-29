/*
 * Copyright 2022-2022 Chris de Vreeze
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

package eu.cdevreeze.yaidom3.experimental.builtin.simple

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.experimental.core.QName
import eu.cdevreeze.yaidom3.experimental.core.EName
import eu.cdevreeze.yaidom3.experimental.core.Scope
import eu.cdevreeze.yaidom3.experimental.internal.StringUtil
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStep

/**
 * Simple nodes, where its element nodes do not know their ancestry.
 *
 * @author
 *   Chris de Vreeze
 */
enum SimpleNode:

  case Text(textString: String)

  case Comment(textString: String)

  case ProcessingInstruction(target: String, data: String)

  case Elem(
      val qname: QName,
      val attrsByQName: ListMap[QName, String],
      val scope: Scope,
      val children: Seq[SimpleNode]
  ) extends SimpleNode, ElemApi[Elem, Elem]

    // Unpolished implementation

    type E = Elem

    private def thisElem: Elem = SimpleNode.this.asInstanceOf[Elem]

    def underlying: Elem = thisElem

    def unwrap: Elem = underlying

    def selectElems(step: ElemStep[Elem]): Seq[Elem] =
      step(thisElem)

    def selectUnderlyingElems(step: ElemStep[Elem]): Seq[Elem] =
      selectElems(step)

    def name: EName = thisElem.scope.resolve(thisElem.qname)

    def attrs: ListMap[EName, String] =
      val attrScope = thisElem.scope.withoutDefaultNamespace
      thisElem.attrsByQName.map { (attrQName, attrValue) =>
        attrScope.resolve(attrQName) -> attrValue
      }

    def attrOption(attrName: EName): Option[String] = attrs.get(attrName)

    def attrOption(attrNoNsName: String): Option[String] = attrOption(EName.of(attrNoNsName))

    def attr(attrName: EName): String = attrOption(attrName).get

    def attr(attrNoNsName: String): String = attrOption(attrNoNsName).get

    def text: String = thisElem.children.collect { case t: SimpleNode.Text => t.textString }.mkString

    def normalizedText: String = StringUtil.normalizeString(text)

    def hasLocalName(localName: String): Boolean = thisElem.qname.localPart == localName

    def hasName(name: EName): Boolean = thisElem.name == name

    def hasName(namespaceOption: Option[String], localName: String): Boolean =
      name.namespaceOption == namespaceOption && name.localPart == localName

    def hasName(namespace: String, localName: String): Boolean = hasName(Some(namespace), localName)

    def textAsQName: QName = QName.parse(text)

    def textAsResolvedQName: EName = thisElem.scope.resolve(textAsQName)

    def attrAsQNameOption(attrName: EName): Option[QName] = attrOption(attrName).map(QName.parse)

    def attrAsQName(attrName: EName): QName = attrAsQNameOption(attrName).get

    def attrAsResolvedQNameOption(attrName: EName): Option[EName] =
      attrAsQNameOption(attrName).map(qn => thisElem.scope.resolve(qn))

    def attrAsResolvedQName(attrName: EName): EName = attrAsResolvedQNameOption(attrName).get

end SimpleNode
