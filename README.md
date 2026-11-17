# Islomiy kutubxona — Android ilova loyihasi

Uchta asl PDF kitobni bitta ilovada offline o'qish uchun Android Studio loyihasi.

## Funksiyalar
- 3 kitob, jami 757 sahifa, original PDF sahifalari o'zgartirilmagan
- Offline ishlaydi; hech qanday server yoki internet ruxsati yo'q
- Sahifalar bo'yicha navigatsiya va sahifaga tez o'tish
- Har bir kitob uchun oxirgi o'qilgan sahifani eslab qolish
- Xatcho'p (har bir kitob uchun bitta), kattalashtirish va tungi ko'rinish

## APK yig'ish
1. Android Studio (JDK 17 bilan) o'rnating.
2. `IslomiyKutubxona` papkasini **Open** orqali oching.
3. Android SDK Platform 35 va Android Gradle Plugin 8.6.1 yuklanishiga ruxsat bering.
4. **Build > Build Bundle(s) / APK(s) > Build APK(s)** ni bosing.
5. Natija: `app/build/outputs/apk/debug/app-debug.apk`.

## Muhim
Bu arxiv tayyor APK emas, balki APK yaratish uchun to'liq manba loyihasidir. Ushbu muhitda Android SDK va Gradle mavjud bo'lmagani uchun APK yig'ib sinab ko'rilmadi. Android 6.0+ uchun mo'ljallangan. PDF kontent huquqlarini tarqatishdan oldin tekshiring.
