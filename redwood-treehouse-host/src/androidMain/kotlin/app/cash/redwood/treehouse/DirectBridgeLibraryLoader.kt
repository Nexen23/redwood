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

/**
 * Loads the bridge native library. Called once before any Zipline operations.
 *
 * On Android, the library is packaged into the APK via [externalNativeBuild]
 * (see [CMakeLists.txt](src/androidMain/cpp/CMakeLists.txt)).
 * It must be loaded after libhermesvmlean (loaded by Zipline's JsEngine) so Hermes
 * symbols are available for resolution.
 */
public object DirectBridgeLibraryLoader {

  public fun load() {
    System.loadLibrary("redwood-bridge")

    // The bridge dispatch (directChangeToJava) calls JNI FindClass for bridge-
    // annotated classes (e.g. ChildrenChange$Add). These classes pull in
    // kotlinx.serialization, whose transitive dependencies include
    // CoroutineExceptionHandlerImplKt. Its <clinit> calls ServiceLoader.load(),
    // which reads META-INF/services/ from the APK — a disk read. If this
    // happens on a thread with StrictMode enabled, the DiskReadViolation
    // marks the class as failed, and the next coroutine exception on main
    // crashes with NoClassDefFoundError.
    //
    // Preload the class now (while allowThreadDiskReads is still in effect)
    // so its <clinit> completes before StrictMode is re-enabled.
    try {
      Class.forName("kotlinx.coroutines.internal.CoroutineExceptionHandlerImplKt")
    } catch (_: Exception) { }
  }
}
