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

package eu.cdevreeze.yaidom3.examples

import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.Namespaces.*
import eu.cdevreeze.yaidom3.core.Navigation.*
import eu.cdevreeze.yaidom3.core.QName
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.core.Shorthands.*
import eu.cdevreeze.yaidom3.node.clark.DefaultClarkNodes
import eu.cdevreeze.yaidom3.node.scoped.DefaultScopedElemFactory.*
import eu.cdevreeze.yaidom3.node.scoped.DefaultScopedNodes
import eu.cdevreeze.yaidom3.node.scoped.given

import scala.collection.immutable.ListMap
import scala.util.chaining.*

/**
 * Element creation and update API example for XBRL instance creation.
 *
 * @author
 *   Chris de Vreeze
 */
object XbrlSample:

  val xbrliNs: Namespace = ns("http://www.xbrl.org/2003/instance")
  val linkNs: Namespace = ns("http://www.xbrl.org/2003/linkbase")
  val xlinkNs: Namespace = ns("http://www.w3.org/1999/xlink")
  val xbrldiNs: Namespace = ns("http://xbrl.org/2006/xbrldi")
  val iso4217Ns: Namespace = ns("http://www.xbrl.org/2003/iso4217")
  val gaapNs: Namespace = ns("http://xasb.org/gaap")

  val xbrliPref: Prefix = pr("xbrli")
  val linkPref: Prefix = pr("link")
  val xlinkPref: Prefix = pr("xlink")
  val xbrldiPref: Prefix = pr("xbrldi")
  val iso4217Pref: Prefix = pr("iso4217")
  val gaapPref: Prefix = pr("gaap")

  val minimalScope: Scope = Scope.from(
    ListMap(
      Some(xbrliPref) -> xbrliNs,
      Some(linkPref) -> linkNs,
      Some(xlinkPref) -> xlinkNs,
      Some(xbrldiPref) -> xbrldiNs,
      Some(iso4217Pref) -> iso4217Ns
    )
  )

  /**
   * Fluent API for xbrli:unit creation (for simple cases of unit construction)
   */
  object UnitBuilder:

    def createUnit(id: String, parentScope: Scope): EmptyUnit =
      val scope = parentScope.unsafeAppendCompatibly(xbrliPref, xbrliNs)
      emptyElem(qn(xbrliPref, "unit"), scope).plusAttribute(qn("id"), id).pipe(EmptyUnit(_))

    transparent sealed trait EmptyOrNonEmptyUnit(unitElem: DefaultScopedNodes.Elem):
      def addMeasure(measure: QName.PrefixedName, namespace: Namespace): NonEmptyUnit =
        val scope = unitElem.scope.unsafeAppendCompatibly(xbrliPref, xbrliNs).unsafeAppendCompatibly(measure.prefix, namespace)
        unitElem
          .plusChild(textElem(qn(xbrliPref, "measure"), scope, measure.toString))
          .pipe(NonEmptyUnit(_))
    end EmptyOrNonEmptyUnit

    final class EmptyUnit(val unitElem: DefaultScopedNodes.Elem) extends EmptyOrNonEmptyUnit(unitElem)

    final class NonEmptyUnit(val unitElem: DefaultScopedNodes.Elem) extends EmptyOrNonEmptyUnit(unitElem)

  end UnitBuilder

  /**
   * Fluent API for xbrli:segment creation (without non-dimensional content, or even typed members)
   */
  object SegmentBuilder:

    def createSegment(parentScope: Scope): EmptySegment =
      val scope = parentScope.unsafeAppendCompatibly(xbrliPref, xbrliNs)
      emptyElem(qn(xbrliPref, "segment"), scope).pipe(EmptySegment(_))

    transparent sealed trait EmptyOrNonEmptySegment(segmentElem: DefaultScopedNodes.Elem):
      def addExplicitDimension(
          dimension: QName.PrefixedName,
          dimensionNs: Namespace,
          member: QName.PrefixedName,
          memberNs: Namespace
      ): NonEmptySegment =
        val scope = segmentElem.scope
          .unsafeAppendCompatibly(xbrldiPref, xbrldiNs)
          .unsafeAppendCompatibly(dimension.prefix, dimensionNs)
          .unsafeAppendCompatibly(member.prefix, memberNs)
        segmentElem
          .plusChild(
            textElem(qn(xbrldiPref, "explicitMember"), scope, member.toString).plusAttribute(qn("dimension"), dimension.toString)
          )
          .pipe(NonEmptySegment(_))
    end EmptyOrNonEmptySegment

    final class EmptySegment(val segmentElem: DefaultScopedNodes.Elem) extends EmptyOrNonEmptySegment(segmentElem)

    final class NonEmptySegment(val segmentElem: DefaultScopedNodes.Elem) extends EmptyOrNonEmptySegment(segmentElem)

  end SegmentBuilder

  /**
   * Fluent API for xbrli:context creation (without scenarios)
   */
  object ContextBuilder:

    def createContext(id: String, parentScope: Scope): EmptyContext =
      val scope = parentScope.unsafeAppendCompatibly(xbrliPref, xbrliNs)
      emptyElem(qn(xbrliPref, "context"), scope).plusAttribute(qn("id"), id).pipe(EmptyContext(_))

    final class EmptyContext(val contextElem: DefaultScopedNodes.Elem):
      def addIdentifier(identifierScheme: String, identifierValue: String): ContextWithIdentifier =
        val scope = contextElem.scope.unsafeAppendCompatibly(xbrliPref, xbrliNs)
        contextElem
          .plusChild(
            emptyElem(qn(xbrliPref, "entity"), scope).plusChild(
              textElem(qn(xbrliPref, "identifier"), scope, identifierValue).plusAttribute(qn("scheme"), identifierScheme)
            )
          )
          .pipe(ContextWithIdentifier(_))
    end EmptyContext

    final class ContextWithIdentifier(val contextElem: DefaultScopedNodes.Elem):
      def skippingSegment: ContextWithOptSegment = ContextWithOptSegment(contextElem)

      def addSegment(segment: SegmentBuilder.NonEmptySegment): ContextWithOptSegment =
        val pathToEntity: NavigationPath = NavigationPath.from(Seq(0))
        assert(contextElem.findDescendantElemOrSelf(pathToEntity).exists(_.name.localPart == ln("entity")))

        contextElem
          .updateDescendantElemOrSelf(pathToEntity) { entityElem =>
            entityElem.plusChild(segment.segmentElem).notUndeclaringPrefixes(Scope.empty)
          }
          .pipe(ContextWithOptSegment(_))
      end addSegment
    end ContextWithIdentifier

    final class ContextWithOptSegment(val contextElem: DefaultScopedNodes.Elem):
      def addInstantPeriod(date: String): CompleteContext =
        contextElem
          .plusChild(
            emptyElem(qn(xbrliPref, "period"), contextElem.scope).plusChild(textElem(qn(xbrliPref, "instant"), contextElem.scope, date))
          )
          .notUndeclaringPrefixes(Scope.empty)
          .pipe(CompleteContext(_))
      end addInstantPeriod

      def addDurationPeriod(startDate: String, endDate: String): CompleteContext =
        contextElem
          .plusChild(
            emptyElem(qn(xbrliPref, "period"), contextElem.scope)
              .plusChild(textElem(qn(xbrliPref, "startDate"), contextElem.scope, startDate))
              .plusChild(textElem(qn(xbrliPref, "endDate"), contextElem.scope, endDate))
          )
          .notUndeclaringPrefixes(Scope.empty)
          .pipe(CompleteContext(_))
      end addDurationPeriod
    end ContextWithOptSegment

    final class CompleteContext(val contextElem: DefaultScopedNodes.Elem)

  end ContextBuilder

end XbrlSample
