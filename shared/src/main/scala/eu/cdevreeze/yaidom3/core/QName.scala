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

import eu.cdevreeze.yaidom3.core.Namespaces.*

/**
 * Qualified name. See http://www.w3.org/TR/xml-names11/. It has a local part, and it may or may not have a namespace prefix.
 *
 * @author
 *   Chris de Vreeze
 */
enum QName(val prefixOption: Option[Prefix], val localPart: LocalName):
  case PrefixedName(prefix: Prefix, override val localPart: LocalName) extends QName(Some(prefix), localPart)
  case UnprefixedName(override val localPart: LocalName) extends QName(None, localPart)

  override def toString: String = this match
    case PrefixedName(pref, ln) => s"$pref:$ln"
    case UnprefixedName(ln)     => ln.toString

  def asPrefixedName: PrefixedName = this.asInstanceOf[PrefixedName]
  def asUnprefixedName: UnprefixedName = this.asInstanceOf[UnprefixedName]

object QName:

  def of(prefix: Prefix, localPart: LocalName): QName = PrefixedName(prefix, localPart)

  def of(localPart: LocalName): QName = UnprefixedName(localPart)

  def of(prefixOption: Option[Prefix], localPart: LocalName): QName =
    prefixOption.map(pref => PrefixedName(pref, localPart)).getOrElse(UnprefixedName(localPart))

  def parse(qnameString: String): QName =
    val parts: Array[String] = qnameString.trim.split(':').ensuring(_.length <= 2, s"More than 2 colons in a QName not allowed")

    parts.length match
      case 1 => of(LocalName(parts(0)))
      case 2 => of(Prefix(parts(0)), LocalName(parts(1)))
      case _ => sys.error(s"Not a QName: '$qnameString'")
  end parse

end QName
