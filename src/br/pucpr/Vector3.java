package br.pucpr;

import java.awt.*;

public class Vector3 implements Cloneable {
    private float r;
    private float g;
    private float b;

    public Vector3() {
    }
    public Vector3(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    public Vector3(Color c) {
        r = c.getRed() / 255.0f;
        g = c.getGreen() / 255.0f;
        b = c.getBlue() / 255.0f;
    }
    public Vector3(Vector3 other) {
        this(other.r, other.g, other.b);
    }

    public Vector3 set(float rgb) {
        this.r = rgb;
        this.g = rgb;
        this.b = rgb;
        return this;
    }

    public Vector3(int color) {
        this(new Color(color));
    }

    public float getR() {
        return r;
    }
    public float getG() {
        return g;
    }
    public float getB() {
        return b;
    }

    public int intR() {
        return (int)(r * 255);
    }
    public int intG() {
        return (int)(g * 255);
    }
    public int intB() {
        return (int)(b * 255);
    }

    public Vector3 add(Vector3 other) {
        r += other.r;
        g += other.g;
        b += other.b;
        return this;
    }
    public static Vector3 add(Vector3 vector, Vector3 other) {
        return vector.clone().add(other);
    }

    public Vector3 add(float value) {
        r += value;
        g += value;
        b += value;
        return this;
    }
    public static Vector3 add(Vector3 vector, float value) {
        return vector.clone().add(value);
    }
    public static Vector3 add(float value, Vector3 vector) {
        return vector.clone().add(value);
    }

    public Vector3 subtract(Vector3 other) {
        r -= other.r;
        g -= other.g;
        b -= other.b;
        return this;
    }
    public static Vector3 subtract(Vector3 vector, Vector3 other) {
        return vector.clone().subtract(other);
    }

    public Vector3 subtract(float value) {
        r -= value;
        g -= value;
        b -= value;
        return this;
    }
    public static Vector3 subtract(Vector3 vector, float value) {
        return vector.clone().subtract(value);
    }
    public static Vector3 subtract(float value, Vector3 vector) {
        Vector3 r = new Vector3();
        r.r = value - vector.r;
        r.g = value - vector.g;
        r.b = value - vector.b;
        return r;
    }

    public Vector3 multiply(float s) {
        r *= s;
        g *= s;
        b *= s;
        return this;
    }
    public static Vector3 multiply(Vector3 vector, float s) {
        return vector.clone().multiply(s);
    }
    public static Vector3 multiply(float s, Vector3 vector) {
        return vector.clone().multiply(s);
    }

    public Vector3 multiply(float r, float g, float b) {
        this.r *= r;
        this.g *= g;
        this.b *= b;
        return this;
    }
    public static Vector3 multiply(Vector3 vector, float r, float g, float b) {
        return vector.clone().multiply(r, g, b);
    }

    public Vector3 multiply(Vector3 other) {
        return multiply(other.r, other.g, other.b);
    }
    public static Vector3 multiply(Vector3 vector, Vector3 other) {
        return vector.clone().multiply(other);
    }

    public Vector3 divide(float s) {
        return multiply(1.0f / s);
    }
    public static Vector3 divide(Vector3 vector, float s) {
        return vector.clone().divide(s);
    }

    public float dot(Vector3 other) {
        return r * other.r + g * other.g + b * other.b;
    }

    private static float clamp(float v) {
        return v > 1.0f ? 1.0f : (v < 0.0f ? 0.0f : v);
    }
    public Vector3 clamp() {
        r = clamp(r);
        g = clamp(g);
        b = clamp(b);
        return this;
    }
    public static Vector3 clamp(Vector3 vector) {
        return vector.clone().clamp();
    }

    public float sizeSqr() {
        return r*r + g*g + b*b;
    }

    public float size() {
        return (float) Math.sqrt(sizeSqr());
    }

    public Vector3 clone() {
        return new Vector3(r, g, b);
    }

    public int getRGB() {
        return new Color(intR(), intG(), intB()).getRGB();
    }

    public Vector3 abs() {
        this.r = Math.abs(r);
        this.g = Math.abs(g);
        this.b = Math.abs(b);
        return this;
    }

    public static Vector3 abs(Vector3 v) {
        return v.clone().abs();
    }
}
