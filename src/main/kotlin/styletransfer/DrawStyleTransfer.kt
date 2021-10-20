package styletransfer

import org.openrndr.MouseButton
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.drawThread
import org.openrndr.draw.launch
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.ColorParameter
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.internal.finish
import org.openrndr.math.Vector2
import org.openrndr.math.map
import org.openrndr.orml.styletransfer.StyleEncoder
import org.openrndr.orml.styletransfer.StyleTransformer
import org.openrndr.shape.Rectangle


fun main() = application {
    configure {
        width = 640
        height = 640
    }

    oliveProgram {
        val encoder = StyleEncoder.load()
        val transformer = StyleTransformer.load()
        var contentImage = loadImage("data/images/image-001.png")
        var result = contentImage

        var clickStart = Vector2(0.0,0.0)

        // Thread stuff
        val secondary = drawThread()


        /**
         * Style image canvas target
         */
        val canvasRect = Rectangle(200.0,0.0,128.0,128.0)
        val target = renderTarget(canvasRect.width.toInt(), canvasRect.height.toInt()) {
            colorBuffer()
        }

        drawer.withTarget(target) {
            drawer.clear(ColorRGBa.WHITE)
        }


        /**
         * GUI
         */
        val settings = object {
            @ColorParameter("Color")
            var color: ColorRGBa = ColorRGBa.PINK

            @DoubleParameter("Brush size", 1.0, 50.0)
            var brushSize: Double = 20.0

            @ActionParameter("Recalculate")
            fun recalculate() {
//                secondary.launch {
                    val encoded = encoder.encodeStyle(target.colorBuffer(0))
                    result = transformer.transformStyle(contentImage, encoded)
//                }
            }
        }

        mouse.buttonDown.listen {
            clickStart = mouse.position
        }

        mouse.buttonUp.listen {
            if (clickStart.isInside(canvasRect)) {
                settings.recalculate()
            }
        }

        window.drop.listen {
            println("${it.files.size} files dropped at ${it.position}")
            println(it.files[0])
            contentImage = loadImage(it.files[0])
            settings.recalculate()
        }


        val gui = GUI()
        gui.add(settings, "Drawing")
        extend(gui)





        val guiOffset = 200.0
        val radius = 10.0

        extend {
            val coord = map(canvasRect, drawer.bounds, mouse.position)

            drawer.withTarget(target) {
                if (mouse.pressedButtons.contains(MouseButton.LEFT)) {
                    drawer.apply {
                        fill = settings.color
                        stroke = ColorRGBa.TRANSPARENT
                        circle(coord, settings.brushSize)
                    }
                }
            }

            drawer.image(target.colorBuffer(0), canvasRect.x, canvasRect.y)

            drawer.fill = ColorRGBa.TRANSPARENT
            drawer.circle(mouse.position, settings.brushSize / 2)


            drawer.image(result, guiOffset, 256.0)
        }
    }
}


/**
 * Utils
 */
fun Rectangle.isIntersecting(other: Rectangle) {

}

fun Rectangle.isInside(point: Vector2): Boolean {
    return point.x > this.x && point.x < this.x + this.width
        && point.y > this.y && point.y < this.y + this.height
}

fun Vector2.isInside(rect: Rectangle): Boolean {
    return this.x > rect.x && this.x < rect.x + rect.width
            && this.y > rect.y && this.y < rect.y + rect.height
}

fun map(a: Rectangle, b: Rectangle, point: Vector2): Vector2 =
    Vector2(
        map(a.x..(a.x+a.width), b.x..b.width, point.x),
        map(a.y..(a.y+a.height), b.x..b.height, point.y))
