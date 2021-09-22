import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.ffmpeg.VideoPlayerFFMPEG


fun main() = application {
    program {
        val videoPlayer = VideoPlayerFFMPEG.fromDevice()

        print(VideoPlayerFFMPEG.defaultDevice())
        print(VideoPlayerFFMPEG.listDeviceNames())

        videoPlayer.play()
        videoPlayer.newFrame.listen {
            println("yo")
        }



        extend {
            drawer.clear(ColorRGBa.BLACK)
            videoPlayer.draw(drawer)
        }
    }
}