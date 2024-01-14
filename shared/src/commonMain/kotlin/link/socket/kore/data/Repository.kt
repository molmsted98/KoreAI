package link.socket.kore.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


abstract class Repository<Key : Any, Value>(
    open val scope: CoroutineScope,
) {

    private val values: MutableMap<Key, Value> = mutableMapOf()

    private val _valuesFlow: MutableStateFlow<Map<Key, Value>> = MutableStateFlow(values)
    private val valuesFlow: StateFlow<Map<Key, Value>> = _valuesFlow

    fun storeValue(key: Key, value: Value) {
        values[key] = value

        scope.launch {
            _valuesFlow.emit(values)
        }
    }

    fun getValue(key: Key): Value? =
        values.getOrDefault(key, null)

    fun observeValue(key: Key): StateFlow<Value?> =
        valuesFlow
            .map { it[key] }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Lazily, null)
}