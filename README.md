# Corenya Client

Minecraft **26.3** (Fabric) için istemci tarafı modül altyapısı: modül sistemi, ClickGUI, config/profil
kaydı, HUD modülleri ve kendi çöl-teması title screen'i. Sıfırdan, temiz bir mimariyle kuruldu.

> Kapsam: bu proje **rekabet avantajı / anti-cheat bypass** içermez. Buradaki her şey istemci tarafı
> görselleştirme ve arayüz. Sunucu kurallarına uyun.

## Önizleme

Tasarımın oyunu açmadan görülebilmesi için `preview/Preview.java` içinde küçük bir Java2D mock-up
renderer var; çıktılar `preview/out/` altında:

| Dosya | İçerik |
| --- | --- |
| `preview/out/01-title.png` | Title screen (çöl, piramitler, amblem, butonlar) |
| `preview/out/02-clickgui.png` | ClickGUI (3 kategori, açılmış ayar paneli) |
| `preview/out/03-hud.png` | HUD öğeleri (watermark, coords, FPS, armor, cooldown) |
| `preview/out/04-title-compact.png` | Aynı title screen, 480×270 (GUI scale 4) sıkışık düzeni |

Yeniden üretmek için:

```powershell
javac -d preview/out preview/Preview.java
java -cp preview/out Preview
```

Mod ikonunu (`src/main/resources/assets/corenya/icon.png`) yeniden üretmek için:

```powershell
javac -d preview/out preview/Icon.java
java -cp preview/out Icon
```

`preview/capture.ps1` çalışan oyunun penceresini yakalayıp PNG kaydeder (tasarımı oyun içinde
kontrol etmek için): `powershell -ExecutionPolicy Bypass -File preview/capture.ps1`

## Gereksinimler

| Araç | Sürüm | Not |
| --- | --- | --- |
| JDK | **25** | Minecraft 26.3 Java 25'te çalışır, derleme de 25 hedefler |
| Gradle | 9.8.2 | `gradlew` ile otomatik iner |
| Minecraft | 26.3 | Fabric Loader 0.20.1 |

> Türkiye'deki javalar genelde 21 oluyor; hangi sürümde olduğunu `java -version` ile kontrol et.
> JDK 25: <https://adoptium.net/temurin/releases/?version=25>

## Derleme

```powershell
.\gradlew build
```

Çıktı: `build/libs/corenya-client-0.1.0.jar` → `.minecraft/mods` klasörüne at.

Geliştirme sırasında oyunu doğrudan başlatmak için:

```powershell
.\gradlew runClient
```

## Klasör yapısı

```
src/main/java/com/corenya/
├── Corenya.java              # mod sabitleri, logger, Identifier helper
├── CorenyaClient.java        # entrypoint: keybind, HUD, config, ekran kancaları
├── config/ConfigManager.java # JSON kayıt + profiller
├── module/
│   ├── Module.java           # modül taban sınıfı (onEnable/onTick/onHudRender/onToggle)
│   ├── ModuleManager.java
│   ├── Category.java
│   ├── setting/              # Boolean, Double (slider), Enum, Color
│   ├── hud/                  # Watermark, Coords, Performance, ArmorHUD, Cooldown
│   ├── render/               # Fullbright, Custom FOV
│   └── misc/                 # Notifications (sohbet geri bildirimi)
├── hud/                      # HudAnchor, HudUtil
├── ui/                       # Theme, CorenyaTitleScreen, ClickGuiScreen, Panel, CorenyaButton
└── util/RenderUtil.java      # çizim yardımcıları (rect, circle, ring, glow, triangle)
```

## Kullanım

| Ne | Nasıl |
| --- | --- |
| ClickGUI | `Right Shift` (Keybinds'ten değiştirilebilir) |
| Modül aç/kapat | Panelde sol tık |
| Modül ayarları | Panelde sağ tık |
| Panel taşı | Başlıktan sürükle |
| Panel katla | Başlığa sağ tık |
| Ana menüden aç | Title screen'deki **Corenya** butonu |
| Oyun içinden aç | Pause menüsündeki **Corenya Client** butonu |

Slider'ı sürükleyerek değiştir, enum ayarlarında sağ tık geriye doğru çevirir. Her modül toggle'ında
`Notifications` açıksa sohbete tek satır düşer.

Ayarlar burada tutulur:

```
.minecraft/config/corenya/config.json
.minecraft/config/corenya/profiles/<isim>.json
```

## Yeni modül eklemek

1. Uygun pakette `Module`'ü genişlet:

```java
public class ClockModule extends Module {
	private final BooleanSetting seconds = add(new BooleanSetting("Seconds", "Show seconds", true));

	public ClockModule() {
		super("Clock", "Real time on screen", Category.HUD);
	}

	@Override
	public void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(seconds.get() ? "HH:mm:ss" : "HH:mm");
		graphics.text(Minecraft.getInstance().font, LocalTime.now().format(format), 6, 6, 0xFFEDE6F2, false);
	}
}
```

2. `CorenyaClient#registerModules` içine `modules.register(new ClockModule());` ekle.

Modül adı config anahtarı olduğu için sonradan değiştirmemeye çalış (yoksa eski ayarlar eşleşmez).
HUD dışındaki modüller `onTick()` / `onEnable()` / `onDisable()` kullanır; HUD çizimi sadece
`Category.HUD` için her karede çağrılır.

## Yol haritası

- [x] Modül çatısı + ayar tipleri + JSON kayıt
- [x] ClickGUI (sürükleme, katlama, slider, renk, dar ekran için satır sarma)
- [x] HUD: Watermark, Coords, Performance, ArmorHUD, Cooldown
- [x] Render: Fullbright, Custom FOV · Client: Notifications
- [x] Kendi title screen'i (prosedürel çöl/piramit sahnesi, özgün amblem)
- [ ] Waypoints (`/corenya wp add <isim>`)
- [ ] Kozmetikler (3D karakter, Floating Relics, particles, swing animation)
- [ ] Sürükle-bırak HUD editörü
- [ ] Modül başına tuş atama ekranı
- [ ] Profil başına "load on join"

## Notlar

- `Varsayılan Proje` gibi boşluk/Türkçe karakter içeren yollarda Gradle ara sıra tuhaf davranır.
  Sorun çıkarsa projeyi `C:\Projects\CorenyaClient` gibi sade bir yola taşı.
- Minecraft 26.1'den itibaren obfuscated değil; Yarn mappings yok, Mojang'ın gerçek isimleri kullanılıyor
  (`Minecraft`, `LocalPlayer`, `Identifier` ...) ve Loom'un `net.fabricmc.fabric-loom` eklentisi kullanılıyor.
- 26.x GUI çizimi `GuiGraphicsExtractor` üzerinden yürüyor: `Screen#extractRenderState`,
  `Screen#extractBackground`, `graphics.text(font, ...)`, `graphics.item(...)`. Eski `GuiGraphics`
  ve `drawString` isimleri bu sürümde yok.
