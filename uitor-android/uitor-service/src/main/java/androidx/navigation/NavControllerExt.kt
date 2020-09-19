package androidx.navigation

import java.util.AbstractCollection

private val backStackField = NavController::class.java.declaredFields
    .first { it.name == "mBackStack" }
    .apply { isAccessible = true }

fun internalExtractBackStack(navController: NavController): List<String>? {
    val collection = backStackField.get(navController)
        as? AbstractCollection<NavBackStackEntry>
    return collection?.map { it.destination.displayName }
}
