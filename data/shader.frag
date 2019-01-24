#version 330

in vec2 v_texCoord0;
uniform sampler2D tex0;

out vec4 o_color;

void main() {
    vec2 flipped = vec2(1.0 - v_texCoord0.x, v_texCoord0.y);

    vec4 color = texture(tex0, flipped);
    o_color = color;
}