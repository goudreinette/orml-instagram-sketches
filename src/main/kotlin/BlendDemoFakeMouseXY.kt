import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.loadImage
import org.openrndr.draw.renderTarget
import org.openrndr.draw.tint
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.orml.styletransfer.StyleEncoder
import org.openrndr.orml.styletransfer.StyleTransformer


fun main() = application {
    data class Anim(var x: Double = 0.0, var y: Double = 0.0) : Animatable()

    configure {
        width = 600
        height = 600
    }

    program {
        val cursor = loadImage("data/images/cursor.png")
        val encoder = StyleEncoder.load()
        val transformer = StyleTransformer.load()

        val contentImage = loadImage("data/images/image-001.png")

        val contentImageFitted = renderTarget(width, height) {
            colorBuffer()
        }

        drawer.isolatedWithTarget(contentImageFitted) {
            drawer.imageFit(contentImage, .0, .0, width.toDouble(), height.toDouble(), .0, .0)
        }


        val styleImage0 = loadImage("data/images/style-003.jpg")
        val styleImage1 = loadImage("data/images/style-001.jpg")

        val styleVector0 = encoder.encodeStyle(styleImage0)
        val styleVector1 = encoder.encodeStyle(styleImage1)

        val anim = Anim()


//        extend(ScreenRecorder().apply {
//            frameRate = 60
//        })


        extend {
            /**
             * Animation
             */
            anim.updateAnimation()
            if (!anim.hasAnimations()) {
                // The 4 steps/directions
                anim.apply {
                    ::x.animate(width.toDouble() - 50, 2000, Easing.CubicOut, 2000)
                    ::x.complete()

                    ::y.animate(height.toDouble() - 50, 2000, Easing.CubicOut);
                    ::y.complete()

                    ::x.animate(0.0, 2000, Easing.CubicOut);
                    ::x.complete()

                    ::y.animate(0.0, 2000, Easing.CubicOut);
                    ::y.complete()
                }
            }



            val x = anim.x // simplex(0,  .95 * seconds) * .8 * width + width/2
            val y = anim.y // simplex(1000, .5*  seconds) * .8 * height + height/2

            val f = (x/width).toFloat()
            val yy = (y / height)


            val styleVector = (styleVector0 zip styleVector1).map {
                it.first * f + it.second * (1.0f-f)
            }.toFloatArray()

            val transformed = transformer.transformStyle(contentImageFitted.colorBuffer(0), styleVector)


            drawer.apply {
                drawStyle.colorMatrix = tint(ColorRGBa.WHITE)
                image(contentImageFitted.colorBuffer(0))

                drawStyle.colorMatrix = tint(ColorRGBa.WHITE.opacify(yy))
                image(transformed)


                // Cursor
                drawStyle.colorMatrix = tint(ColorRGBa.WHITE)
                imageFit(cursor, x, y, 30.0, 30.0, 0.0, 0.0, FitMethod.Contain)
            }
        }
    }
}
