# ✅ Checklist - Hospedagem em https://mega.mrit.com.br

## 📋 Antes de Subir

### 1. Verificar Arquivos Essenciais
- [ ] `index.html` está na raiz
- [ ] `player.js` está na raiz
- [ ] `service-worker.js` está na raiz
- [ ] `manifest.json` está na raiz
- [ ] `icon-192.png` existe (192x192 pixels)
- [ ] `icon-512.png` existe (512x512 pixels)
- [ ] `vision_logo.png` existe

### 2. Verificar HTTPS
- [ ] Site está acessível via HTTPS
- [ ] Certificado SSL válido
- [ ] Sem avisos de segurança no navegador

### 3. Verificar Manifest.json
- [ ] `start_url` está correto: `/` ou `/index.html`
- [ ] `scope` está correto: `/`
- [ ] Ícones apontam para caminhos corretos: `/icon-192.png` e `/icon-512.png`

### 4. Verificar Service Worker
- [ ] Service Worker está registrado no `player.js`
- [ ] Service Worker está acessível em `/service-worker.js`
- [ ] Service Worker funciona offline

## 🚀 Após Subir

### 1. Testar Acesso
- [ ] Site abre em: https://mega.mrit.com.br
- [ ] Site abre em: https://mega.mrit.com.br/index.html
- [ ] Manifest acessível: https://mega.mrit.com.br/manifest.json
- [ ] Service Worker acessível: https://mega.mrit.com.br/service-worker.js
- [ ] Ícones acessíveis:
  - https://mega.mrit.com.br/icon-192.png
  - https://mega.mrit.com.br/icon-512.png

### 2. Testar PWA
- [ ] Abrir no Chrome/Edge
- [ ] Verificar se aparece opção "Instalar app"
- [ ] Testar instalação como PWA
- [ ] Testar funcionamento offline

### 3. Testar Player
- [ ] Inserir código de display
- [ ] Inserir local da tela
- [ ] Verificar se carrega conteúdo
- [ ] Verificar se cache funciona
- [ ] Testar reprodução de vídeo
- [ ] Testar reprodução de imagem

### 4. Verificar Console
- [ ] Abrir DevTools (F12)
- [ ] Verificar se há erros no Console
- [ ] Verificar se Service Worker está registrado
- [ ] Verificar se cache está funcionando

## 🔧 Configurar GitHub Actions

### 1. Adicionar Secret PWA_URL
- [ ] Ir em: https://github.com/MRITSoftware/player-vision/settings/secrets/actions
- [ ] Criar secret: `PWA_URL`
- [ ] Valor: `https://mega.mrit.com.br`
- [ ] Salvar

### 2. Testar Geração de APK
- [ ] Ir em: https://github.com/MRITSoftware/player-vision/actions
- [ ] Executar workflow "Build APK"
- [ ] Verificar se gera APK corretamente
- [ ] Baixar e testar APK

## 📱 Testar no Dispositivo Android

### 1. Via Navegador
- [ ] Abrir Chrome no Android
- [ ] Acessar: https://mega.mrit.com.br
- [ ] Adicionar à tela inicial
- [ ] Testar como PWA instalado

### 2. Via APK
- [ ] Gerar APK assinado
- [ ] Instalar no dispositivo
- [ ] Testar funcionamento
- [ ] Verificar cache offline

## ⚠️ Problemas Comuns

### Service Worker não registra
- Verificar se está usando HTTPS
- Verificar se o arquivo está acessível
- Verificar console para erros

### Manifest não encontrado
- Verificar caminho no HTML: `<link rel="manifest" href="/manifest.json">`
- Verificar se arquivo existe na raiz
- Verificar permissões do servidor

### Ícones não aparecem
- Verificar se arquivos existem
- Verificar caminhos no manifest.json
- Verificar tamanhos (192x192 e 512x512)

### Cache não funciona
- Verificar Service Worker registrado
- Verificar console para erros
- Limpar cache e testar novamente

## 📝 URLs Importantes

- **Player:** https://mega.mrit.com.br
- **Manifest:** https://mega.mrit.com.br/manifest.json
- **Service Worker:** https://mega.mrit.com.br/service-worker.js
- **Ícone 192:** https://mega.mrit.com.br/icon-192.png
- **Ícone 512:** https://mega.mrit.com.br/icon-512.png

## 🎯 Próximos Passos

1. ✅ Subir arquivos para o servidor
2. ✅ Testar acesso e funcionalidades
3. ✅ Configurar GitHub Secrets
4. ✅ Gerar primeiro APK
5. ✅ Testar APK no dispositivo
