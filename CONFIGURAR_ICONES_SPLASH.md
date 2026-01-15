# 🎨 Configurar Ícones e Splash Screen com vision_logo.png

## 📋 O que será configurado

1. **Ícones do App (PWA)**: `icon-192.png` e `icon-512.png`
2. **Ícones do Android**: Diferentes tamanhos para diferentes densidades de tela
3. **Splash Screen**: Tela de carregamento com o logo Vision

## 🚀 Como usar

### Passo 1: Instalar dependências

```bash
npm install
```

Isso instalará a biblioteca `sharp` necessária para processar as imagens.

### Passo 2: Gerar ícones e configurar splash screen

```bash
npm run setup:icons
```

Este comando irá:
- ✅ Gerar `icon-192.png` e `icon-512.png` a partir de `vision_logo.png`
- ✅ Gerar ícones do Android em diferentes tamanhos
- ✅ Gerar splash screen para Android

### Passo 3: Sincronizar com Capacitor

```bash
npm run capacitor:sync
```

Isso copiará os recursos gerados para o projeto Android.

### Passo 4: Fazer upload dos ícones PWA

Após gerar os ícones, faça upload para o servidor:
- `icon-192.png` → `https://mega.mrit.com.br/icon-192.png`
- `icon-512.png` → `https://mega.mrit.com.br/icon-512.png`

## 📱 O que foi configurado

### Manifest.json
- ✅ Já configurado para usar `icon-192.png` e `icon-512.png`

### Capacitor Config
- ✅ Splash screen configurado para mostrar por 2 segundos
- ✅ Usa `vision_logo.png` como imagem do splash
- ✅ Fundo preto (#000000)
- ✅ Centralizado (CENTER)

### Android Resources
- ✅ Ícones gerados para todas as densidades (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- ✅ Splash screen gerado (1920x1080px)

## 🔍 Verificar se funcionou

1. **Ícones PWA:**
   - Verifique se `icon-192.png` e `icon-512.png` foram criados na raiz
   - Faça upload para o servidor
   - Teste em: https://www.pwabuilder.com/

2. **Splash Screen:**
   - Execute `npm run capacitor:sync`
   - Compile o APK
   - Ao abrir o app, deve mostrar o logo Vision por 2 segundos

3. **Ícone do App:**
   - Após instalar o APK, o ícone do app deve ser o logo Vision

## ⚠️ Notas Importantes

- O script precisa que a pasta `android/` exista (criada pelo `npx cap sync`)
- Se a pasta Android não existir, o script ainda gerará os ícones PWA
- O splash screen será configurado automaticamente quando você executar `npx cap sync`

## 🛠️ Alternativa Manual

Se preferir fazer manualmente:

1. **Gerar ícones PWA:**
   - Abra `gerar_icones_automatico.html` no navegador
   - Selecione `vision_logo.png`
   - Baixe os ícones gerados

2. **Configurar Android:**
   - Copie `vision_logo.png` para `android/app/src/main/res/drawable/splash.png`
   - Redimensione para diferentes tamanhos de ícone manualmente

## ✅ Pronto!

Após seguir estes passos, seu app terá:
- ✅ Ícone personalizado (logo Vision)
- ✅ Splash screen personalizado (logo Vision)
- ✅ Tudo configurado automaticamente!
