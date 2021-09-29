import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import org.openrndr.ffmpeg.loadVideoDevice
import org.openrndr.orml.styletransfer.StyleEncoder
import org.openrndr.orml.styletransfer.StyleTransformer


fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        val encoder = StyleEncoder.load()
        val transformer = StyleTransformer.load()

        val styleImage = loadImage("data/images/pinkpanther.jpg")
        val styleVector = encoder.encodeStyle(styleImage)


        val target = renderTarget(1000, 1000) {
            colorBuffer()
        }

        val videoPlayer = loadVideoDevice()
        videoPlayer.play()

//        extend(ScreenRecorder())


        extend {
            drawer.clear(ColorRGBa.BLACK)

            drawer.withTarget(target) {
                videoPlayer.draw(drawer)
            }

            val transformed = transformer.transformStyle(target.colorBuffer(0), styleVector)

            drawer.imageFit(transformed, 0.0,0.0, width.toDouble(), height.toDouble(), 0.0,0.0, FitMethod.Contain)
        }
    }
}