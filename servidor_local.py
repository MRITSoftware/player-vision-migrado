#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Servidor local simples para testar o Player MRIT com Service Worker
Execute: python servidor_local.py
Acesse: http://localhost:8000
"""

import http.server
import socketserver
import os
import sys
from urllib.parse import urlparse, parse_qs
import json

class MRITHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=os.getcwd(), **kwargs)
    
    def end_headers(self):
        # Adicionar headers CORS para permitir requisições
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type, Range')
        self.send_header('Access-Control-Expose-Headers', 'Content-Range, Accept-Ranges, Content-Length')
        self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
        super().end_headers()
    
    def do_OPTIONS(self):
        # Responder a requisições OPTIONS (CORS preflight)
        self.send_response(200)
        self.end_headers()
    
    def do_GET(self):
        # Log da requisição
        print(f"📥 GET {self.path}")
        
        # Verificar se é uma requisição de Range (para vídeos)
        range_header = self.headers.get('Range')
        if range_header:
            print(f"🎬 Range request: {range_header}")
        
        # Chamar o método pai para servir o arquivo
        super().do_GET()
    
    def log_message(self, format, *args):
        # Log personalizado
        print(f"🌐 {format % args}")

def main():
    PORT = 8000
    
    # Verificar se a porta está disponível
    try:
        with socketserver.TCPServer(("", PORT), MRITHandler) as httpd:
            print("🚀 Servidor MRIT iniciado!")
            print(f"📍 URL: http://localhost:{PORT}")
            print(f"📁 Diretório: {os.getcwd()}")
            print("🛑 Para parar: Ctrl+C")
            print("-" * 50)
            
            try:
                httpd.serve_forever()
            except KeyboardInterrupt:
                print("\n🛑 Servidor parado pelo usuário")
                
    except OSError as e:
        if e.errno == 48:  # Address already in use
            print(f"❌ Erro: Porta {PORT} já está em uso")
            print("💡 Tente fechar outros servidores ou usar uma porta diferente")
            print("💡 Para usar porta diferente: python servidor_local.py 8080")
        else:
            print(f"❌ Erro ao iniciar servidor: {e}")
        sys.exit(1)

if __name__ == "__main__":
    # Permitir porta customizada via argumento
    if len(sys.argv) > 1:
        try:
            PORT = int(sys.argv[1])
            print(f"🔧 Usando porta customizada: {PORT}")
        except ValueError:
            print("❌ Porta inválida. Use: python servidor_local.py [porta]")
            sys.exit(1)
    else:
        PORT = 8000
    
    main()