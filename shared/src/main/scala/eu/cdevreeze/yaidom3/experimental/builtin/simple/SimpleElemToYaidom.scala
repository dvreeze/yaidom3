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

package eu.cdevreeze.yaidom3.experimental.builtin.simple

import eu.cdevreeze.yaidom3.experimental.queryapi.ToYaidom

/**
 * ToYaidom type class instance for SimpleNode.Elem.
 *
 * @author
 *   Chris de Vreeze
 */
object SimpleElemToYaidom extends ToYaidom[SimpleNode.Elem, SimpleNode.Elem]:
  
  extension (elem: SimpleNode.Elem)
    def wrap: SimpleNode.Elem = elem
