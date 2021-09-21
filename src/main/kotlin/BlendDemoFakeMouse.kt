import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.orml.styletransfer.StyleEncoder
import org.openrndr.orml.styletransfer.StyleTransformer
import kotlin.math.sin


fun main() = application {
    program {
        val cursor = loadImage("data/images/cursor.png")
        val encoder = StyleEncoder.load()
        val transformer = StyleTransformer.load()

        val contentImage = loadImage("data/images/image-001.png")
        val styleImage0 = loadImage("data/images/style-003.jpg")
        val styleImage1 = loadImage("data/images/style-001.jpg")
        val styleVector0 = encoder.encodeStyle(styleImage0)
        val styleVector1 = encoder.encodeStyle(styleImage1)

        extend(ScreenRecorder())

        extend {
            val x = simplex(0,  .95 * seconds * .8 * width + width/2)
            val y = simplex(1000, .1 *  seconds * .005 * height + height/2)

            val f = (x/width).toFloat()

            val styleVector = (styleVector0 zip styleVector1).map {
                it.first * f + it.second * (1.0f-f)
            }.toFloatArray()

            val transformed = transformer.transformStyle(contentImage, styleVector)


            drawer.image(transformed)


            drawer.imageFit(cursor, x, y, 20.0, 20.0)
        }
    }
}
