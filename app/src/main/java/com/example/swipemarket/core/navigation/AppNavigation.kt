package com.example.swipemarket.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.swipemarket.core.data.UserRole
import com.example.swipemarket.core.data.mockProducts
import com.example.swipemarket.core.data.remote.ApiClient
import com.example.swipemarket.feature.auth.ui.AccountRole
import com.example.swipemarket.feature.auth.ui.ForgotPasswordScreen
import com.example.swipemarket.feature.auth.ui.LoginScreen
import com.example.swipemarket.feature.auth.ui.RegisterStep1Screen
import com.example.swipemarket.feature.auth.ui.RegisterStep2Screen
import com.example.swipemarket.feature.auth.ui.RegisterStep3Screen
import com.example.swipemarket.feature.auth.ui.WelcomeScreen
import com.example.swipemarket.feature.chat.ui.ChatScreen
import com.example.swipemarket.feature.detail.ui.ProductDetailScreen
import com.example.swipemarket.feature.feed.ui.MarketplaceScreen
import com.example.swipemarket.feature.placeholder.ui.ChatsScreen
import com.example.swipemarket.feature.placeholder.ui.ProfileScreen
import com.example.swipemarket.feature.placeholder.ui.SellScreen
import com.example.swipemarket.feature.splash.ui.SplashScreen
import com.example.swipemarket.feature.swipe.ui.SwipeScreen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull

/** Rutas del prototipo navegable de SwipeMarket. */
enum class AppScreen(val route: String) {
    Splash("splash"),
    Welcome("welcome"),
    Login("login"),
    Register1("register1"),
    Register2("register2"),
    Register3("register3"),
    ForgotPassword("forgot_password"),
    Marketplace("marketplace"),
    Sell("sell"),
    Chats("chats"),
    Profile("profile"),
    Swipe("swipe"),
    ProductDetail("product_detail/{productId}"),
    Chat("chat");

    companion object {
        const val ProductIdArg = "productId"

        fun productDetailRoute(productId: Int): String = "product_detail/$productId"
    }
}

