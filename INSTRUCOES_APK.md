# 📱 Como Gerar APK do MRIT Player

## 🚀 Opção 1: Gerar APK Automaticamente no GitHub (Recomendado)

### Passo 1: Subir o código para o GitHub

```bash
# Inicializar repositório Git (se ainda não tiver)
git init

# Adicionar arquivos
git add .

# Fazer commit
git commit -m "Initial commit - MRIT Player"

# Adicionar repositório remoto
git remote add origin https://github.com/MRITSoftware/player-vision.git

# Fazer push
git branch -M main
git push -u origin main
```

### Passo 2: Configurar GitHub Actions

1. O workflow já está configurado em `.github/workflows/build-apk.yml`
2. Quando você fizer push para `main` ou `master`, o APK será gerado automaticamente
3. Acesse a aba **Actions** no GitHub para ver o progresso
4. Baixe o APK na seção **Artifacts** após o build completar

### Passo 3: Baixar o APK

1. Vá para: `https://github.com/MRITSoftware/player-vision/actions`
2. Clique no workflow mais recente
3. Role até a seção **Artifacts**
4. Baixe `mrit-player-apk`
5. Extraia e instale o APK no dispositivo Android

## 🛠️ Opção 2: Gerar APK Localmente

### Pré-requisitos

- Node.js 18+ instalado
- Java JDK 17+ instalado
- Android SDK instalado (ou usar Android Studio)

### Passo 1: Instalar dependências

```bash
npm install
```

### Passo 2: Criar ícones (se não tiver)

Você precisa de dois ícones:
- `icon-192.png` (192x192 pixels)
- `icon-512.png` (512x512 pixels)

Você pode usar o `vision_logo.png` como base ou criar novos ícones.

### Passo 3: Gerar APK

**Windows:**
```bash
build-apk.bat
```

**Linux/Mac:**
```bash
chmod +x build-apk.sh
./build-apk.sh
```

**Ou manualmente:**
```bash
npm run build
npm install -g @pwabuilder/cli
pwabuilder android --manifest ./manifest.json --package com.mritsoftware.player --name "MRIT Player" --short-name "MRIT" --display standalone --orientation landscape --theme-color "#000000" --background-color "#000000" --skipPwaValidation
```

## 📦 Opção 3: Usar PWA Builder Online

1. Acesse: https://www.pwabuilder.com/
2. Insira a URL do seu player hospedado
3. Clique em "Build My PWA"
4. Selecione "Android"
5. Baixe o APK gerado

## ⚙️ Configurações do APK

O APK está configurado com:
- **Package ID:** `com.mritsoftware.player`
- **Nome:** MRIT Player
- **Orientação:** Any (adaptável - horizontal ou vertical)
- **Tema:** Preto (#000000)
- **Display:** Standalone (sem barra do navegador)

## 🔧 Troubleshooting

### Erro: "Ícones não encontrados"
- Adicione `icon-192.png` e `icon-512.png` na raiz do projeto
- Ou use o `vision_logo.png` como base

### Erro: "Java não encontrado"
- Instale Java JDK 17 ou superior
- Configure a variável de ambiente `JAVA_HOME`

### Erro: "Android SDK não encontrado"
- Instale o Android Studio
- Ou configure manualmente o Android SDK
- Ou use o PWA Builder online (Opção 3)

### APK não instala no dispositivo
- Verifique se "Fontes desconhecidas" está habilitado nas configurações do Android
- Verifique se o dispositivo suporta a arquitetura do APK (arm64-v8a, armeabi-v7a, x86, x86_64)

## 📝 Notas Importantes

1. **URL do PWA:** O PWA Builder precisa de uma URL pública para funcionar corretamente. Se estiver gerando localmente, você pode:
   - Hospedar temporariamente em um servidor
   - Usar ngrok para criar um túnel: `ngrok http 5173`
   - Usar o GitHub Pages para hospedar

2. **Assinatura:** O APK gerado não está assinado. Para produção, você precisa assinar o APK com uma chave.

3. **Atualizações:** O APK não atualiza automaticamente. Você precisa gerar um novo APK e reinstalar quando houver atualizações.

## 🎯 Próximos Passos

1. Configure o GitHub Actions para gerar APKs automaticamente
2. Configure releases automáticas quando criar tags
3. Considere usar Google Play Console para distribuição
4. Configure assinatura de APK para produção
