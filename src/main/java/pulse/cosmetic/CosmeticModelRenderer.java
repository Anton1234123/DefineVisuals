package pulse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import ru.pulse.Pulse;

public class CosmeticModelRenderer {
   private static CosmeticModelRenderer c;
   private final Map<Integer, ModelDefinition> d = new ConcurrentHashMap<>();
   private final Map<Integer, CosmeticModelRenderer.CompiledModel> e = new ConcurrentHashMap<>();
   private final Set<Integer> f = ConcurrentHashMap.newKeySet();
   private final Map<Integer, Long> g = new ConcurrentHashMap<>();
   private final Map<Integer, Map<String, float[]>> h = new ConcurrentHashMap<>();
   private final CosmeticModelLoader i = new CosmeticModelLoader();
   public static int a;
   public static boolean b;

   public static CosmeticModelRenderer a() {
      if (c == null) {
         c = new CosmeticModelRenderer();
      }

      return c;
   }

   public void a(CosmeticModel var1, MatrixStack var2, VertexConsumerProvider var3, int var4) {
      if (var1 != null && var1.e() != null) {
         ModelDefinition var5 = this.a(var1);
         if (var5 != null) {
            CosmeticModelRenderer.CompiledModel var6 = this.b(var1);
            if (var6 != null) {
               this.a(var5, var6, var1.b());
            }

            Identifier var7 = var1.e();
            RenderLayer var8 = RenderLayer.getEntityCutoutNoCull(var7);
            VertexConsumer var9 = var3.getBuffer(var8);
            RenderSystem.disableCull();

            for (ModelPart var11 : var5.a) {
               this.a(var11, var2, var9, var4, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            RenderSystem.enableCull();
         }
      }
   }

   private ModelDefinition a(CosmeticModel var1) {
      int var2 = var1.b();
      if (!this.d.containsKey(var2)) {
         ModelDefinition var3 = this.i.a(var1);
         if (var3 != null) {
            this.d.put(var2, var3);
            this.a(var2, var3);
            Logger var10000 = Pulse.getLOGGER();
            String var4 = var1.a();
            StringBuilder var10001 = new StringBuilder();
            int var6 = -792468225;
            int var5 = 1131194635;
            int var10002 = (var5 | var6) - (var5 & var6);
            var6 = -1424888266;
            var5 = var10002;
            var10002 = (var5 & ~var6 | ~var5 & var6) * (var5 & ~var6 | ~var5 & var6);
            int var10003 = (int)-6308362442501138298L;
            var6 = 759312543;
            var5 = var10003;
            var10003 = var5 + var6 - 2 * (var5 & var6);
            var6 = 935999732;
            var5 = var10003;
            var10003 = (var5 | var6) & ~(var5 & var6);
            int var10004 = (int)5659419676923333250L;
            var6 = -188664614;
            var5 = var10004;
            var10004 = var5 + var6 - 2 * (var5 & var6);
            var6 = 1583411191;
            var5 = var10004;
            var6 = var5 + var6 - 2 * (var5 & var6);
            var5 = var10003;
            var10003 = (var5 | var6) + (var5 & var6);
            var10004 = (int)-7658879654909786589L;
            var6 = -1557318297;
            var5 = var10004;
            var10004 = var5 + var6 - 2 * (var5 & var6);
            var6 = -296058515;
            var5 = var10004;
            var10004 = (var5 | var6) & ~(var5 & var6);
            var10004 = (int)7781495978404788953L;
            var6 = 2038901782;
            var5 = var10004;
            var10004 = (var5 | var6) & ~(var5 & var6);
            var6 = 1015433260;
            var5 = var10004;
            var10004 = var5 + var6 - 2 * (var5 & var6);
            int var10005 = (int)4124010502068121389L;
            var6 = -1496167214;
            var5 = var10005;
            var10005 = (var5 | var6) - (var5 & var6);
            var6 = -405792311;
            var5 = var10005;
            var10005 = (var5 & ~var6 | ~var5 & var6) * (var5 & ~var6 | ~var5 & var6);
            var6 = -580178651;
            var5 = 664954988;
            var10005 = var5 + var6 - 2 * (var5 & var6);
            var6 = 1139675742;
            var5 = var10005;
            var10005 = var5 + var6 - 2 * (var5 & var6);
            int var10007 = (int)5850031404345978919L;
            var6 = -1329415577;
            var5 = var10007;
            var10007 = (var5 | var6) - (var5 & var6);
            var6 = -198620592;
            var5 = var10007;
            var10007 = (var5 | var6) & (~var5 | ~var6);
            var6 = -426637313;
            var5 = -459172816;
            var10007 = (var5 | var6) & ~(var5 & var6);
            var6 = 36934595;
            var5 = var10007;
            var10007 = var5 + var6 - 2 * (var5 & var6);
            var10007 = (int)-7308674043400714931L;
            var6 = 474933543;
            var5 = var10007;
            var10007 = (var5 | var6) & (~var5 | ~var6);
            var6 = 733310074;
            var5 = var10007;
            var10007 = (var5 | var6) & (~var5 | ~var6);
            int var10006 = (int)1109498937997786349L;
            var6 = -193899620;
            var5 = var10006;
            var10006 = (var5 | var6) & (~var5 | ~var6);
            var6 = 753052789;
            var5 = var10006;
            var10006 = (var5 | var6) & ~(var5 & var6);
            var10007 = (int)-21463277322201978L;
            var6 = -1908202235;
            var5 = var10007;
            var10007 = var5 + var6 - 2 * (var5 & var6);
            var6 = 1698299793;
            var5 = var10007;
            var10007 = (var5 | var6) & ~(var5 & var6);
            var6 = 1651360018;
            var5 = 1560603931;
            int var10008 = var5 + var6 - 2 * (var5 & var6);
            var6 = 598261209;
            var5 = var10008;
            var6 = var5 & ~var6 | ~var5 & var6;
            var5 = var10007;
            var10007 = var5 - ~var6 - 1;
            var6 = -160403761;
            var5 = 1655788463;
            var10007 = (var5 | var6) & (~var5 | ~var6);
            var6 = -1799016923;
            var5 = var10007;
            var10000.info(
               var10001.append(
                     a(
                        "л©Џзџ‹л©Їзџ‚л©©зџЋлЁ¬зџ‡л©ЈзџЋл©©зџ†лЁ¬зџЊл©ЈзџлЁ¬зџ‰л©Јзџ™л©ЎзџЏл©ёзџѓл©ЇзћђлЁ¬",
                        "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                        var10004,
                        var10005,
                        var10006,
                        (var5 | var6) - (var5 & var6)
                     )
                  )
                  .append(var4)
                  .toString()
            );
         }

         return var3;
      } else {
         return this.d.get(var2);
      }
   }

   private void a(int var1, ModelDefinition var2) {
      HashMap var3 = new HashMap();

      for (ModelPart var5 : var2.a) {
         this.a(var5, var3);
      }

      this.h.put(var1, var3);
   }

   private void a(ModelPart var1, Map<String, float[]> var2) {
      String var10001 = var1.d;
      int var6 = -1467663312;
      int var5 = 1604013145;
      int var10002 = var5 + var6 - 2 * (var5 & var6);
      var6 = -149000096;
      var5 = var10002;
      float[] var45 = new float[var5 + var6 - 2 * (var5 & var6)];
      var6 = 1409824661;
      var5 = -921231349;
      int var10004 = (var5 | var6) & ~(var5 & var6);
      var6 = -1658906722;
      var5 = var10004;
      var45[(var5 | var6) & (~var5 | ~var6)] = var1.a();
      var10004 = (int)-4215925465465187412L;
      var6 = -35053236;
      var5 = var10004;
      var10004 = (var5 | var6) - (var5 & var6);
      var6 = -1165643039;
      var5 = var10004;
      var45[var5 + var6 - 2 * (var5 & var6)] = var1.b();
      var6 = -1702197877;
      var5 = 348063178;
      var10004 = (var5 | var6) & (~var5 | ~var6);
      var6 = -1909096381;
      var5 = var10004;
      var45[var5 & ~var6 | ~var5 & var6] = var1.c();
      var6 = 330481216;
      var5 = -1416283528;
      var10004 = (var5 | var6) - (var5 & var6);
      var6 = -1205369797;
      var5 = var10004;
      var45[var5 & ~var6 | ~var5 & var6] = var1.d();
      var10004 = (int)3885677084142647398L;
      var6 = -2088289257;
      var5 = var10004;
      var10004 = var5 + var6 - 2 * (var5 & var6);
      var6 = 196445301;
      var5 = var10004;
      var45[(var5 | var6) - (var5 & var6)] = var1.e();
      var6 = -393483509;
      var5 = -1750404413;
      var10004 = var5 & ~var6 | ~var5 & var6;
      var6 = 2132875725;
      var5 = var10004;
      var45[(var5 | var6) - (var5 & var6)] = var1.f();
      var10004 = (int)-9122663595549317422L;
      var6 = -1550884483;
      var5 = var10004;
      var10004 = (var5 | var6) & ~(var5 & var6);
      var6 = -1771419735;
      var5 = var10004;
      var45[(var5 | var6) & ~(var5 & var6)] = var1.g();
      var6 = -2087904450;
      var5 = -749816392;
      var10004 = var5 + var6 - 2 * (var5 & var6);
      var6 = 1355001473;
      var5 = var10004;
      var45[(var5 | var6) & ~(var5 & var6)] = var1.h();
      var6 = -1053394135;
      var5 = -775280296;
      var10004 = var5 & ~var6 | ~var5 & var6;
      var6 = 284971641;
      var5 = var10004;
      var45[(var5 | var6) & ~(var5 & var6)] = var1.i();
      var2.put(var10001, var45);

      for (ModelPart var4 : var1.b) {
         this.a(var4, var2);
      }
   }

   private void a(ModelPart var1, MatrixStack var2, VertexConsumer var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      if (!var1.e) {
         var2.push();
         ModelTransformApplier.translatePartOffset(var1, var2);
         ModelTransformApplier.translatePartPivot(var1, var2);
         ModelTransformApplier.rotatePart(var1, var2);
         ModelTransformApplier.scalePart(var1, var2);
         ModelTransformApplier.translatePartPivotBack(var1, var2);

         for (ModelCube var11 : var1.c) {
            this.a(var11, var2, var3, var4, var5, var6, var7, var8, var9);
         }

         for (ModelPart var13 : var1.b) {
            this.a(var13, var2, var3, var4, var5, var6, var7, var8, var9);
         }

         var2.pop();
      }
   }

   private void a(ModelCube var1, MatrixStack var2, VertexConsumer var3, int var4, int var5, float var6, float var7, float var8, float var9) {
      var2.push();
      ModelTransformApplier.translateCubePivot(var1, var2);
      ModelTransformApplier.rotateCube(var1, var2);
      ModelTransformApplier.translateCubePivotBack(var1, var2);
      Entry var10 = var2.peek();
      Matrix4f var11 = var10.getPositionMatrix();
      ModelVertex[] var12 = var1.a;
      int var13 = var12.length;
      int var10000 = (int)8913926221082690003L;
      int var26 = 231655978;
      int var25 = var10000;
      var10000 = var25 & ~var26 | ~var25 & var26;
      var26 = -2073932807;
      var25 = var10000;

      for (int var14 = (var25 | var26) & (~var25 | ~var26); var14 < var13; var14++) {
         ModelVertex var15 = var12[var14];
         if (var15 != null) {
            float var16 = var15.b.a();
            float var17 = var15.b.b();
            float var18 = var15.b.c();
            if (var1.b.b() == 0.0F || var1.b.c() == 0.0F) {
               if (!(var16 < 0.0F)) {
                  String var46 = "ik";
                  var10000 = (int)-4051716907273876823L;
                  var26 = -944027509;
                  var25 = var10000;
                  var10000 = (var25 | var26) & ~(var25 & var26);
                  var26 = 94599343;
                  var25 = var10000;
                  int var24 = (var25 | var26) & ~(var25 & var26);
                  a = var24;
                  var10000 = (int)-3375483281115130544L;
                  var26 = 229370622;
                  var25 = var10000;
                  var10000 = (var25 | var26) - (var25 & var26);
                  var26 = 227731712;
                  var25 = var10000;
                  var10000 = (var25 | var26) & ~(var25 & var26);
                  var26 = 1097239844;
                  var25 = 600293025;
                  var10000 = var25 + var26 - 2 * (var25 & var26);
                  var26 = 147190311;
                  var25 = var10000;
                  if (((var25 | var26) & ~(var25 & var26)) < a) {
                     throw new ExceptionInInitializerError();
                  }
               } else {
                  var16 = -var16;
               }
            }

            if ((var1.b.a() == 0.0F || var1.b.c() == 0.0F) && var17 < 0.0F) {
               var17 = -var17;
            }

            if ((var1.b.a() == 0.0F || var1.b.b() == 0.0F) && var18 < 0.0F) {
               var18 = -var18;
            }

            ModelVertexUv[] var19 = var15.a;
            int var20 = var19.length;
            var26 = -1862378259;
            var25 = 1874607752;
            var10000 = (var25 | var26) & (~var25 | ~var26);
            var26 = -12426651;
            var25 = var10000;

            for (int var21 = var25 + var26 - 2 * (var25 & var26); var21 < var20; var21++) {
               ModelVertexUv var22 = var19[var21];
               var3.vertex(var11, var22.position.a(), var22.position.b(), var22.position.c())
                  .color(var6, var7, var8, var9)
                  .texture(var22.u, var22.v)
                  .overlay(var5)
                  .light(var4)
                  .normal(var10, var16, var17, var18);
            }
         }
      }

      var2.pop();
   }

   private CosmeticModelRenderer.CompiledModel b(CosmeticModel var1) {
      int var2 = var1.b();
      if (!this.e.containsKey(var2)) {
         if (!this.f.contains(var2)) {
            JsonObject var3 = var1.q();
            if (var3 == null) {
               this.f.add(var2);
               return null;
            }

            try {
               CosmeticModelRenderer.CompiledModel var4 = this.a(var3);
               if (var4 != null) {
                  this.e.put(var2, var4);
                  Logger var140 = Pulse.getLOGGER();
                  String var5 = var1.a();
                  StringBuilder var141 = new StringBuilder();
                  int var145 = (int)2385020719554702858L;
                  int var101 = -5899112;
                  int var36 = var145;
                  var145 = var36 & ~var101 | ~var36 & var101;
                  var101 = 1333017821;
                  var36 = var145;
                  var145 = (var36 + var101 - 2 * (var36 & var101)) * (var36 + var101 - 2 * (var36 & var101));
                  var101 = -2074088993;
                  var36 = 1731355360;
                  int var158 = var36 & ~var101 | ~var36 & var101;
                  var101 = -479360197;
                  var36 = var158;
                  var158 = (var36 | var101) - (var36 & var101);
                  var101 = 728230863;
                  var36 = -1732350837;
                  var158 = var36 + var101 - 2 * (var36 & var101);
                  var101 = -1277589691;
                  var36 = var158;
                  var158 = (var36 | var101) & ~(var36 & var101);
                  int var168 = (int)7400516227856375352L;
                  var101 = 386207770;
                  var36 = var168;
                  var168 = var36 + var101 - 2 * (var36 & var101);
                  var101 = -1528721870;
                  var36 = var168;
                  var168 = var36 + var101 - 2 * (var36 & var101);
                  var168 = (int)-1008153719100512773L;
                  var101 = 961648868;
                  var36 = var168;
                  var168 = (var36 | var101) & (~var36 | ~var101);
                  var101 = -1755841265;
                  var36 = var168;
                  var168 = (var36 | var101) - (var36 & var101);
                  var101 = -669975585;
                  var36 = 1101745574;
                  var168 = (var36 | var101) & (~var36 | ~var101);
                  var101 = -1715751312;
                  var36 = var168;
                  var168 = var36 + var101 - 2 * (var36 & var101);
                  var168 = (int)-3900607631638114782L;
                  var101 = -940342854;
                  var36 = var168;
                  var168 = var36 + var101 - 2 * (var36 & var101);
                  var101 = -1141125229;
                  var36 = var168;
                  var168 = (var36 | var101) - (var36 & var101);
                  var158 = (int)-7745530597045728643L;
                  var101 = -641948196;
                  var36 = var158;
                  var158 = (var36 | var101) & ~(var36 & var101);
                  var101 = 124055874;
                  var36 = var158;
                  var158 = (var36 | var101) & ~(var36 & var101);
                  var101 = -1395830000;
                  var36 = -2090539886;
                  var168 = var36 + var101 - 2 * (var36 & var101);
                  var101 = 774065931;
                  var36 = var168;
                  var168 = (var36 | var101) & ~(var36 & var101);
                  var168 = (int)-2454153029707899948L;
                  var101 = -758015932;
                  var36 = var168;
                  var168 = (var36 | var101) - (var36 & var101);
                  var101 = -139243798;
                  var36 = var168;
                  var168 = (var36 | var101) & ~(var36 & var101);
                  int var187 = (int)4963038199283143950L;
                  var101 = -1302369360;
                  var36 = var187;
                  var187 = (var36 | var101) - (var36 & var101);
                  var101 = 1979334434;
                  var36 = var187;
                  var187 = var36 & ~var101 | ~var36 & var101;
                  int var198 = (int)-7905832384971972933L;
                  var101 = -1156264357;
                  var36 = var198;
                  var198 = (var36 | var101) & (~var36 | ~var101);
                  var101 = -1595704074;
                  var36 = var198;
                  var101 = var36 & ~var101 | ~var36 & var101;
                  var36 = var187;
                  var187 = (var36 & ~var101) + (var101 & ~var36) + 2 * (var36 & var101);
                  var187 = (int)-5018955016638641865L;
                  var101 = 2127638939;
                  var36 = var187;
                  var187 = (var36 | var101) & (~var36 | ~var101);
                  var101 = 2050536011;
                  var36 = var187;
                  var187 = var36 + var101 - 2 * (var36 & var101);
                  int var10008 = (int)3826003298648579429L;
                  var101 = -805618112;
                  var36 = var10008;
                  var10008 = var36 & ~var101 | ~var36 & var101;
                  var101 = 1787880244;
                  var36 = var10008;
                  var10008 = var36 & ~var101 | ~var36 & var101;
                  var101 = -207618903;
                  var36 = -1156646150;
                  var10008 = (var36 | var101) & ~(var36 & var101);
                  var101 = 1217463875;
                  var36 = var10008;
                  var10008 = (var36 | var101) & ~(var36 & var101);
                  var10008 = (int)-6927678109080080709L;
                  var101 = 154442275;
                  var36 = var10008;
                  var10008 = var36 & ~var101 | ~var36 & var101;
                  var101 = -549669738;
                  var36 = var10008;
                  var10008 = var36 & ~var101 | ~var36 & var101;
                  var101 = -1828530153;
                  var36 = -1939317063;
                  var10008 = var36 & ~var101 | ~var36 & var101;
                  var101 = 527074985;
                  var36 = var10008;
                  var10008 = (var36 | var101) & (~var36 | ~var101);
                  var10008 = (int)5604521176953112493L;
                  var101 = -1884625819;
                  var36 = var10008;
                  var10008 = var36 & ~var101 | ~var36 & var101;
                  var101 = -1881463869;
                  var36 = var10008;
                  var10008 = var36 & ~var101 | ~var36 & var101;
                  var198 = (int)-5676650514445015018L;
                  var101 = -642124355;
                  var36 = var198;
                  var198 = (var36 | var101) & ~(var36 & var101);
                  var101 = 902193638;
                  var36 = var198;
                  var140.info(
                     var141.append(
                           a(
                              "\ueceaм№¦\ueccaм№Ї\uecccм№Ј\uec89м№¦\uecc7м№®\uecc4м№¦\uecddм№®\uecc6м№©\uec89м№Ў\uecc6м№µ\uec89м№¤\uecc6м№ґ\uecc4м№ў\uecddм№®\ueccaмёЅ\uec89",
                              "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                              var158,
                              var168,
                              var187,
                              var36 + var101 - 2 * (var36 & var101)
                           )
                        )
                        .append(var5)
                        .toString()
                  );
               } else {
                  this.f.add(var2);
               }

               return var4;
            } catch (Exception var9) {
               Logger var10000 = Pulse.getLOGGER();
               String var6 = var1.a();
               StringBuilder var10001 = new StringBuilder();
               int var10002 = (int)-7016648978624275518L;
               int var8 = 1103239276;
               int var7 = var10002;
               var10002 = var7 + var8 - 2 * (var7 & var8);
               var8 = -1649600409;
               var7 = var10002;
               var10002 = var7 & ~var8 | ~var7 & var8;
               var8 = 903356723;
               var7 = -1896934167;
               int var10003 = var7 & ~var8 | ~var7 & var8;
               var8 = -539314765;
               var7 = var10003;
               var8 = (var7 | var8) & (~var7 | ~var8);
               var7 = var10002;
               var10002 = var7 - ~var8 - 1;
               int var10004 = (int)-7987638744192049533L;
               var8 = -1793744609;
               var7 = var10004;
               var10004 = (var7 | var8) & (~var7 | ~var8);
               var8 = 2136784789;
               var7 = var10004;
               var10004 = (var7 | var8) & ~(var7 & var8);
               var8 = 1067278030;
               var7 = -1057573834;
               var10004 = var7 + var8 - 2 * (var7 & var8);
               var8 = -9704713;
               var7 = var10004;
               var10004 = (var7 | var8) & ~(var7 & var8);
               var8 = -1607894495;
               var7 = 575870940;
               var10004 = var7 + var8 - 2 * (var7 & var8);
               var8 = -2105905678;
               var7 = var10004;
               var10004 = var7 & ~var8 | ~var7 & var8;
               var8 = -1685734476;
               var7 = 1896385521;
               var10004 = (var7 | var8) & (~var7 | ~var8);
               var8 = -2106307501;
               var7 = var10004;
               var10004 = (var7 | var8) - (var7 & var8);
               var8 = 1290059810;
               var7 = 1564610360;
               var10004 = (var7 | var8) & ~(var7 & var8);
               var8 = 1767182841;
               var7 = var10004;
               var10004 = (var7 | var8) & ~(var7 & var8);
               var8 = -1897124818;
               var7 = -64674006;
               int var10005 = (var7 | var8) & ~(var7 & var8);
               var8 = -1565336871;
               var7 = var10005;
               var10005 = ((var7 | var8) & (~var7 | ~var8)) * ((var7 | var8) & (~var7 | ~var8));
               var8 = -2066893055;
               var7 = -1098207627;
               var10005 = var7 + var8 - 2 * (var7 & var8);
               var8 = 1760497722;
               var7 = var10005;
               var10005 = var7 & ~var8 | ~var7 & var8;
               var8 = -1140263077;
               var7 = 1349305265;
               int var10006 = (var7 | var8) & (~var7 | ~var8);
               var8 = 724480061;
               var7 = var10006;
               var10006 = (var7 & ~var8 | ~var7 & var8) * (var7 & ~var8 | ~var7 & var8);
               var8 = -1117870798;
               var7 = -1387126832;
               var10006 = var7 & ~var8 | ~var7 & var8;
               var8 = 392672281;
               var7 = var10006;
               var10006 = var7 + var8 - 2 * (var7 & var8);
               int var10007 = (int)7313312706011299885L;
               var8 = 1673783793;
               var7 = var10007;
               var10007 = (var7 | var8) & ~(var7 & var8);
               var8 = -370502760;
               var7 = var10007;
               var10007 = var7 & ~var8 | ~var7 & var8;
               var10007 = (int)9036466298158125354L;
               var8 = -532094882;
               var7 = var10007;
               var10007 = (var7 | var8) - (var7 & var8);
               var8 = 1858734421;
               var7 = var10007;
               var10000.error(
                  var10001.append(
                        a(
                           "\uecefмЁЉ\uecc0мЁ‡\uecccмЁЏ\uec89мЁџ\uecc6м©‹\uecd9мЁЉ\uecdbмЁ\uecccм©‹\uecc8мЁ…\uecc0мЁ†\uecc8мЁџ\uecc0мЁ„\uecc7м©‹\ueccfмЁ„\uecdbм©‘\uec89",
                           "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                           var10004,
                           var10005,
                           var10006,
                           var7 + var8 - 2 * (var7 & var8)
                        )
                     )
                     .append(var6)
                     .toString(),
                  var9
               );
               this.f.add(var2);
               return null;
            }
         } else {
            return null;
         }
      } else {
         return this.e.get(var2);
      }
   }

   private CosmeticModelRenderer.CompiledModel a(JsonObject var1) {
      CosmeticModelRenderer.CompiledModel var2 = new CosmeticModelRenderer.CompiledModel();
      int var10002 = (int)-4861176986748125569L;
      int var15 = -1165455370;
      int var14 = var10002;
      var10002 = (var14 | var15) & (~var14 | ~var15);
      var15 = -132484724;
      var14 = var10002;
      var10002 = (var14 | var15) - (var14 & var15);
      var15 = -125107036;
      var14 = 38057416;
      var10002 = (var14 | var15) & ~(var14 & var15);
      var15 = 666027094;
      var14 = var10002;
      var10002 = (var14 | var15) - (var14 & var15);
      var15 = 71218278;
      var14 = -507974598;
      int var10004 = (var14 | var15) & (~var14 | ~var15);
      var15 = -444180387;
      var14 = var10004;
      var10004 = (var14 | var15) & (~var14 | ~var15);
      var15 = -1248891146;
      var14 = 962970867;
      var10004 = var14 + var15 - 2 * (var14 & var15);
      var15 = -1930778111;
      var14 = var10004;
      var10004 = var14 + var15 - 2 * (var14 & var15);
      int var10003 = (int)1216914327671197696L;
      var15 = 76295650;
      var14 = var10003;
      var10003 = (var14 | var15) & (~var14 | ~var15);
      var15 = 1385374465;
      var14 = var10003;
      var10003 = var14 + var15 - 2 * (var14 & var15);
      int var10005 = (int)-2635264315450158647L;
      var15 = 1590697453;
      var14 = var10005;
      var10005 = (var14 | var15) & ~(var14 & var15);
      var15 = 1652853806;
      var14 = var10005;
      var10005 = var14 & ~var15 | ~var14 & var15;
      var10004 = (int)-3396612918047469890L;
      var15 = -781510755;
      var14 = var10004;
      var10004 = (var14 | var15) & ~(var14 & var15);
      var15 = 1109628260;
      var14 = var10004;
      var10004 = (var14 | var15) & (~var14 | ~var15);
      var15 = 1622812576;
      var14 = 583976287;
      var10005 = var14 + var15 - 2 * (var14 & var15);
      var15 = 2107385312;
      var14 = var10005;
      var10005 = (var14 | var15) & (~var14 | ~var15);
      var15 = -1154023763;
      var14 = -1029149909;
      int var10006 = var14 & ~var15 | ~var14 & var15;
      var15 = 2139824363;
      var14 = var10006;
      var15 = var14 + var15 - 2 * (var14 & var15);
      var14 = var10005;
      var10005 = var14 - ~var15 - 1;
      var10005 = (int)-8267353998029911251L;
      var15 = -1589593618;
      var14 = var10005;
      var10005 = (var14 | var15) & ~(var14 & var15);
      var15 = -331855303;
      var14 = var10005;
      var10005 = var14 & ~var15 | ~var14 & var15;
      var10006 = (int)3408278616071490178L;
      var15 = -1447677487;
      var14 = var10006;
      var10006 = (var14 | var15) - (var14 & var15);
      var15 = 983499845;
      var14 = var10006;
      var10006 = (var14 | var15) - (var14 & var15);
      var15 = -1590015901;
      var14 = 741637871;
      var10006 = (var14 | var15) & (~var14 | ~var15);
      var15 = -1926072699;
      var14 = var10006;
      if (!var1.has(
         a(
            "лЏ™вґјлЏ‘вґїлЏ™\u2d26лЏ‘вґЅлЏ–вґЎ",
            "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
            var10003,
            var10004,
            var10005,
            (var14 | var15) - (var14 & var15)
         )
      )) {
         return null;
      }

      var10002 = (int)5378105497212975648L;
      var15 = 1128980125;
      var14 = var10002;
      var10002 = var14 & ~var15 | ~var14 & var15;
      var15 = -826366787;
      var14 = var10002;
      var10002 = var14 + var15 - 2 * (var14 & var15);
      var10002 = (int)4818828063898065085L;
      var15 = -33202299;
      var14 = var10002;
      var10002 = (var14 | var15) & ~(var14 & var15);
      var15 = 945194813;
      var14 = var10002;
      var10002 = (var14 | var15) - (var14 & var15);
      var10002 = (int)5916806631847133461L;
      var15 = -1397405571;
      var14 = var10002;
      var10002 = (var14 | var15) & ~(var14 & var15);
      var15 = -614872726;
      var14 = var10002;
      var10002 = (var14 | var15) & (~var14 | ~var15);
      var15 = 1207010161;
      var14 = -1520758857;
      var10002 = (var14 | var15) & ~(var14 & var15);
      var15 = -492139305;
      var14 = var10002;
      var10002 = (var14 | var15) & (~var14 | ~var15);
      var15 = 1176820099;
      var14 = 203762381;
      var10002 = (var14 | var15) & ~(var14 & var15);
      var15 = 921612123;
      var14 = var10002;
      var10002 = (var14 | var15) - (var14 & var15);
      var10004 = (int)-7692827390013957245L;
      var15 = 1422465762;
      var14 = var10004;
      var10004 = (var14 | var15) & (~var14 | ~var15);
      var15 = 387678569;
      var14 = var10004;
      var10004 = var14 + var15 - 2 * (var14 & var15);
      var15 = 1119028369;
      var14 = -526390921;
      var10004 = var14 + var15 - 2 * (var14 & var15);
      var15 = -1574113810;
      var14 = var10004;
      var10004 = (var14 | var15) & ~(var14 & var15);
      var15 = -755245900;
      var14 = 2095211008;
      var10004 = (var14 | var15) & ~(var14 & var15);
      var15 = -1374044486;
      var14 = var10004;
      var10004 = (var14 | var15) & (~var14 | ~var15);
      var15 = -1335031186;
      var14 = 1599500407;
      var10003 = var14 + var15 - 2 * (var14 & var15);
      var15 = -1748458246;
      var14 = var10003;
      var10003 = (var14 | var15) & ~(var14 & var15);
      var15 = -308762485;
      var14 = -209029858;
      var10005 = var14 & ~var15 | ~var14 & var15;
      var15 = 504552857;
      var14 = var10005;
      var10005 = (var14 | var15) & ~(var14 & var15);
      var15 = 1898532974;
      var14 = -1638086772;
      var10005 = (var14 | var15) & ~(var14 & var15);
      var15 = -277506070;
      var14 = var10005;
      var10005 = var14 & ~var15 | ~var14 & var15;
      var10005 = (int)2745076723313953870L;
      var15 = -313209540;
      var14 = var10005;
      var10005 = var14 + var15 - 2 * (var14 & var15);
      var15 = -1291419277;
      var14 = var10005;
      var10005 = (var14 | var15) & ~(var14 & var15);
      var15 = -807524743;
      var14 = 622947745;
      var10004 = (var14 | var15) & (~var14 | ~var15);
      var15 = 1977651588;
      var14 = var10004;
      var10004 = var14 & ~var15 | ~var14 & var15;
      var10005 = (int)-6401733084535803878L;
      var15 = -23184093;
      var14 = var10005;
      var10005 = var14 & ~var15 | ~var14 & var15;
      var15 = 165001467;
      var14 = var10005;
      var10005 = (var14 | var15) & ~(var14 & var15);
      var10005 = (int)4615433854063821322L;
      var15 = 1604978129;
      var14 = var10005;
      var10005 = (var14 | var15) & ~(var14 & var15);
      var15 = -1495617946;
      var14 = var10005;
      var10005 = (var14 | var15) & (~var14 | ~var15);
      String var969 = "il";
      var15 = 1500903897;
      var14 = -1492452880;
      var10006 = var14 & ~var15 | ~var14 & var15;
      var15 = -30175689;
      var14 = var10006;
      JsonObject var3 = var1.getAsJsonObject(
         a(
            "лЏ™\ue32bлЏ‘\ue328лЏ™\ue331лЏ‘\ue32aлЏ–\ue336",
            "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
            var10003,
            var10004,
            var10005,
            (var14 | var15) & (~var14 | ~var15)
         )
      );
      Iterator var4 = var3.entrySet().iterator();
      if (var4.hasNext()) {
         JsonObject var7;
         int var692;
         label54: {
            java.util.Map.Entry var5 = (java.util.Map.Entry)var4.next();
            String var6 = (String)var5.getKey();
            var7 = ((JsonElement)var5.getValue()).getAsJsonObject();
            var2.a = var6;
            var15 = 1742794022;
            var14 = -1900553460;
            var10003 = var14 & ~var15 | ~var14 & var15;
            var15 = -380160478;
            var14 = var10003;
            var10003 = (var14 | var15) & (~var14 | ~var15);
            var10003 = (int)7871632996636349896L;
            var15 = -1279446587;
            var14 = var10003;
            var10003 = var14 + var15 - 2 * (var14 & var15);
            var15 = -2055347171;
            var14 = var10003;
            var10003 = (var14 | var15) & (~var14 | ~var15);
            var15 = 1764476839;
            var14 = -1502102237;
            var10003 = (var14 | var15) & (~var14 | ~var15);
            var15 = -2074430416;
            var14 = var10003;
            var10003 = (var14 | var15) & ~(var14 & var15);
            var10004 = (int)6462776890034758962L;
            var15 = 629080246;
            var14 = var10004;
            var10004 = var14 + var15 - 2 * (var14 & var15);
            var15 = 1268031168;
            var14 = var10004;
            var10004 = (var14 + var15 - 2 * (var14 & var15)) * (var14 + var15 - 2 * (var14 & var15));
            var10004 = (int)-2672678651658442499L;
            var15 = -593515657;
            var14 = var10004;
            var10004 = var14 & ~var15 | ~var14 & var15;
            var15 = -447963799;
            var14 = var10004;
            var10004 = (var14 | var15) & (~var14 | ~var15);
            var10005 = (int)-4123234675118239131L;
            var15 = -2108155537;
            var14 = var10005;
            var10005 = var14 + var15 - 2 * (var14 & var15);
            var15 = 1726664466;
            var14 = var10005;
            var10005 = ((var14 | var15) - (var14 & var15)) * ((var14 | var15) - (var14 & var15));
            var10005 = (int)5948556218288800556L;
            var15 = -1544611695;
            var14 = var10005;
            var10005 = var14 + var15 - 2 * (var14 & var15);
            var15 = 561259864;
            var14 = var10005;
            var10005 = (var14 | var15) & ~(var14 & var15);
            var15 = -1083506699;
            var14 = -2052092108;
            int var10007 = (var14 | var15) & (~var14 | ~var15);
            var15 = 986017997;
            var14 = var10007;
            var10007 = var14 + var15 - 2 * (var14 & var15);
            var10007 = (int)5408382033314430578L;
            var15 = 643393941;
            var14 = var10007;
            var10007 = (var14 | var15) - (var14 & var15);
            var15 = -686368790;
            var14 = var10007;
            var10007 = (var14 | var15) - (var14 & var15);
            var10006 = (int)3040741785780057130L;
            var15 = -1037057417;
            var14 = var10006;
            var10006 = (var14 | var15) - (var14 & var15);
            var15 = -394865876;
            var14 = var10006;
            var10006 = var14 + var15 - 2 * (var14 & var15);
            var10007 = (int)1686284968731499110L;
            var15 = 530196238;
            var14 = var10007;
            var10007 = var14 + var15 - 2 * (var14 & var15);
            var15 = 2131664566;
            var14 = var10007;
            var10007 = var14 & ~var15 | ~var14 & var15;
            var15 = 824758079;
            var14 = -1674136077;
            var10007 = var14 + var15 - 2 * (var14 & var15);
            var15 = -1390213013;
            var14 = var10007;
            if (var7.has(
               a(
                  "лЏ”бѕ“лЏ—бѕЊ",
                  "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                  var10004,
                  var10005,
                  var10006,
                  (var14 | var15) - (var14 & var15)
               )
            )) {
               var15 = -593295571;
               var14 = 1101588720;
               var10002 = (var14 | var15) & (~var14 | ~var15);
               var15 = -1995757842;
               var14 = var10002;
               var10002 = var14 & ~var15 | ~var14 & var15;
               var15 = 97732319;
               var14 = -367627801;
               var10004 = var14 + var15 - 2 * (var14 & var15);
               var15 = -272287957;
               var14 = var10004;
               var10004 = var14 & ~var15 | ~var14 & var15;
               var15 = 1381720253;
               var14 = 1613834166;
               var10004 = var14 & ~var15 | ~var14 & var15;
               var15 = 845825801;
               var14 = var10004;
               var10004 = (var14 | var15) & (~var14 | ~var15);
               var15 = 981687892;
               var14 = -1875432634;
               var10004 = var14 & ~var15 | ~var14 & var15;
               var15 = -1431014114;
               var14 = var10004;
               var10004 = var14 + var15 - 2 * (var14 & var15);
               var10004 = (int)-7913783302062986277L;
               var15 = 1751565998;
               var14 = var10004;
               var10004 = (var14 | var15) & ~(var14 & var15);
               var15 = -1128378097;
               var14 = var10004;
               var10004 = (var14 | var15) & (~var14 | ~var15);
               var10005 = (int)-4706310212557182154L;
               var15 = -1784163009;
               var14 = var10005;
               var10005 = (var14 | var15) & ~(var14 & var15);
               var15 = -1945899491;
               var14 = var10005;
               var15 = var14 + var15 - 2 * (var14 & var15);
               var14 = var10004;
               var10004 = 2 * (var14 | var15) - (var14 ^ var15);
               var10004 = (int)490859523263160590L;
               var15 = 535558145;
               var14 = var10004;
               var10004 = (var14 | var15) & (~var14 | ~var15);
               var15 = -642791444;
               var14 = var10004;
               var10004 = var14 + var15 - 2 * (var14 & var15);
               var15 = -555359340;
               var14 = -1713436392;
               var10006 = (var14 | var15) & ~(var14 & var15);
               var15 = 1195043484;
               var14 = var10006;
               var10006 = (var14 | var15) - (var14 & var15);
               var15 = 363973758;
               var14 = 1591528007;
               var10006 = (var14 | var15) & (~var14 | ~var15);
               var15 = 1265436214;
               var14 = var10006;
               var10006 = (var14 | var15) & ~(var14 & var15);
               var10005 = (int)7268137379971572641L;
               var15 = -124804558;
               var14 = var10005;
               var10005 = var14 + var15 - 2 * (var14 & var15);
               var15 = 813102706;
               var14 = var10005;
               var10005 = var14 + var15 - 2 * (var14 & var15);
               String var978 = "im";
               var10006 = (int)-1105200415022883937L;
               var15 = -1103698267;
               var14 = var10006;
               var10006 = (var14 | var15) & (~var14 | ~var15);
               var15 = -1104855439;
               var14 = var10006;
               var10006 = (var14 | var15) - (var14 & var15);
               int var10008 = (int)-241105874082971722L;
               var15 = -1505302291;
               var14 = var10008;
               var10008 = (var14 | var15) & (~var14 | ~var15);
               var15 = -2143234215;
               var14 = var10008;
               var10008 = var14 + var15 - 2 * (var14 & var15);
               var10008 = (int)8698754414317336052L;
               var15 = 1371202264;
               var14 = var10008;
               var10008 = (var14 | var15) - (var14 & var15);
               var15 = 335812390;
               var14 = var10008;
               var10008 = var14 & ~var15 | ~var14 & var15;
               var10007 = (int)1130608983229611744L;
               var15 = -734475562;
               var14 = var10007;
               var10007 = var14 & ~var15 | ~var14 & var15;
               var15 = -1656135787;
               var14 = var10007;
               if (var7.get(
                     a(
                        "лЏ”мљ—лЏ—мљ€",
                        "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                        var10004,
                        var10005,
                        var10006,
                        (var14 | var15) & ~(var14 & var15)
                     )
                  )
                  .getAsBoolean()) {
                  var692 = (int)-131797817450406969L;
                  var15 = 1758391776;
                  var14 = var692;
                  var692 = (var14 | var15) & (~var14 | ~var15);
                  var15 = 1014806054;
                  var14 = var692;
                  var692 = (var14 | var15) - (var14 & var15);
                  break label54;
               }
            }

            var15 = -388208353;
            var14 = 403625966;
            var692 = (var14 | var15) & ~(var14 & var15);
            var15 = -254624015;
            var14 = var692;
            var692 = var14 & ~var15 | ~var14 & var15;
         }

         var2.b = pulse.Bool.from(var692);
         var15 = -183836697;
         var14 = -1757197109;
         var10002 = (var14 | var15) - (var14 & var15);
         var15 = -936449054;
         var14 = var10002;
         var10002 = (var14 | var15) & (~var14 | ~var15);
         var10003 = (int)8319315513680382798L;
         var15 = -394738385;
         var14 = var10003;
         var10003 = (var14 | var15) - (var14 & var15);
         var15 = 506582770;
         var14 = var10003;
         var10003 = var14 + var15 - 2 * (var14 & var15);
         var10004 = (int)2952875976592898040L;
         var15 = -1176456428;
         var14 = var10004;
         var10004 = (var14 | var15) - (var14 & var15);
         var15 = 1200714242;
         var14 = var10004;
         var10004 = (var14 | var15) & ~(var14 & var15);
         var15 = -265108520;
         var14 = 1233171695;
         var10005 = var14 & ~var15 | ~var14 & var15;
         var15 = -1385400822;
         var14 = var10005;
         var15 = var14 & ~var15 | ~var14 & var15;
         var14 = var10004;
         var10004 = var14 - ~var15 - 1;
         var15 = 1366662243;
         var14 = -679683046;
         var10004 = (var14 | var15) - (var14 & var15);
         var15 = -17129830;
         var14 = var10004;
         var10004 = (var14 | var15) - (var14 & var15);
         var10005 = (int)-681084672904267368L;
         var15 = 1155536987;
         var14 = var10005;
         var10005 = (var14 | var15) & (~var14 | ~var15);
         var15 = -1112447292;
         var14 = var10005;
         var10005 = var14 & ~var15 | ~var14 & var15;
         var15 = 331475260;
         var14 = -1951069200;
         var10005 = (var14 | var15) & (~var14 | ~var15);
         var15 = 124631157;
         var14 = var10005;
         var10005 = (var14 | var15) & ~(var14 & var15);
         var10006 = (int)831818925394651264L;
         var15 = 1991813393;
         var14 = var10006;
         var10006 = (var14 | var15) & (~var14 | ~var15);
         var15 = -156346498;
         var14 = var10006;
         var10006 = (var14 + var15 - 2 * (var14 & var15)) * (var14 + var15 - 2 * (var14 & var15));
         var15 = -766030338;
         var14 = 2078651479;
         var10006 = (var14 | var15) & ~(var14 & var15);
         var15 = -1452520704;
         var14 = var10006;
         var10006 = (var14 | var15) & (~var14 | ~var15);
         var15 = -1354877677;
         var14 = -1814525074;
         int var1044 = var14 & ~var15 | ~var14 & var15;
         var15 = 1362207605;
         var14 = var1044;
         var1044 = ((var14 | var15) - (var14 & var15)) * ((var14 | var15) - (var14 & var15));
         var15 = 2052453424;
         var14 = 1769303503;
         var1044 = (var14 | var15) & (~var14 | ~var15);
         var15 = 325490564;
         var14 = var1044;
         float var695;
         if (!var7.has(
            a(
               "лЏ™жЌЋлЏ‘жЌЌлЏ™жЌ”лЏ‘жЌЏлЏ–жЌїлЏ”жЌ…лЏ–жЌ‡лЏЊжЌ€",
               "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
               var10004,
               var10005,
               var10006,
               var14 & ~var15 | ~var14 & var15
            )
         )) {
            var695 = 1.0F;
         } else {
            String var726 = "in";
            String var751 = "io";
            var15 = 1173303765;
            var14 = -849750576;
            var10004 = (var14 | var15) - (var14 & var15);
            var15 = -1400312753;
            var14 = var10004;
            var10004 = ((var14 | var15) & (~var14 | ~var15)) * ((var14 | var15) & (~var14 | ~var15));
            var15 = 1685047439;
            var14 = -218393210;
            var10004 = var14 & ~var15 | ~var14 & var15;
            var15 = -295205910;
            var14 = var10004;
            var10004 = (var14 | var15) & (~var14 | ~var15);
            var10005 = (int)-1212588615494570860L;
            var15 = 541761061;
            var14 = var10005;
            var10005 = (var14 | var15) & (~var14 | ~var15);
            var15 = 854019752;
            var14 = var10005;
            var10005 = ((var14 | var15) & ~(var14 & var15)) * ((var14 | var15) & ~(var14 & var15));
            var10005 = (int)7335811050177012094L;
            var15 = 1574129745;
            var14 = var10005;
            var10005 = (var14 | var15) & ~(var14 & var15);
            var15 = 1242294963;
            var14 = var10005;
            var10005 = (var14 | var15) - (var14 & var15);
            var15 = -1595853927;
            var14 = -215048849;
            var10006 = (var14 | var15) & (~var14 | ~var15);
            var15 = -33661683;
            var14 = var10006;
            var10006 = (var14 | var15) & (~var14 | ~var15);
            var1044 = (int)7838250906929455762L;
            var15 = -1250985474;
            var14 = var1044;
            var1044 = (var14 | var15) - (var14 & var15);
            var15 = -1172698578;
            var14 = var1044;
            var15 = (var14 | var15) - (var14 & var15);
            var14 = var10006;
            var10006 = var14 - ~var15 - 1;
            var15 = 2024268816;
            var14 = -1496080744;
            var10006 = (var14 | var15) & (~var14 | ~var15);
            var15 = -963341455;
            var14 = var10006;
            var10006 = (var14 | var15) & (~var14 | ~var15);
            var1044 = (int)8771606580660798583L;
            var15 = 1036644451;
            var14 = var1044;
            var1044 = (var14 | var15) & ~(var14 & var15);
            var15 = 534133313;
            var14 = var1044;
            var1044 = (var14 | var15) - (var14 & var15);
            var15 = 1840737033;
            var14 = 2083273856;
            var1044 = var14 & ~var15 | ~var14 & var15;
            var15 = 297310423;
            var14 = var1044;
            var695 = var7.get(
                  a(
                     "лЏ™к‰«лЏ‘к‰ЁлЏ™к‰±лЏ‘к‰ЄлЏ–к‰љлЏ”к‰ лЏ–к‰ўлЏЊк‰­",
                     "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                     var10004,
                     var10005,
                     var10006,
                     var14 & ~var15 | ~var14 & var15
                  )
               )
               .getAsFloat();
         }

         var2.c = var695;
         var692 = (int)-4685028752671132439L;
         var15 = -2032920735;
         var14 = var692;
         var692 = (var14 | var15) & ~(var14 & var15);
         var15 = 548366473;
         var14 = var692;
         var692 = var14 & ~var15 | ~var14 & var15;
         var10002 = (int)-3127803732058647025L;
         var15 = -1102408958;
         var14 = var10002;
         var10002 = var14 & ~var15 | ~var14 & var15;
         var15 = -76587335;
         var14 = var10002;
         var15 = var14 & ~var15 | ~var14 & var15;
         var14 = var692;
         var692 = 2 * (var14 | var15) - (var14 ^ var15);
         String var729 = "ip";
         String var752 = "iq";
         var10003 = (int)-1493958166361515229L;
         var15 = 1025766672;
         var14 = var10003;
         var10003 = (var14 | var15) & ~(var14 & var15);
         var15 = -848662320;
         var14 = var10003;
         var10003 = var14 + var15 - 2 * (var14 & var15);
         String var849 = "ir";
         var10004 = (int)8510213139189104528L;
         var15 = 856627020;
         var14 = var10004;
         var10004 = var14 & ~var15 | ~var14 & var15;
         var15 = 2044638920;
         var14 = var10004;
         var10004 = (var14 | var15) - (var14 & var15);
         String var930 = "is";
         var15 = 537197576;
         var14 = -1167192153;
         var10005 = var14 + var15 - 2 * (var14 & var15);
         var15 = -1887469194;
         var14 = var10005;
         var10005 = var14 & ~var15 | ~var14 & var15;
         var1044 = (int)1788056350004162404L;
         var15 = -1642178950;
         var14 = var1044;
         var1044 = (var14 | var15) & ~(var14 & var15);
         var15 = 1115650321;
         var14 = var1044;
         var1044 = var14 + var15 - 2 * (var14 & var15);
         var15 = -1279584799;
         var14 = 439754560;
         var1044 = (var14 | var15) - (var14 & var15);
         var15 = -1450374482;
         var14 = var1044;
         var1044 = (var14 | var15) - (var14 & var15);
         var10006 = (int)-6324940925512986201L;
         var15 = 1621193321;
         var14 = var10006;
         var10006 = (var14 | var15) & ~(var14 & var15);
         var15 = 1573794328;
         var14 = var10006;
         if (var7.has(
            a(
               "лЏљпіўлЏ–піЁлЏ‹",
               "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
               var10003,
               var10004,
               var10005,
               var14 + var15 - 2 * (var14 & var15)
            )
         )) {
            String var700 = "it";
            var10003 = (int)6365538993076112653L;
            var15 = -1817039634;
            var14 = var10003;
            var10003 = (var14 | var15) & (~var14 | ~var15);
            var15 = 1702961651;
            var14 = var10003;
            var10003 = var14 & ~var15 | ~var14 & var15;
            var10003 = (int)-1260664259973797068L;
            var15 = 485547368;
            var14 = var10003;
            var10003 = (var14 | var15) & ~(var14 & var15);
            var15 = -137628074;
            var14 = var10003;
            var10003 = var14 + var15 - 2 * (var14 & var15);
            var10003 = (int)-1686333849614615469L;
            var15 = 1432936895;
            var14 = var10003;
            var10003 = (var14 | var15) & ~(var14 & var15);
            var15 = 879238635;
            var14 = var10003;
            var10003 = var14 + var15 - 2 * (var14 & var15);
            var15 = 1136944672;
            var14 = -602054589;
            var10003 = (var14 | var15) - (var14 & var15);
            var15 = -1157790029;
            var14 = var10003;
            var10003 = var14 & ~var15 | ~var14 & var15;
            var15 = 1078819628;
            var14 = 247160519;
            var10004 = var14 + var15 - 2 * (var14 & var15);
            var15 = 517333971;
            var14 = var10004;
            var15 = (var14 | var15) & (~var14 | ~var15);
            var14 = var10003;
            var10003 = 2 * (var14 | var15) - (var14 ^ var15);
            var10003 = (int)-4861844541149211283L;
            var15 = -870396063;
            var14 = var10003;
            var10003 = (var14 | var15) & ~(var14 & var15);
            var15 = -1756625681;
            var14 = var10003;
            var10003 = var14 + var15 - 2 * (var14 & var15);
            String var854 = "iu";
            var10004 = (int)-7478076036051845819L;
            var15 = -468578368;
            var14 = var10004;
            var10004 = var14 + var15 - 2 * (var14 & var15);
            var15 = 1091560351;
            var14 = var10004;
            var10004 = (var14 | var15) - (var14 & var15);
            var15 = 819833781;
            var14 = -1932602178;
            var10006 = var14 & ~var15 | ~var14 & var15;
            var15 = -1139572983;
            var14 = var10006;
            var10006 = var14 + var15 - 2 * (var14 & var15);
            var10006 = (int)4303969953041072472L;
            var15 = -1311927637;
            var14 = var10006;
            var10006 = var14 + var15 - 2 * (var14 & var15);
            var15 = -549825567;
            var14 = var10006;
            var10006 = var14 + var15 - 2 * (var14 & var15);
            var15 = -327586902;
            var14 = -265165561;
            var10006 = var14 + var15 - 2 * (var14 & var15);
            var15 = 474517180;
            var14 = var10006;
            var10006 = var14 & ~var15 | ~var14 & var15;
            var15 = 542886626;
            var14 = -360825602;
            var10005 = (var14 | var15) & (~var14 | ~var15);
            var15 = -797611279;
            var14 = var10005;
            var10005 = (var14 | var15) & (~var14 | ~var15);
            var10006 = (int)-1766447999921161265L;
            var15 = 1559726849;
            var14 = var10006;
            var10006 = var14 & ~var15 | ~var14 & var15;
            var15 = 1489170798;
            var14 = var10006;
            var10006 = (var14 | var15) & (~var14 | ~var15);
            var1044 = (int)748681633112172144L;
            var15 = 1994972495;
            var14 = var1044;
            var1044 = var14 & ~var15 | ~var14 & var15;
            var15 = -1985545521;
            var14 = var1044;
            var15 = var14 + var15 - 2 * (var14 & var15);
            var14 = var10006;
            var10006 = 2 * (var14 | var15) - (var14 ^ var15);
            var15 = -1367644201;
            var14 = -2050044559;
            var10006 = (var14 | var15) - (var14 & var15);
            var15 = 733195898;
            var14 = var10006;
            JsonObject var8 = var7.getAsJsonObject(
               a(
                  "лЏљж§ЁлЏ–ж§ўлЏ‹",
                  "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                  var10003,
                  var10004,
                  var10005,
                  var14 + var15 - 2 * (var14 & var15)
               )
            );

            for (java.util.Map.Entry var10 : var8.entrySet()) {
               String var11 = (String)var10.getKey();
               JsonObject var12 = ((JsonElement)var10.getValue()).getAsJsonObject();
               CosmeticModelRenderer.PartPoseMap var13 = new CosmeticModelRenderer.PartPoseMap();
               var692 = (int)-458941959742096558L;
               var15 = 1145553392;
               var14 = var692;
               var692 = var14 & ~var15 | ~var14 & var15;
               var15 = 1595721545;
               var14 = var692;
               var692 = (var14 | var15) - (var14 & var15);
               var15 = -1428995221;
               var14 = -922172455;
               var10002 = (var14 | var15) & ~(var14 & var15);
               var15 = -654375000;
               var14 = var10002;
               var10002 = (var14 | var15) & (~var14 | ~var15);
               var10003 = (int)5017980922293005754L;
               var15 = 1182303075;
               var14 = var10003;
               var10003 = (var14 | var15) - (var14 & var15);
               var15 = 761092832;
               var14 = var10003;
               var10003 = var14 & ~var15 | ~var14 & var15;
               var10003 = (int)7381298537429388178L;
               var15 = 501599147;
               var14 = var10003;
               var10003 = (var14 | var15) & ~(var14 & var15);
               var15 = 494984922;
               var14 = var10003;
               var10003 = var14 + var15 - 2 * (var14 & var15);
               String var858 = "iv";
               var10004 = (int)2901528298227598455L;
               var15 = -279340465;
               var14 = var10004;
               var10004 = var14 & ~var15 | ~var14 & var15;
               var15 = 835423799;
               var14 = var10004;
               var10004 = (var14 | var15) - (var14 & var15);
               var10005 = (int)-8578049031207424260L;
               var15 = 544161043;
               var14 = var10005;
               var10005 = (var14 | var15) - (var14 & var15);
               var15 = 1853543633;
               var14 = var10005;
               var10005 = ((var14 | var15) & ~(var14 & var15)) * ((var14 | var15) & ~(var14 & var15));
               var15 = 1913909196;
               var14 = 1456324510;
               var10005 = var14 + var15 - 2 * (var14 & var15);
               var15 = 797334353;
               var14 = var10005;
               var10005 = (var14 | var15) - (var14 & var15);
               var15 = 1056629491;
               var14 = -778047206;
               var10006 = (var14 | var15) & (~var14 | ~var15);
               var15 = 808500016;
               var14 = var10006;
               var10006 = ((var14 | var15) - (var14 & var15)) * ((var14 | var15) - (var14 & var15));
               var15 = 170957864;
               var14 = 1240361730;
               var10006 = (var14 | var15) & ~(var14 & var15);
               var15 = 1139193083;
               var14 = var10006;
               if (var12.has(
                  a(
                     "лЏЉв»ҐлЏЊв»«лЏЊв»ЈлЏ—в»¤",
                     "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                     var10003,
                     var10004,
                     var10005,
                     var14 + var15 - 2 * (var14 & var15)
                  )
               )) {
                  String var777 = "iw";
                  var10004 = (int)8133321196095134700L;
                  var15 = -1917895309;
                  var14 = var10004;
                  var10004 = (var14 | var15) - (var14 & var15);
                  var15 = -346535992;
                  var14 = var10004;
                  var10004 = (var14 | var15) & ~(var14 & var15);
                  var15 = -129744133;
                  var14 = -1738430003;
                  var10005 = (var14 | var15) & ~(var14 & var15);
                  var15 = 1127206705;
                  var14 = var10005;
                  var15 = (var14 | var15) - (var14 & var15);
                  var14 = var10004;
                  var10004 = (var14 | var15) + (var14 & var15);
                  var10005 = (int)5887239407217653332L;
                  var15 = -679560818;
                  var14 = var10005;
                  var10005 = var14 & ~var15 | ~var14 & var15;
                  var15 = -615127157;
                  var14 = var10005;
                  var10005 = var14 + var15 - 2 * (var14 & var15);
                  var15 = 600977545;
                  var14 = -982888981;
                  var10005 = var14 & ~var15 | ~var14 & var15;
                  var15 = -1639210111;
                  var14 = var10005;
                  var10005 = var14 + var15 - 2 * (var14 & var15);
                  var10006 = (int)1484633475444985993L;
                  var15 = -1540979286;
                  var14 = var10006;
                  var10006 = var14 + var15 - 2 * (var14 & var15);
                  var15 = 374315411;
                  var14 = var10006;
                  var10006 = (var14 | var15) & (~var14 | ~var15);
                  var15 = -548074226;
                  var14 = 444688241;
                  var1044 = var14 + var15 - 2 * (var14 & var15);
                  var15 = 606207448;
                  var14 = var1044;
                  var15 = (var14 | var15) & (~var14 | ~var15);
                  var14 = var10006;
                  var10006 = (var14 ^ var15) + 2 * (var14 & var15);
                  var10006 = (int)5301307216203187647L;
                  var15 = -1088513701;
                  var14 = var10006;
                  var10006 = (var14 | var15) & (~var14 | ~var15);
                  var15 = 2059659730;
                  var14 = var10006;
                  var10006 = var14 & ~var15 | ~var14 & var15;
                  var15 = 670979649;
                  var14 = 1207359069;
                  var1044 = (var14 | var15) & ~(var14 & var15);
                  var15 = -950355704;
                  var14 = var1044;
                  var1044 = ((var14 | var15) & ~(var14 & var15)) * ((var14 | var15) & ~(var14 & var15));
                  var15 = 551329120;
                  var14 = -463561663;
                  var1044 = var14 + var15 - 2 * (var14 & var15);
                  var15 = -745581486;
                  var14 = var1044;
                  var1044 = var14 & ~var15 | ~var14 & var15;
                  var15 = -1881633783;
                  var14 = -2100947101;
                  int var1085 = var14 & ~var15 | ~var14 & var15;
                  var15 = -1439952742;
                  var14 = var1085;
                  var1085 = (var14 | var15) - (var14 & var15);
                  var15 = -292814495;
                  var14 = -1180962616;
                  var1085 = (var14 | var15) & ~(var14 & var15);
                  var15 = 1461606737;
                  var14 = var1085;
                  var13.a = this.a(
                     var12.get(
                        a(
                           "лЏЉм—ЊлЏЊм—‚лЏЊм—ЉлЏ—м—Ќ",
                           "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                           var10005,
                           var10006,
                           var1044,
                           (var14 | var15) & ~(var14 & var15)
                        )
                     )
                  );
               }

               var15 = 508954253;
               var14 = -415694722;
               var10002 = (var14 | var15) & ~(var14 & var15);
               var15 = -110164255;
               var14 = var10002;
               var10002 = (var14 | var15) & (~var14 | ~var15);
               var10002 = (int)-3446428895771787044L;
               var15 = -481071184;
               var14 = var10002;
               var10002 = (var14 | var15) & (~var14 | ~var15);
               var15 = 11726697;
               var14 = var10002;
               var10002 = (var14 | var15) - (var14 & var15);
               var15 = -257678386;
               var14 = -939723085;
               var10003 = (var14 | var15) & (~var14 | ~var15);
               var15 = 928568688;
               var14 = var10003;
               var10003 = (var14 | var15) & (~var14 | ~var15);
               var15 = -1276966678;
               var14 = -1679871978;
               var10003 = (var14 | var15) & ~(var14 & var15);
               var15 = 675035372;
               var14 = var10003;
               var10003 = var14 + var15 - 2 * (var14 & var15);
               var10003 = (int)-6294603067150133802L;
               var15 = -544882759;
               var14 = var10003;
               var10003 = (var14 | var15) - (var14 & var15);
               var15 = -1947522435;
               var14 = var10003;
               var10003 = var14 + var15 - 2 * (var14 & var15);
               var15 = 700069618;
               var14 = 1286376012;
               var10003 = (var14 | var15) - (var14 & var15);
               var15 = -1801823588;
               var14 = var10003;
               var10003 = (var14 | var15) & (~var14 | ~var15);
               var15 = 991855666;
               var14 = -1435759560;
               var10003 = (var14 | var15) & ~(var14 & var15);
               var15 = -377399575;
               var14 = var10003;
               var10003 = (var14 | var15) & ~(var14 & var15);
               String var866 = "ix";
               var15 = 208211790;
               var14 = -723981285;
               var10004 = (var14 | var15) & (~var14 | ~var15);
               var15 = 1200734636;
               var14 = var10004;
               var10004 = var14 & ~var15 | ~var14 & var15;
               var10005 = (int)-4539221829532170903L;
               var15 = -564418112;
               var14 = var10005;
               var10005 = var14 & ~var15 | ~var14 & var15;
               var15 = -1532450633;
               var14 = var10005;
               var10005 = (var14 | var15) - (var14 & var15);
               var10006 = (int)6460754769137691381L;
               var15 = 955321418;
               var14 = var10006;
               var10006 = var14 + var15 - 2 * (var14 & var15);
               var15 = -417756855;
               var14 = var10006;
               var15 = var14 & ~var15 | ~var14 & var15;
               var14 = var10005;
               var10005 = var14 - ~var15 - 1;
               var10005 = (int)-6427920029367494392L;
               var15 = -849962587;
               var14 = var10005;
               var10005 = (var14 | var15) & (~var14 | ~var15);
               var15 = -656543240;
               var14 = var10005;
               var10005 = var14 + var15 - 2 * (var14 & var15);
               String var1018 = "iy";
               var15 = -273585053;
               var14 = -1395980140;
               var10006 = (var14 | var15) & ~(var14 & var15);
               var15 = 1127658060;
               var14 = var10006;
               if (var12.has(
                  a(
                     "лЏ€л®ЏлЏ‹л®‰лЏЊл®‰лЏ—л®Ћ",
                     "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                     var10003,
                     var10004,
                     var10005,
                     (var14 | var15) & ~(var14 & var15)
                  )
               )) {
                  var10004 = (int)-6762512168032472760L;
                  var15 = -32196915;
                  var14 = var10004;
                  var10004 = (var14 | var15) - (var14 & var15);
                  var15 = -851556469;
                  var14 = var10004;
                  var10004 = (var14 | var15) & (~var14 | ~var15);
                  var10004 = (int)-2711821006954104618L;
                  var15 = -469497212;
                  var14 = var10004;
                  var10004 = var14 & ~var15 | ~var14 & var15;
                  var15 = -1643449770;
                  var14 = var10004;
                  var10004 = var14 + var15 - 2 * (var14 & var15);
                  var15 = 1075535445;
                  var14 = 88657427;
                  var10004 = (var14 | var15) - (var14 & var15);
                  var15 = 1975266449;
                  var14 = var10004;
                  var10004 = (var14 | var15) - (var14 & var15);
                  var15 = -765539733;
                  var14 = -2085535601;
                  var10005 = var14 + var15 - 2 * (var14 & var15);
                  var15 = 1942113477;
                  var14 = var10005;
                  var10005 = var14 + var15 - 2 * (var14 & var15);
                  var10006 = (int)-849185760529479916L;
                  var15 = -1041441009;
                  var14 = var10006;
                  var10006 = (var14 | var15) & ~(var14 & var15);
                  var15 = -1477145866;
                  var14 = var10006;
                  var15 = (var14 | var15) & ~(var14 & var15);
                  var14 = var10005;
                  var10005 = (var14 | var15) + (var14 & var15);
                  var10005 = (int)3472153651002420314L;
                  var15 = 1131194955;
                  var14 = var10005;
                  var10005 = (var14 | var15) - (var14 & var15);
                  var15 = 1443649778;
                  var14 = var10005;
                  var10005 = (var14 | var15) - (var14 & var15);
                  var15 = -1826862427;
                  var14 = -1713955301;
                  var1044 = var14 & ~var15 | ~var14 & var15;
                  var15 = 181097652;
                  var14 = var1044;
                  var1044 = (var14 | var15) - (var14 & var15);
                  var15 = -192011115;
                  var14 = -862823639;
                  var1044 = (var14 | var15) & (~var14 | ~var15);
                  var15 = 941391789;
                  var14 = var1044;
                  var1044 = var14 & ~var15 | ~var14 & var15;
                  var10006 = (int)6710675175003402860L;
                  var15 = -189275594;
                  var14 = var10006;
                  var10006 = (var14 | var15) & ~(var14 & var15);
                  var15 = 851239573;
                  var14 = var10006;
                  var10006 = var14 & ~var15 | ~var14 & var15;
                  var1044 = (int)-3644190407725792058L;
                  var15 = -2085062565;
                  var14 = var1044;
                  var1044 = (var14 | var15) & (~var14 | ~var15);
                  var15 = 846417399;
                  var14 = var1044;
                  var1044 = (var14 | var15) & ~(var14 & var15);
                  var15 = -1873934168;
                  var14 = -337906726;
                  int var1088 = (var14 | var15) & ~(var14 & var15);
                  var15 = 975936517;
                  var14 = var1088;
                  var15 = (var14 | var15) - (var14 & var15);
                  var14 = var1044;
                  var1044 = (var14 ^ var15) + 2 * (var14 & var15);
                  var1044 = (int)6692503843665783724L;
                  var15 = -365062005;
                  var14 = var1044;
                  var1044 = (var14 | var15) & ~(var14 & var15);
                  var15 = 1499738087;
                  var14 = var1044;
                  var1044 = (var14 | var15) & ~(var14 & var15);
                  var15 = -111042014;
                  var14 = -1165172742;
                  var1088 = (var14 | var15) & ~(var14 & var15);
                  var15 = 1087910310;
                  var14 = var1088;
                  var1088 = ((var14 | var15) & ~(var14 & var15)) * ((var14 | var15) & ~(var14 & var15));
                  var1088 = (int)-1666117556309812761L;
                  var15 = 884141380;
                  var14 = var1088;
                  var1088 = (var14 | var15) & ~(var14 & var15);
                  var15 = -1024617934;
                  var14 = var1088;
                  var13.b = this.a(
                     var12.get(
                        a(
                           "лЏ€и®ҐлЏ‹и®ЈлЏЊи®ЈлЏ—и®¤",
                           "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                           var10005,
                           var10006,
                           var1044,
                           (var14 | var15) & (~var14 | ~var15)
                        )
                     )
                  );
               }

               String var704 = "iz";
               var10003 = (int)6829180500530981259L;
               var15 = 100407731;
               var14 = var10003;
               var10003 = (var14 | var15) & ~(var14 & var15);
               var15 = -1798279128;
               var14 = var10003;
               var10003 = (var14 | var15) & ~(var14 & var15);
               var15 = -160279712;
               var14 = -1283824660;
               var10003 = (var14 | var15) - (var14 & var15);
               var15 = 1158164610;
               var14 = var10003;
               var10003 = (var14 | var15) & ~(var14 & var15);
               var10003 = (int)-1029094840495856452L;
               var15 = -146940176;
               var14 = var10003;
               var10003 = (var14 | var15) & (~var14 | ~var15);
               var15 = 523467332;
               var14 = var10003;
               var10003 = (var14 | var15) & (~var14 | ~var15);
               var15 = 1808306544;
               var14 = -904345603;
               var10003 = var14 + var15 - 2 * (var14 & var15);
               var15 = -1580181874;
               var14 = var10003;
               var10003 = (var14 | var15) & ~(var14 & var15);
               var15 = -706853784;
               var14 = 1410706512;
               var10003 = var14 + var15 - 2 * (var14 & var15);
               var15 = -243087459;
               var14 = var10003;
               var10003 = (var14 | var15) & (~var14 | ~var15);
               var15 = 1801625604;
               var14 = 576542950;
               var10004 = var14 + var15 - 2 * (var14 & var15);
               var15 = -381058520;
               var14 = var10004;
               var15 = (var14 | var15) & ~(var14 & var15);
               var14 = var10003;
               var10003 = var14 - ~var15 - 1;
               var15 = -2130452176;
               var14 = -1188345419;
               var10003 = (var14 | var15) - (var14 & var15);
               var15 = 1088124518;
               var14 = var10003;
               var10003 = var14 & ~var15 | ~var14 & var15;
               var15 = 1964749261;
               var14 = 192207830;
               var10004 = var14 + var15 - 2 * (var14 & var15);
               var15 = 210512930;
               var14 = var10004;
               var10004 = (var14 | var15) & (~var14 | ~var15);
               var10004 = (int)-4063517221366832597L;
               var15 = -89062622;
               var14 = var10004;
               var10004 = (var14 | var15) & ~(var14 & var15);
               var15 = -1139713896;
               var14 = var10004;
               var10004 = (var14 | var15) & (~var14 | ~var15);
               String var959 = "ja";
               var15 = 2141649603;
               var14 = -1057861336;
               var10005 = (var14 | var15) - (var14 & var15);
               var15 = -1266253706;
               var14 = var10005;
               var10005 = (var14 | var15) & (~var14 | ~var15);
               var15 = 449343282;
               var14 = 1455937709;
               var10006 = var14 & ~var15 | ~var14 & var15;
               var15 = 1868028111;
               var14 = var10006;
               var10006 = var14 + var15 - 2 * (var14 & var15);
               var10006 = (int)-1501984896304660521L;
               var15 = -630658137;
               var14 = var10006;
               var10006 = (var14 | var15) - (var14 & var15);
               var15 = 266418467;
               var14 = var10006;
               if (var12.has(
                  a(
                     "лЏ‹иЃ«лЏ™иЃ¤лЏќ",
                     "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                     var10003,
                     var10004,
                     var10005,
                     var14 + var15 - 2 * (var14 & var15)
                  )
               )) {
                  String var804 = "jb";
                  var10004 = (int)4783508133324365218L;
                  var15 = -1072756337;
                  var14 = var10004;
                  var10004 = (var14 | var15) & ~(var14 & var15);
                  var15 = 67687302;
                  var14 = var10004;
                  var10004 = var14 & ~var15 | ~var14 & var15;
                  String var962 = "jc";
                  var15 = -1041602404;
                  var14 = 357150136;
                  var10005 = (var14 | var15) - (var14 & var15);
                  var15 = -1404036153;
                  var14 = var10005;
                  var10005 = (var14 | var15) - (var14 & var15);
                  var15 = 1396788507;
                  var14 = 898489174;
                  var10006 = (var14 | var15) & ~(var14 & var15);
                  var15 = -683276820;
                  var14 = var10006;
                  var10006 = (var14 + var15 - 2 * (var14 & var15)) * (var14 + var15 - 2 * (var14 & var15));
                  var10006 = (int)3570845988421218842L;
                  var15 = -438573654;
                  var14 = var10006;
                  var10006 = (var14 | var15) & (~var14 | ~var15);
                  var15 = -1516163530;
                  var14 = var10006;
                  var10006 = (var14 | var15) - (var14 & var15);
                  String var1076 = "jd";
                  var1044 = (int)-3522902407211106410L;
                  var15 = 843173766;
                  var14 = var1044;
                  var1044 = var14 & ~var15 | ~var14 & var15;
                  var15 = -978800468;
                  var14 = var1044;
                  var1044 = var14 & ~var15 | ~var14 & var15;
                  int var1093 = (int)8877972920642046798L;
                  var15 = 1827908644;
                  var14 = var1093;
                  var1093 = var14 + var15 - 2 * (var14 & var15);
                  var15 = -663231641;
                  var14 = var1093;
                  var1093 = ((var14 | var15) - (var14 & var15)) * ((var14 | var15) - (var14 & var15));
                  var1093 = (int)6733537919941466222L;
                  var15 = 544246679;
                  var14 = var1093;
                  var1093 = (var14 | var15) & ~(var14 & var15);
                  var15 = -671383887;
                  var14 = var1093;
                  var13.c = this.a(
                     var12.get(
                        a(
                           "лЏ‹дЃ°лЏ™дЃїлЏќ",
                           "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                           var10005,
                           var10006,
                           var1044,
                           (var14 | var15) - (var14 & var15)
                        )
                     )
                  );
               }

               var2.d.put(var11, var13);
            }
         }
      }

      return var2;
   }

   private Map<Float, float[]> a(JsonElement var1) {
      HashMap var2 = new HashMap();
      if (var1.isJsonObject()) {
         JsonObject var3 = var1.getAsJsonObject();

         for (java.util.Map.Entry var5 : var3.entrySet()) {
            try {
               float var6 = Float.parseFloat((String)var5.getKey());
               JsonElement var7 = (JsonElement)var5.getValue();
               int var12 = -1028707369;
               int var11 = 2036051600;
               int var10000 = (var11 | var12) & (~var11 | ~var12);
               var12 = -1141603004;
               var11 = var10000;
               float[] var8 = new float[var11 + var12 - 2 * (var11 & var12)];
               if (!var7.isJsonObject()) {
                  if (var7.isJsonArray()) {
                     JsonArray var9 = var7.getAsJsonArray();
                     var12 = -32765834;
                     var11 = -1074498992;
                     int var10001 = var11 + var12 - 2 * (var11 & var12);
                     var12 = 1106803238;
                     var11 = var10001;
                     var10001 = var11 + var12 - 2 * (var11 & var12);
                     var12 = 1892010631;
                     var11 = 1421721718;
                     int var10003 = (var11 | var12) - (var11 & var12);
                     var12 = 611846897;
                     var11 = var10003;
                     var8[var10001] = var9.get(var11 & ~var12 | ~var11 & var12).getAsFloat();
                     var12 = -1043442802;
                     var11 = -1714685043;
                     var10001 = var11 & ~var12 | ~var11 & var12;
                     var12 = 1476765698;
                     var11 = var10001;
                     var10001 = (var11 | var12) & (~var11 | ~var12);
                     var12 = 1477451181;
                     var11 = 1974299378;
                     var10003 = var11 & ~var12 | ~var11 & var12;
                     var12 = 767391582;
                     var11 = var10003;
                     var8[var10001] = var9.get(var11 & ~var12 | ~var11 & var12).getAsFloat();
                     var10001 = (int)-2249915480601722413L;
                     var12 = -1940338516;
                     var11 = var10001;
                     var10001 = var11 + var12 - 2 * (var11 & var12);
                     var12 = -263337603;
                     var11 = var10001;
                     var10001 = (var11 | var12) & (~var11 | ~var12);
                     var10003 = (int)-1842971632531192877L;
                     var12 = -1781933148;
                     var11 = var10003;
                     var10003 = var11 + var12 - 2 * (var11 & var12);
                     var12 = 437426293;
                     var11 = var10003;
                     var8[var10001] = var9.get((var11 | var12) & ~(var11 & var12)).getAsFloat();
                  }
               } else {
                  JsonObject var14 = var7.getAsJsonObject();
                  String var154 = "je";
                  int var10002 = (int)-8826666977262069376L;
                  var12 = -261125987;
                  var11 = var10002;
                  var10002 = (var11 | var12) - (var11 & var12);
                  var12 = 158259302;
                  var11 = var10002;
                  var10002 = (var11 | var12) & (~var11 | ~var12);
                  int var171 = (int)-8167758465553451942L;
                  var12 = -1489475279;
                  var11 = var171;
                  var171 = (var11 | var12) & ~(var11 & var12);
                  var12 = 1644541277;
                  var11 = var171;
                  var171 = ((var11 | var12) & (~var11 | ~var12)) * ((var11 | var12) & (~var11 | ~var12));
                  var171 = (int)-4061765618347768378L;
                  var12 = 962580615;
                  var11 = var171;
                  var171 = (var11 | var12) - (var11 & var12);
                  var12 = -187659358;
                  var11 = var171;
                  var171 = var11 & ~var12 | ~var11 & var12;
                  int var10004 = (int)7960373226917030930L;
                  var12 = -1087215960;
                  var11 = var10004;
                  var10004 = var11 & ~var12 | ~var11 & var12;
                  var12 = 138238080;
                  var11 = var10004;
                  var10004 = (var11 | var12) - (var11 & var12);
                  var10004 = (int)-8529874531885655762L;
                  var12 = 2130013662;
                  var11 = var10004;
                  var10004 = (var11 | var12) & (~var11 | ~var12);
                  var12 = 1897166426;
                  var11 = var10004;
                  var10004 = (var11 | var12) & ~(var11 & var12);
                  var12 = -987730868;
                  var11 = -1444435885;
                  int var10005 = (var11 | var12) & (~var11 | ~var12);
                  var12 = 1210453398;
                  var11 = var10005;
                  var10005 = (var11 & ~var12 | ~var11 & var12) * (var11 & ~var12 | ~var11 & var12);
                  var12 = -2050858118;
                  var11 = -1614076424;
                  var10005 = (var11 | var12) & (~var11 | ~var12);
                  var12 = 484415190;
                  var11 = var10005;
                  var10005 = (var11 | var12) & (~var11 | ~var12);
                  var12 = -1000826739;
                  var11 = 165576061;
                  int var10006 = (var11 | var12) - (var11 & var12);
                  var12 = -2145442043;
                  var11 = var10006;
                  var10006 = (var11 | var12) & (~var11 | ~var12);
                  var12 = -1276145872;
                  var11 = -1008213437;
                  var10006 = (var11 | var12) - (var11 & var12);
                  var12 = 1879672838;
                  var11 = var10006;
                  if (var14.has(
                     a(
                        "\u20c7б­‚вѓ’б­“вѓћб­•",
                        "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                        var171,
                        var10004,
                        var10005,
                        (var11 | var12) & ~(var11 & var12)
                     )
                  )) {
                     var154 = "jf";
                     var10002 = (int)-3689403496276651339L;
                     var12 = -299298615;
                     var11 = var10002;
                     var10002 = var11 + var12 - 2 * (var11 & var12);
                     var12 = -774942229;
                     var11 = var10002;
                     var10002 = var11 + var12 - 2 * (var11 & var12);
                     var10004 = (int)-4889618751252579449L;
                     var12 = -1886650464;
                     var11 = var10004;
                     var10004 = (var11 | var12) & (~var11 | ~var12);
                     var12 = 1661715497;
                     var11 = var10004;
                     var10004 = var11 + var12 - 2 * (var11 & var12);
                     var12 = -1262441658;
                     var11 = -275573141;
                     var10004 = var11 + var12 - 2 * (var11 & var12);
                     var12 = 1532214573;
                     var11 = var10004;
                     var10004 = (var11 | var12) - (var11 & var12);
                     var171 = (int)-937361851870622049L;
                     var12 = -1022839045;
                     var11 = var171;
                     var171 = var11 & ~var12 | ~var11 & var12;
                     var12 = 180659847;
                     var11 = var171;
                     var171 = (var11 | var12) & (~var11 | ~var12);
                     String var195 = "jg";
                     var10004 = (int)6085047062237631314L;
                     var12 = 1447095335;
                     var11 = var10004;
                     var10004 = (var11 | var12) - (var11 & var12);
                     var12 = 126924611;
                     var11 = var10004;
                     var10004 = (var11 | var12) & (~var11 | ~var12);
                     var10006 = (int)4715957735416568602L;
                     var12 = -1654932533;
                     var11 = var10006;
                     var10006 = (var11 | var12) - (var11 & var12);
                     var12 = 1712922835;
                     var11 = var10006;
                     var10006 = var11 & ~var12 | ~var11 & var12;
                     var10005 = (int)4910025148502300591L;
                     var12 = -1520325050;
                     var11 = var10005;
                     var10005 = (var11 | var12) - (var11 & var12);
                     var12 = 89757472;
                     var11 = var10005;
                     var10005 = var11 + var12 - 2 * (var11 & var12);
                     var10006 = (int)-2808490404550676374L;
                     var12 = 777065751;
                     var11 = var10006;
                     var10006 = (var11 | var12) & ~(var11 & var12);
                     var12 = -1268430298;
                     var11 = var10006;
                     var10006 = ((var11 | var12) & (~var11 | ~var12)) * ((var11 | var12) & (~var11 | ~var12));
                     var12 = 1683549567;
                     var11 = -982441726;
                     var10006 = var11 + var12 - 2 * (var11 & var12);
                     var12 = -1592531076;
                     var11 = var10006;
                     JsonArray var10 = var14.getAsJsonArray(
                        a(
                           "\u20c7н”¶вѓ’н”§вѓћн”Ў",
                           "`кџік“єкЃЎк¤ђзµєи№™в‘їи­ёг‘ґ}кџµк’ёкЃґк¤‘зµїи№‰\u2435и¬°г‘Іq\ua7edк’єкЃЅк¤Њзµґиё…\u245dи¬Іг‘ґy\ua7e9к’№кЃёк¤‡зµ•и№…в‘©и¬єг‘Іf\ua7efк’¶кЃѓк¤Ђзµёи№Ћв‘їи¬Ґг‘І`",
                           var171,
                           var10004,
                           var10005,
                           (var11 | var12) & ~(var11 & var12)
                        )
                     );
                     var12 = 1991196947;
                     var11 = -1082756375;
                     int var156 = var11 & ~var12 | ~var11 & var12;
                     var12 = -908512262;
                     var11 = var156;
                     int var157 = var11 + var12 - 2 * (var11 & var12);
                     var171 = (int)-8865706771144211241L;
                     var12 = 907565245;
                     var11 = var171;
                     var171 = (var11 | var12) - (var11 & var12);
                     var12 = 825547882;
                     var11 = var171;
                     var8[var157] = var10.get((var11 | var12) - (var11 & var12)).getAsFloat();
                     var12 = 2099795445;
                     var11 = 705088341;
                     int var158 = var11 + var12 - 2 * (var11 & var12);
                     var12 = 1462673057;
                     var11 = var158;
                     int var159 = var11 & ~var12 | ~var11 & var12;
                     var12 = 675363934;
                     var11 = 371189649;
                     var171 = (var11 | var12) & ~(var11 & var12);
                     var12 = 1046404046;
                     var11 = var171;
                     var8[var159] = var10.get((var11 | var12) & (~var11 | ~var12)).getAsFloat();
                     var12 = -862252843;
                     var11 = 120513031;
                     int var160 = (var11 | var12) - (var11 & var12);
                     var12 = -877268272;
                     var11 = var160;
                     int var161 = (var11 | var12) & ~(var11 & var12);
                     var171 = (int)-6033327769917201393L;
                     var12 = -1811132926;
                     var11 = var171;
                     var171 = (var11 | var12) & (~var11 | ~var12);
                     var12 = 1620683279;
                     var11 = var171;
                     var8[var161] = var10.get((var11 | var12) - (var11 & var12)).getAsFloat();
                  }

                  if (a != a) {
                     var12 = -1781993874;
                     var11 = 111639647;
                     var10000 = (var11 | var12) - (var11 & var12);
                     var12 = -1821404623;
                     var11 = var10000;
                     var10000 = var11 + var12 - 2 * (var11 & var12);
                     var12 = 402192624;
                     var11 = 1948563541;
                     int var162 = (var11 | var12) - (var11 & var12);
                     var12 = 1675378853;
                     var11 = var162;
                     var10000 /= (var11 | var12) & (~var11 | ~var12);
                  }
               }

               var2.put(var6, var8);
            } catch (NumberFormatException var13) {
            }
         }
      }

      return var2;
   }

   private void a(ModelDefinition var1, CosmeticModelRenderer.CompiledModel var2, int var3) {
      Map var4 = this.h.get(var3);
      if (var4 != null) {
         float var8;
         label64: {
            long var5 = this.g.computeIfAbsent(var3, var0 -> System.currentTimeMillis());
            float var10000 = (float)(System.currentTimeMillis() - var5);
            int var19 = -419465447;
            int var18 = 128494928;
            int var10001 = (var18 | var19) & (~var18 | ~var19);
            var19 = 388170400;
            var18 = var10001;
            var10001 = var18 + var19 - 2 * (var18 & var19);
            float var7 = var10000 / 1000.0F;
            if (var2.b) {
               if (var2.c > 0.0F) {
                  var8 = var7 % var2.c;
                  int var226 = (int)6554044767606945359L;
                  var19 = -1434585268;
                  var18 = var226;
                  int var227 = (var18 | var19) - (var18 & var19);
                  var19 = -1418649070;
                  var18 = var227;
                  int var228 = (var18 | var19) & ~(var18 & var19);
                  var10001 = (int)-3012910573915235907L;
                  var19 = 731667476;
                  var18 = var10001;
                  var10001 = var18 + var19 - 2 * (var18 & var19);
                  var19 = 2083359512;
                  var18 = var10001;
                  var19 = var18 & ~var19 | ~var18 & var19;
                  var18 = var228;
                  int var229 = (var18 ^ var19) + 2 * (var18 & var19);
                  int var230 = (int)3775767403983525801L;
                  var19 = -300420972;
                  var18 = var230;
                  int var231 = (var18 | var19) & ~(var18 & var19);
                  var19 = 1329777598;
                  var18 = var231;
                  int var24 = (var18 | var19) & (~var18 | ~var19);
                  var19 = -1714170790;
                  var18 = 793278642;
                  var10001 = (var18 | var19) & ~(var18 & var19);
                  var19 = -1231311646;
                  var18 = var10001;
                  var10001 = (var18 | var19) & ~(var18 & var19);
                  var10001 = (int)4146953496350132348L;
                  var19 = 741384736;
                  var18 = var10001;
                  var10001 = var18 & ~var19 | ~var18 & var19;
                  var19 = -298636706;
                  var18 = var10001;
                  var10001 = (var18 | var19) & (~var18 | ~var19);
                  var10001 = (int)-3963874218326704271L;
                  var19 = -949293746;
                  var18 = var10001;
                  var10001 = var18 + var19 - 2 * (var18 & var19);
                  var19 = -1524346317;
                  var18 = var10001;
                  var10001 = (var18 | var19) - (var18 & var19);
                  int var232 = (int)-8857898406515138553L;
                  var19 = 798187716;
                  var18 = var232;
                  int var233 = (var18 | var19) - (var18 & var19);
                  var19 = -1285793641;
                  var18 = var233;
                  if (((var18 | var19) & ~(var18 & var19)) >= var24) {
                     int var234 = (int)-1580450410676597793L;
                     var19 = -1424880101;
                     var18 = var234;
                     int var235 = (var18 | var19) & ~(var18 & var19);
                     var19 = 898778564;
                     var18 = var235;
                     int var236 = var18 + var19 - 2 * (var18 & var19);
                     var10001 = (int)-2858157071374363142L;
                     var19 = 540951402;
                     var18 = var10001;
                     var10001 = var18 & ~var19 | ~var18 & var19;
                     var19 = 1212913296;
                     var18 = var10001;
                     int var237 = var236 / ((var18 | var19) & (~var18 | ~var19));
                  }
                  break label64;
               }

               if (b) {
                  var19 = -530386677;
                  var18 = 1757106186;
                  int var223 = (var18 | var19) & ~(var18 & var19);
                  var19 = -928128795;
                  var18 = var223;
                  int var224 = (var18 & ~var19 | ~var18 & var19) * (var18 & ~var19 | ~var18 & var19);
                  var19 = -1372613613;
                  var18 = 229933811;
                  int var225 = (var18 | var19) & ~(var18 & var19);
                  var19 = -460852231;
                  var18 = var225;
                  int var17 = var18 + var19 - 2 * (var18 & var19);
               }
            }

            var8 = Math.min(var7, var2.c);
         }

         for (ModelPart var10 : var1.a) {
            this.b(var10, var4);
            String var238 = "jh";
            int var149 = 601571273;
            int var51 = 914944487;
            int var239 = (var51 | var149) - (var51 & var149);
            var149 = 1448827882;
            var51 = var239;
            int var25 = (var51 | var149) - (var51 & var149);
            int var240 = (int)6623312334861426253L;
            var149 = 674713855;
            var51 = var240;
            int var241 = (var51 | var149) & ~(var51 & var149);
            var149 = -1976653525;
            var51 = var241;
            int var242 = ((var51 | var149) & ~(var51 & var149)) * ((var51 | var149) & ~(var51 & var149));
            int var243 = (int)3812619330859112242L;
            var149 = 1851066957;
            var51 = var243;
            int var244 = var51 & ~var149 | ~var51 & var149;
            var149 = -1521868748;
            var51 = var244;
            if ((var51 | var149) - (var51 & var149) >= var25) {
               int var245 = (int)-5633317405770168459L;
               var149 = -1041749307;
               var51 = var245;
               int var246 = var51 & ~var149 | ~var51 & var149;
               var149 = 1138978224;
               var51 = var246;
               int var247 = var51 & ~var149 | ~var51 & var149;
               int var273 = (int)-525321276606521419L;
               var149 = -2017396224;
               var51 = var273;
               var273 = (var51 | var149) & (~var51 | ~var149);
               var149 = -1926683211;
               var51 = var273;
               int var248 = var247 / ((var51 | var149) - (var51 & var149));
            }
         }

         for (java.util.Map.Entry var21 : var2.d.entrySet()) {
            String var11 = (String)var21.getKey();
            CosmeticModelRenderer.PartPoseMap var12 = (CosmeticModelRenderer.PartPoseMap)var21.getValue();
            ModelPart var13 = this.a(var1, var11);
            if (var13 != null) {
               float[] var14 = (float[])var4.get(var11);
               if (var14 == null) {
                  int var249 = (int)-5798989327063115647L;
                  int var159 = 1833607477;
                  int var61 = var249;
                  var249 = var61 + var159 - 2 * (var61 & var159);
                  var159 = 492157373;
                  var61 = var249;
                  float[] var251 = new float[var61 & ~var159 | ~var61 & var159];
                  var159 = -1528439910;
                  var61 = 620583225;
                  int var10002 = (var61 | var159) - (var61 & var159);
                  var159 = -2145877341;
                  var61 = var10002;
                  var251[var61 + var159 - 2 * (var61 & var159)] = 0.0F;
                  var159 = -177530218;
                  var61 = -1895196500;
                  var10002 = var61 & ~var159 | ~var61 & var159;
                  var159 = 2053276219;
                  var61 = var10002;
                  var251[(var61 | var159) & (~var61 | ~var159)] = 0.0F;
                  var159 = 515296633;
                  var61 = -469575970;
                  var10002 = (var61 | var159) & ~(var61 & var159);
                  var159 = -88859739;
                  var61 = var10002;
                  var251[var61 + var159 - 2 * (var61 & var159)] = 0.0F;
                  var159 = -1514347999;
                  var61 = 1868267198;
                  var10002 = (var61 | var159) & (~var61 | ~var159);
                  var159 = -890790756;
                  var61 = var10002;
                  var251[(var61 | var159) - (var61 & var159)] = 0.0F;
                  var10002 = (int)-7830338641108284572L;
                  var159 = -1314527231;
                  var61 = var10002;
                  var10002 = (var61 | var159) & ~(var61 & var159);
                  var159 = -1742986399;
                  var61 = var10002;
                  var251[var61 & ~var159 | ~var61 & var159] = 0.0F;
                  var10002 = (int)5028535981324608221L;
                  var159 = 17237872;
                  var61 = var10002;
                  var10002 = (var61 | var159) - (var61 & var159);
                  var159 = 706258344;
                  var61 = var10002;
                  var251[(var61 | var159) & (~var61 | ~var159)] = 0.0F;
                  var10002 = (int)-6336894713743261673L;
                  var159 = 2010909910;
                  var61 = var10002;
                  var10002 = (var61 | var159) & ~(var61 & var159);
                  var159 = 2140425415;
                  var61 = var10002;
                  var251[(var61 | var159) & (~var61 | ~var159)] = 1.0F;
                  var159 = 1342353129;
                  var61 = 649808130;
                  var10002 = (var61 | var159) & ~(var61 & var159);
                  var159 = 1991895020;
                  var61 = var10002;
                  var251[var61 + var159 - 2 * (var61 & var159)] = 1.0F;
                  var159 = -464236669;
                  var61 = -556789209;
                  var10002 = var61 + var159 - 2 * (var61 & var159);
                  var159 = 981753260;
                  var61 = var10002;
                  var251[var61 & ~var159 | ~var61 & var159] = 1.0F;
                  var14 = var251;
               }

               if (!var12.a.isEmpty()) {
                  float[] var15 = this.a(var12.a, var8);
                  int var179 = 775286102;
                  int var81 = 716974208;
                  int var297 = (var81 | var179) & (~var81 | ~var179);
                  var179 = 76137942;
                  var81 = var297;
                  float var275 = var14[(var81 | var179) & (~var81 | ~var179)];
                  var179 = 848054546;
                  var81 = -267713559;
                  int var10003 = (var81 | var179) & (~var81 | ~var179);
                  var179 = -1031320837;
                  var81 = var10003;
                  var13.a(var275 + (float)Math.toRadians(-var15[(var81 | var179) & (~var81 | ~var179)]));
                  var179 = -1589746739;
                  var81 = -885035675;
                  var297 = (var81 | var179) - (var81 & var179);
                  var179 = 1778454185;
                  var81 = var297;
                  var275 = var14[(var81 | var179) - (var81 & var179)];
                  var10003 = (int)-2318660692925345380L;
                  var179 = -184861439;
                  var81 = var10003;
                  var10003 = (var81 | var179) & (~var81 | ~var179);
                  var179 = -2021839716;
                  var81 = var10003;
                  var13.b(var275 + (float)Math.toRadians(-var15[var81 & ~var179 | ~var81 & var179]));
                  var297 = (int)-5410075230806883105L;
                  var179 = 406250225;
                  var81 = var297;
                  var297 = var81 & ~var179 | ~var81 & var179;
                  var179 = -743758292;
                  var81 = var297;
                  var275 = var14[(var81 | var179) - (var81 & var179)];
                  var179 = -1595851867;
                  var81 = -206254109;
                  var10003 = var81 + var179 - 2 * (var81 & var179);
                  var179 = 1398142020;
                  var81 = var10003;
                  var13.c(var275 + (float)Math.toRadians(var15[(var81 | var179) & ~(var81 & var179)]));
               }

               if (!var12.b.isEmpty()) {
                  float[] var22 = this.a(var12.b, var8);
                  int var191 = -1020368514;
                  int var93 = -989798522;
                  int var301 = var93 + var191 - 2 * (var93 & var191);
                  var191 = 103724795;
                  var93 = var301;
                  float var278 = var14[(var93 | var191) & (~var93 | ~var191)];
                  int var313 = (int)-1504271723408825324L;
                  var191 = 1926432258;
                  var93 = var313;
                  var313 = (var93 | var191) - (var93 & var191);
                  var191 = 442393110;
                  var93 = var313;
                  var13.d(var278 + var22[(var93 | var191) & ~(var93 & var191)]);
                  var301 = (int)-3586688583074935473L;
                  var191 = 1324722577;
                  var93 = var301;
                  var301 = var93 & ~var191 | ~var93 & var191;
                  var191 = -752147238;
                  var93 = var301;
                  var278 = var14[var93 + var191 - 2 * (var93 & var191)];
                  var191 = -949272479;
                  var93 = -213855363;
                  var313 = (var93 | var191) & ~(var93 & var191);
                  var191 = 875270941;
                  var93 = var313;
                  var13.e(var278 + var22[(var93 | var191) - (var93 & var191)]);
                  var191 = 1150607419;
                  var93 = 1506611241;
                  var301 = (var93 | var191) & ~(var93 & var191);
                  var191 = 492433431;
                  var93 = var301;
                  var278 = var14[var93 + var191 - 2 * (var93 & var191)];
                  var191 = 1143217427;
                  var93 = -1232566074;
                  var313 = var93 + var191 - 2 * (var93 & var191);
                  var191 = -223570473;
                  var93 = var313;
                  var13.f(var278 + var22[(var93 | var191) & (~var93 | ~var191)]);
               }

               if (!var12.c.isEmpty()) {
                  float[] var23 = this.a(var12.c, var8);
                  int var305 = (int)-966552416683257136L;
                  int var203 = -335621037;
                  int var105 = var305;
                  var305 = (var105 | var203) & (~var105 | ~var203);
                  var203 = 1496467077;
                  var105 = var305;
                  float var281 = var14[var105 + var203 - 2 * (var105 & var203)];
                  int var317 = (int)5115386760756385885L;
                  var203 = 899661279;
                  var105 = var317;
                  var317 = var105 + var203 - 2 * (var105 & var203);
                  var203 = 1040278914;
                  var105 = var317;
                  var13.g(var281 * var23[(var105 | var203) & (~var105 | ~var203)]);
                  var305 = (int)-9071985021596821933L;
                  var203 = -1146001400;
                  var105 = var305;
                  var305 = var105 + var203 - 2 * (var105 & var203);
                  var203 = -1883242916;
                  var105 = var305;
                  var281 = var14[(var105 | var203) & (~var105 | ~var203)];
                  var203 = 80136289;
                  var105 = -2086592831;
                  var317 = var105 & ~var203 | ~var105 & var203;
                  var203 = -2023237983;
                  var105 = var317;
                  var13.h(var281 * var23[var105 + var203 - 2 * (var105 & var203)]);
                  var203 = 2107388639;
                  var105 = 1437748910;
                  var305 = (var105 | var203) & (~var105 | ~var203);
                  var203 = 674135161;
                  var105 = var305;
                  var281 = var14[(var105 | var203) - (var105 & var203)];
                  var203 = -368289128;
                  var105 = -1878595331;
                  var317 = (var105 | var203) - (var105 & var203);
                  var203 = 2047521383;
                  var105 = var317;
                  var13.i(var281 * var23[(var105 | var203) & ~(var105 & var203)]);
               }
            }
         }

         String var252 = "ji";
         int var215 = 1002186511;
         int var117 = -1725217451;
         int var253 = var117 + var215 - 2 * (var117 & var215);
         var215 = -1525507790;
         var117 = var253;
         int var26 = var117 + var215 - 2 * (var117 & var215);
         a = var26;
         var252 = "jj";
         int var255 = (int)2085026693325386846L;
         var215 = 686699391;
         var117 = var255;
         int var256 = var117 + var215 - 2 * (var117 & var215);
         var215 = 1433422242;
         var117 = var256;
         if (var117 + var215 - 2 * (var117 & var215) <= a) {
            var215 = -1353071699;
            var117 = -877575009;
            int var257 = var117 + var215 - 2 * (var117 & var215);
            var215 = 1692960562;
            var117 = var257;
            int var258 = var117 & ~var215 | ~var117 & var215;
            int var284 = (int)1705305130441758494L;
            var215 = -1754785781;
            var117 = var284;
            var284 = (var117 | var215) & ~(var117 & var215);
            var215 = -1295397099;
            var117 = var284;
            int var259 = var258 / ((var117 | var215) & (~var117 | ~var215));
         }
      }
   }

   private void b(ModelPart var1, Map<String, float[]> var2) {
      float[] var3 = (float[])var2.get(var1.d);
      if (var3 != null) {
         int var7 = 324839377;
         int var6 = 1274831236;
         int var10002 = var6 + var7 - 2 * (var6 & var7);
         var7 = 1486931541;
         var6 = var10002;
         var1.a(var3[(var6 | var7) & ~(var6 & var7)]);
         var7 = -1754152036;
         var6 = -1548440143;
         var10002 = (var6 | var7) - (var6 & var7);
         var7 = 885354028;
         var6 = var10002;
         var1.b(var3[var6 & ~var7 | ~var6 & var7]);
         var7 = -334704791;
         var6 = -220143002;
         var10002 = (var6 | var7) & (~var6 | ~var7);
         var7 = 518794509;
         var6 = var10002;
         var1.c(var3[(var6 | var7) - (var6 & var7)]);
         var7 = 827306679;
         var6 = 1184532668;
         var10002 = var6 & ~var7 | ~var6 & var7;
         var7 = 2010462728;
         var6 = var10002;
         var1.d(var3[var6 & ~var7 | ~var6 & var7]);
         var7 = 37089037;
         var6 = 191167574;
         var10002 = (var6 | var7) & (~var6 | ~var7);
         var7 = 156308319;
         var6 = var10002;
         var1.e(var3[var6 + var7 - 2 * (var6 & var7)]);
         var10002 = (int)-511858558445770207L;
         var7 = 376041599;
         var6 = var10002;
         var10002 = (var6 | var7) & (~var6 | ~var7);
         var7 = 1995528795;
         var6 = var10002;
         var1.f(var3[var6 + var7 - 2 * (var6 & var7)]);
         var10002 = (int)-241148358638937851L;
         var7 = 372129452;
         var6 = var10002;
         var10002 = (var6 | var7) & ~(var6 & var7);
         var7 = 1728473007;
         var6 = var10002;
         var1.g(var3[var6 & ~var7 | ~var6 & var7]);
         var7 = -1018183958;
         var6 = -1475353493;
         var10002 = (var6 | var7) - (var6 & var7);
         var7 = 1799379590;
         var6 = var10002;
         var1.h(var3[(var6 | var7) & ~(var6 & var7)]);
         var7 = 417543848;
         var6 = 974122815;
         var10002 = (var6 | var7) & ~(var6 & var7);
         var7 = 585947551;
         var6 = var10002;
         var1.i(var3[(var6 | var7) & ~(var6 & var7)]);
      }

      for (ModelPart var5 : var1.b) {
         this.b(var5, var2);
      }
   }

   private ModelPart a(ModelDefinition var1, String var2) {
      for (ModelPart var4 : var1.a) {
         ModelPart var5 = this.a(var4, var2);
         if (var5 != null) {
            return var5;
         }

         if (a != a) {
            int var9 = -223279868;
            int var8 = -1192055051;
            int var10000 = var8 & ~var9 | ~var8 & var9;
            var9 = 473718802;
            var8 = var10000;
            var10000 = (var8 | var9) - (var8 & var9);
            var10000 = (int)-8194288097157434871L;
            var9 = 1861468574;
            var8 = var10000;
            var10000 = var8 & ~var9 | ~var8 & var9;
            var9 = 164930616;
            var8 = var10000;
            int var7 = (var8 | var9) - (var8 & var9);
            a = var7;
         }
      }

      return null;
   }

   private ModelPart a(ModelPart var1, String var2) {
      if (var1.d.equals(var2)) {
         return var1;
      }

      int var10001 = (int)1922589827649406582L;
      int var9 = -1155633107;
      int var8 = var10001;
      var10001 = (var8 | var9) & (~var8 | ~var9);
      var9 = -1228081575;
      var8 = var10001;
      var10001 = var8 & ~var9 | ~var8 & var9;
      int var10000 = (int)-3532757623593741893L;
      var9 = 1622661818;
      var8 = var10000;
      var10000 = (var8 | var9) - (var8 & var9);
      var9 = -695587450;
      var8 = var10000;
      int var7 = var8 + var9 - 2 * (var8 & var9);
      a = var7;
      var9 = -224467913;
      var8 = 740865159;
      var10001 = var8 & ~var9 | ~var8 & var9;
      var9 = -558477135;
      var8 = var10001;
      var10001 = (var8 | var9) - (var8 & var9);
      var9 = -1712336373;
      var8 = -263113379;
      var10001 = var8 + var9 - 2 * (var8 & var9);
      var9 = 1774118749;
      var8 = var10001;
      var10001 = (var8 | var9) & ~(var8 & var9);
      var10001 = (int)-4279729453683685764L;
      var9 = 1807488316;
      var8 = var10001;
      var10001 = (var8 | var9) & ~(var8 & var9);
      var9 = -908831929;
      var8 = var10001;
      var10001 = (var8 | var9) & ~(var8 & var9);
      var10000 = (int)-9222878409536177558L;
      var9 = -378654077;
      var8 = var10000;
      var10000 = var8 + var9 - 2 * (var8 & var9);
      var9 = 933919401;
      var8 = var10000;
      if (((var8 | var9) & ~(var8 & var9)) < a) {
         var10000 = (int)-3193757352929083139L;
         var9 = 1683745570;
         var8 = var10000;
         var10000 = var8 + var9 - 2 * (var8 & var9);
         var9 = -1283118113;
         var8 = var10000;
         var10000 = var8 + var9 - 2 * (var8 & var9);
         var9 = -1475025551;
         var8 = 500738781;
         var10001 = (var8 | var9) & (~var8 | ~var9);
         var9 = -1244901460;
         var8 = var10001;
         var10000 /= (var8 | var9) & ~(var8 & var9);
      }

      for (ModelPart var4 : var1.b) {
         ModelPart var5 = this.a(var4, var2);
         if (var5 != null) {
            return var5;
         }
      }

      return null;
   }

   private float[] a(Map<Float, float[]> var1, float var2) {
      if (var1.isEmpty()) {
         int var87 = -2064150806;
         int var46 = -318043666;
         int var100 = (var46 | var87) & (~var46 | ~var87);
         var87 = 1778157319;
         var46 = var100;
         float[] var101 = new float[var46 & ~var87 | ~var46 & var87];
         int var112 = (int)-2999376386126218030L;
         var87 = -1582480961;
         var46 = var112;
         var112 = var46 + var87 - 2 * (var46 & var87);
         var87 = -722649747;
         var46 = var112;
         var101[(var46 | var87) & ~(var46 & var87)] = 0.0F;
         var112 = (int)-5208299663971671013L;
         var87 = 1358912667;
         var46 = var112;
         var112 = (var46 | var87) - (var46 & var87);
         var87 = -1244530559;
         var46 = var112;
         var101[var46 + var87 - 2 * (var46 & var87)] = 0.0F;
         var87 = 1885558517;
         var46 = -384474505;
         var112 = var46 & ~var87 | ~var46 & var87;
         var87 = -1720306560;
         var46 = var112;
         var101[(var46 | var87) & ~(var46 & var87)] = 0.0F;
         return var101;
      }

      Float var3 = null;
      Float var4 = null;
      float[] var5 = null;
      float[] var6 = null;

      for (java.util.Map.Entry var8 : var1.entrySet()) {
         float var9 = (Float)var8.getKey();
         if (var9 <= var2 && (var3 == null || var9 > var3)) {
            var3 = var9;
            var5 = (float[])var8.getValue();
         }

         if (var9 >= var2 && (var4 == null || var9 < var4)) {
            var4 = var9;
            var6 = (float[])var8.getValue();
         }
      }

      if (var5 == null && var6 == null) {
         int var97 = (int)9086824945748160723L;
         int var79 = -212627960;
         int var38 = var97;
         var97 = var38 & ~var79 | ~var38 & var79;
         var79 = 271171288;
         var38 = var97;
         float[] var99 = new float[(var38 | var79) & ~(var38 & var79)];
         var79 = -1653012709;
         var38 = -123836254;
         int var108 = (var38 | var79) - (var38 & var79);
         var79 = 1709666233;
         var38 = var108;
         var99[var38 + var79 - 2 * (var38 & var79)] = 0.0F;
         var79 = -2109002718;
         var38 = -321895359;
         var108 = var38 + var79 - 2 * (var38 & var79);
         var79 = 1855678562;
         var38 = var108;
         var99[(var38 | var79) - (var38 & var79)] = 0.0F;
         var108 = (int)-7027735322519020927L;
         var79 = -1402145058;
         var38 = var108;
         var108 = (var38 | var79) - (var38 & var79);
         var79 = -1046139811;
         var38 = var108;
         var99[var38 + var79 - 2 * (var38 & var79)] = 0.0F;
         return var99;
      } else if (var5 == null) {
         return var6;
      } else if (var6 == null) {
         return var5;
      } else if (!var3.equals(var4)) {
         float var12 = (var2 - var3) / (var4 - var3);
         int var10000 = (int)-8125689639608172434L;
         int var11 = -1484046174;
         int var10 = var10000;
         var10000 = (var10 | var11) - (var10 & var11);
         var11 = -1281490737;
         var10 = var10000;
         float[] var96 = new float[var10 & ~var11 | ~var10 & var11];
         var11 = -770356623;
         var10 = -1073842641;
         int var10002 = var10 & ~var11 | ~var10 & var11;
         var11 = 1844132958;
         var10 = var10002;
         var10002 = (var10 | var11) & (~var10 | ~var11);
         int var10004 = (int)-7942662712056301048L;
         var11 = -1803879465;
         var10 = var10004;
         var10004 = var10 & ~var11 | ~var10 & var11;
         var11 = -111263265;
         var10 = var10004;
         float var10003 = var5[(var10 | var11) & (~var10 | ~var11)];
         int var10006 = (int)-5794607073990995335L;
         var11 = 2059382812;
         var10 = var10006;
         var10006 = var10 + var11 - 2 * (var10 & var11);
         var11 = 1198695013;
         var10 = var10006;
         float var10005 = var6[var10 & ~var11 | ~var10 & var11];
         var11 = -1643347853;
         var10 = 1503152650;
         int var10007 = (var10 | var11) & (~var10 | ~var11);
         var11 = -946550151;
         var10 = var10007;
         var96[var10002] = var10003 + var12 * (var10005 - var5[(var10 | var11) & (~var10 | ~var11)]);
         var10002 = (int)8228152598292361907L;
         var11 = -638560753;
         var10 = var10002;
         var10002 = var10 + var11 - 2 * (var10 & var11);
         var11 = 1394353341;
         var10 = var10002;
         var10002 = var10 & ~var11 | ~var10 & var11;
         var11 = 1238533522;
         var10 = -69131972;
         var10004 = var10 & ~var11 | ~var10 & var11;
         var11 = -1305238353;
         var10 = var10004;
         var10003 = var5[(var10 | var11) & ~(var10 & var11)];
         var11 = -1162827258;
         var10 = -1180056733;
         var10006 = var10 + var11 - 2 * (var10 & var11);
         var11 = 51997028;
         var10 = var10006;
         var10005 = var6[(var10 | var11) - (var10 & var11)];
         var10007 = (int)3376378597174429168L;
         var11 = 2091449322;
         var10 = var10007;
         var10007 = (var10 | var11) - (var10 & var11);
         var11 = -2089239013;
         var10 = var10007;
         var96[var10002] = var10003 + var12 * (var10005 - var5[(var10 | var11) & (~var10 | ~var11)]);
         var11 = 1062776461;
         var10 = -1244436218;
         var10002 = (var10 | var11) - (var10 & var11);
         var11 = -1970550903;
         var10 = var10002;
         var10002 = (var10 | var11) & (~var10 | ~var11);
         var10004 = (int)1077185989319884659L;
         var11 = -1262409966;
         var10 = var10004;
         var10004 = var10 & ~var11 | ~var10 & var11;
         var11 = -1900980125;
         var10 = var10004;
         var10003 = var5[var10 + var11 - 2 * (var10 & var11)];
         var10006 = (int)8688816749052744540L;
         var11 = 988903131;
         var10 = var10006;
         var10006 = var10 & ~var11 | ~var10 & var11;
         var11 = 2133960069;
         var10 = var10006;
         var10005 = var6[(var10 | var11) & (~var10 | ~var11)];
         var11 = 30777340;
         var10 = -1373812442;
         var10007 = (var10 | var11) & ~(var10 & var11);
         var11 = -1345792296;
         var10 = var10007;
         var96[var10002] = var10003 + var12 * (var10005 - var5[var10 & ~var11 | ~var10 & var11]);
         return var96;
      } else {
         return var5;
      }
   }

   public static String a(String var0, String var1, int var2, int var3, int var4, int var5) {
      char[] var6 = var1.toCharArray();
      char[] var7 = new char[var6.length];
      int var18 = 936769891;
      int var17 = 341229285;
      int var10000 = (var17 | var18) - (var17 & var18);
      var18 = 595806092;
      var17 = var10000;
      char[] var261 = new char[(var17 | var18) & (~var17 | ~var18)];
      int var10002 = (int)-5700187200206337295L;
      var18 = 282947346;
      var17 = var10002;
      var10002 = var17 & ~var18 | ~var17 & var18;
      var18 = 715417059;
      var17 = var10002;
      var10002 = (var17 | var18) - (var17 & var18);
      var18 = -379697299;
      var17 = -694338247;
      int var10003 = (var17 | var18) - (var17 & var18);
      var18 = 1069757030;
      var17 = var10003;
      var261[var10002] = (char)((var17 | var18) - (var17 & var18));
      var18 = -1645355403;
      var17 = 297665350;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var18 = -1940660942;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var18 = 1456563914;
      var17 = 626337104;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = 1938055199;
      var17 = var10003;
      var261[var10002] = (char)((var17 | var18) & (~var17 | ~var18));
      var10002 = (int)-7703052581304264661L;
      var18 = 2075400041;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var18 = 511708992;
      var17 = var10002;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var18 = -438080351;
      var17 = -1081885021;
      var10003 = (var17 | var18) & ~(var17 & var18);
      var18 = 1516302724;
      var17 = var10003;
      var261[var10002] = (char)((var17 | var18) - (var17 & var18));
      var10002 = (int)7525232820172334791L;
      var18 = -445804060;
      var17 = var10002;
      var10002 = var17 & ~var18 | ~var17 & var18;
      var18 = -820627680;
      var17 = var10002;
      var10002 = var17 & ~var18 | ~var17 & var18;
      String var328 = "jk";
      var10003 = (int)3724288278397359134L;
      var18 = -1921980928;
      var17 = var10003;
      var10003 = var17 & ~var18 | ~var17 & var18;
      var18 = -1904809459;
      var17 = var10003;
      var261[var10002] = (char)((var17 | var18) & ~(var17 & var18));
      var18 = -1428038800;
      var17 = 379416518;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = -1132677966;
      var17 = var10002;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var18 = -445084698;
      var17 = 1481290414;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = 223370254;
      var17 = var10003;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      int var10004 = (int)-9083730510800261381L;
      var18 = -484334324;
      var17 = var10004;
      var10004 = var17 + var18 - 2 * (var17 & var18);
      var18 = 1159020035;
      var17 = var10004;
      var18 = (var17 | var18) & (~var17 | ~var18);
      var17 = var10003;
      var10003 = var17 - ~var18 - 1;
      var18 = -1405475064;
      var17 = -636946681;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = 1983004970;
      var17 = var10003;
      var261[var10002] = (char)((var17 | var18) - (var17 & var18));
      var10002 = (int)566564979515654808L;
      var18 = -567504043;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = 68784584;
      var17 = var10002;
      var10002 = (var17 | var18) - (var17 & var18);
      var10003 = (int)3863071427678742732L;
      var18 = -1119222063;
      var17 = var10003;
      var10003 = var17 & ~var18 | ~var17 & var18;
      var18 = 1099623327;
      var17 = var10003;
      var261[var10002] = (char)(var17 & ~var18 | ~var17 & var18);
      var10002 = (int)-1539667642522858001L;
      var18 = -317179817;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var18 = 710620606;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var10003 = (int)-1758479368733346734L;
      var18 = -1047518201;
      var17 = var10003;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = 2019268934;
      var17 = var10003;
      var261[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
      var18 = 521287992;
      var17 = 632487230;
      var10002 = (var17 | var18) - (var17 & var18);
      var18 = 983614465;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = -1104591926;
      var17 = 1759960729;
      var10003 = (var17 | var18) - (var17 & var18);
      var18 = -691033743;
      var17 = var10003;
      var261[var10002] = (char)((var17 | var18) & (~var17 | ~var18));
      var10002 = (int)4355315891308821075L;
      var18 = -677248806;
      var17 = var10002;
      var10002 = (var17 | var18) - (var17 & var18);
      var18 = -1649313151;
      var17 = var10002;
      var10002 = (var17 | var18) - (var17 & var18);
      var18 = -1986668161;
      var17 = 1164888660;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = -855958664;
      var17 = var10003;
      var261[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
      var18 = -885370718;
      var17 = 58859936;
      var10002 = var17 & ~var18 | ~var17 & var18;
      var18 = -927436533;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var10003 = (int)8809925880584458165L;
      var18 = -385393861;
      var17 = var10003;
      var10003 = var17 + var18 - 2 * (var17 & var18);
      var18 = -1194945622;
      var17 = var10003;
      var261[var10002] = (char)((var17 | var18) & ~(var17 & var18));
      char[] var8 = var261;
      var18 = -663120140;
      var17 = 1639993682;
      var10000 = (var17 | var18) & (~var17 | ~var18);
      var18 = -1179007060;
      var17 = var10000;
      char[] var263 = new char[(var17 | var18) & (~var17 | ~var18)];
      var18 = 1917966153;
      var17 = 379292727;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var18 = 1690983294;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = 512229924;
      var17 = -554997746;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = -1067245046;
      var17 = var10003;
      var263[var10002] = (char)(var17 & ~var18 | ~var17 & var18);
      var10002 = (int)7015186951264138678L;
      var18 = 20100769;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var18 = 910115606;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      String var344 = "jl";
      var18 = 1548797345;
      var17 = 266380321;
      var10003 = var17 + var18 - 2 * (var17 & var18);
      var18 = 1404102019;
      var17 = var10003;
      var263[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
      var10002 = (int)5032421689802941026L;
      var18 = 837322024;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var18 = 491022152;
      var17 = var10002;
      var10002 = var17 & ~var18 | ~var17 & var18;
      var18 = 1486493309;
      var17 = 683376690;
      var10003 = (var17 | var18) & ~(var17 & var18);
      var18 = -956575933;
      var17 = var10003;
      var10003 = (var17 | var18) - (var17 & var18);
      var18 = -1791302343;
      var17 = 1155151097;
      var10004 = var17 + var18 - 2 * (var17 & var18);
      var18 = -1337150360;
      var17 = var10004;
      var18 = (var17 | var18) - (var17 & var18);
      var17 = var10003;
      var10003 = (var17 | var18) + (var17 & var18);
      var10003 = (int)8309490489750538163L;
      var18 = 1490113810;
      var17 = var10003;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = 1925223922;
      var17 = var10003;
      var263[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
      var18 = -2027009178;
      var17 = 598481762;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var18 = -1534963705;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var10003 = (int)5559949384842967278L;
      var18 = 466334203;
      var17 = var10003;
      var10003 = (var17 | var18) - (var17 & var18);
      var18 = -277024489;
      var17 = var10003;
      var263[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
      var10002 = (int)5884195707251369755L;
      var18 = -803994724;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var18 = 931964035;
      var17 = var10002;
      var10002 = var17 & ~var18 | ~var17 & var18;
      var10003 = (int)-4949483567158003195L;
      var18 = 279916960;
      var17 = var10003;
      var10003 = var17 & ~var18 | ~var17 & var18;
      var18 = 1564931045;
      var17 = var10003;
      var263[var10002] = (char)((var17 | var18) - (var17 & var18));
      var18 = 882336937;
      var17 = 272117761;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = 615470253;
      var17 = var10002;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var18 = 1301525151;
      var17 = 59343739;
      var10003 = (var17 | var18) & ~(var17 & var18);
      var18 = 1310329200;
      var17 = var10003;
      var263[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
      var18 = 886123142;
      var17 = 30929675;
      var10002 = var17 & ~var18 | ~var17 & var18;
      var18 = 889641355;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = 774967379;
      var17 = -1775876207;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var18 = 36251426;
      var17 = var10003;
      var10003 = (var17 | var18) & (~var17 | ~var18);
      var10004 = (int)7238924968873592596L;
      var18 = -1200417469;
      var17 = var10004;
      var10004 = var17 + var18 - 2 * (var17 & var18);
      var18 = 60684737;
      var17 = var10004;
      var18 = (var17 | var18) & (~var17 | ~var18);
      var17 = var10003;
      var10003 = 2 * (var17 | var18) - (var17 ^ var18);
      var10003 = (int)7380792425325500747L;
      var18 = -615550501;
      var17 = var10003;
      var10003 = (var17 | var18) & ~(var17 & var18);
      var18 = -886804567;
      var17 = var10003;
      var263[var10002] = (char)(var17 & ~var18 | ~var17 & var18);
      var10002 = (int)-5028482411895896066L;
      var18 = -1040103914;
      var17 = var10002;
      var10002 = (var17 | var18) - (var17 & var18);
      var18 = -1381760529;
      var17 = var10002;
      var10002 = (var17 | var18) & ~(var17 & var18);
      var10003 = (int)6965967667560608854L;
      var18 = -629416546;
      var17 = var10003;
      var10003 = var17 + var18 - 2 * (var17 & var18);
      var18 = 1874180592;
      var17 = var10003;
      var263[var10002] = (char)((var17 | var18) & (~var17 | ~var18));
      var18 = -725690760;
      var17 = -786521136;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = 94401952;
      var17 = var10002;
      var10002 = var17 + var18 - 2 * (var17 & var18);
      var18 = 1926146416;
      var17 = 2002937590;
      var10003 = (var17 | var18) & ~(var17 & var18);
      var18 = -1508625727;
      var17 = var10003;
      var10003 = ((var17 | var18) & ~(var17 & var18)) * ((var17 | var18) & ~(var17 & var18));
      var18 = -114713778;
      var17 = -403049949;
      var10003 = var17 + var18 - 2 * (var17 & var18);
      var18 = 517007977;
      var17 = var10003;
      var263[var10002] = (char)((var17 | var18) & ~(var17 & var18));
      var18 = 551223039;
      var17 = -285667563;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var18 = -836505117;
      var17 = var10002;
      var10002 = (var17 | var18) & (~var17 | ~var18);
      var10003 = (int)2197931774393970918L;
      var18 = 1540600401;
      var17 = var10003;
      var10003 = var17 + var18 - 2 * (var17 & var18);
      var18 = -426217084;
      var17 = var10003;
      var263[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
      char[] var9 = var263;
      var18 = -23248165;
      var17 = -1712154705;
      var10000 = (var17 | var18) & (~var17 | ~var18);
      var18 = 1735382388;
      var17 = var10000;

      for (int var10 = (var17 | var18) & ~(var17 & var18); var10 < var6.length; var10++) {
         char var322 = var6[var10];
         char var243 = var8[var10 % var8.length];
         char var123 = var322;
         var7[var10] = (char)((var123 | var243) & ~(var123 & var243));
      }

      char[] var19 = new char[var7.length];
      var18 = -518069264;
      var17 = -1239159674;
      var10000 = (var17 | var18) & ~(var17 & var18);
      var18 = 1463619446;
      var17 = var10000;

      for (int var11 = var17 + var18 - 2 * (var17 & var18); var11 < var6.length; var11++) {
         char var323 = var7[var11];
         char var246 = var9[var11 % var9.length];
         char var126 = var323;
         var19[var11] = (char)(var126 + var246 - 2 * (var126 & var246));
      }

      String var20 = new String(var19);
      int var12 = var20.hashCode();
      var18 = var5;
      var17 = var3;
      var10000 = 2 * (var17 & ~var18) - (var17 ^ var18);
      var18 = var2;
      var17 = var10000;
      int var13 = 2 * (var17 & ~var18) - (var17 ^ var18);
      char[] var14 = var0.toCharArray();
      char[] var15 = new char[var14.length];
      var18 = 455461693;
      var17 = -970823744;
      var10000 = (var17 | var18) & ~(var17 & var18);
      var18 = -586699523;
      var17 = var10000;

      for (int var16 = (var17 | var18) & (~var17 | ~var18); var16 < var15.length; var16++) {
         int var10001 = (int)-2364126536755339020L;
         var18 = 295642830;
         var17 = var10001;
         var10001 = var17 + var18 - 2 * (var17 & var18);
         var18 = -721385928;
         var17 = var10001;
         switch (var16 % ((var17 | var18) & ~(var17 & var18))) {
            case 0:
               var18 = var12;
               var17 = var13;
               var10002 = var17 & ~var18 | ~var17 & var18;
               int var256 = var14[var16];
               var17 = var10002;
               var15[var16] = (char)(var17 + var256 - 2 * (var17 & var256));
               if (a != a) {
                  var256 = -389913308;
                  var17 = -824286867;
                  var10000 = (var17 | var256) & (~var17 | ~var256);
                  var256 = 639370313;
                  var17 = var10000;
                  var10000 = (var17 | var256) & ~(var17 & var256);
                  var256 = 1170266903;
                  var17 = -2127781640;
                  var10001 = (var17 | var256) & ~(var17 & var256);
                  var256 = -991134737;
                  var17 = var10001;
                  var10000 /= (var17 | var256) - (var17 & var256);
               }
               break;
            case 1:
               var18 = var13;
               var17 = var5;
               var10002 = (var17 | var18) - (var17 & var18);
               char var254 = var14[var16];
               var17 = var10002;
               var15[var16] = (char)((var17 | var254) & ~(var17 & var254));
         }
      }

      return new String(var15);
   }

   private static class PartPoseMap {
      Map<Float, float[]> a = new HashMap<>();
      Map<Float, float[]> b = new HashMap<>();
      Map<Float, float[]> c = new HashMap<>();
      public static int d;
      public static boolean e;

      public static String a(String var0, String var1, int var2, int var3, int var4, int var5) {
         char[] var6 = var1.toCharArray();
         char[] var7 = new char[var6.length];
         int var10000 = (int)-3138673716250598927L;
         int var18 = -930145348;
         int var17 = var10000;
         var10000 = (var17 | var18) & (~var17 | ~var18);
         var18 = -1517798841;
         var17 = var10000;
         char[] var236 = new char[var17 & ~var18 | ~var17 & var18];
         int var10002 = (int)-8847519719885963438L;
         var18 = -848686380;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var18 = 763686278;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         int var10003 = (int)-7446045153592988760L;
         var18 = -1996753975;
         var17 = var10003;
         var10003 = var17 + var18 - 2 * (var17 & var18);
         var18 = -1100378029;
         var17 = var10003;
         var236[var10002] = (char)((var17 | var18) - (var17 & var18));
         var18 = 899695278;
         var17 = 1879239820;
         var10002 = (var17 | var18) - (var17 & var18);
         var18 = 1168289315;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var10003 = (int)-7404057603086770627L;
         var18 = 374430288;
         var17 = var10003;
         var10003 = var17 & ~var18 | ~var17 & var18;
         var18 = -1993676824;
         var17 = var10003;
         var236[var10002] = (char)(var17 & ~var18 | ~var17 & var18);
         var10002 = (int)538022524659030220L;
         var18 = 588394042;
         var17 = var10002;
         var10002 = (var17 | var18) - (var17 & var18);
         var18 = -1805125900;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var10003 = (int)3255321746634239308L;
         var18 = -928909225;
         var17 = var10003;
         var10003 = (var17 | var18) - (var17 & var18);
         var18 = 1495086749;
         var17 = var10003;
         var236[var10002] = (char)((var17 | var18) & (~var17 | ~var18));
         var10002 = (int)1089901407488418017L;
         var18 = 1668634377;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var18 = 1526659051;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         String var307 = "bea";
         var18 = 20927284;
         var17 = 4743061;
         var10003 = (var17 | var18) & (~var17 | ~var18);
         var18 = 24614066;
         var17 = var10003;
         var236[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
         var18 = -1206781797;
         var17 = -1912244701;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = 907316924;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         String var309 = "beb";
         var10003 = (int)-2067760370414102959L;
         var18 = -1336765889;
         var17 = var10003;
         var10003 = (var17 | var18) & (~var17 | ~var18);
         var18 = -1199286965;
         var17 = var10003;
         var236[var10002] = (char)((var17 | var18) - (var17 & var18));
         var10002 = (int)902566257709352257L;
         var18 = -136579453;
         var17 = var10002;
         var10002 = (var17 | var18) - (var17 & var18);
         var18 = 1323234247;
         var17 = var10002;
         var10002 = (var17 | var18) - (var17 & var18);
         var10003 = (int)7959931774664333933L;
         var18 = -1271005422;
         var17 = var10003;
         var10003 = (var17 | var18) & ~(var17 & var18);
         var18 = 1024188669;
         var17 = var10003;
         var236[var10002] = (char)(var17 & ~var18 | ~var17 & var18);
         var10002 = (int)-6398016719396105102L;
         var18 = 1141797773;
         var17 = var10002;
         var10002 = (var17 | var18) - (var17 & var18);
         var18 = 1500309497;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var10003 = (int)-2202161295481112760L;
         var18 = 486497415;
         var17 = var10003;
         var10003 = (var17 | var18) & ~(var17 & var18);
         var18 = 944022236;
         var17 = var10003;
         var236[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
         var10002 = (int)8119715244884150184L;
         var18 = -1421054671;
         var17 = var10002;
         var10002 = (var17 | var18) & (~var17 | ~var18);
         var18 = 1810410142;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = 1993644538;
         var17 = -1122970236;
         var10003 = var17 + var18 - 2 * (var17 & var18);
         var18 = -876316580;
         var17 = var10003;
         var236[var10002] = (char)((var17 | var18) & ~(var17 & var18));
         var10002 = (int)3850010121844719217L;
         var18 = -451010182;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = 1529372419;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var18 = -1323675093;
         var17 = 2015857886;
         var10003 = var17 + var18 - 2 * (var17 & var18);
         var18 = -918694234;
         var17 = var10003;
         var236[var10002] = (char)((var17 | var18) & ~(var17 & var18));
         var10002 = (int)-792287764257137213L;
         var18 = -1011019397;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var18 = 539856049;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var18 = 1927605461;
         var17 = -1298087278;
         var10003 = var17 + var18 - 2 * (var17 & var18);
         var18 = -1069274781;
         var17 = var10003;
         var236[var10002] = (char)((var17 | var18) & (~var17 | ~var18));
         char[] var8 = var236;
         var18 = 2123670801;
         var17 = 1669264634;
         var10000 = (var17 | var18) - (var17 & var18);
         var18 = 501895649;
         var17 = var10000;
         char[] var238 = new char[(var17 | var18) & ~(var17 & var18)];
         var10002 = (int)-7517197468908948835L;
         var18 = -288295764;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var18 = 971474481;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var18 = -571769297;
         var17 = -1341364357;
         var10003 = (var17 | var18) & ~(var17 & var18);
         var18 = 1843876212;
         var17 = var10003;
         var238[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
         var10002 = (int)4438833057440835940L;
         var18 = 827471392;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = -413687995;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var10003 = (int)-4567575715642239310L;
         var18 = 1359962550;
         var17 = var10003;
         var10003 = (var17 | var18) & (~var17 | ~var18);
         var18 = 1160213224;
         var17 = var10003;
         var10003 = var17 + var18 - 2 * (var17 & var18);
         var10003 = (int)-8838289669051980201L;
         var18 = -2003991924;
         var17 = var10003;
         var10003 = var17 & ~var18 | ~var17 & var18;
         var18 = 111927512;
         var17 = var10003;
         var238[var10002] = (char)((var17 | var18) & (~var17 | ~var18));
         var18 = -558357008;
         var17 = 909260055;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var18 = -393604891;
         var17 = var10002;
         var10002 = (var17 | var18) - (var17 & var18);
         var18 = -1848663831;
         var17 = -305419311;
         var10003 = (var17 | var18) - (var17 & var18);
         var18 = 955024786;
         var17 = var10003;
         var10003 = (var17 | var18) - (var17 & var18);
         var18 = 1805225488;
         var17 = -1300953759;
         var10003 = var17 & ~var18 | ~var17 & var18;
         var18 = -638841822;
         var17 = var10003;
         var238[var10002] = (char)((var17 | var18) - (var17 & var18));
         var10002 = (int)-8107216161711675995L;
         var18 = -2037440107;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = 1431405619;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = -2141634028;
         var17 = 565262304;
         var10003 = (var17 | var18) & ~(var17 & var18);
         var18 = -1578611210;
         var17 = var10003;
         var238[var10002] = (char)(var17 & ~var18 | ~var17 & var18);
         var18 = -1582985038;
         var17 = -1678548952;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = 978776222;
         var17 = var10002;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var18 = 3752357;
         var17 = 1140055236;
         var10003 = var17 & ~var18 | ~var17 & var18;
         var18 = 1137354017;
         var17 = var10003;
         var238[var10002] = (char)((var17 | var18) & ~(var17 & var18));
         var10002 = (int)1766481264693655893L;
         var18 = 282417020;
         var17 = var10002;
         var10002 = (var17 | var18) & (~var17 | ~var18);
         var18 = -861409748;
         var17 = var10002;
         var10002 = (var17 | var18) & (~var17 | ~var18);
         var18 = 1983605686;
         var17 = -502292920;
         var10003 = (var17 | var18) & ~(var17 & var18);
         var18 = -1808478870;
         var17 = var10003;
         var238[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
         var10002 = (int)1473821980979628161L;
         var18 = 1560516360;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var18 = 729259919;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var18 = -1668584372;
         var17 = 619655530;
         var10003 = (var17 | var18) & (~var17 | ~var18);
         var18 = -208831513;
         var17 = var10003;
         var10003 = ((var17 | var18) - (var17 & var18)) * ((var17 | var18) - (var17 & var18));
         var18 = 79004995;
         var17 = -812865570;
         var10003 = (var17 | var18) - (var17 & var18);
         var18 = -885414492;
         var17 = var10003;
         var238[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
         var18 = 235420662;
         var17 = -231352489;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var18 = -63050586;
         var17 = var10002;
         var10002 = var17 + var18 - 2 * (var17 & var18);
         var10003 = (int)-2589368441469361072L;
         var18 = -568151866;
         var17 = var10003;
         var10003 = var17 & ~var18 | ~var17 & var18;
         var18 = -1010957138;
         var17 = var10003;
         var238[var10002] = (char)((var17 | var18) & (~var17 | ~var18));
         var18 = 225095530;
         var17 = 1848672955;
         var10002 = (var17 | var18) - (var17 & var18);
         var18 = 1666854361;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var18 = -2063056471;
         var17 = -443456003;
         var10003 = (var17 | var18) & ~(var17 & var18);
         var18 = -190214769;
         var17 = var10003;
         var10003 = (var17 | var18) & (~var17 | ~var18);
         var10003 = (int)-1171522577937620788L;
         var18 = -1090669648;
         var17 = var10003;
         var10003 = var17 + var18 - 2 * (var17 & var18);
         var18 = -1588555656;
         var17 = var10003;
         var238[var10002] = (char)(var17 + var18 - 2 * (var17 & var18));
         var18 = -1105813613;
         var17 = -797451090;
         var10002 = var17 & ~var18 | ~var17 & var18;
         var18 = 1851867956;
         var17 = var10002;
         var10002 = (var17 | var18) & ~(var17 & var18);
         var10003 = (int)-3312369268299662391L;
         var18 = -1826688838;
         var17 = var10003;
         var10003 = var17 & ~var18 | ~var17 & var18;
         var18 = -1391642560;
         var17 = var10003;
         var238[var10002] = (char)((var17 | var18) & ~(var17 & var18));
         char[] var9 = var238;
         var10000 = (int)6692374906327999228L;
         var18 = -1955587020;
         var17 = var10000;
         var10000 = var17 + var18 - 2 * (var17 & var18);
         var18 = -2011480376;
         var17 = var10000;

         for (int var10 = var17 + var18 - 2 * (var17 & var18); var10 < var6.length; var10++) {
            char var298 = var6[var10];
            char var221 = var8[var10 % var8.length];
            char var114 = var298;
            var7[var10] = (char)((var114 | var221) & ~(var114 & var221));
         }

         char[] var19 = new char[var7.length];
         var10000 = (int)-886548122796977255L;
         var18 = 1030934002;
         var17 = var10000;
         var10000 = (var17 | var18) & ~(var17 & var18);
         var18 = -854106517;
         var17 = var10000;

         for (int var11 = var17 + var18 - 2 * (var17 & var18); var11 < var6.length; var11++) {
            char var299 = var7[var11];
            char var224 = var9[var11 % var9.length];
            char var117 = var299;
            var19[var11] = (char)((var117 | var224) & (~var117 | ~var224));
         }

         String var20 = new String(var19);
         int var12 = var20.hashCode();
         var18 = var5;
         var17 = var3;
         var10000 = 2 * (var17 & ~var18) - (var17 ^ var18);
         var18 = var2;
         var17 = var10000;
         int var13 = (var17 ^ var18) - 2 * (~var17 & var18);
         char[] var14 = var0.toCharArray();
         char[] var15 = new char[var14.length];
         var10000 = (int)-3115341864545012415L;
         var18 = 104802377;
         var17 = var10000;
         var10000 = (var17 | var18) & ~(var17 & var18);
         var18 = 2145066248;
         var17 = var10000;

         for (int var16 = (var17 | var18) & ~(var17 & var18); var16 < var15.length; var16++) {
            var18 = 473953617;
            var17 = -2118732637;
            int var10001 = var17 + var18 - 2 * (var17 & var18);
            var18 = -1651949072;
            var17 = var10001;
            switch (var16 % ((var17 | var18) & (~var17 | ~var18))) {
               case 0:
                  var18 = var12;
                  var17 = var13;
                  var10002 = (var17 | var18) & ~(var17 & var18);
                  char var234 = var14[var16];
                  var17 = var10002;
                  var15[var16] = (char)(var17 & ~var234 | ~var17 & var234);
                  break;
               case 1:
                  var18 = var13;
                  var17 = var5;
                  var10002 = (var17 | var18) & ~(var17 & var18);
                  char var232 = var14[var16];
                  var17 = var10002;
                  var15[var16] = (char)(var17 + var232 - 2 * (var17 & var232));
            }
         }

         return new String(var15);
      }
   }

   private static class CompiledModel {
      String a;
      boolean b;
      float c;
      Map<String, CosmeticModelRenderer.PartPoseMap> d = new HashMap<>();
      public static int e;
      public static boolean f;

      public static String a(String var0, String var1, int var2, int var3, int var4, int var5) {
         char[] var6 = var1.toCharArray();
         char[] var7 = new char[var6.length];
         int var20 = 315638893;
         int var19 = -319882801;
         int var10000 = (var19 | var20) & ~(var19 & var20);
         var20 = -29442648;
         var19 = var10000;
         char[] var278 = new char[(var19 | var20) - (var19 & var20)];
         int var10002 = (int)7229600682113781411L;
         var20 = -1505894973;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = -1955753120;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = -1664489108;
         var19 = 600120958;
         int var10003 = var19 + var20 - 2 * (var19 & var20);
         var20 = -1089683680;
         var19 = var10003;
         var278[var10002] = (char)(var19 + var20 - 2 * (var19 & var20));
         var10002 = (int)-2944924554085181470L;
         var20 = -1732896724;
         var19 = var10002;
         var10002 = var19 & ~var20 | ~var19 & var20;
         var20 = 400248783;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = 313648854;
         var19 = 1839736662;
         var10003 = (var19 | var20) & ~(var19 & var20);
         var20 = 2132402693;
         var19 = var10003;
         var278[var10002] = (char)((var19 | var20) & (~var19 | ~var20));
         var10002 = (int)-2553920021042439990L;
         var20 = 1699819699;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = -867112837;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = 94316613;
         var19 = 1283799555;
         var10003 = (var19 | var20) - (var19 & var20);
         var20 = 1226453440;
         var19 = var10003;
         var278[var10002] = (char)((var19 | var20) - (var19 & var20));
         var10002 = (int)7869662822727759637L;
         var20 = 193720231;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = 1363335345;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = 242295664;
         var19 = -551183596;
         var10003 = var19 & ~var20 | ~var19 & var20;
         var20 = -633535687;
         var19 = var10003;
         var10003 = ((var19 | var20) & ~(var19 & var20)) * ((var19 | var20) & ~(var19 & var20));
         var20 = 440404837;
         var19 = 1754646410;
         var10003 = var19 + var20 - 2 * (var19 & var20);
         var20 = 1926583548;
         var19 = var10003;
         var278[var10002] = (char)(var19 & ~var20 | ~var19 & var20);
         var10002 = (int)-988648743782911204L;
         var20 = -620470379;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = 2016062605;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var10003 = (int)6311781972774937005L;
         var20 = -1155572631;
         var19 = var10003;
         var10003 = var19 & ~var20 | ~var19 & var20;
         var20 = -1013009242;
         var19 = var10003;
         var10003 = var19 + var20 - 2 * (var19 & var20);
         var20 = -1605911535;
         var19 = 854455335;
         var10003 = var19 + var20 - 2 * (var19 & var20);
         var20 = -1834297069;
         var19 = var10003;
         var278[var10002] = (char)(var19 + var20 - 2 * (var19 & var20));
         var20 = 840521567;
         var19 = -1756231182;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = -1521787224;
         var19 = var10002;
         var10002 = var19 & ~var20 | ~var19 & var20;
         var10003 = (int)-7683398215816126988L;
         var20 = 1093486512;
         var19 = var10003;
         var10003 = var19 + var20 - 2 * (var19 & var20);
         var20 = -1220157498;
         var19 = var10003;
         var278[var10002] = (char)((var19 | var20) & (~var19 | ~var20));
         var10002 = (int)-4976221444587616196L;
         var20 = 1556151515;
         var19 = var10002;
         var10002 = var19 & ~var20 | ~var19 & var20;
         var20 = 1265399009;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var10003 = (int)-5789966798918403457L;
         var20 = 1546955348;
         var19 = var10003;
         var10003 = var19 & ~var20 | ~var19 & var20;
         var20 = 622663992;
         var19 = var10003;
         var278[var10002] = (char)((var19 | var20) - (var19 & var20));
         var10002 = (int)-2872739163643443322L;
         var20 = -1522691063;
         var19 = var10002;
         var10002 = var19 & ~var20 | ~var19 & var20;
         var20 = -543976568;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var10003 = (int)7298197250510434879L;
         var20 = 1666023416;
         var19 = var10003;
         var10003 = (var19 | var20) & ~(var19 & var20);
         var20 = 975534565;
         var19 = var10003;
         var278[var10002] = (char)((var19 | var20) - (var19 & var20));
         var10002 = (int)8661604677697860415L;
         var20 = -1920125047;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = -1568160578;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = 1145572423;
         var19 = -84248386;
         var10003 = var19 & ~var20 | ~var19 & var20;
         var20 = -1095598934;
         var19 = var10003;
         var278[var10002] = (char)((var19 | var20) & ~(var19 & var20));
         var20 = -152847370;
         var19 = 1332697868;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = -1181949709;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var10003 = (int)-2663339864644045301L;
         var20 = 1304084683;
         var19 = var10003;
         var10003 = var19 & ~var20 | ~var19 & var20;
         var20 = 1182527972;
         var19 = var10003;
         var278[var10002] = (char)((var19 | var20) & (~var19 | ~var20));
         char[] var8 = var278;
         var20 = 376131458;
         var19 = 1480067953;
         var10000 = var19 & ~var20 | ~var19 & var20;
         var20 = 1314079993;
         var19 = var10000;
         char[] var280 = new char[(var19 | var20) & ~(var19 & var20)];
         var20 = -285550564;
         var19 = 271633243;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = -20311225;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var10003 = (int)498181936989349876L;
         var20 = -110182009;
         var19 = var10003;
         var10003 = (var19 | var20) & ~(var19 & var20);
         var20 = -223695277;
         var19 = var10003;
         var280[var10002] = (char)((var19 | var20) & (~var19 | ~var20));
         var10002 = (int)1765140810656753676L;
         var20 = -1147062964;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = -1373856447;
         var19 = var10002;
         var10002 = var19 & ~var20 | ~var19 & var20;
         var10003 = (int)-1258974061861483364L;
         var20 = 1806064328;
         var19 = var10003;
         var10003 = var19 + var20 - 2 * (var19 & var20);
         var20 = 1702562592;
         var19 = var10003;
         var10003 = ((var19 | var20) & (~var19 | ~var20)) * ((var19 | var20) & (~var19 | ~var20));
         var20 = 1846377890;
         var19 = -231727414;
         var10003 = (var19 | var20) & ~(var19 & var20);
         var20 = -1673664661;
         var19 = var10003;
         var280[var10002] = (char)(var19 & ~var20 | ~var19 & var20);
         var10002 = (int)-1769158010849201013L;
         var20 = -636349803;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = 1104799260;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = -1154889806;
         var19 = -1186733611;
         var10003 = (var19 | var20) & (~var19 | ~var20);
         var20 = -1821230345;
         var19 = var10003;
         var10003 = (var19 | var20) - (var19 & var20);
         var20 = -1143160929;
         var19 = -184817246;
         var10003 = (var19 | var20) & ~(var19 & var20);
         var20 = 1328009582;
         var19 = var10003;
         var280[var10002] = (char)(var19 + var20 - 2 * (var19 & var20));
         var10002 = (int)2956148751112992222L;
         var20 = -1792708526;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = -530020977;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = 117866613;
         var19 = -153697205;
         var10003 = (var19 | var20) & (~var19 | ~var20);
         var20 = -237994948;
         var19 = var10003;
         var280[var10002] = (char)((var19 | var20) - (var19 & var20));
         var10002 = (int)8696862193104195803L;
         var20 = -920669227;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = -106978550;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = 1727913564;
         var19 = -1336446968;
         var10003 = (var19 | var20) & (~var19 | ~var20);
         var20 = -693463532;
         var19 = var10003;
         var280[var10002] = (char)(var19 + var20 - 2 * (var19 & var20));
         var10002 = (int)3782226928438622219L;
         var20 = 1673148320;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = 1379536814;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = 967305061;
         var19 = -318406749;
         var10003 = var19 + var20 - 2 * (var19 & var20);
         var20 = -727539630;
         var19 = var10003;
         var280[var10002] = (char)((var19 | var20) - (var19 & var20));
         var10002 = (int)4635686576970747804L;
         var20 = -1975963419;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         var20 = -195395713;
         var19 = var10002;
         var10002 = var19 & ~var20 | ~var19 & var20;
         String var396 = "doe";
         var20 = -1596626776;
         var19 = -4571810;
         var10003 = (var19 | var20) - (var19 & var20);
         var20 = 1601163983;
         var19 = var10003;
         var280[var10002] = (char)(var19 & ~var20 | ~var19 & var20);
         var10002 = (int)-330111036135022238L;
         var20 = -758833362;
         var19 = var10002;
         var10002 = var19 + var20 - 2 * (var19 & var20);
         var20 = 679050827;
         var19 = var10002;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = 1261132143;
         var19 = 671884113;
         var10003 = (var19 | var20) & ~(var19 & var20);
         var20 = 1663527430;
         var19 = var10003;
         var280[var10002] = (char)((var19 | var20) & (~var19 | ~var20));
         var20 = -2139007121;
         var19 = 1599239114;
         var10002 = (var19 | var20) & (~var19 | ~var20);
         var20 = -539810643;
         var19 = var10002;
         var10002 = (var19 | var20) - (var19 & var20);
         String var399 = "dof";
         var10003 = (int)4485492409884340523L;
         var20 = -1902651813;
         var19 = var10003;
         var10003 = (var19 | var20) & (~var19 | ~var20);
         var20 = 1800053876;
         var19 = var10003;
         var280[var10002] = (char)((var19 | var20) - (var19 & var20));
         var10002 = (int)-9203909995219369785L;
         var20 = -541023312;
         var19 = var10002;
         var10002 = var19 & ~var20 | ~var19 & var20;
         var20 = 1904796542;
         var19 = var10002;
         var10002 = (var19 | var20) & ~(var19 & var20);
         var10003 = (int)-8044363197903211203L;
         var20 = -592672800;
         var19 = var10003;
         var10003 = (var19 | var20) & ~(var19 & var20);
         var20 = 74203630;
         var19 = var10003;
         var280[var10002] = (char)(var19 + var20 - 2 * (var19 & var20));
         char[] var9 = var280;
         var10000 = (int)4651923801615143655L;
         var20 = -168095548;
         var19 = var10000;
         var10000 = var19 + var20 - 2 * (var19 & var20);
         var20 = 9404963;
         var19 = var10000;

         for (int var10 = var19 + var20 - 2 * (var19 & var20); var10 < var6.length; var10++) {
            var20 = 685477258;
            var19 = -505430586;
            var10000 = (var19 | var20) - (var19 & var20);
            var20 = -132286845;
            var19 = var10000;
            var10000 = ((var19 | var20) & (~var19 | ~var20)) * ((var19 | var20) & (~var19 | ~var20));
            var20 = -291315744;
            var19 = 1193848667;
            var10000 = (var19 | var20) & ~(var19 & var20);
            var20 = -1512528120;
            var19 = var10000;
            int var18 = var19 & ~var20 | ~var19 & var20;
            e = var18;
            var10000 = (int)1808338826435779040L;
            var20 = -1546277454;
            var19 = var10000;
            var10000 = (var19 | var20) & ~(var19 & var20);
            var20 = 584693412;
            var19 = var10000;
            var10000 = (var19 | var20) - (var19 & var20);
            var20 = -913386347;
            var19 = -996362306;
            var10000 = (var19 | var20) - (var19 & var20);
            var20 = 338576917;
            var19 = var10000;
            if (((var19 | var20) & (~var19 | ~var20)) < e) {
               var20 = -1223330096;
               var19 = -1462113033;
               int var10001 = (var19 | var20) & ~(var19 & var20);
               var20 = 533502511;
               var19 = var10001;
               var10001 = (var19 | var20) - (var19 & var20);
               var20 = -1787805391;
               var19 = -388948847;
               var10001 = var19 + var20 - 2 * (var19 & var20);
               var20 = 2107727268;
               var19 = var10001;
               var10001 = var19 & ~var20 | ~var19 & var20;
               var20 = 685630686;
               var19 = 1737215229;
               var10001 = (var19 | var20) & ~(var19 & var20);
               var20 = 1331046446;
               var19 = var10001;
               var10001 = var19 & ~var20 | ~var19 & var20;
               var20 = -1319947621;
               var19 = 219000549;
               var10000 = (var19 | var20) & (~var19 | ~var20);
               var20 = -296440807;
               var19 = var10000;
               var18 = var19 + var20 - 2 * (var19 & var20);
               e = var18;
            }

            char var362 = var6[var10];
            char var260 = var8[var10 % var8.length];
            char var133 = var362;
            var7[var10] = (char)(var133 & ~var260 | ~var133 & var260);
         }

         char[] var21 = new char[var7.length];
         var20 = 988846933;
         var19 = 1584846532;
         var10000 = var19 & ~var20 | ~var19 & var20;
         var20 = 1686523281;
         var19 = var10000;

         for (int var11 = var19 + var20 - 2 * (var19 & var20); var11 < var6.length; var11++) {
            char var363 = var7[var11];
            char var263 = var9[var11 % var9.length];
            char var136 = var363;
            var21[var11] = (char)((var136 | var263) & (~var136 | ~var263));
         }

         String var22 = new String(var21);
         int var12 = var22.hashCode();
         var20 = var5;
         var19 = var3;
         var10000 = var19 + ~var20 + 1;
         var20 = var2;
         var19 = var10000;
         int var13 = (var19 ^ var20) - 2 * (~var19 & var20);
         char[] var14 = var0.toCharArray();
         char[] var15 = new char[var14.length];
         var10000 = (int)2117244116096196620L;
         var20 = -1500767011;
         var19 = var10000;
         var10000 = var19 + var20 - 2 * (var19 & var20);
         var20 = -197182255;
         var19 = var10000;
         int var16 = var19 & ~var20 | ~var19 & var20;
         if (e != e) {
            var10000 = (int)5677348001110675296L;
            var20 = 1316322715;
            var19 = var10000;
            var10000 = var19 + var20 - 2 * (var19 & var20);
            var20 = 369931003;
            var19 = var10000;
            var10000 = (var19 | var20) - (var19 & var20);
            int var304 = (int)5634963560444232342L;
            var20 = -2063156944;
            var19 = var304;
            var304 = var19 & ~var20 | ~var19 & var20;
            var20 = 998914982;
            var19 = var304;
            var10000 /= (var19 | var20) - (var19 & var20);
         }

         for (; var16 < var15.length; var16++) {
            var20 = -24264042;
            var19 = -1434194614;
            int var306 = var19 + var20 - 2 * (var19 & var20);
            var20 = 1410215902;
            var19 = var306;
            switch (var16 % (var19 + var20 - 2 * (var19 & var20))) {
               case 0:
                  var20 = var12;
                  var19 = var13;
                  var10002 = var19 + var20 - 2 * (var19 & var20);
                  char var277 = var14[var16];
                  var19 = var10002;
                  var15[var16] = (char)(var19 & ~var277 | ~var19 & var277);
                  break;
               case 1:
                  var20 = var13;
                  var19 = var5;
                  var10002 = var19 + var20 - 2 * (var19 & var20);
                  char var275 = var14[var16];
                  var19 = var10002;
                  var15[var16] = (char)(var19 + var275 - 2 * (var19 & var275));
            }
         }

         return new String(var15);
      }
   }
}
