# Como Executar o Script SQL

## 📋 Passo a Passo

### 1. Acesse o Supabase Dashboard
- Vá para: https://app.supabase.com
- Faça login na sua conta
- Selecione o projeto correto (base.muraltv.com.br)

### 2. Abra o SQL Editor
- No menu lateral esquerdo, clique em **"SQL Editor"** (ícone de banco de dados)
- Ou use o atalho: vá em **"Database"** → **"SQL Editor"**

### 3. Execute o Script
- Clique em **"New query"** ou use a área de texto existente
- Cole todo o conteúdo do arquivo `adicionar_campos_dispositivo.sql`
- Clique no botão **"Run"** (ou pressione `Ctrl+Enter` / `Cmd+Enter`)

### 4. Verifique o Resultado
- Você verá uma mensagem de sucesso
- A query de verificação no final mostrará os campos criados:
  - `device_id` (TEXT, nullable)
  - `device_last_seen` (TIMESTAMPTZ, nullable)

## ✅ Resultado Esperado

Você deve ver algo como:

```
Success. No rows returned

Query 2 returned 2 rows:
column_name        | data_type   | is_nullable
-------------------|-------------|-------------
device_id          | text        | YES
device_last_seen   | timestamp   | YES
```

## 🔍 Verificar Manualmente (Opcional)

Se quiser verificar depois, execute esta query:

```sql
SELECT 
  column_name, 
  data_type, 
  is_nullable
FROM information_schema.columns 
WHERE table_name = 'displays' 
  AND column_name IN ('device_id', 'device_last_seen');
```

## ⚠️ Importante

- ✅ **Seguro para produção** - não quebra nada
- ✅ **Pode executar múltiplas vezes** - usa `IF NOT EXISTS`
- ✅ **Não altera dados existentes**
- ✅ **Campos são opcionais** (NULL permitido)

## 🚨 Se Der Erro

Se aparecer algum erro, provavelmente é de permissão. Nesse caso:
1. Verifique se você tem permissão de administrador
2. Ou execute apenas os comandos `ALTER TABLE` (linhas 12-13 e 17-18)
3. Ignore os comandos de índice e comentários se der erro
