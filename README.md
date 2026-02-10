# MRIT Player Vision Android Migrado

Projeto Android nativo do MRIT Player Vision, migrado a partir do PWA.

Player de mídia digital para exibição em displays Android usando PWA (Progressive Web App).

## 🚀 Funcionalidades

- Reprodução de vídeos (MP4, HLS) e imagens
- Cache inteligente para funcionamento offline
- Detecção automática de velocidade de rede
- Gerenciamento de dispositivos físicos
- Controle remoto via banco de dados
- Suporte a orientação automática (portrait/landscape)

## 📱 Instalação no Android

### Opção 1: APK Gerado Automaticamente (Recomendado)

1. Acesse a aba **Actions** no GitHub
2. Selecione o workflow **Build APK**
3. Baixe o APK gerado na seção **Artifacts**
4. Instale no dispositivo Android

### Opção 2: Gerar APK Localmente

```bash
# Instalar dependências
npm install

# Build do projeto
npm run build

# Usar PWA Builder (recomendado)
npx @pwabuilder/cli android --manifest ./manifest.json

# Ou usar Bubblewrap
npm install -g @bubblewrap/cli
bubblewrap init --manifest https://sua-url.com/manifest.json
bubblewrap build
```

## 🛠️ Desenvolvimento

```bash
# Instalar dependências
npm install

# Iniciar servidor de desenvolvimento
npm run dev

# Build para produção
npm run build
```

## 📋 Requisitos

- Node.js 18+
- Dispositivo Android 5.0+ (API 21+)
- Conexão com Supabase configurada

## 🔧 Configuração

1. Configure as credenciais do Supabase em `player.js`
2. Execute os scripts SQL no Supabase:
   - `criar_tabela_dispositivos.sql`
   - `adicionar_campos_dispositivo.sql`
3. Ajuste o `manifest.json` com suas informações
4. Adicione os ícones (icon-192.png e icon-512.png)

## 📦 Estrutura do Projeto

```
├── index.html          # Interface principal
├── player.js           # Lógica do player
├── service-worker.js   # Cache e offline
├── manifest.json       # Configuração PWA
└── .github/
    └── workflows/      # CI/CD para gerar APK
```

## 🔄 CI/CD

O projeto está configurado para gerar APK automaticamente via GitHub Actions quando:
- Push para `main` ou `master`
- Criação de tag de release
- Execução manual (workflow_dispatch)

## 📝 Licença

© 2025 MRIT Software
