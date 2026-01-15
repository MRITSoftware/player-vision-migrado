# 🚀 Gerar APK no GitHub - Guia Rápido

## ✅ Sim! Dá para gerar o APK no GitHub sem Android Studio!

O workflow do GitHub Actions já está configurado em `.github/workflows/build-apk.yml`.

## 📋 3 Passos Simples:

### 1️⃣ Subir código para o GitHub

```bash
git init
git add .
git commit -m "Player com Capacitor para APK 24h"
git remote add origin https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
git branch -M main
git push -u origin main
```

### 2️⃣ Aguardar build (5-10 minutos)

O GitHub Actions vai compilar automaticamente quando você fizer push.

### 3️⃣ Baixar o APK

1. Vá em: **Actions** → Workflow mais recente
2. Role até **Artifacts**
3. Baixe **"mrit-player-apk"**
4. Extraia e instale o `app-debug.apk`

## 🎯 Executar Manualmente:

1. Vá em: **Actions** → **Build APK - MRIT Player**
2. Clique em **"Run workflow"**
3. Aguarde e baixe o APK

## 📱 Instalar:

1. Transfira APK para o Android
2. Ative **"Fontes desconhecidas"**
3. Instale tocando no arquivo

## ⚡ Pronto!

Agora você pode gerar APKs sem precisar do Android Studio! 🎉

---

**Documentação completa:**
- `COMO_USAR_GITHUB_ACTIONS.md` - Guia detalhado
- `COMO_GERAR_APK_GITHUB.md` - Documentação completa
