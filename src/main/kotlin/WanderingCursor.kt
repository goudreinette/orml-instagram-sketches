import org.openrndr.application
import org.openrndr.extra.noise.simplex


fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {

        extend {
            val x = simplex(0,  seconds) * .5 * width + width/2
            val y = simplex(1000,  seconds) * .5 * height + height/2
            drawer.circle(x, y, 10.0)
        }
    }
}
