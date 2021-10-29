package com.sychev.facedetector.presentation.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailScreen
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailViewModel
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClothesRetailActivity: AppCompatActivity() {


    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ClothesRetailActivity called")
        val bundle = intent.extras
        val clothesList = bundle?.getParcelableArrayList<Clothes>("clothes_list") ?: listOf<Clothes>()
        val selectedClothes = bundle?.getParcelableArrayList<Clothes>("selected_clothes") ?: listOf<Clothes>()
        Log.d(TAG, "onCreate: clothesList: $clothesList")

        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.ClothesListRetail.route) {
                    composable(Screen.ClothesListRetail.route){

                        val clothesListRetailViewModel: ClothesListRetailViewModel = hiltViewModel(
                            navController.getBackStackEntry(Screen.ClothesListRetail.route)
                        )

//                        ClothesListRetailScreen(
//                            viewModel = clothesListRetailViewModel,
//                            clothesList = clothesList,
//                            selectedClothes = selectedClothes,
//                            onBackClick = {
//                                onBackPressed()
//                            }
//                        )
                    }
                }
            }
        }
    }

}