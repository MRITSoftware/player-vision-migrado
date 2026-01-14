-- Script para criar tabela de dispositivos físicos
-- Execute este script no Supabase SQL Editor

-- Criar tabela de dispositivos físicos
-- SEM foreign keys para não afetar tabelas em produção
CREATE TABLE IF NOT EXISTS dispositivos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  device_id TEXT UNIQUE NOT NULL,  -- ID único do dispositivo (gerado pelo navegador)
  codigo_display TEXT NOT NULL,    -- Código do display que está usando (sem FK)
  local_nome TEXT,                 -- Local/nome da tela física (ex: "Sala 1", "Recepção")
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  last_seen TIMESTAMPTZ DEFAULT NOW(),
  is_ativo BOOLEAN DEFAULT true
);

-- Criar índices para performance
CREATE INDEX IF NOT EXISTS idx_dispositivos_device_id ON dispositivos(device_id);
CREATE INDEX IF NOT EXISTS idx_dispositivos_codigo_display ON dispositivos(codigo_display);
CREATE INDEX IF NOT EXISTS idx_dispositivos_is_ativo ON dispositivos(is_ativo);

-- Constraint única: Um código só pode estar ativo em um dispositivo por vez
-- Isso garante que não haverá dois dispositivos ativos com o mesmo código
CREATE UNIQUE INDEX IF NOT EXISTS idx_dispositivos_codigo_ativo 
ON dispositivos(codigo_display) 
WHERE is_ativo = true;

-- Função para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_dispositivos_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para atualizar updated_at
DROP TRIGGER IF EXISTS trigger_update_dispositivos_updated_at ON dispositivos;
CREATE TRIGGER trigger_update_dispositivos_updated_at
  BEFORE UPDATE ON dispositivos
  FOR EACH ROW
  EXECUTE FUNCTION update_dispositivos_updated_at();

-- Comentários explicativos
COMMENT ON TABLE dispositivos IS 'Tabela de dispositivos físicos (telas) com seus códigos de display e localização';
COMMENT ON COLUMN dispositivos.device_id IS 'ID único gerado pelo navegador/dispositivo';
COMMENT ON COLUMN dispositivos.codigo_display IS 'Código do display que este dispositivo está usando';
COMMENT ON COLUMN dispositivos.local_nome IS 'Local/nome da tela física (ex: Sala 1, Recepção, Loja A)';
COMMENT ON COLUMN dispositivos.is_ativo IS 'Se o dispositivo está ativo (false = desativado/removido)';

-- Verificar se tabela foi criada
SELECT 
  table_name,
  column_name,
  data_type,
  is_nullable
FROM information_schema.columns
WHERE table_name = 'dispositivos'
ORDER BY ordinal_position;
