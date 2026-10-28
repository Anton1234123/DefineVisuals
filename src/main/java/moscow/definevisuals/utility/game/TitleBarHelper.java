package moscow.definevisuals.utility.game;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.ptr.IntByReference;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.MemoryUtil;

public class TitleBarHelper {
   private static final int DWM_USE_IMMERSIVE_DARK_MODE = 20;

   public static void setDarkTitleBar() {
      applyTitleBarTheme(1);
   }

   public static void setLightTitleBar() {
      applyTitleBarTheme(0);
   }

   public static void setCustomWindowIcon() {
      if (System.getProperty("os.name").toLowerCase().contains("windows")) {
         try {
            Function.getFunction("shell32", "SetCurrentProcessExplicitAppUserModelID")
               .invokeInt(new Object[]{ new WString("Define.DefineVisuals") });
         } catch (Throwable ignored) {
         }
      }

      applyWindowIcon();
   }

   public static void applyWindowIcon() {
      try {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc == null || mc.getWindow() == null) {
            return;
         }
         long windowHandle = mc.getWindow().getHandle();
         if (windowHandle == 0L) {
            return;
         }

         String[] iconPaths = {
            "/assets/definevisuals/icons/window/icon16x16.png",
            "/assets/definevisuals/icons/window/icon32x32.png",
            "/assets/definevisuals/icons/window/icon64x64.png",
            "/assets/definevisuals/icons/window/icon128x128.png"
         };

         List<NativeImage> images = new ArrayList<>();
         for (String path : iconPaths) {
            try (InputStream stream = TitleBarHelper.class.getResourceAsStream(path)) {
               if (stream != null) {
                  NativeImage img = NativeImage.read(stream);
                  if (img != null) {
                     images.add(img);
                  }
               }
            } catch (Throwable ignored) {
            }
         }

         if (images.isEmpty()) {
            return;
         }

         List<ByteBuffer> allocatedBuffers = new ArrayList<>(images.size());
         try (GLFWImage.Buffer glfwImages = GLFWImage.malloc(images.size())) {
            for (int i = 0; i < images.size(); i++) {
               NativeImage img = images.get(i);
               ByteBuffer pixelBuffer = MemoryUtil.memAlloc(img.getWidth() * img.getHeight() * 4);
               allocatedBuffers.add(pixelBuffer);
               pixelBuffer.asIntBuffer().put(img.copyPixelsAbgr());
               glfwImages.position(i);
               glfwImages.width(img.getWidth());
               glfwImages.height(img.getHeight());
               glfwImages.pixels(pixelBuffer);
            }
            glfwImages.position(0);
            GLFW.glfwSetWindowIcon(windowHandle, glfwImages);
         } finally {
            for (ByteBuffer b : allocatedBuffers) {
               MemoryUtil.memFree(b);
            }
            for (NativeImage img : images) {
               img.close();
            }
         }
      } catch (Throwable ignored) {
      }
   }

   private static void applyTitleBarTheme(int themeValue) {
      if (System.getProperty("os.name").toLowerCase().contains("windows")) {
         try {
            long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
            long hwndHandle = GLFWNativeWin32.glfwGetWin32Window(windowHandle);
            HWND hwnd = new HWND(Pointer.createConstant(hwndHandle));
            IntByReference useDarkTheme = new IntByReference(themeValue);
            LPVOID pointerToValue = new LPVOID(useDarkTheme.getPointer());
            DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, new DWORD(20L), pointerToValue, new DWORD(4L));
         } catch (Exception var8) {
         }
      }
   }
}
