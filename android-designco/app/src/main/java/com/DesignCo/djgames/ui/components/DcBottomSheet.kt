package com.DesignCo.djgames.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.DesignCo.djgames.ui.theme.DC

/** Dark Material 3 bottom sheet styled for DesignCo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DcBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = DC.Surface,
        contentColor = DC.Ivory,
        scrimColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f),
        modifier = modifier
    ) {
        Column(Modifier.navigationBarsPadding(), content = content)
    }
}
