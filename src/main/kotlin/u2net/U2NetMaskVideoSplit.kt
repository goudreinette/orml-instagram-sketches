import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.draw.renderTarget
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.parameters.XYParameter
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.*
import org.openrndr.math.Vector2
import org.openrndr.orml.u2net.U2Net


fun main() = application {
    configure {
        width = 1920/2
        height = 1080/2
    }


    program {
        val u2 = U2Net.load()

        val conf = VideoPlayerConfiguration().apply {
            allowFrameSkipping = false
        }
        val videoPlayer = VideoPlayerFFMPEG.fromFile("data/videos/wide putin.mov", PlayMode.BOTH, conf).apply {
            play()
            ended.listen {
                restart()
            }
        }



        // --
        val target = renderTarget(width,height) {
            colorBuffer()
        }


        extend(ScreenRecorder())

        extend {
            drawer.clear(ColorRGBa.PINK)

            videoPlayer.draw(drawer, true)
            drawer.withTarget(target) {
                imageFit(videoPlayer.colorBuffer!!, 0.0,0.0, width.toDouble(),height.toDouble())
            }


            // Left
            drawer.imageFit(target.colorBuffer(0), 0.0,0.0, width/3.0, height.toDouble(), 0.0,0.0)


            // Center
            val mask = u2.matte(target.colorBuffer(0))
            drawer.imageFit(mask, width/3.0,0.0, width/3.0, height.toDouble(), 0.0,0.0)


            // Right
            val result = u2.removeBackground(target.colorBuffer(0))
            drawer.imageFit(result, width/3.0*2,0.0, width/3.0, height.toDouble(), 0.0,0.0)
        }
    }
}