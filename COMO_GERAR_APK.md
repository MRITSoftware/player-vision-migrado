# 📱 Como Gerar APK a partir da URL Hospedada

## 🚀 Opção 1: PWA Builder Online (Mais Fácil)

### Passo a Passo:

1. **Acesse o PWA Builder:**
   - Vá para: https://www.pwabuilder.com/

2. **Insira sua URL:**
   - Cole a URL do seu player hospedado: `https://mega.mrit.com.br`
   - Clique em "Start"

3. **Gere o APK:**
   - Aguarde a análise do PWA
   - Clique em "Build My PWA"
   - Selecione "Android"
   - Configure:
     - Package ID: `com.mritsoftware.player`
     - Nome: `MRIT Player`
     - Orientação: `Any` (adaptável)
   - Clique em "Generate"
   - Baixe o APK gerado

## ⚙️ Opção 2: GitHub Actions Automático

### Configuração:

1. **Adicione a URL como Secret no GitHub:**
   - Vá para: `Settings` > `Secrets and variables` > `Actions`
   - Clique em "New repository secret"
   - Nome: `PWA_URL`
   - Valor: `https://mega.mrit.com.br` (URL do player)
   - Clique em "Add secret"

2. **O workflow já está configurado!**
   - Toda vez que você fizer push, o APK será gerado automaticamente
   - Ou execute manualmente em: `Actions` > `Build APK` > `Run workflow`

3. **Baixe o APK:**
   - Vá para a aba `Actions`
   - Clique no workflow mais recente
   - Baixe o APK na seção `Artifacts`

## 🛠️ Opção 3: Linha de Comando (Local)

### Pré-requisitos:
- Node.js instalado
- PWA Builder CLI instalado

### Comandos:

```bash
# Instalar PWA Builder CLI
npm install -g @pwabuilder/cli

# Gerar APK a partir da URL
pwabuilder android \
  --url https://mega.mrit.com.br \
  --package com.mritsoftware.player \
  --name "MRIT Player" \
  --short-name "MRIT" \
  --display standalone \
  --orientation any \
  --theme-color "#000000" \
  --background-color "#000000" \
  --skipPwaValidation
```

## 📋 Requisitos da URL

Para gerar o APK, sua URL precisa ter:

1. ✅ **HTTPS** (obrigatório para Service Worker)
2. ✅ **manifest.json** acessível em `/manifest.json`
3. ✅ **Ícones** (icon-192.png e icon-512.png)
4. ✅ **Service Worker** registrado

## 🔧 Troubleshooting

### Erro: "Manifest not found"
- Verifique se o `manifest.json` está acessível em `https://sua-url.com/manifest.json`
- Verifique se o caminho está correto no HTML: `<link rel="manifest" href="/manifest.json">`

### Erro: "Service Worker not registered"
- Verifique se o Service Worker está registrado corretamente
- Verifique se está usando HTTPS

### Erro: "Icons not found"
- Adicione os ícones `icon-192.png` e `icon-512.png` na raiz do servidor
- Verifique se estão acessíveis via URL

## 💡 Dica

**Recomendação:** Use a **Opção 1 (PWA Builder Online)** para testes rápidos e a **Opção 2 (GitHub Actions)** para geração automática sempre que houver atualizações.

## 📝 Notas

- O APK gerado não está assinado (para testes)
- Para produção, você precisa assinar o APK
- O APK funciona offline após a primeira instalação (graças ao cache)
- Atualizações do PWA não atualizam o APK automaticamente (precisa reinstalar)
