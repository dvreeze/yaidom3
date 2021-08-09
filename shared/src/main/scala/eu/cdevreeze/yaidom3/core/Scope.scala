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

  /**
   * Returns the scope resulting from retaining only the optional default namespace.
   */
  def retainingDefaultNamespace: Scope = Scope(ListMap.empty, defaultNamespaceOption)

  def withoutDefaultNamespace: Scope = Scope(prefixedNamespaceMap, None)

  /**
   * Appends the parameter scope to this scope. The parameter scope is leading in case of "conflicts". That is, the result is a super-scope
   * of the parameter scope (not necessarily compatible), and not necessarily of this scope.
   */
  def append(otherScope: Scope): Scope =
    Scope(
      prefixedNamespaceMap.concat(otherScope.prefixedNamespaceMap),
      otherScope.defaultNamespaceOption.orElse(defaultNamespaceOption)
    )

  /**
   * Returns `otherScope.append(this)`.
   */
  def prepend(otherScope: Scope): Scope = otherScope.append(this)

  /**
   * Returns true if this is a sub-scope of the parameter scope, that is, if for each prefix in this scope, the parameter scope has the same
   * prefix mapping to the same namespace, and the default namespace of this scope, if any, is the default namespace of the parameter scope.
   * Note that it is possible that this scope is a sub-scope of the parameter scope, where this scope has no default namespace and the
   * parameter scope does have a default namespace. Therefore the sub-scoping relationship does not imply that unprefixed QNames are
   * resolved in the same way by this scope and the parameter scope. See method `compatibleSubScopeOf` if we need more guarantees about
   * equivalent QName resolution for QNames that can be resolved by this scope.
   */
  def subScopeOf(otherScope: Scope): Boolean =
    // A bit expensive, due to collection creation
    otherScope.prefixedNamespaceMap.view.filterKeys(prefixedNamespaceMap.keySet).toMap == prefixedNamespaceMap &&
      defaultNamespaceOption.forall(ns => otherScope.defaultNamespaceOption.contains(ns))
  end subScopeOf

  /**
   * Returns `otherScope.subScopeOf(this)`.
   */
  def superScopeOf(otherScope: Scope): Boolean = otherScope.subScopeOf(this)

  /**
   * Filters this scope, using a predicate on the optional prefix and namespace (non-empty first predicate argument for prefixes, empty
   * first predicate argument for the default namespace).
   */
  def filter(p: (Option[Prefix], Namespace) => Boolean): Scope =
    Scope(
      prefixedNamespaceMap.filter((pref, ns) => p(Some(pref), ns)),
      defaultNamespaceOption.filter(ns => p(None, ns))
    )

  /**
   * Filters this scope, using a predicate on the optional prefix (non-empty predicate argument for prefixes, empty predicate argument for
   * the default namespace).
   */
  def filterOptPrefixes(p: Option[Prefix] => Boolean): Scope =
    Scope(
      prefixedNamespaceMap.filter((pref, _) => p(Some(pref))),
      defaultNamespaceOption.filter(_ => p(None))
    )

  /**
   * Filters this scope, using a predicate on the namespace.
   */
  def filterNamespaces(p: Namespace => Boolean): Scope =
    Scope(
      prefixedNamespaceMap.filter((_, ns) => p(ns)),
      defaultNamespaceOption.filter(ns => p(ns))
    )

  def prefixes: Set[Prefix] = prefixedNamespaceMap.keySet

  /**
   * Returns the prefixes, wrapped in an Option, and None as well if the default namespace exists.
   */
  def optPrefixes: Set[Option[Prefix]] = prefixes.map(Option(_)).concat(defaultNamespaceOption.map(_ => None).toSet)

  def namespaces: Set[Namespace] = prefixedNamespaceMap.values.toSet.concat(defaultNamespaceOption.toSet)

  /**
   * Resolves the passed QName, if possible, wrapping the result in an Option.
   */
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

  /**
   * Returns the equivalent of `resolveOption(qname).get`.
   */
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

  /**
   * Returns the result of `Scope.areCompatible(this, otherScope)`.
   */
  def isCompatibleWith(otherScope: Scope): Boolean = Scope.areCompatible(this, otherScope)

  /**
   * Returns true if this is a compatible sub-scope of the parameter scope. That is, returns true if this is a sub-scope of the parameter
   * scope and both scopes are compatible. In other words, returns true if this is a sub-scope of the parameter scope and both scopes either
   * have no default namespace or have the same default namespace. In other words, returns true if the parameter scope can safely be used
   * instead of this scope without changing anything in the resolution of QNames that can be resolved by this scope.
   */
  def compatibleSubScopeOf(otherScope: Scope): Boolean =
    subScopeOf(otherScope) && defaultNamespaceOption == otherScope.defaultNamespaceOption

  /**
   * Returns `otherScope.compatibleSubScopeOf(this)`.
   */
  def compatibleSuperScopeOf(otherScope: Scope): Boolean = otherScope.compatibleSubScopeOf(this)

  /**
   * Returns the equivalent of `unsafeAppendCompatibly(Scope.from(ListMap(prefix -> namespace), defaultNamespaceOption))`. An exception is
   * thrown if the "xml" prefix is passed as first parameter (even if the namespace is correct).
   */
  def unsafeAppendCompatibly(prefix: Prefix, namespace: Namespace): Scope =
    require(
      this.prefixedNamespaceMap.get(prefix).forall(_ == namespace),
      s"Cannot compatibly add prefix '$prefix' mapping to namespace '$namespace' to scope '${Scope.this}'"
    )
    require(prefix != Namespaces.XmlPrefix, s"Cannot add prefix '${Namespaces.XmlPrefix}'")

    Scope.from(this.prefixedNamespaceMap.updated(prefix, namespace), this.defaultNamespaceOption)
  end unsafeAppendCompatibly

  /**
   * Equivalent to calling `unsafeAppendCompatibly` repeatedly, one prefix per call.
   */
  def unsafeAppendCompatibly(prefixNamespaceMap: ListMap[Prefix, Namespace]): Scope =
    require(!(prefixNamespaceMap.keySet.contains(Namespaces.XmlPrefix)), s"Cannot add prefix '${Namespaces.XmlPrefix}'")

    val otherScope: Scope = Scope.from(prefixNamespaceMap, None).prepend(this.retainingDefaultNamespace)
    unsafeAppendCompatibly(otherScope)
  end unsafeAppendCompatibly

  /**
   * Returns the result of `append(otherScope)` if both scopes are compatible, and throws an exception otherwise. The result scope, if any,
   * is equal to `Scope.findCompatibleSuperScope(Seq(this, otherScope)).get` (with the potential exception of prefix order).
   *
   * Note that both scopes must either have no default namespace, or must have the same default namespace. Also note that for compatible
   * scopes, appending or prepending are the same operation, except for the prefix order.
   */
  def unsafeAppendCompatibly(otherScope: Scope): Scope =
    require(isCompatibleWith(otherScope), s"Cannot compatibly add scope '$otherScope' to this scope '${Scope.this}'")

    this.append(otherScope)
  end unsafeAppendCompatibly

