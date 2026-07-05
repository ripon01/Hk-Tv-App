# FTP Player (Android + Android TV)

একটা simple app যেটা আপনার FTP server (`10.251.251.2`, anonymous login) এর ফাইল/ফোল্ডার browse করে
এবং ভিডিও ফাইলে ক্লিক করলে সরাসরি স্ট্রিম করে প্লে করে (ExoPlayer দিয়ে) — ফোন ও Android TV দুটোতেই চলবে।

## এই প্রজেক্টে কী আছে
- `MainActivity` – FTP ফোল্ডার লিস্ট দেখায়, ফোল্ডারে ক্লিক করলে ভেতরে ঢোকে, ভিডিওতে ক্লিক করলে প্লেয়ার খোলে।
- `PlayerActivity` – ExoPlayer (media3) দিয়ে ভিডিও প্লে করে।
- `FtpDataSource` – একটা কাস্টম DataSource যা FTP থেকে সরাসরি স্ট্রিম করে ExoPlayer-কে দেয় (ডাউনলোড না করেই)।
- `FtpRepository` – Apache Commons Net দিয়ে ফোল্ডার লিস্ট করে।
- TV সাপোর্ট: `LEANBACK_LAUNCHER` category, D-pad focusable লিস্ট আইটেম।

## FTP সার্ভার অ্যাড্রেস পরিবর্তন করতে হলে
`app/src/main/res/values/strings.xml` ফাইলে `ftp_host` ভ্যালুটা বদলে দিন।

## কীভাবে APK বানাবেন (Android Studio ছাড়াই — GitHub দিয়ে, ফ্রি)

এই প্রজেক্টে একটা `.github/workflows/build.yml` ফাইল আছে যেটা GitHub-এর সার্ভারকে বলে দেয় কীভাবে APK বানাতে হয়। আপনাকে শুধু কোড GitHub-এ upload করতে হবে, বাকিটা GitHub নিজেই করে দেবে।

**ধাপ:**
1. [github.com](https://github.com)-এ গিয়ে ফ্রি একটা অ্যাকাউন্ট বানান (না থাকলে)।
2. উপরে ডানদিকে **+** আইকনে ক্লিক করে **New repository** সিলেক্ট করুন। নাম দিন যেমন `ftp-player`, **Public** সিলেক্ট করে **Create repository** ক্লিক করুন।
3. এই zip ফাইলটা আপনার কম্পিউটারে **extract/unzip** করুন। ভেতরে `FtpPlayer` নামে একটা ফোল্ডার পাবেন — এর ভেতরের সব ফাইল/ফোল্ডার লাগবে (`app`, `.github`, `build.gradle` ইত্যাদি সব)।
4. GitHub repo পেজে **"uploading an existing file"** লিংকে ক্লিক করুন।
5. আপনার কম্পিউটারের ফাইল এক্সপ্লোরার থেকে `FtpPlayer` ফোল্ডারের **ভেতরের সব ফাইল/ফোল্ডার একসাথে সিলেক্ট করে** GitHub পেজে drag-and-drop করুন (Chrome ব্রাউজারে ফোল্ডার-সহ drag কাজ করে)।
6. নিচে "Commit changes" বাটনে ক্লিক করুন — upload শুরু হবে।
7. Upload শেষ হলে repo-র উপরে **"Actions"** ট্যাবে যান। একটা "Build APK" workflow চলতে দেখবেন (হলুদ 🟡 চিহ্ন = চলছে, সবুজ ✅ = সফল, প্রায় ৩-৫ মিনিট লাগে)।
8. সবুজ ✅ হয়ে গেলে সেই workflow run-এ ক্লিক করুন, নিচে **"Artifacts"** সেকশনে **"FtpPlayer-apk"** নামে একটা zip পাবেন — ডাউনলোড করুন।
9. এই zip খুললে ভেতরে `app-debug.apk` পাবেন — এটাই আপনার আসল APK, ফোন/টিভিতে ইনস্টল করে নিন।

লাল ❌ চিহ্ন (build fail) দেখলে সেই run-এ ক্লিক করে error log-টা কপি করে আমাকে পাঠান, আমি ঠিক করে দেব।

### উপায় ২ (ঐচ্ছিক): Android Studio দিয়ে
1. [Android Studio](https://developer.android.com/studio) ইনস্টল করুন (যদি না থাকে)।
2. এই পুরো `FtpPlayer` ফোল্ডারটা **File > Open** করে খুলুন।
3. প্রথমবার খোলার সময় Android Studio নিজে থেকেই দরকারি Gradle ও dependencies ডাউনলোড করে নেবে (ইন্টারনেট লাগবে)।
4. উপরে মেনু থেকে **Build > Build Bundle(s) / APK(s) > Build APK(s)** ক্লিক করুন।
5. Build শেষ হলে `app/build/outputs/apk/debug/app-debug.apk` ফাইলটা পাবেন — এটাই আপনার APK।

### উপায় ৩ (ঐচ্ছিক): Command line দিয়ে (যদি Android SDK আগে থেকে ইনস্টল করা থাকে)
```bash
cd FtpPlayer
./gradlew assembleDebug
```
(প্রথমবার gradlew রান করার আগে `chmod +x gradlew` করে নিন যদি permission error দেয়।)

## ফোন/টিভিতে ইনস্টল করবেন কীভাবে
- **ফোনে:** APK ফাইলটা ফোনে কপি করে ওপেন করুন (Settings-এ "Install unknown apps" allow করতে হতে পারে)।
- **Android TV-তে:** USB দিয়ে বা ADB দিয়ে sideload করুন:
  ```bash
  adb connect <TV-এর IP address>
  adb install app-debug.apk
  ```
  অথবা TV-তে "Downloader" / "Send Files to TV" জাতীয় অ্যাপ দিয়েও APK ইনস্টল করা যায়।

## গুরুত্বপূর্ণ নোট
- অ্যাপটা ধরে নিচ্ছে FTP সার্ভারে **anonymous login** কাজ করে (username/password লাগে না)। যদি ভবিষ্যতে লাগে, `FtpRepository.kt` ও `FtpDataSource.kt`-এ `client.login("anonymous", "anonymous@")` লাইনটা বদলে আসল username/password দিতে হবে।
- ফোন আর টিভি একই WiFi/নেটওয়ার্কে থাকতে হবে যেখানে `10.251.251.2` অ্যাক্সেসযোগ্য।
- এই কোডটা তৈরি করা হয়েছে কিন্তু Android SDK ছাড়া environment-এ কম্পাইল/টেস্ট করা সম্ভব হয়নি — তাই প্রথমবার build করার পর কোনো ছোটখাটো error দেখা দিলে জানাবেন, আমি ঠিক করে দেব।
