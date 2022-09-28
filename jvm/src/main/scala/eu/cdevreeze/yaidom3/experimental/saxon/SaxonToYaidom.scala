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

import eu.cdevreeze.yaidom3.experimental.queryapi.ElemApi
import eu.cdevreeze.yaidom3.experimental.queryapi.ToYaidom
import net.sf.saxon.s9api.XdmNode

/**
 * ToYaidom type class instance for Saxon.
 *
 * @author
 *   Chris de Vreeze
 */
object SaxonToYaidom extends ToYaidom[XdmNode, SaxonElem]:

  extension (elem: XdmNode)
    def wrap: ElemApi[SaxonElem, XdmNode] = SaxonElem(elem)
