package `super-resolution`

import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle

fun main() = application {
    configure {
        width = 1280
        height = 1280
    }

    oliveProgram {
        val upscaler = ImageUpscaler.load()
        val image = loadImage("data/images/image-003.jpg")
        var upscale = upscaler.upscale(image, octaves = 1)

        val rt = renderTarget(width, height) {
            colorBuffer()
        }

        drawer.withTarget(rt) {
            drawer.imageFit(upscale, 0.0,0.0, width.toDouble(), height.toDouble(), 0.0,0.0)
        }

        mouse.buttonDown.listen {

        }

        extend {
            val x = mouse.position.x

            val source = Rectangle(x,0.0, width.toDouble() - x, height*1.0)
            val target = Rectangle(0 + x, 0.0, width.toDouble() - x, height*1.0)

            drawer.imageFit(image, 0.0,0.0, width.toDouble(), height.toDouble(), 0.0,0.0)
            drawer.image(rt.colorBuffer(0), source, target)

            drawer.defaults()
            drawer.lineSegment(x, 0.0, x, height * 1.0)
        }
    }
}