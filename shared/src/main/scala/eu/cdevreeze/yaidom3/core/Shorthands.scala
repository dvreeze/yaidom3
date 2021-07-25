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

import Namespaces._

/**
 * Shorthands for creating ENames, QNames , Namespaces etc. To use them, import all members of this object in one go.
 *
 * @author
 *   Chris de Vreeze
 */
object Shorthands {

  def en(nsOption: Option[Namespace], localName: String)(using enameProvider: ENameProvider): EName =
    enameProvider.ename(nsOption, LocalName(localName))

  def en(ns: Namespace, localName: String)(using enameProvider: ENameProvider): EName =
    enameProvider.ename(ns, LocalName(localName))

  def en(localName: String)(using enameProvider: ENameProvider): EName =
    enameProvider.ename(LocalName(localName))

  def parseEn(s: String)(using enameProvider: ENameProvider): EName = enameProvider.parseEName(s)

  def qn(prefixOption: Option[Prefix], localName: String): QName =
    QName.of(prefixOption, LocalName(localName))

  def qn(prefix: Prefix, localName: String): QName = QName.of(prefix, LocalName(localName))

  def qn(localName: String): QName = QName.of(LocalName(localName))

  def parseQn(s: String): QName = QName.parse(s)

  def ns(s: String): Namespace = Namespace(s)

  def pr(s: String): Prefix = Prefix(s)

  def ln(s: String): LocalName = LocalName(s)
}
