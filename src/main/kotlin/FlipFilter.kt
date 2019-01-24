import org.openrndr.draw.Filter
import org.openrndr.draw.filterShaderFromUrl

class FlipFilter : Filter(filterShaderFromUrl("file:data/shader.frag")) {
}