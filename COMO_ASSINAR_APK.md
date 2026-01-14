# 🔐 Como Gerar APK Assinado para Produção

## 🎯 Método Recomendado: GitHub Actions Automático

Este é o método **mais prático e seguro** para gerar APKs assinados automaticamente.

## 📋 Pré-requisitos

### 1. Criar Keystore (Uma vez só)

Execute no seu computador (Windows/Linux/Mac):

```bash
keytool -genkey -v -keystore mrit-player.jks -alias mrit-key -keyalg RSA -keysize 2048 -validity 10000
```

**Informações que você precisará fornecer:**
- **Senha do keystore**: (anote bem, você vai precisar!)
- **Senha da chave**: (pode ser a mesma do keystore)
- **Nome e sobrenome**: MRIT Software
- **Unidade organizacional**: (opcional)
- **Organização**: MRIT Software
- **Cidade**: (sua cidade)
- **Estado**: (seu estado)
- **País**: BR

**⚠️ IMPORTANTE:**
- **GUARDE O ARQUIVO `mrit-player.jks` EM LUGAR SEGURO!**
- **ANOTE AS SENHAS!**
- **Se perder o keystore, NÃO poderá atualizar o app na Play Store!**

### 2. Converter Keystore para Base64

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("mrit-player.jks")) | Out-File -Encoding ASCII keystore-base64.txt
```

**Linux/Mac:**
```bash
base64 -i mrit-player.jks -o keystore-base64.txt
```

**Ou online:**
- Abra o arquivo `mrit-player.jks` em um editor de base64 online
- Copie o conteúdo

### 3. Adicionar Secrets no GitHub

1. Vá para: `https://github.com/MRITSoftware/player-vision/settings/secrets/actions`
2. Clique em **"New repository secret"** para cada um:

   **a) KEYSTORE_BASE64**
   - Nome: `KEYSTORE_BASE64`
   - Valor: Cole o conteúdo do `keystore-base64.txt` (todo o conteúdo)

   **b) KEYSTORE_PASSWORD**
   - Nome: `KEYSTORE_PASSWORD`
   - Valor: A senha do keystore que você criou

   **c) KEY_ALIAS**
   - Nome: `KEY_ALIAS`
   - Valor: `mrit-key` (ou o alias que você usou)

   **d) KEY_PASSWORD**
   - Nome: `KEY_PASSWORD`
   - Valor: A senha da chave (pode ser a mesma do keystore)

   **e) PWA_URL** (se ainda não tiver)
   - Nome: `PWA_URL`
   - Valor: `https://meuplayer.com.br` (sua URL)

## 🚀 Gerar APK Assinado

### Opção 1: Manual (Recomendado para primeira vez)

1. Vá para: `https://github.com/MRITSoftware/player-vision/actions`
2. Clique em **"Build Signed APK"**
3. Clique em **"Run workflow"**
4. Preencha:
   - **Version Code**: `1` (incremente a cada build: 2, 3, 4...)
   - **Version Name**: `1.0.0` (ex: 1.0.1, 1.1.0, 2.0.0)
5. Clique em **"Run workflow"**
6. Aguarde o build completar
7. Baixe o APK assinado em **"Artifacts"**

### Opção 2: Automático (Releases)

1. Crie uma **Release** no GitHub:
   - Vá em: `Releases` > `Create a new release`
   - Tag: `v1.0.0` (ou a versão desejada)
   - Título: `MRIT Player v1.0.0`
   - Clique em **"Publish release"**
2. O workflow rodará automaticamente
3. O APK assinado será anexado à release

## 📦 Estrutura dos Secrets

```
KEYSTORE_BASE64     → Conteúdo do keystore em Base64
KEYSTORE_PASSWORD   → Senha do keystore
KEY_ALIAS          → mrit-key (ou seu alias)
KEY_PASSWORD       → Senha da chave
PWA_URL            → https://meuplayer.com.br
```

## 🔄 Atualizar Versão

Sempre que gerar um novo APK:

1. **Incremente o Version Code** (obrigatório):
   - 1, 2, 3, 4... (sempre maior que o anterior)

2. **Atualize o Version Name** (recomendado):
   - `1.0.0`, `1.0.1`, `1.1.0`, `2.0.0`...

## ⚠️ Troubleshooting

### Erro: "Keystore not found"
- Verifique se `KEYSTORE_BASE64` está configurado corretamente
- Certifique-se de que copiou TODO o conteúdo do arquivo base64

### Erro: "Wrong password"
- Verifique se `KEYSTORE_PASSWORD` e `KEY_PASSWORD` estão corretos
- Certifique-se de que não há espaços extras

### Erro: "Key alias not found"
- Verifique se `KEY_ALIAS` está correto (geralmente `mrit-key`)
- Deve ser exatamente o mesmo usado ao criar o keystore

## 🔒 Segurança

- ✅ Keystore nunca é exposto no código
- ✅ Senhas ficam apenas nos GitHub Secrets
- ✅ APK gerado é assinado automaticamente
- ✅ Pronto para distribuição na Play Store

## 📱 Próximos Passos

Após gerar o APK assinado:

1. **Teste localmente** no dispositivo Android
2. **Upload na Google Play Console** (se for publicar)
3. **Distribuição interna** (instalação direta)

## 💡 Dica

**Mantenha o keystore seguro:**
- Faça backup em múltiplos lugares
- Use um gerenciador de senhas
- Documente onde está guardado
- **NUNCA** commite o keystore no Git!
