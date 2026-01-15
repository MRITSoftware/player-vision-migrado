# ⚡ Instruções Rápidas - APK 24h

## 🚀 Build Rápido (3 passos)

### 1. Instalar dependências:
```bash
npm install
```

### 2. Executar script de build:

**Windows:**
```bash
build-apk-capacitor.bat
```

**Linux/Mac:**
```bash
chmod +x build-apk-capacitor.sh
./build-apk-capacitor.sh
```

### 3. Abrir no Android Studio e compilar:
```bash
npx cap open android
```

No Android Studio: **Build** → **Build APK(s)**

---

## ✅ O que está configurado:

- ✅ **Tela sempre ligada** (Wake Lock)
- ✅ **Fullscreen immersive** (esconde barras)
- ✅ **Orientação landscape** fixa
- ✅ **Reativa fullscreen** automaticamente
- ✅ **Mantém tela ligada** mesmo sem interação

---

## 📱 Instalar no dispositivo:

1. Conecte via USB
2. Ative "Depuração USB"
3. Arraste o APK para o dispositivo
4. OU: `adb install app-debug.apk`

---

## 🔧 Troubleshooting Rápido:

**Erro: "Capacitor não encontrado"**
```bash
npm install -g @capacitor/cli
```

**Erro: "Gradle não encontrado"**
- Abra no Android Studio (ele baixa automaticamente)

**Tela desliga mesmo assim**
- Verifique se o dispositivo está conectado à energia
- Verifique configurações de economia de energia do Android

---

📖 **Documentação completa:** Veja `COMO_CRIAR_APK_24H.md`
