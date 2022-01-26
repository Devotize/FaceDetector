package com.sychev.facedetector.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ImageDataEntity.TABLE_NAME)
data class ImageDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1,
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray,
) {
    companion object {
        const val TABLE_NAME = "image_data_table_name"
    }
}