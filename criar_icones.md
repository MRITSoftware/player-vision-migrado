# 🎨 Como Criar os Ícones Necessários

## Problema
O PWA Builder precisa dos ícones `icon-192.png` e `icon-512.png` acessíveis no servidor.

## Solução Rápida

### Opção 1: Usar o vision_logo.png como base

Se você tem o `vision_logo.png`, você pode:

1. **Usar um editor online:**
   - Acesse: https://www.iloveimg.com/resize-image
   - Faça upload do `vision_logo.png`
   - Redimensione para 192x192 pixels
   - Salve como `icon-192.png`
   - Repita para 512x512 pixels → `icon-512.png`

2. **Usar Photoshop/GIMP:**
   - Abra o `vision_logo.png`
   - Redimensione para 192x192 (salve como `icon-192.png`)
   - Redimensione para 512x512 (salve como `icon-512.png`)

### Opção 2: Criar ícones simples

Se não tiver logo, você pode criar ícones simples:

1. Use um gerador online:
   - https://www.favicon-generator.org/
   - https://realfavicongenerator.net/
   - Faça upload de qualquer imagem
   - Gere os tamanhos necessários

### Opção 3: Usar imagem temporária

Crie ícones temporários usando qualquer imagem quadrada:
- 192x192 pixels → `icon-192.png`
- 512x512 pixels → `icon-512.png`

## 📋 Requisitos dos Ícones

- **Formato:** PNG
- **Tamanhos obrigatórios:**
  - `icon-192.png` → exatamente 192x192 pixels
  - `icon-512.png` → exatamente 512x512 pixels
- **Tipo de conteúdo:** image/png
- **Localização:** Raiz do servidor (mesmo lugar do index.html)

## ✅ Após Criar

1. Faça upload dos ícones para o servidor:
   - `https://mega.mrit.com.br/icon-192.png`
   - `https://mega.mrit.com.br/icon-512.png`

2. Verifique se estão acessíveis:
   - Abra: https://mega.mrit.com.br/icon-192.png
   - Abra: https://mega.mrit.com.br/icon-512.png
   - Devem abrir as imagens, não erro 404

3. Teste novamente no PWA Builder:
   - https://www.pwabuilder.com/
   - Cole: https://mega.mrit.com.br
   - Os erros de ícones devem desaparecer

## 🔧 Se ainda der erro

Verifique:
- ✅ Os arquivos estão na raiz do servidor?
- ✅ Os nomes estão corretos? (`icon-192.png` e `icon-512.png`)
- ✅ Os tamanhos estão corretos? (192x192 e 512x512)
- ✅ O servidor permite acesso aos arquivos PNG?
- ✅ O `manifest.json` aponta para os caminhos corretos?
