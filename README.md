# PocketFarm

[English](#english) · [Türkçe](#türkçe)

## English

An Android marketplace that connects farmers and buyers directly through product listings, maps, chat and per-kilogram offers.

### Features

- Farmer/buyer accounts, profiles and product listings.
- Location-based discovery with Google Maps.
- Firebase-backed real-time chat and purchase offers.

### Getting started

Clone the project and open it in Android Studio. Register the Android app in Firebase using package `com.tarlamcebimde.app`; put `google-services.json` in `app/`, enable email/password authentication and create Firestore. Enable Maps SDK for Android and replace the API-key placeholder in `AndroidManifest.xml`.

```bash
git clone https://github.com/talhacaglar/PocketFarm.git
```

Sync Gradle, select an emulator or device, then run the `app` configuration. The technical reference includes the Firestore schema and screen flow.

[Detailed technical reference](REFERENCE.md)

## Türkçe

Ürün ilanları, harita, sohbet ve kilogram bazlı tekliflerle çiftçileri ve alıcıları doğrudan buluşturan Android pazar yeri.

### Özellikler

- Çiftçi/alıcı hesapları, profiller ve ürün ilanları.
- Google Maps ile konuma dayalı ürün keşfi.
- Firebase tabanlı gerçek zamanlı sohbet ve satın alma teklifleri.

### Başlangıç

Projeyi klonlayıp Android Studio’da açın. Firebase üzerinde `com.tarlamcebimde.app` paket adıyla Android uygulaması kaydedin; `google-services.json` dosyasını `app/` içine koyun, e-posta/parola girişini etkinleştirin ve Firestore oluşturun. Maps SDK for Android’i etkinleştirip `AndroidManifest.xml` içindeki API anahtarı yer tutucusunu değiştirin.

```bash
git clone https://github.com/talhacaglar/PocketFarm.git
```

Gradle senkronizasyonunu tamamlayın, emülatör veya cihaz seçin ve `app` yapılandırmasını çalıştırın. Firestore şeması ve ekran akışı teknik referanstadır.

[Ayrıntılı teknik referans](REFERENCE.md)
