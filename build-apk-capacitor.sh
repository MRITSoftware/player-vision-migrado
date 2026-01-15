#!/bin/bash
# Script para gerar APK usando Capacitor (Linux/Mac)
# Este script cria um APK nativo com suporte a fullscreen 24h

echo "🚀 Iniciando build do APK com Capacitor..."
echo ""

# Verificar se Node.js está instalado
if ! command -v node &> /dev/null; then
    echo "❌ Node.js não encontrado. Instale Node.js primeiro."
    echo "📥 Baixe em: https://nodejs.org/"
    exit 1
fi

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "⚠️  Java não encontrado. Você precisará do Android Studio para compilar."
    echo "📥 Baixe o Android Studio em: https://developer.android.com/studio"
fi

echo "📦 Instalando dependências..."
npm install

if [ $? -ne 0 ]; then
    echo "❌ Erro ao instalar dependências"
    exit 1
fi

echo "🔨 Fazendo build do projeto..."
npm run build

if [ $? -ne 0 ]; then
    echo "❌ Erro ao fazer build"
    exit 1
fi

echo "📱 Sincronizando com Capacitor..."
npx cap sync android

if [ $? -ne 0 ]; then
    echo "❌ Erro ao sincronizar Capacitor"
    echo "💡 Execute: npm install -g @capacitor/cli"
    exit 1
fi

echo ""
echo "✅ Build concluído com sucesso!"
echo ""
echo "📋 Próximos passos:"
echo ""
echo "1. Abra o Android Studio:"
echo "   npx cap open android"
echo ""
echo "2. No Android Studio:"
echo "   - Vá em: Build > Build Bundle(s) / APK(s) > Build APK(s)"
echo "   - OU: Build > Generate Signed Bundle / APK"
echo "   - O APK estará em: android/app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "3. Para modo kiosk completo (opcional):"
echo "   - Configure o dispositivo como \"Device Owner\" ou \"Kiosk Mode\""
echo "   - Use apps como \"Kiosk Browser\" ou configure via ADB"
echo ""
echo "💡 Dica: Para instalar diretamente no dispositivo conectado:"
echo "   npx cap run android"
echo ""
