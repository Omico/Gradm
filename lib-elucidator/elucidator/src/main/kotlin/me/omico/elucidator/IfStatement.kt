/*
 * Copyright 2023 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.omico.elucidator

interface IfStatementScope {
    fun start(condition: String, vararg args: Any, block: FunctionScope.() -> Unit)
    fun then(condition: String, vararg args: Any, block: FunctionScope.() -> Unit)
    fun final(block: FunctionScope.() -> Unit)
    fun build()
}

internal class IfStatementBuilder(
    private val builder: ConditionalStatementBuilder,
) : IfStatementScope {
    override fun start(condition: String, vararg args: Any, block: FunctionScope.() -> Unit): Unit =
        builder.begin(condition = "if ($condition)", args = args, block = block)

    override fun then(condition: String, vararg args: Any, block: FunctionScope.() -> Unit): Unit =
        builder.next(condition = "else if ($condition)", args = args, block = block)

    override fun final(block: FunctionScope.() -> Unit): Unit =
        builder.next(condition = "else", block = block)

    override fun build(): Unit = builder.end()
}

fun FunctionScope.addIfStatement(block: IfStatementScope.() -> Unit): Unit =
    ConditionalStatementBuilder(this).let(::IfStatementBuilder).apply(block).build()
