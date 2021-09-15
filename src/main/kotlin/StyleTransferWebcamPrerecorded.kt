import org.openrndr.application
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
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

        val target = renderTarget(1920, 1080) {
            colorBuffer()
        }

        val styleVector = encoder.encodeStyle(styleImage)

//        extend(ScreenRecorder())
        val videoPlayer = VideoPlayerFFMPEG.fromFile("data/videos/webcam.mp4")
        videoPlayer.play()

        videoPlayer.newFrame.listen {
            drawer.isolatedWithTarget(target){
                videoPlayer.draw(drawer)
            }

            val transformed  = transformer.transformStyle(target.colorBuffer(0), styleVector)
            drawer.imageFit(transformed, 0.0,0.0, width.toDouble(), height.toDouble())
        }
    }
}