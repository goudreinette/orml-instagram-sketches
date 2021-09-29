import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.renderTarget
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.VideoPlayerFFMPEG


fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        val videoPlayer = VideoPlayerFFMPEG.fromFile("data/videos/wide putin.mov").apply {
            play()
            ended.listen {
                restart()
            }
        }

        val target = renderTarget(400, 800) {
            colorBuffer()
        }


        extend {
            drawer.clear(ColorRGBa.PINK)

            videoPlayer.draw(drawer)

            // Left
            drawer.withTarget(target) {

            }

            drawer.imageFit(target.colorBuffer(0), 0.0,0.0, 400.0, 800.0, 0.0,0.0, FitMethod.Contain)


            // Right
        }
    }
}