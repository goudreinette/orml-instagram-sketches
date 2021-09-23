import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.map
import kotlin.math.sin

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.fill = ColorRGBa.PINK

            // Transforms
            drawer.translate(width/2.0, height/2.0)
            drawer.scale(sin(seconds).map(-1.0, 1.0, 0.5, 1.0))
            drawer.translate(-width/4.0, -height/4.0)

            drawer.rectangle(0.0, 0.0, width/2.0, height/2.0)
        }
    }
}
