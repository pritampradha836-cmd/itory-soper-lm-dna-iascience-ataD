package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "saved_notes")
@JsonClass(generateAdapter = true)
data class SavedNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val type: String, // "AI", "ML", "DL", "NOTE"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "model_sessions")
@JsonClass(generateAdapter = true)
data class ModelSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val modelName: String, // "Neural Network", "K-Means Clustering", "Linear Regression"
    val datasetName: String,
    val finalMetricName: String, // "Loss", "Silhouette Score", "MSE"
    val finalMetricValue: Double,
    val epochCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// Data Class for Linear Regression 2D points
data class Point2D(
    val x: Float,
    val y: Float
)

// Data Class for K-Means points & assignment
data class ClusterPoint(
    val x: Float,
    val y: Float,
    var clusterIndex: Int = -1 // -1 means unassigned
)

data class Centroid(
    var x: Float,
    var y: Float,
    val colorHex: String
)
