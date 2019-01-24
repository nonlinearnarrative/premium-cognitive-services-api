import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Filter
import org.openrndr.draw.FontImageMap
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.renderTarget
import org.openrndr.extra.compositor.blend
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.ffmpeg.FFMPEGVideoPlayer
import org.openrndr.filter.blend.*
import org.openrndr.shape.Circle

fun main() = application {

    configure {
        width = 1280
        height = 720
        title = "Premium Cognitive Services API"
    }

    program {
        println(FFMPEGVideoPlayer.listDevices())

        // Buffers and devices
        val videoPlayer = FFMPEGVideoPlayer.fromDevice(width = width, height = height, framerate = 30.0)
        videoPlayer.start()

        val rt = renderTarget(width, height) {
            colorBuffer()
            depthBuffer()
        }

        val flippedBuffer = colorBuffer(width, height)


        // Filters
        val flipFilter = FlipFilter()
        val overlay: Filter by lazy { Overlay() }

        // Typography
        val font = FontImageMap.fromUrl("file:data/Arial.ttf", 50.0)

        // Compositor
        val drawing = compose {

            layer {
                draw {
                    drawer.withTarget(rt) {
                        drawer.background(ColorRGBa.BLACK)
                        videoPlayer.next()
                        videoPlayer.draw(drawer)
                    }

                    flipFilter.apply(rt.colorBuffer(0), flippedBuffer)

                    drawer.image(flippedBuffer, 0.0, 0.0)
                }
            }

            layer {
                draw {
                    drawer.fill = ColorRGBa.WHITE
                    drawer.stroke = null
                    drawer.rectangle((width / 2.0) - 150.0, (height / 2.0) - 150.0, 300.0, 300.0)
                }

                blend(overlay)
            }

            layer {
                draw {
                    // Path
                    val point = Circle(
                            185.0,
                            height / 2.0,
                            90.0).contour.position((seconds * 0.1) % 1.0)

                    // Box
                    drawer.fill = ColorRGBa.WHITE
                    drawer.stroke = ColorRGBa.BLACK
                    drawer.rectangle(point.x, point.y, 532.0, 50.0)

                    // Text
                    drawer.fill = ColorRGBa.BLACK
                    drawer.fontMap = font
                    drawer.text("Please step into the frame", point.x + 5, point.y + 40)
                }
            }
        }

        // Here we go
        extend {
            drawing.draw(drawer)
        }
    }
}