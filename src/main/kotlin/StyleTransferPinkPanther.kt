import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.orml.styletransfer.StyleEncoder
import org.openrndr.orml.styletransfer.StyleTransformer


fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        val encoder = StyleEncoder.load()
        val transformer = StyleTransformer.load()

        val contentImage = loadImage("data/images/cats/1.jpg")
        val styleImage = loadImage("data/images/pinkpanther.jpg")


        val styleVector = encoder.encodeStyle(styleImage)


        extend {
            val transformed = transformer.transformStyle(contentImage, styleVector)
            drawer.imageFit(transformed, 0.0,0.0, width.toDouble(), height.toDouble())
        }
    }
}