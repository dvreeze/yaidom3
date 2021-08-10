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

import java.io.File

import scala.collection.immutable.ListMap
import scala.language.adhocExtensions
import scala.util.chaining.*

import eu.cdevreeze.yaidom3.core.ENameProvider
import eu.cdevreeze.yaidom3.core.ENameProvider.Trivial.given
import eu.cdevreeze.yaidom3.core.ENameProvider.UsingGrowingMap
import eu.cdevreeze.yaidom3.core.Scope
import eu.cdevreeze.yaidom3.core.Shorthands.*
import eu.cdevreeze.yaidom3.node.clark.DefaultClarkNodes
import eu.cdevreeze.yaidom3.node.common.CommonXbrlQuerySpec
import eu.cdevreeze.yaidom3.node.common.DefaultCommonNodes
import eu.cdevreeze.yaidom3.node.common.given
import eu.cdevreeze.yaidom3.node.saxon.SaxonNodes
import eu.cdevreeze.yaidom3.node.scoped.DefaultScopedElemFactory.*
import eu.cdevreeze.yaidom3.node.scoped.DefaultScopedNodes
import eu.cdevreeze.yaidom3.node.scoped.given
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.streams.Predicates.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.*

/**
 * Element update API test suite using XBRL instance creation as example.
 *
 * @author
 *   Chris de Vreeze
 */
