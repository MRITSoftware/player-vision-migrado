import sharp from 'sharp';
import { existsSync } from 'fs';

const inputFile = 'vision_logo.png';
const sizes = [192, 512];

async function generateIcons() {
  if (!existsSync(inputFile)) {
    console.error(`❌ Arquivo ${inputFile} não encontrado!`);
    process.exit(1);
  }

  console.log(`🎨 Gerando ícones a partir de ${inputFile}...`);

  for (const size of sizes) {
    try {
      const outputFile = `icon-${size}.png`;
      
      await sharp(inputFile)
        .resize(size, size, {
          fit: 'contain',
          background: { r: 255, g: 255, b: 255, alpha: 0 } // Fundo transparente
        })
        .toFile(outputFile);
      
      console.log(`✅ ${outputFile} gerado (${size}x${size}px)`);
    } catch (error) {
      console.error(`❌ Erro ao gerar icon-${size}.png:`, error.message);
    }
  }

  console.log('\n✅ Ícones gerados com sucesso!');
  console.log('📤 Agora você pode:');
  console.log('   1. Fazer upload dos ícones para o servidor');
  console.log('   2. Executar: npm run capacitor:sync');
}

generateIcons().catch(console.error);
