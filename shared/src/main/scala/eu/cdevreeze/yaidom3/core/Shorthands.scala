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

import Namespaces.*

/**
 * Shorthands for creating ENames, QNames , Namespaces etc. To use them, import all members of this object in one go.
 * There is also a (given) conversion from String to LocalName. It needs a "given import" in order to be used.
 *
 * @author
 *   Chris de Vreeze
 */
object Shorthands:

  def en(nsOption: Option[Namespace], localName: LocalName)(using enameProvider: ENameProvider): EName =
    enameProvider.ename(nsOption, localName)

  def en(ns: Namespace, localName: LocalName)(using enameProvider: ENameProvider): EName =
    enameProvider.ename(ns, localName)

  def en(localName: LocalName)(using enameProvider: ENameProvider): EName =
    enameProvider.ename(localName)

  def parseEn(s: String)(using enameProvider: ENameProvider): EName = enameProvider.parseEName(s)

  def qn(prefixOption: Option[Prefix], localName: LocalName): QName =
    QName.of(prefixOption, localName)

  def qn(prefix: Prefix, localName: LocalName): QName = QName.of(prefix, localName)

  def qn(localName: LocalName): QName = QName.of(localName)

  def parseQn(s: String): QName = QName.parse(s)

  def ns(s: String): Namespace = Namespace(s)

  def pr(s: String): Prefix = Prefix(s)

  def ln(s: String): LocalName = LocalName(s)

  given Conversion[String, LocalName] with
    def apply(s: String): LocalName = LocalName(s)

end Shorthands
