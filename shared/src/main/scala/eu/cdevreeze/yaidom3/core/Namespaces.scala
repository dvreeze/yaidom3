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

/**
 * XML namespaces and namespace prefixes. See http://www.w3.org/TR/xml-names11/.
 *
 * Empty namespaces and empty prefixes are not allowed, and such construction attempts cause exceptions to be thrown. The same holds for
 * empty local names.
 *
 * @author
 *   Chris de Vreeze
 */
object Namespaces:

  opaque type Namespace = String

  object Namespace:
    def apply(ns: String): Namespace = ns.ensuring(_.nonEmpty, "Empty namespace not allowed")
  end Namespace

  val XmlNamespace: Namespace = "http://www.w3.org/XML/1998/namespace"

  opaque type Prefix = String

  object Prefix:
    def apply(pref: String): Prefix = pref.ensuring(_.nonEmpty, "Empty namespace prefix not allowed")
  end Prefix

  val XmlPrefix: Prefix = "xml"

  opaque type LocalName = String

  object LocalName:
    def apply(name: String): LocalName = name.ensuring(_.nonEmpty, "Empty local name not allowed")
  end LocalName

  extension (ns: Namespace) def namespaceAsString: String = ns
  end extension

  extension (pref: Prefix) def prefixAsString: String = pref
  end extension

  extension (localName: LocalName) def localNameAsString: String = localName
  end extension

// TODO Implicit conversions from strings to LocalName etc.

end Namespaces
