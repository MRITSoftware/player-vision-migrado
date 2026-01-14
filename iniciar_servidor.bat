@echo off
echo 🚀 Iniciando Servidor MRIT...
echo.

REM Verificar se Python está instalado
python --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Python não encontrado!
    echo 💡 Instale Python 3.6+ em: https://python.org
    echo 💡 Ou use: python3 servidor_local.py
    pause
    exit /b 1
)

REM Iniciar servidor
echo ✅ Python encontrado
echo 🌐 Iniciando servidor na porta 8000...
echo 📍 Acesse: http://localhost:8000
echo 🛑 Para parar: Ctrl+C
echo.

python servidor_local.py

pause
