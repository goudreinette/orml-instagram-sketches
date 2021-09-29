import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import org.openrndr.ffmpeg.VideoWriter
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

        val target2 = renderTarget(width, height) {
            colorBuffer()
        }

        val videoPlayer = VideoPlayerFFMPEG.fromFile("data/videos/webcam.mp4")
        videoPlayer.play()
        videoPlayer.ended.listen {
            videoPlayer.restart()
        }

        val videoWriter = VideoWriter()
        videoWriter.size(width, height)
        videoWriter.output("video/${program.assetMetadata().assetBaseName}.mp4")
        videoWriter.start()

        ended.listen {
            videoWriter.stop()
        }

        extend {

            drawer.clear(ColorRGBa.BLACK)

            drawer.withTarget(target) {
                videoPlayer.draw(drawer)
            }

            val transformed = transformer.transformStyle(target.colorBuffer(0), styleVector)

            drawer.imageFit(transformed, 0.0,0.0, width.toDouble(), height.toDouble(), 0.0,0.0, FitMethod.Contain)

            drawer.withTarget(target2) {
                imageFit(transformed, 0.0,0.0, width.toDouble(), height.toDouble(), 0.0,0.0, FitMethod.Contain)
            }

            videoWriter.frame(target2.colorBuffer(0))
        }


    }
}