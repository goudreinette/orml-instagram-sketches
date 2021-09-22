import org.openrndr.application
import java.io.File

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.blur.BoxBlur
import org.openrndr.extra.fx.color.ColorCorrection
import org.openrndr.extra.fx.patterns.Checkers
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.random
import org.openrndr.extra.parameters.ColorParameter
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.timeoperators.TimeOperators
import org.openrndr.orml.u2net.U2Net
import org.openrndr.shape.Rectangle
import org.openrndr.extra.timer.repeat
import org.openrndr.extras.easing.*
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.ScreenRecorder
import java.awt.Color
import kotlin.math.PI
import kotlin.math.cos





fun main() {
    data class Anim(var x: Double = 0.0, var opacity: Double = 0.0) : Animatable()

    data class Fruit(
        val sourceImage: ColorBuffer,
        val resultImage: ColorBuffer,
        val rect: Rectangle,
        var anim: Anim
    )

    application {
        configure {
            width = 400
            height = 400
        }


        program {
            val u2 = U2Net.load()
//            val filter = ColorCorrection()

            val images = mutableListOf<ColorBuffer>()

            File("data/images/cats").list { dir, name ->
                val path = "data/images/cats/$name"
                println(path)
                images.add(loadImage(path))
                true
            }


            val fruits = mutableListOf<Fruit>()

            var i = 0

            repeat(2.0) {
                val image = images[i % images.size]

                i++

                val anim = Anim()

                anim.animate(anim::x, 1.0, 2000, Easing.CubicOut, 500);
                anim.animate(anim::opacity, 1.0, 500, Easing.CubicOut);
                anim.animate(anim::opacity, 0.0, 500, Easing.CubicOut, 2000);

                val rt = renderTarget(width, height) {
                    colorBuffer()
                }

                drawer.isolatedWithTarget(rt) {
                    imageFit(image, 0.0,0.0, width.toDouble(), height.toDouble(), 0.0, 0.0, FitMethod.Contain)
                }

                val result = u2.removeBackground(rt.colorBuffer(0))


                fruits.add(Fruit(
                    sourceImage = rt.colorBuffer(0),
                    resultImage = result,
                    rect = Rectangle(0.0,0.0, result.width.toDouble(), result.height.toDouble()),
                    anim = anim
                ))
            }

            val checkers = Checkers()
            val settings = object {
                @DoubleParameter("scale", 0.0, .5)
                var scale: Double = 0.04

                @ColorParameter("foreground")
                var foreground: ColorRGBa = ColorRGBa.PINK

                @ColorParameter("background")
                var background: ColorRGBa = ColorRGBa.PINK
            }

            val gui = GUI()
            gui.add(settings, "Checkers")
//            extend(gui)


            val composite = compose {
                layer {
                    post(checkers) {
                        size = settings.scale
                        foreground = settings.foreground
                        background = settings.background
                    }
                }
            }


//            extend(ScreenRecorder().apply {
//                frameRate = 60
//            })

            val blurred = colorBuffer(width, height)
            val blur = BoxBlur()

            extend {
                checkers.size = settings.scale
                composite.draw(drawer)
                drawer.fill = ColorRGBa.TRANSPARENT

                fruits.forEach {
                    it.anim.updateAnimation()

                    val x = it.anim.x * it.resultImage.width

                    val source = Rectangle(x,0.0, it.resultImage.width.toDouble() - x, it.resultImage.height*1.0)
                    val target = Rectangle(it.rect.x + x,it.rect.y, it.resultImage.width.toDouble() - x, it.resultImage.height*1.0)

//                    drawer.translate(it.rect.corner)
//                    drawer.scale(it.anim.opacity)
                    drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(it.anim.opacity))
                    drawer.image(it.sourceImage, source, target)
//
                    blur.apply(it.resultImage, blurred)
                    drawer.image(blurred)

                    drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(it.anim.opacity))
                    drawer.image(it.resultImage, it.rect.corner)


                    // IDEA can also use easing functions here
                    drawer.strokeWeight = 3.0
                    drawer.stroke = ColorRGBa.WHITE
                    if (it.anim.x < 1) {
                        drawer.rectangle(target)
                        drawer.rectangle(it.rect)
                    }
                }
            }
        }
    }
}
