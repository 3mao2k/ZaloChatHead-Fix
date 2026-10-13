# ZaloChatHead - Xposed Module
Khôi phục Bong bóng chat tròn xoe (Chat Head) cho Zalo và bo tròn bong bóng hệ thống trên Xiaomi MIUI / HyperOS.

## Tính năng
1. **Lớp 1 (Zalo Process):** Ép Zalo bỏ Android 11 Bubbles (bong bóng vuông có dấu `+` của Xiaomi) và kích hoạt lại cơ chế `MiniChat` (Chat Head tròn xoe nguyên bản trôi nổi tự do).
2. **Lớp 2 (Đường lui - SystemUI Process):** Tự động gọt toàn bộ icon bong bóng hệ thống của Xiaomi thành hình tròn xoe (`Oval outline`), đảm bảo không còn bị vuông bo góc.

## Cách Build APK qua GitHub Actions (Nhanh nhất & Miễn phí)
1. Đăng nhập [GitHub](https://github.com) và tạo một Repository mới (đặt tên ví dụ: `ZaloChatHead`).
2. Tải toàn bộ thư mục này (hoặc giải nén từ file `ZaloChatHead-source.zip`) lên repository đó.
3. Vào tab **Actions** trên GitHub repository của bạn:
   - Bạn sẽ thấy tiến trình **Build APK** đang tự động chạy.
   - Chờ khoảng 1 - 2 phút khi hiện dấu tích xanh ✅, bấm vào bản build.
   - Kéo xuống mục **Artifacts** và tải về file **`ZaloChatHead-Debug`** (file APK đã build sẵn).
4. Cài đặt file APK vào điện thoại.

## Cách Kích Hoạt trong LSPosed
1. Mở **LSPosed Manager**.
2. Tìm đến module **ZaloChatHead**.
3. Bật công tắc module và tích chọn:
   - ✅ **Zalo** (`com.zing.zalo`)
   - ✅ **Giao diện hệ thống** (`com.android.systemui`) *(Đường lui)*
4. Khởi động lại Zalo hoặc khởi động lại điện thoại để tận hưởng bong bóng chat tròn xoe!
