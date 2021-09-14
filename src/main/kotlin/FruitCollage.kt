import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import org.openrndr.extensions.Screenshots
import org.openrndr.orml.u2net.U2Net
import org.openrndr.shape.Rectangle
import org.w3c.dom.css.Rect
import java.io.File

data class Fruit(val image: ColorBuffer, val rect: Rectangle)



fun main() {
    application {
        val fruits = listOf<ColorBuffer>()
//
//        repeat1(2.0) {
//            println("hello there")
//        }

        program {
            val u2= U2Net.load()
            val image = loadImage("data/images/cheeta.jpg")
            val result = u2.removeBackground(image)

            extend(Screenshots())
            extend {
                drawer.clear(ColorRGBa.PINK)


                val r1 = Rectangle(0.0, 0.0, result.width.toDouble(), result.height*1.0)
                drawer.image(result, r1, r1)
            }
        }
    }
}