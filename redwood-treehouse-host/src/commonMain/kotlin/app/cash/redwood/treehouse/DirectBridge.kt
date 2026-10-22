/*
 * Copyright (C) 2026 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.redwood.treehouse

import app.cash.redwood.protocol.BridgeChange
import app.cash.redwood.protocol.Change
import app.cash.redwood.protocol.ChangesSink
import app.cash.redwood.protocol.ChildrenChange
import app.cash.redwood.protocol.Create
import app.cash.redwood.protocol.ModifierChange
import app.cash.redwood.protocol.ModifierElement
import app.cash.redwood.protocol.PropertyChange
import app.cash.zipline.DirectChangeSink
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * A per-zipline-session bridge between the Hermes direct changes channel and the host UI.
 *
 * One instance exists per zipline session (that is, per screen of a Clive app). Each session's
 * QuickJS runtime writes changes through its own [DirectChangeSink] adapter (see
 * [asDirectChangeSink]) which routes them to this instance's [callsink] — the host-side
 * [ChangesSink] that applies them to the UI.
 *
 * The stateless protocol-object factories live in the companion so the Android JNI bridge
 * can keep calling them as static methods.
 */
public expect class DirectBridge() : ChangesSink {

  internal var callsink: ChangesSink?
  public override fun sendChanges(changes: List<Change>)
  public fun sendBatch(changes: List<Change>)

  public fun asDirectChangeSink(): DirectChangeSink

  public companion object {
    public fun createCreate(id: Int, tag: Int): Create
    public fun createAdd(id: Int, tag: Int, childId: Int, index: Int): ChildrenChange
    public fun createRemove(id: Int, tag: Int, index: Int, detach: Boolean): ChildrenChange
    public fun createMove(id: Int, tag: Int, fromIndex: Int, toIndex: Int, count: Int): ChildrenChange
    public fun createPropertyChange(id: Int, widgetTag: Int, propertyTag: Int, value: JsonElement): PropertyChange
    public fun createModifierChange(id: Int, elements: List<ModifierElement>): ModifierChange
    public fun createModifierElement(tag: Int, value: JsonElement): ModifierElement
    public fun createBridgeChange(id: Int, wrapped: Any?): BridgeChange
    public fun jsonPrimitiveString(value: String): JsonPrimitive
    public fun jsonPrimitiveInt(value: Int): JsonPrimitive
    public fun jsonPrimitiveLong(value: Long): JsonPrimitive
    public fun jsonPrimitiveDouble(value: Double): JsonPrimitive
    public fun jsonPrimitiveBoolean(value: Boolean): JsonPrimitive
    public fun jsonNull(): JsonNull
    public fun createJsonArray(elements: List<JsonElement>): JsonArray
    public fun createJsonObject(keys: List<String>, values: List<JsonElement>): JsonObject
  }
}
