import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.*
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


        val target = renderTarget(width, height) {
            colorBuffer()
        }

        val conf = VideoPlayerConfiguration().apply {
            allowFrameSkipping = false
        }

        val videoPlayer = VideoPlayerFFMPEG.fromFile("data/videos/webcam.mp4", PlayMode.BOTH, conf).apply {
            play()
            ended.listen {
                restart()
            }
        }


        extend(ScreenRecorder())


        extend {

            drawer.clear(ColorRGBa.BLACK)

            videoPlayer.draw(drawer, true)
            drawer.withTarget(target) {
                imageFit(videoPlayer.colorBuffer!!, 0.0,0.0, width.toDouble(),height.toDouble())
            }

            val transformed = transformer.transformStyle(target.colorBuffer(0), styleVector)

            drawer.imageFit(transformed, 0.0,0.0, width.toDouble(), height.toDouble(), -width/2.0,0.0, FitMethod.Contain)


        }


    }
}