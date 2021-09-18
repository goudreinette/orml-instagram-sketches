import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.imageFit.imageFit


fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        val cursor = loadImage("data/images/cursor.png")

        extend {
            val x = simplex(0,  .5 * seconds) * .5 * width + width/2
            val y = simplex(1000, .1*  seconds) * .005 * height + height/2
//            drawer.circle(x, y, 10.0)
            drawer.imageFit(cursor, x, y, 20.0, 20.0)
        }
    }
}
