package facemesh


import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.rectangleFormat
import org.openrndr.ffmpeg.PlayMode
import org.openrndr.ffmpeg.VideoPlayerConfiguration
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import org.openrndr.ffmpeg.loadVideoDevice
import org.openrndr.orml.facemesh.BlazeFace
import org.openrndr.shape.IntRectangle
import org.openrndr.shape.Rectangle

fun main() {
    application {
        configure {
            width = 1920
            height = 1080
        }
        program {
            val video = VideoPlayerFFMPEG.fromFile(
                "data/videos/webcam.mp4",
                PlayMode.BOTH,
                VideoPlayerConfiguration().apply {
                    allowFrameSkipping = false
                }).apply {
                play()
                ended.listen {
                    restart()
                }
            }


            val bf = BlazeFace.load()
            extend {
                video.draw(drawer, blind = true)
                val rectangles = bf.detectFaces(video.colorBuffer!!)
                drawer.image(bf.inputImage)

                for (rectangle in rectangles) {
                    val s = 640.0
                    val r = Rectangle(rectangle.area.corner * s, rectangle.area.width * s, rectangle.area.height*s)
                    drawer.fill = null
                    drawer.stroke = ColorRGBa.PINK
                    drawer.rectangle(r)
                }
            }
        }
    }
}