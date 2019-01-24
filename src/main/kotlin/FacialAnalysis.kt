import org.openrndr.MouseButton
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.compositor.blend
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.ffmpeg.FFMPEGVideoPlayer
import org.openrndr.filter.blend.*
import org.openrndr.shape.Circle

class Step(var x: Double, var y: Double, var width: Double, var text: String)

fun main() = application {

    configure {
        width = 1280
        height = 720
        title = "Premium Cognitive Services API"
    }

    program {
        // Buffers and devices
        val videoPlayer = FFMPEGVideoPlayer.fromDevice("1", width = 640, height = 480, framerate = 30.0)
        videoPlayer.start()

        val rt = renderTarget(640, 480) {
            colorBuffer()
            depthBuffer()
        }

        val flippedBuffer = colorBuffer(width, height)


        // Filters
        val flipFilter = FlipFilter()
        val overlay: Filter by lazy { Overlay() }

        // Typography
        val font = FontImageMap.fromUrl("file:data/Arial.ttf", 50.0)

        // Steps
        var currentStep = 0
        val steps = listOf(
                Step(120.0, 120.0, 532.0, "Please step into the frame"),
                Step(150.0, 500.0, 534.0, "Perfect. Please hold still…"),
                Step(500.0, 120.0, 249.0, "Analyzing…"),
                Step(500.0, 500.0, 555.0, "Your report is being printed")
        )

        // Compositor
        val drawing = compose {

            layer {
                draw {
                    drawer.background(ColorRGBa.PINK)

                    drawer.isolatedWithTarget(rt) {
                        drawer.ortho(rt)

                        videoPlayer.next()
                        videoPlayer.draw(drawer)
                    }

                    flipFilter.apply(rt.colorBuffer(0), flippedBuffer)

                    drawer.image(flippedBuffer, 0.0 ,-120.0, 1280.0, 960.0)
                }
            }

            layer {
                draw {
                    drawer.fill = if(currentStep > 0) ColorRGBa.RED else ColorRGBa.BLUE
                    drawer.stroke = null
                    drawer.rectangle((width / 2.0) - 250.0, (height / 2.0) - 250.0, 500.0, 500.0)
                }

                blend(overlay)
            }

            layer {
                draw {
                    val stepLabel = steps[currentStep]

                    // Path
                    val point = Circle(
                            stepLabel.x,
                            stepLabel.y,
                            90.0).contour.position((seconds * 0.1) % 1.0)

                    // Box
                    drawer.fill = ColorRGBa.WHITE
                    drawer.stroke = ColorRGBa.BLACK
                    drawer.rectangle(point.x, point.y, stepLabel.width, 50.0)

                    // Text
                    drawer.fill = ColorRGBa.BLACK
                    drawer.fontMap = font
                    drawer.text(stepLabel.text, point.x + 5, point.y + 40)
                }
            }
        }

        // Here we go
        extend {
            drawing.draw(drawer)
        }

        // Mouse clicks
        mouse.clicked.listen {
            if (it.button == MouseButton.LEFT) {
                currentStep = (currentStep + 1) % steps.size
            } else {
                currentStep = 0
            }
        }
    }
}