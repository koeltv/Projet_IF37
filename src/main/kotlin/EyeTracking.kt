import nu.pattern.OpenCV
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.opencv.objdetect.Objdetect
import org.opencv.video.Video
import org.opencv.videoio.VideoCapture
import java.awt.Image
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Handle eye tracking via a webcam (WIP).
 */
class EyeTracking {
    private val pcs = PropertyChangeSupport(this)
    private val cascadeClassifier = CascadeClassifier("src/main/resources/haarcascade_eye.xml")

    private val capture = VideoCapture(0)

    private var bufferImage: Mat? = null
    private var pointsBuffer: MatOfPoint2f? = MatOfPoint2f()

    private var oldFocusPoint: Point? = null

    init {
        OpenCV.loadLocally()
    }

    fun addListener(changeListener: PropertyChangeListener) {
        pcs.addPropertyChangeListener(changeListener)
    }

    fun getCaptureWithFaceDetection(): Image {
        val mat = Mat()
        capture.read(mat)
        return detectEyesAndUpdate(mat).toImage()
    }

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
        pointsBuffer = newPoints //TODO Test for difference
        //Possibility of comparing 2 outputs of detectEyes()

        detectEyes(mat).toArray().forEach { eye ->
            Imgproc.rectangle(mat, eye.tl(), eye.br(), Scalar(0.0, 0.0, 255.0), 3)
        }

        return mat.toImage()
    }

    fun trackEyes(): Image {
        val mat = Mat()
        capture.read(mat)

        val eyes = detectEyes(mat).toArray()
        if (eyes.isEmpty()) return mat.toImage()

        val focusPoint = eyes.getCenters().average()

        pcs.firePropertyChange("Moved", oldFocusPoint, focusPoint)

        eyes.forEach { eye ->
            Imgproc.rectangle(mat, eye.tl(), eye.br(), Scalar(0.0, 0.0, 255.0), 3)
        }

        oldFocusPoint = focusPoint
        return mat.toImage()
    }

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

    private fun detectEyesAndUpdate(image: Mat): Mat {
        val eyesDetected = MatOfRect()
        val minFaceSize = round(image.rows() * 0.1f).toDouble()

        cascadeClassifier.detectMultiScale(
            image,
            eyesDetected,
            1.1,
            3,
            Objdetect.CASCADE_SCALE_IMAGE,
            Size(minFaceSize, minFaceSize),
            Size()
        )

        eyesDetected.toArray().forEach { eye ->
            Imgproc.rectangle(image, eye.tl(), eye.br(), Scalar(0.0, 0.0, 255.0), 3)
        }
        return image
    }

    fun getEyesPosition(): Array<Point> {
        val mat = Mat()
        capture.read(mat)

        val eyes = detectEyes(mat)
        return eyes.getCenters()
    }

    private fun MatOfRect.getCenters(): Array<Point> {
        return toArray().getCenters()
    }

    private fun Array<Rect>.getCenters(): Array<Point> {
        return map { rect ->
            Point((rect.x + rect.width/2).toDouble(), (rect.y + rect.height/2).toDouble())
        }.take(2).toTypedArray()
//    return MatOfPoint2f(*temp)
    }
}