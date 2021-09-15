import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.grayscale
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.color.ColorCorrection
import org.openrndr.extra.fx.patterns.Checkers
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.random
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.timeoperators.TimeOperators
import org.openrndr.orml.u2net.U2Net
import org.openrndr.shape.Rectangle
import org.openrndr.extra.timer.repeat
import org.openrndr.extras.easing.*
import kotlin.math.PI
import kotlin.math.cos


data class Anim(var x: Double = 0.0, var opacity: Double = 0.0) : Animatable()

data class Fruit(
    val sourceImage: ColorBuffer,
    val resultImage: ColorBuffer,
    val rect: Rectangle,
    var anim: Anim
)



fun main() {
    application {
        configure {
            width = 800
            height = 800
        }

        program {
            val u2 = U2Net.load()
//            val filter = ColorCorrection()

            val images = listOf<String>(
                "data/images/cheeta.jpg",
                "data/images/apple.jpg",
                "data/images/ananas.jpg"
            ).map {
                loadImage(it)
            }

            val fruits = mutableListOf<Fruit>()


            repeat(2.0) {
                val image = images.random()
                val result = u2.removeBackground(image)

                val x = random(- (width.toDouble() / 2.0), width.toDouble() / 2.0)
                val y = random(- (height.toDouble() / 2.0), height.toDouble() / 2.0)

                val anim = Anim()

                anim.animate(anim::x, 1.0, 2000, Easing.CubicOut, 500);
                anim.animate(anim::opacity, 1.0, 500, Easing.CubicOut);

                fruits.add(Fruit(
                    sourceImage = image,
                    resultImage = result,
                    rect = Rectangle(x, y, result.width.toDouble(), result.height.toDouble()),
                    anim = anim
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
                    it.anim.updateAnimation()

                    val x = it.anim.x * it.resultImage.width

                    val source = Rectangle(x,0.0, it.resultImage.width.toDouble() - x, it.resultImage.height*1.0)
                    val target = Rectangle(it.rect.x + x,it.rect.y, it.resultImage.width.toDouble() - x, it.resultImage.height*1.0)


                    drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(it.anim.opacity - 0.2))
                    drawer.image(it.sourceImage, source, target)


                    drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(it.anim.opacity))
                    drawer.image(it.resultImage, it.rect.corner)


                    // IDEA can also use easing functions here
                    drawer.strokeWeight = 3.0
                    drawer.stroke = ColorRGBa.PINK.opacify(1 - easeCubicIn(it.anim.x))
                    if (it.anim.x < 1) {
                        drawer.rectangle(target)
                        drawer.rectangle(it.rect)
                    }
                }
            }
        }
    }
}