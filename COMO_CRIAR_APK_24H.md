# 📱 Como Criar APK para Player Fullscreen 24h

Este guia explica como criar um APK que roda o player em **fullscreen 24h** sem a tela desligar.

## 🎯 O que este APK faz:

- ✅ **Fullscreen permanente** (modo immersive)
- ✅ **Tela sempre ligada** (não entra em sleep/standby)
- ✅ **Orientação landscape** fixa
- ✅ **Esconde barras de navegação** do Android
- ✅ **Reativa fullscreen** automaticamente se sair
- ✅ **Funciona offline** após primeira instalação

## 🚀 Método 1: Build Automático (Recomendado)

### Pré-requisitos:

1. **Node.js 18+** instalado
2. **Android Studio** instalado (para compilar o APK)
3. **Java JDK 17+** (vem com Android Studio)

### Passo a Passo:

#### 1. Instalar dependências:

```bash
npm install
```

#### 2. Gerar APK:

**Windows:**
```bash
build-apk-capacitor.bat
```

**Linux/Mac:**
```bash
chmod +x build-apk-capacitor.sh
./build-apk-capacitor.sh
```

#### 3. Abrir no Android Studio:

```bash
npx cap open android
```

#### 4. Compilar APK no Android Studio:

1. No Android Studio, vá em: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Aguarde a compilação
3. O APK estará em: `android/app/build/outputs/apk/debug/app-debug.apk`

#### 5. Instalar no dispositivo:

- Conecte o dispositivo Android via USB
- Ative "Depuração USB" nas configurações do dispositivo
- Arraste o APK para o dispositivo ou use: `adb install app-debug.apk`

## 🛠️ Método 2: Build via Linha de Comando (Avançado)

### Pré-requisitos adicionais:

- Android SDK configurado
- Variáveis de ambiente `ANDROID_HOME` e `JAVA_HOME` configuradas

### Comandos:

```bash
# 1. Build do projeto web
npm run build

# 2. Sincronizar com Capacitor
npx cap sync android

# 3. Compilar APK (requer Gradle)
cd android
./gradlew assembleDebug
cd ..

# APK estará em: android/app/build/outputs/apk/debug/app-debug.apk
```

## ⚙️ Configurações do APK

### Package ID:
- `com.mritsoftware.player`

### Nome do App:
- `MRIT Player`

### Orientação:
- **Landscape** (horizontal) fixa

### Permissões:
- ✅ Internet
- ✅ Wake Lock (manter tela ligada)
- ✅ Desabilitar Keyguard (desbloquear tela)

## 🔧 Configurações Avançadas

### Modo Kiosk Completo (Opcional)

Para um modo kiosk verdadeiro (impedir saída do app):

#### Opção A: Usar App de Kiosk
1. Instale um app de kiosk como "Kiosk Browser" ou "SureLock"
2. Configure para abrir apenas o MRIT Player

#### Opção B: Device Owner (Requer reset do dispositivo)
```bash
# Via ADB (requer dispositivo resetado)
adb shell dpm set-device-owner com.mritsoftware.player/.DeviceAdminReceiver
```

#### Opção C: Launcher Padrão
1. Configure o MRIT Player como launcher padrão
2. O usuário não conseguirá sair sem configurar outro launcher

### Manter Tela Ligada (Já implementado)

O APK já mantém a tela ligada usando:
- **Keep Awake Plugin** do Capacitor
- **Wake Lock API** nativa do Android
- **FLAG_KEEP_SCREEN_ON** no MainActivity

### Fullscreen Immerisve (Já implementado)

O APK já entra em fullscreen immersive usando:
- **SYSTEM_UI_FLAG_IMMERSIVE_STICKY**
- **SYSTEM_UI_FLAG_HIDE_NAVIGATION**
- **SYSTEM_UI_FLAG_FULLSCREEN**

## 📋 Checklist de Build

Antes de gerar o APK, verifique:

- [ ] Node.js instalado
- [ ] Dependências instaladas (`npm install`)
- [ ] Ícones criados (`icon-192.png` e `icon-512.png`)
- [ ] Build do projeto funciona (`npm run build`)
- [ ] Android Studio instalado
- [ ] Java JDK configurado

## 🔍 Troubleshooting

### Erro: "Capacitor CLI não encontrado"
```bash
npm install -g @capacitor/cli
```

### Erro: "Gradle não encontrado"
- Abra o projeto no Android Studio
- O Android Studio baixará o Gradle automaticamente

### Erro: "SDK não encontrado"
- Abra o Android Studio
- Vá em: **Tools** → **SDK Manager**
- Instale o Android SDK necessário

### APK não mantém tela ligada
- Verifique se as permissões estão no AndroidManifest.xml
- Verifique se o MainActivity.java está aplicando FLAG_KEEP_SCREEN_ON

### Fullscreen não funciona
- Verifique se o MainActivity.java está aplicando SYSTEM_UI_FLAG_IMMERSIVE_STICKY
- Teste em dispositivo físico (emulador pode ter limitações)

### App fecha sozinho
- Verifique logs: `adb logcat | grep -i "mrit"`
- Verifique se há erros JavaScript no console

## 📝 Assinatura do APK (Produção)

Para distribuir o APK, você precisa assiná-lo:

### 1. Gerar keystore:
```bash
keytool -genkey -v -keystore mrit-player.keystore -alias mrit-player -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configurar no Android Studio:
1. **Build** → **Generate Signed Bundle / APK**
2. Selecione o keystore
3. Configure a senha
4. Gere o APK assinado

### 3. Ou via linha de comando:
```bash
cd android
./gradlew assembleRelease
# O APK assinado estará em: app/build/outputs/apk/release/
```

## 🎯 Próximos Passos

1. ✅ Testar o APK em dispositivo físico
2. ✅ Verificar se a tela não desliga após 24h
3. ✅ Verificar se fullscreen funciona corretamente
4. ✅ Configurar modo kiosk (se necessário)
5. ✅ Assinar APK para produção
6. ✅ Distribuir via Google Play ou instalação direta

## 💡 Dicas

- **Teste sempre em dispositivo físico** (emuladores podem ter limitações)
- **Mantenha o dispositivo conectado à energia** para uso 24h
- **Configure brilho automático** para economizar energia
- **Use modo "Não perturbe"** para evitar notificações
- **Desative atualizações automáticas** do Android

## 📞 Suporte

Se encontrar problemas:
1. Verifique os logs: `adb logcat`
2. Verifique o console do navegador (via Chrome DevTools remoto)
3. Verifique se todas as dependências estão instaladas

---

**Versão:** 1.0.0  
**Última atualização:** 2025-01-27
