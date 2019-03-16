#version 420 core

layout(location = 0) out vec4 color;

layout(location = 0) in vec3 Color;
layout(location = 1) in vec2 fpos;
layout(location = 2) in vec4 containerData;
layout(location = 3) in vec2 Uv;

layout (set = 0, binding = 2) uniform sampler2D iconSampler;

void main() {
    if (fpos.x < containerData.x || fpos.x > containerData.x + containerData.z ||
        fpos.y < containerData.y || fpos.y > containerData.y + containerData.w) {
      discard;
    }
    else {
        if (Uv.x > 0.0f && Uv.y > 0.0f) {
            color = texture(iconSampler, Uv) * vec4(Color, 1.0f);
        }
        else {
            color = vec4(Color, 1.0f);
        }
    }
}
