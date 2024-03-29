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

package eu.cdevreeze.yaidom3.experimental.saxon

import eu.cdevreeze.yaidom3.experimental.queryapi.ElemQueryApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ElemStepFactory
import eu.cdevreeze.yaidom3.experimental.queryapi.ToYaidom
import net.sf.saxon.s9api.XdmNode

/**
 * Saxon "givens".
 *
 * @author
 *   Chris de Vreeze
 */
object SaxonGivens:

  given toYaidom: ToYaidom[XdmNode, SaxonNode.Elem] = SaxonToYaidom

  given elemStepFactory: ElemStepFactory[XdmNode] = SaxonElemStepFactory
  
  given elemQueryApi: ElemQueryApi[XdmNode] = SaxonElemQueryApi

end SaxonGivens
