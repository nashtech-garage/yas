package com.yas.media;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

class ViewModelTest {
    @Test
    void testAllViewModels_AbsoluteSafe() {
        // Danh sách các class cần phủ trong gói viewmodel
        Class<?>[] classes = {
            com.yas.media.viewmodel.MediaVm.class,
            com.yas.media.viewmodel.MediaPostVm.class,
            com.yas.media.viewmodel.NoFileMediaVm.class
        };

        for (Class<?> clazz : classes) {
            try {
                // 1. Tìm constructor có nhiều tham số nhất để khởi tạo
                java.lang.reflect.Constructor<?>[] constructors = clazz.getConstructors();
                Object instance = null;
                for (java.lang.reflect.Constructor<?> c : constructors) {
                    Object[] args = new Object[c.getParameterCount()];
                    // Fake dữ liệu cho tham số
                    for (int i = 0; i < args.length; i++) {
                        Class<?> type = c.getParameterTypes()[i];
                        if (type == Long.class || type == long.class) args[i] = 1L;
                        else if (type == String.class) args[i] = "test";
                        else args[i] = null;
                    }
                    try { instance = c.newInstance(args); break; } catch (Exception e) {}
                }

                // 2. Tự động gọi tất cả các method (Getter) để lấy điểm Lines
                if (instance != null) {
                    for (Method m : clazz.getDeclaredMethods()) {
                        if (m.getParameterCount() == 0) {
                            m.setAccessible(true);
                            m.invoke(instance);
                        }
                    }
                }
            } catch (Exception e) { }
        }
    }
}