object Scope:

  val empty: Scope = Scope(ListMap.empty, None)

  def from(prefixedNamespaceMap: ListMap[Prefix, Namespace], defaultNamespaceOption: Option[Namespace]): Scope =
    apply(prefixedNamespaceMap.filterNot(_._1 == XmlPrefix), defaultNamespaceOption)

  def from(prefixOptionNamespaceMap: ListMap[Option[Prefix], Namespace]): Scope =
    from(
      prefixOptionNamespaceMap.collect { case (Some(prefix), ns) => prefix -> ns },
      prefixOptionNamespaceMap.collectFirst { case (None, ns) => ns }
    )

  /**
   * Returns true if both parameter scopes have the same optional default namespace and map the prefixes they have in common to the same
   * namespaces. Note that they are not compatible if one scope has a default namespace and the other one does not.
   *
   * If in an element tree all element scopes are mutually compatible, then all namespace declarations can be pushed up to the root element
   * without changing anything semantically to the content of the element tree. In other words, in that case all elements in the tree can
   * have the same scope, which is a compatible super-scope of all element scopes.
   */
  def areCompatible(scope1: Scope, scope2: Scope): Boolean =
    scope1.defaultNamespaceOption == scope2.defaultNamespaceOption && {
      val commonPrefixes: Set[Prefix] = scope1.prefixedNamespaceMap.keySet.intersect(scope2.prefixedNamespaceMap.keySet)

      scope1.prefixedNamespaceMap.view
        .filterKeys(commonPrefixes)
        .toMap == scope2.prefixedNamespaceMap.view.filterKeys(commonPrefixes).toMap
    }
  end areCompatible

  /**
   * Returns the optional compatible super-scope of the parameter scopes, but returns None if the parameter scope collection is empty or if
   * the scopes are not all mutually compatible. The result, if defined, is a (compatible) super-scope of all the parameter scopes.
   */
  def findCompatibleSuperScope(scopes: Seq[Scope]): Option[Scope] =
    val distinctScopes = scopes.distinct

    distinctScopes.drop(1).foldLeft(distinctScopes.headOption) { (accOptScope, nextScope) =>
      accOptScope.flatMap { accScope =>
        if areCompatible(accScope, nextScope) then Some(accScope.append(nextScope)) else None
      }
    }
  end findCompatibleSuperScope

  /**
   * Given the parameter scopes, for all prefixes for which these scopes do not conflict with respect to the namespace, returns the scope
   * that maps those prefixes to their namespaces. The result contains no default namespace. If the parameter scope collection is empty, the
   * empty scope is returned. Note that it is perfectly ok for some prefixes to occur in only a few of the passed scopes. As long as there
   * are no conflicts for those prefixes, they make it into the resulting scope.
   *
   * If the scope collection is non-empty and all its scopes are compatible, then the result is the same as the filled optional result of
   * function `findCompatibleSuperScope`, except for the missing default namespace.
   */
  def commonPrefixedScope(scopes: Seq[Scope]): Scope =
    val distinctScopes: Seq[Scope] = scopes.distinct.map(_.withoutDefaultNamespace).distinct
    val prefixNamespaceMap: Map[Prefix, Seq[Namespace]] =
      distinctScopes.flatMap(_.prefixedNamespaceMap).distinct.groupMap(_._1)(_._2)
    val commonPrefixMap: ListMap[Prefix, Namespace] = prefixNamespaceMap.toSeq
      .collect {
        case (pref, namespaces) if namespaces.sizeIs == 1 => pref -> namespaces.head
      }
      .to(ListMap)
    Scope.from(commonPrefixMap, None)
  end commonPrefixedScope

end Scope
