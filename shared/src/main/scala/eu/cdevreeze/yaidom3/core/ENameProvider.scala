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

import eu.cdevreeze.yaidom3.core.ENameProvider.TrivialENameProvider
import eu.cdevreeze.yaidom3.core.Namespaces._

/**
 * Expanded name provider. Efficient implementations help reduce the number of EName instances for ENames that are equal.
 *
 * @author
 *   Chris de Vreeze
 */
trait ENameProvider:

  def ename(namespaceOption: Option[Namespace], localPart: LocalName): EName

  def ename(namespace: Namespace, localPart: LocalName): EName

  /**
   * Creates an EName without namespace.
   */
  def ename(localPart: LocalName): EName

  def parseEName(enameString: String): EName

object ENameProvider:

  object TrivialENameProvider extends ENameProvider:

    def ename(namespaceOption: Option[Namespace], localPart: LocalName): EName = EName.of(namespaceOption, localPart)

    def ename(namespace: Namespace, localPart: LocalName): EName = EName.of(namespace, localPart)

    def ename(localPart: LocalName): EName = EName.of(localPart)

    def parseEName(enameString: String): EName = EName.parse(enameString)

  end TrivialENameProvider

end ENameProvider
