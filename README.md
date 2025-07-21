# login_jwt

## Proje Özeti
Bu proje, Spring Boot ile JWT tabanlı kimlik doğrulama ve kullanıcı detayları (user_details) için tam CRUD işlemlerini içeren bir REST API'dir. Amaç, gerçek bir veritabanı ile çalışan, güvenli ve temiz mimariye uygun bir kullanıcı yönetim sistemi geliştirmekti.

## Geliştirme Sürecinde Karşılaşılan Sorunlar ve Çözümler

### 1. **In-Memory Repository'den Gerçek Veritabanına Geçiş**
- **Sorun:** Başlangıçta repository sınıfları sadece bellekte çalışıyordu, veriler kalıcı değildi.
- **Çözüm:** Spring Data JPA kullanılarak repository'ler gerçek PostgreSQL veritabanına bağlandı. Entity'lere JPA annotation'ları eklendi.

### 2. **Entity Tanıma Hatası**
- **Sorun:** `UserDetails` entity'si JPA tarafından tanınmıyordu (`Not a managed type` hatası).
- **Çözüm:** `@Entity` ve `@Table` annotation'ları eklendi, ana uygulama sınıfına `@EntityScan` ile entity'lerin taranacağı paket belirtildi.

### 3. **JWT Kütüphanesi ve Java 17 Uyumsuzluğu**
- **Sorun:** Eski `jjwt` kütüphanesi Java 17 ile uyumlu değildi.
- **Çözüm:** `jjwt` kütüphanesi 0.12.3 sürümüne yükseltildi, kodlar yeni API'ye göre güncellendi.

### 4. **Kullanıcı ve UserDetails ID Uyuşmazlığı**
- **Sorun:** `users` tablosundaki id ile `user_details` tablosundaki userId alanı arasında kayma oluyordu.
- **Çözüm:** UserDetails kaydı eklenirken userId, JWT token'dan çıkarılan id ile eşleştirildi. Manuel veritabanı müdahalelerinden kaçınıldı.

### 5. **HTTP Status Kodları ve Hata Mesajları**
- **Sorun:** Endpoint'lerde doğru HTTP kodları ve açıklayıcı hata mesajları dönmüyordu.
- **Çözüm:** Tüm controller'larda 200, 201, 204, 400, 401, 404, 409 gibi kodlar ve açıklayıcı mesajlar eklendi. Validasyon için `@Valid` ve özel hata mesajları kullanıldı.

### 6. **Validasyon ve Kullanıcı Deneyimi**
- **Sorun:** Adres boş, telefon 10 haneli değil, doğum tarihi geçmişte değil gibi hatalar kullanıcıya net iletilmiyordu.
- **Çözüm:** DTO'larda validasyon annotation'ları (`@NotBlank`, `@Pattern`, `@Past`) kullanıldı. Gerekirse global ExceptionHandler ile daha okunabilir hata mesajları sağlanabilir.

### 7. **Maven ve Spring Boot Plugin Sorunu**
- **Sorun:** `mvn spring-boot:run` komutu çalışmıyordu, plugin bulunamıyordu.
- **Çözüm:** `pom.xml` dosyası kontrol edildi, Spring Boot Maven plugin'i ve dependency yönetimi düzeltildi.

## Proje Mimarisi
- **Katmanlar:** Domain, Application, Infrastructure, Presentation
- **Güvenlik:** JWT tabanlı authentication, tüm user_details işlemleri token ile korunur.
- **Veritabanı:** PostgreSQL, Spring Data JPA ile erişim.
- **Validasyon:** Jakarta Validation API ile DTO seviyesinde.

## Kullanım
1. Java 17 ve Maven kurulu olmalı.
2. PostgreSQL ayarlarını `application.properties` dosyasında yap.
3. Proje dizininde:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```
4. API endpoint'leri ve örnek istekler için kısa bir özet:
   - **POST /register**: Kullanıcı kaydı
   - **POST /login**: Giriş, JWT token al
   - **POST/GET/PUT/DELETE /user/details**: Kullanıcı detayları CRUD (JWT zorunlu)

## Sonuç
Bu proje, gerçek hayatta karşılaşılabilecek birçok Spring Boot, JPA, JWT ve REST API sorununu çözerek, temiz ve sürdürülebilir bir mimariyle tamamlanmıştır.

---