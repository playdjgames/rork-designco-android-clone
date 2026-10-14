package com.DesignCo.djgames.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.MessageAction
import com.DesignCo.djgames.ui.screens.AccountScreen
import com.DesignCo.djgames.ui.screens.AppointmentsScreen
import com.DesignCo.djgames.ui.screens.AuthScreen
import com.DesignCo.djgames.ui.screens.BookScreen
import com.DesignCo.djgames.ui.screens.CartScreen
import com.DesignCo.djgames.ui.screens.ClubScreen
import com.DesignCo.djgames.ui.screens.FavoritesScreen
import com.DesignCo.djgames.ui.screens.HomeScreen
import com.DesignCo.djgames.ui.screens.OrdersScreen
import com.DesignCo.djgames.ui.screens.ProductDetailScreen
import com.DesignCo.djgames.ui.screens.ShopScreen
import com.DesignCo.djgames.ui.theme.DC

/** Top-level destinations shown in the floating tab bar. */
enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Filled.Home),
    Book("book", "Book", Icons.Filled.EditCalendar),
    Appointments("appointments", "Appointments", Icons.Filled.CalendarMonth),
    Favorites("favorites", "Favorites", Icons.Filled.Favorite),
    Account("account", "Profile", Icons.Filled.AccountCircle)
}

object Routes {
    const val CLUB = "club"
    const val SHOP = "shop"
    const val PRODUCT = "product/{id}"
    const val CART = "cart"
    const val ORDERS = "orders"
    const val AUTH = "auth/{mode}"
    fun product(id: String): String = "product/$id"
    fun auth(mode: String): String = "auth/$mode"
}

/** Bottom inset tab screens add so their last item clears the floating tab bar. */
@Composable
fun tabBarClearance(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 104.dp

/** Navigation actions shared by every screen. */
class AppNavigator(private val navController: NavHostController) {
    fun openTab(tab: Tab) {
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun openClub() = navController.navigate(Routes.CLUB) { launchSingleTop = true }
    fun openShop() = navController.navigate(Routes.SHOP) { launchSingleTop = true }
    fun openProduct(id: String) = navController.navigate(Routes.product(id))
    fun openCart() = navController.navigate(Routes.CART) { launchSingleTop = true }
    fun openOrders() = navController.navigate(Routes.ORDERS)
    fun openAuth(mode: String) = navController.navigate(Routes.auth(mode))
    fun back() {
        navController.popBackStack()
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val vm: AppViewModel = viewModel()
    val navigator = remember(navController) { AppNavigator(navController) }
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val currentTab = Tab.entries.firstOrNull { it.route == currentRoute }

    LaunchedEffect(Unit) {
        vm.deliverWaitlistAlerts()
        vm.messages.collect { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = message.text,
                actionLabel = message.action?.label,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                when (message.action) {
                    MessageAction.ViewBag -> navigator.openCart()
                    MessageAction.OpenClub -> navigator.openClub()
                    MessageAction.OpenAppointments -> navigator.openTab(Tab.Appointments)
                    MessageAction.OpenBook -> navigator.openTab(Tab.Book)
                    null -> Unit
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DC.Canvas)
    ) {
        NavHost(
            navController = navController,
            startDestination = Tab.Home.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable(Tab.Home.route) { HomeScreen(vm, navigator) }
            composable(Tab.Book.route) { BookScreen(vm, navigator) }
            composable(Tab.Appointments.route) { AppointmentsScreen(vm, navigator) }
            composable(Tab.Favorites.route) { FavoritesScreen(vm, navigator) }
            composable(Tab.Account.route) { AccountScreen(vm, navigator) }
            composable(Routes.CLUB) { ClubScreen(vm, navigator) }
            composable(Routes.SHOP) { ShopScreen(vm, navigator) }
            composable(
                Routes.PRODUCT,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                ProductDetailScreen(entry.arguments?.getString("id").orEmpty(), vm, navigator)
            }
            composable(Routes.CART) { CartScreen(vm, navigator) }
            composable(Routes.ORDERS) { OrdersScreen(vm, navigator) }
            composable(
                Routes.AUTH,
                arguments = listOf(navArgument("mode") { type = NavType.StringType })
            ) { entry ->
                AuthScreen(entry.arguments?.getString("mode") ?: "create", vm, navigator)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    containerColor = DC.SurfaceHigh,
                    contentColor = DC.Ivory,
                    actionColor = DC.Gold
                )
            }
            AnimatedVisibility(
                visible = currentTab != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                FloatingTabBar(
                    current = currentTab ?: Tab.Home,
                    onSelect = navigator::openTab
                )
            }
        }
    }
}

@Composable
private fun FloatingTabBar(
    current: Tab,
    onSelect: (Tab) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(40.dp)
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 10.dp)
            .shadow(24.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(DC.Surface.copy(alpha = 0.96f))
            .border(1.dp, DC.Border, shape)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Tab.entries.forEach { tab ->
                NavigationBarItem(
                    selected = tab == current,
                    onClick = {
                        if (tab != current) haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onSelect(tab)
                    },
                    icon = { Icon(tab.icon, contentDescription = null) },
                    label = {
                        Text(
                            tab.label,
                            maxLines = 1,
                            fontSize = 11.sp,
                            fontWeight = if (tab == current) FontWeight.SemiBold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DC.Gold,
                        selectedTextColor = DC.Gold,
                        indicatorColor = DC.SurfaceHigh,
                        unselectedIconColor = DC.Ivory,
                        unselectedTextColor = DC.Ivory
                    )
                )
            }
        }
    }
}
