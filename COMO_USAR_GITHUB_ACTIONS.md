# 🚀 Como Gerar APK no GitHub (Sem Android Studio)

## ✅ Perfeito! Você não precisa do Android Studio!

O workflow do GitHub Actions já está configurado e vai gerar o APK automaticamente.

## 📋 Passo a Passo Rápido:

### 1. Subir código para o GitHub

```bash
# Se ainda não inicializou o Git
git init

# Adicionar todos os arquivos
git add .

# Fazer commit
git commit -m "Configuração inicial - Player com Capacitor para APK 24h"

# Criar repositório no GitHub primeiro, depois:
git remote add origin https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
git branch -M main
git push -u origin main
```

### 2. O build roda automaticamente! 🎉

Quando você fizer push, o GitHub Actions vai:
- ✅ Instalar Node.js e dependências
- ✅ Fazer build do projeto
- ✅ Configurar Android SDK
- ✅ Compilar o APK
- ✅ Disponibilizar para download

### 3. Baixar o APK

1. Vá para: `https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions`
2. Clique no workflow mais recente (ícone verde ✅)
3. Role até **"Artifacts"**
4. Clique em **"mrit-player-apk"** para baixar
5. Extraia o ZIP
6. O APK estará dentro: `app-debug.apk`

## ⏱️ Tempo de Build:

- **Primeira vez:** ~10-15 minutos (baixa Android SDK)
- **Próximas vezes:** ~5-8 minutos (usa cache)

## 🎯 Executar Manualmente:

Se quiser executar sem fazer push:

1. Vá para: `https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions`
2. Clique em **"Build APK - MRIT Player"**
3. Clique em **"Run workflow"** (botão no canto superior direito)
4. Selecione a branch e clique em **"Run workflow"**

## 📱 Instalar o APK:

1. Baixe o APK dos artefatos do GitHub
2. Transfira para o dispositivo Android (USB, email, etc)
3. Ative **"Fontes desconhecidas"** nas configurações
4. Toque no arquivo APK para instalar

## 🔧 Se algo der errado:

### Build falha?
- Verifique os logs na aba **Actions**
- Clique no workflow que falhou
- Veja os logs de cada step

### APK não aparece?
- Verifique se o build completou (ícone verde)
- Verifique se não há erros nos logs

### Quer testar localmente primeiro?
```bash
npm install
npm run build
npx cap sync android
```

## 💡 Dicas:

- ✅ **APKs ficam disponíveis por 30 dias** nos artefatos
- ✅ **Use Releases** para manter APKs permanentemente
- ✅ **Crie tags** para versões: `git tag v1.0.0 && git push origin v1.0.0`
- ✅ **Monitore os logs** se algo der errado

## 🎉 Pronto!

Agora é só fazer push e aguardar o APK ser gerado automaticamente!

---

**Documentação completa:** Veja `COMO_GERAR_APK_GITHUB.md` para mais detalhes.
