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
 * Navigation paths.
 *
 * @author
 *   Chris de Vreeze
 */
object Navigation:

  /**
   * Child element index of the child element to navigate to. This is not a child node index, but a child element index, zero-based.
   */
  opaque type NavigationStep = Int

  object NavigationStep:
    def apply(childElemIdx: Int): NavigationStep = childElemIdx
  end NavigationStep

  opaque type NavigationPath = Seq[NavigationStep]

  object NavigationPath:
    def apply(steps: Seq[NavigationStep]): NavigationPath = steps
    def from(steps: Seq[Int]): NavigationPath = steps.map(NavigationStep.apply)

    val empty: NavigationPath = Seq.empty[NavigationStep]
  end NavigationPath

  extension (step: NavigationStep) def toInt: Int = step

  extension (path: NavigationPath)
    def steps: Seq[NavigationStep] = path
    def isEmpty: Boolean = path.steps.isEmpty
    def nonEmpty: Boolean = path.steps.nonEmpty
    def appended(step: NavigationStep): NavigationPath = path.steps.appended(step)
    def appendedAll(otherPath: NavigationPath): NavigationPath = path.steps.appendedAll(otherPath)
    def prepended(step: NavigationStep): NavigationPath = path.steps.prepended(step)
    def init: NavigationPath = path.steps.init
    def initOption: Option[NavigationPath] = if isEmpty then None else Some(path.steps.init)
    def head: NavigationStep = path.steps.head
    def headOption: Option[NavigationStep] = if isEmpty then None else Some(path.steps.head)
    def tail: NavigationPath = path.steps.tail
    def tailOption: Option[NavigationPath] = if isEmpty then None else Some(path.steps.tail)

// TODO Parse navigation path from string

end Navigation
