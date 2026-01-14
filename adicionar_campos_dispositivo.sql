-- Script para adicionar campos de dispositivo na tabela displays
-- Execute este script no Supabase SQL Editor
-- 
-- ⚠️ IMPORTANTE: Este script é SEGURO para produção
-- - Os campos são OPCIONAIS (NULL permitido)
-- - Não altera dados existentes
-- - Não quebra funcionalidades atuais
-- - Pode ser executado em produção sem impacto

-- Adicionar campo device_id para identificar o dispositivo físico
-- Campo é OPCIONAL (NULL permitido) - não quebra nada se não for usado
ALTER TABLE displays 
ADD COLUMN IF NOT EXISTS device_id TEXT;

-- Adicionar campo device_last_seen para rastrear última vez que o dispositivo foi visto
-- Campo é OPCIONAL (NULL permitido) - não quebra nada se não for usado
ALTER TABLE displays 
ADD COLUMN IF NOT EXISTS device_last_seen TIMESTAMPTZ;

-- Criar índice para facilitar buscas por device_id (apenas se campo existir)
CREATE INDEX IF NOT EXISTS idx_displays_device_id ON displays(device_id);

-- Comentários explicativos
COMMENT ON COLUMN displays.device_id IS 'ID único do dispositivo físico que está usando este display (opcional)';
COMMENT ON COLUMN displays.device_last_seen IS 'Última vez que este dispositivo foi visto online (opcional)';

-- Verificar se campos foram criados corretamente
SELECT 
  column_name, 
  data_type, 
  is_nullable
FROM information_schema.columns 
WHERE table_name = 'displays' 
  AND column_name IN ('device_id', 'device_last_seen');
