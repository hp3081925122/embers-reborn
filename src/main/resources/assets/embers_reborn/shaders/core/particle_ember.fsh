#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

// Ember particle colour, ported from the 1.21.1 particle_ember / particle_ember_fab shaders.
// ALPHA_CUTOFF replaces the AlphaCutoff uniform; FAB selects the old fabulous-graphics variant.

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
#ifdef FAB
    color.rgb *= color.a;
    color.a = (color.r + color.g + color.b) / 3.0;
    color *= vertexColor;
    color.rgb *= 2.0;
    color.a /= 2.0;
    if (color.r < vertexColor.r || color.g < vertexColor.g || color.b < vertexColor.b) {
        color.rgb = vertexColor.rgb;
    }
    color *= ColorModulator;
    if (color.a <= ALPHA_CUTOFF * 2.0) {
        discard;
    }
#else
    if (color.a <= ALPHA_CUTOFF) {
        discard;
    }
    color.rgb *= color.a;
    color.a = (color.r + color.g + color.b) / 3.0;
    color.rgb = vec3(1.0, 1.0, 1.0);
    color *= vertexColor * ColorModulator;
#endif
    if (color.a <= 0.0) {
        discard;
    }
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
