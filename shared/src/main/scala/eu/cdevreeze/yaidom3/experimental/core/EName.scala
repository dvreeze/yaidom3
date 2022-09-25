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

/**
 * Expanded name. See http://www.w3.org/TR/xml-names11/. It has a local part, and it may or may not have a namespace.
 *
 * @author
 *   Chris de Vreeze
 */
enum EName(val namespaceOption: Option[String], val localPart: String):
  case QualifiedEName(namespace: String, override val localPart: String) extends EName(Some(namespace), localPart)
  case UnqualifiedEName(override val localPart: String) extends EName(None, localPart)

  def toClarkString: String =
    namespaceOption match
      case Some(ns) =>
        val (openBrace, closeBrace) = ("{", "}")
        s"$openBrace$ns$closeBrace$localPart"
      case _ =>
        localPart
  end toClarkString

  override def toString: String = toClarkString

// TODO To URI-qualified

object EName:

  def of(namespace: String, localPart: String): EName = QualifiedEName(namespace, localPart)

  def of(localPart: String): EName = UnqualifiedEName(localPart)

  def of(namespaceOption: Option[String], localPart: String): EName =
    namespaceOption.map(ns => QualifiedEName(ns, localPart)).getOrElse(UnqualifiedEName(localPart))

  def parse(enameString: String): EName =
    val st = enameString.trim

    if st.startsWith("{") then
      val idx = st.indexOf('}')
      require(idx >= 2 && idx < st.length - 1, s"Opening brace not closed or at incorrect location in EName '$st'")
      val ns = st.substring(1, idx)
      val localPart = st.substring(idx + 1)
      EName.of(ns, localPart)
    else
      require(st.indexOf("{") < 0, s"No opening brace allowed unless at the beginning in EName '$st'")
      require(st.indexOf("}") < 0, s"Closing brace without matching opening brace not allowed in EName '$st'")
      EName.of(st)
  end parse

  // TODO Parse from URI-qualified

end EName
