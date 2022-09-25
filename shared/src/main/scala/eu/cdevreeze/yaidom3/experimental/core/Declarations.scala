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

package eu.cdevreeze.yaidom3.experimental.core

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.experimental.core.Declarations.OptNamespace
import eu.cdevreeze.yaidom3.experimental.core.Declarations.XmlNamespace
import eu.cdevreeze.yaidom3.experimental.core.Declarations.XmlPrefix

/**
 * Namespace declarations, which may include namespace undeclarations. The "xml" namespace is not part of the namespace (un)declarations,
 * since it is implicitly always there.
 *
 * @author
 *   Chris de Vreeze
 */
final case class Declarations private (
  prefixedDeclarationMap: ListMap[String, OptNamespace],
  defaultNamespaceDeclarationOption: Option[OptNamespace]
):
  assert(!prefixedDeclarationMap.contains(XmlPrefix))

  def isEmpty: Boolean = prefixedDeclarationMap.isEmpty && defaultNamespaceDeclarationOption.isEmpty

  def nonEmpty: Boolean = !isEmpty

  def retainingUndeclarations: Declarations =
    Declarations(prefixedDeclarationMap.filter(_._2.isEmpty), defaultNamespaceDeclarationOption.filter(_.isEmpty))

  def withoutUndeclarations: Declarations =
    Declarations(prefixedDeclarationMap.filter(_._2.nonEmpty), defaultNamespaceDeclarationOption.filter(_.nonEmpty))

  def retainingDefaultNamespaceDeclaration: Declarations = Declarations(ListMap.empty, defaultNamespaceDeclarationOption)

  def withoutDefaultNamespaceDeclaration: Declarations = Declarations(prefixedDeclarationMap, None)

  def append(otherDeclarations: Declarations): Declarations =
    Declarations(
      prefixedDeclarationMap.concat(otherDeclarations.prefixedDeclarationMap),
      otherDeclarations.defaultNamespaceDeclarationOption.orElse(defaultNamespaceDeclarationOption)
    )

  def prepend(otherDeclarations: Declarations): Declarations = otherDeclarations.append(this)

object Declarations:

  val XmlNamespace = "http://www.w3.org/XML/1998/namespace"
  val XmlPrefix = "xml"

  opaque type OptNamespace = Option[String]

  object OptNamespace:

    def of(namespace: String): OptNamespace = Some(namespace)

    def empty: OptNamespace = None

    def apply(namespaceString: String): OptNamespace = if namespaceString.isEmpty then empty else of(namespaceString)

  end OptNamespace

  extension (optNamespace: OptNamespace)
    def namespaceOption: Option[String] = optNamespace
    def namespace: String = optNamespace.get
    def isEmpty: Boolean = optNamespace.isEmpty
    def nonEmpty: Boolean = optNamespace.nonEmpty

  val empty: Declarations = Declarations(ListMap.empty, None)

  def from(prefixedDeclarationMap: ListMap[String, OptNamespace], defaultNamespaceDeclarationOption: Option[OptNamespace]): Declarations =
    apply(prefixedDeclarationMap.filterNot(_._1 == XmlPrefix), defaultNamespaceDeclarationOption)

  // TODO Parse from strings

end Declarations
