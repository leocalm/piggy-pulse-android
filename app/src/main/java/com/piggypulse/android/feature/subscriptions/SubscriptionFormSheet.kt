package com.piggypulse.android.feature.subscriptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.piggypulse.android.core.model.CreateSubscriptionRequest
import com.piggypulse.android.core.model.SubscriptionItem
import com.piggypulse.android.core.model.UpdateSubscriptionRequest
import com.piggypulse.android.core.util.CurrencyFormatter
import com.piggypulse.android.design.component.PpButton
import com.piggypulse.android.design.component.PpTextField
import com.piggypulse.android.design.theme.PpTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFormSheet(
    subscription: SubscriptionItem?,
    onSave: (CreateSubscriptionRequest) -> Unit,
    onUpdate: (String, UpdateSubscriptionRequest) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEditing = subscription != null
    var name by remember { mutableStateOf(subscription?.name ?: "") }
    var amountText by remember {
        mutableStateOf(
            if (subscription != null) CurrencyFormatter.centsToDisplay(subscription.billingAmount).toString()
            else "",
        )
    }
    var billingCycle by remember { mutableStateOf(subscription?.billingCycle ?: "monthly") }
    var billingDayText by remember { mutableStateOf(subscription?.billingDay?.toString() ?: "1") }
    var nextChargeDate by remember { mutableStateOf(subscription?.nextChargeDate ?: LocalDate.now().toString()) }
    var categoryId by remember { mutableStateOf(subscription?.categoryId ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PpTheme.colors.card,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = if (isEditing) "Edit subscription" else "New subscription",
                style = MaterialTheme.typography.titleLarge,
                color = PpTheme.colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            PpTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = "Billing amount",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = billingCycle,
                onValueChange = { billingCycle = it },
                label = "Billing cycle (monthly/quarterly/yearly)",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = billingDayText,
                onValueChange = { billingDayText = it },
                label = "Billing day (1-31)",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = nextChargeDate,
                onValueChange = { nextChargeDate = it },
                label = "Next charge date (YYYY-MM-DD)",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            PpTextField(
                value = categoryId,
                onValueChange = { categoryId = it },
                label = "Category ID",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))

            val amount = amountText.toDoubleOrNull()
            val billingDay = billingDayText.toIntOrNull()
            val canSave = name.isNotBlank() && amount != null && amount > 0 &&
                billingDay != null && billingDay in 1..31 && categoryId.isNotBlank()

            PpButton(
                text = if (isEditing) "Save changes" else "Create subscription",
                onClick = {
                    val cents = CurrencyFormatter.displayToCents(amount!!)
                    if (isEditing) {
                        onUpdate(
                            subscription!!.id,
                            UpdateSubscriptionRequest(name, categoryId, null, cents, billingCycle, billingDay!!, nextChargeDate),
                        )
                    } else {
                        onSave(
                            CreateSubscriptionRequest(name, categoryId, null, cents, billingCycle, billingDay!!, nextChargeDate),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave,
            )
        }
    }
}
