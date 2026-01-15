# 🚀 Como Gerar APK no GitHub (Sem Android Studio)

Este guia explica como gerar o APK automaticamente no GitHub Actions, **sem precisar do Android Studio instalado localmente**.

## ✅ Vantagens:

- ✅ **Não precisa do Android Studio** instalado
- ✅ **Build automático** a cada push
- ✅ **APK disponível para download** como artefato
- ✅ **Gratuito** (GitHub Actions tem 2000 minutos/mês grátis)
- ✅ **Funciona em qualquer sistema** (Windows, Mac, Linux)

## 🚀 Passo a Passo:

### 1. Subir código para o GitHub

Se ainda não tem um repositório:

```bash
# Inicializar Git (se ainda não tiver)
git init

# Adicionar arquivos
git add .

# Fazer commit
git commit -m "Configuração inicial - Player com Capacitor"

# Criar repositório no GitHub e adicionar remote
git remote add origin https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git

# Fazer push
git branch -M main
git push -u origin main
```

### 2. O workflow já está configurado!

O arquivo `.github/workflows/build-apk.yml` já está criado e configurado. Ele vai:

- ✅ Instalar Node.js
- ✅ Instalar dependências
- ✅ Fazer build do projeto
- ✅ Configurar Android SDK
- ✅ Compilar o APK
- ✅ Disponibilizar para download

### 3. Executar o build

#### Opção A: Push automático
O build roda automaticamente quando você faz push para `main` ou `master`:

```bash
git add .
git commit -m "Atualização do player"
git push
```

#### Opção B: Executar manualmente
1. Vá para: `https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions`
2. Clique em **"Build APK - MRIT Player"**
3. Clique em **"Run workflow"**
4. Selecione a branch e clique em **"Run workflow"**

### 4. Baixar o APK

Após o build completar (leva ~5-10 minutos):

1. Vá para a aba **Actions** no GitHub
2. Clique no workflow mais recente (que deve estar verde ✅)
3. Role até a seção **Artifacts**
4. Clique em **"mrit-player-apk"** para baixar
5. Extraia o arquivo ZIP
6. O APK estará dentro: `app-debug.apk`

## 📋 Estrutura do Workflow:

O workflow faz o seguinte:

```yaml
1. Checkout do código
2. Setup Node.js 18
3. npm install
4. npm run build
5. Setup Java 17
6. Setup Android SDK
7. npx cap sync android
8. ./gradlew assembleDebug
9. Upload do APK como artefato
```

## 🔧 Configurações Avançadas:

### Criar Release com APK

Para criar uma release automaticamente quando criar uma tag:

```bash
# Criar tag
git tag -a v1.0.0 -m "Versão 1.0.0"
git push origin v1.0.0
```

O workflow vai criar uma release automaticamente com o APK anexado!

### Modificar configurações do APK

Edite o arquivo `capacitor.config.js` para mudar:
- Package ID
- Nome do app
- Configurações de plugins

Depois faça commit e push:

```bash
git add capacitor.config.js
git commit -m "Atualizar configurações do APK"
git push
```

## ⚠️ Importante:

### Arquivos que NÃO devem ser commitados:

Certifique-se de que o `.gitignore` inclui:

```
node_modules/
dist/
android/
ios/
.DS_Store
*.log
```

O workflow vai gerar a pasta `android/` automaticamente, então não precisa commitá-la.

### Se o build falhar:

1. **Verifique os logs** na aba Actions
2. **Erros comuns:**
   - Dependências faltando → Verifique `package.json`
   - Erro no build → Verifique se `npm run build` funciona localmente
   - Erro no Gradle → O workflow configura automaticamente

## 📱 Instalar o APK no dispositivo:

1. **Baixe o APK** do GitHub Actions
2. **Transfira para o dispositivo Android** (via USB, email, etc)
3. **Ative "Fontes desconhecidas"** nas configurações do Android
4. **Instale o APK** tocando no arquivo

## 🎯 Próximos Passos:

1. ✅ Fazer push do código
2. ✅ Aguardar build completar (~5-10 min)
3. ✅ Baixar APK dos artefatos
4. ✅ Testar no dispositivo
5. ✅ Criar tag para release (opcional)

## 💡 Dicas:

- **Builds levam ~5-10 minutos** - seja paciente!
- **APKs ficam disponíveis por 30 dias** nos artefatos
- **Use releases** para manter APKs permanentemente
- **Monitore os logs** se algo der errado

## 🔍 Troubleshooting:

### Build falha com "Gradle não encontrado"
- O workflow configura automaticamente, mas pode levar alguns minutos na primeira vez

### Build falha com "Capacitor não encontrado"
- Verifique se `package.json` tem as dependências do Capacitor
- Execute `npm install` localmente para testar

### APK não aparece nos artefatos
- Verifique se o build completou com sucesso (ícone verde)
- Verifique os logs para erros

### Quer build mais rápido?
- Use cache do npm (já configurado)
- Considere usar `assembleRelease` para APK otimizado (requer keystore)

---

**Pronto!** Agora você pode gerar APKs sem precisar do Android Studio! 🎉
