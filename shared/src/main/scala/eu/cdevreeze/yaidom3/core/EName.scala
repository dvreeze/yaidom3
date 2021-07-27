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
 * Expanded name. See http://www.w3.org/TR/xml-names11/. It has a local part, and it may or may not have a namespace.
 *
 * @author
 *   Chris de Vreeze
 */
enum EName(val namespaceOption: Option[Namespace], val localPart: LocalName):
  case QualifiedEName(namespace: Namespace, override val localPart: LocalName) extends EName(Some(namespace), localPart)
  case UnqualifiedEName(override val localPart: LocalName) extends EName(None, localPart)

  def toClarkString: String =
    namespaceOption.map(_.namespaceAsString) match
      case Some(ns) =>
        val (openBrace, closeBrace) = ("{", "}")
        s"$openBrace$ns$closeBrace${localPart.localNameAsString}"
      case _ =>
        localPart.localNameAsString
  end toClarkString

// TODO To URI-qualified

object EName:

  def of(namespace: Namespace, localPart: LocalName): EName = QualifiedEName(namespace, localPart)

  def of(localPart: LocalName): EName = UnqualifiedEName(localPart)

  def of(namespaceOption: Option[Namespace], localPart: LocalName): EName =
    namespaceOption.map(ns => QualifiedEName(ns, localPart)).getOrElse(UnqualifiedEName(localPart))

  def parse(enameString: String): EName =
    val st = enameString.trim

    if st.startsWith("{") then
      val idx = st.indexOf('}')
      require(idx >= 2 && idx < st.length - 1, s"Opening brace not closed or at incorrect location in EName '$st'")
      val ns = Namespace(st.substring(1, idx))
      val localPart = LocalName(st.substring(idx + 1))
      EName.of(ns, localPart)
    else
      require(st.indexOf("{") < 0, s"No opening brace allowed unless at the beginning in EName '$st'")
      require(st.indexOf("}") < 0, s"Closing brace without matching opening brace not allowed in EName '$st'")
      EName.of(LocalName(st))
  end parse

// TODO Parse from URI-qualified

end EName