/**
 * Grafo de navegación principal de la app.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedTabIndex = MainTabRoutes.indexOf(currentRoute).takeIf { it >= 0 } ?: 0

    NavHost(
        navController = navController,
        startDestination = AppScreen.Splash.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(250)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(250)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
        }
    ) {
        composable(AppScreen.Splash.route) {
            var isLoading by remember { mutableStateOf(true) }
            var destinationRoute by remember { mutableStateOf<String?>(null) }
            var splashFinished by remember { mutableStateOf(false) }

            // Comprueba token y rol en paralelo a la animación del splash.
            // Cualquier error o timeout se interpreta como "sin sesión" para no
            // dejar la app bloqueada en la pantalla de carga.
            LaunchedEffect(Unit) {
                destinationRoute = try {
                    val session = withTimeoutOrNull(4_000) {
                        val token = ApiClient.tokenManager.getJwtToken()
                        if (token.isNullOrBlank()) {
                            null
                        } else {
                            UserRole.fromApi(ApiClient.tokenManager.getUserRole())
                        }
                    }

                    when (session) {
                        null -> AppScreen.Login.route
                        UserRole.Seller -> AppScreen.Sell.route
                        UserRole.Buyer -> AppScreen.Marketplace.route
                    }
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (error: Exception) {
                    AppScreen.Login.route
                }

                isLoading = false
            }

            // Navega solo cuando terminaron tanto la animación como la lectura.
            LaunchedEffect(isLoading, splashFinished, destinationRoute) {
                val route = destinationRoute
                if (isLoading || !splashFinished || route == null) return@LaunchedEffect

                navController.navigate(route) {
                    popUpTo(AppScreen.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }

            SplashScreen(
                onSplashFinished = { splashFinished = true }
            )
        }

        composable(AppScreen.Welcome.route) {
            WelcomeScreen(
                onCreateAccountClick = navController::goToRegister,
                onLoginClick = navController::goToLogin
            )
        }

        composable(AppScreen.Login.route) {
            LoginScreen(
                onLoginClick = { role -> navController.goToHome(role) },
                onForgotPasswordClick = {
                    navController.navigate(AppScreen.ForgotPassword.route)
                },
                onRegisterClick = navController::goToRegister
            )
        }

        composable(AppScreen.Register1.route) {
            RegisterStep1Screen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = { _, _, _ ->
                    navController.navigate(AppScreen.Register2.route)
                },
                onLoginClick = navController::goToLogin
            )
        }

        composable(AppScreen.Register2.route) {
            RegisterStep2Screen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(AppScreen.Register3.route)
                }
            )
        }

        composable(AppScreen.Register3.route) {
            RegisterStep3Screen(
                onBackClick = { navController.popBackStack() },
                onCreateAccountClick = { accountRole ->
                    val role = when (accountRole) {
                        AccountRole.Seller -> UserRole.Seller
                        AccountRole.Buyer -> UserRole.Buyer
                    }
                    navController.goToHome(role)
                }
            )
        }

        composable(AppScreen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendLinkClick = { /* Simulado hasta conectar el backend. */ }
            )
        }

        composable(AppScreen.Marketplace.route) {
            MarketplaceScreen(
                onProductClick = { product ->
                    navController.navigate(AppScreen.productDetailRoute(product.id))
                },
                onSwipeClick = {
                    navController.navigate(AppScreen.Swipe.route)
                },
                selectedTabIndex = selectedTabIndex,
                onTabSelected = navController::navigateToMainTab
            )
        }

        composable(AppScreen.Sell.route) {
            SellScreen(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = navController::navigateToMainTab
            )
        }

        composable(AppScreen.Chats.route) {
            ChatsScreen(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = navController::navigateToMainTab
            )
        }

        composable(AppScreen.Profile.route) {
            ProfileScreen(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = navController::navigateToMainTab
            )
        }

        composable(AppScreen.Swipe.route) {
            SwipeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = AppScreen.ProductDetail.route,
            arguments = listOf(
                navArgument(AppScreen.ProductIdArg) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt(AppScreen.ProductIdArg) ?: -1
            val product = mockProducts.firstOrNull { it.id == productId }
                ?: mockProducts.first()

            ProductDetailScreen(
                product = product,
                onBackClick = { navController.popBackStack() },
                onMessageSellerClick = {
                    navController.navigate(AppScreen.Chat.route)
                }
            )
        }

        composable(AppScreen.Chat.route) {
            ChatScreen(
                onBackClick = { navController.popBackStack() },
                onSendMessage = { /* Simulado hasta conectar mensajería. */ }
            )
        }
    }
}

private fun NavHostController.goToLogin() {
    navigate(AppScreen.Login.route) {
        popUpTo(AppScreen.Welcome.route)
        launchSingleTop = true
    }
}

private fun NavHostController.goToRegister() {
    navigate(AppScreen.Register1.route) {
        popUpTo(AppScreen.Welcome.route)
        launchSingleTop = true
    }
}

/** Orden de las pestañas de la barra inferior. */
private val MainTabRoutes = listOf(
    AppScreen.Marketplace.route,
    AppScreen.Sell.route,
    AppScreen.Chats.route,
    AppScreen.Profile.route
)

/** Navega entre pestañas conservando la pestaña inicial como raíz. */
private fun NavHostController.navigateToMainTab(index: Int) {
    val route = MainTabRoutes.getOrNull(index) ?: return

    val homeRoute = currentBackStack.value
        .mapNotNull { it.destination.route }
        .firstOrNull { it in MainTabRoutes }
        ?: route

    navigate(route) {
        popUpTo(homeRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Lleva al inicio que corresponde al rol y limpia el flujo de autenticación. */
private fun NavHostController.goToHome(role: UserRole) {
    val destination = when (role) {
        UserRole.Seller -> AppScreen.Sell.route
        UserRole.Buyer -> AppScreen.Marketplace.route
    }

    val firstAuthRoute = currentBackStack.value
        .mapNotNull { it.destination.route }
        .firstOrNull { it != AppScreen.Splash.route }

    navigate(destination) {
        firstAuthRoute?.let { popUpTo(it) { inclusive = true } }
        launchSingleTop = true
    }
}
