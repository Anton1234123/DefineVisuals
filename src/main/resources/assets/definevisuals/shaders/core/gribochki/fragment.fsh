#version 150

in vec2 texCoord;
in vec4 vertexColor;

uniform sampler2D Sampler0;
uniform float Time;
uniform float RgbOffset;
uniform float ChromaticIntensity;
uniform float GlitchIntensity;
uniform float PulseSpeed;
uniform float Distortion;
uniform float Random;
uniform float VignetteIntensity;
uniform float ScanlineIntensity;
uniform float FishEyeAmount;
uniform float PixelationAmount;
uniform float ColorInversion;
uniform float NoiseAmount;
uniform vec4 ColorModulator;

out vec4 OutColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

vec2 distortUV(vec2 uv, float time, float distortion) {
    float wave = sin(uv.x * 10.0 + time * PulseSpeed) * 0.01 * distortion;
    wave += cos(uv.y * 8.0 + time * PulseSpeed * 1.3) * 0.01 * distortion;
    wave += sin((uv.x + uv.y) * 15.0 + time * PulseSpeed * 0.7) * 0.008 * distortion;
    return uv + vec2(wave, wave * 0.7);
}

vec2 glitchUV(vec2 uv, float time, float intensity) {
    float glitch = step(0.98, hash(vec2(floor(time * 10.0), floor(uv.y * 20.0))));
    float slice = step(0.95, hash(vec2(floor(time * 15.0), floor(uv.y * 10.0))));
    
    vec2 offset = vec2(0.0);
    if (glitch > 0.5) {
        offset.x = (hash(vec2(time, uv.y)) - 0.5) * 0.1 * intensity;
    }
    if (slice > 0.5) {
        offset.y = (hash(vec2(time + 100.0, uv.x)) - 0.5) * 0.05 * intensity;
    }
    
    return uv + offset;
}

vec3 chromaticAberration(vec2 uv, float offset, float intensity) {
    float r = texture(Sampler0, uv + vec2(offset * 0.01 * intensity, 0.0)).r;
    float g = texture(Sampler0, uv).g;
    float b = texture(Sampler0, uv - vec2(offset * 0.01 * intensity, 0.0)).b;
    
    // Add vertical chromatic aberration
    r = texture(Sampler0, uv + vec2(0.0, offset * 0.008 * intensity)).r;
    b = texture(Sampler0, uv - vec2(0.0, offset * 0.008 * intensity)).b;
    
    return vec3(r, g, b);
}

vec3 rgbShift(vec2 uv, float time, float offset, float intensity) {
    float angle = time * 2.0;
    vec2 direction = vec2(cos(angle), sin(angle));
    
    float r = texture(Sampler0, uv + direction * offset * 0.015 * intensity).r;
    float g = texture(Sampler0, uv).g;
    float b = texture(Sampler0, uv - direction * offset * 0.015 * intensity).b;
    
    return vec3(r, g, b);
}

vec3 pulseColor(vec3 color, float time, float speed) {
    float pulse = 0.5 + 0.5 * sin(time * speed);
    float boost = 1.0 + pulse * 0.3;
    return color * boost;
}

vec3 scanlines(vec2 uv, float time, float intensity) {
    float scanline = sin(uv.y * 800.0 + time * 10.0) * 0.04 * intensity;
    return vec3(scanline);
}

vec3 vignette(vec2 uv, float intensity) {
    vec2 center = uv - 0.5;
    float dist = length(center);
    float vignette = 1.0 - dist * intensity;
    return vec3(vignette);
}

vec2 fishEye(vec2 uv, float amount) {
    vec2 center = uv - 0.5;
    float dist = length(center);
    float distortion = 1.0 + amount * dist * dist;
    return center * distortion + 0.5;
}

vec2 pixelate(vec2 uv, float amount) {
    float pixels = amount * 100.0 + 10.0;
    vec2 pixelated = floor(uv * pixels) / pixels;
    return mix(uv, pixelated, amount);
}

vec3 invertColor(vec3 color, float amount) {
    return mix(color, 1.0 - color, amount);
}

void main() {
    vec2 uv = texCoord;
    
    // Apply pixelation
    uv = pixelate(uv, PixelationAmount);
    
    // Apply fish eye
    uv = fishEye(uv, FishEyeAmount);
    
    // Apply distortion
    uv = distortUV(uv, Time, Distortion);
    
    // Apply glitch
    uv = glitchUV(uv, Time, GlitchIntensity);
    
    // Apply chromatic aberration
    vec3 color = chromaticAberration(uv, RgbOffset, ChromaticIntensity);
    
    // Apply RGB shift
    color = rgbShift(uv, Time, RgbOffset, ChromaticIntensity * 0.5);
    
    // Apply pulse
    color = pulseColor(color, Time, PulseSpeed);
    
    // Add scanlines with intensity
    color += scanlines(uv, Time, ScanlineIntensity);
    
    // Add vignette with intensity
    color *= vignette(uv, VignetteIntensity);
    
    // Add noise with amount
    float noiseVal = noise(uv * 100.0 + Time * 5.0) * 0.05 * NoiseAmount;
    color += noiseVal;
    
    // Apply color inversion
    color = invertColor(color, ColorInversion);
    
    // Color cycling
    float cycle = sin(Time * 0.5) * 0.5 + 0.5;
    vec3 cycleColor = mix(vec3(1.0, 0.0, 0.0), vec3(0.0, 1.0, 0.0), cycle);
    cycleColor = mix(cycleColor, vec3(0.0, 0.0, 1.0), sin(Time * 0.3) * 0.5 + 0.5);
    color = mix(color, color * cycleColor * 1.5, 0.15);
    
    // Random color bursts
    float burst = step(0.995, hash(vec2(Time, Random)));
    if (burst > 0.5) {
        vec3 burstColor = vec3(
            hash(vec2(Time, 0.0)),
            hash(vec2(Time, 1.0)),
            hash(vec2(Time, 2.0))
        );
        color = mix(color, burstColor * 2.0, 0.3);
    }
    
    // Ensure color stays in valid range
    color = clamp(color, 0.0, 1.0);
    
    OutColor = vec4(color, 1.0) * ColorModulator * vertexColor;
}
