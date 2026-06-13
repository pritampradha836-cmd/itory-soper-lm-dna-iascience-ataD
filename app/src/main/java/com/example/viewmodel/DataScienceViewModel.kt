package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DataScienceRepository
import com.example.data.local.AppDatabase
import com.example.data.model.Centroid
import com.example.data.model.ClusterPoint
import com.example.data.model.ModelSession
import com.example.data.model.Point2D
import com.example.data.model.SavedNote
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class DataScienceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataScienceRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DataScienceRepository(database.dao())
    }

    // --- SQLite Flows via Repository ---
    val savedNotes: StateFlow<List<SavedNote>> = repository.allNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val modelSessions: StateFlow<List<ModelSession>> = repository.allSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- UI State Navigation / Panels ---
    var selectedTab by mutableStateOf("deep_learning") // "deep_learning", "machine_learning", "ai_copilot", "lab_notes"

    // ==========================================
    // 1. DEEP LEARNING: NEURAL NETWORK BUILDER
    // ==========================================
    var nnDatasetName by mutableStateOf("XOR Gate") // "XOR Gate", "AND Gate"
    var nnEpoch by mutableStateOf(0)
    var nnLoss by mutableStateOf(0.0)
    var nnLearningRate by mutableStateOf(0.2f)
    var isNnTraining by mutableStateOf(false)

    // Weights & Biases
    // Layer 1: 2 Inputs -> 3 Hidden Neurons
    var w1 by mutableStateOf(Array(2) { FloatArray(3) { Random.nextFloat() * 2f - 1f } })
    var b1 by mutableStateOf(FloatArray(3) { 0.0f })

    // Layer 2: 3 Hidden Neurons -> 1 Output Neuron
    var w2 by mutableStateOf(FloatArray(3) { Random.nextFloat() * 2f - 1f })
    var b2 by mutableStateOf(0.0f)

    // Activations for presentation
    var inputX1 by mutableStateOf(1.0f)
    var inputX2 by mutableStateOf(0.0f)
    var activationHidden by mutableStateOf(FloatArray(3) { 0.0f })
    var activationOutput by mutableStateOf(0.0f)

    private var nnTrainingJob: Job? = null

    // Target Patterns
    private val xorInputs = arrayOf(
        floatArrayOf(0f, 0f),
        floatArrayOf(0f, 1f),
        floatArrayOf(1f, 0f),
        floatArrayOf(1f, 1f)
    )
    private val xorTargets = floatArrayOf(0f, 1f, 1f, 0f)

    private val andInputs = arrayOf(
        floatArrayOf(0f, 0f),
        floatArrayOf(0f, 1f),
        floatArrayOf(1f, 0f),
        floatArrayOf(1f, 1f)
    )
    private val andTargets = floatArrayOf(0f, 0f, 0f, 1f)

    init {
        resetNeuralNetwork()
        runForwardPropagation()
    }

    fun resetNeuralNetwork() {
        nnTrainingJob?.cancel()
        isNnTraining = false
        nnEpoch = 0
        w1 = Array(2) { FloatArray(3) { Random.nextFloat() * 2f - 1f } }
        b1 = FloatArray(3) { 0.0f }
        w2 = FloatArray(3) { Random.nextFloat() * 2f - 1f }
        b2 = 0.0f
        calculateGlobalNnLoss()
        runForwardPropagation()
    }

    private fun sigmoid(x: Float): Float {
        return (1.0f / (1.0f + exp(-x.toDouble()))).toFloat()
    }

    private fun sigmoidDerivative(activation: Float): Float {
        return activation * (1.0f - activation)
    }

    // Run forward propagation for UI sliders input
    fun runForwardPropagation() {
        // Hidden Layer
        val h = FloatArray(3)
        for (j in 0..2) {
            val z = inputX1 * w1[0][j] + inputX2 * w1[1][j] + b1[j]
            h[j] = sigmoid(z)
        }
        activationHidden = h

        // Output Layer
        val zOut = h[0] * w2[0] + h[1] * w2[1] + h[2] * w2[2] + b2
        activationOutput = sigmoid(zOut)
    }

    // Helper to calculate total Mean Squared Error on the entire training set
    private fun calculateGlobalNnLoss() {
        val inputs = if (nnDatasetName == "XOR Gate") xorInputs else andInputs
        val targets = if (nnDatasetName == "XOR Gate") xorTargets else andTargets
        var sumSquares = 0.0f

        for (k in inputs.indices) {
            val x = inputs[k]
            // Forward pass
            val h = FloatArray(3)
            for (j in 0..2) {
                h[j] = sigmoid(x[0] * w1[0][j] + x[1] * w1[1][j] + b1[j])
            }
            val zOut = h[0] * w2[0] + h[1] * w2[1] + h[2] * w2[2] + b2
            val out = sigmoid(zOut)
            sumSquares += (out - targets[k]).pow(2)
        }
        nnLoss = (sumSquares / inputs.size).toDouble()
    }

    fun executeBackpropagationStep() {
        val inputs = if (nnDatasetName == "XOR Gate") xorInputs else andInputs
        val targets = if (nnDatasetName == "XOR Gate") xorTargets else andTargets

        // Accumulate grid of updates across the training items for batch/stochastic learning
        for (patternIdx in inputs.indices) {
            val x = inputs[patternIdx]
            val target = targets[patternIdx]

            // 1. Forward Pass
            val h = FloatArray(3)
            for (j in 0..2) {
                h[j] = sigmoid(x[0] * w1[0][j] + x[1] * w1[1][j] + b1[j])
            }
            val zOut = h[0] * w2[0] + h[1] * w2[1] + h[2] * w2[2] + b2
            val out = sigmoid(zOut)

            // 2. Output Error gradient
            val error = out - target
            val deltaOut = error * sigmoidDerivative(out)

            // 3. Hidden Error gradient
            val deltaHidden = FloatArray(3)
            for (j in 0..2) {
                deltaHidden[j] = deltaOut * w2[j] * sigmoidDerivative(h[j])
            }

            // 4. Update Weights & Biases gradient descent step
            val lr = nnLearningRate
            for (j in 0..2) {
                w2[j] -= lr * deltaOut * h[j]
            }
            b2 -= lr * deltaOut

            for (i in 0..1) {
                for (j in 0..2) {
                    w1[i][j] -= lr * deltaHidden[j] * x[i]
                }
            }
            for (j in 0..2) {
                b1[j] -= lr * deltaHidden[j]
            }
        }

        nnEpoch++
        calculateGlobalNnLoss()
        runForwardPropagation()
    }

    fun toggleNnTraining() {
        if (isNnTraining) {
            nnTrainingJob?.cancel()
            isNnTraining = false
        } else {
            isNnTraining = true
            nnTrainingJob = viewModelScope.launch {
                while (isNnTraining) {
                    executeBackpropagationStep()
                    delay(30) // Faster live animations
                }
            }
        }
    }

    fun saveNnTrainingRun() {
        viewModelScope.launch {
            val session = ModelSession(
                modelName = "Feed-Forward Neural Network",
                datasetName = nnDatasetName,
                finalMetricName = "Mean Squared Error (MSE)",
                finalMetricValue = nnLoss,
                epochCount = nnEpoch
            )
            repository.insertSession(session)
            // Add a matching notebook entry automatically
            val note = SavedNote(
                title = "Lab Run: MLP on ${nnDatasetName}",
                content = "Trained a 2-3-1 Feed-Forward Artificial Neural Network on the $nnDatasetName dataset for $nnEpoch epochs. Final optimization MSE loss reached ${String.format("%.5f", nnLoss)}. Learning Rate parameter was $nnLearningRate.",
                type = "DL"
            )
            repository.insertNote(note)
        }
    }


    // ==========================================
    // 2. MACHINE LEARNING: K-MEANS & REGRESSION
    // ==========================================
    var mlPlaygroundSelected by mutableStateOf("kmeans") // "kmeans" or "regression"

    // --- K_MEANS STATE ---
    var kMeansPoints by mutableStateOf(emptyList<ClusterPoint>())
    var kMeansCentroids by mutableStateOf(emptyList<Centroid>())
    var kMeansStepDescription by mutableStateOf("Tap 'Generate Data' to start cluster playground")
    var kMeansStepCount by mutableStateOf(0)

    init {
        generateKMeansDatasets()
    }

    fun generateKMeansDatasets() {
        kMeansStepCount = 0
        kMeansStepDescription = "New data generated. Initialized 3 random clusters."

        // Generate 3 blobs of points
        val points = mutableListOf<ClusterPoint>()
        // Blob 1 around (25, 25)
        repeat(8) {
            points.add(ClusterPoint(20f + Random.nextFloat() * 15f, 20f + Random.nextFloat() * 15f))
        }
        // Blob 2 around (70, 30)
        repeat(8) {
            points.add(ClusterPoint(65f + Random.nextFloat() * 15f, 25f + Random.nextFloat() * 15f))
        }
        // Blob 3 around (50, 75)
        repeat(8) {
            points.add(ClusterPoint(40f + Random.nextFloat() * 20f, 65f + Random.nextFloat() * 20f))
        }
        kMeansPoints = points

        // Setup Centroids
        kMeansCentroids = listOf(
            Centroid(20f, 80f, "#00B6B7"), // Electric Teal
            Centroid(80f, 80f, "#A020F0"), // Neon Purple
            Centroid(50f, 15f, "#FF1493")  // Intense Pink
        )
    }

    fun stepKMeansAssignment() {
        val points = kMeansPoints.map { point ->
            var minDistance = Float.MAX_VALUE
            var bestIdx = -1
            kMeansCentroids.forEachIndexed { idx, centroid ->
                val distance = sqrt((point.x - centroid.x).pow(2) + (point.y - centroid.y).pow(2))
                if (distance < minDistance) {
                    minDistance = distance
                    bestIdx = idx
                }
            }
            point.copy(clusterIndex = bestIdx)
        }
        kMeansPoints = points
        kMeansStepDescription = "Points assigned to the closest centroid color."
        kMeansStepCount++
    }

    fun stepKMeansCentroidsUpdate() {
        val centroids = kMeansCentroids.mapIndexed { idx, centroid ->
            val assignedPoints = kMeansPoints.filter { it.clusterIndex == idx }
            if (assignedPoints.isNotEmpty()) {
                val sumX = assignedPoints.sumOf { it.x.toDouble() }.toFloat()
                val sumY = assignedPoints.sumOf { it.y.toDouble() }.toFloat()
                centroid.copy(
                    x = sumX / assignedPoints.size,
                    y = sumY / assignedPoints.size
                )
            } else {
                centroid
            }
        }
        kMeansCentroids = centroids
        kMeansStepDescription = "Centroids updated to middle of cluster."
        kMeansStepCount++
    }

    fun saveKMeansSession() {
        viewModelScope.launch {
            val countAssigned = kMeansPoints.filter { it.clusterIndex != -1 }.size
            val session = ModelSession(
                modelName = "K-Means Clustering",
                datasetName = "Synthetic Blobs (N=${kMeansPoints.size})",
                finalMetricName = "Iterations",
                finalMetricValue = kMeansStepCount.toDouble(),
                epochCount = kMeansStepCount
            )
            repository.insertSession(session)
            val note = SavedNote(
                title = "Lab Run: K-Means Clustering",
                content = "Fitted K-Means Clustering for K=3 on ${kMeansPoints.size} synthetic 2D coordinates. Executed $kMeansStepCount centroid recalibration iterations. Assigned $countAssigned data points successfully.",
                type = "ML"
            )
            repository.insertNote(note)
        }
    }


    // --- LINEAR REGRESSION STATE ---
    var regressionPoints by mutableStateOf(listOf(
        Point2D(10f, 25f),
        Point2D(30f, 40f),
        Point2D(45f, 55f),
        Point2D(70f, 72f),
        Point2D(90f, 95f)
    ))

    var regW by mutableStateOf(0.1f) // Slope
    var regB by mutableStateOf(10.0f) // Intercept
    var regLearningRate by mutableStateOf(0.0003f)
    var regLoss by mutableStateOf(0.0)
    var regStepCount by mutableStateOf(0)

    init {
        calculateRegressionLoss()
    }

    fun resetRegression() {
        regW = 0.1f
        regB = 10.0f
        regStepCount = 0
        calculateRegressionLoss()
    }

    fun calculateRegressionLoss() {
        if (regressionPoints.isEmpty()) return
        var sumSqrError = 0.0f
        for (p in regressionPoints) {
            val pred = regW * p.x + regB
            sumSqrError += (pred - p.y).pow(2)
        }
        regLoss = (sumSqrError / regressionPoints.size).toDouble()
    }

    fun executeRegressionGradientStep() {
        if (regressionPoints.isEmpty()) return
        val n = regressionPoints.size
        var dw = 0f
        var db = 0f

        for (p in regressionPoints) {
            val pred = regW * p.x + regB // y_pred = wx + b
            val err = pred - p.y
            dw += err * p.x
            db += err
        }
        // Multiply gradients
        dw = (2f / n) * dw
        db = (2f / n) * db

        // Update parameters
        regW -= regLearningRate * dw
        regB -= regLearningRate * db
        regStepCount++

        calculateRegressionLoss()
    }

    fun saveRegressionSession() {
        viewModelScope.launch {
            val session = ModelSession(
                modelName = "Linear Regression Gradient Descent",
                datasetName = "Salary vs Experience (Interactive)",
                finalMetricName = "MSE Loss",
                finalMetricValue = regLoss,
                epochCount = regStepCount
            )
            repository.insertSession(session)
            val note = SavedNote(
                title = "Lab Run: Reg Gradient Descent",
                content = "Optimized Simple Linear Regression Line with manual gradient descent. Epoch count: $regStepCount. Final loss MSE reached ${String.format("%.3f", regLoss)}. Equation: y = ${String.format("%.3f", regW)}x + ${String.format("%.3f", regB)}.",
                type = "ML"
            )
            repository.insertNote(note)
        }
    }


    // ==========================================
    // 3. AI SERVICES: GEMINI DATA SCIENCE COMPANION
    // ==========================================
    var aiQuery by mutableStateOf("")
    var aiResponse by mutableStateOf("Start by choosing a prompt template below or typing a custom question about Data Science, Machine Learning models, activation functions, or architectures!")
    var isAiLoading by mutableStateOf(false)

    fun askAiAssistant(customQuery: String? = null) {
        val finalQuery = customQuery ?: aiQuery
        if (finalQuery.isBlank()) return
        isAiLoading = true
        viewModelScope.launch {
            val systemMsg = "You are an elite research Data Scientist and Machine Learning Professor. Explain concepts clearly. When writing code, write fully functional Python with comments. Adhere strictly to proper data-science methodologies. Tone should be professional, insightful, and accessible."
            val response = repository.consultAI(finalQuery, systemInstruction = systemMsg)
            aiResponse = response
            isAiLoading = false
            if (customQuery == null) {
                aiQuery = "" // Clear entry if custom
            }
        }
    }

    fun saveAiResponseToNotebook() {
        if (aiResponse.isBlank() || isAiLoading) return
        viewModelScope.launch {
            val note = SavedNote(
                title = "AI Insight: Data Science Concept",
                content = aiResponse,
                type = "AI"
            )
            repository.insertNote(note)
        }
    }


    // ==========================================
    // 4. ROOM persistence notebook: SAVED NOTES
    // ==========================================
    var newNoteTitle by mutableStateOf("")
    var newNoteContent by mutableStateOf("")

    fun saveCustomNote() {
        if (newNoteTitle.isBlank() || newNoteContent.isBlank()) return
        viewModelScope.launch {
            val note = SavedNote(
                title = newNoteTitle,
                content = newNoteContent,
                type = "NOTE"
            )
            repository.insertNote(note)
            newNoteTitle = ""
            newNoteContent = ""
        }
    }

    fun deleteNote(note: SavedNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun deleteSession(session: ModelSession) {
        viewModelScope.launch {
            // Delete support logic
            // Since we don't have delete single of sessions, we can implement it or clear all
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearSessions()
        }
    }

    // ==========================================
    // 5. CHART LAB: SYNTHETIC DATA & VISUALIZATION
    // ==========================================
    var visualDatasetType by mutableStateOf("Gaussian") // "Gaussian", "Uniform", "Correlation", "Clusters"
    var visualPointCount by mutableStateOf(80f) // range 20..200
    var visualParamNoise by mutableStateOf(10f) // range 1..30
    var visualPoints by mutableStateOf(emptyList<Point2D>())
    var histogramBinsCount by mutableStateOf(10f) // range 5..25

    fun generateVisualSyntheticData() {
        val points = mutableListOf<Point2D>()
        val count = visualPointCount.toInt().coerceIn(10, 300)
        val noise = visualParamNoise / 10f // scaled noise multiplier (0.1 to 3.0)

        when (visualDatasetType) {
            "Gaussian" -> {
                for (i in 0 until count) {
                    val u1 = Random.nextFloat().toDouble().coerceAtLeast(1e-9)
                    val u2 = Random.nextFloat().toDouble()
                    val z0 = sqrt(-2.0 * kotlin.math.log(u1, exp(1.0))) * kotlin.math.cos(2.0 * Math.PI * u2)
                    
                    val xVal = (50f + z0.toFloat() * 12f * noise).coerceIn(5f, 95f)
                    
                    val u3 = Random.nextFloat().toDouble().coerceAtLeast(1e-9)
                    val u4 = Random.nextFloat().toDouble()
                    val z1 = sqrt(-2.0 * kotlin.math.log(u3, exp(1.0))) * kotlin.math.cos(2.0 * Math.PI * u4)
                    val yVal = (50f + z1.toFloat() * 12f * noise).coerceIn(5f, 95f)
                    points.add(Point2D(xVal, yVal))
                }
            }
            "Uniform" -> {
                for (i in 0 until count) {
                    val xVal = 5f + Random.nextFloat() * 90f
                    val yVal = 5f + Random.nextFloat() * 90f
                    points.add(Point2D(xVal, yVal))
                }
            }
            "Correlation" -> {
                for (i in 0 until count) {
                    val xVal = 10f + Random.nextFloat() * 80f
                    val baseLineY = xVal * 0.8f + 5f
                    val u1 = Random.nextFloat().toDouble().coerceAtLeast(1e-9)
                    val u2 = Random.nextFloat().toDouble()
                    val g = sqrt(-2.0 * kotlin.math.log(u1, exp(1.0))) * kotlin.math.cos(2.0 * Math.PI * u2)
                    val yVal = (baseLineY + g.toFloat() * 8f * noise).coerceIn(5f, 95f)
                    points.add(Point2D(xVal, yVal))
                }
            }
            "Clusters" -> {
                val c1X = 30f
                val c1Y = 30f
                val c2X = 70f
                val c2Y = 70f

                for (i in 0 until count) {
                    val center = if (Random.nextBoolean()) 1 else 2
                    val centerX = if (center == 1) c1X else c2X
                    val centerY = if (center == 1) c1Y else c2Y
                    
                    val u1 = Random.nextFloat().toDouble().coerceAtLeast(1e-9)
                    val u2 = Random.nextFloat().toDouble()
                    val z0 = sqrt(-2.0 * kotlin.math.log(u1, exp(1.0))) * kotlin.math.cos(2.0 * Math.PI * u2)
                    
                    val xVal = (centerX + z0.toFloat() * 6f * noise).coerceIn(5f, 95f)
                    
                    val u3 = Random.nextFloat().toDouble().coerceAtLeast(1e-9)
                    val u4 = Random.nextFloat().toDouble()
                    val z1 = sqrt(-2.0 * kotlin.math.log(u3, exp(1.0))) * kotlin.math.cos(2.0 * Math.PI * u4)
                    val yVal = (centerY + z1.toFloat() * 6f * noise).coerceIn(5f, 95f)
                    points.add(Point2D(xVal, yVal))
                }
            }
        }
        visualPoints = points
    }

    fun saveDatasetSessionToLabJournal() {
        viewModelScope.launch {
            val session = ModelSession(
                modelName = "Synthetic Dataset Visualizer",
                datasetName = "$visualDatasetType Distribution (N=${visualPoints.size})",
                finalMetricName = "Bins/Purity",
                finalMetricValue = histogramBinsCount.toDouble(),
                epochCount = visualPoints.size
            )
            repository.insertSession(session)
            
            val xMean = if (visualPoints.isEmpty()) 0.0 else visualPoints.map { it.x }.average()
            val yMean = if (visualPoints.isEmpty()) 0.0 else visualPoints.map { it.y }.average()
            
            val note = SavedNote(
                title = "Lab Chart: $visualDatasetType",
                content = "Generated and visualized synthetic data of type $visualDatasetType with N=${visualPoints.size} coordinates. Statistical properties: Mean X = ${String.format("%.2f", xMean)}, Mean Y = ${String.format("%.2f", yMean)}. Visualized on custom high-performance Native Jetpack Compose Chart canvas.",
                type = "ML"
            )
            repository.insertNote(note)
        }
    }

    // ==========================================
    // 6. MACHINE LEARNING: DECISION TREE
    // ==========================================
    var treePoints by mutableStateOf<List<LabeledPoint>>(emptyList())
    var treeMaxDepth by mutableStateOf(3f) // 1..5
    var treeMinSamplesSplit by mutableStateOf(2f) // 2..10
    var treeCriterion by mutableStateOf("Gini Impurity") // "Gini Impurity" or "Entropy"
    var treeModelRoot by mutableStateOf<DecisionNode?>(null)
    var isTreeTraining by mutableStateOf(false)
    var treeStepCount by mutableStateOf(0)
    var treeAccuracy by mutableStateOf(0.0)
    var treeTrainingLog by mutableStateOf("Config parameters and press 'Train Model'")

    fun generateDecisionTreePoints() {
        treeStepCount = 0
        treeModelRoot = null
        treeAccuracy = 0.0
        isTreeTraining = false
        treeTrainingLog = "Generated 30 synthetic data points belonging to two distinct classes."
        
        val points = mutableListOf<LabeledPoint>()
        // Simple synthetic binary classification data
        val random = Random(42) // Seed for stability
        repeat(15) {
            val rx = 10f + random.nextFloat() * 45f
            val ry = 10f + random.nextFloat() * 80f
            val trueLabel = if (rx + ry > 65f + random.nextFloat() * 10f) 1 else 0
            points.add(LabeledPoint(rx, ry, trueLabel))
        }
        repeat(15) {
            val rx = 45f + random.nextFloat() * 45f
            val ry = 10f + random.nextFloat() * 80f
            val trueLabel = if (rx + ry > 80f + random.nextFloat() * 10f) 1 else 0
            points.add(LabeledPoint(rx, ry, trueLabel))
        }
        treePoints = points
    }

    private fun calculateImpurity(subset: List<LabeledPoint>, criterion: String): Float {
        if (subset.isEmpty()) return 0f
        val total = subset.size.toFloat()
        val c0 = subset.count { it.label == 0 } / total
        val c1 = subset.count { it.label == 1 } / total

        return if (criterion == "Gini Impurity") {
            1f - c0 * c0 - c1 * c1
        } else {
            val log0 = if (c0 > 0f) c0 * kotlin.math.log2(c0) else 0f
            val log1 = if (c1 > 0f) c1 * kotlin.math.log2(c1) else 0f
            -(log0 + log1)
        }
    }

    private fun findBestSplit(subset: List<LabeledPoint>, criterion: String): SplitResult? {
        if (subset.size < treeMinSamplesSplit.toInt()) return null
        val parentImpurity = calculateImpurity(subset, criterion)
        if (parentImpurity == 0f) return null

        var bestGain = -1f
        var bestFeature = -1 // 0 for X, 1 for Y
        var bestThreshold = 0f
        var bestLeft = emptyList<LabeledPoint>()
        var bestRight = emptyList<LabeledPoint>()

        for (feat in 0..1) {
            val values = subset.map { if (feat == 0) it.x else it.y }.distinct().sorted()
            if (values.size < 2) continue
            // Midpoints check
            for (i in 0 until values.size - 1) {
                val threshold = (values[i] + values[i+1]) / 2f
                val (left, right) = if (feat == 0) {
                    subset.partition { it.x < threshold }
                } else {
                    subset.partition { it.y < threshold }
                }

                if (left.isEmpty() || right.isEmpty()) continue

                val leftImpurity = calculateImpurity(left, criterion)
                val rightImpurity = calculateImpurity(right, criterion)
                val weightedImpurity = (left.size.toFloat() / subset.size) * leftImpurity +
                                       (right.size.toFloat() / subset.size) * rightImpurity
                
                val gain = parentImpurity - weightedImpurity
                if (gain > bestGain) {
                    bestGain = gain
                    bestFeature = feat
                    bestThreshold = threshold
                    bestLeft = left
                    bestRight = right
                }
            }
        }

        if (bestGain <= 0f) return null
        return SplitResult(bestFeature, bestThreshold, bestLeft, bestRight, bestGain)
    }

    private class SplitResult(
        val feature: Int,
        val threshold: Float,
        val left: List<LabeledPoint>,
        val right: List<LabeledPoint>,
        val gain: Float
    )

    private fun buildTreeRec(subset: List<LabeledPoint>, depth: Int, maxDepth: Int, criterion: String): DecisionNode {
        val total = subset.size
        val impurity = calculateImpurity(subset, criterion)
        
        val c0Count = subset.count { it.label == 0 }
        val c1Count = subset.count { it.label == 1 }
        val majorityLabel = if (c0Count >= c1Count) 0 else 1

        val node = DecisionNode(
            totalSamples = total,
            impurity = impurity,
            label = majorityLabel
        )

        if (depth >= maxDepth) {
            return node
        }

        val split = findBestSplit(subset, criterion)
        if (split == null) {
            return node
        }

        node.feature = split.feature
        node.threshold = split.threshold
        node.left = buildTreeRec(split.left, depth + 1, maxDepth, criterion)
        node.right = buildTreeRec(split.right, depth + 1, maxDepth, criterion)
        return node
    }

    fun trainDecisionTree() {
        if (treePoints.isEmpty()) {
            generateDecisionTreePoints()
        }
        isTreeTraining = true
        treeStepCount = 0
        treeTrainingLog = "Initializing decision tree solver..."
        
        viewModelScope.launch {
            delay(400)
            treeTrainingLog = "Analyzing dataset coordinates across splits..."
            delay(400)
            treeTrainingLog = "Applying split partitions on X and Y features..."
            delay(500)
            
            val root = buildTreeRec(treePoints, 0, treeMaxDepth.toInt().coerceIn(1, 5), treeCriterion)
            treeModelRoot = root
            
            var correct = 0
            treePoints.forEach { pt ->
                val pred = predictTree(root, pt)
                if (pred == pt.label) correct++
            }
            treeAccuracy = (correct.toDouble() / treePoints.size) * 100.0
            treeStepCount = countNodes(root)
            treeTrainingLog = "Decision Tree fitted. Created $treeStepCount nodes. Final Training Accuracy: ${String.format("%.1f", treeAccuracy)}%"
            isTreeTraining = false
        }
    }

    private fun predictTree(node: DecisionNode, pt: LabeledPoint): Int {
        if (node.feature == -1) return node.label
        val featureVal = if (node.feature == 0) pt.x else pt.y
        val nextNode = if (featureVal < node.threshold) node.left else node.right
        return if (nextNode != null) predictTree(nextNode, pt) else node.label
    }

    private fun countNodes(node: DecisionNode?): Int {
        if (node == null) return 0
        return 1 + countNodes(node.left) + countNodes(node.right)
    }

    fun saveDecisionTreeSession() {
        viewModelScope.launch {
            val session = ModelSession(
                modelName = "Decision Tree Classifier",
                datasetName = "Classification Matrix (N=${treePoints.size})",
                finalMetricName = "Accuracy",
                finalMetricValue = treeAccuracy,
                epochCount = treeMaxDepth.toInt()
            )
            repository.insertSession(session)
            val note = SavedNote(
                title = "Lab Run: Decision Tree",
                content = "Fitted static boundary Decision Tree Classifier with $treeCriterion criterion, Max Depth=${treeMaxDepth.toInt()}, Min Split=${treeMinSamplesSplit.toInt()}. Created a model of $treeStepCount total nodes resulting in ${String.format("%.1f", treeAccuracy)}% binary fit accuracy.",
                type = "ML"
            )
            repository.insertNote(note)
        }
    }

    init {
        generateVisualSyntheticData()
        generateDecisionTreePoints()
    }

    override fun onCleared() {
        super.onCleared()
        nnTrainingJob?.cancel()
    }
}

data class LabeledPoint(val x: Float, val y: Float, val label: Int)

class DecisionNode(
    var feature: Int = -1, // 0 for X, 1 for Y, -1 for leaf
    var threshold: Float = 0f,
    var label: Int = -1, // predicted class if leaf
    var left: DecisionNode? = null,
    var right: DecisionNode? = null,
    val totalSamples: Int = 0,
    val impurity: Float = 0f
)
