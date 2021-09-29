import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.draw.renderTarget
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.ColorParameter
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.XYParameter
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import org.openrndr.math.Vector2
import org.openrndr.orml.u2net.U2Net


fun main() = application {
    configure {
        width = 1200
        height = 800
    }


    program {
        val u2 = U2Net.load()

        val videoPlayer = VideoPlayerFFMPEG.fromFile("data/videos/wide putin.mov").apply {
            play()
            ended.listen {
                restart()
            }
        }


        val settings = object {
            @XYParameter("position", 0.0, 1200.0, 0.0, 800.0)
            var position: Vector2 = Vector2(0.0,800.0)
        }

        val gui = GUI()
        gui.add(settings)
//            extend(gui)

        extend(ScreenRecorder())

        extend {
            drawer.clear(ColorRGBa.PINK)

            // Left
            videoPlayer.draw(drawer, true)
            drawer.image(videoPlayer.colorBuffer!!)

            // Center
            val mask = u2.matte(videoPlayer.colorBuffer!!)
            drawer.isolated {
                translate(settings.position)
                scale(1.0, -1.0)
                image(mask, 400.0, 0.0)
            }

            // Right
            val result = u2.removeBackground(videoPlayer.colorBuffer!!)
            drawer.isolated {
                translate(settings.position)
                scale(1.0, -1.0)
                image(result, 800.0, 0.0)
            }
        }
    }
}