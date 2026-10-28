package pulse;

import java.util.ArrayList;
import java.util.List;

public class ModelPart {
   public ModelPart a;
   public List<ModelPart> b = new ArrayList<>();
   public List<ModelCube> c = new ArrayList<>();
   public String d;
   public boolean e;
   public float f;
   public float g;
   public float h;
   private float k;
   private float l;
   private float m;
   private float n;
   private float o;
   private float p;
   private float q = 1.0F;
   private float r = 1.0F;
   private float s = 1.0F;

   public ModelPart(String name) {
      this.d = name;
   }

   public float a() {
      return this.k;
   }

   public float b() {
      return this.l;
   }

   public float c() {
      return this.m;
   }

   public void a(float rotationX) {
      this.k = rotationX;
   }

   public void b(float rotationY) {
      this.l = rotationY;
   }

   public void c(float rotationZ) {
      this.m = rotationZ;
   }

   public float d() {
      return this.n;
   }

   public float e() {
      return this.o;
   }

   public float f() {
      return this.p;
   }

   public void d(float x) {
      this.n = x;
   }

   public void e(float y) {
      this.o = y;
   }

   public void f(float z) {
      this.p = z;
   }

   public float g() {
      return this.q;
   }

   public float h() {
      return this.r;
   }

   public float i() {
      return this.s;
   }

   public void g(float scaleX) {
      this.q = scaleX;
   }

   public void h(float scaleY) {
      this.r = scaleY;
   }

   public void i(float scaleZ) {
      this.s = scaleZ;
   }

   public float j() {
      return this.f;
   }

   public float k() {
      return this.g;
   }

   public float l() {
      return this.h;
   }

   public void j(float pivotX) {
      this.f = pivotX;
   }

   public void k(float pivotY) {
      this.g = pivotY;
   }

   public void l(float pivotZ) {
      this.h = pivotZ;
   }

   public String m() {
      return this.d;
   }

   public boolean n() {
      return this.e;
   }

   public void a(boolean hidden) {
      this.e = hidden;
   }
}
