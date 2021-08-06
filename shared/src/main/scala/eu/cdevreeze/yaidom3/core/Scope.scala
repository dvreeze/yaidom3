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

package eu.cdevreeze.yaidom3.core

import scala.collection.immutable.ListMap

import eu.cdevreeze.yaidom3.core.Namespaces.*

/**
 * In-scope namespaces. The "xml" namespace is not part of the in-scope namespaces, since it is implicitly always there.
 *
 * In an XML document, the in-scope namespaces of an element in that document are the net combined result of the namespace (un)declarations
 * of the element (if any) and of all its ancestor elements.
 *
 * @author
 *   Chris de Vreeze
 */
final case class Scope private (prefixedNamespaceMap: ListMap[Prefix, Namespace], defaultNamespaceOption: Option[Namespace]):
  assert(!prefixedNamespaceMap.contains(XmlPrefix))

  def isEmpty: Boolean = prefixedNamespaceMap.isEmpty && defaultNamespaceOption.isEmpty

  def nonEmpty: Boolean = !isEmpty

  def retainingDefaultNamespace: Scope = Scope(ListMap.empty, defaultNamespaceOption)

  def withoutDefaultNamespace: Scope = Scope(prefixedNamespaceMap, None)

  def append(otherScope: Scope): Scope =
    Scope(
      prefixedNamespaceMap.concat(otherScope.prefixedNamespaceMap),
      otherScope.defaultNamespaceOption.orElse(defaultNamespaceOption)
    )

  def prepend(otherScope: Scope): Scope = otherScope.append(this)

  def subScopeOf(otherScope: Scope): Boolean =
    // A bit expensive, due to collection creation
    otherScope.prefixedNamespaceMap.view.filterKeys(prefixedNamespaceMap.keySet).toMap == prefixedNamespaceMap &&
    defaultNamespaceOption.forall(ns => otherScope.defaultNamespaceOption.contains(ns))

  def superScopeOf(otherScope: Scope): Boolean = otherScope.subScopeOf(this)

  def filter(p: (Option[Prefix], Namespace) => Boolean): Scope =
    Scope(
      prefixedNamespaceMap.filter((pref, ns) => p(Some(pref), ns)),
      defaultNamespaceOption.filter(ns => p(None, ns))
    )

  def filterOptPrefixes(p: Option[Prefix] => Boolean): Scope =
    Scope(
      prefixedNamespaceMap.filter((pref, _) => p(Some(pref))),
      defaultNamespaceOption.filter(_ => p(None))
    )

  def filterNamespaces(p: Namespace => Boolean): Scope =
    Scope(
      prefixedNamespaceMap.filter((_, ns) => p(ns)),
      defaultNamespaceOption.filter(ns => p(ns))
    )

  def prefixes: Set[Prefix] = prefixedNamespaceMap.keySet

  def optPrefixes: Set[Option[Prefix]] = prefixes.map(Option(_)).concat(defaultNamespaceOption.map(_ => None).toSet)

  def namespaces: Set[Namespace] = prefixedNamespaceMap.values.toSet.concat(defaultNamespaceOption.toSet)

  def resolveOption(qname: QName)(using enameProvider: ENameProvider): Option[EName] =
    qname match
      case QName.UnprefixedName(localPart) if defaultNamespaceOption.isEmpty =>
        Some(enameProvider.ename(localPart))
      case QName.UnprefixedName(localPart) =>
        Some(enameProvider.ename(defaultNamespaceOption, localPart))
      case QName.PrefixedName(prefix, localPart) =>
        prefix match
          case Namespaces.XmlPrefix => Some(enameProvider.ename(XmlNamespace, localPart))
          case _                    => prefixedNamespaceMap.get(prefix).map(ns => enameProvider.ename(ns, localPart))
  end resolveOption

  def resolve(qname: QName)(using enameProvider: ENameProvider): EName =
    resolveOption(qname)(using enameProvider)
      .getOrElse(sys.error(s"Could not resolve QName '$qname' in scope '${Scope.this}'"))

  def resolve(declarations: Declarations): Scope =
    if declarations.isEmpty then Scope.this // fast for this usual case
    else
      val newDefaultNamespaceOption: Option[Namespace] =
        (defaultNamespaceOption, declarations.defaultNamespaceDeclarationOption) match
          case (_, Some(nsDecl)) if nsDecl.isEmpty => None
          case (_, Some(nsDecl))                   => nsDecl.namespaceOption
          case (_, _)                              => defaultNamespaceOption

      val prefixesToUndeclare: Set[Prefix] = declarations.retainingUndeclarations.prefixedDeclarationMap.keySet

      Scope(
        prefixedNamespaceMap
          .concat(declarations.withoutUndeclarations.prefixedDeclarationMap.view.mapValues(_.namespace).to(ListMap))
          .view
          .filterKeys(pref => !prefixesToUndeclare.contains(pref))
          .to(ListMap),
        newDefaultNamespaceOption
      )
  end resolve

// TODO relativize, minimize

object Scope:

  val empty: Scope = Scope(ListMap.empty, None)

  def from(prefixedNamespaceMap: ListMap[Prefix, Namespace], defaultNamespaceOption: Option[Namespace]): Scope =
    apply(prefixedNamespaceMap.filterNot(_._1 == XmlPrefix), defaultNamespaceOption)

  def from(prefixOptionNamespaceMap: ListMap[Option[Prefix], Namespace]): Scope =
    from(
      prefixOptionNamespaceMap.collect { case (Some(prefix), ns) => prefix -> ns },
      prefixOptionNamespaceMap.collectFirst { case (None, ns) => ns }
    )

end Scope
