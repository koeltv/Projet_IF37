package eyeTracking

import Observable
import nu.pattern.OpenCV
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.objdetect.Objdetect
import org.opencv.video.Video
import org.opencv.videoio.VideoCapture
import toImage
import java.awt.Image
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.math.roundToInt

/**
 * Handle eye tracking via a webcam (WIP).
 */
class EyeTracking : Observable {
    override val changeSupport: PropertyChangeSupport

    private val pcs = PropertyChangeSupport(this)
    private val cascadeClassifier = CascadeClassifier("src/main/resources/haarcascade_eye.xml")

    private val capture = VideoCapture(0)

    private var bufferImage: Mat? = null
    private var pointsBuffer: MatOfPoint2f? = MatOfPoint2f()

    private var oldFocusPoint: Point? = null

    private val screenScaleConverter: ScreenScaleConverter

    init {
        OpenCV.loadLocally()
        screenScaleConverter = ScreenScaleConverter(this)
        changeSupport = screenScaleConverter.changeSupport
    }

    override fun addListener(changeListener: PropertyChangeListener) {
        screenScaleConverter.addListener(changeListener)
    }

    /**
     * Capture an image from the webcam and return it with an overlay on the eyes.
     */
    fun getCaptureWithFaceDetection(): Image {
        val mat = Mat()
        capture.read(mat)
        return detectEyesAndUpdate(mat).toImage()
    }

    /**
     * Find the difference between the image in the buffer and a new image
     * @return the captured image with an overlay over the eyes
     */
    fun trackEyeMovement(): Image {
        val mat = Mat()
        capture.read(mat)

        if (bufferImage == null) {
            bufferImage = mat
            pointsBuffer = MatOfPoint2f(*(detectEyes(mat).getCenters()))
        }

        val newPoints = MatOfPoint2f()
        Video.calcOpticalFlowPyrLK(
            bufferImage,
            mat,
            pointsBuffer,
            newPoints,
            MatOfByte(),
            MatOfFloat(),
        )
        pcs.firePropertyChange("Moved", pointsBuffer, newPoints)
        pointsBuffer = newPoints
        //Possibility of comparing 2 outputs of detectEyes()

        detectEyes(mat).toArray().forEach { eye ->
            Imgproc.rectangle(mat, eye.tl(), eye.br(), Scalar(0.0, 0.0, 255.0), 3)
        }

        return mat.toImage()
    }

    /**
     * Find the position of the eyes on the given image
     * @return the position of the eyes as rectangles
     */
    private fun detectEyes(loadedImage: Mat): MatOfRect {
        val eyesDetected = MatOfRect()
        val minFaceSize = (loadedImage.rows() * 0.1f).roundToInt().toDouble()

        cascadeClassifier.detectMultiScale(
            loadedImage,
            eyesDetected,
            1.1,
            3,
            Objdetect.CASCADE_SCALE_IMAGE,
            Size(minFaceSize, minFaceSize),
            Size()
        )
        return eyesDetected
    }

    /**
     * Find the position of the eyes on the given images and display it as an overlay on the image
     * @return the captured image with an overlay over the eyes
     */
    private fun detectEyesAndUpdate(image: Mat): Mat {
        val eyesDetected = detectEyes(image)

        eyesDetected.toArray().forEach { eye ->
            Imgproc.rectangle(image, eye.tl(), eye.br(), Scalar(0.0, 0.0, 255.0), 3)
        }

        return image
    }

    /**
     * Find the eyes and return their center position
     * @return center of both eyes
     */
    fun getEyesPosition(): Array<Point> {
        val mat = Mat()
        capture.read(mat)

        val eyes = detectEyes(mat)
        return eyes.getCenters()
    }

    private fun MatOfRect.getCenters(): Array<Point> {
        return toArray().getCenters()
    }

    /**
     * Find the centers of an array of rectangles.
     */
    private fun Array<Rect>.getCenters(): Array<Point> {
        return map { rect ->
            Point((rect.x + rect.width/2).toDouble(), (rect.y + rect.height/2).toDouble())
        }.take(2).toTypedArray()
//    return MatOfPoint2f(*temp)
    }
}