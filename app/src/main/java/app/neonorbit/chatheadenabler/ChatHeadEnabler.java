package app.neonorbit.chatheadenabler;

import android.app.Notification;
import android.content.Context;
import android.graphics.Outline;
import android.provider.Settings;
import android.view.View;
import android.view.ViewOutlineProvider;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class ChatHeadEnabler implements IXposedHookLoadPackage {
    public static final String ZALO_PACKAGE = "com.zing.zalo";
    public static final String SYSTEM_UI_PACKAGE = "com.android.systemui";

    @Override
    public void handleLoadPackage(LoadPackageParam param) {
        if (param.packageName.equals(ZALO_PACKAGE)) {
            hookZalo(param);
        } else if (param.packageName.equals(SYSTEM_UI_PACKAGE)) {
            hookSystemUI(param);
        }
    }

    private static void hookZalo(LoadPackageParam param) {
        XposedBridge.log("ZaloChatHead: Initializing hooks for " + param.packageName);

        // 1. Chặn Android 11+ System BubbleMetadata (Ngăn bong bóng vuông của Xiaomi)
        try {
            XposedHelpers.findAndHookMethod(
                Notification.Builder.class,
                "setBubbleMetadata",
                Notification.BubbleMetadata.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam p) {
                        return p.thisObject;
                    }
                }
            );
            XposedBridge.log("ZaloChatHead: Hooked Notification.Builder.setBubbleMetadata successfully");
        } catch (Throwable t) {
            XposedBridge.log("ZaloChatHead: Notification.Builder hook notice: " + t.getMessage());
        }

        try {
            Class<?> compatBuilder = XposedHelpers.findClassIfExists("androidx.core.app.NotificationCompat$Builder", param.classLoader);
            if (compatBuilder != null) {
                Class<?> bubbleMeta = XposedHelpers.findClassIfExists("androidx.core.app.NotificationCompat$BubbleMetadata", param.classLoader);
                if (bubbleMeta != null) {
                    XposedHelpers.findAndHookMethod(compatBuilder, "setBubbleMetadata", bubbleMeta, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam p) {
                            return p.thisObject;
                        }
                    });
                    XposedBridge.log("ZaloChatHead: Hooked NotificationCompat.Builder.setBubbleMetadata successfully");
                }
            }
        } catch (Throwable t) {
            XposedBridge.log("ZaloChatHead: NotificationCompat hook notice: " + t.getMessage());
        }

        // 2. Ép cấp quyền vẽ cửa sổ nổi canDrawOverlays trong tiến trình Zalo
        try {
            XposedHelpers.findAndHookMethod(
                Settings.class,
                "canDrawOverlays",
                Context.class,
                XC_MethodReplacement.returnConstant(true)
            );
            XposedBridge.log("ZaloChatHead: Hooked Settings.canDrawOverlays successfully");
        } catch (Throwable t) {
            XposedBridge.log("ZaloChatHead: canDrawOverlays hook notice: " + t.getMessage());
        }

        // 3. Ép cờ bật ChatHead của Zalo (Lp10/j;->z)
        try {
            Class<?> p10j = XposedHelpers.findClassIfExists("p10.j", param.classLoader);
            if (p10j != null) {
                XposedHelpers.findAndHookMethod(p10j, "z", XC_MethodReplacement.returnConstant(true));
                XposedBridge.log("ZaloChatHead: Hooked p10.j.z (SETTING_ENABLE_CHAT_HEAD_CLIENT) successfully");
            }
        } catch (Throwable t) {
            XposedBridge.log("ZaloChatHead: p10.j.z hook notice: " + t.getMessage());
        }

        // 4. Ép cờ kiểm tra MiniChat (Lgu/n;->a)
        try {
            Class<?> gun = XposedHelpers.findClassIfExists("gu.n", param.classLoader);
            if (gun != null) {
                XposedHelpers.findAndHookMethod(gun, "a", XC_MethodReplacement.returnConstant(true));
                XposedBridge.log("ZaloChatHead: Hooked gu.n.a (MiniChat condition) successfully");
            }
        } catch (Throwable t) {
            XposedBridge.log("ZaloChatHead: gu.n.a hook notice: " + t.getMessage());
        }

        // 5. Ép cờ điều khiển Notification sang MiniChat (Lii/a;->f)
        try {
            Class<?> iia = XposedHelpers.findClassIfExists("ii.a", param.classLoader);
            if (iia != null) {
                XposedHelpers.findAndHookMethod(iia, "f", XC_MethodReplacement.returnConstant(true));
                XposedBridge.log("ZaloChatHead: Hooked ii.a.f (Controller check) successfully");
            }
        } catch (Throwable t) {
            XposedBridge.log("ZaloChatHead: ii.a.f hook notice: " + t.getMessage());
        }
    }

    private static void hookSystemUI(LoadPackageParam param) {
        XposedBridge.log("ZaloChatHead: Initializing fallback hooks for SystemUI");

        String[] targetClasses = new String[] {
            "com.android.systemui.wmshell.bubbles.BadgedImageView",
            "com.android.systemui.bubbles.BadgedImageView",
            "com.android.systemui.wmshell.bubbles.BubbleView",
            "com.android.systemui.bubbles.BubbleView"
        };

        for (String cls : targetClasses) {
            try {
                Class<?> clazz = XposedHelpers.findClassIfExists(cls, param.classLoader);
                if (clazz != null) {
                    XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam p) {
                            applyCircularOutline((View) p.thisObject);
                        }
                    });

                    XposedHelpers.findAndHookMethod(clazz, "onLayout", boolean.class, int.class, int.class, int.class, int.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam p) {
                            View view = (View) p.thisObject;
                            applyCircularOutline(view);
                            view.invalidateOutline();
                        }
                    });
                    XposedBridge.log("ZaloChatHead: Successfully hooked SystemUI class: " + cls);
                }
            } catch (Throwable ignored) {}
        }
    }

    private static void applyCircularOutline(View view) {
        if (view == null) return;
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View v, Outline outline) {
                int size = Math.min(v.getWidth(), v.getHeight());
                if (size > 0) {
                    outline.setOval(0, 0, size, size);
                }
            }
        });
    }
}
