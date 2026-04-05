package com.piggypulse.android.feature.dashboard

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class WidgetDefinition(
    val id: String,
    val name: String,
    val description: String,
    val defaultVisible: Boolean = true,
)

val standardWidgetDefinitions = listOf(
    WidgetDefinition("net_position", "Net Position", "Total across all accounts"),
    WidgetDefinition("current_period", "Current Period", "Budget progress for the active period"),
    WidgetDefinition("cash_flow", "Cash Flow", "Inflows vs outflows"),
    WidgetDefinition("recent_transactions", "Recent Transactions", "Latest activity"),
    WidgetDefinition("subscriptions", "Subscriptions", "Recurring charges timeline"),
    WidgetDefinition("spending_trend", "Spending Trend", "Spend over time"),
    WidgetDefinition("top_vendors", "Top Vendors", "Where money goes"),
    WidgetDefinition("variable_categories", "Variable Categories", "Discretionary spending tracker"),
    WidgetDefinition("fixed_categories", "Fixed Categories", "Predictable expenses checklist"),
)

val defaultWidgetOrder = listOf(
    "net_position", "current_period", "cash_flow", "recent_transactions",
    "subscriptions", "spending_trend", "top_vendors",
    "variable_categories", "fixed_categories",
)

const val ACCOUNT_WIDGET_PREFIX = "account:"

@Singleton
class DashboardLayout @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dashboard_layout", Context.MODE_PRIVATE)

    private val _widgetOrder = MutableStateFlow(loadOrder())
    val widgetOrder: StateFlow<List<String>> = _widgetOrder.asStateFlow()

    private val _hiddenWidgets = MutableStateFlow(loadHidden())
    val hiddenWidgets: StateFlow<Set<String>> = _hiddenWidgets.asStateFlow()

    val visibleWidgets: List<String>
        get() = _widgetOrder.value.filter { it !in _hiddenWidgets.value }

    fun moveWidget(fromIndex: Int, toIndex: Int) {
        val visible = visibleWidgets.toMutableList()
        if (fromIndex !in visible.indices || toIndex !in visible.indices) return
        val item = visible.removeAt(fromIndex)
        visible.add(toIndex, item)
        // Reconstruct full order: visible in new order + hidden items maintain position
        val hidden = _widgetOrder.value.filter { it in _hiddenWidgets.value }
        _widgetOrder.value = visible + hidden
        save()
    }

    fun removeWidget(id: String) {
        _hiddenWidgets.value = _hiddenWidgets.value + id
        save()
    }

    fun addWidget(id: String) {
        _hiddenWidgets.value = _hiddenWidgets.value - id
        // If it's a new widget not in order, add to end
        if (id !in _widgetOrder.value) {
            _widgetOrder.value = _widgetOrder.value + id
        }
        save()
    }

    fun isAccountWidget(id: String): Boolean = id.startsWith(ACCOUNT_WIDGET_PREFIX)

    fun accountIdFromWidget(widgetId: String): String? {
        return if (isAccountWidget(widgetId)) widgetId.removePrefix(ACCOUNT_WIDGET_PREFIX) else null
    }

    fun accountWidgetId(accountId: String): String = "$ACCOUNT_WIDGET_PREFIX$accountId"

    private fun save() {
        prefs.edit()
            .putStringSet(KEY_ORDER, null) // clear old set format
            .putString(KEY_ORDER_LIST, _widgetOrder.value.joinToString(","))
            .putStringSet(KEY_HIDDEN, _hiddenWidgets.value)
            .apply()
    }

    private fun loadOrder(): List<String> {
        val saved = prefs.getString(KEY_ORDER_LIST, null)
        return if (saved != null && saved.isNotBlank()) {
            saved.split(",")
        } else {
            defaultWidgetOrder
        }
    }

    private fun loadHidden(): Set<String> {
        return prefs.getStringSet(KEY_HIDDEN, emptySet()) ?: emptySet()
    }

    companion object {
        private const val KEY_ORDER_LIST = "widget_order_list"
        private const val KEY_HIDDEN = "hidden_widgets"
        private const val KEY_ORDER = "widget_order"
    }
}