class XbrlSampleUpdateSpec extends AnyFlatSpec, should.Matchers:

  import XbrlSample.*

  private val saxonProcessor: Processor = Processor(false)

  private val rootElem: DefaultCommonNodes.Elem = loadData()

  "The 'common update API'".should("successfully (functionally) update parts of the XBRL instance").in {
    val segmentElemOption: Option[DefaultCommonNodes.Elem] =
      rootElem
        .findDescendantElem(e => e.hasName(xbrliNs, "context") && e.attr(en("id")) == "D-2007")
        .flatMap(_.findDescendantElem(_.hasName(xbrliNs, "segment")))

    segmentElemOption.isDefined.should(equal(true))

    val segmentElem: DefaultCommonNodes.Elem = segmentElemOption.get

    val updatedSegmentElem: DefaultCommonNodes.Elem =
      segmentElem.updatedWithinTree { e =>
        SegmentBuilder
          .NonEmptySegment(e)
          .addExplicitDimension(
            qn(gaapPref, "ClassOfPreferredStockDescriptionAxis").asPrefixedName,
            gaapNs,
            qn(gaapPref, "AllClassesOfPreferredStockDomain").asPrefixedName,
            gaapNs
          )
          .segmentElem
      }

    // Very important! The root element has changed after the updatedWithinTree call!
    val updatedRootElem: DefaultCommonNodes.Elem = updatedSegmentElem.rootElem

    val foundSegmentElemOption: Option[DefaultCommonNodes.Elem] =
      updatedRootElem
        .findDescendantElem(e => e.hasName(xbrliNs, "context") && e.attr(en("id")) == "D-2007")
        .flatMap(_.findDescendantElem(_.hasName(xbrliNs, "segment")))

    foundSegmentElemOption.isDefined.should(equal(true))

    val foundSegmentElem: DefaultCommonNodes.Elem = foundSegmentElemOption.get

    foundSegmentElem.findAllChildElems.size.should(equal(1 + segmentElem.findAllChildElems.size))

    makeSegmentsEmpty(updatedRootElem)
      .pipe(DefaultClarkNodes.Elem.from(_))
      .should(
        equal(
          makeSegmentsEmpty(rootElem).pipe(DefaultClarkNodes.Elem.from(_))
        )
      )

    DefaultClarkNodes.Elem
      .from(updatedRootElem)
      .should(
        not(
          equal(
            DefaultClarkNodes.Elem.from(rootElem)
          )
        )
      )
  }

  it.should("successfully (functionally) update parts of the XBRL instance using a 'bulk' API call").in {
    val contextElemOption: Option[DefaultCommonNodes.Elem] =
      rootElem
        .findDescendantElem(e => e.hasName(xbrliNs, "context") && e.attr(en("id")) == "D-2007")

    contextElemOption.isDefined.should(equal(true))

    val contextElem: DefaultCommonNodes.Elem = contextElemOption.get

    val updatedContextElem: DefaultCommonNodes.Elem = contextElem.filterAndUpdateDescendantElemsOrSelfWithinTree {
      _.hasName(xbrliNs, "segment")
    } { e =>
      SegmentBuilder
        .NonEmptySegment(e)
        .addExplicitDimension(
          qn(gaapPref, "ClassOfPreferredStockDescriptionAxis").asPrefixedName,
          gaapNs,
          qn(gaapPref, "AllClassesOfPreferredStockDomain").asPrefixedName,
          gaapNs
        )
        .segmentElem
    }

    // Very important! The root element has changed after the filterAndUpdateDescendantElemsOrSelfWithinTree call!
    val updatedRootElem: DefaultCommonNodes.Elem = updatedContextElem.rootElem

    val foundSegmentElemOption: Option[DefaultCommonNodes.Elem] =
      updatedRootElem
        .findDescendantElem(e => e.hasName(xbrliNs, "context") && e.attr(en("id")) == "D-2007")
        .flatMap(_.findDescendantElem(_.hasName(xbrliNs, "segment")))

    foundSegmentElemOption.isDefined.should(equal(true))

    val foundSegmentElem: DefaultCommonNodes.Elem = foundSegmentElemOption.get

    makeSegmentsEmpty(updatedRootElem)
      .pipe(DefaultClarkNodes.Elem.from(_))
      .should(
        equal(
          makeSegmentsEmpty(rootElem).pipe(DefaultClarkNodes.Elem.from(_))
        )
      )

    DefaultClarkNodes.Elem
      .from(updatedRootElem)
      .should(
        not(
          equal(
            DefaultClarkNodes.Elem.from(rootElem)
          )
        )
      )
  }

  it.should("successfully update (and remove the default namespace) in an XBRL instance").in {
    // Remove default namespace

    val updatedRootElem: DefaultCommonNodes.Elem =
      rootElem
        .filterAndUpdateDescendantElemsOrSelfWithinTree { e =>
          e.hasName(xbrliNs, "measure") && e.textAsQName.prefixOption.isEmpty
        } { e =>
          textElem(e.qname, e.attrsByQName, e.scope, qn(xbrliPref, e.textAsQName.localPart.toString).toString)
        }
        .filterAndUpdateDescendantElemsOrSelfWithinTree {
          _.name.namespaceOption.contains(xbrliNs)
        } { e =>
          elem(qn(xbrliPref, e.qname.localPart.toString), e.attrsByQName, e.scope, e.children)
        }
        .filterAndUpdateDescendantElemsOrSelfWithinTree(_ => true) { e =>
          elem(e.qname, e.attrsByQName, e.scope.withoutDefaultNamespace, e.children)
        }

    updatedRootElem.underlyingElem.hasSameScopeInDescendantsOrSelf.should(equal(true))
    updatedRootElem.underlyingElem.scope.defaultNamespaceOption.should(equal(None))

    makeComparable(updatedRootElem.underlyingElem).should(equal(makeComparable(rootElem.underlyingElem)))
  }

  "The 'transformation API'".should("successfully update (and remove the default namespace) in an XBRL instance").in {
    // Remove default namespace

    val updatedUnderlyingRootElem: DefaultScopedNodes.Elem =
      rootElem.underlyingElem
        .transformDescendantElemsOrSelf {
          case e if e.name.namespaceOption.contains(xbrliNs) =>
            elem(qn(xbrliPref, e.qname.localPart.toString), e.attrsByQName, e.scope, e.children)
          case e => e
        }
        .transformDescendantElems {
          case e if e.hasName(xbrliNs, "measure") && e.textAsQName.prefixOption.isEmpty =>
            textElem(e.qname, e.attrsByQName, e.scope, qn(xbrliPref, e.textAsQName.localPart.toString).toString)
          case e => e
        }
        .transformDescendantElemsOrSelf { e =>
          elem(e.qname, e.attrsByQName, e.scope.withoutDefaultNamespace, e.children)
        }

    val updatedRootElem: DefaultCommonNodes.Elem =
      DefaultCommonNodes.Elem.ofRoot(rootElem.docUriOption, updatedUnderlyingRootElem)

    updatedRootElem.underlyingElem.hasSameScopeInDescendantsOrSelf.should(equal(true))
    updatedRootElem.underlyingElem.scope.defaultNamespaceOption.should(equal(None))

    makeComparable(updatedRootElem.underlyingElem).should(equal(makeComparable(rootElem.underlyingElem)))
  }

  "The 'navigation Path based update API'".should("successfully update (and remove the default namespace) in an XBRL instance").in {
    // Remove default namespace
    // Unnecessarily low-level code. Use higher-level API method filterAndUpdateDescendantElemsOrSelfWithinTree instead.

    val updatedRootElem: DefaultCommonNodes.Elem =
      rootElem
        .pipe { root =>
          val paths = root
            .filterDescendantElems(e => e.hasName(xbrliNs, "measure") && e.textAsQName.prefixOption.isEmpty)
            .map(_.elemNavigationPathFromRoot)
            .toSet
          root.underlyingElem
            .updateDescendantElemsOrSelf(paths) { (e, _) =>
              textElem(e.qname, e.attrsByQName, e.scope, qn(xbrliPref, e.textAsQName.localPart.toString).toString)
            }
            .pipe(e => DefaultCommonNodes.Elem.ofRoot(root.docUriOption, e))
        }
        .pipe { root =>
          val paths = root
            .filterDescendantElemsOrSelf(_.name.namespaceOption.contains(xbrliNs))
            .map(_.elemNavigationPathFromRoot)
            .toSet
          root.underlyingElem
            .updateDescendantElemsOrSelf(paths) { (e, _) =>
              elem(qn(xbrliPref, e.qname.localPart.toString), e.attrsByQName, e.scope, e.children)
            }
            .pipe(e => DefaultCommonNodes.Elem.ofRoot(root.docUriOption, e))
        }
        .pipe { root =>
          val paths = root.findAllDescendantElemsOrSelf.map(_.elemNavigationPathFromRoot).toSet
          root.underlyingElem
            .updateDescendantElemsOrSelf(paths) { (e, _) =>
              elem(e.qname, e.attrsByQName, e.scope.withoutDefaultNamespace, e.children)
            }
            .pipe(e => DefaultCommonNodes.Elem.ofRoot(root.docUriOption, e))
        }

    updatedRootElem.underlyingElem.hasSameScopeInDescendantsOrSelf.should(equal(true))
    updatedRootElem.underlyingElem.scope.defaultNamespaceOption.should(equal(None))

    makeComparable(updatedRootElem.underlyingElem).should(equal(makeComparable(rootElem.underlyingElem)))
  }

  private def makeSegmentsEmpty(root: DefaultCommonNodes.Elem): DefaultCommonNodes.Elem =
    DefaultCommonNodes.Elem.ofRoot(
      root.docUriOption,
      root.underlyingElem.transformDescendantElems {
        case e if e.hasName(xbrliNs, "segment") => e.withChildren(Seq.empty)
        case e                                  => e
      }
    )

  private def makeComparable(e: DefaultScopedNodes.Elem): DefaultClarkNodes.Elem =
    e.transformDescendantElems {
      case e if e.hasName(xbrliNs, "measure") => textElem(e.qname, e.attrsByQName, e.scope, e.textAsResolvedQName.toString)
      case e                                  => e
    }.pipe(e => DefaultClarkNodes.Elem.from(e))

  private def loadData(): DefaultCommonNodes.Elem =
    given enameProvider: ENameProvider = UsingGrowingMap.makeENameProvider

    val file = File(classOf[CommonXbrlQuerySpec].getResource("/sample-xbrl-instance.xml").toURI)
    saxonProcessor
      .newDocumentBuilder()
      .build(file)
      .pipe(_.children(isElement.test(_)).iterator.next)
      .pipe(SaxonNodes.Elem(_))
      .pipe(e => DefaultCommonNodes.Elem.from(e)(using enameProvider))
      .ensuring(_.findAllDescendantElemsOrSelf.sizeIs >= 1000)

end XbrlSampleUpdateSpec
