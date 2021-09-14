import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.patterns.Checkers
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.random
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.timeoperators.TimeOperators
import org.openrndr.orml.u2net.U2Net
import org.openrndr.shape.Rectangle
import org.openrndr.extra.timer.repeat
import kotlin.math.PI
import kotlin.math.cos

data class Fruit(
    val sourceImage: ColorBuffer,
    val resultImage: ColorBuffer,
    val rect: Rectangle,
    var anim: Double
)



fun main() {
    application {
        configure {
            width = 1920
            height = 1080
        }

        program {
            val u2 = U2Net.load()

            val images = listOf<String>(
                "data/images/cheeta.jpg",
                "data/images/apple.jpg",
                "data/images/ananas.jpg"
            )

            val fruits = mutableListOf<Fruit>()


            repeat(5.0) {
                val image = loadImage(images.random())
                val result = u2.removeBackground(image)

                val x = random(- (width.toDouble() / 2.0), width.toDouble() / 2.0)
                val y = random(- (height.toDouble() / 2.0), height.toDouble() / 2.0)

                fruits.add(Fruit(
                    sourceImage = image,
                    resultImage = result,
                    rect = Rectangle(x, y, result.width.toDouble(), result.height.toDouble()),
                    anim = 0.0
                ))
            }

            val checkers = Checkers()
            val settings = object {
                @DoubleParameter("scale", 0.0, .5)
                var scale: Double = 0.04
            }

            val gui = GUI()
            gui.add(settings, "Checkers")
            extend(gui)


            val composite = compose {
                layer {
                    post(checkers) {
                        size = settings.scale
                    }
                }
            }



            extend {
                checkers.size = settings.scale
                composite.draw(drawer)
                drawer.fill = ColorRGBa.TRANSPARENT

                fruits.forEach {
                    val x = it.anim * it.resultImage.width

                    val source = Rectangle(x,0.0, it.resultImage.width.toDouble() - x, it.resultImage.height*1.0)
                    val target = Rectangle(it.rect.x + x,it.rect.y, it.resultImage.width.toDouble() - x, it.resultImage.height*1.0)


                    drawer.image(it.sourceImage, source, target)


                    drawer.image(it.resultImage, it.rect.corner)
                    drawer.rectangle(target)
                    drawer.rectangle(it.rect)


                    if (it.anim < 1) {
                        it.anim += .005
                    }
                }
            }
        }
    }
}