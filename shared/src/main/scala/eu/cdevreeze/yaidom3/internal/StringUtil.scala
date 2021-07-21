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

package eu.cdevreeze.yaidom3.internal

/**
 * XML-related String utilities.
 *
 * @author
 *   Chris de Vreeze
 */
object StringUtil:

  /**
   * Normalizes the string, removing surrounding whitespace and normalizing internal whitespace to a single space. Whitespace includes #x20
   * (space), #x9 (tab), #xD (carriage return), #xA (line feed). If there is only whitespace, the empty string is returned. Inspired by the
   * JDOM library.
   */
  def normalizeString(s: String): String =
    val separators = Array(' ', '\t', '\r', '\n')
    val words: Seq[String] = s.split(separators).toSeq.filterNot(_.isEmpty)

    words.mkString(" ") // Returns empty string if words.isEmpty
  end normalizeString

end StringUtil
