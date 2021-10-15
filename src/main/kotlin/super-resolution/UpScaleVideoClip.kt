package `super-resolution`


import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.PlayMode
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.ffmpeg.VideoPlayerConfiguration
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import org.openrndr.shape.Rectangle

fun main() = application {
    configure {
        width = 1280
        height = 960
    }

    program {
        val upscaler = ImageUpscaler.load()

        val video = VideoPlayerFFMPEG.fromFile(
            "data/videos/rickroll_short.mp4",
            PlayMode.BOTH,
            VideoPlayerConfiguration().apply {
                allowFrameSkipping = false
            }).apply {
            play()
            ended.listen {
                restart()
            }
        }


        extend(ScreenRecorder())

        video.newFrame.listen {

        }

        extend {
            video.draw(drawer, blind = true)

            val upscale = upscaler.upscale(video.colorBuffer!!, octaves = 1)
            drawer.imageFit(upscale, 0.0,0.0, width.toDouble(), height.toDouble(), 0.0,0.0)
        }
    }
}