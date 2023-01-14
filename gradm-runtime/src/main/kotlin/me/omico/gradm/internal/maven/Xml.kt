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
package me.omico.gradm.internal.maven

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

internal val documentBuilder: DocumentBuilder by lazy { DocumentBuilderFactory.newInstance().newDocumentBuilder() }

internal fun <T> NodeList.map(transform: (Node) -> T): List<T> =
    (0 until length).map { index -> transform(item(index)) }

internal fun <T> NodeList.mapNotNull(transform: (Node) -> T?): List<T> =
    (0 until length).mapNotNull { index -> transform(item(index)) }

internal fun NodeList.forEach(transform: (Node) -> Unit): Unit =
    (0 until length).forEach { index -> transform(item(index)) }
