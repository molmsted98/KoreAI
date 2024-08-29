package link.socket.kore.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A base repository class that provides fundamental operations to store and manage values in a mutable map.
 * This class utilizes Kotlin coroutines and flows to store values and monitor changes.
 *
 * @param Key The type of keys that will be used to identify stored values.
 * @param Value The type of values that will be stored in the repository.
 * @property scope The CoroutineScope used for launching coroutine operations within repository functions.
 */
abstract class Repository<Key : Any, Value>(
    open val scope: CoroutineScope,
) {

    private val values: MutableMap<Key, Value> = mutableMapOf()

    // MutableStateFlow to emit the changes in the values map
    private val _valuesFlow: MutableStateFlow<Map<Key, Value>> = MutableStateFlow(values)
    // Read-only StateFlow exposed to observers
    private val valuesFlow: StateFlow<Map<Key, Value>> = _valuesFlow

    /**
     * Stores a value associated with the specified key and emits the updated map via a flow.
     *
     * @param key The key with which the value will be associated.
     * @param value The value to be stored.
     */
    fun storeValue(key: Key, value: Value) {
        values[key] = value

        // Emit the updated values map using the provided CoroutineScope
        scope.launch {
            _valuesFlow.emit(values)
        }
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value linked to the specified key, or null if the key doesn't exist.
     */
    fun getValue(key: Key): Value? = values[key]

    /**
     * Observes all stored values in the repository. Returns a StateFlow containing the map of all key-value pairs.
     *
     * @return A StateFlow that emits the map of all stored key-value pairs.
     */
    fun observeValues(): StateFlow<Map<Key, Value>> = valuesFlow

    /**
     * Observes the value associated with the specified key. Returns a StateFlow which
     * emits the value whenever it changes.
     *
     * @param key The key whose associated value is to be observed.
     * @return A StateFlow that emits the value linked to the specified key whenever it changes.
     */
    fun observeValue(key: Key): StateFlow<Value?> =
        valuesFlow
            .map { it[key] }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Lazily, null)
}