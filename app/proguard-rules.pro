# Add project specific ProGuard rules here.
# By default, Android Studio will automatically add rules that are appropriate
# for the current project type, including rules for third-party libraries
# that are commonly used.

-dontwarn kotlin.Metadata
-dontwarn java.time.**

# Room
-keepnames class * extends androidx.room.RoomDatabase
-keepnames class com.androidforge.streakup.data.local.entity.* { *; }
-keepnames class com.androidforge.streakup.data.local.dao.* { *; }
-keep class com.androidforge.streakup.data.local.database.Converters { *; } # Keep Room TypeConverter

# Hilt
-keep class dagger.hilt.android.internal.managers.HiltController
-keep class dagger.hilt.internal.aggregatedroot.AggregatedRoot* { *; }
-keep class dagger.hilt.internal.definecomponent.DefineComponent* { *; }
-keep class dagger.hilt.android.internal.builders.* { *; }
-keep class dagger.hilt.android.internal.modules.* { *; }
-keep class dagger.hilt.android.internal.lifecycle.* { *; }
-keep class dagger.hilt.android.internal.testing.* { *; }
-keep class dagger.hilt.android.internal.uninstallmodules.* { *; }
-keep class dagger.hilt.internal.processedroots.ProcessedRoots* { *; }
-keep class com.androidforge.streakup.StreakUpApplication

# WorkManager
-keep class androidx.work.impl.background.systemalarm.RescheduleReceiver
-keep class com.androidforge.streakup.data.workers.ReminderWorker { *; }
-keepnames class * extends androidx.work.ListenableWorker

# AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep public class com.google.android.gms.common.internal.safeparcel.**
-keep public class com.google.android.gms.ads.formats.** { *; }
-keep public class com.google.android.gms.ads.identifier.** { *; }
-keep public class com.google.ads.** { *; }
-keep class com.google.gson.Gson
-keep class com.google.gson.TypeAdapter
-keep class com.google.gson.reflect.TypeToken
-keep class com.google.gson.internal.UnsafeAllocator
-keep class com.google.gson.stream.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.google.ads.**
-dontwarn com.google.gson.**

# Timber
-dontwarn timber.log.**
-keep class timber.log.Timber { *; }

# Kotlinx Coroutines
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepnames class kotlinx.coroutines.flow.internal.SafeCollector { *; }
-dontwarn kotlinx.coroutines.flow.SafeFlow

# DataStore
-keep class androidx.datastore.preferences.protobuf.GeneratedMessageLite$DefaultInstanceBasedParser { *; }
-keep class androidx.datastore.preferences.protobuf.ExtensionRegistryLite { *; }
-keep class androidx.datastore.preferences.protobuf.GeneratedMessageLite { *; }
-keep class androidx.datastore.preferences.protobuf.ExtensionRegistryFactory { *; }
-keep class androidx.datastore.preferences.protobuf.GeneratedMessageLite$GeneratedExtension { *; }
-keep class androidx.datastore.preferences.protobuf.WireFormat$FieldType { *; }
-keep class androidx.datastore.preferences.protobuf.WireFormat$JavaType { *; }
-dontwarn com.google.protobuf.**
-dontwarn okio.**

# Compose specific rules (often handled by default, but good to ensure)
-keep class * implements androidx.compose.runtime.internal.StabilityInferred { *; }
-keep class * implements androidx.compose.runtime.internal.ComposableLambda { *; }
-keep class * implements androidx.compose.runtime.internal.ComposableLambdaN { *; }
-keep class * implements androidx.compose.ui.tooling.preview.PreviewParameterProvider { *; }
-keep class * implements androidx.compose.ui.tooling.preview.PreviewParameter { *; }
-keep class * extends androidx.compose.ui.tooling.preview.PreviewParameterProvider { *; }
-keep class * extends androidx.compose.ui.tooling.preview.PreviewParameter { *; }
-keep class androidx.compose.ui.tooling.preview.** { *; }