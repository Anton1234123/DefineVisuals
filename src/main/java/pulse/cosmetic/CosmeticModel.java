package pulse;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class CosmeticModel {
   private final String c;
   private final int d;
   private final int e;
   private String f;
   private Identifier g;
   private AttachmentPoint h = AttachmentPoint.HEAD;
   private float i = 1.0F;
   private float j;
   private float k;
   private float l;
   private float m;
   private float n;
   private float o;
   private float p = 0.0F;
   private float q = 1.0F;
   private float r = 0.0F;
   private JsonObject s;

   public CosmeticModel(String name, int id, int ownerId) {
      this.c = name;
      this.d = id;
      this.e = ownerId;
   }

   public String a() {
      return this.c;
   }

   public int b() {
      return this.d;
   }

   public int c() {
      return this.e;
   }

   public String d() {
      return this.f;
   }

   public void a(String value) {
      this.f = value;
   }

   public Identifier e() {
      return this.g;
   }

   public void a(Identifier texture) {
      this.g = texture;
   }

   public AttachmentPoint f() {
      return this.h;
   }

   public void a(AttachmentPoint point) {
      this.h = point;
   }

   public float g() {
      return this.i;
   }

   public void a(float scale) {
      this.i = scale;
   }

   public float h() {
      return this.j;
   }

   public void b(float x) {
      this.j = x;
   }

   public float i() {
      return this.k;
   }

   public void c(float y) {
      this.k = y;
   }

   public float j() {
      return this.l;
   }

   public void d(float z) {
      this.l = z;
   }

   public float k() {
      return this.m;
   }

   public void e(float rotationX) {
      this.m = rotationX;
   }

   public float l() {
      return this.n;
   }

   public void f(float rotationY) {
      this.n = rotationY;
   }

   public float m() {
      return this.o;
   }

   public void g(float rotationZ) {
      this.o = rotationZ;
   }

   public float n() {
      return this.p;
   }

   public void h(float bobAmount) {
      this.p = bobAmount;
   }

   public float o() {
      return this.q;
   }

   public void i(float animationSpeed) {
      this.q = animationSpeed;
   }

   public float p() {
      return this.r;
   }

   public void j(float spinSpeed) {
      this.r = spinSpeed;
   }

   public JsonObject q() {
      return this.s;
   }

   public void a(JsonObject modelJson) {
      this.s = modelJson;
   }
}
