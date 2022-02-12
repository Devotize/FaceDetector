package com.sychev.feature.gallery.impl.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.sychev.common.Destinations
import com.sychev.common.di.injectedViewModel
import com.sychev.feature.gallery.api.GalleryEntryPoint
import com.sychev.feature.gallery.impl.di.DaggerGalleryComponent
import com.sychev.feature.gallery.impl.di.GalleryComponent
import javax.inject.Inject

class GalleryEntryPointImpl @Inject constructor() : GalleryEntryPoint() {

    @Composable
    override fun NavGraphBuilder.Composable(
        navController: NavHostController,
        destinations: Destinations,
        backStackEntry: NavBackStackEntry
    ) {
        val component = initializeComponent()
        val viewModel = injectedViewModel {
            component.viewModel
        }
        Surface {

        }
    }

    @Composable
    private fun initializeComponent(): GalleryComponent {
        return DaggerGalleryComponent
            .builder()
            .build()
    }

}