package com.sychev.facedetector.data.remote.model


import com.google.gson.annotations.SerializedName

class UnsplashDto : ArrayList<UnsplashDto.UnsplashDtoItem>(){
    data class UnsplashDtoItem(
        @SerializedName("alt_description")
        val altDescription: String,
        @SerializedName("blur_hash")
        val blurHash: String,
        @SerializedName("categories")
        val categories: List<Any>,
        @SerializedName("color")
        val color: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("current_user_collections")
        val currentUserCollections: List<Any>,
        @SerializedName("description")
        val description: String?,
        @SerializedName("downloads")
        val downloads: Int,
        @SerializedName("exif")
        val exif: Exif,
        @SerializedName("height")
        val height: Int,
        @SerializedName("id")
        val id: String,
        @SerializedName("liked_by_user")
        val likedByUser: Boolean,
        @SerializedName("likes")
        val likes: Int,
        @SerializedName("links")
        val links: Links,
        @SerializedName("location")
        val location: Location,
        @SerializedName("promoted_at")
        val promotedAt: String?,
        @SerializedName("sponsorship")
        val sponsorship: Any?,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("urls")
        val urls: Urls,
        @SerializedName("user")
        val user: User,
        @SerializedName("views")
        val views: Int,
        @SerializedName("width")
        val width: Int
    ) {
        data class Exif(
            @SerializedName("aperture")
            val aperture: String,
            @SerializedName("exposure_time")
            val exposureTime: String,
            @SerializedName("focal_length")
            val focalLength: String,
            @SerializedName("iso")
            val iso: Int,
            @SerializedName("make")
            val make: String,
            @SerializedName("model")
            val model: String
        )
    
        data class Links(
            @SerializedName("download")
            val download: String,
            @SerializedName("download_location")
            val downloadLocation: String,
            @SerializedName("html")
            val html: String,
            @SerializedName("self")
            val self: String
        )
    
        data class Location(
            @SerializedName("city")
            val city: Any?,
            @SerializedName("country")
            val country: Any?,
            @SerializedName("name")
            val name: Any?,
            @SerializedName("position")
            val position: Position,
            @SerializedName("title")
            val title: Any?
        ) {
            data class Position(
                @SerializedName("latitude")
                val latitude: Any?,
                @SerializedName("longitude")
                val longitude: Any?
            )
        }
    
        data class Urls(
            @SerializedName("full")
            val full: String,
            @SerializedName("raw")
            val raw: String,
            @SerializedName("regular")
            val regular: String,
            @SerializedName("small")
            val small: String,
            @SerializedName("thumb")
            val thumb: String
        )
    
        data class User(
            @SerializedName("accepted_tos")
            val acceptedTos: Boolean,
            @SerializedName("bio")
            val bio: Any?,
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("for_hire")
            val forHire: Boolean,
            @SerializedName("id")
            val id: String,
            @SerializedName("instagram_username")
            val instagramUsername: String,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("links")
            val links: Links,
            @SerializedName("location")
            val location: Any?,
            @SerializedName("name")
            val name: String,
            @SerializedName("portfolio_url")
            val portfolioUrl: Any?,
            @SerializedName("profile_image")
            val profileImage: ProfileImage,
            @SerializedName("social")
            val social: Social,
            @SerializedName("total_collections")
            val totalCollections: Int,
            @SerializedName("total_likes")
            val totalLikes: Int,
            @SerializedName("total_photos")
            val totalPhotos: Int,
            @SerializedName("twitter_username")
            val twitterUsername: Any?,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("username")
            val username: String
        ) {
            data class Links(
                @SerializedName("followers")
                val followers: String,
                @SerializedName("following")
                val following: String,
                @SerializedName("html")
                val html: String,
                @SerializedName("likes")
                val likes: String,
                @SerializedName("photos")
                val photos: String,
                @SerializedName("portfolio")
                val portfolio: String,
                @SerializedName("self")
                val self: String
            )
    
            data class ProfileImage(
                @SerializedName("large")
                val large: String,
                @SerializedName("medium")
                val medium: String,
                @SerializedName("small")
                val small: String
            )
    
            data class Social(
                @SerializedName("instagram_username")
                val instagramUsername: String,
                @SerializedName("paypal_email")
                val paypalEmail: Any?,
                @SerializedName("portfolio_url")
                val portfolioUrl: Any?,
                @SerializedName("twitter_username")
                val twitterUsername: Any?
            )
        }
    }
}