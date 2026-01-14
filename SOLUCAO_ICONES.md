# 🎨 Solução: Criar Ícones para o PWA

## ❌ Problema
Os ícones `icon-192.png` e `icon-512.png` não estão acessíveis no servidor, causando erro 404.

## ✅ Solução Rápida

### Opção 1: Usar vision_logo.png como base (Recomendado)

1. **Redimensionar o logo:**
   - Use um editor online: https://www.iloveimg.com/resize-image
   - Ou use Photoshop/GIMP
   - Redimensione `vision_logo.png` para:
     - **192x192 pixels** → salve como `icon-192.png`
     - **512x512 pixels** → salve como `icon-512.png`

2. **Fazer upload para o servidor:**
   - Envie `icon-192.png` para: `https://mega.mrit.com.br/icon-192.png`
   - Envie `icon-512.png` para: `https://mega.mrit.com.br/icon-512.png`

3. **Verificar:**
   - Abra: https://mega.mrit.com.br/icon-192.png (deve mostrar a imagem)
   - Abra: https://mega.mrit.com.br/icon-512.png (deve mostrar a imagem)

### Opção 2: Gerador Online

1. Acesse: https://www.favicon-generator.org/
2. Faça upload do `vision_logo.png`
3. Gere os tamanhos:
   - 192x192
   - 512x512
4. Baixe e renomeie:
   - `android-icon-192x192.png` → `icon-192.png`
   - `android-icon-512x512.png` → `icon-512.png`
5. Faça upload para o servidor

### Opção 3: Criar ícone simples temporário

Se não tiver logo, crie um ícone simples:

1. Use: https://realfavicongenerator.net/
2. Escolha uma cor e texto
3. Gere os ícones
4. Baixe e faça upload

## 📋 Requisitos

- ✅ Formato: PNG
- ✅ Tamanho exato: 192x192 e 512x512 pixels
- ✅ Localização: Raiz do servidor (mesmo lugar do index.html)
- ✅ Nomes: exatamente `icon-192.png` e `icon-512.png`

## 🔍 Verificar se funcionou

Após fazer upload, teste:

```bash
# No navegador, abra:
https://mega.mrit.com.br/icon-192.png
https://mega.mrit.com.br/icon-512.png
```

Se abrir as imagens (não erro 404), está funcionando!

## 🚀 Depois de corrigir

1. Teste novamente no PWA Builder: https://www.pwabuilder.com/
2. Os erros de ícones devem desaparecer
3. Você poderá gerar o APK normalmente
