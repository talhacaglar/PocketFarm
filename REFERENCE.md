# 🌾 Tarlam Cebimde

**Tarımsal Ürün Pazar Yeri Mobil Uygulaması**

Çiftçiler ve alıcıları aracısız bir şekilde buluşturan, tarımsal ürünlerin doğrudan ticaretini sağlayan Android mobil uygulaması.

> *"Tarladan Sofraya, Aracısız!"*

---

## 📱 Özellikler

### 🛒 Ürün Yönetimi
- Ürün ekleme (fotoğraf, fiyat/kg, stok kg, kategori, konum)
- **En ucuz ürün sıralaması** (fiyata göre artan)
- Kategori filtresi (Meyve, Sebze, Tahıl, Baklagil, Süt Ürünleri, Diğer)
- Ürün detay sayfası

### 🗺️ Harita Entegrasyonu
- Google Maps üzerinde tüm ürün konumları
- **Marker'a tıklayınca veritabanından ürün bilgisi** gösterimi
- Ürün eklerken haritadan konum seçimi
- Ürün detayında gömülü harita

### 💬 Gerçek Zamanlı Mesajlaşma
- Alıcı ve satıcı arasında anlık chat
- Sohbet listesi (son mesaj önizlemeli)
- Mesaj tipleri: Metin, Teklif, Sistem

### 💰 Fiyat Teklifi Sistemi
- Alıcıdan satıcıya fiyat/kg + kilogram teklifi
- Teklif durumları: Beklemede / Kabul / Red
- Teklifler chat'e otomatik entegre

### 👤 Profil Yönetimi
- Profil fotoğrafı yükleme/değiştirme
- E-posta ve şifre güncelleme
- Ürünlerim, Tekliflerim, Mesajlarıma hızlı navigasyon

### 🔐 Yönetici Paneli (Admin)
- Kullanıcı listeleme ve silme
- Ürün listeleme ve silme
- Teklif görüntüleme ve yönetme
- Tam CRUD işlemleri

---

## 🏗️ Mimari

```
MVVM + Repository Pattern
```

```
com.tarlamcebimde.app/
├── model/          → Veri modelleri (User, Product, Chat, Message, Offer)
├── repository/     → Veritabanı işlemleri (Auth, User, Product, Chat, Offer)
├── viewmodel/      → UI-veri köprüsü
├── ui/
│   ├── splash/     → Açılış ekranı
│   ├── auth/       → Giriş & Kayıt
│   ├── main/       → Ana sayfa, Harita, Mesajlar, Profil
│   ├── product/    → Ürün detay & ekleme
│   ├── chat/       → Mesajlaşma
│   ├── offer/      → Teklif dialogu
│   ├── profile/    → Profil düzenleme, Ürünlerim, Tekliflerim
│   └── admin/      → Yönetici paneli
├── adapter/        → RecyclerView adapter'ları
└── util/           → Yardımcı sınıflar (Constants, FirebaseHelper)
```

---

## 🛠️ Teknolojiler

| Teknoloji | Kullanım |
|-----------|----------|
| **Java** | Uygulama dili |
| **Firebase Auth** | Kimlik doğrulama (E-posta/Şifre) |
| **Firebase Firestore** | Bulut veritabanı (NoSQL) |
| **Firebase Storage** | Fotoğraf depolama |
| **Google Maps SDK** | Harita ve konum |
| **Material Design 3** | Modern UI bileşenleri |
| **Glide** | Görsel yükleme ve önbellekleme |
| **CircleImageView** | Profil fotoğrafı görünümü |

---

## 📊 Veritabanı Yapısı (Firestore)

```
├── users/{userId}
│   ├── email, fullName, phone, role, city, district
│   └── profileImageUrl, createdAt
│
├── products/{productId}
│   ├── title, description, category, pricePerKg, availableKg
│   ├── imageUrls[], sellerId, sellerName
│   └── location: { lat, lng, city, district, address }
│
├── chats/{chatId}
│   ├── participants[], participantNames, productTitle
│   ├── lastMessage, lastMessageTime
│   └── messages/{messageId} (subcollection)
│       └── senderId, text, timestamp, type
│
└── offers/{offerId}
    ├── productId, buyerId, sellerId
    ├── offeredPricePerKg, requestedKg, totalPrice
    └── status (pending/accepted/rejected)
```

---

## 🚀 Kurulum

### Gereksinimler
- Android Studio (Arctic Fox veya üzeri)
- JDK 8+
- Firebase hesabı
- Google Cloud Console hesabı (Maps API için)

### Adımlar

1. **Projeyi klonlayın**
   ```bash
   git clone https://github.com/talhacaglar/PocketFarm.git
   ```

2. **Firebase kurulumu**
   - [Firebase Console](https://console.firebase.google.com/) üzerinden yeni proje oluşturun
   - Android uygulaması ekleyin (paket adı: `com.tarlamcebimde.app`)
   - `google-services.json` dosyasını indirip `app/` dizinine yerleştirin
   - Authentication → E-posta/Şifre yöntemini etkinleştirin
   - Firestore Database oluşturun

3. **Google Maps API**
   - [Google Cloud Console](https://console.cloud.google.com/) üzerinden Maps SDK for Android'i etkinleştirin
   - API anahtarı oluşturun
   - `AndroidManifest.xml` içindeki `YOUR_GOOGLE_MAPS_API_KEY_HERE` kısmını güncelleyin

4. **Android Studio'da açın ve çalıştırın**
   ```
   Run → Select Device → Run 'app'
   ```

---

## 📸 Ekran Akışı

```
Splash → Giriş/Kayıt → Ana Sayfa (4 Tab)
                            ├── 🏠 Home (Ürün Listesi)
                            ├── 🗺️ Harita (Ürün Konumları)
                            ├── 💬 Mesajlar (Sohbet Listesi)
                            └── 👤 Profil
                                 ├── Profil Düzenle
                                 ├── Ürünlerim
                                 ├── Tekliflerim
                                 └── Mesajlarım

Ürün Detay → Mesaj Gönder / Teklif Ver
Admin Girişi → Yönetici Paneli (Kullanıcı/Ürün/Teklif CRUD)
```

---

## 👥 Kullanıcı Rolleri

| Rol | Yetkiler |
|-----|----------|
| **Alıcı** | Ürün görüntüleme, mesaj gönderme, teklif verme |
| **Satıcı** | Ürün ekleme/düzenleme, teklif kabul/red, mesajlaşma |
| **Yönetici** | Tüm kayıtları görüntüleme, ekleme, güncelleme, silme |

---

## 📋 Ders Bilgileri

- **Ders:** BIL056 Mobil Programlama
- **Dönem:** 2025-2026 Bahar
- **Konu:** Tarım alanında gerçek dünya problemi çözümü

---

## 📄 Lisans

Bu proje eğitim amaçlıdır.
